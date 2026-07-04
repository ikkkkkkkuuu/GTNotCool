package com.xyp.gtnc.ae2thing.loader;

import net.minecraft.inventory.IInventory;

import com.xyp.gtnc.ae2thing.api.AE2ThingAPI;
import com.xyp.gtnc.ae2thing.api.Constants;
import com.xyp.gtnc.ae2thing.api.adapter.pattern.THDualInterfacePatternTerminal;
import com.xyp.gtnc.ae2thing.client.gui.container.ContainerWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.inventory.IPatternTerminal;
import com.xyp.gtnc.ae2thing.nei.NEIUtils;

public class PatternTerminalLoader implements Runnable {

    @Override
    public void run() {
        AE2ThingAPI.instance()
            .terminal()
            .registerPatternTerminal(new THDualInterfacePatternTerminal())
            .registerIdentifier(Constants.NEI_DEFAULT, (container, inputs, outputs, identifier, adapter, message) -> {
                if (container instanceof ContainerWirelessDualInterfaceTerminal ciw) {
                    boolean combine = ciw.combine;
                    ciw.setCraftingMode(message.isCraft);
                    ciw.setCrafting(message.isCraft);
                    IPatternTerminal pt = ciw.getContainer()
                        .getPatternTerminal();
                    IInventory inputSlot = pt
                        .getInventoryByName(message.isCraft ? Constants.CRAFTING : Constants.CRAFTING_EX);
                    IInventory outputSlot = pt.getInventoryByName(Constants.OUTPUT_EX);
                    for (int i = 0; i < inputSlot.getSizeInventory(); i++) {
                        inputSlot.setInventorySlotContents(i, null);
                    }
                    for (int i = 0; i < outputSlot.getSizeInventory(); i++) {
                        outputSlot.setInventorySlotContents(i, null);
                    }
                    if (!message.isCraft) {
                        if (combine) {
                            inputs = NEIUtils.compress(inputs);
                            outputs = NEIUtils.compress(outputs);
                        }
                        inputs = NEIUtils.clearNull(inputs);
                        outputs = NEIUtils.clearNull(outputs);
                    }
                    adapter.transferPack(inputs, inputSlot);
                    adapter.transferPack(outputs, outputSlot);
                    ciw.onCraftMatrixChanged(inputSlot);
                    ciw.onCraftMatrixChanged(outputSlot);
                    ciw.saveChanges();
                }
            })
            // BlockRenderer6343 multiblock structure preview -> processing pattern (blocks in, renamed paper out).
            .registerIdentifier(Constants.NEI_BR, (container, inputs, outputs, identifier, adapter, message) -> {
                if (container instanceof ContainerWirelessDualInterfaceTerminal ciw) {
                    ciw.setCraftingMode(false);
                    ciw.setCrafting(false);
                    IPatternTerminal pt = ciw.getContainer()
                        .getPatternTerminal();
                    IInventory inputSlot = pt.getInventoryByName(Constants.CRAFTING_EX);
                    IInventory outputSlot = pt.getInventoryByName(Constants.OUTPUT_EX);
                    for (int i = 0; i < inputSlot.getSizeInventory(); i++) {
                        inputSlot.setInventorySlotContents(i, null);
                    }
                    for (int i = 0; i < outputSlot.getSizeInventory(); i++) {
                        outputSlot.setInventorySlotContents(i, null);
                    }
                    inputs = NEIUtils.clearNull(inputs);
                    outputs = NEIUtils.clearNull(outputs);
                    adapter.transferPack(inputs, inputSlot);
                    adapter.transferPack(outputs, outputSlot);
                    ciw.onCraftMatrixChanged(inputSlot);
                    ciw.onCraftMatrixChanged(outputSlot);
                    ciw.saveChanges();
                }
            });
    }
}
