package com.xyp.gtnc.Common.recipe.gregtech;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class FurnaceRecipes {

    public static void loadRecipes() {
        // 沙子烧玻璃 (ZS写反了,正确是 sand → glass)
        GTModHandler.addSmeltingRecipe(new ItemStack(Blocks.sand), new ItemStack(Blocks.glass));

        // 锻铁锭烧钢锭 (WroughtIron → Steel)
        GTModHandler.addSmeltingRecipe(
            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1L),
            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L));

        // 铁锭烧锻铁锭 (Iron → WroughtIron)
        GTModHandler.addSmeltingRecipe(
            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L),
            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1L));

        // 铜锭烧退火铜锭 (Copper → AnnealedCopper)
        GTModHandler.addSmeltingRecipe(
            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Copper, 1L),
            GTOreDictUnificator.get(OrePrefixes.ingot, Materials.AnnealedCopper, 1L));

    }
}
