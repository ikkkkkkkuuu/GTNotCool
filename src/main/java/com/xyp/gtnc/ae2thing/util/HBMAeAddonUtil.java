package com.xyp.gtnc.ae2thing.util;

import net.minecraft.item.ItemStack;

import appeng.api.storage.data.IAEFluidStack;

/**
 * Stub: the HBM AE Addon integration is not present in this environment. All callers are guarded by
 * {@code Mods.HBM_AE_ADDON.isModLoaded()} (always false here), so these methods are never invoked at runtime.
 */
public class HBMAeAddonUtil {

    public static boolean getItemHasFluidType(ItemStack is) {
        return false;
    }

    public static boolean getItemIsEmptyContainer(ItemStack is, IAEFluidStack fs) {
        return false;
    }

    public static ItemStack getFillContainer(ItemStack is, IAEFluidStack fs) {
        return null;
    }

    public static int getEmptyContainerAmount(ItemStack is, IAEFluidStack fs) {
        return 0;
    }

    public static net.minecraftforge.fluids.FluidStack getFluidPerContainer(ItemStack is) {
        return null;
    }
}
