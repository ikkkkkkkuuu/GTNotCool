package com.xyp.gtnc.Common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.item.ItemStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class AssemblerRecipes {

    public static void loadRecipes() {
        RecipeMap<?> As = RecipeMaps.assemblerRecipes;

        // 蒸汽蜜蜂
        GTRecipeBuilder.builder()
            .itemInputs(
                GTNCItemList.LargeSteamAssembler.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(GTNCItemList.LargeSteamBeeBreeder.get(1))
            .duration(100)
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
            .duration(100)
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
            .duration(100)
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
            .duration(100)
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
            .duration(200)
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
            .duration(200)
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
            .duration(200)
            .eut(30)
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
            .fluidInputs(Materials.Lubricant.getFluid(1 * INGOTS))
            .duration(10 * SECONDS)
            .eut(32);

        // ME输出总线
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Output_Bus_MV.get(1L),
                GTModHandler.getModItem("appliedenergistics2", "tile.BlockInterface", 1),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemMultiMaterial", 2, 27))
            .itemOutputs(ItemList.Hatch_Output_Bus_ME.get(1L))
            .duration(6 * SECONDS)
            .eut(128)
            .addTo(As);
        // ME输出仓
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hatch_Output_MV.get(1L),
                GTModHandler.getModItem("ae2fc", "fluid_interface", 1),
                GTModHandler.getModItem("appliedenergistics2", "item.ItemMultiMaterial", 2, 27))
            .itemOutputs(ItemList.Hatch_Output_ME.get(1L))
            .duration(6 * SECONDS)
            .eut(128)
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
            .eut(32)
            .addTo(As);
        // AE2 流体接口
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WroughtIron, 4),
                GTOreDictUnificator.get(OrePrefixes.wireGt04, Materials.RedAlloy, 2),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.GreenSapphire, 1),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Sapphire, 1),
                ItemList.Casing_LV.get(1))
            .circuit(14)
            .itemOutputs(GTModHandler.getModItem("ae2fc", "fluid_interface", 1))
            .duration(5 * SECONDS)
            .eut(32)
            .addTo(As);
        // SMD inductor贴片电感
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.NaquadahAlloy, 8),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.NaquadahAlloy, 1))
            .fluidInputs(Materials.Lubricant.getFluid(1296))
            .itemOutputs(GTNCItemList.BiowareSMDInductor.get(32))
            .duration(20)
            .eut(2048)
            .addTo(As);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Machine_LV_Electrolyzer.get(1L),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2),
                GTUtility.getIntegratedCircuit(19))
            .itemOutputs(GTNCItemList.LargeSteamElectrolyzer.get(1))
            .duration(100)
            .eut(32)
            .addTo(As);
        loadcovers();
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
