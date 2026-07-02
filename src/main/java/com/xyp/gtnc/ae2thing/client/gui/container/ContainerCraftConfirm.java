package com.xyp.gtnc.ae2thing.client.gui.container;

import java.util.Objects;

import net.minecraft.entity.player.InventoryPlayer;

import com.xyp.gtnc.ae2thing.common.parts.THPart;
import com.xyp.gtnc.ae2thing.inventory.InventoryHandler;
import com.xyp.gtnc.ae2thing.inventory.gui.GuiType;
import com.xyp.gtnc.ae2thing.inventory.item.WirelessDualInterfaceTerminalInventory;
import com.xyp.gtnc.ae2thing.inventory.item.WirelessTerminal;
import com.xyp.gtnc.ae2thing.util.BlockPos;

import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ITerminalHost;

public class ContainerCraftConfirm extends appeng.container.implementations.ContainerCraftConfirm {

    public ContainerCraftConfirm(final InventoryPlayer ip, final ITerminalHost te) {
        super(ip, te);
    }

    @Override
    public void switchToOriginalGUI() {
        GuiType originalGui = null;

        final IActionHost ah = this.getActionHost();
        if (ah instanceof WirelessDualInterfaceTerminalInventory) {
            originalGui = GuiType.WIRELESS_DUAL_INTERFACE_TERMINAL;
        }

        if (this.getOpenContext() != null && ah instanceof THPart) {
            InventoryHandler.openGui(
                this.getInventoryPlayer().player,
                getWorld(),
                new BlockPos(
                    this.getOpenContext()
                        .getTile()),
                Objects.requireNonNull(
                    this.getOpenContext()
                        .getSide()),
                originalGui);
        } else if (ah instanceof WirelessTerminal) {
            InventoryHandler.openGui(
                this.getInventoryPlayer().player,
                getWorld(),
                new BlockPos(((WirelessTerminal) ah).getInventorySlot(), 0, 0),
                Objects.requireNonNull(
                    this.getOpenContext()
                        .getSide()),
                originalGui);
        }
    }
}
