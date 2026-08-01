package com.xyp.gtnc.Common.recipe.gregtech;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import bartworks.common.loaders.ItemRegistry;
import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.objects.SubstituteFluidStack;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import tectech.recipe.TTRecipeAdder;

public class AssemblingLineRecipes {

    public static void loadrecipes() {
        IRecipeMap AL = GTRecipeConstants.AssemblyLine;

        GTValues.RA.stdBuilder()
            .metadata(GTRecipeConstants.RESEARCH_ITEM, ItemRegistry.megaMachines[4])
            .metadata(GTRecipeConstants.SCANNING, new Scanning(30 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_IV))
            .itemInputs(
                ItemList.MixerLuV.get(4),
                ItemList.CentrifugeLuV.get(4),
                ItemList.DistilleryLuV.get(4),
                ItemList.ChemicalReactorLuV.get(4),
                GTOreDictUnificator.get(OrePrefixes.pipeNonuple, Materials.TungstenSteel, 8),
                ItemList.Emitter_LuV.get(4),
                new Object[] { OrePrefixes.circuit.get(Materials.ZPM), 4 },
                ItemList.Electric_Piston_LuV.get(8),
                GTOreDictUnificator.get(OrePrefixes.plateDouble, Materials.HSSE, 16))
            .fluidInputs(
                SubstituteFluidStack.soldering(2304),
                Materials.Polytetrafluoroethylene.getMolten(2304),
                MaterialsAlloy.AQUATIC_STEEL.getFluidStack(1152))
            .itemOutputs(GTNCItemList.FuelRefiningComplex.get(1))
            .eut(TierEU.RECIPE_ZPM)
            .duration(50 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            GTNCItemList.FuelRefiningComplex.get(1),
            32768000,
            4096,
            (int) TierEU.RECIPE_UIV,
            16,
            new Object[] { GTNCItemList.FuelRefiningComplex.get(4), ItemList.Neutronium_Active_Casing.get(64),
                new ItemStack(WerkstoffLoader.BWBlockCasings, 64, 31_766 + 129),
                new ItemStack(WerkstoffLoader.BWBlockCasingsAdvanced, 64, 31_766 + 129),
                ItemList.Electric_Pump_UEV.get(16), ItemList.Field_Generator_UEV.get(8), ItemRefer.HiC_T5.get(32),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 32 }, GregtechItemList.Laser_Lens_Special.get(4),
                Mods.EternalSingularity.isModLoaded()
                    ? GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 2)
                    : new ItemStack(Items.feather),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.nanite, 16),
                GTOreDictUnificator.get(OrePrefixes.wireGt08, Materials.SuperconductorUEV, 32),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Infinity, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.CosmicNeutronium, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Netherite, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Bedrockium, 4) },
            new FluidStack[] { MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(128000),
                Materials.Lubricant.getFluid(128000), MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(23040),
                Materials.Naquadria.getMolten(46080) },
            GTNCItemList.AtomicEnergyExcitationPlant.get(1),
            4000,
            (int) TierEU.RECIPE_UIV);

        TTRecipeAdder.addResearchableAssemblylineRecipe(
            ItemList.SpaceElevatorController.get(1),
            96000,
            256,
            (int) TierEU.RECIPE_UEV,
            1,
            new Object[] { ItemList.SpaceElevatorController.get(2), ItemList.SpaceElevatorController.get(2),
                ItemList.SpaceElevatorController.get(2), ItemList.SpaceElevatorController.get(2),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TranscendentMetal, 32),
                ItemList.Field_Generator_UEV.get(32), new Object[] { OrePrefixes.circuit.get(Materials.UHV), 64 },
                new Object[] { OrePrefixes.circuit.get(Materials.UEV), 32 }, ItemList.Circuit_Chip_QPIC.get(64),
                Mods.GalacticraftAmunRa.isModLoaded()
                    ? GTModHandler.getModItem(Mods.GalacticraftAmunRa.ID, "item.baseItem", 64, 15)
                    : new ItemStack(Items.feather),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.Infinity, 64),
                ItemList.SpaceElevatorBaseCasing.get(64) },
            new FluidStack[] { Materials.Tungsten.getMolten(4000), Materials.MoltenProtoHalkoniteBase.getFluid(8000),
                Materials.DimensionallyShiftedSuperfluid.getFluid(16000), Materials.Infinity.getMolten(4608) },
            GTNCItemList.SuperSpaceElevator.get(1),
            9000,
            (int) TierEU.RECIPE_UEV);

    }

}
