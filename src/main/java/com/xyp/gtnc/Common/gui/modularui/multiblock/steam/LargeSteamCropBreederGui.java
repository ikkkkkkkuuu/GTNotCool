package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import com.xyp.gtnc.Common.gui.modularui.multiblock.BaseGui.GTNCSteamMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamCropBreeder;

public class LargeSteamCropBreederGui extends GTNCSteamMultiBlockBaseGui {

    public LargeSteamCropBreederGui(LargeSteamCropBreeder multiblock) {
        super(multiblock);
    }

    @Override
    protected boolean shouldDisplayInputSeparation() {
        return false;
    }

    @Override
    protected boolean shouldDisplayBatchMode() {
        return false;
    }

    @Override
    protected boolean shouldDisplayRecipeLock() {
        return false;
    }
}
