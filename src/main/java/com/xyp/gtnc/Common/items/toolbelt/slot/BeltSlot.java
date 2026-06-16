package com.xyp.gtnc.Common.items.toolbelt.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot for the belt item in the BeltSlotMenu.
 */
public class BeltSlot extends Slot {

    private static IInventory emptyInventory = new IInventory() {

        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return null;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            return null;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {}

        @Override
        public String getInventoryName() {
            return "";
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public int getInventoryStackLimit() {
            return 0;
        }

        @Override
        public void markDirty() {}

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return false;
        }

        @Override
        public void openInventory() {}

        @Override
        public void closeInventory() {}

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return false;
        }
    };

    private final BeltAttachment attachment;

    public BeltSlot(BeltAttachment attachment, int x, int y) {
        super(emptyInventory, 0, x, y);
        this.attachment = attachment;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        return attachment.canEquip(stack);
    }

    @Override
    public ItemStack getStack() {
        ItemStack contents = attachment.getContents();
        return contents != null ? contents : null;
    }

    @Override
    public void putStack(ItemStack stack) {
        attachment.setContents(stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack oldStack, ItemStack newStack) {}

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack stack = attachment.getContents();
        if (stack == null) return null;

        int available = Math.min(stack.stackSize, amount);

        if (available <= 0) return null;

        ItemStack split = stack.copy();
        split.stackSize = available;

        if (stack.stackSize - available <= 0) {
            attachment.setContents(null);
        } else {
            stack.stackSize -= available;
            attachment.setContents(stack);
        }

        this.onSlotChanged();
        return split;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return attachment.canUnequip();
    }

    public BeltAttachment getBeltAttachment() {
        return attachment;
    }
}
