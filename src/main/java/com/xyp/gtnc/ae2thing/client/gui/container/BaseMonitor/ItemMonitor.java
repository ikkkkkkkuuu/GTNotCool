package com.xyp.gtnc.ae2thing.client.gui.container.BaseMonitor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;

import com.xyp.gtnc.Common.compat.FluidDropCompat;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.common.storage.RefreshableStorageMonitor;
import com.xyp.gtnc.ae2thing.network.SPacketMEItemInvUpdate;

import appeng.api.AEApi;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.Platform;

public class ItemMonitor implements IMEMonitorHandlerReceiver<IAEItemStack>, IProcessItemList {

    private IMEMonitor<IAEItemStack> itemMonitor;
    private final IItemList<IAEItemStack> items = AEApi.instance()
        .storage()
        .createItemList();
    private final List<ICrafting> crafters;
    private FluidMonitor fluidMonitorObject = null;

    public ItemMonitor(List<ICrafting> crafters) {
        this.crafters = crafters;
    }

    public void setMonitor(IMEMonitor<IAEItemStack> itemMonitor) {
        // 深度防御：若已持有旧 delegate 且被换成新的，先把自己从旧 delegate 退订，否则旧 delegate（可能是个已挂到 grid 的
        // RefreshingItemMonitor）会永久残留、无法被摘除，形成「每次替换泄漏一个僵尸监视器」。正常路径下 setMonitor 每次
        // 打开只应调用一次（见 ContainerWirelessDualInterfaceTerminal 的说明），这里只是兜底。
        if (this.itemMonitor != null && this.itemMonitor != itemMonitor) {
            this.itemMonitor.removeListener(this);
        }
        this.itemMonitor = itemMonitor;
    }

    public void setFluidMonitorObject(FluidMonitor objectMonitor) {
        this.fluidMonitorObject = objectMonitor;
    }

    @Override
    public void addListener() {
        this.itemMonitor.addListener(this, null);
    }

    @Override
    public boolean isValid(Object verificationToken) {
        return this.itemMonitor != null;
    }

    @Override
    public void postChange(IBaseMonitor<IAEItemStack> monitor, Iterable<IAEItemStack> change,
        BaseActionSource actionSource) {
        for (final IAEItemStack is : change) {
            this.items.add(is);
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

    private void fluidHandler(IAEItemStack send) {
        // [液滴分类] 可迁原生：识别可合成液滴条目转交流体监视器标记可合成态,属显示分流不参与合成计算
        if (this.fluidMonitorObject != null && send.getStackSize() == 0
            && FluidDropCompat.isFluidDrop(send.getItem())) {
            this.fluidMonitorObject.addItemCraftingFluid(send);
        }
    }

    @Override
    public void processItemList() {
        IItemList<IAEItemStack> monitorCache = null;
        if (this.itemMonitor instanceof RefreshableStorageMonitor refreshable) {
            monitorCache = refreshable.refreshExternalChanges(null, false);
        }
        if (!this.items.isEmpty()) {
            if (monitorCache == null) {
                monitorCache = this.itemMonitor.getStorageList();
            }
            List<IAEItemStack> toSend = new ArrayList<>();
            for (final IAEItemStack is : this.items) {
                IAEItemStack send = monitorCache.findPrecise(is);
                if (send != null) {
                    fluidHandler(send.copy());
                    toSend.add(send);
                } else {
                    is.setStackSize(0);
                    toSend.add(is);
                }
            }
            SPacketMEItemInvUpdate piu = new SPacketMEItemInvUpdate();
            piu.addAll(toSend);
            for (final Object c : this.crafters) {
                if (c instanceof EntityPlayer) {
                    AE2Thing.proxy.netHandler.sendTo(piu, (EntityPlayerMP) c);
                }
            }
            this.items.resetStatus();
        }
    }

    @Override
    public void queueInventory(ICrafting c) {
        if (Platform.isServer() && c instanceof EntityPlayer && this.itemMonitor != null) {
            final IItemList<IAEItemStack> monitorCache = this.itemMonitor instanceof RefreshableStorageMonitor refreshable
                ? refreshable.refreshExternalChanges(null, true)
                : this.itemMonitor.getStorageList();
            List<IAEItemStack> toSend = new ArrayList<>();
            for (final IAEItemStack is : monitorCache) {
                fluidHandler(is.copy());
                toSend.add(is);
            }
            SPacketMEItemInvUpdate piu = new SPacketMEItemInvUpdate();
            piu.addAll(toSend);
            AE2Thing.proxy.netHandler.sendTo(piu, (EntityPlayerMP) c);
        }
    }

    @Override
    public void removeCraftingFromCrafters(ICrafting c) {
        if (this.crafters.isEmpty() && this.itemMonitor != null) {
            this.itemMonitor.removeListener(this);
        }
    }

    @Override
    public void removeListener() {
        if (this.itemMonitor != null) this.itemMonitor.removeListener(this);
    }

    public IMEMonitor<IAEItemStack> getMonitor() {
        return this.itemMonitor;
    }
}
