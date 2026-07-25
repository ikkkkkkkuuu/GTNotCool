package com.xyp.gtnc.Common.recipe.machine;

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
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsElements;

public class IndustrialArcaneAssembler {

    public static void loadRecipes() {
        IRecipeMap AL = GTRecipeConstants.AssemblyLine;

        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();

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
