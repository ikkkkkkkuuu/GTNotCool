package com.xyp.gtnc.Common.gui.modularui.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.lang.TextLocalization;

public class GTNCSteamMultiBlockBaseGui extends GTNCModernMultiBlockBaseGui<GTNCSteamMultiBlockBase<?>> {

    public GTNCSteamMultiBlockBaseGui(GTNCSteamMultiBlockBase<?> multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .child(createPowerSwitchButton())
            .child(createWirelessModeButton(syncManager))
            .child(createStructureUpdateButton(syncManager));
    }

    protected ButtonWidget<?> createWirelessModeButton(PanelSyncManager syncManager) {
        BooleanSyncValue wireless = syncManager.findSyncHandler("wirelessMode", BooleanSyncValue.class);
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .marginBottom(2)
            .overlay(
                new DynamicDrawable(
                    () -> wireless.getBoolValue() ? GTNCGuiTextures.OVERLAY_BUTTON_BATTERY_ON
                        : GTNCGuiTextures.OVERLAY_BUTTON_BATTERY_OFF))
            .onMousePressed(data -> {
                wireless.setBoolValue(!wireless.getBoolValue());
                return true;
            })
            .tooltip(t -> t.addLine(TextLocalization.GUI_WIRELESS_STEAM_BUTTON))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernStateButton(button, wireless::getBoolValue, () -> true);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            "wirelessMode",
            new BooleanSyncValue(() -> multiblock.wirelessMode, value -> multiblock.wirelessMode = value).allowC2S());
    }
}
