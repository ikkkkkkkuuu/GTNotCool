package com.xyp.gtnc.ae2thing.loader;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.ae2thing.api.AE2ThingAPI;
import com.xyp.gtnc.ae2thing.api.Constants;
import com.xyp.gtnc.ae2thing.api.adapter.pattern.FCPatternTerminal;
import com.xyp.gtnc.ae2thing.api.adapter.pattern.IRecipeHandler;
import com.xyp.gtnc.ae2thing.api.adapter.pattern.THDualInterfacePatternTerminal;
import com.xyp.gtnc.ae2thing.client.gui.container.ContainerWirelessDualInterfaceTerminal;

import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.implementations.ContainerPatternTermEx;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.util.Platform;

public class PatternTerminalMouseWheelLoader implements Runnable {

    @Override
    public void run() {
        IRecipeHandler handler = (container, inputs, outputs, identifier, adapter, message) -> {
            if (container instanceof IAEAppEngInventory inventory) {
                ItemStack in = (ItemStack) inputs.get(0)
                    .getStack();
                ItemStack out = (ItemStack) outputs.get(0)
                    .getStack();
                IInventory inv = adapter.getInventoryByName(container, adapter.getCraftingInvName());
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    if (Platform.isSameItemPrecise(inv.getStackInSlot(i), in)) {
                        inv.setInventorySlotContents(i, out);
                    }
                }
                container.onCraftMatrixChanged(inv);
                inventory.saveChanges();
            }
        };

        AE2ThingAPI.instance()
            .terminal()
            .registerPatternTerminal(new THDualInterfacePatternTerminal())
            .registerIdentifier(
                Constants.NEI_MOUSE_WHEEL,
                (container, inputs, outputs, identifier, adapter, message) -> {
                    if (container instanceof ContainerWirelessDualInterfaceTerminal c) {
                        ItemStack in = (ItemStack) inputs.get(0)
                            .getStack();
                        ItemStack out = (ItemStack) outputs.get(0)
                            .getStack();
                        IInventory inv = adapter.getInventoryByName(
                            container,
                            c.getContainer()
                                .getPatternTerminal()
                                .isCraftingRecipe() ? Constants.CRAFTING : Constants.CRAFTING_EX);
                        for (int i = 0; i < inv.getSizeInventory(); i++) {
                            if (Platform.isSameItemPrecise(inv.getStackInSlot(i), in)) {
                                inv.setInventorySlotContents(i, out);
                            }
                        }
                        container.onCraftMatrixChanged(inv);
                        c.saveChanges();
                    }
                });
        AE2ThingAPI.instance()
            .terminal()
            .registerPatternTerminal(new FCPatternTerminal(ContainerPatternTerm.class))
            .registerIdentifier(Constants.NEI_MOUSE_WHEEL, handler);
        AE2ThingAPI.instance()
            .terminal()
            .registerPatternTerminal(new FCPatternTerminal(ContainerPatternTermEx.class))
            .registerIdentifier(Constants.NEI_MOUSE_WHEEL, handler);
    }

}
