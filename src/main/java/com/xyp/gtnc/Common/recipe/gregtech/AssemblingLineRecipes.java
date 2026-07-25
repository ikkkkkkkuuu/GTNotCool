package com.xyp.gtnc.Common.recipe.gregtech;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.item.ItemUtils;

import appeng.api.AEApi;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsElements;
import tectech.recipe.TTRecipeAdder;

public class AssemblingLineRecipes {

    public static void loadrecipes() {
        IRecipeMap AL = GTRecipeConstants.AssemblyLine;

        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();

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

        GTRecipeBuilder.builder()
            .metadata(
                GTRecipeConstants.RESEARCH_ITEM,
                GTModHandler.getModItem(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 1))
            .metadata(GTRecipeConstants.SCANNING, new Scanning(20 * GTRecipeBuilder.MINUTES, TierEU.RECIPE_UHV))
            .itemInputs(
                GTModHandler.getModItem(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 64),
                GTModHandler.getModItem(Mods.ThaumicEnergistics.ID, "thaumicenergistics.block.arcane.assembler", 64),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockStoneDevice", 64, 2),
                GTModHandler.getModItem(Mods.Thaumcraft.ID, "blockStoneDevice", 64, 2),
                ItemUtils.getItemStack(
                    Mods.Thaumcraft.ID,
                    "WandCasting",
                    1,
                    9000,
                    "{cap:\"matrix\",rod:\"infinity\",aer:999999900,aqua:999999900,ignis:999999900,ordo:999999900,perditio:999999900,terra:999999900}",
                    null),
                GTModHandler.getModItem(Mods.Avaritia.ID, "Akashic_Record", 1),
                new Object[] { OrePrefixes.circuit.get(Materials.UIV), 16 },
                ItemList.Robot_Arm_UEV.get(32),
                ItemList.Field_Generator_UEV.get(16),
                MaterialsElements.STANDALONE.HYPOGEN.getPlateDense(32),
                GTModHandler.getModItem(Mods.EternalSingularity.ID, "eternal_singularity", 8),
                ItemList.EnergisedTesseract.get(8),
                GTModHandler.getModItem(Mods.WitchingGadgets.ID, "item.WG_Material", 1, 7),
                aeMaterials.cardSuperSpeed()
                    .maybeStack(64)
                    .orNull())
            .fluidInputs(
                Materials.ExcitedDTEC.getFluid(64000),
                Materials.StableBaryonicMatter.getFluid(64000),
                MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(64000))
            .itemOutputs(GTNCItemList.IndustrialArcaneAssembler.get(1))
            .eut(TierEU.RECIPE_UIV)
            .duration(300 * GTRecipeBuilder.SECONDS)
            .addTo(AL);

    }

}
