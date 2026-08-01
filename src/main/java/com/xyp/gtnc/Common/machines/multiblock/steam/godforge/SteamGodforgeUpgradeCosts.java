package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;

/** Physical, LV-obtainable costs for the 31-node steam Godforge tree. */
public final class SteamGodforgeUpgradeCosts {

    private SteamGodforgeUpgradeCosts() {}

    public static List<ItemStack> get(ForgeOfGodsUpgrade upgrade) {
        int ordinal = upgrade.ordinal();
        if (ordinal == 0) return Collections.emptyList();

        int band = Math.min(8, (ordinal + 3) / 4);
        int amount = Math.min(64, 2 + ordinal * 2);
        List<ItemStack> cost = new ArrayList<>(3);

        switch (band) {
            case 1 -> {
                cost.add(unified(OrePrefixes.plate, amount));
                cost.add(unified(OrePrefixes.gearGtSmall, Math.max(1, amount / 2)));
            }
            case 2 -> {
                cost.add(unified(OrePrefixes.plateDouble, amount));
                cost.add(unified(OrePrefixes.gearGt, Math.max(1, amount / 3)));
            }
            case 3 -> {
                cost.add(unified(OrePrefixes.rotor, Math.max(1, amount / 3)));
                cost.add(unified(OrePrefixes.pipeMedium, amount));
            }
            case 4 -> {
                cost.add(ItemList.Casing_BronzePlatedBricks.get(amount));
                cost.add(unified(OrePrefixes.gearGt, Math.max(1, amount / 2)));
            }
            case 5 -> {
                cost.add(ItemList.Casing_Gearbox_Bronze.get(amount));
                cost.add(ItemList.Casing_Pipe_Bronze.get(amount));
            }
            case 6 -> {
                cost.add(ItemList.Casing_Firebox_Bronze.get(amount));
                cost.add(unified(OrePrefixes.frameGt, Math.max(1, amount / 2)));
            }
            case 7 -> {
                cost.add(ItemList.Block_BronzePlate.get(Math.max(1, amount / 4)));
                cost.add(unified(OrePrefixes.rotor, amount));
                cost.add(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, Math.max(1, amount / 8)));
            }
            default -> {
                cost.add(ItemList.Block_BronzePlate.get(Math.max(1, amount / 2)));
                cost.add(ItemList.Casing_Gearbox_Bronze.get(amount));
                cost.add(ItemList.Casing_Firebox_Bronze.get(amount));
            }
        }
        cost.removeIf(stack -> stack == null || stack.stackSize <= 0);
        return Collections.unmodifiableList(cost);
    }

    public static int requiredMilestoneLevel(ForgeOfGodsUpgrade upgrade) {
        return Math.min(7, upgrade.ordinal() / 4);
    }

    private static ItemStack unified(OrePrefixes prefix, int amount) {
        return GTOreDictUnificator.get(prefix, Materials.Bronze, amount);
    }
}
