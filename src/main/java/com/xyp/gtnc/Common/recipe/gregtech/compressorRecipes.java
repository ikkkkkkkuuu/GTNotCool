package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.xyp.gtnc.Common.material.GTNCMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;

public class compressorRecipes {

    public static void loadRecipes() {

        RecipeMap<?> CP = RecipeMaps.compressorRecipes;

        // 压缩蒸汽锭：蒸汽 + 锭模具 → 压缩蒸汽锭
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Ingot.get(0))
            .itemOutputs(GTNCMaterials.CompressedSteam.get(OrePrefixes.ingot, 1))
            .fluidInputs(Materials.Steam.getGas(100000))
            .duration(SECONDS / 2)
            .eut(30)
            .addTo(CP);

    }
}
