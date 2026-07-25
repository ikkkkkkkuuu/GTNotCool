package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class BlastFurnaceRecipes {

    public static void loadRecipes() {
        RecipeMap<?> BFR = RecipeMaps.blastFurnaceRecipes;
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.copyAmountUnsafe(1280, GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 1)),
                ItemList.GalliumArsenideCrystal.get(10))
            .itemOutputs(ItemList.Circuit_Silicon_Ingot.get(40))
            .circuit(2)
            .metadata(COIL_HEAT, 300)
            .duration(250 * SECONDS)
            .eut(RECIPE_MV)
            .addTo(BFR);
    }
}
