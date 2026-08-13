package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DrawableStack;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamGodforgeProcessingModule;
import com.xyp.gtnc.utils.enums.mode.SteamGodforgeProcessingMode;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;

/**
 * GUI for the Steam Godforge processing module.
 *
 * <p>
 * Normal left/right clicks retain the original cyclic mode switching. Shift+left click opens a popup containing
 * every available processing mode. The button list is generated from SteamGodforgeProcessingMode and becomes scrollable
 * automatically.
 * </p>
 */
public final class SteamGodforgeProcessingModuleGui
    extends SteamGodforgeSmeltingModeModuleGui<SteamGodforgeProcessingModule> {

    private static final String MODE_SELECTOR_PANEL_SYNC_ID = "steamGodforgeProcessingModeSelectorPanel";
    private static final String MODE_SELECTOR_GUI_ID = "steam_godforge_processing_mode_selector";

    private static final int SELECTOR_COLUMNS = 5;
    private static final int SELECTOR_VISIBLE_ROWS = 5;
    private static final int MODE_BUTTON_SIZE = 28;
    private static final int MODE_ICON_SIZE = 16;
    private static final int SELECTOR_PADDING = 7;
    private static final int SELECTOR_HEADER_HEIGHT = 38;
    private static final int SELECTOR_WIDTH = SELECTOR_COLUMNS * MODE_BUTTON_SIZE + SELECTOR_PADDING * 2 + 4;

    public SteamGodforgeProcessingModuleGui(SteamGodforgeProcessingModule multiblock) {
        /*
         * 图标数组自动从统一模式定义表生成。
         * 以后新增模式不需要再修改 GUI 构造器。
         */
        super(multiblock, SteamGodforgeProcessingMode.createIconArray());
    }

    @Override
    protected IWidget createModeSwitchButton(PanelSyncManager syncManager) {
        IntSyncValue machineModeSyncer = syncManager.findSyncHandler("machineMode", IntSyncValue.class);
        IPanelHandler selectorPanel = syncManager.syncedPanel(
            MODE_SELECTOR_PANEL_SYNC_ID,
            true,
            (mainPanel, player) -> createModeSelectorPanel(machineModeSyncer));

        return new ButtonWidget<>().size(16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> SteamGodforgeProcessingMode.fromId(machineModeSyncer.getIntValue())
                        .getIcon()))
            .onMousePressed(mouseButton -> {
                if (mouseButton == 0 && Interactable.hasShiftDown()) {
                    if (selectorPanel.isPanelOpen()) {
                        selectorPanel.closePanel();
                    } else {
                        selectorPanel.openPanel();
                    }
                    return true;
                }

                int currentMode = machineModeSyncer.getIntValue();
                if (mouseButton == 0) {
                    machineModeSyncer.setIntValue(wrapMode(currentMode + 1));
                    return true;
                }
                if (mouseButton == 1) {
                    machineModeSyncer.setIntValue(wrapMode(currentMode - 1));
                    return true;
                }
                return false;
            })
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.lang("GT5U.gui.button.mode_switch"))
                    .addLine(IKey.dynamic(() -> GTUtility.getColoredSecondaryTooltip(multiblock.getMachineModeName())))
                    .addLine(TextLocalization.SteamGodforgeProcessingModeSelectorOpen))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private ModularPanel createModeSelectorPanel(IntSyncValue machineModeSyncer) {
        int modeCount = SteamGodforgeProcessingMode.count();
        int totalRows = (modeCount + SELECTOR_COLUMNS - 1) / SELECTOR_COLUMNS;
        int visibleRows = Math.max(1, Math.min(totalRows, SELECTOR_VISIBLE_ROWS));
        int selectorHeight = SELECTOR_HEADER_HEIGHT + visibleRows * MODE_BUTTON_SIZE + SELECTOR_PADDING;

        ModularPanel panel = GTGuis.createPopUpPanel(MODE_SELECTOR_GUI_ID)
            .size(SELECTOR_WIDTH, selectorHeight);

        panel.child(
            Flow.row()
                .coverChildren()
                .childPadding(4)
                .pos(SELECTOR_PADDING, 6)
                .child(
                    new ItemDrawable(multiblock.getStackForm(1)).asWidget()
                        .size(16))
                .child(
                    IKey.str(TextLocalization.SteamGodforgeProcessingModeSelectorTitle)
                        .alignment(Alignment.CenterLeft)
                        .scale(0.72f)
                        .asWidget()
                        .width(SELECTOR_WIDTH - SELECTOR_PADDING * 2 - 20)
                        .height(16)));

        panel.child(
            IKey.dynamic(
                () -> StatCollector.translateToLocal("GT5U.gui.select.current") + ": "
                    + StatCollector.translateToLocal(multiblock.getMachineModeKey(machineModeSyncer.getIntValue())))
                .alignment(Alignment.CENTER)
                .scale(0.75f)
                .asWidget()
                .width(SELECTOR_WIDTH - SELECTOR_PADDING * 2)
                .left(SELECTOR_PADDING)
                .top(24));

        panel.child(
            new Grid().minColWidth(MODE_BUTTON_SIZE)
                .gridOfWidthHeight(SELECTOR_COLUMNS, totalRows, (x, y, index) -> {
                    if (index >= modeCount) {
                        return null;
                    }
                    return createModeButton(panel, machineModeSyncer, index);
                })
                .size(SELECTOR_COLUMNS * MODE_BUTTON_SIZE + 4, visibleRows * MODE_BUTTON_SIZE)
                .scrollable()
                .horizontalCenter()
                .top(SELECTOR_HEADER_HEIGHT));

        return panel;
    }

    private IWidget createModeButton(ModularPanel panel, IntSyncValue machineModeSyncer, int mode) {
        if (mode < 0 || mode >= SteamGodforgeProcessingMode.count()) {
            return null;
        }

        return new ButtonWidget<>().size(MODE_BUTTON_SIZE)
            .background(
                new DynamicDrawable(
                    () -> machineModeSyncer.getIntValue() == mode
                        ? new DrawableStack(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32, GTGuiTextures.SLOT_OUTLINE_GREEN)
                        : GTGuiTextures.TT_BUTTON_CELESTIAL_32x32))
            .child(
                SteamGodforgeProcessingMode.fromId(mode)
                    .getIcon()
                    .asWidget()
                    .size(MODE_ICON_SIZE)
                    .pos((MODE_BUTTON_SIZE - MODE_ICON_SIZE) / 2, (MODE_BUTTON_SIZE - MODE_ICON_SIZE) / 2))
            .onMousePressed(mouseButton -> {
                if (mouseButton != 0) {
                    return false;
                }

                machineModeSyncer.setIntValue(mode);
                panel.closeIfOpen();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang(multiblock.getMachineModeKey(mode))))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private int wrapMode(int mode) {
        return SteamGodforgeProcessingMode.wrapId(mode);
    }
}
