package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.material.GTNCMaterials;
import com.xyp.gtnc.Loader.ItemsLoader;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import appeng.api.AEApi;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CraftingTableRecipes {

    public static void loadRecipes() {
        // spotless:off
        // 通配样板符配方：空白样板 → 通配样板符
        GameRegistry.addRecipe(
            new ItemStack(ItemsLoader.wildcardPattern),
            "B", 'B', AEApi.instance()
                .definitions()
                .materials()
                .blankPattern()
                .maybeStack(1)
                .orNull());

        // 诱变框架配方：验证框架(Forestry) 中心 + 四周高算力芯片I
        GameRegistry.addRecipe(
            new ItemStack(ItemsLoader.mutagenicFrame),
            " A ", "ABA", " A ",
            'A', GTNCMaterials.CompressedSteam.get(OrePrefixes.gearGt, 1),
            'B', GameRegistry.findItemStack("Forestry", "frameProven", 1));

        // 无尽框架配方：诱变框架中心 + 四周高算力芯片III
        GameRegistry.addRecipe(
            new ItemStack(ItemsLoader.endlessFrame),
            " A ", "ABA", " A ",
            'A', GTNCMaterials.CompressedSteam.get(OrePrefixes.gearGt, 1),
            'B', new ItemStack(ItemsLoader.mutagenicFrame));

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamEyeOfHarmony.get(1),
            new Object[] { "ABA", "BCB", "ABA",
                'A', new ItemStack(Blocks.brick_block),
                'B', "plankWood",
                'C', GTNCItemList.ChipTier1.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorLV.get(1),
            new Object[] { "ABA", "CDC", "EFE",
                'A', ItemList.Electric_Piston_LV.get(1),
                'B', OrePrefixes.circuit.get(Materials.LV),
                'C', GregtechItemList.GTFluidTank_LV.get(1),
                'D', ItemList.Hull_LV.get(1),
                'E', ItemList.Electric_Pump_LV,
                'F', GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Tin, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorMV.get(1),
            new Object[] { "ABA", "CDC", "EFE",
                'A', ItemList.Electric_Piston_MV.get(1),
                'B', OrePrefixes.circuit.get(Materials.MV),
                'C', GregtechItemList.GTFluidTank_MV.get(1),
                'D', ItemList.Hull_MV.get(1),
                'E', ItemList.Electric_Pump_MV,
                'F', OrePrefixes.cableGt08.get(Materials.AnyCopper)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorHV.get(1),
            new Object[] { "ABA", "CDC", "EFE",
                'A', ItemList.Electric_Piston_HV.get(1),
                'B', OrePrefixes.circuit.get(Materials.HV),
                'C', GregtechItemList.GTFluidTank_HV.get(1),
                'D', ItemList.Hull_HV.get(1),
                'E', ItemList.Electric_Pump_HV,
                'F', GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Gold, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.DieselGeneratorEV.get(1),
            new Object[] { "ABA", "CDC", "EFE",
                'A', ItemList.Electric_Piston_EV.get(1),
                'B', OrePrefixes.circuit.get(Materials.EV),
                'C', GregtechItemList.GTFluidTank_HV.get(1),
                'D', ItemList.Hull_EV.get(1),
                'E', ItemList.Electric_Pump_EV,
                'F', GTOreDictUnificator.get(OrePrefixes.cableGt08, Materials.Gold, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineLV.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', ItemList.Electric_Pump_LV.get(1),
                'B', OrePrefixes.circuit.get(Materials.LV),
                'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L),
                'D', ItemList.Hull_LV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Tin, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineMV.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', ItemList.Electric_Pump_MV.get(1),
                'B', OrePrefixes.circuit.get(Materials.MV),
                'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Aluminium, 1L),
                'D', ItemList.Hull_MV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.AnnealedCopper, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineHV.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', ItemList.Electric_Pump_HV.get(1),
                'B', OrePrefixes.circuit.get(Materials.HV),
                'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1L),
                'D', ItemList.Hull_HV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Gold, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineEV.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', ItemList.Electric_Pump_EV.get(1),
                'B', OrePrefixes.circuit.get(Materials.EV),
                'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.StainlessSteel, 1L),
                'D', ItemList.Hull_EV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Gold, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineIV.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', ItemList.Electric_Pump_IV.get(1),
                'B', OrePrefixes.circuit.get(Materials.IV),
                'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L),
                'D', ItemList.Hull_IV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.Tungsten, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.SteamTurbineLuV.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', ItemList.Electric_Pump_LuV.get(1),
                'B', OrePrefixes.circuit.get(Materials.LuV),
                'C', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Chrome, 1L),
                'D', ItemList.Hull_LuV.get(1),
                'E', GTOreDictUnificator.get(OrePrefixes.cableGt16, Materials.NiobiumTitanium, 1L)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerBronze.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Tin, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1L),
                'C', OrePrefixes.circuit.get(Materials.LV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1L),
                'E', ItemList.Casing_Firebox_Bronze.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Copper, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L),
                'C', OrePrefixes.circuit.get(Materials.MV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L),
                'E', ItemList.Casing_Firebox_Steel.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerTitanium.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Gold, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 1L),
                'C', OrePrefixes.circuit.get(Materials.HV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1L),
                'E', ItemList.Casing_Firebox_Titanium.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeBoilerTungstenSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt01, Materials.Aluminium, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L),
                'C', OrePrefixes.circuit.get(Materials.EV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L),
                'E', ItemList.Casing_Firebox_TungstenSteel.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineBronze.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Tin, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Bronze, 1L),
                'C', OrePrefixes.circuit.get(Materials.LV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Bronze, 1L),
                'E', ItemList.Casing_Firebox_Bronze.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Copper, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1L),
                'C', OrePrefixes.circuit.get(Materials.MV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L),
                'E', ItemList.Casing_Firebox_Steel.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineTitanium.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Gold, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Titanium, 1L),
                'C', OrePrefixes.circuit.get(Materials.HV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 1L),
                'E', ItemList.Casing_Firebox_Titanium.get(1)
            });

        GTModHandler.addCraftingRecipe(
            GTNCItemList.LargeSteamTurbineTungstenSteel.get(1),
            new Object[] { "ABA", "CDC", "AEA",
                'A', GTOreDictUnificator.get(OrePrefixes.cableGt02, Materials.Aluminium, 1L),
                'B', GTOreDictUnificator.get(OrePrefixes.rotor, Materials.TungstenSteel, 1L),
                'C', OrePrefixes.circuit.get(Materials.EV),
                'D', GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 1L),
                'E', ItemList.Casing_Firebox_TungstenSteel.get(1)
            });

        // spotless:on

        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamAssemblerModule.get(1),
            ItemList.Machine_LV_Assembler.get(1),
            ItemList.Machine_LV_CircuitAssembler.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamFormingModule.get(1),
            ItemList.Machine_LV_FluidSolidifier.get(1),
            ItemList.Machine_LV_Press.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamProcessingModule.get(1),
            ItemList.Machine_LV_Centrifuge.get(1),
            ItemList.Machine_LV_Electrolyzer.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamIndustrialModule.get(1),
            ItemList.Machine_LV_Macerator.get(1),
            ItemList.Machine_LV_ChemicalReactor.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamSmeltingModule.get(1),
            ItemList.Machine_LV_E_Furnace.get(1),
            ItemList.Machine_LV_ArcFurnace.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamMoltenModule.get(1),
            ItemList.Machine_LV_FluidExtractor.get(1),
            ItemList.Machine_LV_FluidSolidifier.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamPlasmaModule.get(1),
            ItemList.Machine_LV_PlasmaArcFurnace.get(1),
            ItemList.Machine_LV_Electrolyzer.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamAlloyBlastSmelterModule.get(1),
            ItemList.Machine_LV_AlloySmelter.get(1),
            ItemList.Machine_LV_Mixer.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamAlloySmelterModule.get(1),
            ItemList.Machine_LV_AlloySmelter.get(1),
            ItemList.Machine_LV_AlloySmelter.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamExtractorModule.get(1),
            ItemList.Machine_LV_Extractor.get(1),
            ItemList.Machine_LV_FluidExtractor.get(1));
        addSteamElevatorModuleRecipe(
            GTNCItemList.SteamPrecisionProcessingModule.get(1),
            ItemList.Machine_LV_LaserEngraver.get(1),
            ItemList.Machine_LV_Wiremill.get(1));

    }

    private static void addSteamElevatorModuleRecipe(ItemStack output, ItemStack leftMachine, ItemStack rightMachine) {
        GTModHandler.addCraftingRecipe(
            output,
            new Object[] { "PFP", "LCR", "PFP", 'P',
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.Steel, 1L), 'F',
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 1L), 'L', leftMachine, 'C',
                new ItemStack(GregTechAPI.sBlockCasings2, 1, 0), 'R', rightMachine });
    }

}
