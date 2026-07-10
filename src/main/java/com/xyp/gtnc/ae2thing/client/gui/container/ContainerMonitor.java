package com.xyp.gtnc.ae2thing.client.gui.container;

import static com.xyp.gtnc.ae2thing.api.Constants.MessageType.UPDATE_PLAYER_ITEM;

import java.io.IOException;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.apache.commons.lang3.tuple.MutablePair;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.glodblock.github.util.Util;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.api.AE2ThingAPI;
import com.xyp.gtnc.ae2thing.client.gui.container.BaseMonitor.FluidMonitor;
import com.xyp.gtnc.ae2thing.client.gui.container.BaseMonitor.ItemMonitor;
import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.inventory.item.INetworkTerminal;
import com.xyp.gtnc.ae2thing.inventory.item.WirelessTerminal;
import com.xyp.gtnc.ae2thing.network.SPacketMEItemInvUpdate;
import com.xyp.gtnc.ae2thing.network.SPacketTypeFilter;
import com.xyp.gtnc.ae2thing.util.HBMAeAddonUtil;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.ITerminalTypeFilterProvider;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.IConfigManager;
import appeng.container.guisync.GuiSync;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.helpers.IContainerCraftingPacket;
import appeng.items.misc.ItemEncodedPattern;
import appeng.me.helpers.ChannelPowerSrc;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import appeng.util.item.AEFluidStack;
import appeng.util.item.AEItemStack;

