package com.xyp.gtnc.Common.gui.modularui.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.function.BooleanSupplier;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/**
 * Shared modern visual layer for GTNC multiblock GUIs.
 *
 * <p>
 * Machine-specific state, synchronization and panels belong in subclasses. This class owns only the common shell,
 * inventory slots and standard button states so every GTNC GUI uses the same centralized texture registry.
 */
public class GTNCModernMultiBlockBaseGui<T extends MTEMultiBlockBase> extends MTEMultiBlockBaseGui<T> {

    public GTNCModernMultiBlockBaseGui(T multiblock) {
        super(multiblock);
    }

    @Override
    protected void initCustomIcons() {
        this.customIcons.put("power_switch_disabled", GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_DISABLED);
        this.customIcons.put("power_switch_on", GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_ON);
        this.customIcons.put("power_switch_off", GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_OFF);
    }

    protected ButtonWidget<?> applyModernButton(ButtonWidget<?> button, BooleanSupplier enabled) {
        button.background(
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        button.hoverBackground(
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON_HOVER
                    : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        return button;
    }

    protected ButtonWidget<?> applyModernStateButton(ButtonWidget<?> button, BooleanSupplier selected,
        BooleanSupplier enabled) {
        button.background(new DynamicDrawable(() -> {
            if (!enabled.getAsBoolean()) return GTNCGuiTextures.MODERN_BUTTON_DISABLED;
            return selected.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON_PRESSED : GTNCGuiTextures.MODERN_BUTTON;
        }));
        button.hoverBackground(
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON_HOVER
                    : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        return button;
    }

    protected ToggleButton applyModernToggleButton(ToggleButton button, BooleanSupplier enabled) {
        button.background(
            false,
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        button.background(
            true,
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON_PRESSED
                    : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        button.hoverBackground(
            false,
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON_HOVER
                    : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        button.hoverBackground(
            true,
            new DynamicDrawable(
                () -> enabled.getAsBoolean() ? GTNCGuiTextures.MODERN_BUTTON_HOVER
                    : GTNCGuiTextures.MODERN_BUTTON_DISABLED));
        return button;
    }

    @Override
    protected ModularPanel getBasePanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return super.getBasePanel(guiData, syncManager, uiSettings).background(GTNCGuiTextures.MODERN_BACKGROUND);
    }

    @Override
    protected ParentWidget<?> createTerminalParentWidget(ModularPanel panel, PanelSyncManager syncManager) {
        return new ParentWidget<>().size(getTerminalWidgetWidth(), getTerminalWidgetHeight())
            .paddingTop(4)
            .paddingBottom(4)
            .paddingLeft(4)
            .paddingRight(0)
            .background(GTNCGuiTextures.MODERN_VAULT_PANEL_BORDER)
            .child(
                createTerminalTextWidget(syncManager, panel)
                    .size(getTerminalWidgetWidth() - 4, getTerminalWidgetHeight() - 8)
                    .collapseDisabledChild())
            .childIf(
                multiblock.supportsTerminalRightCornerColumn(),
                () -> createTerminalRightCornerColumn(panel, syncManager))
            .childIf(
                multiblock.supportsTerminalLeftCornerColumn(),
                () -> createTerminalLeftCornerColumn(panel, syncManager));
    }

    @Override
    protected IWidget createInventoryRow(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.row()
            .fullWidth()
            .height(76)
            .childIf(
                multiblock.doesBindPlayerInventory(),
                () -> SlotGroupWidget
                    .playerInventory((index, slot) -> slot.background(GTNCGuiTextures.MODERN_VAULT_ITEM_SLOT))
                    .marginLeft(4))
            .child(createButtonColumn(panel, syncManager));
    }

    @Override
    protected IWidget createStructureUpdateButton(PanelSyncManager syncManager) {
        ToggleButton button = ((ToggleButton) super.createStructureUpdateButton(syncManager)).size(16)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.getStructureUpdateTime() > -20) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_STRUCTURE_CHECK;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_STRUCTURE_CHECK_OFF;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernToggleButton(button, () -> true);
    }

    @Override
    protected ToggleButton createPowerSwitchButton() {
        ToggleButton button = super.createPowerSwitchButton().size(16)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isAllowedToWork()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_ON;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_DISABLED;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernToggleButton(button, () -> !isPowerSwitchDisabled());
    }

    @Override
    protected ToggleButton createMuffleButton() {
        return applyModernToggleButton(super.createMuffleButton(), () -> true);
    }

    @Override
    protected ButtonWidget<?> createVoidExcessButton(PanelSyncManager syncManager) {
        ButtonWidget<?> button = ((ButtonWidget<?>) super.createVoidExcessButton(syncManager)).size(16)
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernButton(button, multiblock::supportsVoidProtection);
    }

    @Override
    protected ToggleButton createInputSeparationButton(PanelSyncManager syncManager) {
        ToggleButton button = ((ToggleButton) super.createInputSeparationButton(syncManager)).size(16)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isInputSeparationEnabled()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_INPUT_SEPARATION;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_INPUT_SEPARATION_OFF;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernToggleButton(button, multiblock::supportsInputSeparation);
    }

    @Override
    protected ToggleButton createBatchModeButton(PanelSyncManager syncManager) {
        ToggleButton button = ((ToggleButton) super.createBatchModeButton(syncManager)).size(16)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isBatchModeEnabled()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_BATCH_MODE;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_BATCH_MODE_OFF;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernToggleButton(button, multiblock::supportsBatchMode);
    }

    @Override
    protected ToggleButton createLockToSingleRecipeButton(PanelSyncManager syncManager) {
        if (!usesLockToSingleRecipeButton()) {
            return new ToggleButton();
        }
        ToggleButton button = ((ToggleButton) super.createLockToSingleRecipeButton(syncManager)).size(16)
            .overlay(new DynamicDrawable(() -> {
                if (multiblock.isRecipeLockingEnabled()) {
                    return GTNCGuiTextures.OVERLAY_BUTTON_RECIPE_LOCKED;
                }
                return GTNCGuiTextures.OVERLAY_BUTTON_RECIPE_UNLOCKED;
            }))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernToggleButton(button, multiblock::supportsSingleRecipeLocking);
    }

    protected boolean usesLockToSingleRecipeButton() {
        return true;
    }

    @Override
    protected IWidget createModeSwitchButton(PanelSyncManager syncManager) {
        IWidget button = super.createModeSwitchButton(syncManager);
        for (int i = 0; i < 8; i++) {
            if (button instanceof CycleButtonWidget w) {
                w.stateBackground(i, GTNCGuiTextures.MODERN_BUTTON);
                w.stateHoverBackground(i, GTNCGuiTextures.MODERN_BUTTON_HOVER);
            }
        }
        return button;
    }

    @Override
    protected ButtonWidget<?> createPowerPanelButton(PanelSyncManager syncManager, ModularPanel parent) {
        return applyModernButton(super.createPowerPanelButton(syncManager, parent), () -> true);
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
    protected Widget<? extends Widget<?>> makeLogoWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return new IDrawable.DrawableWidget(GTNCGuiTextures.PICTURE_GODFORGE_LOGO).size(18)
            .marginTop(4);
    }
}
