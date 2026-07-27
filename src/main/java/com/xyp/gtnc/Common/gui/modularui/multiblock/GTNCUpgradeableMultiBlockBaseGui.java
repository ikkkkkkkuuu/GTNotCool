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
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.util.GTUtility;
import gregtech.api.util.StringUtils;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

/**
 * Optional chip-upgrade GUI layer.
 *
 * <p>
 * Machines which only need the modern GTNC shell should extend {@link GTNCModernMultiBlockBaseGui} directly. Ordinary
 * and steam machine GUI bases extend this class and supply their own machine-side upgrade storage through the hooks
 * below.
 */
public abstract class GTNCUpgradeableMultiBlockBaseGui<T extends MTEMultiBlockBase>
    extends GTNCModernMultiBlockBaseGui<T> {

    protected final Set<Integer> paidCostIndices = new HashSet<>();

    protected GTNCUpgradeableMultiBlockBaseGui(T multiblock) {
        super(multiblock);
    }

    protected abstract List<ItemStack> getUpgradeCosts();

    protected abstract Set<Integer> getMachinePaidUpgradeCostIndices();

    protected abstract boolean isUpgradeTreeSupported();

    protected abstract void onUpgradeComplete();

    protected ButtonWidget<?> createUpgradeTreeButton(ModularPanel panel, PanelSyncManager syncManager) {
        IPanelHandler upgradePanel = syncManager
            .syncedPanel("upgradeTreePanel", true, (sm, sh) -> createUpgradeTreePanel(sm, panel));
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .marginBottom(2)
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
        return applyModernStateButton(button, upgradePanel::isPanelOpen, this::isUpgradeTreeSupported);
    }

    protected ModularPanel createUpgradeTreePanel(PanelSyncManager syncManager, ModularPanel parent) {
        ItemStackHandler handler = new ItemStackHandler(16);
        syncManager.registerSlotGroup("upgradeTreeInput", 16);

        ModularPanel panel = new ModularPanel("upgradeTreePanel").relative(parent)
            .leftRelOffset(0, 4)
            .topRelOffset(0, 3)
            .size(190, 115)
            .background(GTNCGuiTextures.MODERN_VAULT_PANEL_BORDER)
            .disableHoverBackground()
            .child(applyModernButton(ButtonWidget.panelCloseButton(), () -> true));

        panel.child(
            IKey.str("Pay Upgrade Costs")
                .style(EnumChatFormatting.GRAY)
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

        List<ItemStack> costs = getUpgradeCosts();
        paidCostIndices.clear();
        paidCostIndices.addAll(getMachinePaidUpgradeCostIndices());
        syncManager.syncValue("paidBits", new IntSyncValue(() -> {
            int bits = 0;
            for (int index : getMachinePaidUpgradeCostIndices()) {
                if (index < 12) bits |= 1 << index;
            }
            return bits;
        }, bits -> {
            paidCostIndices.clear();
            for (int i = 0; i < 12; i++) {
                if ((bits & 1 << i) != 0) paidCostIndices.add(i);
            }
        }));

        mainRow.child(buildCostColumn(costs, 0));
        mainRow.child(buildCostColumn(costs, 4));
        mainRow.child(buildCostColumn(costs, 8));
        mainRow.child(buildUpgradeSlotGrid(handler, "upgradeTreeInput"));
        panel.child(mainRow);

        InteractionSyncHandler upgradeSync = new InteractionSyncHandler().setOnMousePressed(mouseData -> {
            if (!mouseData.isClient()) {
                performUpgrade(handler);
            }
        });
        ButtonWidget<?> consumeButton = new ButtonWidget<>().syncHandler(upgradeSync)
            .overlay(
                IKey.str("Consume Upgrade Materials")
                    .style(EnumChatFormatting.LIGHT_PURPLE)
                    .alignment(Alignment.CENTER)
                    .scale(0.75f))
            .disableHoverOverlay()
            .size(180, 18)
            .bottomRel(0)
            .leftRel(0)
            .marginBottom(5)
            .marginLeft(5);
        panel.child(applyModernButton(consumeButton, this::isUpgradeTreeSupported));
        return panel;
    }

    protected IWidget buildCostColumn(List<ItemStack> costs, int start) {
        Flow column = Flow.column()
            .size(36, 72);
        for (int i = 0; i < 4; i++) {
            column.child(buildCostRow(costs, start + i));
        }
        return column;
    }

    protected Flow buildCostRow(List<ItemStack> costs, int index) {
        return Flow.row()
            .size(36, 18)
            .collapseDisabledChild()
            .child(
                GTNCGuiTextures.MODERN_BUTTON_DISABLED.asWidget()
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
                .background(GTNCGuiTextures.MODERN_VAULT_ITEM_SLOT)
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

    protected SlotGroupWidget buildUpgradeSlotGrid(ItemStackHandler handler, String group) {
        String[] matrix = new String[4];
        String repeat = StringUtils.getRepetitionOf('s', 4);
        Arrays.fill(matrix, repeat);
        return SlotGroupWidget.builder()
            .matrix(matrix)
            .key(
                's',
                i -> new ItemSlot().slot(new ModularSlot(handler, i).slotGroup(group))
                    .background(GTNCGuiTextures.MODERN_VAULT_ITEM_SLOT))
            .build()
            .rightRel(0);
    }

    protected void performUpgrade(ItemStackHandler handler) {
        List<ItemStack> costs = getUpgradeCosts();
        if (costs.isEmpty()) return;

        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack != null) inputs.add(stack.copy());
        }

        boolean foundMatch = false;
        Set<Integer> paidIndices = getMachinePaidUpgradeCostIndices();
        for (int index = 0; index < costs.size(); index++) {
            if (paidIndices.contains(index)) continue;
            ItemStack cost = costs.get(index);
            int remaining = cost.stackSize;
            for (ItemStack input : inputs) {
                if (GTUtility.areStacksEqual(cost, input)) {
                    remaining -= input.stackSize;
                    if (remaining <= 0) break;
                }
            }
            if (remaining > 0) continue;

            foundMatch = true;
            remaining = cost.stackSize;
            for (int slotIndex = 0; slotIndex < handler.getSlots() && remaining > 0; slotIndex++) {
                ItemStack slot = handler.getStackInSlot(slotIndex);
                if (slot != null && GTUtility.areStacksEqual(cost, slot)) {
                    int take = Math.min(remaining, slot.stackSize);
                    slot.stackSize -= take;
                    remaining -= take;
                    if (slot.stackSize <= 0) handler.setStackInSlot(slotIndex, null);
                }
            }
            paidIndices.add(index);
            paidCostIndices.add(index);
            break;
        }
        if (foundMatch) {
            onUpgradeComplete();
        }
    }
}
