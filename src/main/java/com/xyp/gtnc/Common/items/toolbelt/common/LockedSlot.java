package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class LockedSlot extends Slot {

    public LockedSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(net.minecraft.entity.player.EntityPlayer player) {
        return false;
    }
}
