package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltData;

/**
 * Container for the tool belt inventory GUI.
 * Reads/writes directly to player's ToolBeltData (10 fixed slots).
 */
public class BeltContainer extends Container {

    public static final int SLOT_SIZE = 18;
    public static final int SLOTS_PER_ROW = 9;

    public static final int PLAYER_INV_LEFT = 8;
    public static final int PLAYER_INV_TOP = 51;
    public static final int HOTBAR_TOP = PLAYER_INV_TOP + 3 * SLOT_SIZE + 4;

    private final InventoryPlayer playerInventory;
    public int inventorySize = ToolBeltData.SLOT_COUNT;
    private final ToolBeltInventory beltInventory;

    public BeltContainer(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;

        ToolBeltData data = ToolBeltData.get(playerInventory.player);
        if (data == null) {
            ToolBeltData.register(playerInventory.player);
            data = ToolBeltData.get(playerInventory.player);
        }
        this.beltInventory = new ToolBeltInventory(data);

        if (inventorySize > 0) {
            int xoff = ((9 - inventorySize) * SLOT_SIZE) / 2;
            for (int k = 0; k < inventorySize; ++k) {
                this.addSlotToContainer(new Slot(beltInventory, k, PLAYER_INV_LEFT + xoff + k * SLOT_SIZE, 20) {

                    @Override
                    public boolean isItemValid(ItemStack stack) {
                        return ConfigData.isItemStackAllowed(stack);
                    }
                });
            }
        }

        bindPlayerInventory();
    }

    private void bindPlayerInventory() {
        // Main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < SLOTS_PER_ROW; ++col) {
                int index = col + (row + 1) * SLOTS_PER_ROW;
                int x = PLAYER_INV_LEFT + col * SLOT_SIZE;
                int y = row * SLOT_SIZE + PLAYER_INV_TOP;
                this.addSlotToContainer(new Slot(playerInventory, index, x, y));
            }
        }

        // Hotbar
        for (int col = 0; col < SLOTS_PER_ROW; ++col) {
            int x = PLAYER_INV_LEFT + col * SLOT_SIZE;
            this.addSlotToContainer(new Slot(playerInventory, col, x, HOTBAR_TOP));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote) {
            ToolBeltData data = ToolBeltData.get(player);
            if (data != null) {
                data.syncToTracking();
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack()) return null;

        ItemStack containedStack = slot.getStack();
        ItemStack originalStack = containedStack.copy();

        if (index < inventorySize) {
            // From belt to player inventory
            if (!this.mergeItemStack(containedStack, inventorySize, inventorySize + 36, true)) {
                return null;
            }
        } else {
            // From player inventory to belt
            if (!this.mergeItemStack(containedStack, 0, inventorySize, false)) {
                return null;
            }
        }

        if (containedStack.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }

        return originalStack;
    }

    /**
     * Inner inventory that wraps ToolBeltData.
     */
    private static class ToolBeltInventory implements IInventory {

        private final ToolBeltData data;

        public ToolBeltInventory(ToolBeltData data) {
            this.data = data;
        }

        @Override
        public int getSizeInventory() {
            return ToolBeltData.SLOT_COUNT;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (data == null) return null;
            return data.getStackInSlot(slot);
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            if (data == null) return null;
            ItemStack stack = data.getStackInSlot(slot);
            if (stack != null) {
                ItemStack split;
                if (stack.stackSize <= amount) {
                    split = stack;
                    data.setStackInSlot(slot, null);
                } else {
                    split = stack.splitStack(amount);
                    data.setStackInSlot(slot, stack);
                }
                this.markDirty();
                return split;
            }
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            if (data == null) return null;
            ItemStack stack = data.getStackInSlot(slot);
            data.setStackInSlot(slot, null);
            this.markDirty();
            return stack;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            if (data == null) return;
            data.setStackInSlot(slot, stack);
            this.markDirty();
        }

        @Override
        public String getInventoryName() {
            return "container.toolbelt";
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {}

        @Override
        public boolean isUseableByPlayer(EntityPlayer player) {
            return true;
        }

        @Override
        public void openInventory() {}

        @Override
        public void closeInventory() {}

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            return ConfigData.isItemStackAllowed(stack);
        }
    }
}
