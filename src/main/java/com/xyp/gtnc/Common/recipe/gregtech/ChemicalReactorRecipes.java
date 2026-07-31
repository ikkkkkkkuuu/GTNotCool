package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Common.material.GTNCMaterials;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;

public class ChemicalReactorRecipes {

    public static void loadRecipes() {
        RecipeMap<?> LCR = RecipeMaps.multiblockChemicalReactorRecipes;
        // 钾 + 氧气 → 钾碱
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potassium, 2L))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potash, 1L))
            .fluidInputs(Materials.Oxygen.getGas(1000L))
            .duration(5 * SECONDS)
            .eut(30)
            .addTo(LCR);

        // 丙烯 + 氢气 → 丙烷 (镍催化加氢)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Nickel.getDust(1)), GTUtility.getIntegratedCircuit(3))
            .fluidInputs(Materials.Propene.getGas(1000L), Materials.Hydrogen.getGas(1000L))
            .fluidOutputs(Materials.Propane.getGas(1000L))
            .duration(10 * SECONDS)
            .eut(30)
            .addTo(LCR);

        // 丙烷 → 丙烯 + 氢气 (铂催化脱氢, PDH工艺)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Platinum.getDust(1)), GTUtility.getIntegratedCircuit(4))
            .fluidInputs(Materials.Propane.getGas(1000L))
            .fluidOutputs(Materials.Propene.getGas(1000L), Materials.Hydrogen.getGas(1000L))
            .duration(12 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 乙烯 + 蒸汽 → 乙醇 (磷酸催化直接水合)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(0, Materials.PhosphoricAcid.getCells(1)),
                GTUtility.getIntegratedCircuit(3))
            .fluidInputs(Materials.Ethylene.getGas(1000L), Materials.Steam.getGas(1000L))
            .fluidOutputs(Materials.Ethanol.getFluid(1000L))
            .duration(8 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 乙醛 + 氢气 → 乙醇 (雷尼镍催化加氢)
        // Acetaldehyde 在 GTNH 中通过 FluidRegistry 注册，此处使用 FluidRegistry 查找
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Nickel.getDust(1)), GTUtility.getIntegratedCircuit(2))
            .fluidInputs(FluidRegistry.getFluidStack("acetaldehyde", 1000), Materials.Hydrogen.getGas(1000L))
            .fluidOutputs(Materials.Ethanol.getFluid(1000L))
            .duration(6 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 甘油 + 二氧化氮 → 三硝酸甘油酯
        GTValues.RA.stdBuilder()
            .circuit(1)
            .fluidInputs(Materials.Glycerol.getFluid(500L), Materials.NitrogenDioxide.getGas(500L))
            .fluidOutputs(Materials.Glyceryl.getFluid(750L))
            .duration(12 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 苯酚 + 甲醛 → 液态树脂
        GTValues.RA.stdBuilder()
            .circuit(1)
            .fluidInputs(Materials.Phenol.getFluid(500L), new FluidStack(GTPPFluids.Formaldehyde, 500))
            .fluidOutputs(new FluidStack(GTPPFluids.LiquidResin, 750))
            .duration(10 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 苯酚 + 氢气 → 环己烷
        GTValues.RA.stdBuilder()
            .circuit(2)
            .fluidInputs(Materials.Phenol.getFluid(1000L), Materials.Hydrogen.getGas(500L))
            .fluidOutputs(new FluidStack(GTPPFluids.Cyclohexane, 850))
            .duration(15 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 煤油 + 硫酸 → 含硫轻燃油
        GTValues.RA.stdBuilder()
            .circuit(3)
            .fluidInputs(new FluidStack(GTPPFluids.Kerosene, 1000), Materials.SulfuricAcid.getFluid(100L))
            .fluidOutputs(Materials.SulfuricLightFuel.getFluid(900L))
            .duration(12 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // ==================== 大型化学反应釜 (LCR) ====================

        // 氧气 + 氮气 → 一氧化氮
        GTValues.RA.stdBuilder()
            .circuit(9)
            .fluidInputs(Materials.Oxygen.getGas(1000L), Materials.Nitrogen.getGas(1000L))
            .fluidOutputs(Materials.NitricOxide.getGas(1000L))
            .duration(6 * SECONDS)
            .eut(2048)
            .addTo(LCR);

        // 石脑油 → 萘 + 氢气 + 甲烷 (铂催化重整)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Platinum.getDust(1)))
            .fluidInputs(Materials.Naphtha.getFluid(1000L))
            .fluidOutputs(
                new FluidStack(GTPPFluids.Naphthalene, 400),
                Materials.Hydrogen.getGas(300L),
                Materials.Methane.getGas(200L))
            .duration(12 * SECONDS)
            .eut(2048)
            .addTo(LCR);

        // 1,2-二甲苯 + 氢气 → 苯 + 甲烷 (加氢脱烷基, 铬催化)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Chrome.getDust(1)), GTUtility.getIntegratedCircuit(11))
            .fluidInputs(Materials.Dimethylbenzene.getFluid(1000L), Materials.Hydrogen.getGas(4000L))
            .fluidOutputs(Materials.Benzene.getFluid(1000L), Materials.Methane.getGas(2000L))
            .duration(12 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 甲烷 + 硫 → 二硫化碳 + 硫化氢 (硅胶催化)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L),
                GTUtility.copyAmountUnsafe(0, Materials.SiliconDioxide.getDust(1)),
                GTUtility.getIntegratedCircuit(4))
            .fluidInputs(Materials.Methane.getGas(1000L))
            .fluidOutputs(new FluidStack(GTPPFluids.CarbonDisulfide, 1000), Materials.HydricSulfide.getGas(2000L))
            .duration(15 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 硫化氢 + 碳 → 二硫化碳 + 氢气 (高温吸热)
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Carbon.getDust(1), GTUtility.getIntegratedCircuit(5))
            .fluidInputs(Materials.HydricSulfide.getGas(2000L))
            .fluidOutputs(new FluidStack(GTPPFluids.CarbonDisulfide, 1000), Materials.Hydrogen.getGas(2000L))
            .duration(20 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 煤气 + 蒸汽 → 氢气 + 二氧化碳 (水煤气变换, 铁基催化)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Iron.getDust(1)), GTUtility.getIntegratedCircuit(9))
            .fluidInputs(new FluidStack(GTPPFluids.CoalGas, 1000), Materials.Steam.getGas(1000L))
            .fluidOutputs(Materials.Hydrogen.getGas(2000L), Materials.CarbonDioxide.getGas(1000L))
            .duration(12 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 甘油 + 二氧化氮 → 三硝酸甘油酯 (LCR版)
        GTValues.RA.stdBuilder()
            .circuit(1)
            .fluidInputs(Materials.Glycerol.getFluid(500L), Materials.NitrogenDioxide.getGas(500L))
            .fluidOutputs(Materials.Glyceryl.getFluid(750L))
            .duration(12 * SECONDS)
            .eut(120)
            .addTo(LCR);

        // 甘油 + 氢气 → 甲烷 + 水 (加氢裂解)
        GTValues.RA.stdBuilder()
            .circuit(2)
            .fluidInputs(Materials.Glycerol.getFluid(1000L), Materials.Hydrogen.getGas(500L))
            .fluidOutputs(Materials.Methane.getGas(600L), Materials.Water.getFluid(400L))
            .duration(16 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 苯酚 + 氢气 → 苯 + 水 (催化加氢脱氧, 钯催化)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(0, Materials.Palladium.getDust(1)),
                GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Phenol.getFluid(1000L), Materials.Hydrogen.getGas(500L))
            .fluidOutputs(Materials.Benzene.getFluid(800L), Materials.Water.getFluid(200L))
            .duration(15 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 苯酚 + 硝化混合物 → 硝基苯
        GTValues.RA.stdBuilder()
            .circuit(1)
            .fluidInputs(Materials.Phenol.getFluid(500L), Materials.NitrationMixture.getFluid(500L))
            .fluidOutputs(new FluidStack(GTPPFluids.Nitrobenzene, 750))
            .duration(20 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 煤油 + 氢气 → 轻燃油 + 甲烷 (加氢裂化)
        GTValues.RA.stdBuilder()
            .circuit(2)
            .fluidInputs(new FluidStack(GTPPFluids.Kerosene, 1000), Materials.Hydrogen.getGas(300L))
            .fluidOutputs(Materials.LightFuel.getFluid(700L), Materials.Methane.getGas(400L))
            .duration(20 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 甲醇 + CO + H2 → 乙醇 (钴催化羰基化)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Cobalt.getDust(1)), GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                Materials.Methanol.getFluid(1000L),
                Materials.CarbonMonoxide.getGas(1000L),
                Materials.Hydrogen.getGas(1000L))
            .fluidOutputs(Materials.Ethanol.getFluid(1000L))
            .duration(10 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 甲醇 + CO + H2 → 乙醇 (IC-24 9x批量)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Cobalt.getDust(1)), GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                Materials.Methanol.getFluid(9000L),
                Materials.CarbonMonoxide.getGas(9000L),
                Materials.Hydrogen.getGas(9000L))
            .fluidOutputs(Materials.Ethanol.getFluid(9000L))
            .duration(90 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // 合成气 → 乙醇 + 水 (费托合成, 铁催化)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, Materials.Iron.getDust(1)), GTUtility.getIntegratedCircuit(5))
            .fluidInputs(Materials.CarbonMonoxide.getGas(2000L), Materials.Hydrogen.getGas(4000L))
            .fluidOutputs(Materials.Ethanol.getFluid(1000L), Materials.Water.getFluid(1000L))
            .duration(14 * SECONDS)
            .eut(480)
            .addTo(LCR);

        // ==================== 乙烯生产路线 ====================

        // 甲醇 → 乙烯 + 丙烯 + 水 (MTO甲醇制烯烃, SAPO分子筛催化)
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(
                    0,
                    WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.SAPO34, 1)))
            .fluidInputs(Materials.Methanol.getFluid(3000L))
            .fluidOutputs(
                Materials.Ethylene.getGas(1000L),
                Materials.Propene.getGas(800L),
                Materials.Water.getFluid(1200L))
            .duration(15 * SECONDS)
            .eut(1920)
            .addTo(LCR);

        // ==================== 火箭燃料 ====================

        // 高标汽油 + 高氯酸铵 → 高密度肼燃料 (AP消耗, 固体氧化剂路线)
        GTValues.RA.stdBuilder()
            .circuit(15)
            .itemInputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.AmmoniumPerchlorate, 1))
            .fluidInputs(Materials.GasolinePremium.getFluid(1000L))
            .fluidOutputs(new FluidStack(GTPPFluids.DenseHydrazineFuelMixture, 1200))
            .duration(10 * SECONDS)
            .eut(1920)
            .addTo(LCR);

        // 高标汽油 + 硝石 → CN3H7O3火箭燃料 (硝石消耗, 固体氧化剂路线)
        GTValues.RA.stdBuilder()
            .circuit(16)
            .itemInputs(Materials.Saltpeter.getDust(1))
            .fluidInputs(Materials.GasolinePremium.getFluid(1000L))
            .fluidOutputs(new FluidStack(GTPPFluids.CN3H7O3RocketFuel, 1000))
            .duration(10 * SECONDS)
            .eut(1920)
            .addTo(LCR);

        // 高密度肼燃料 + ADN → H8N4C2O4火箭燃料 (ADN消耗, 高能氧化剂升级)
        GTValues.RA.stdBuilder()
            .circuit(17)
            .itemInputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.AmmoniumDinitramide, 1))
            .fluidInputs(new FluidStack(GTPPFluids.DenseHydrazineFuelMixture, 1000))
            .fluidOutputs(new FluidStack(GTPPFluids.H8N4C2O4RocketFuel, 800))
            .duration(14 * SECONDS)
            .eut(7680)
            .addTo(LCR);

        // H8N4C2O4火箭燃料 + 硅岩氧化剂 → 硅岩基燃料MkV (NaqOx消耗, 顶级氧化剂升级)
        GTValues.RA.stdBuilder()
            .circuit(18)
            .itemInputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.NaquadahOxidizer, 1))
            .fluidInputs(new FluidStack(GTPPFluids.H8N4C2O4RocketFuel, 1000))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(600))
            .duration(20 * SECONDS)
            .eut(30720)
            .addTo(LCR);

        // 重油 + ADN → 高辛烷值汽油 (ADN消耗, 辛烷值提升)
        GTValues.RA.stdBuilder()
            .circuit(19)
            .itemInputs(
                WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.dust, GTNCMaterials.AmmoniumDinitramide, 1))
            .fluidInputs(Materials.HeavyFuel.getFluid(1000L))
            .fluidOutputs(Materials.GasolinePremium.getFluid(800L))
            .duration(12 * SECONDS)
            .eut(1920)
            .addTo(LCR);
    }
}
