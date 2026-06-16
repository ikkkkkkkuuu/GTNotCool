package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.common.BeltUpgradeRecipe;
import com.xyp.gtnc.Loader.ItemsLoader;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import appeng.api.AEApi;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CraftingTableRecipes {

    public static void loadRecipes() {
        // 合成工具腰带(Tool Belt)的配方
        // 配方结构: LLL
        // L L
        // LIL
        // 其中: L = 皮革, I = 铁锭
        GameRegistry.addRecipe(
            new ItemStack(ItemsLoader.toolBelt),
            new Object[] { "LLL", "L L", "LIL", 'L', Items.leather, 'I', Items.iron_ingot });

        // 合成小袋(Pouch)的配方
        // 配方结构: LLL
        // L L
        // LLL
        // 其中: L = 皮革
        GameRegistry
            .addRecipe(new ItemStack(ItemsLoader.pouch), new Object[] { "LLL", "L L", "LLL", 'L', Items.leather });

        // 腰带升级配方（皮革环绕）
        // 使用自定义配方类实现容量+1的效果
        GameRegistry.addRecipe(new BeltUpgradeRecipe());

        // 通配样板符配方：空白样板 → 通配样板符
        GameRegistry.addRecipe(
            new ItemStack(ItemsLoader.wildcardPattern),
            new Object[] { "B", 'B', AEApi.instance()
                .definitions()
                .materials()
                .blankPattern()
                .maybeStack(1)
                .orNull() });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorLV.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', ItemList.Electric_Piston_LV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', GregtechItemList.GTFluidTank_LV.get(1), 'D',
                ItemList.Hull_LV.get(1), 'E', ItemList.Electric_Pump_LV, 'F',
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Tin, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorMV.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', ItemList.Electric_Piston_MV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.MV), 'C', GregtechItemList.GTFluidTank_MV.get(1), 'D',
                ItemList.Hull_MV.get(1), 'E', ItemList.Electric_Pump_MV, 'F',
                OrePrefixes.cableGt08.get(Materials.AnyCopper) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorHV.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', ItemList.Electric_Piston_HV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.HV), 'C', GregtechItemList.GTFluidTank_HV.get(1), 'D',
                ItemList.Hull_HV.get(1), 'E', ItemList.Electric_Pump_HV, 'F',
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Gold, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorEV.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', ItemList.Electric_Piston_EV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.EV), 'C', GregtechItemList.GTFluidTank_HV.get(1), 'D',
                ItemList.Hull_EV.get(1), 'E', ItemList.Electric_Pump_EV, 'F',
                GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Gold, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineLV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_LV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'D', ItemList.Hull_LV.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Tin, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineMV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_MV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.MV), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Aluminium, 1L), 'D', ItemList.Hull_MV.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.AnnealedCopper, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineHV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_HV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.HV), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1L), 'D', ItemList.Hull_HV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Gold, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineEV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_EV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.EV), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1L), 'D', ItemList.Hull_EV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Gold, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineIV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_IV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.IV), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L), 'D', ItemList.Hull_IV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Tungsten, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineLuV.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', ItemList.Electric_Pump_LuV.get(1), 'B',
                OrePrefixes.circuit.get(Materials.LuV), 'C',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Chrome, 1L), 'D', ItemList.Hull_LuV.get(1), 'E',
                GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.NiobiumTitanium, 1L) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerBronze.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Tin, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1L), 'C',
                OrePrefixes.circuit.get(Materials.LV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1L), 'E',
                ItemList.Casing_Firebox_Bronze.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Copper, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'C',
                OrePrefixes.circuit.get(Materials.MV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L), 'E',
                ItemList.Casing_Firebox_Steel.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerTitanium.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 1L), 'C',
                OrePrefixes.circuit.get(Materials.HV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1L), 'E',
                ItemList.Casing_Firebox_Titanium.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerTungstenSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L), 'C',
                OrePrefixes.circuit.get(Materials.EV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L), 'E',
                ItemList.Casing_Firebox_TungstenSteel.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineBronze.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Tin, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1L), 'C',
                OrePrefixes.circuit.get(Materials.LV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1L), 'E',
                ItemList.Casing_Firebox_Bronze.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Copper, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L), 'C',
                OrePrefixes.circuit.get(Materials.MV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L), 'E',
                ItemList.Casing_Firebox_Steel.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineTitanium.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A', GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Gold, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 1L), 'C',
                OrePrefixes.circuit.get(Materials.HV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1L), 'E',
                ItemList.Casing_Firebox_Titanium.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineTungstenSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA", 'A',
                GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Aluminium, 1L), 'B',
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L), 'C',
                OrePrefixes.circuit.get(Materials.EV), 'D',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L), 'E',
                ItemList.Casing_Firebox_TungstenSteel.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamLaserEngraver.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_LaserEngraver.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamAssembler.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Assembler.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamCentrifuge.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Centrifuge.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamBending.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Bender.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamFluidExtractor.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_FluidExtractor.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamFluidSolidifier.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_FluidSolidifier.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamChemicalReactor.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_ChemicalReactor.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamWireMill.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Wiremill.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamMixer.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Mixer.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamAlloySmelter.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_AlloySmelter.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamCircuitAssembler.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_CircuitAssembler.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamCompressor.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Compressor.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamCutting.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Cutter.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamElectrolyzer.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Electrolyzer.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamFormingPress.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Press.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamHammer.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Hammer.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamExtruder.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Extruder.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamDistillationTower.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.Machine_LV_Distillery.get(1) });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamPyrolyseOven.get(1),
            new Object[] { "ABA", "BCB", "ABA", 'A',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Bronze, 1), 'B',
                OrePrefixes.circuit.get(Materials.LV), 'C', ItemList.PyrolyseOven.get(1) });

    }

}
