package com.xyp.gtnc.ae2thing.api.adapter.pattern;

import net.minecraft.inventory.Container;

import com.xyp.gtnc.ae2thing.api.Constants;
import com.xyp.gtnc.ae2thing.client.gui.container.ContainerWirelessDualInterfaceTerminal;

public class THDualInterfacePatternTerminal implements IPatternTerminalAdapter {

    @Override
    public boolean supportFluid() {
        return true;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerWirelessDualInterfaceTerminal.class;
    }

    @Override
    public String getOutputInvName() {
        return Constants.OUTPUT_EX;
    }

    @Override
    public String getCraftingInvName() {
        return Constants.CRAFTING_EX;
    }
}
