package com.xyp.gtnc.Common.gui.modularui.multiblock.BaseGui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCAdvancedSteamMultiBlockBase;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.lang.TextLocalization;

/**
 * Shared GUI for the clean GTNC steam family.
 *
 * <p>
 * The legacy chip-upgrade panel is an optional capability: it is exposed only for machines that still inherit
 * {@link GTNCAdvancedSteamMultiBlockBase}. Clean steam machines, including the Steam Elevator family, keep the modern
 * shell and wireless-steam controls without receiving the legacy upgrade tree.
 */
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

    private GTNCAdvancedSteamMultiBlockBase<?> advancedSteamMachine() {
        return multiblock instanceof GTNCAdvancedSteamMultiBlockBase<?>advanced ? advanced : null;
    }

    @Override
    protected List<ItemStack> getUpgradeCosts() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced == null ? Collections.emptyList() : advanced.getUpgradeCosts();
    }

    @Override
    protected Set<Integer> getMachinePaidUpgradeCostIndices() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced == null ? Collections.emptySet() : advanced.getPaidUpgradeCostIndices();
    }

    @Override
    protected boolean isUpgradeTreeSupported() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced != null && advanced.supportsUpgradeTree();
    }

    @Override
    protected boolean tryApplyUpgrade(ItemStackHandler inputs) {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced != null && advanced.tryApplyUpgrade(inputs);
    }

    @Override
    protected int getUpgradeTier() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced == null ? 0 : advanced.getUpgradeTierForGui();
    }

    @Override
    protected int getUpgradeSpeedPercent() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced == null ? 100 : advanced.getUpgradeSpeedPercentForGui();
    }

    @Override
    protected int getUpgradeParallel() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced == null ? 0 : advanced.getUpgradeParallelForGui();
    }

    private boolean isWirelessModeEnabled() {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        return advanced == null ? multiblock.wirelessMode : advanced.wirelessMode;
    }

    private void setWirelessModeEnabled(boolean value) {
        GTNCAdvancedSteamMultiBlockBase<?> advanced = advancedSteamMachine();
        if (advanced == null) {
            multiblock.wirelessMode = value;
        } else {
            advanced.wirelessMode = value;
        }
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
            new BooleanSyncValue(this::isWirelessModeEnabled, this::setWirelessModeEnabled).allowC2S());
    }
}
