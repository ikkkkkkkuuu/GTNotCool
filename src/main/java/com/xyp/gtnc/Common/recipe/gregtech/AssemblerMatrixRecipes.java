package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import appeng.api.AEApi;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

/**
 * Crafting recipes for the Assembler Matrix multiblock (controller) and its Meta Casing 02 casing blocks. All recipes
 * are MV-tier assembler recipes, themed around the AE2 molecular assembler since the multiblock is effectively a
 * molecular assembler array. The Debug Crafter Core (meta 18) is intentionally left without a recipe.
 */
public class AssemblerMatrixRecipes {

    public static void loadRecipes() {

        // AE2 分子装配机（本机器本质就是分子装配阵列，主题取此）
        final ItemStack molecularAssembler = AEApi.instance()
            .definitions()
            .blocks()
            .molecularAssembler()
            .maybeStack(1)
            .orNull();
        final ItemStack ae2Interface = GTModHandler.getModItem("appliedenergistics2", "tile.BlockInterface", 1);

        // 装配矩阵 (控制器)
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hull_MV.get(1),
                copies(molecularAssembler, 4),
                GTModHandler.getModItem("appliedenergistics2", "tile.BlockInterface", 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.StainlessSteel.getMolten(1152))
            .itemOutputs(GTNCItemList.AssemblerMatrix.get(1))
            .eut(RECIPE_MV)
            .duration(30 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 矩阵墙 (结构外壳，需求量大，一次产 8)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1))
            .circuit(6)
            .fluidInputs(Materials.StainlessSteel.getMolten(288))
            .itemOutputs(GTNCItemList.AssemblerMatrixWall.get(8))
            .eut(RECIPE_MV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 图案核心 (每个 72 样板槽)
        GTValues.RA.stdBuilder()
            .itemInputs(
                copies(molecularAssembler, 2),
                copies(ae2Interface, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 2),
                ItemList.Tool_DataStick.get(4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(GTNCItemList.AssemblerMatrixPatternCore.get(1))
            .eut(RECIPE_MV)
            .duration(15 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 合成核心 (每个 2048 并行)
        GTValues.RA.stdBuilder()
            .itemInputs(
                copies(molecularAssembler, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Gold, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(GTNCItemList.AssemblerMatrixCrafterCore.get(1))
            .eut(RECIPE_MV)
            .duration(20 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 速度核心 (每个操作时间减半，最多 5 个)
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Electric_Motor_MV.get(4),
                ItemList.Electric_Piston_MV.get(2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 2),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.Lubricant.getFluid(1000))
            .itemOutputs(GTNCItemList.AssemblerMatrixSpeedCore.get(1))
            .eut(RECIPE_MV)
            .duration(20 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 奇点合成核心 (Integer.MAX 并行 + 触发无线供电，高价配方)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.AssemblerMatrixCrafterCore.get(1),
                GTNCItemList.SingularityDataHub.get(1),
                copies(molecularAssembler, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(GTNCItemList.AssemblerMatrixSingularityCrafterCore.get(1))
            .eut(RECIPE_MV)
            .duration(40 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 调试核心 (Long.MAX 并行) 故意不给配方 —— 仅创造/指令获取
    }

    /** Returns a size-{@code n} copy of {@code stack}, or {@code null} if the source item is unavailable. */
    private static ItemStack copies(ItemStack stack, int n) {
        if (stack == null) return null;
        ItemStack copy = stack.copy();
        copy.stackSize = n;
        return copy;
    }
}
