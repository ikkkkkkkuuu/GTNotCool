package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.utils.serialization.IByteBufAdapter;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.GenericSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.GTNCSteamMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamVoidMiner;

import bwcrossmod.galacticgreg.VoidMinerUtility;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.util.GTUtility;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import gtneioreplugin.util.DimensionHelper;

// #tr GT5U.gui.text.vm.title
// # Ore Filter
// # zh_CN 矿石筛选

// #tr GT5U.gui.text.vm.searchhint
// # Search...
// # zh_CN 搜索...

// #tr GT5U.gui.button.vm.whitelist
// # Whitelist Mode: Only selected ores kept
// # zh_CN 白名单模式：仅保留选中矿石

// #tr GT5U.gui.button.vm.blacklist
// # Blacklist Mode: Selected ores voided
// # zh_CN 黑名单模式：选中矿石销毁

// #tr GT5U.gui.button.vm.select
// # Select All
// # zh_CN 全选

// #tr GT5U.gui.button.vm.deselect
// # Deselect All
// # zh_CN 取消全选

// #tr GT5U.gui.button.vm.tt.void
// # VOIDED
// # zh_CN 将被销毁

// #tr GT5U.gui.button.vm.tt.novoid
// # KEPT
// # zh_CN 将被保留

// #tr GT5U.gui.button.vm.dimension
// # Dimension Override
// # zh_CN 维度切换

// #tr GT5U.gui.button.vm.clear_dim
// # Clear Dimension
// # zh_CN 清除维度

public class LargeSteamVoidMinerGui extends GTNCSteamMultiBlockBaseGui {

    String search = "";
    private IPanelHandler filterPanel;
    private final ItemStackHandler dimSlotHandler = new ItemStackHandler(1) {

        @Override
        protected void onContentsChanged(int slot) {
            LargeSteamVoidMiner vm = vm();
            ItemStack stack = getStackInSlot(0);
            // Persist to machine NBT so it survives GUI close (both sides)
            vm.dimensionBlock = stack != null ? stack.copy() : null;
            vm.getBaseMetaTileEntity()
                .markDirty();

            // ItemDimensionDisplay returns abbreviation (e.g. "Ow"), convert to full name
            String newDim = null;
            if (stack != null) {
                String abbr = ItemDimensionDisplay.getDimension(stack);
                if (abbr != null && !abbr.isEmpty()) {
                    try {
                        newDim = DimensionHelper.getFullName(abbr);
                    } catch (IllegalStateException ignored) {}
                }
            }
            // Only recalculate if the dimension actually changed
            boolean changed = !Objects.equals(vm.targetDimName, newDim);
            vm.targetDimName = newDim;
            if (changed) {
                vm.recalculateDropMap();
                if (filterPanel != null && filterPanel.isPanelOpen()) {
                    filterPanel.closePanel();
                }
            }
        }
    };

    public LargeSteamVoidMinerGui(LargeSteamVoidMiner base) {
        super(base);
    }

