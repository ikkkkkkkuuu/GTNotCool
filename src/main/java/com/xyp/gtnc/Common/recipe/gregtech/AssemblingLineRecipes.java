package com.xyp.gtnc.Common.recipe.gregtech;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import tectech.recipe.TTRecipeAdder;

public class AssemblingLineRecipes {

    public static void loadrecipes() {
        IRecipeMap AL = GTRecipeConstants.AssemblyLine;

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
