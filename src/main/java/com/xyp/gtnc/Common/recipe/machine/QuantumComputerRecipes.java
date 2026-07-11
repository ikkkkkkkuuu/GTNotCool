package com.xyp.gtnc.Common.recipe.machine;

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
 * Crafting recipes for the Quantum Computer multiblock (controller) and its Meta Casing 02 casing blocks. All recipes
 * are MV-tier assembler recipes, themed around real AE2 crafting-storage / co-processor blocks since the multiblock is
 * effectively a giant crafting CPU. The functional blocks preserve an upgrade chain (128M -> 256M, accelerator + 256M
 * -> core, core + accelerators -> multi-threader / singularity core), mirroring the source project.
 */
public class QuantumComputerRecipes {

    public static void loadRecipes() {

        // AE2 合成存储组件 (本机器本质是巨型合成 CPU，主题取此)
        final ItemStack craftingUnit = aeBlock("craftingUnit", 1);
        final ItemStack craftingStorage64k = aeBlock("craftingStorage64k", 1);
        final ItemStack craftingStorage256k = aeBlock("craftingStorage256k", 1);
        final ItemStack craftingAccelerator = aeBlock("craftingAccelerator", 1);
        final ItemStack ae2Interface = GTModHandler.getModItem("appliedenergistics2", "tile.BlockInterface", 1);

        // 量子计算机 (控制器)
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hull_MV.get(1),
                copies(craftingUnit, 4),
                copies(ae2Interface, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.StainlessSteel.getMolten(1152))
            .itemOutputs(GTNCItemList.QuantumComputer.get(1))
            .eut(RECIPE_MV)
            .duration(30 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 量子计算机外壳 (结构外壳，需求量大，一次产 8；circuit 10 区别于装配矩阵墙的 circuit 6，避免装配机配方冲突)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 6),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1))
            .circuit(10)
            .fluidInputs(Materials.StainlessSteel.getMolten(288))
            .itemOutputs(GTNCItemList.QuantumComputerCasing.get(8))
            .eut(RECIPE_MV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 量子合成单元 (基础内部方块)
        GTValues.RA.stdBuilder()
            .itemInputs(
                copies(craftingUnit, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 2),
                ItemList.Tool_DataStick.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(GTNCItemList.QuantumComputerUnit.get(1))
            .eut(RECIPE_MV)
            .duration(15 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 128M 合成存储
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerUnit.get(1),
                copies(craftingStorage64k, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 2),
                ItemList.Tool_DataStick.get(8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(GTNCItemList.QuantumComputerCraftingStorage128M.get(1))
            .eut(RECIPE_MV)
            .duration(20 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 256M 合成存储 (由两个 128M 升级)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerCraftingStorage128M.get(2),
                copies(craftingStorage256k, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 2),
                ItemList.Tool_DataOrb.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(GTNCItemList.QuantumComputerCraftingStorage256M.get(1))
            .eut(RECIPE_MV)
            .duration(20 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 数据纠缠器 (存储 x4)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerCraftingStorage256M.get(1),
                copies(craftingStorage256k, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 4),
                ItemList.Tool_DataOrb.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(GTNCItemList.QuantumComputerDataEntangler.get(1))
            .eut(RECIPE_MV)
            .duration(30 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 加速器 (每个提供 1638400 并行线程)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerUnit.get(1),
                copies(craftingAccelerator, 3),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 2),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.Gold, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4))
            .fluidInputs(Materials.SolderingAlloy.getMolten(288))
            .itemOutputs(GTNCItemList.QuantumComputerAccelerator.get(1))
            .eut(RECIPE_MV)
            .duration(20 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 核心 (256M 存储 + 16384 并行，由加速器 + 256M 存储升级)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerAccelerator.get(1),
                GTNCItemList.QuantumComputerCraftingStorage256M.get(1),
                copies(craftingAccelerator, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(GTNCItemList.QuantumComputerCore.get(1))
            .eut(RECIPE_MV)
            .duration(30 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 多线程处理器 (协处理器 x4，由核心 + 加速器升级)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerCore.get(1),
                GTNCItemList.QuantumComputerAccelerator.get(4),
                copies(craftingAccelerator, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(GTNCItemList.QuantumComputerMultiThreader.get(1))
            .eut(RECIPE_MV)
            .duration(40 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 奇点核心 (存储锁定 9.22E，高价配方)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCItemList.QuantumComputerCore.get(1),
                GTNCItemList.QuantumComputerDataEntangler.get(1),
                GTNCItemList.SingularityDataHub.get(1),
                aeBlock("craftingStorageSingularity", 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8))
            .fluidInputs(Materials.SolderingAlloy.getMolten(1152))
            .itemOutputs(GTNCItemList.QuantumComputerSingularityCore.get(1))
            .eut(RECIPE_MV)
            .duration(60 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }

    /** Looks up an AE2 block definition by name, returning a size-{@code n} stack or {@code null} if unavailable. */
    private static ItemStack aeBlock(String name, int n) {
        switch (name) {
            case "craftingUnit":
                return orNull(
                    AEApi.instance()
                        .definitions()
                        .blocks()
                        .craftingUnit(),
                    n);
            case "craftingStorage64k":
                return orNull(
                    AEApi.instance()
                        .definitions()
                        .blocks()
                        .craftingStorage64k(),
                    n);
            case "craftingStorage256k":
                return orNull(
                    AEApi.instance()
                        .definitions()
                        .blocks()
                        .craftingStorage256k(),
                    n);
            case "craftingStorageSingularity":
                return orNull(
                    AEApi.instance()
                        .definitions()
                        .blocks()
                        .craftingStorageSingularity(),
                    n);
            case "craftingAccelerator":
                return orNull(
                    AEApi.instance()
                        .definitions()
                        .blocks()
                        .craftingAccelerator64x(),
                    n);
            default:
                return null;
        }
    }

    private static ItemStack orNull(appeng.api.definitions.IItemDefinition def, int n) {
        return def.maybeStack(n)
            .orNull();
    }

    /** Returns a size-{@code n} copy of {@code stack}, or {@code null} if the source item is unavailable. */
    private static ItemStack copies(ItemStack stack, int n) {
        if (stack == null) return null;
        ItemStack copy = stack.copy();
        copy.stackSize = n;
        return copy;
    }
}
