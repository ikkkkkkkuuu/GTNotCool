package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_IV;
import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.enums.TierEU.RECIPE_UHV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.enums.ModList;
import com.xyp.gtnc.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class AssemblerRecipes {

    public static void loadRecipes() {
        RecipeMap<?> As = RecipeMaps.assemblerRecipes;

        // 贴片二极管
        if (ModList.NewHorizonsCoreMod.isModLoaded()) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Platinum, 16),
                    ItemList.GalliumArsenideCrystal.get(1L))
                .circuit(3)
                .itemOutputs(ItemList.Circuit_Parts_DiodeSMD.get(64L))
                .fluidInputs(Materials.Polyethylene.getMolten(576L))
                .duration(30 * SECONDS)
                .eut(TierEU.RECIPE_LV)
                .addTo(As);
        }

        // 二极管
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.GalliumArsenideCrystal.get(1L),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Copper, 16))
            .itemOutputs(ItemList.Circuit_Parts_Diode.get(16))
            .fluidInputs(Materials.Polyethylene.getMolten(576))
            .circuit(4)
            .duration(3 * SECONDS)
            .eut(RECIPE_LV)
            .addTo(As);

        // 虚空蒸汽采矿场
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.VibrantAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 8),
                ItemList.Casing_MV.get(4),
                ItemList.Electric_Pump_MV.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 8),
                ItemList.Electric_Piston_MV.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.StainlessSteel, 16))
            .fluidInputs(Materials.StainlessSteel.getMolten(9216))
            .itemOutputs(GTNCItemList.LargeSteamVoidMiner.get(1))
            .circuit(1)
            .eut(RECIPE_MV)
            .duration(SECONDS * 60)
            .addTo(As);

        // 矿机平台
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.VibrantAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 8),
                ItemList.Casing_HV.get(4),
                ItemList.Electric_Pump_HV.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 8),
                ItemList.Electric_Piston_HV.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.StainlessSteel, 16),
                GTModHandler.getIC2Item("miningPipe", 64))
            .fluidInputs(Materials.StainlessSteel.getMolten(9216))
            .itemOutputs(GTNCItemList.MiningRig.get(1))
            .circuit(2)
            .eut(RECIPE_HV)
            .duration(SECONDS * 60 * 5)
            .addTo(As);

        // 钻井平台
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.VibrantAlloy, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 8),
                ItemList.Casing_HV.get(4),
                ItemList.Electric_Pump_HV.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 8),
                ItemList.Electric_Piston_HV.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.StainlessSteel, 16),
                GTModHandler.getIC2Item("miningPipe", 64))
            .fluidInputs(Materials.StainlessSteel.getMolten(9216))
            .itemOutputs(GTNCItemList.DrillingRig.get(1))
            .circuit(1)
            .eut(RECIPE_HV)
            .duration(SECONDS * 60 * 5)
            .addTo(As);

        // 通用化工厂
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Tool_DataStick.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 6),
                ItemList.Hull_EV.get(1),
                ItemList.Electric_Piston_HV.get(2))
            .itemOutputs(GTNCItemList.GeneralChemicalFactory.get(1))
            .fluidInputs(Materials.Polytetrafluoroethylene.getMolten(2304))
            .eut(RECIPE_EV)
            .duration(SECONDS * 15)
            .addTo(As);

        // chipTier1
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(2))
            .itemOutputs(GTNCItemList.ChipTier1.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_MV)
            .duration(SECONDS * 15)
            .addTo(As);

        // chipTier2
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(8))
            .itemOutputs(GTNCItemList.ChipTier2.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_HV)
            .duration(20 * SECONDS)
            .addTo(As);

        // chipTier3
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.HV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(16))
            .itemOutputs(GTNCItemList.ChipTier3.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_EV)
            .duration(20 * SECONDS)
            .addTo(As);

        // chipTier4
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(32))
            .itemOutputs(GTNCItemList.ChipTier4.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_IV)
            .duration(20 * SECONDS)
            .addTo(As);

        // chipTier5
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(42))
            .itemOutputs(GTNCItemList.ChipTier5.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_LuV)
            .duration(20 * SECONDS)
            .addTo(As);

        // chipTier6
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LuV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(48))
            .itemOutputs(GTNCItemList.ChipTier6.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_ZPM)
            .duration(20 * SECONDS)
            .addTo(As);

        // chipTier7
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                ItemList.Tool_DataStick.get(42))
            .itemOutputs(GTNCItemList.ChipTier7.get(1))
            .fluidInputs(Materials.Polyethylene.getMolten(9216))
            .circuit(24)
            .eut(RECIPE_UHV)
            .duration(20 * SECONDS)
            .addTo(As);

        // 坩埚
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_Firebricks.get(16),
                ItemList.Hull_LV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 8),
                ItemList.Electric_Piston_LV.get(4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1))
            .fluidInputs(Materials.Steel.getMolten(1152))
            .itemOutputs(GTNCItemList.LargeSteamCrucibleSteel.get(1))
            .eut(RECIPE_LV)
            .duration(20 * SECONDS)
            .addTo(As);

        // 殷钢坩埚（MV级）
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_Firebricks.get(16),
                ItemList.Hull_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Invar, 8),
                ItemList.Electric_Piston_MV.get(4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Invar, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Invar, 1))
            .fluidInputs(Materials.Invar.getMolten(576))
            .itemOutputs(GTNCItemList.LargeSteamCrucibleInvar.get(1))
            .eut(RECIPE_MV)
            .duration(20 * SECONDS)
            .addTo(As);

        // 不锈钢坩埚（HV级）
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_Firebricks.get(16),
                ItemList.Hull_HV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 8),
                ItemList.Electric_Piston_HV.get(4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.StainlessSteel, 1))
            .fluidInputs(Materials.StainlessSteel.getMolten(576))
            .itemOutputs(GTNCItemList.LargeSteamCrucibleStainless.get(1))
            .eut(RECIPE_HV)
            .duration(20 * SECONDS)
            .addTo(As);

        // 钛坩埚（EV级）
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_Firebricks.get(16),
                ItemList.Hull_EV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Titanium, 8),
                ItemList.Electric_Piston_EV.get(4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Titanium, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1))
            .fluidInputs(Materials.Titanium.getMolten(576))
            .itemOutputs(GTNCItemList.LargeSteamCrucibleTitanium.get(1))
            .eut(RECIPE_EV)
            .duration(20 * SECONDS)
            .addTo(As);

        // 钨钢坩埚（IV级）
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Casing_Firebricks.get(16),
                ItemList.Hull_IV.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 8),
                ItemList.Electric_Piston_IV.get(4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.TungstenSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1))
            .fluidInputs(Materials.TungstenSteel.getMolten(576))
            .itemOutputs(GTNCItemList.LargeSteamCrucibleTungstenSteel.get(1))
            .eut(RECIPE_IV)
            .duration(20 * SECONDS)
            .addTo(As);

        // 蒸汽蜜蜂
        GTRecipeBuilder.builder()
            .itemInputs(
                GTNCItemList.LargeSteamAssembler.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTNCItemList.LargeSteamBeeBreeder.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 养蜂机
        GTRecipeBuilder.builder()
            .itemInputs(
                GTNCItemList.LargeSteamAssembler.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Copper, 2),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTNCItemList.MegaIndustrialApiary.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 蜂窝处理
        GTRecipeBuilder.builder()
            .itemInputs(
                GTNCItemList.LargeSteamAssembler.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 2),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTNCItemList.LargeSteamCombProcessor.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 超级样板输入总线
        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_Bus_LV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 2))
            .circuit(22)
            .itemOutputs(GTNCItemList.SuperMTEHatchCraftingInputBusME.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 超级样板输入总成
        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_Bus_LV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 2))
            .circuit(21)
            .itemOutputs(GTNCItemList.SuperMTEHatchCraftingInputME.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 超级样板输入镜像
        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Hatch_Input_Bus_LV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 2))
            .circuit(23)
            .itemOutputs(GTNCItemList.SuperMTEHatchCraftingInputSlave.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 保险库
        GTRecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4))
            .circuit(24)
            .itemOutputs(GTNCItemList.SingularityDataHub.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 保险库数据中心
        GTRecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4))
            .circuit(24)
            .itemOutputs(GTNCItemList.VaultPortHatch.get(1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 大型矿物处理
        GTRecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Copper, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Aluminium, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4))
            .circuit(24)
            .itemOutputs(GTNCItemList.LargeOreProcessor.get(1))
            .duration(200)
            .eut(32)
            .addTo(As);

        // 矿处理外壳
        GTRecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Copper, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4),
                ItemList.Casing_LV.get(1),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(GTNCItemList.MineralprocessingFrame.get(4))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);

        // 存储输入总线 (ME输入总线 → 高级ME输入总线)
        GTRecipeBuilder.builder()
            .itemInputs(ItemList.Hatch_Input_Bus_ME.get(1))
            .itemOutputs(ItemList.Hatch_Input_Bus_ME_Advanced.get(1))
            .circuit(1)
            .duration(SECONDS)
            .eut(RECIPE_LV)
            .addTo(As);

        // me流体存储输入仓 (ME输入总线 → me流体存储输入仓)
        GTRecipeBuilder.builder()
            .itemInputs(ItemList.Hatch_Input_Bus_ME.get(1))
            .itemOutputs(ItemList.Hatch_Input_ME_Advanced.get(1))
            .circuit(2)
            .duration(SECONDS)
            .eut(RECIPE_LV)
            .addTo(As);

        // T4 无人机
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Steel, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit.get(Materials.LV), 4),
                ItemList.Electric_Motor_LV.get(16),
                ItemList.Emitter_LV.get(4),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Sapphire, 1))
            .circuit(4)
            .itemOutputs(ItemList.TierdDrone3.get(4))
            .fluidInputs(Materials.Lubricant.getFluid(INGOTS))
            .duration(10 * SECONDS)
            .eut(RECIPE_LV);

        // ME输出总线
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Output_Bus_MV.get(1L),
                GTModHandler.getModItem("appliedenergistics2", "tile.BlockInterface", 1),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemMultiMaterial", 2, 27))
            .itemOutputs(ItemList.Hatch_Output_Bus_ME.get(1L))
            .duration(6 * SECONDS)
            .eut(RECIPE_MV)
            .addTo(As);
        // ME输出仓
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Output_MV.get(1L),
                GTModHandler.getModItem("ae2fc", "fluid_interface", 1),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemMultiMaterial", 2, 27))
            .itemOutputs(ItemList.Hatch_Output_ME.get(1L))
            .duration(6 * SECONDS)
            .eut(RECIPE_MV)
            .addTo(As);
        // ME接口
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.RedAlloy, 2),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Ruby, 1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Sapphire, 1),
                ItemList.Casing_LV.get(1))
            .circuit(14)
            .itemOutputs(GTModHandler.getModItem("appliedenergistics2", "tile.BlockInterface", 1))
            .duration(5 * SECONDS)
            .eut(RECIPE_LV)
            .addTo(As);
        // AE2 流体接口
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CastIron, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.RedAlloy, 2),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.GreenSapphire, 1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Sapphire, 1),
                ItemList.Casing_LV.get(1))
            .circuit(14)
            .itemOutputs(GTModHandler.getModItem("ae2fc", "fluid_interface", 1))
            .duration(5 * SECONDS)
            .eut(RECIPE_LV)
            .addTo(As);
        // SMD inductor贴片电感
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.NaquadahAlloy, 1))
            .fluidInputs(Materials.Lubricant.getFluid(1296))
            .itemOutputs(GTNCItemList.BiowareSMDInductor.get(32))
            .duration(SECONDS)
            .eut(RECIPE_EV)
            .addTo(As);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Machine_LV_Electrolyzer.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2),
                GTUtility.getIntegratedCircuit(19))
            .itemOutputs(GTNCItemList.LargeSteamElectrolyzer.get(1))
            .duration(5 * SECONDS)
            .eut(RECIPE_LV)
            .addTo(As);
        loadcovers();
        loadEnergyHatches();
    }

    public static void loadEnergyHatches() {

        // 多A能源仓 LV/MV/HV: 4A, 16A, 64A
        // 配方: 4个低安能源仓 + 线圈 + 线缆 → 1个高一级多安能源仓
        ItemStack[] COIL = { ItemList.LV_Coil.get(2), ItemList.MV_Coil.get(3), ItemList.HV_Coil.get(4) };
        Materials[] CABLE_MAT = { Materials.Tin, Materials.Copper, Materials.Gold };

        for (int j = 0; j < 3; j++) {
            for (int i = 1; i <= 3; i++) { // 0=1A(原版), 1=4A, 2=16A, 3=64A
                int cableAmt = i * 2; // 4A→2, 16A→4, 64A→6
                GTRecipeBuilder.builder()
                    .itemInputs(
                        GTNCItemList.ENERGY_HATCH[j][i - 1].get(4), // 4x低安能源仓
                        GTOreDictUnificator.get(OrePrefixes.cableGt04, CABLE_MAT[j], cableAmt), // 4x线缆
                        COIL[j]) // 线圈
                    .itemOutputs(GTNCItemList.ENERGY_HATCH[j][i].get(1))
                    .fluidInputs(Materials.Lubricant.getFluid(144))
                    .duration(100 + 50 * i)
                    .eut(GTValues.VP[j + 1])
                    .addTo(RecipeMaps.assemblerRecipes);
            }
        }
    }

    public static void loadcovers() {

        ItemStack[] COIL = { ItemList.LV_Coil.get(1), ItemList.MV_Coil.get(2), ItemList.HV_Coil.get(3),
            ItemList.EV_Coil.get(3), ItemList.IV_Coil.get(4), ItemList.LuV_Coil.get(8), ItemList.ZPM_Coil.get(8),
            ItemList.UV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(12),
            ItemList.UHV_Coil.get(12), ItemList.UHV_Coil.get(16), ItemList.UHV_Coil.get(32) };

        ItemStack[] CHIP = { GTOreDictUnificator.get(OrePrefixes.spring, Materials.Tin, 16),
            ItemList.Circuit_Chip_ULPIC.get(2), ItemList.Circuit_Chip_LPIC.get(2), ItemList.Circuit_Chip_PIC.get(3),
            ItemList.Circuit_Chip_HPIC.get(3), ItemList.Circuit_Chip_UHPIC.get(4), ItemList.Circuit_Chip_NPIC.get(4),
            ItemList.Circuit_Chip_PPIC.get(6), ItemList.Circuit_Chip_QPIC.get(6), ItemList.Circuit_Chip_QPIC.get(8),
            ItemList.Circuit_Chip_QPIC.get(8), ItemList.Circuit_Chip_QPIC.get(12), ItemList.Circuit_Chip_QPIC.get(16),
            ItemList.Circuit_Chip_QPIC.get(32) };

        for (int i = 0; i < 14; i++) {
            boolean isHighTier = i >= 11; // UMV+
            OrePrefixes cable01 = isHighTier ? OrePrefixes.wireGt01 : OrePrefixes.cableGt01;

            // ① 1A覆盖版
            GTRecipeBuilder.builder()
                .itemInputs(
                    ItemUtils.SENSOR[i].get(1), // 传感器
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.EnderPearl, 12), // 末影珍珠板×12
                    GTOreDictUnificator.get(OrePrefixes.circuit, ItemUtils.TIER[i], 4), // 电路×4
                    COIL[i], // 线圈
                    CHIP[i], // 芯片
                    GTOreDictUnificator.get(cable01, ItemUtils.CABLE[i], 8), // 线缆×8
                    GTOreDictUnificator.get(cable01, Materials.RedAlloy, 32), // 红石合金线缆×32
                    GTOreDictUnificator.get(OrePrefixes.plate, ItemUtils.TIER_MATERIAL[i], 12)) // 板材×12
                .itemOutputs(GTNCItemList.WIRELESS_ENERGY_COVER[i].get(1))
                .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                .duration(200)
                .eut(GTValues.VP[i + 1])
                .addTo(RecipeMaps.assemblerRecipes);
        }

        ItemStack[] COIL_4A = { ItemList.LV_Coil.get(2), ItemList.MV_Coil.get(3), ItemList.HV_Coil.get(3),
            ItemList.EV_Coil.get(3), ItemList.IV_Coil.get(4), ItemList.LuV_Coil.get(8), ItemList.ZPM_Coil.get(8),
            ItemList.UV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(8), ItemList.UHV_Coil.get(12),
            ItemList.UHV_Coil.get(12), ItemList.UHV_Coil.get(32), ItemList.UHV_Coil.get(64) };

        ItemStack[] INDUCTOR = { ItemList.Circuit_Parts_Coil.get(4), ItemList.Circuit_Parts_Coil.get(8),
            ItemList.Circuit_Parts_InductorSMD.get(4), ItemList.Circuit_Parts_InductorSMD.get(8),
            ItemList.Circuit_Parts_InductorSMD.get(16), ItemList.Circuit_Parts_InductorSMD.get(32),
            ItemList.Circuit_Parts_InductorASMD.get(4), ItemList.Circuit_Parts_InductorASMD.get(8),
            ItemList.Circuit_Parts_InductorASMD.get(16), ItemList.Circuit_Parts_InductorXSMD.get(4),
            ItemList.Circuit_Parts_InductorXSMD.get(8), ItemList.Circuit_Parts_InductorXSMD.get(16),
            GTNCItemList.BiowareSMDInductor.get(8), GTNCItemList.BiowareSMDInductor.get(16) };

        for (int i = 0; i < 14; i++) {
            boolean isHighTier = i >= 11;
            OrePrefixes cable04 = isHighTier ? OrePrefixes.wireGt04 : OrePrefixes.cableGt04;

            // ② 4A覆盖版（2个1A→1个4A）
            GTRecipeBuilder.builder()
                .itemInputs(
                    GTNCItemList.WIRELESS_ENERGY_COVER[i].get(2), // 1A覆盖版×2
                    INDUCTOR[i], // 电感器
                    GTOreDictUnificator.get(cable04, ItemUtils.CABLE[i], 4), // 粗线缆×4
                    COIL_4A[i], // 加强线圈
                    GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.BatteryAlloy, 2)) // 电池合金双层板×2
                .itemOutputs(GTNCItemList.WIRELESS_ENERGY_COVER_4A[i].get(1))
                .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                .duration(200)
                .eut(GTValues.VP[i + 1])
                .addTo(RecipeMaps.assemblerRecipes);
        }

        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 9; i++) {
                int quantity;
                if (i < 4) {
                    quantity = 1;
                } else {
                    quantity = 1 << (i - 1);
                }

                OrePrefixes prefix = switch (i) {
                    case 0 -> OrePrefixes.wireGt01;
                    case 1 -> OrePrefixes.wireGt02;
                    case 2 -> OrePrefixes.wireGt04;
                    default -> OrePrefixes.wireGt08;
                };
                GTRecipeBuilder.builder()
                    .itemInputs(
                        ItemUtils.HULL[j + 4].get(1),
                        GTUtility
                            .copyAmountUnsafe(1 << i, GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 1)),
                        GTUtility.copyAmountUnsafe(1 << i, ItemUtils.SENSOR[j + 4].get(1)),
                        GTUtility.copyAmountUnsafe(1 << i, ItemUtils.ELECTRIC_PUMP[j + 4].get(1)),
                        GTUtility
                            .copyAmountUnsafe(quantity, GTOreDictUnificator.get(prefix, ItemUtils.CABLE[j + 4], 1)),
                        GTUtility.getIntegratedCircuit(i + 1))
                    .itemOutputs(GTNCItemList.LASER_ENERGY_HATCH[j][i].get(1))
                    .fluidInputs(Materials.SolderingAlloy.getMolten(144))
                    .duration(50 << i)
                    .eut(GTValues.VP[j + 5])
                    .addTo(RecipeMaps.assemblerRecipes);

            }

        }
    }

}
