package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.util.GTUtility;

/** Server-side item transaction helpers shared by electric and steam upgrade trees. */
public final class UpgradeTreeHelper {

    public static final int MAX_CHIP_TIER = 10;

    private UpgradeTreeHelper() {}

    static boolean tryPayUpgradeCost(List<ItemStack> costs, Set<Integer> paidIndices, ItemStackHandler inputs) {
        if (costs == null || costs.isEmpty() || inputs == null) return false;

        for (int index = 0; index < costs.size(); index++) {
            if (paidIndices.contains(index)) continue;

            ItemStack cost = costs.get(index);
            if (cost == null || cost.stackSize <= 0 || countMatchingItems(inputs, cost) < cost.stackSize) continue;

            int remaining = cost.stackSize;
            for (int slot = 0; slot < inputs.getSlots() && remaining > 0; slot++) {
                ItemStack input = inputs.getStackInSlot(slot);
                if (input == null || !GTUtility.areStacksEqual(cost, input)) continue;

                int extracted = Math.min(remaining, input.stackSize);
                inputs.extractItem(slot, extracted, false);
                remaining -= extracted;
            }
            paidIndices.add(index);
            return true;
        }
        return false;
    }

    public static boolean isUpgradeCost(List<ItemStack> costs, ItemStack stack) {
        if (stack == null || costs == null) return false;
        for (ItemStack cost : costs) {
            if (cost != null && GTUtility.areStacksEqual(cost, stack)) return true;
        }
        return false;
    }

    /** Returns the registered High Computing Power Chip tier, or zero when the stack is not a chip. */
    public static int getChipTier(ItemStack stack) {
        if (stack == null) return 0;
        for (int tier = MAX_CHIP_TIER; tier >= 1; tier--) {
            GTNCItemList chip = GTNCItemList.valueOf("ChipTier" + tier);
            if (chip.hasBeenSet() && GTUtility.areStacksEqual(stack, chip.get(1))) {
                return tier;
            }
        }
        return 0;
    }

    private static int countMatchingItems(ItemStackHandler inputs, ItemStack cost) {
        int amount = 0;
        for (int slot = 0; slot < inputs.getSlots(); slot++) {
            ItemStack input = inputs.getStackInSlot(slot);
            if (input != null && GTUtility.areStacksEqual(cost, input)) {
                amount += input.stackSize;
                if (amount >= cost.stackSize) return amount;
            }
        }
        return amount;
    }
}
