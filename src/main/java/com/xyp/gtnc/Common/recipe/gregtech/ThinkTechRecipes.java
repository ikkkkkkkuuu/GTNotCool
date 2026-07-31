package com.xyp.gtnc.Common.recipe.gregtech;

import static bartworks.system.material.WerkstoffLoader.SodiumNitrate;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.enums.TierEU.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Common.material.GTNCMaterials;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialsElements;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import ic2.api.item.IC2Items;

public class ThinkTechRecipes {

    public static void loadRecipes() {
        RecipeMap<?> LCR = RecipeMaps.multiblockChemicalReactorRecipes;
        RecipeMap<?> MX = GTPPRecipeMaps.mixerNonCellRecipes;
        RecipeMap<?> GT_Mixer = RecipeMaps.mixerRecipes;
        RecipeMap<?> VF = RecipeMaps.vacuumFreezerRecipes;
        RecipeMap<?> As = RecipeMaps.assemblerRecipes;

        // ==== 烷烃水混合物 ====
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .fluidInputs(Materials.NaturalGas.getGas(4000), Materials.Steam.getGas(20000))
            .fluidOutputs(GTNCMaterials.AlkaneWaterMixture.getFluidOrGas(12000))
            .eut(RECIPE_MV)
            .duration(20 * 10)
            .addTo(MX);

        GTValues.RA.stdBuilder()
            .itemInputs(Materials.NaturalGas.getCells(1))
            .itemOutputs(IC2Items.getItem("cell"))
            .fluidInputs(Materials.Steam.getGas(5000))
            .fluidOutputs(GTNCMaterials.AlkaneWaterMixture.getFluidOrGas(3000))
            .eut(RECIPE_MV)
            .duration(20 * 4)
            .addTo(GT_Mixer);

        // ==== TNT ====
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Toluene.getFluid(1000), Materials.NitricAcid.getFluid(3000))
            .fluidOutputs(GTNCMaterials.Trinitrotoluene.getMolten(1000), Materials.Water.getFluid(3000))
            .eut(RECIPE_HV)
            .duration(20 * 3)
            .addTo(LCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(24))
            .fluidInputs(Materials.Toluene.getFluid(8000), Materials.NitricAcid.getFluid(24000))
            .fluidOutputs(GTNCMaterials.Trinitrotoluene.getMolten(8000), Materials.Water.getFluid(24000))
            .eut(RECIPE_EV)
            .duration(20 * 5)
            .addTo(LCR);

