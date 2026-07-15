package com.xyp.gtnc.ae2thing.client.gui.container.BaseMonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;

import com.xyp.gtnc.Common.compat.FluidDropCompat;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.network.SPacketMEFluidInvUpdate;

import appeng.api.AEApi;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.Platform;

public class FluidMonitor implements IMEMonitorHandlerReceiver<IAEFluidStack>, IProcessItemList {

    private IMEMonitor<IAEFluidStack> fluidMonitor;
    private IMEMonitor<IAEItemStack> itemMonitor;
    private final IItemList<IAEFluidStack> fluids = AEApi.instance()
        .storage()
        .createFluidList();
    private final Set<IAEItemStack> craftingFluids = new HashSet<>();
    private final List<ICrafting> crafters;
    private final List<IAEFluidStack> toSend = new ArrayList<>();
    // Snapshot of the fluid storage list for external-change (removed craftable) detection — mirrors
    // RefreshingItemMonitor on the item side. AE2's crafting cache only posts the CURRENT craftable set, never a
    // "no-longer-craftable" event, so a vanished craftable fluid must be detected by diffing.
    private IItemList<IAEFluidStack> lastSnapshot;
    private int refreshCounter = 0;

    public FluidMonitor(IStorageGrid storageGrid, List<ICrafting> crafters) {
        this.fluidMonitor = storageGrid.getFluidInventory();
        this.itemMonitor = storageGrid.getItemInventory();
        this.crafters = crafters;
    }

    public FluidMonitor(List<ICrafting> crafters) {
        this.crafters = crafters;
    }

    @Override
    public boolean isValid(Object verificationToken) {
        return this.fluidMonitor != null;
    }

    public void addItemCraftingFluid(IAEItemStack is) {
        craftingFluids.add(is);
    }

    @Override
    public void postChange(IBaseMonitor<IAEFluidStack> monitor, Iterable<IAEFluidStack> change,
        BaseActionSource actionSource) {
        for (final IAEFluidStack is : change) {
            this.fluids.add(is);
        }
    }

    @Override
    public void onListUpdate() {
        for (final Object c : this.crafters) {
            if (c instanceof final ICrafting cr) {
                this.queueInventory(cr);
            }
        }
    }

    @Override
    public void addListener() {
        this.fluidMonitor.addListener(this, null);
    }

    @Override
    public void removeListener() {
        if (this.fluidMonitor != null) this.fluidMonitor.removeListener(this);
    }

    @Override
    public IMEMonitor<IAEFluidStack> getMonitor() {
        return this.fluidMonitor;
    }

    /**
     * Detect craftable fluids that vanished from the network (e.g. a pattern was removed). AE2's crafting cache only
     * posts the current craftable set, never a removal event, so we snapshot-diff the fluid storage list every few
     * ticks. A vanished entry is queued into {@code this.fluids} with a non-zero sentinel size so it survives the
     * MeaningfulFluidIterator in the loop below; that loop's else-branch then zeroes it before sending, and the client
     * prunes the size-0 / non-craftable record. Mirrors {@code RefreshingItemMonitor} on the item side.
     */
    private void detectExternalChanges() {
        if (this.fluidMonitor == null) {
            return;
        }
        // Throttle to every ~5 ticks (processItemList runs each tick via detectAndSendChanges).
        if (this.lastSnapshot != null && (this.refreshCounter++ % 5) != 0) {
            return;
        }
        final IItemList<IAEFluidStack> current = this.fluidMonitor.getStorageList();
        // Build a clean deep copy of the current list. getStorageList() returns AE2's shared cachedList, in which a
        // removed craftable leaves a ZOMBIE record: resetStatus() zeroed it (size 0 / craftable false) but never
        // physically removed the key from the backing set. findPrecise() reads that set directly, so it would still
        // hit the zombie and we'd never detect the removal. Iterating `current` here drives the MeaningfulFluidIterator
        // (which skips + physically removes zombies), so only meaningful entries land in `copy`; using `copy` for the
        // findPrecise check below makes vanished craftables actually detectable. (This is what the diagnostic dump was
        // accidentally doing — the detection was never really working without that iteration.)
        final IItemList<IAEFluidStack> copy = AEApi.instance()
            .storage()
            .createFluidList();
        // Iterate the WHOLE current list (drives the MeaningfulFluidIterator that physically evicts zombies — see
        // above), but only retain the craftable subset in the snapshot: the diff below solely detects vanished
        // craftables, so non-craftable fluids are dead weight. On large networks this cuts the per-refresh allocation
        // from O(all fluids) down to O(craftable fluids). Mirrors RefreshingItemMonitor.copyCraftables on the item
        // side.
        for (final IAEFluidStack f : current) {
            if (f.isCraftable()) {
                copy.addStorage(f.copy());
            }
        }
        if (this.lastSnapshot != null) {
            for (final IAEFluidStack prev : this.lastSnapshot) {
                if (copy.findPrecise(prev) == null) {
                    final IAEFluidStack gone = prev.copy();
                    gone.reset();
                    gone.setStackSize(-1);
                    this.fluids.add(gone);
                }
            }
        }
        this.lastSnapshot = copy;
    }

