package com.xyp.gtnc.Common.gui.modularui.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;

public class GTNCSteamMultiBlockBaseGui extends GTNCUpgradeableMultiBlockBaseGui<GTNCSteamMultiBlockBase<?>> {

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
            .childIf(isUpgradeTreeSupported(), () -> createUpgradeTreeButton(panel, syncManager))
            .child(createWirelessModeButton(syncManager))
            .child(createStructureUpdateButton(syncManager));
    }

    @Override
    protected List<ItemStack> getUpgradeCosts() {
        return multiblock.getUpgradeCosts();
    }

    @Override
    protected Set<Integer> getMachinePaidUpgradeCostIndices() {
        return multiblock.paidUpgradeCostIndices;
    }

    @Override
    protected boolean isUpgradeTreeSupported() {
        return multiblock.supportsUpgradeTree();
    }

    @Override
    protected void onUpgradeComplete() {
        multiblock.onUpgradeComplete();
    }

    // #tr GTNC_gui_button_wireless_steam
    // # Toggle Wireless Steam Mode
    // # zh_CN 切换无线蒸汽模式
    protected ButtonWidget<?> createWirelessModeButton(PanelSyncManager syncManager) {
        BooleanSyncValue wirelessSyncer = syncManager.findSyncHandler("wirelessMode", BooleanSyncValue.class);
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .marginBottom(2)
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
        return applyModernStateButton(button, wirelessSyncer::getBoolValue, () -> true);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        BooleanSyncValue wirelessModeSyncer = new BooleanSyncValue(
            () -> multiblock.wirelessMode,
            val -> multiblock.wirelessMode = val).allowC2S();
        syncManager.syncValue("wirelessMode", wirelessModeSyncer);
    }
}
