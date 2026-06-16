package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;
import com.xyp.gtnc.Loader.ItemsLoader;

/**
 * Custom recipe to upgrade a Tool Belt by surrounding it with leather.
 * Preserves the belt's NBT data (stored items) during the upgrade.
 */
public class BeltUpgradeRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int beltCount = 0;
        int leatherCount = 0;
        int otherCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                if (stack.getItem() == ItemsLoader.toolBelt) {
                    beltCount++;
                } else if (stack.getItem() == Items.leather) {
                    leatherCount++;
                } else {
                    otherCount++;
                }
            }
        }

        // Exactly 1 belt and 8 leather, no other items
        return beltCount == 1 && leatherCount == 8 && otherCount == 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack beltInput = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && stack.getItem() == ItemsLoader.toolBelt) {
                beltInput = stack;
                break;
            }
        }

        if (beltInput == null) return null;

        int currentSize = ToolBeltItem.getBeltSize(beltInput);
        if (currentSize >= 9) return null; // Max size reached

        // Create new belt with size + 1, preserving existing items
        ItemStack result = new ItemStack(ItemsLoader.toolBelt);
        ToolBeltItem.setBeltSize(result, currentSize + 1);

        // Copy items from input belt to result belt
        for (int i = 0; i < currentSize; i++) {
            ItemStack slotItem = ToolBeltItem.getBeltSlot(beltInput, i);
            if (slotItem != null) {
                ToolBeltItem.setBeltSlot(result, i, slotItem.copy());
            }
        }

        return result;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        // Return a representative output for display purposes
        return ToolBeltItem.of(3);
    }
}
