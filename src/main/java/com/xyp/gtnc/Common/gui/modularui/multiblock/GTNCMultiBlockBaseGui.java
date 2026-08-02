package com.xyp.gtnc.Common.gui.modularui.multiblock;

import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCMultiBlockBase;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

public class GTNCMultiBlockBaseGui<T extends MTEMultiBlockBase> extends GTNCUpgradeableMultiBlockBaseGui<T> {

    public GTNCMultiBlockBaseGui(T multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(com.cleanroommc.modularui.utils.Alignment.MainAxis.END)
            .reverseLayout(true)
            .child(createPowerSwitchButton())
            .childIf(isUpgradeTreeSupported(), () -> createUpgradeTreeButton(panel, syncManager))
            .child(createStructureUpdateButton(syncManager));
    }

    private GTNCMultiBlockBase<?> upgradeMachine() {
        return (GTNCMultiBlockBase<?>) multiblock;
    }

    @Override
    protected List<ItemStack> getUpgradeCosts() {
        return upgradeMachine().getUpgradeCosts();
    }

    @Override
    protected Set<Integer> getMachinePaidUpgradeCostIndices() {
        return upgradeMachine().getPaidUpgradeCostIndices();
    }

    @Override
    protected boolean isUpgradeTreeSupported() {
        return upgradeMachine().supportsUpgradeTree();
    }

    @Override
    protected boolean tryApplyUpgrade(ItemStackHandler inputs) {
        return upgradeMachine().tryApplyUpgrade(inputs);
    }

    @Override
    protected int getUpgradeTier() {
        return upgradeMachine().getUpgradeTierForGui();
    }

    @Override
    protected int getUpgradeSpeedPercent() {
        return upgradeMachine().getUpgradeSpeedPercentForGui();
    }

    @Override
    protected int getUpgradeParallel() {
        return upgradeMachine().getUpgradeParallelForGui();
    }
}
