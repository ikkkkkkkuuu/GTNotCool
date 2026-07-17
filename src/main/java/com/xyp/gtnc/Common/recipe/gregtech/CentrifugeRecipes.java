package com.xyp.gtnc.Common.recipe.gregtech;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;

public class CentrifugeRecipes {

    public static void loadRecipes() {
        RecipeMap<?> Ce = RecipeMaps.centrifugeRecipes;

        // 4 钛蜂窝 → 4 钛粉
        GTRecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TITANIUM, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 4))
            .duration(40)
            .eut(30)
            .addTo(Ce);

        // 4 钨蜂窝 → 4 钨粉
        GTRecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TUNGSTEN, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 4))
            .duration(40)
            .eut(30)
            .addTo(Ce);
    }

}
