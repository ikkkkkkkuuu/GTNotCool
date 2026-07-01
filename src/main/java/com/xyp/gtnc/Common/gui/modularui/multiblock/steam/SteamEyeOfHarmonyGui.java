package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.xyp.gtnc.Common.gui.modularui.multiblock.GTNCSteamMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.multiblock.steam.SteamEyeOfHarmony;

public class SteamEyeOfHarmonyGui extends GTNCSteamMultiBlockBaseGui {

    public SteamEyeOfHarmonyGui(SteamEyeOfHarmony multiblock) {
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
            .child(
                new ItemSlot().slot(new ModularSlot(multiblock.inventoryHandler, multiblock.getControllerSlotIndex()) {

                    @Override
                    public int getSlotStackLimit() {
                        return multiblock.getInventoryStackLimit();
                    }
                }.singletonSlotGroup()))
            .child(createPowerSwitchButton())
            .child(createUpgradeTreeButton(panel, syncManager))
            .child(createStructureUpdateButton(syncManager));
    }
}