    private LargeSteamVoidMiner vm() {
        return (LargeSteamVoidMiner) multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        // targetDimName lives only on the server (restored in loadNBTData), so a freshly reloaded client has it as
        // null and builds the ore filter list for the machine's own world dimension instead of the overridden one.
        // Sync it here — before any panel is built — so the authoritative value reaches the client on the initial
        // sync. IMPORTANT: the setter must be a PURE field assignment. Doing side effects here (e.g. refreshing the
        // drop map, which swaps out vm().dropMap) mutates state the filter panel's widget tree is built from while
        // the client is still processing the sync packet and restoring a remembered filter panel, which corrupts
        // its widgets (the search TextField) and crashes on the next tick. The drop map is refreshed safely later,
        // in a player-driven context, by createFilterPopup()'s own refreshDropMap() call when the panel is opened.
        syncManager.syncValue(
            "targetDimName",
            new StringSyncValue(
                () -> vm().targetDimName == null ? "" : vm().targetDimName,
                str -> vm().targetDimName = (str == null || str.isEmpty()) ? null : str));
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        IPanelHandler filterPopup = syncManager.syncedPanel("filter", true, this::createFilterPopup);
        this.filterPanel = filterPopup;
        IPanelHandler dimPanel = syncManager.syncedPanel("dimPanel", true, this::createDimSlotPanel);
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .child(createPowerSwitchButton())
            .child(createWirelessModeButton(syncManager))
            .child(createDimOverrideButton(dimPanel))
            .child(
                new ButtonWidget<>().size(16)
                    .marginBottom(2)
                    .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
                    .disableHoverBackground()
                    .overlay(GuiTextures.GEAR)
                    .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseDelta -> {
                        if (!mouseDelta.isClient()) {
                            vm().refreshDropMap();
                            vm().getBaseMetaTileEntity()
                                .markDirty();
                        }
                    }))
                    .onMousePressed(button -> {
                        if (filterPopup.isPanelOpen()) {
                            filterPopup.closePanel();
                        } else filterPopup.openPanel();
                        return true;
                    })
                    .tooltip(t -> t.addLine(translateToLocal("GT5U.gui.text.vm.title")))
                    .tooltipShowUpTimer(TOOLTIP_DELAY))
            .child(createStructureUpdateButton(syncManager));
    }

    private ButtonWidget<?> createDimOverrideButton(IPanelHandler dimPanel) {
        return new ButtonWidget<>().size(16)
            .marginBottom(2)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .overlay(GuiTextures.GEAR)
            .onMousePressed(button -> {
                if (dimPanel.isPanelOpen()) {
                    dimPanel.closePanel();
                } else dimPanel.openPanel();
                return true;
            })
            .tooltip(t -> t.addLine(translateToLocal("GT5U.gui.button.vm.dimension")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    public ModularPanel createDimSlotPanel(PanelSyncManager syncManager, IPanelHandler panelHandler) {
        syncManager.registerSlotGroup("dimInput", 1);
        // Restore persisted dimension block when panel opens
        ItemStack saved = vm().dimensionBlock;
        if (saved != null) {
            dimSlotHandler.setStackInSlot(0, saved.copy());
        }
        ModularSlot dimSlot = new ModularSlot(dimSlotHandler, 0);
        dimSlot.slotGroup("dimInput");

        return new ModularPanel("gtnc:vm:dim").child(ButtonWidget.panelCloseButton())
            .child(
                Flow.column()
                    .child(
                        IKey.lang("GT5U.gui.button.vm.dimension")
                            .asWidget()
                            .marginTop(12))
                    .child(
                        new ItemSlot().slot(dimSlot)
                            .size(18)
                            .margin(4))
                    .childPadding(4)
                    .margin(8)
                    .coverChildren())
            .coverChildren();
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager);
    }

    public ModularPanel createFilterPopup(PanelSyncManager syncManager, IPanelHandler panelHandler) {
        GenericSyncValue<ItemStackHandler, ?> listSyncer = new GenericSyncValue<>(
            ItemStackHandler.class,
            () -> vm().selected,
            handler -> vm().selected = handler,
            new ItemStackListAdapter()).allowC2S();
        syncManager.syncValue("selected", listSyncer);

        // Always refresh (dropMap may be stale world-dimension data from onFirstTick)
        vm().refreshDropMap();

        // Use static map as fallback when client dropMap hasn't been calculated yet
        VoidMinerUtility.DropMap displayMap = vm().dropMap;
        if (displayMap == null && vm().targetDimName != null) {
            displayMap = VoidMinerUtility.dropMapsByDimName.get(vm().targetDimName);
        }
        GTUtility.ItemId[] ores = displayMap != null ? sortOres(displayMap) : new GTUtility.ItemId[0];
        // Keep selected sized to the current dimension's ore list so the button grid (indexed 0..ores.length) never
        // reads an out-of-range slot. selected may carry a different size restored from NBT for another dimension.
        if (vm().selected.getSlots() != ores.length) {
            vm().selected.setSize(ores.length);
        }
        return new ModularPanel("gtnc:vm:filter").child(ButtonWidget.panelCloseButton())
            .child(
                Flow.column()
                    .child(
                        IKey.lang("GT5U.gui.text.vm.title")
                            .asWidget())
                    .child(
                        Flow.row()
                            .child(createOreToggleButtonGrid(syncManager, ores))
                            .child(createRightButtonColumn(listSyncer, ores))
                            .childPadding(3)
                            .crossAxisAlignment(Alignment.CrossAxis.START)
                            .coverChildren())
                    .child(
                        new TextFieldWidget().value(new StringSyncValue(() -> search, str -> search = str).allowC2S())
                            .hintText(translateToLocal("GT5U.gui.text.vm.searchhint"))
                            .autoUpdateOnChange(true)
                            .anchorLeft(0)
                            .width(100))
                    .childPadding(3)
                    .margin(8)
                    .coverChildren())
            .coverChildren();
    }

    private ListWidget<IWidget, ?> createOreToggleButtonGrid(PanelSyncManager syncManager, GTUtility.ItemId[] ores) {
        GenericSyncValue<ItemStackHandler, ?> syncer = (GenericSyncValue<ItemStackHandler, ?>) syncManager
            .findSyncHandler("selected");
        int buttonsPerRow = 10;
        int rowCount = (int) Math.ceil((double) ores.length / buttonsPerRow);
        return new ListWidget<>().child(
            Flow.row()
                .children(ores.length, index -> {
                    ItemStack stack = ores[index].getItemStack();
                    return new ToggleButton()
                        // Guard every access to selected by its current slot count. The button grid is built for
                        // the current dimension's ore list, but selected may momentarily have a different size
                        // (e.g. restored from NBT for another dimension) while a remembered filter panel is being
                        // rebuilt on GUI reopen. An unchecked index here throws "Slot N not in valid range",
                        // aborting widget-tree init and leaving the search TextField in an invalid state.
                        .value(
                            new BoolValue.Dynamic(
                                () -> index < vm().selected.getSlots() && vm().selected.getStackInSlot(index) != null,
                                bool -> {
                                    if (index >= vm().selected.getSlots()) return;
                                    if (bool) {
                                        vm().selected.insertItem(index, stack, false);
                                    } else vm().selected.extractItem(index, 1, false);
                                    syncer.setValue(vm().selected);
                                }))
                        .overlay(
                            new ItemDrawable(stack).asIcon()
                                .size(16))
                        .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED.withColorOverride(Color.GREY.main))
                        .background(false, GTGuiTextures.BUTTON_STANDARD)
                        .tooltipBuilder(false, t -> getOreButtonTooltip(t, stack, false))
                        .tooltipBuilder(true, t -> getOreButtonTooltip(t, stack, true))
                        .setEnabledIf($ -> matchesSearch(stack));
                })
                .wrap()
                .crossAxisAlignment(Alignment.CrossAxis.START)
                .width(18 * buttonsPerRow)
                .coverChildrenHeight()
                .collapseDisabledChild())
            .coverChildrenWidth()
            .height(18 * Math.min(rowCount, 8));
    }

    private Flow createRightButtonColumn(GenericSyncValue<ItemStackHandler, ?> syncer, GTUtility.ItemId[] ores) {
        return Flow.column()
            .child(
                new ToggleButton()
                    .value(new BooleanSyncValue(() -> vm().blacklist, bool -> vm().blacklist = bool).allowC2S())
                    .tooltip(false, t -> t.add(translateToLocal("GT5U.gui.button.vm.whitelist")))
                    .tooltip(true, t -> t.add(translateToLocal("GT5U.gui.button.vm.blacklist")))
                    .overlay(
                        false,
                        GTGuiTextures.OVERLAY_BUTTON_WHITELIST.asIcon()
                            .size(16))
                    .overlay(
                        true,
                        GTGuiTextures.OVERLAY_BUTTON_BLACKLIST.asIcon()
                            .size(16)))
            .child(new ButtonWidget<>().onMousePressed(button -> {
                for (int i = 0; i < ores.length; i++) {
                    vm().selected.setStackInSlot(i, ores[i].getItemStack());
                }
                syncer.setValue(vm().selected);
                return true;
            })
                .tooltip(t -> t.add(translateToLocal("GT5U.gui.button.vm.select")))
                .overlay(
                    GTGuiTextures.OVERLAY_BUTTON_CHECKMARK.asIcon()
                        .size(16)))
            .child(
                new ButtonWidget<>().tooltip(t -> t.add(translateToLocal("GT5U.gui.button.vm.deselect")))
                    .onMousePressed(button -> {
                        vm().selected = new ItemStackHandler(ores.length);
                        syncer.setValue(vm().selected);
                        return true;
                    })
                    .overlay(
                        GTGuiTextures.OVERLAY_BUTTON_CROSS.asIcon()
                            .size(16)))
            .crossAxisAlignment(Alignment.CrossAxis.START)
            .childPadding(3)
            .coverChildrenWidth()
            .heightRel(1F);
    }

    private GTUtility.ItemId[] sortOres(VoidMinerUtility.DropMap dropMap) {
        return Arrays.stream(dropMap.getOres())
            .sorted((ore1, ore2) -> {
                if (ore1.metaData() == ore2.metaData()) return 0;
                return ore1.metaData() > ore2.metaData() ? 1 : -1;
            })
            .toArray(GTUtility.ItemId[]::new);
    }

    private void getOreButtonTooltip(RichTooltip tt, ItemStack stack, boolean selected) {
        tt.addFromItem(stack);
        if (vm().blacklist) {
            tt.addLine(translateToLocal("GT5U.gui.button.vm.tt." + (selected ? "void" : "novoid")));
        } else tt.addLine(translateToLocal("GT5U.gui.button.vm.tt." + (selected ? "novoid" : "void")));
        tt.setAutoUpdate(true);
    }

    private boolean matchesSearch(ItemStack ore) {
        return search.isEmpty() || ore.getDisplayName()
            .toLowerCase()
            .contains(search.toLowerCase());
    }

    private static class ItemStackListAdapter implements IByteBufAdapter<ItemStackHandler> {

        @Override
        public ItemStackHandler deserialize(PacketBuffer buffer) throws IOException {
            ItemStackHandler handler = new ItemStackHandler();
            handler.deserializeNBT(buffer.readNBTTagCompoundFromBuffer());
            return handler;
        }

        @Override
        public void serialize(PacketBuffer buffer, ItemStackHandler u) throws IOException {
            buffer.writeNBTTagCompoundToBuffer(u.serializeNBT());
        }

        @Override
        public boolean areEqual(@NotNull ItemStackHandler t1, @NotNull ItemStackHandler t2) {
            if (t1.getSlots() != t2.getSlots()) return false;
            for (int i = 0; i < t1.getSlots(); i++) {
                if (!ItemStack.areItemStacksEqual(t1.getStackInSlot(i), t2.getStackInSlot(i))) return false;
            }
            return true;
        }
    }

    @Override
    protected boolean usesLockToSingleRecipeButton() {
        return false;
    }
}
