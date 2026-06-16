package com.xyp.gtnc.Common.items.toolbelt.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import com.xyp.gtnc.Common.items.toolbelt.BeltFinder;

public class BeltSlotMenu extends Container {

    private final InventoryPlayer playerInventory;
    private final EntityPlayer player;
    private final BeltSlot slotBelt;
    private final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    private final IInventory craftResult = new InventoryCraftResult();

    public BeltSlotMenu(InventoryPlayer playerInventory) {
        this.playerInventory = playerInventory;
        this.player = playerInventory.player;

        // Crafting result
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, craftMatrix, craftResult, 0, 154, 28));

        // Crafting grid (2x2)
        for (int row = 0; row < 2; ++row) {
            for (int col = 0; col < 2; ++col) {
                this.addSlotToContainer(new Slot(craftMatrix, col + row * 2, 98 + col * 18, 18 + row * 18));
            }
        }

        // Armor slots
        for (int i = 0; i < 4; ++i) {
            final int armorSlot = 3 - i;
            this.addSlotToContainer(
                new SlotArmor(playerInventory.player, playerInventory, 36 + armorSlot, 8, 8 + i * 18, armorSlot));
        }

        // Main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col + (row + 1) * 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        // Belt slot
        BeltAttachment attachment = BeltAttachment.get(playerInventory.player);
        if (attachment == null) {
            BeltAttachment.register(playerInventory.player);
            attachment = BeltAttachment.get(playerInventory.player);
        }
        this.slotBelt = new BeltSlot(attachment, 77, 44);
        this.addSlotToContainer(slotBelt);

        // Update crafting result
        this.onCraftMatrixChanged(craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        this.craftResult.setInventorySlotContents(
            0,
            CraftingManager.getInstance()
                .findMatchingRecipe(this.craftMatrix, player.worldObj));
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        // Return crafting contents
        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
            ItemStack stack = this.craftMatrix.getStackInSlotOnClosing(i);
            if (stack != null) {
                player.dropPlayerItemWithRandomChoice(stack, false);
            }
        }

        // Sync belt slot state to clients
        if (!player.worldObj.isRemote) {
            BeltFinder.sendSync(player);
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

        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();

        if (index == 0) {
            // Crafting result
            if (!this.mergeItemStack(stack, 9, 45, true)) {
                return null;
            }
            slot.onSlotChange(stack, original);
        } else if (index >= 1 && index < 5) {
            // Crafting grid
            if (!this.mergeItemStack(stack, 9, 45, false)) {
                return null;
            }
        } else if (index >= 5 && index < 9) {
            // Armor
            if (!this.mergeItemStack(stack, 9, 45, false)) {
                return null;
            }
        } else if (index == slotBelt.slotNumber) {
            // Belt slot -> inventory
            if (!this.mergeItemStack(stack, 9, 45, false)) {
                return null;
            }
        } else if (stack.getItem() instanceof ItemArmor) {
            // Try armor slots
            ItemArmor armor = (ItemArmor) stack.getItem();
            int armorType = armor.armorType;
            int targetSlot = 8 - armorType;
            if (!this.inventorySlots.get(targetSlot)
                .getHasStack()) {
                if (!this.mergeItemStack(stack, targetSlot, targetSlot + 1, false)) {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(stack, 9, 45, false)) {
                    return null;
                }
            }
        } else if (slotBelt.isItemValid(stack)) {
            // Try belt slot
            if (!slotBelt.getHasStack()) {
                if (!this.mergeItemStack(stack, slotBelt.slotNumber, slotBelt.slotNumber + 1, false)) {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(stack, 9, 45, false)) {
                    return null;
                }
            }
        } else if (index >= 9 && index < 36) {
            // Main inventory -> hotbar
            if (!this.mergeItemStack(stack, 36, 45, false)) {
                return null;
            }
        } else if (index >= 36 && index < 45) {
            // Hotbar -> main inventory
            if (!this.mergeItemStack(stack, 9, 36, false)) {
                return null;
            }
        } else {
            if (!this.mergeItemStack(stack, 9, 45, false)) {
                return null;
            }
        }

        if (stack.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }

        return original;
    }

    @Override
    public boolean func_94530_a(ItemStack stack, Slot slot) {
        return slot.inventory != this.craftResult && super.func_94530_a(stack, slot);
    }

    /**
     * Custom armor slot for proper positioning.
     */
    private static class SlotArmor extends Slot {

        private final int armorType;

        public SlotArmor(EntityPlayer player, IInventory inventory, int index, int x, int y, int armorType) {
            super(inventory, index, x, y);
            this.armorType = armorType;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack == null) return false;
            return stack.getItem() instanceof ItemArmor && ((ItemArmor) stack.getItem()).armorType == armorType;
        }
    }
}
