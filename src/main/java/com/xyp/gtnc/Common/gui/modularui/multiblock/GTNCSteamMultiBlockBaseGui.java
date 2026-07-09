package com.xyp.gtnc.Common.gui.modularui.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTUtility;
import gregtech.api.util.StringUtils;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

public class GTNCSteamMultiBlockBaseGui extends MTEMultiBlockBaseGui<GTNCSteamMultiBlockBase<?>> {

    /** Local copy of paid cost indices, synced from machine on panel open. */
    protected final Set<Integer> paidCostIndices = new HashSet<>();

    public GTNCSteamMultiBlockBaseGui(GTNCSteamMultiBlockBase<?> multiblock) {
        super(multiblock);
    }

    // ============================================================
    // Celestial-themed button column
    // ============================================================

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .child(createPowerSwitchButton())
            .child(createUpgradeTreeButton(panel, syncManager))
            .child(createWirelessModeButton(syncManager))
            .child(createStructureUpdateButton(syncManager));
    }

    // #tr GTNC_gui_button_upgrade_tree
    // # Upgrade Tree
    // # zh_CN 天途

    protected ButtonWidget<?> createUpgradeTreeButton(ModularPanel panel, PanelSyncManager syncManager) {
        IPanelHandler upgradePanel = syncManager
            .syncedPanel("upgradeTreePanel", true, (sm, sh) -> createUpgradeTreePanel(sm, panel, syncManager));
        return new ButtonWidget<>().size(16)
            .marginBottom(2)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .overlay(GTNCGuiTextures.OVERLAY_BUTTON_ARROW_BLUE_UP)
            .onMousePressed(d -> {
                if (!upgradePanel.isPanelOpen()) {
                    upgradePanel.openPanel();
                } else {
                    upgradePanel.closePanel();
                }
                return true;
            })
            .tooltip(t -> t.addLine(StatCollector.translateToLocal("GTNC_gui_button_upgrade_tree")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    // ============================================================
    // Upgrade Tree Panel — material insertion
    // ============================================================

    protected ModularPanel createUpgradeTreePanel(PanelSyncManager sm, ModularPanel parent,
        PanelSyncManager mainSyncManager) {
        ItemStackHandler handler = new ItemStackHandler(16);
        // 槽位属于本子面板的 sync manager（sm），必须注册到 sm 而非 mainSyncManager，
        // 否则子面板 initialize 时在容器里找不到该组 → 每次打开抛 "SlotGroup not registered"
        sm.registerSlotGroup("upgradeTreeInput", 16);

        ModularPanel panel = new ModularPanel("upgradeTreePanel").relative(parent)
            .leftRelOffset(0, 4)
            .topRelOffset(0, 3)
            .size(190, 115)
            .background(GTNCGuiTextures.BACKGROUND_SPACE)
            .disableHoverBackground()
            .child(ButtonWidget.panelCloseButton());

        panel.child(
            IKey.str("Pay Upgrade Costs")
                .style(EnumChatFormatting.DARK_GRAY)
                .alignment(Alignment.CENTER)
                .asWidget()
                .horizontalCenter()
                .marginTop(5));

        Flow mainRow = Flow.row()
            .size(180, 72)
            .topRel(0)
            .leftRel(0)
            .marginLeft(5)
            .marginTop(16);

        GTNCSteamMultiBlockBase<?> base = (GTNCSteamMultiBlockBase<?>) multiblock;
        List<ItemStack> costs = base.getUpgradeCosts();
        // Sync paid indices from machine → survives game restart
        paidCostIndices.clear();
        paidCostIndices.addAll(base.paidUpgradeCostIndices);
        // Keep client in sync with server via bitmask sync value
        sm.syncValue("paidBits", new IntSyncValue(() -> {
            int bits = 0;
            for (int idx : base.paidUpgradeCostIndices) {
                if (idx < 12) bits |= (1 << idx);
            }
            return bits;
        }, bits -> {
            paidCostIndices.clear();
            for (int i = 0; i < 12; i++) {
                if ((bits & (1 << i)) != 0) paidCostIndices.add(i);
            }
        }));
        // 3 cost columns × 4 rows each
        mainRow.child(buildCostColumn(costs, 0));
        mainRow.child(buildCostColumn(costs, 4));
        mainRow.child(buildCostColumn(costs, 8));

        // 4×4 input slots grid
        mainRow.child(buildUpgradeSlotGrid(handler, "upgradeTreeInput"));

        panel.child(mainRow);

        // Consume inputs button — server-side processing via InteractionSyncHandler
        InteractionSyncHandler upgradeSync = new InteractionSyncHandler().setOnMousePressed(mouseDelta -> {
            if (!mouseDelta.isClient()) {
                performUpgrade(handler);
            }
        });
        panel.child(
            new ButtonWidget<>().syncHandler(upgradeSync)
                .overlay(
                    IKey.str("Consume Upgrade Materials")
                        .style(EnumChatFormatting.DARK_GRAY)
                        .alignment(Alignment.CENTER)
                        .scale(0.75f))
                .disableHoverBackground()
                .disableHoverOverlay()
                .size(180, 18)
                .bottomRel(0)
                .leftRel(0)
                .marginBottom(5)
                .marginLeft(5));

        return panel;
    }

    /** Single cost column — 4 rows, each matching Godforge createCostRow exactly. */
    protected IWidget buildCostColumn(List<ItemStack> costs, int start) {
        Flow column = Flow.column()
            .size(36, 72);
        for (int i = 0; i < 4; i++) {
            column.child(buildCostRow(costs, start + i));
        }
        return column;
    }

    /**
     * A single cost row, mirroring Godforge ManualInsertionPanel.createCostRow:
     * [disabled-btn | item-icon | xNN | ✅] stacked in a 36×18 Flow.row.
     */
    protected Flow buildCostRow(List<ItemStack> costs, int index) {
        return Flow.row()
            .size(36, 18)
            .collapseDisabledChild()
            .child(
                GTGuiTextures.BUTTON_STANDARD_DISABLED.asWidget()
                    .size(18)
                    .setEnabledIf($ -> index >= costs.size()))
            .child(new SlotLikeButtonWidget(() -> costs.get(index)).onMousePressed(d -> {
                ItemStack stack = costs.get(index);
                if (d == 0) {
                    GuiCraftingRecipe.openRecipeGui("item", stack);
                } else if (d == 1) {
                    GuiUsageRecipe.openRecipeGui("item", stack);
                }
                return true;
            })
                .tooltipDynamic(t -> {
                    if (index < costs.size()) {
                        t.addFromItem(costs.get(index));
                    }
                })
                .tooltipAutoUpdate(true)
                .setEnabledIf($ -> index < costs.size()))
            .child(IKey.dynamic(() -> {
                if (index >= costs.size() || paidCostIndices.contains(index)) return "";
                return EnumChatFormatting.GOLD + "x" + costs.get(index).stackSize;
            })
                .alignment(Alignment.CENTER)
                .scale(0.8f)
                .asWidget()
                .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                .size(18)
                .setEnabledIf($ -> index < costs.size() && !paidCostIndices.contains(index)))
            .child(
                GTGuiTextures.GREEN_CHECKMARK_11x9.asWidget()
                    .size(11, 9)
                    .marginRight(4)
                    .marginTop(5)
                    .setEnabledIf($ -> paidCostIndices.contains(index)));
    }

    /** Right side — 4×4 input slot grid (exactly like Godforge). */
    protected SlotGroupWidget buildUpgradeSlotGrid(ItemStackHandler handler, String group) {
        String[] matrix = new String[4];
        String repeat = StringUtils.getRepetitionOf('s', 4);
        Arrays.fill(matrix, repeat);
        return SlotGroupWidget.builder()
            .matrix(matrix)
            .key('s', i -> new ItemSlot().slot(new ModularSlot(handler, i).slotGroup(group)))
            .build()
            .rightRel(0);
    }

    /**
     * Called when Consume is pressed. Reads input slots, checks against unpaid costs,
     * consumes matching items, marks them paid, then triggers upgrade on the machine.
     */
    protected void performUpgrade(ItemStackHandler handler) {
        GTNCSteamMultiBlockBase<?> base = (GTNCSteamMultiBlockBase<?>) multiblock;
        List<ItemStack> costs = base.getUpgradeCosts();
        if (costs.isEmpty()) return;

        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack != null) inputs.add(stack.copy());
        }

        boolean foundMatch = false;
        for (int idx = 0; idx < costs.size(); idx++) {
            if (base.paidUpgradeCostIndices.contains(idx)) continue;
            ItemStack cost = costs.get(idx);
            int remaining = cost.stackSize;
            for (ItemStack input : inputs) {
                if (GTUtility.areStacksEqual(cost, input)) {
                    remaining -= input.stackSize;
                }
            }
            if (remaining > 0) continue;

            foundMatch = true;
            remaining = cost.stackSize;
            for (int i = 0; i < handler.getSlots() && remaining > 0; i++) {
                ItemStack slot = handler.getStackInSlot(i);
                if (slot != null && GTUtility.areStacksEqual(cost, slot)) {
                    int take = Math.min(remaining, slot.stackSize);
                    slot.stackSize -= take;
                    remaining -= take;
                    if (slot.stackSize <= 0) handler.setStackInSlot(i, null);
                }
            }
            base.paidUpgradeCostIndices.add(idx);
            paidCostIndices.add(idx);
            break;
        }
        if (!foundMatch) return;
        onUpgradeComplete();
    }

    /** Called after items are consumed, delegates to the machine for upgrade logic. */
    protected void onUpgradeComplete() {
        ((GTNCSteamMultiBlockBase<?>) multiblock).onUpgradeComplete();
    }

    // #tr GTNC_gui_button_wireless_steam
    // # Toggle Wireless Steam Mode
    // # zh_CN 切换无线蒸汽模式

    protected ButtonWidget<?> createWirelessModeButton(PanelSyncManager syncManager) {
        BooleanSyncValue wirelessSyncer = syncManager.findSyncHandler("wirelessMode", BooleanSyncValue.class);
        return new ButtonWidget<>().size(16)
            .marginBottom(2)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .disableHoverBackground()
            .overlay(new DynamicDrawable(() -> {
                if (wirelessSyncer.getBoolValue()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_BATTERY_ON;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_BATTERY_OFF;
            }))
            .onMousePressed(d -> {
                wirelessSyncer.setBoolValue(!wirelessSyncer.getBoolValue());
                return true;
            })
            .tooltip(t -> t.addLine(StatCollector.translateToLocal("GTNC_gui_button_wireless_steam")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected IWidget createStructureUpdateButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createStructureUpdateButton(syncManager)).size(16)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.getStructureUpdateTime() > -20) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_STRUCTURE_CHECK;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_STRUCTURE_CHECK_OFF;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected ToggleButton createPowerSwitchButton() {
        return super.createPowerSwitchButton().size(16)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isAllowedToWork()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_ON;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_DISABLED;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    // ============================================================
    // Panel gap row buttons — celestial background
    // ============================================================

    @Override
    protected IWidget createVoidExcessButton(PanelSyncManager syncManager) {
        return ((ButtonWidget<?>) super.createVoidExcessButton(syncManager)).size(16)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected IWidget createInputSeparationButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createInputSeparationButton(syncManager)).size(16)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isInputSeparationEnabled()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_INPUT_SEPARATION;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_INPUT_SEPARATION_OFF;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected IWidget createBatchModeButton(PanelSyncManager syncManager) {
        return ((ToggleButton) super.createBatchModeButton(syncManager)).size(16)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isBatchModeEnabled()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_BATCH_MODE;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_BATCH_MODE_OFF;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected IWidget createLockToSingleRecipeButton(PanelSyncManager syncManager) {
        if (!usesLockToSingleRecipeButton()) {
            return new Widget<>();
        }
        return ((ToggleButton) super.createLockToSingleRecipeButton(syncManager)).size(16)
            .background(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .selectedBackground(GTNCGuiTextures.BUTTON_CELESTIAL_32x32)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isRecipeLockingEnabled()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_RECIPE_LOCKED;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_RECIPE_UNLOCKED;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    protected boolean usesLockToSingleRecipeButton() {
        return true;
    }

    // ============================================================
    // Terminal corner & logo
    // ============================================================

    @Override
    protected IWidget createModeSwitchButton(PanelSyncManager syncManager) {
        IWidget button = super.createModeSwitchButton(syncManager);
        for (int i = 0; i < 8; i++) {
            if (button instanceof com.cleanroommc.modularui.widgets.CycleButtonWidget w) {
                w.stateBackground(i, GTNCGuiTextures.BUTTON_CELESTIAL_32x32);
            }
        }
        return button;
    }

    @Override
    protected IWidget createPowerPanelButton(PanelSyncManager syncManager, ModularPanel parent) {
        return new Widget<>();
    }

    @Override
    protected Flow createTerminalRightCornerColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .rightRel(0, 6, 0)
            .bottomRel(0, 6, 0)
            .childIf(
                multiblock.supportsShutdownReasonHoverable(),
                () -> createShutdownReasonHoverableTerminal(syncManager))
            .childIf(
                multiblock.supportsMaintenanceIssueHoverable(),
                () -> createMaintIssueHoverableTerminal(syncManager))
            .child(makeLogoWidget(syncManager, panel));
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        BooleanSyncValue wirelessModeSyncer = new BooleanSyncValue(
            () -> ((GTNCSteamMultiBlockBase<?>) multiblock).wirelessMode,
            val -> ((GTNCSteamMultiBlockBase<?>) multiblock).wirelessMode = val).allowC2S();
        syncManager.syncValue("wirelessMode", wirelessModeSyncer);
    }

    @Override
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(GTNCGuiTextures.PICTURE_GODFORGE_LOGO).size(18)
            .marginTop(4);
    }
}