        // ==== PETN ====
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pentaerythritol, 4))
            .fluidInputs(Materials.NitricAcid.getFluid(4000))
            .fluidOutputs(GTNCMaterials.PETN.getMolten(1000))
            .eut(RECIPE_EV)
            .duration(20 * 8)
            .addTo(LCR);

        // ==== HNIW ====
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(0, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1)))
            .fluidInputs(
                GTNCMaterials.Phenylmethanamine.getFluidOrGas(6000),
                GTNCMaterials.Ethanedial.getFluidOrGas(3000),
                Materials.Hydrogen.getGas(2000),
                Materials.NitricAcid.getFluid(6000))
            .fluidOutputs(GTNCMaterials.HNIW.getMolten(1000))
            .eut(RECIPE_ZPM)
            .duration(20 * 5)
            .addTo(LCR);

        // ==== HMT ====
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Ammonia.getGas(4000), new FluidStack(GTPPFluids.Formaldehyde, 6000))
            .fluidOutputs(GTNCMaterials.HMT.getMolten(1000), Materials.Water.getFluid(6000))
            .eut(RECIPE_EV)
            .duration(20 * 2)
            .addTo(LCR);

        // ==== HMX ====
        GTValues.RA.stdBuilder()
            .fluidInputs(
                GTNCMaterials.HMT.getMolten(1000),
                Materials.NitricAcid.getFluid(4000),
                WerkstoffMaterialPool.AmmoniumNitrate.getFluidOrGas(2000),
                FluidRegistry.getFluidStack("molten.aceticanhydride", 6000))
            .fluidOutputs(GTNCMaterials.HMX.getMolten(1500))
            .eut(RECIPE_LuV)
            .duration(20 * 2)
            .addTo(LCR);

        // ==== 乙二醛 ====
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Ethyleneglycol.getFluid(8000), Materials.Oxygen.getGas(8000))
            .fluidOutputs(GTNCMaterials.Ethanedial.getFluidOrGas(8000), Materials.Water.getFluid(8000))
            .eut(RECIPE_MV)
            .duration(20 * 5)
            .addTo(LCR);

        // ==== 苯甲醛 ====
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Toluene.getFluid(4000), Materials.Oxygen.getGas(4000))
            .fluidOutputs(GTNCMaterials.Benzaldehyd.getFluidOrGas(4000))
            .eut(RECIPE_MV)
            .duration(20 * 3)
            .addTo(LCR);

        // ==== 苄胺 ====
        GTValues.RA.stdBuilder()
            .fluidInputs(GTNCMaterials.Benzaldehyd.getFluidOrGas(4000), Materials.Ammonia.getGas(4000))
            .fluidOutputs(GTNCMaterials.Phenylmethanamine.getFluidOrGas(4000))
            .eut(RECIPE_HV)
            .duration(20)
            .addTo(LCR);

        // ==== 五氯化磷 ====
        GTValues.RA.stdBuilder()
            .circuit(5)
            .itemInputs(Materials.Phosphorus.getDust(1))
            .fluidInputs(Materials.Chlorine.getGas(5000))
            .itemOutputs(GTNCMaterials.Pentachloride.get(OrePrefixes.dust, 1))
            .eut(RECIPE_MV)
            .duration(30)
            .addTo(LCR);

        // ==== 叠氮化铅 ====
        GTValues.RA.stdBuilder()
            .itemInputs(GTNCMaterials.SodiumAzide.get(OrePrefixes.dust, 64), Materials.Lead.getDust(64))
            .itemOutputs(SodiumNitrate.get(OrePrefixes.dust, 64), GTNCMaterials.LeadAzide.get(OrePrefixes.dust, 64))
            .fluidInputs(Materials.NitricAcid.getFluid(128000))
            .fluidOutputs(Materials.Hydrogen.getGas(128000))
            .eut(RECIPE_MV)
            .duration(20 * 3)
            .addTo(LCR);

        // ==== 叠氮化钠 ====
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Sodium.getDust(32))
            .itemOutputs(GTNCMaterials.SodiumAzide.get(OrePrefixes.dust, 32))
            .fluidInputs(Materials.Ammonia.getGas(32000), Materials.Nitrogen.getGas(16000))
            .fluidOutputs(Materials.Hydrogen.getGas(48000))
            .eut(RECIPE_MV)
            .duration(20 * 5)
            .addTo(LCR);

        // ==== 预培养维生冷藏细菌液 ====
        GTValues.RA.stdBuilder()
            .fluidInputs(GTNCMaterials.PreculturedBacterialSolution.getFluidOrGas(1000))
            .fluidOutputs(GTNCMaterials.FreezedPreculturedBacterialSolution.getFluidOrGas(1000))
            .eut(RECIPE_EV)
            .duration(20 * 25)
            .addTo(VF);

        // ==== 二氧化硒 ====
        GTValues.RA.stdBuilder()
            .itemInputs(MaterialsElements.getInstance().SELENIUM.getDust(1))
            .fluidInputs(Materials.Oxygen.getGas(2000))
            .itemOutputs(GTNCMaterials.SeleniumDioxide.get(OrePrefixes.dust, 1))
            .eut(RECIPE_LV)
            .duration(20)
            .addTo(LCR);

        // ==== 营养液 ====
        GTValues.RA.stdBuilder()
            .itemInputs(
                Materials.RockSalt.getDust(4),
                Materials.Phosphorus.getDust(1),
                MaterialsElements.getInstance().IODINE.getDust(1))
            .fluidInputs(Materials.Nitrogen.getGas(2000))
            .fluidOutputs(GTNCMaterials.NutrientSolution.getFluidOrGas(1000))
            .eut(RECIPE_MV)
            .duration(20 * 3)
            .addTo(GT_Mixer);

        // ==== 营养液产肥料 ====
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Calcite.getDust(1))
            .fluidInputs(GTNCMaterials.NutrientSolution.getFluidOrGas(1000))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "itemFertilizer", 40))
            .eut(30)
            .duration(20 * 5)
            .addTo(LCR);

        // ==================== GTNL 独立化工配方 ====================

        // 氯化钡: Ba + HCl → BaCl₂ + H₂
        GTValues.RA.stdBuilder()
            .itemInputs(Materials.Barium.getDust(1))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(2000))
            .itemOutputs(GTNCMaterials.BariumChloride.get(OrePrefixes.dust, 3))
            .fluidOutputs(Materials.Hydrogen.getGas(2000))
            .duration(3 * 20)
            .eut(RECIPE_LV)
            .addTo(LCR);

        // 马来酸酐: 丁烷 + O₂ → 马来酸酐 + 水 (Bi催化)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1)))
            .fluidInputs(Materials.Oxygen.getGas(7000), Materials.Butane.getGas(1000))
            .fluidOutputs(Materials.Water.getFluid(4000), GTNCMaterials.MaleicAnhydride.getFluidOrGas(1000))
            .duration(14 * 20)
            .eut(RECIPE_MV)
            .addTo(LCR);

        // 均四甲苯: 氯甲烷 + 二甲苯 → 均四甲苯 + HCl
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Chloromethane.getGas(2000), Materials.Dimethylbenzene.getFluid(1000))
            .itemOutputs(GTNCMaterials.Durene.get(OrePrefixes.dust, 24))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(2000))
            .duration(150)
            .eut(RECIPE_MV)
            .addTo(LCR);

        // 三氟化锑: Sb₂O₃ + HF → SbF₃ + H₂O
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.AntimonyTrioxide, 5))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(6000))
            .itemOutputs(GTNCMaterials.AntimonyTrifluoride.get(OrePrefixes.dust, 8))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .duration(3 * 20)
            .eut(RECIPE_LV)
            .addTo(LCR);

        // 硫酸氢铵: NH₃ + H₂SO₄ → NH₄HSO₄
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Ammonia.getGas(1000), Materials.SulfuricAcid.getFluid(1000))
            .fluidOutputs(GTNCMaterials.AmmoniumBisulfate.getFluidOrGas(1000))
            .duration(15 * SECONDS)
            .eut(RECIPE_EV)
            .addTo(LCR);

        // ==================== GTNL 材料下游消耗配方 ====================

        // 氯化钡 + 硫酸 → 重晶石 + HCl
        GTValues.RA.stdBuilder()
            .itemInputs(GTNCMaterials.BariumChloride.get(OrePrefixes.dust, 3))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barite, 6))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(2000))
            .duration(4 * 20)
            .eut(RECIPE_LV)
            .addTo(LCR);

        // 马来酸酐 → 丁二酸 (RhPd催化加氢)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmountUnsafe(0, WerkstoffLoader.RhodiumPlatedPalladium.get(OrePrefixes.dust, 1)))
            .fluidInputs(
                Materials.Water.getFluid(1000),
                Materials.Hydrogen.getGas(1000),
                GTNCMaterials.MaleicAnhydride.getFluidOrGas(2000))
            .itemOutputs(GTNCMaterials.SuccinicAcid.get(OrePrefixes.dust, 14))
            .duration(10 * 20)
            .eut(RECIPE_EV)
            .addTo(LCR);

        // 均四甲苯 + O₂ → PMDA + H₂O
        GTValues.RA.stdBuilder()
            .itemInputs(GTNCMaterials.Durene.get(OrePrefixes.dust, 24))
            .fluidInputs(Materials.Oxygen.getGas(12000))
            .itemOutputs(GTNCMaterials.PMDA.get(OrePrefixes.dust, 18))
            .fluidOutputs(Materials.Water.getFluid(6000))
            .duration(150)
            .eut(RECIPE_MV)
            .addTo(LCR);

        // 三氟化锑 + HF → 氟锑酸 (超强酸)
        GTValues.RA.stdBuilder()
            .itemInputs(GTNCMaterials.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(4000))
            .fluidOutputs(GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000), Materials.Hydrogen.getGas(2000))
            .duration(300)
            .eut(RECIPE_HV)
            .addTo(LCR);

        // 硫酸氢铵 + H₂O → 过硫酸铵 + H₂O₂ (电解-化学联合)
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(GTNCMaterials.AmmoniumBisulfate.getFluidOrGas(2000), Materials.Water.getFluid(1000))
            .fluidOutputs(
                GTNCMaterials.AmmoniumBisulfate.getFluidOrGas(1000),
                new FluidStack(GTPPFluids.HydrogenPeroxide, 1000))
            .duration(10 * 20)
            .eut(RECIPE_IV)
            .addTo(LCR);

        // ==== SmallBaka (硝基苯流体) ====

        // HNO₃ + 苯 + H₂ → SmallBaka + H₂O
        GTValues.RA.stdBuilder()
            .fluidInputs(
                Materials.NitricAcid.getFluid(3000),
                Materials.Benzene.getFluid(1000),
                Materials.Hydrogen.getGas(5000))
            .fluidOutputs(Materials.Water.getFluid(4000), GTNCMaterials.SmallBaka.getFluidOrGas(1000))
            .duration(20)
            .eut(RECIPE_HV)
            .addTo(LCR);

        // SmallBaka + 聚乙烯板 → 工业TNT
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polyethylene, 4))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "blockITNT", 32))
            .fluidInputs(GTNCMaterials.SmallBaka.getFluidOrGas(2000))
            .duration(100)
            .eut(RECIPE_HV)
            .addTo(LCR);

        // ==================== 电路材料合成 ====================

        // 聚酰亚胺: C + H₂ + N₂ + O₂ → 聚酰亚胺熔融
        GTValues.RA.stdBuilder()
            .circuit(29)
            .itemInputs(Materials.Carbon.getDust(22))
            .fluidInputs(
                Materials.Hydrogen.getGas(6000L),
                Materials.Nitrogen.getGas(1000L),
                Materials.Oxygen.getGas(3000L))
            .fluidOutputs(GTNCMaterials.Polyimide.getMolten(1000))
            .duration(20 * SECONDS)
            .eut(7680)
            .addTo(LCR);

        // ==================== BiowareSMD 电路 ====================

        // 生物贴片电容: Naq合金细线 + 硅箔 + Naq箔 + 聚酰亚胺
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Silicon, 8),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Naquadah, 4))
            .fluidInputs(GTNCMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNCItemList.BiowareSMDCapacitor.get(16))
            .duration(100)
            .eut(491520)
            .addTo(As);

        // 生物贴片二极管: Naq合金细线 + Lu + Tritanium + 聚酰亚胺
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1))
            .fluidInputs(GTNCMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNCItemList.BiowareSMDDiode.get(16))
            .duration(100)
            .eut(491520)
            .addTo(As);

        // 生物贴片电阻: Naq合金细线 + Naquadria板 + Tritanium板 + 聚酰亚胺
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Naquadria, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Tritanium, 1))
            .fluidInputs(GTNCMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNCItemList.BiowareSMDResistor.get(16))
            .duration(100)
            .eut(491520)
            .addTo(As);

        // 生物贴片晶体管: Naq合金细线 + Ge₃W₃N₁₀板 + SiC板 + 聚酰亚胺
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTNCMaterials.Germaniumtungstennitride.get(OrePrefixes.plate, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Silicon, 2))
            .fluidInputs(GTNCMaterials.Polyimide.getMolten(288))
            .itemOutputs(GTNCItemList.BiowareSMDTransistor.get(16))
            .duration(100)
            .eut(491520)
            .addTo(As);

        // 二硅化钼线圈: MoSi₂棒 + HSLA钢熔融
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNCMaterials.MolybdenumDisilicide.get(OrePrefixes.stick, 32),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Mica, 16))
            .fluidInputs(GTNCMaterials.HSLASteel.getMolten(144))
            .itemOutputs(GTNCItemList.MolybdenumDisilicideCoil.get(1))
            .duration(500)
            .eut(1920)
            .addTo(As);

        // ==================== BiowareSMD 下游消耗 ====================

        // 4种SMD + 晶体处理器 → UV电路板
        GTValues.RA.stdBuilder()
            .circuit(30)
            .itemInputs(
                GTNCItemList.BiowareSMDCapacitor.get(16),
                GTNCItemList.BiowareSMDDiode.get(16),
                GTNCItemList.BiowareSMDResistor.get(16),
                GTNCItemList.BiowareSMDTransistor.get(16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 1))
            .duration(200)
            .eut(491520)
            .addTo(As);

        // SMD电容×32 + SMD电阻×32 + 晶圆 → UV处理器
        GTValues.RA.stdBuilder()
            .circuit(31)
            .itemInputs(
                GTNCItemList.BiowareSMDCapacitor.get(32),
                GTNCItemList.BiowareSMDResistor.get(32),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 1))
            .duration(150)
            .eut(491520)
            .addTo(As);

        // 二硅化钼线圈 → 高温超导线圈
        GTValues.RA.stdBuilder()
            .circuit(32)
            .itemInputs(
                GTNCItemList.MolybdenumDisilicideCoil.get(1),
                GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorLuV, 8))
            .itemOutputs(ItemList.Casing_Coil_Superconductor.get(1))
            .duration(300)
            .eut(30720)
            .addTo(As);
    }
}
