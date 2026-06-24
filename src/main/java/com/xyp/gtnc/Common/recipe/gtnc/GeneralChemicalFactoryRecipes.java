package com.xyp.gtnc.Common.recipe.gtnc;

import static gregtech.api.enums.TierEU.RECIPE_IV;

import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;

public class GeneralChemicalFactoryRecipes {

    public static void loadRecipes() {

        RecipeMap<?> GCFR = GTNCRecipeMaps.GeneralChemicalFactoryRecipes;

        // 硼砂处理
        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.SodiumHydroxide.getDust(64),
                Materials.SodiumHydroxide.getDust(64),
                Materials.SodiumHydroxide.getDust(64),
                Materials.SodiumCarbonate.getDust(64),
                Materials.SodiumCarbonate.getDust(32))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(24, Materials.Salt.getDust(1)),
                GTUtility.copyAmountUnsafe(736, Materials.Borax.getDust(64)))
            .fluidInputs(Materials.SaltWater.getFluid(128000), new FluidStack(GTPPFluids.Kerosene, 6000))
            .eut(RECIPE_IV)
            .duration(20 * 64)
            .addTo(GCFR);

    }
}
