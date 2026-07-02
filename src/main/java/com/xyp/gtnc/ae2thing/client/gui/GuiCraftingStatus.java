package com.xyp.gtnc.ae2thing.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import com.xyp.gtnc.ae2thing.inventory.InventoryHandler;
import com.xyp.gtnc.ae2thing.inventory.gui.GuiType;
import com.xyp.gtnc.ae2thing.inventory.item.WirelessDualInterfaceTerminalInventory;
import com.xyp.gtnc.ae2thing.loader.ItemAndBlockHolder;
import com.xyp.gtnc.ae2thing.util.Ae2ReflectClient;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.widgets.GuiTabButton;

public class GuiCraftingStatus extends appeng.client.gui.implementations.GuiCraftingStatus {

    private GuiTabButton originalGuiBtn;
    private final ITerminalHost host;

    public GuiCraftingStatus(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
        host = te;
    }

    @Override
    public void initGui() {
        if (host instanceof WirelessDualInterfaceTerminalInventory) {
            Ae2ReflectClient.rewriteIcon(this, ItemAndBlockHolder.ITEM_WIRELESS_DUAL_INTERFACE_TERMINAL.stack());
        }
        super.initGui();
        originalGuiBtn = Ae2ReflectClient.getOriginalGuiButton(this);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) {
        if (btn == originalGuiBtn) {
            if (host instanceof WirelessDualInterfaceTerminalInventory) {
                InventoryHandler.switchGui(GuiType.WIRELESS_DUAL_INTERFACE_TERMINAL);
            }
        } else {
            super.actionPerformed(btn);
        }
    }
}
