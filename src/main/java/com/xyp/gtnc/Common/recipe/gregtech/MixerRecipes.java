package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.xyp.gtnc.Common.material.GTNCMaterials;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class MixerRecipes {

    public static void loadRecipes() {
        RecipeMap<?> Mixer = GTPPRecipeMaps.mixerNonCellRecipes;

        // ==================== 催化剂合成 ====================

        // SAPO分子筛（MTO催化剂）
        GTValues.RA.stdBuilder()
            .circuit(1)
            .itemInputs(
                Materials.SiliconDioxide.getDust(2),
                Materials.Aluminiumoxide.getDust(2),
                Materials.Phosphorus.getDust(2))
            .fluidInputs(Materials.Oxygen.getGas(5000L), Materials.Water.getFluid(500L))
            .itemOutputs(WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.SAPO34, 5))
            .duration(15 * SECONDS)
            .eut(120)
            .addTo(Mixer);

        // ==================== 火箭燃料化工 ====================

        // 高氯酸铵（固体火箭氧化剂）：Cl₂ + NH₃ + O₂ → NH₄ClO₄
        GTValues.RA.stdBuilder()
            .circuit(2)
            .fluidInputs(
                Materials.Chlorine.getGas(2000L),
                Materials.Ammonia.getGas(1000L),
                Materials.Oxygen.getGas(4000L))
            .itemOutputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.AmmoniumPerchlorate, 1))
            .duration(12 * SECONDS)
            .eut(480)
            .addTo(Mixer);

        // 二硝酰胺铵 ADN：NH₃ + N₂O₄ + HNO₃ → NH₄N₃O₄ + 2H₂O
        GTValues.RA.stdBuilder()
            .circuit(4)
            .fluidInputs(
                Materials.Ammonia.getGas(1000L),
                Materials.NitrogenDioxide.getGas(2000L),
                Materials.NitricAcid.getFluid(1000L))
            .itemOutputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.AmmoniumDinitramide, 1))
            .fluidOutputs(Materials.Water.getFluid(2000L))
            .duration(15 * SECONDS)
            .eut(480)
            .addTo(Mixer);

        // 硅岩氧化剂：Naquadah + F₂ + O₂ → Naq-O-F
        GTValues.RA.stdBuilder()
            .circuit(5)
            .itemInputs(Materials.Naquadah.getDust(1))
            .fluidInputs(Materials.Fluorine.getGas(2000L), Materials.Oxygen.getGas(1000L))
            .itemOutputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.NaquadahOxidizer, 1))
            .duration(20 * SECONDS)
            .eut(7680)
            .addTo(Mixer);
    }
}
