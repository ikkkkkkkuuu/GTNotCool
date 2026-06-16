package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.BeltFinder;
import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;

/**
 * Container for the belt's internal inventory GUI.
 * When a belt is in the player's hand/inventory, this container provides slots
 * for the belt's internal inventory backed by the belt item's NBT.
 */
public class BeltContainer extends Container {

    public static final int SLOT_SIZE = 18;
    public static final int SLOTS_PER_ROW = 9;

    public static final int PLAYER_INV_LEFT = 8;
    public static final int PLAYER_INV_TOP = 51;
    public static final int HOTBAR_TOP = PLAYER_INV_TOP + 3 * SLOT_SIZE + 4;

    private final InventoryPlayer playerInventory;
    private final int blockedSlot;
    private ItemStack blockedStack;
    public int inventorySize = 0;
    private ItemStack beltStack;
    private final BeltInventory beltInventory;

    public BeltContainer(InventoryPlayer playerInventory, int blockedSlot, ItemStack blockedStack) {
        this.playerInventory = playerInventory;
        this.blockedSlot = blockedSlot;
        this.blockedStack = blockedStack;

        this.beltStack = playerInventory.getStackInSlot(blockedSlot);
        if (beltStack != null && beltStack.getItem() instanceof ToolBeltItem) {
            this.inventorySize = ToolBeltItem.getBeltSize(beltStack);
            this.beltInventory = new BeltInventory(beltStack);
        } else {
            this.inventorySize = 0;
            this.beltInventory = new BeltInventory(null);
        }

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
                if (index == blockedSlot) {
                    this.addSlotToContainer(new LockedSlot(playerInventory, index, x, y));
                } else {
                    this.addSlotToContainer(new Slot(playerInventory, index, x, y));
                }
            }
        }

        // Hotbar
        for (int col = 0; col < SLOTS_PER_ROW; ++col) {
            int x = PLAYER_INV_LEFT + col * SLOT_SIZE;
            if (col == blockedSlot) {
                this.addSlotToContainer(new LockedSlot(playerInventory, col, x, HOTBAR_TOP));
            } else {
                this.addSlotToContainer(new Slot(playerInventory, col, x, HOTBAR_TOP));
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote) {
            // Update the blockedStack reference to match the current belt state
            this.blockedStack = playerInventory.getStackInSlot(blockedSlot);
            // Force sync belt item NBT to client
            if (blockedStack != null) {
                playerInventory.markDirty();
            }
            // Sync belt slot to tracking players
            BeltFinder.sendSync(player);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (inventorySize <= 0) return false;
        // Check that the slot still contains a ToolBeltItem (allow any belt, not just the original)
        ItemStack actualStack = player.inventory.getStackInSlot(blockedSlot);
        if (actualStack == null) return false;
        if (!(actualStack.getItem() instanceof ToolBeltItem)) return false;
        // Update beltStack reference if it changed (e.g., NBT was modified externally)
        if (beltStack != actualStack) {
            beltStack = actualStack;
            blockedStack = actualStack;
            beltInventory.updateBeltStack(actualStack);
            inventorySize = ToolBeltItem.getBeltSize(actualStack);
        }
        return true;
    }

    @Override
    public void detectAndSendChanges() {
        // Update blockedStack to keep reference in sync with current belt NBT
        ItemStack currentStack = playerInventory.getStackInSlot(blockedSlot);
        if (currentStack != null && currentStack.getItem() instanceof ToolBeltItem) {
            if (currentStack != beltStack) {
                beltStack = currentStack;
                blockedStack = currentStack;
                beltInventory.updateBeltStack(currentStack);
                inventorySize = ToolBeltItem.getBeltSize(currentStack);
            } else if (blockedStack == null || !ItemStack.areItemStacksEqual(blockedStack, currentStack)) {
                // NBT changed (items added/removed), update reference
                blockedStack = currentStack;
            }
        }
        super.detectAndSendChanges();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot == null || !slot.getHasStack() || index == blockedSlot) return null;

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
     * Inner inventory that wraps the belt's NBT inventory.
     * All modifications go directly to the belt ItemStack's NBT.
     */
    private static class BeltInventory implements IInventory {

        private ItemStack beltStack;

        public BeltInventory(ItemStack beltStack) {
            this.beltStack = beltStack;
        }

        /**
         * Update the belt stack reference (called when the original stack changes).
         */
        public void updateBeltStack(ItemStack newBeltStack) {
            this.beltStack = newBeltStack;
        }

        @Override
        public int getSizeInventory() {
            return beltStack != null ? ToolBeltItem.getBeltSize(beltStack) : 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemStack stack = ToolBeltItem.getBeltSlot(beltStack, slot);
            return stack != null ? stack : null;
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount) {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null) {
                ItemStack split;
                if (stack.stackSize <= amount) {
                    split = stack;
                    ToolBeltItem.setBeltSlot(beltStack, slot, null);
                } else {
                    split = stack.splitStack(amount);
                    ToolBeltItem.setBeltSlot(beltStack, slot, stack);
                }
                this.markDirty();
                return split;
            }
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            ItemStack stack = getStackInSlot(slot);
            ToolBeltItem.setBeltSlot(beltStack, slot, null);
            this.markDirty();
            return stack;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            ToolBeltItem.setBeltSlot(beltStack, slot, stack);
            this.markDirty();
        }

        @Override
        public String getInventoryName() {
            return "container.belt";
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
        public void markDirty() {
            // Belt NBT modifications happen directly on the ItemStack,
            // no separate dirty flag needed. But we ensure the belt stack
            // is valid after modifications.
        }

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
