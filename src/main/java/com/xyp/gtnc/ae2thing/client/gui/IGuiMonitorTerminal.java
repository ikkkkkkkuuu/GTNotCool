package com.xyp.gtnc.ae2thing.client.gui;

import net.minecraft.item.ItemStack;

import com.xyp.gtnc.ae2thing.client.gui.widget.IGuiMonitor;
import com.xyp.gtnc.ae2thing.client.gui.widget.THGuiTextField;

public interface IGuiMonitorTerminal extends IGuiMonitor {

    void setPlayerInv(ItemStack is);

    THGuiTextField getSearchField();
}