public abstract class ContainerMonitor extends ContainerMEMonitorable
    implements IAEAppEngInventory, IContainerCraftingPacket, ITypeFilterContainer {

    protected final IItemList<IAEItemStack> items = AEApi.instance()
        .storage()
        .createItemList();
    protected final ItemMonitor monitor;
    protected final FluidMonitor fluidMonitor;
    private boolean typeFilterSynced = false;

    // Fields that shadow private/package-private fields in ContainerMEMonitorable
    protected ITerminalHost host;
    protected IConfigManagerHost gui;
    protected IConfigManager serverCM;
    protected IGridNode networkNode;

    // Power and wireless terminal fields (from BaseNetworkContainer)
    protected final EntityPlayer player;
    protected WirelessTerminal terminal;
    protected int ticks;
    protected final double powerMultiplier = 0.5;

    @GuiSync(1)
    public boolean hasPower = false;

    public ContainerMonitor(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable, false);
        this.host = monitorable;
        this.player = ip.player;
        this.monitor = new ItemMonitor(this.crafters);
        this.fluidMonitor = new FluidMonitor(this.crafters);
        if (Platform.isServer()) {
            if (monitorable instanceof INetworkTerminal it) {
                this.networkNode = it.getGridNode();
            }
            if (this.host instanceof WirelessTerminal wt) {
                this.terminal = wt;
                this.setPowerSource(this.terminal);
            } else if (this instanceof INetworkTerminal it) {
                this.setPowerSource(
                    new ChannelPowerSrc(
                        it.getGridNode(),
                        it.getGrid()
                            .getCache(IEnergyGrid.class)));
            }
            this.setMonitor();
        }
    }

    protected void dropItem(ItemStack is) {
        if (is == null || is.stackSize <= 0) return;
        ItemStack itemStack = is.copy();
        int i = itemStack.getMaxStackSize();
        while (itemStack.stackSize > 0) {
            if (i > itemStack.stackSize) {
                if (!getPlayerInv().addItemStackToInventory(itemStack.copy())) {
                    getPlayerInv().player.entityDropItem(itemStack.copy(), 0);
                }
                break;
            } else {
                itemStack.stackSize -= i;
                ItemStack item = itemStack.copy();
                item.stackSize = i;
                if (!getPlayerInv().addItemStackToInventory(item)) {
                    getPlayerInv().player.entityDropItem(item, 0);
                }
            }
        }
    }

    protected void dropItem(ItemStack itemStack, int stackSize) {
        if (itemStack == null || itemStack.stackSize <= 0) return;
        ItemStack is = itemStack.copy();
        is.stackSize = stackSize;
        this.dropItem(is);
    }

    protected void adjustStack(ItemStack stack) {
        if (stack != null && stack.stackSize > stack.getMaxStackSize()) {
            dropItem(stack, stack.stackSize - stack.getMaxStackSize());
            stack.stackSize = stack.getMaxStackSize();
        }
    }

    abstract void setMonitor();

    public void setGui(@Nonnull final IConfigManagerHost gui) {
        this.gui = gui;
    }

    public IMEMonitor<IAEItemStack> getMonitor() {
        return this.monitor.getMonitor();
    }

    @Override
    public ITerminalTypeFilterProvider getTypeFilterHost() {
        return this.host instanceof ITerminalTypeFilterProvider provider ? provider : null;
    }

    protected boolean isInvalid() {
        return !this.monitor.isValid(null);
    }

    protected void processItemList() {
        this.monitor.processItemList();
    }

    @Override
    public void detectAndSendChanges() {
        if (Platform.isServer()) {
            if (isInvalid()) {
                this.setValidContainer(false);
            }
            if (this.serverCM != null) {
                for (final Settings set : this.serverCM.getSettings()) {
                    final Enum<?> sideLocal = this.serverCM.getSetting(set);
                    final Enum<?> sideRemote = this.getConfigManager()
                        .getSetting(set);

                    if (sideLocal != sideRemote) {
                        this.getConfigManager()
                            .putSetting(set, sideLocal);
                        for (final Object crafter : this.crafters) {
                            try {
                                NetworkHandler.instance.sendTo(
                                    new PacketValueConfig(set.name(), sideLocal.name()),
                                    (EntityPlayerMP) crafter);
                            } catch (final IOException e) {
                                AELog.debug(e);
                            }
                        }
                    }
                }
            }
            // Power extraction for wireless terminals
            if (this.terminal != null && this.hasPower) {
                ticks = this.terminal.getWirelessObject()
                    .extractPower(getPowerMultiplier() * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG, ticks);
            }
            processItemList();
            syncTypeFilter();
            super.detectAndSendChanges();
        }
    }

    protected void updatePowerStatus() {
        try {
            if (this.terminal != null && this.terminal.getGridNode() != null) {
                this.setPowered(
                    this.terminal.getGridNode()
                        .isActive());
            } else if (this.getPowerSource() instanceof IEnergyGrid eg) {
                this.setPowered(eg.isNetworkPowered());
            } else {
                this.setPowered(
                    this.getPowerSource()
                        .extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.8);
            }
        } catch (final Throwable ignore) {}
    }

    protected void setPowered(final boolean isPowered) {
        this.hasPower = isPowered;
    }

    public double getPowerMultiplier() {
        return this.powerMultiplier;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int idx) {
        boolean didSomething = false;
        final Object container = this instanceof com.xyp.gtnc.ae2thing.client.gui.container.widget.IWidgetPatternContainer w
            ? w.getContainer()
            : this;
        final Slot clickSlot = this.inventorySlots.get(idx);
        final ItemStack is = clickSlot.getStack();
        if (is != null && is.stackSize == 1 && is.getItem() instanceof ItemEncodedPattern) {
            try {
                final java.lang.reflect.Method getPatternOutputSlot = container.getClass()
                    .getMethod("getPatternOutputSlot");
                final Object patternSlot = getPatternOutputSlot.invoke(container);
                if (patternSlot != null) {
                    final java.lang.reflect.Method getHasStack = patternSlot.getClass()
                        .getMethod("getHasStack");
                    if (!(boolean) getHasStack.invoke(patternSlot)) {
                        final ItemStack output = is.copy();
                        patternSlot.getClass()
                            .getMethod("putStack", ItemStack.class)
                            .invoke(patternSlot, output);
                        p.inventory.setInventorySlotContents(clickSlot.getSlotIndex(), null);
                        didSomething = true;
                    }
                }
            } catch (final Exception ignored) {}
        }
        if (didSomething) {
            return null;
        }
        // Shift-clicking a normal (non-pattern) item from the player inventory should push it straight into the ME
        // network, NOT into the interface pattern-encoding fake slots. AEBaseContainer.transferStackInSlot would
        // otherwise treat the encoding area as a valid shift destination (getValidDestinationFakeSlot) and drop the
        // item there. Route player-side non-pattern items directly to shiftStoreItem (ME insert) and bypass base.
        if (Platform.isServer() && clickSlot instanceof appeng.container.slot.AppEngSlot aeSlot
            && aeSlot.isPlayerSide()
            && is != null
            && !(is.getItem() instanceof ItemEncodedPattern)) {
            final int before = is.stackSize;
            final ItemStack remainder = this.shiftStoreItem(is);
            if (remainder == null || remainder.stackSize != before) {
                // Something (possibly all) was inserted into the ME network.
                aeSlot.putStack(remainder);
                // 只回传这一个被改动的玩家槽，不调用 detectAndSendChanges()。
                // 客户端 shift 点击是本地预测的：ME 插入是服务端专属逻辑(Platform.isServer 保护)，
                // 客户端那边 super.transferStackInSlot 不会真正移走物品，于是物品在客户端界面残留，
                // 要到关闭 GUI 客户端重建容器才消失。这里直接把服务端的真实槽内容(remainder，通常为 null)
                // 推给正在看的玩家即可修正。
                // 为什么不用 detectAndSendChanges：空格+左键(AE2 MOVE_REGION)会在同一 tick 内对每个同类槽
                // 各调一次 transferStackInSlot(满背包 = 36 次)，每次都跑全表 processItemList + 遍历所有槽+ME
                // 监视器 → 瞬时 MSPT 爆炸。sendSlotContents 是单槽 O(1)，即使 36 次也无压力。
                for (final Object crafter : this.crafters) {
                    if (crafter instanceof ICrafting c) {
                        c.sendSlotContents(this, idx, remainder);
                    }
                }
                return null;
            }
            // Nothing could be stored (network full / no power) — fall through to vanilla shift behavior so the item
            // can still shuffle between hotbar and main inventory.
        }
        return super.transferStackInSlot(p, idx);
    }

    private void syncTypeFilter() {
        if (this.typeFilterSynced) {
            return;
        }
        final ITerminalTypeFilterProvider provider = this.getTypeFilterHost();
        if (provider == null) {
            this.typeFilterSynced = true;
            return;
        }
        for (final Object crafter : this.crafters) {
            if (crafter instanceof EntityPlayerMP playerMP) {
                AE2Thing.proxy.netHandler.sendTo(new SPacketTypeFilter(provider.getTypeFilter(playerMP)), playerMP);
            }
        }
        this.typeFilterSynced = true;
    }

    protected IConfigManagerHost getGui() {
        return this.gui;
    }

    @Override
    public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
        if (this.getGui() != null) {
            this.getGui()
                .updateSetting(manager, settingName, newValue);
        }
    }

    @Override
    public void addCraftingToCrafters(final ICrafting c) {
        updatePowerStatus();
        super.addCraftingToCrafters(c);
        this.monitor.queueInventory(c);
    }

    @Override
    public void removeCraftingFromCrafters(final ICrafting c) {
        super.removeCraftingFromCrafters(c);
        this.monitor.removeCraftingFromCrafters(c);
    }

    @Override
    public void onContainerClosed(final EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.monitor.getMonitor() != null) this.monitor.removeListener();
    }

    private void extractPlayerInventoryItemStack(EntityPlayer player, ItemStack itemStack, int stackSize) {
        for (int x = 0; x < player.inventory.mainInventory.length; x++) {
            ItemStack is = player.inventory.mainInventory[x];
            if (is == null) continue;
            if (Platform.isSameItemPrecise(is, itemStack)) {
                ItemStack tmp = is.copy();
                if (is.stackSize < stackSize) {
                    stackSize = is.stackSize;
                }
                is.stackSize -= stackSize;
                tmp.stackSize = stackSize;
                if (is.stackSize == 0) {
                    player.inventory.setInventorySlotContents(x, null);
                }
                player.inventory.setItemStack(tmp);
                player.inventory.markDirty();
                return;
            }
        }
    }

    private boolean canFillDefaultContainer(IAEFluidStack ifs) {
        if (ifs == null) return false;
        MutablePair<Integer, ItemStack> result = null;
        ItemStack container = AE2ThingAPI.instance()
            .getFluidContainer(ifs);
        if (Util.FluidUtil.isFluidContainer(
            AE2ThingAPI.instance()
                .getFluidContainer(ifs))) {
            result = Util.FluidUtil.fillStack(container, ifs.getFluidStack());
        }
        return result != null && result.left != 0;
    }

    public void postChange(IAEFluidStack fluid, EntityPlayer player, int slotIndex, boolean shift) {
        ItemStack targetStack = getTargetStack(player, slotIndex);
        if (targetStack == null) {
            if (!canFillDefaultContainer(fluid)) return;
            IAEItemStack extractItem = this.monitor.getMonitor()
                .extractItems(
                    AEItemStack.create(
                        AE2ThingAPI.instance()
                            .getFluidContainer(fluid)),
                    Actionable.MODULATE,
                    this.getActionSource());
            if (extractItem != null) {
                player.inventory.setItemStack(extractItem.getItemStack());
            } else {
                this.extractPlayerInventoryItemStack(
                    player,
                    AE2ThingAPI.instance()
                        .getFluidContainer(fluid),
                    1);
            }
            targetStack = getTargetStack(player, slotIndex);
        }

        if (targetStack == null) return;
        // The primary output itemstack
        if (fluid != null
            && ((Mods.HBM_AE_ADDON.isModLoaded() && HBMAeAddonUtil.getItemIsEmptyContainer(targetStack, fluid))
                || Util.FluidUtil.isEmpty(targetStack))) {
            // Situation 1.a: Empty fluid container, and nonnull slot
            extractFluid(fluid, player, slotIndex, shift);
        } else if ((Util.FluidUtil.isFluidContainer(targetStack) && !Util.FluidUtil.isEmpty(targetStack))
            || (Mods.HBM_AE_ADDON.isModLoaded() && HBMAeAddonUtil.getItemHasFluidType(targetStack))) {
                // Situation 2.a: We are holding a non-empty container.
                insertFluid(player, slotIndex, shift);
                // End of situation 2.a
            }
        // No op (Any other situation)

        this.detectAndSendChanges();
    }

    private ItemStack getTargetStack(EntityPlayer player, int slotIndex) {
        if (slotIndex == -1) {
            return player.inventory.getItemStack();
        } else {
            return player.inventory.getStackInSlot(slotIndex);
        }
    }

    /**
     * The insert operation. For input, we have a filled container stack. For outputs, we have the following:
     * <ol>
     * <li>Leftover filled container stack</li>
     * <li>Empty containers</li>
     * <li>Partially filled container x1</li>
     * </ol>
     * In order above, the itemstack at `slotIndex` is transformed into the output.
     */
    private void insertFluid(EntityPlayer player, int slotIndex, boolean shift) {
        ItemStack targetStack = getTargetStack(player, slotIndex);
        final int containersRequestedToInsert = shift ? targetStack.stackSize : 1;

        // Step 1: Determine container characteristics and verify fluid to be extractable
        final int fluidPerContainer;
        final FluidStack fluidStackPerContainer;
        final boolean partialInsertSupported;
        if (targetStack.getItem() instanceof IFluidContainerItem fcItem) {
            ItemStack test = targetStack.copy();
            test.stackSize = 1;
            fluidStackPerContainer = fcItem.drain(test, Integer.MAX_VALUE, false);
            if (fluidStackPerContainer == null || fluidStackPerContainer.amount == 0) {
                return;
            }

            fluidPerContainer = fluidStackPerContainer.amount;
            partialInsertSupported = true;
        } else if (FluidContainerRegistry.isContainer(targetStack)) {
            ItemStack emptyTank = FluidContainerRegistry.drainFluidContainer(targetStack);
            if (emptyTank == null) {
                return;
            }
            fluidStackPerContainer = FluidContainerRegistry.getFluidForFilledItem(targetStack);
            fluidPerContainer = fluidStackPerContainer.amount;
            partialInsertSupported = false;
        } else {
            return;
        }

        // Step 2: determine network capacity
        final IAEFluidStack totalFluid = AEFluidStack.create(fluidStackPerContainer);
        totalFluid.setStackSize((long) fluidPerContainer * containersRequestedToInsert);

        final IAEFluidStack notInsertable = this.injectFluids(totalFluid, Actionable.SIMULATE);

        final long insertableFluid;
        if (notInsertable == null || notInsertable.getStackSize() == 0) {
            insertableFluid = totalFluid.getStackSize();
        } else {
            long insertable = totalFluid.getStackSize() - notInsertable.getStackSize();
            if (partialInsertSupported) {
                insertableFluid = insertable;
            } else {
                // avoid remainder
                insertableFluid = insertable - (insertable % fluidPerContainer);
            }
        }
        totalFluid.setStackSize(insertableFluid);

        // Step 3: perform insert
        final long totalInserted;
        final IAEFluidStack notInserted = this.injectFluids(totalFluid, Actionable.MODULATE);
        if (notInserted != null && notInserted.getStackSize() > 0) {
            // User has a setup that causes discrepancy between simulation and modulation. Likely double storage bus.
            long total = totalFluid.getStackSize() - notInserted.getStackSize();
            if (total == 0) {
                return;
            }
            if (partialInsertSupported) {
                totalInserted = total;
            } else {
                // We cant have partially filled containers -> user will receive a fluid packet as last resort
                long overflowAmount = fluidPerContainer - (total % fluidPerContainer);
                IAEFluidStack overflow = AEFluidStack.create(fluidStackPerContainer);
                overflow.setStackSize(overflowAmount);
                dropItem(ItemFluidPacket.newStack(overflow));
                totalInserted = total + overflowAmount;
            }
        } else {
            totalInserted = totalFluid.getStackSize();
        }

        // Step 4: calculate outputs
        final int emptiedTanks = (int) (totalInserted / fluidPerContainer);
        final int partialDrain = (int) (totalInserted % fluidPerContainer);
        final int partialTanks = partialDrain > 0 && partialInsertSupported ? 1 : 0;
        final int usedTanks = emptiedTanks + partialTanks;
        final int untouchedTanks = targetStack.stackSize - usedTanks;

        ItemStack emptiedTanksStack;
        final ItemStack partialTanksStack;

        if (targetStack.getItem() instanceof IFluidContainerItem fcItem) {
            if (emptiedTanks > 0) {
                emptiedTanksStack = targetStack.copy();
                emptiedTanksStack.stackSize = 1;
                fcItem.drain(emptiedTanksStack, fluidPerContainer, true);
                emptiedTanksStack.stackSize = emptiedTanks;
            } else {
                emptiedTanksStack = null;
            }
            if (partialTanks > 0) {
                partialTanksStack = targetStack.copy();
                partialTanksStack.stackSize = 1;
                fcItem.drain(partialTanksStack, partialDrain, true);
            } else {
                partialTanksStack = null;
            }
        } else if (Mods.HBM_AE_ADDON.isModLoaded() && HBMAeAddonUtil.getItemHasFluidType(targetStack)) {
            emptiedTanksStack = null;
            // Not possible > see Step 2 and Step 3
            partialTanksStack = null;
        } else {
            if (emptiedTanks > 0) {
                emptiedTanksStack = FluidContainerRegistry.drainFluidContainer(targetStack);
                emptiedTanksStack.stackSize = emptiedTanks;
            } else {
                emptiedTanksStack = null;
            }
            // Not possible > see Step 2 and Step 3
            partialTanksStack = null;
        }

        // Done. Put the output in the inventory or ground, and update stack size.
        boolean shouldSendStack = true;
        if (slotIndex == -1) {
            // Item is in mouse hand
            if (untouchedTanks > 0) {
                targetStack.stackSize = untouchedTanks;
                adjustStack(targetStack);
                dropItem(emptiedTanksStack);
                dropItem(partialTanksStack);
            } else if (emptiedTanksStack != null) {
                adjustStack(emptiedTanksStack);
                player.inventory.setItemStack(emptiedTanksStack);
                dropItem(partialTanksStack);
            } else if (partialTanksStack != null) {
                player.inventory.setItemStack(partialTanksStack);
            } else {
                player.inventory.setItemStack(null);
                shouldSendStack = false;
            }
        } else {
            // Shift clicked in
            if (untouchedTanks > 0) {
                targetStack.stackSize = untouchedTanks;
                adjustStack(targetStack);
                dropItem(emptiedTanksStack);
                dropItem(partialTanksStack);
            } else if (emptiedTanksStack != null) {
                adjustStack(emptiedTanksStack);
                player.inventory.setInventorySlotContents(slotIndex, emptiedTanksStack);
                dropItem(partialTanksStack);
            } else if (partialTanksStack != null) {
                player.inventory.setInventorySlotContents(slotIndex, partialTanksStack);
            } else {
                player.inventory.setItemStack(null);
                shouldSendStack = false;
            }
        }
        SPacketMEItemInvUpdate packet = new SPacketMEItemInvUpdate(UPDATE_PLAYER_ITEM);
        if (shouldSendStack) {
            packet.appendItem(
                AEApi.instance()
                    .storage()
                    .createItemStack(player.inventory.getItemStack()));
        }
        AE2Thing.proxy.netHandler.sendTo(packet, (EntityPlayerMP) player);
    }

    /**
     * The extract operation. For input, we have an empty container stack. For outputs, we have the following:
     * <ol>
     * <li>Leftover empty container stack</li>
     * <li>Filled containers (full)</li>
     * <li>Partially filled container x1</li>
     * </ol>
     * In order above, the itemstack at `slotIndex` is transformed into the output.
     */
    private void extractFluid(IAEFluidStack clientRequestedFluid, EntityPlayer player, int slotIndex, boolean shift) {
        if (slotIndex != -1) {
            // shift-click from inventory cant fill fluids
            return;
        }
        final ItemStack targetStack = player.inventory.getItemStack();
        final int containersRequestedToExtract = shift ? targetStack.stackSize : 1;

        final FluidStack clientRequestedFluidStack = clientRequestedFluid.getFluidStack();
        clientRequestedFluidStack.amount = Integer.MAX_VALUE;

        // Step 1: Determine container characteristics and verify fluid to be insertable
        final int fluidPerContainer;
        final boolean partialInsertSupported;
        if (targetStack.getItem() instanceof IFluidContainerItem fcItem) {
            ItemStack testStack = targetStack.copy();
            testStack.stackSize = 1;
            fluidPerContainer = fcItem.fill(testStack, clientRequestedFluidStack, false);
            if (fluidPerContainer == 0) {
                return;
            }
            partialInsertSupported = true;
        } else if (FluidContainerRegistry.isContainer(targetStack)) {
            fluidPerContainer = FluidContainerRegistry.getContainerCapacity(clientRequestedFluidStack, targetStack);
            partialInsertSupported = false;
        } else if (Mods.HBM_AE_ADDON.isModLoaded()
            && HBMAeAddonUtil.getItemIsEmptyContainer(targetStack, clientRequestedFluid)) {
                fluidPerContainer = HBMAeAddonUtil.getEmptyContainerAmount(targetStack, clientRequestedFluid);
                partialInsertSupported = false;
            } else {
                return;
            }

        // Step 2: determine fluid in network
        final IAEFluidStack totalRequestedFluid = clientRequestedFluid.copy();
        totalRequestedFluid.setStackSize((long) fluidPerContainer * containersRequestedToExtract);

        final IAEFluidStack availableFluid = this.extractFluids(totalRequestedFluid, Actionable.SIMULATE);
        if (availableFluid == null || availableFluid.getStackSize() == 0) {
            return;
        }

        if (availableFluid.getStackSize() != totalRequestedFluid.getStackSize() && !partialInsertSupported) {
            availableFluid.decStackSize(availableFluid.getStackSize() % fluidPerContainer);
        }

        // Step 3: perform extract
        final IAEFluidStack extracted = this.extractFluids(availableFluid, Actionable.MODULATE);
        final long totalExtracted = extracted != null ? extracted.getStackSize() : 0;

        // Step 4: calculate outputs
        final int filledTanks = (int) (totalExtracted / fluidPerContainer);
        final int partialFill = (int) (totalExtracted % fluidPerContainer);
        final int partialTanks = partialFill > 0 && partialInsertSupported ? 1 : 0;
        final int usedTanks = filledTanks + partialTanks;
        final int untouchedTanks = targetStack.stackSize - usedTanks;

        ItemStack filledTanksStack;
        ItemStack partialTanksStack;

        if (targetStack.getItem() instanceof IFluidContainerItem fcItem) {
            if (filledTanks > 0) {
                filledTanksStack = targetStack.copy();
                filledTanksStack.stackSize = 1;
                FluidStack toInsert = extracted.getFluidStack()
                    .copy();
                toInsert.amount = fluidPerContainer;
                fcItem.fill(filledTanksStack, toInsert, true);
                filledTanksStack.stackSize = filledTanks;
            } else {
                filledTanksStack = null;
            }
            if (partialTanks > 0) {
                partialTanksStack = targetStack.copy();
                partialTanksStack.stackSize = 1;
                FluidStack toInsert = extracted.getFluidStack()
                    .copy();
                toInsert.amount = partialFill;
                fcItem.fill(partialTanksStack, toInsert, true);
            } else {
                partialTanksStack = null;
            }
        } else if (Mods.HBM_AE_ADDON.isModLoaded()
            && HBMAeAddonUtil.getItemIsEmptyContainer(targetStack, clientRequestedFluid)) {
                if (filledTanks > 0) {
                    filledTanksStack = targetStack.copy();
                    filledTanksStack.stackSize = 1;
                    FluidStack toInsert = extracted.getFluidStack()
                        .copy();
                    toInsert.amount = fluidPerContainer;
                    filledTanksStack = HBMAeAddonUtil.getFillContainer(targetStack, clientRequestedFluid);
                    filledTanksStack.stackSize = filledTanks;
                } else {
                    filledTanksStack = null;
                }
                if (partialTanks > 0) {
                    partialTanksStack = targetStack.copy();
                    partialTanksStack.stackSize = 1;
                    FluidStack toInsert = extracted.getFluidStack()
                        .copy();
                    toInsert.amount = partialFill;
                    partialTanksStack = HBMAeAddonUtil.getFillContainer(targetStack, clientRequestedFluid);
                } else {
                    partialTanksStack = null;
                }
            } else {
                if (filledTanks > 0) {
                    FluidStack toInsert = extracted.getFluidStack()
                        .copy();
                    toInsert.amount = fluidPerContainer;
                    filledTanksStack = FluidContainerRegistry.fillFluidContainer(toInsert, targetStack);
                    filledTanksStack.stackSize = filledTanks;
                } else {
                    filledTanksStack = null;
                }
                if (partialFill > 0) {
                    // User has a setup that causes discrepancy between simulation and modulation. Likely double storage
                    // bus.
                    // We cant have partially filled containers -> user will receive a fluid packet as last resort
                    IAEFluidStack overflow = extracted.copy();
                    overflow.setStackSize(partialFill);
                    dropItem(ItemFluidPacket.newStack(overflow));
                }
                partialTanksStack = null;
            }

        // Done. Put the output in the inventory or ground, and update stack size.
        // We can assume slotIndex == -1, since we don't actually allow extraction via shift click.
        boolean shouldSendStack = true;
        if (untouchedTanks > 0) {
            ItemStack emptyStack = player.inventory.getItemStack();
            emptyStack.stackSize = untouchedTanks;
            adjustStack(emptyStack);
            dropItem(filledTanksStack);
            dropItem(partialTanksStack);
        } else if (filledTanksStack != null) {
            adjustStack(filledTanksStack);
            player.inventory.setItemStack(filledTanksStack);
            dropItem(partialTanksStack);
        } else if (partialTanksStack != null) {
            player.inventory.setItemStack(partialTanksStack);
        } else {
            player.inventory.setItemStack(null);
            shouldSendStack = false;
        }
        SPacketMEItemInvUpdate packet = new SPacketMEItemInvUpdate(UPDATE_PLAYER_ITEM);
        if (shouldSendStack) {
            packet.appendItem(
                AEApi.instance()
                    .storage()
                    .createItemStack(player.inventory.getItemStack()));
        }
        AE2Thing.proxy.netHandler.sendTo(packet, (EntityPlayerMP) player);
    }

    protected IAEFluidStack extractFluids(IAEFluidStack ifs, Actionable mode) {
        if (ifs.getStackSize() == 0) return ifs;
        return this.host.getFluidInventory()
            .extractItems(ifs, mode, this.getActionSource());

    }

    protected IAEFluidStack injectFluids(IAEFluidStack ifs, Actionable mode) {
        return this.host.getFluidInventory()
            .injectItems(ifs, mode, this.getActionSource());
    }
}