    @Override
    public void processItemList() {
        this.detectExternalChanges();
        // Allocate the update packet lazily: most ticks have no fluid changes, so building the packet up front (like
        // the
        // old code) burned an object per tick per open terminal. ItemMonitor.processItemList already only allocates
        // when
        // there's something to send — this brings the fluid side in line.
        SPacketMEFluidInvUpdate piu = null;
        if (!this.fluids.isEmpty()) {
            final IItemList<IAEFluidStack> monitorCache = this.fluidMonitor.getStorageList();
            final IItemList<IAEItemStack> itemMonitorCache = this.itemMonitor.getStorageList();
            for (final IAEFluidStack is : this.fluids) {
                IAEFluidStack send = monitorCache.findPrecise(is);
                if (send != null) {
                    // [液滴分类] 可迁原生：用液滴键在物品缓存里查同物以拷贝可合成态到流体显示,纯显示同步不参与合成
                    IAEItemStack item = itemMonitorCache.findPrecise(FluidDropCompat.newAeStack(send));
                    if (item != null) {
                        send = send.copy();
                        send.setCraftable(item.isCraftable());
                    }
                    toSend.add(send);
                } else {
                    is.setStackSize(0);
                    toSend.add(is);
                }
            }
            piu = new SPacketMEFluidInvUpdate();
            piu.addAll(toSend);
            this.fluids.resetStatus();
        }
        if (!this.craftingFluids.isEmpty()) {
            final IItemList<IAEFluidStack> monitorCache = this.fluidMonitor.getStorageList();
            for (IAEItemStack is : this.craftingFluids) {
                is.setStackSize(1);
                // [液滴分类] 可迁原生：把可合成液滴项还原为流体栈推给流体面板显示,纯显示不参与合成计算
                IAEFluidStack fs = FluidDropCompat.getAeFluidStack(is);
                if (fs == null) continue;
                if (monitorCache.findPrecise(fs) == null) {
                    fs.setStackSize(0);
                    fs.setCraftable(is.isCraftable());
                    toSend.add(fs);
                }
            }
            if (piu == null) piu = new SPacketMEFluidInvUpdate();
            piu.addAll(toSend);
            this.craftingFluids.clear();
        }
        if (piu != null && !piu.isEmpty()) {
            for (final Object c : this.crafters) {
                if (c instanceof EntityPlayer) {
                    AE2Thing.proxy.netHandler.sendTo(piu, (EntityPlayerMP) c);
                }
            }
        }
        toSend.clear();
    }

    @Override
    public void queueInventory(ICrafting c) {
        if (Platform.isServer() && c instanceof EntityPlayer && this.fluidMonitor != null && this.itemMonitor != null) {
            final IItemList<IAEFluidStack> monitorCache = this.fluidMonitor.getStorageList();
            final IItemList<IAEItemStack> itemMonitorCache = this.itemMonitor.getStorageList();
            List<IAEFluidStack> toSend = new ArrayList<>();
            for (final IAEFluidStack is : monitorCache) {
                final IAEFluidStack send = is.copy();
                // [液滴分类] 可迁原生：用液滴键在物品缓存里查同物以拷贝可合成态到流体条目,初始化列表显示不参与合成
                IAEItemStack fluidDrop = itemMonitorCache.findPrecise(FluidDropCompat.newAeStack(is));
                if (fluidDrop != null) {
                    send.setCraftable(fluidDrop.isCraftable());
                }
                toSend.add(send);
            }
            SPacketMEFluidInvUpdate piu = new SPacketMEFluidInvUpdate();
            piu.addAll(toSend);
            AE2Thing.proxy.netHandler.sendTo(piu, (EntityPlayerMP) c);
        }
    }

    @Override
    public void removeCraftingFromCrafters(ICrafting c) {
        if (this.crafters.isEmpty() && this.fluidMonitor != null) {
            this.fluidMonitor.removeListener(this);
        }
    }

    public void setMonitor(IMEMonitor<IAEFluidStack> fluidMonitor, IMEMonitor<IAEItemStack> itemMonitor) {
        this.fluidMonitor = fluidMonitor;
        this.itemMonitor = itemMonitor;
    }
}
