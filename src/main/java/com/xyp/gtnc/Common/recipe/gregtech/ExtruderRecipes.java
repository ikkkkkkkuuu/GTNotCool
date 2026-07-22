package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class ExtruderRecipes {

    public static void loadRecipes() {

        RecipeMap<?> Ex = RecipeMaps.extruderRecipes;

        // 赛特斯石英压杆

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.CertusQuartz, 1),
                ItemList.Shape_Mold_Rod.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.CertusQuartz, 2))
            .duration(SECONDS)
            .eut(RECIPE_LV)
            .addTo(Ex);

        // 赛特斯石英压螺栓
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.CertusQuartz, 1),
                ItemList.Shape_Mold_Bolt.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.bolt, Materials.CertusQuartz, 8))
            .duration(SECONDS)
            .eut(RECIPE_LV)
            .addTo(Ex);

        // 石英岩 → 石英岩杆
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Quartzite, 1),
                ItemList.Shape_Mold_Rod.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Quartzite, 2))
            .duration(SECONDS)
            .eut(RECIPE_LV)
            .addTo(Ex);

        // 石英岩螺栓 → 石英岩螺栓
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Quartzite, 1),
                ItemList.Shape_Mold_Bolt.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Quartzite, 8))
            .duration(SECONDS)
            .eut(RECIPE_LV)
            .addTo(Ex);

    }

}
