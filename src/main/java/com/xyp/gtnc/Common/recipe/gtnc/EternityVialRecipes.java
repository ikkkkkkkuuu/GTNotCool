package com.xyp.gtnc.Common.recipe.gtnc;

import static gregtech.api.util.GTRecipeBuilder.MINUTES;

import com.xyp.gtnc.Loader.ItemsLoader;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.CompressionTierKey;

/** The direct Eternity Vial recipe from NH-Utilities; the normal Time Vial dependency is intentionally omitted. */
public final class EternityVialRecipes {

    private EternityVialRecipes() {}

    public static void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Timepiece.get(1))
            .fluidInputs(Materials.MHDCSM.getMolten(1145L))
            .itemOutputs(new net.minecraft.item.ItemStack(ItemsLoader.eternityVial))
            .metadata(CompressionTierKey.INSTANCE, 2)
            .duration(3 * MINUTES)
            .eut(TierEU.RECIPE_UXV)
            .addTo(RecipeMaps.neutroniumCompressorRecipes);
    }
}
