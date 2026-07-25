package com.xyp.gtnc.Common.recipe.gregtech;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import bartworks.system.material.CircuitGeneration.CircuitPartsItem;
import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.SubstituteFluidStack;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;

public class CircuitAssemblerRecipes {

    public static void loadRecipes() {
        RecipeMap<?> CAR = RecipeMaps.circuitAssemblerRecipes;

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Battery_RE_ULV_Tantalum.get(4L),
                ItemList.Circuit_Parts_Wiring_Basic.get(4L),
                ItemList.Circuit_Parts_Coil.get(4L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gem, 1),
                ItemList.Circuit_Primitive.get(1L))
            .fluidInputs(SubstituteFluidStack.soldering(72))
            .itemOutputs(GTNCItemList.CircuitResonaticULV.get(4))
            .duration(50)
            .eut(30)
            .requiresCleanRoom()
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_Advanced.get(4L),
                ItemList.Circuit_Parts_Wiring_Elite.get(4L),
                ItemList.Circuit_Parts_Wiring_Advanced.get(4L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gem, 1),
                GTNCItemList.CircuitResonaticULV.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(72))
            .itemOutputs(GTNCItemList.CircuitResonaticLV.get(4))
            .duration(90)
            .eut(120)
            .requiresCleanRoom()
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_Advanced.get(8L),
                ItemList.Circuit_Parts_Wiring_Elite.get(8L),
                ItemList.Circuit_Parts_Wiring_Advanced.get(8L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gem, 1),
                GTNCItemList.CircuitResonaticLV.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(72))
            .itemOutputs(GTNCItemList.CircuitResonaticMV.get(4))
            .duration(150)
            .eut(480)
            .requiresCleanRoom()
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeSMD.get(4L),
                ItemList.Circuit_Parts_TransistorSMD.get(4L),
                ItemList.Circuit_Parts_CapacitorSMD.get(4L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3, 2),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNCItemList.CircuitResonaticMV.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(144))
            .itemOutputs(GTNCItemList.CircuitResonaticHV.get(4))
            .duration(230)
            .eut(1920)
            .requiresCleanRoom()
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeSMD.get(8L),
                ItemList.Circuit_Parts_TransistorSMD.get(8L),
                ItemList.Circuit_Parts_CapacitorSMD.get(8L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3, 4),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNCItemList.CircuitResonaticHV.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(144))
            .itemOutputs(GTNCItemList.CircuitResonaticEV.get(4))
            .duration(330)
            .eut(7680)
            .requiresCleanRoom()
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeASMD.get(4L),
                ItemList.Circuit_Parts_TransistorASMD.get(4L),
                ItemList.Circuit_Parts_CapacitorASMD.get(4L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3, 4),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNCItemList.CircuitResonaticEV.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(144))
            .itemOutputs(GTNCItemList.CircuitResonaticIV.get(4))
            .duration(450)
            .eut(30720)
            .requiresCleanRoom()
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeASMD.get(8L),
                ItemList.Circuit_Parts_TransistorASMD.get(8L),
                ItemList.Circuit_Parts_CapacitorASMD.get(8L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3, 4),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemFlawless, 1),
                GTNCItemList.CircuitResonaticIV.get(1))
            .fluidInputs(SubstituteFluidStack.soldering(144))
            .itemOutputs(GTNCItemList.CircuitResonaticLuV.get(4))
            .duration(570)
            .eut(122880)
            .requiresCleanRoom()
            .addTo(CAR);

        // GTRecipeBuilder.builder()
        // .itemInputs(
        // GTNCItemList.BiowareSMDDiode.get(16),
        // GTNCItemList.BiowareSMDCapacitor.get(16),
        // GTNCItemList.BiowareSMDTransistor.get(16),
        // CircuitPartsItem.getCircuitParts()
        // .getStack(3, 8),
        // WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
        // GTNCItemList.CircuitResonaticLuV.get(1))
        // .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(144))
        // .itemOutputs(GTNCItemList.CircuitResonaticZPM.get(4))
        // .duration(710)
        // .eut(491520)
        // .requiresCleanRoom()
        // .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Parts_DiodeXSMD.get(16L),
                ItemList.Circuit_Parts_TransistorXSMD.get(16L),
                ItemList.Circuit_Parts_CapacitorXSMD.get(16L),
                CircuitPartsItem.getCircuitParts()
                    .getStack(3, 8),
                WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
                GTNCItemList.CircuitResonaticZPM.get(1))
            .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(288))
            .itemOutputs(GTNCItemList.CircuitResonaticUV.get(4))
            .duration(730)
            .eut(1966080)
            .requiresCleanRoom()
            .addTo(CAR);

        // GTRecipeBuilder.builder()
        // .itemInputs(
        // GTNCItemList.ExoticSMDDiode.get(16),
        // GTNCItemList.ExoticSMDCapacitor.get(16),
        // GTNCItemList.ExoticSMDTransistor.get(16),
        // CircuitPartsItem.getCircuitParts()
        // .getStack(3, 8),
        // WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
        // GTNCItemList.CircuitResonaticUV.get(1))
        // .fluidInputs(MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(432))
        // .itemOutputs(GTNCItemList.CircuitResonaticUHV.get(4))
        // .duration(750)
        // .eut(7864320)
        // .requiresCleanRoom()
        // .addTo(CAR);
        //
        // GTRecipeBuilder.builder()
        // .itemInputs(
        // GTNCItemList.CosmicSMDDiode.get(16),
        // GTNCItemList.CosmicSMDCapacitor.get(16),
        // GTNCItemList.CosmicSMDTransistor.get(16),
        // CircuitPartsItem.getCircuitParts()
        // .getStack(3, 8),
        // WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
        // GTNCItemList.CircuitResonaticUHV.get(1))
        // .fluidInputs(GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(288))
        // .itemOutputs(GTNCItemList.CircuitResonaticUEV.get(4))
        // .duration(770)
        // .eut(31457280)
        // .requiresCleanRoom()
        // .addTo(CAR);
        //
        // GTRecipeBuilder.builder()
        // .itemInputs(
        // GTNCItemList.TemporallySMDDiode.get(16),
        // GTNCItemList.TemporallySMDCapacitor.get(16),
        // GTNCItemList.TemporallySMDTransistor.get(16),
        // CircuitPartsItem.getCircuitParts()
        // .getStack(3, 8),
        // WerkstoffLoader.MagnetoResonaticDust.get(OrePrefixes.gemExquisite, 1),
        // GTNCItemList.CircuitResonaticUEV.get(1))
        // .fluidInputs(GTNLMaterials.SuperMutatedLivingSolder.getFluidOrGas(432))
        // .itemOutputs(GTNCItemList.CircuitResonaticUIV.get(4))
        // .duration(790)
        // .eut(125829120)
        // .requiresCleanRoom()
        // .addTo(CAR);
        //

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Primitive.get(0),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                GTModHandler.getModItem(Mods.Minecraft.ID, "redstone", 1, 0))
            .fluidInputs(Materials.Glue.getFluid(20))
            .itemOutputs(GTNCItemList.VerySimpleCircuit.get(2))
            .duration(40)
            .eut(7)
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemPartCircuit", 0),
                GTNCItemList.VerySimpleCircuit.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.RedAlloy, 1))
            .fluidInputs(Materials.Glue.getFluid(20))
            .itemOutputs(GTNCItemList.SimpleCircuit.get(2))
            .duration(80)
            .eut(16)
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, ItemList.Circuit_Good.get(1L)),
                GTModHandler.getModItem(Mods.Minecraft.ID, "paper", 1, 0),
                GTNCItemList.SimpleCircuit.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CastIron, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Diamond, 1))
            .fluidInputs(Materials.Glue.getFluid(20))
            .itemOutputs(GTNCItemList.BasicCircuit.get(2))
            .duration(160)
            .eut(30)
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemPartCircuitAdv", 0),
                ItemList.Circuit_Board_Coated_Basic.get(1L),
                GTNCItemList.BasicCircuit.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.dustSmall, Materials.Obsidian, 1),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.RedAlloy, 1))
            .fluidInputs(Materials.Glue.getFluid(20))
            .itemOutputs(GTNCItemList.AdvancedCircuit.get(1))
            .duration(80)
            .eut(120)
            .addTo(CAR);

        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Circuit_Board_Phenolic_Good.get(1L),
                GTNCItemList.AdvancedCircuit.get(1),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.RedAlloy, 8))
            .fluidInputs(SubstituteFluidStack.soldering(144))
            .itemOutputs(GTNCItemList.EliteCircuit.get(1))
            .duration(200)
            .eut(480)
            .addTo(CAR);

    }

}
