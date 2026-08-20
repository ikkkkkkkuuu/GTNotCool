package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator.SteamElevatorModuleGui;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;

/** Five-mode industrial module connected to a Steam Elevator controller. */
public final class SteamIndustrialModule extends SteamElevatorModuleBase {

    private static final int MODE_MACERATOR = 0;
    private static final int MODE_CHEMICAL_REACTOR = 1;
    private static final int MODE_CIRCUIT_ASSEMBLER = 2;
    private static final int MODE_CHEMICAL_BATH = 3;
    private static final int MODE_MIXER = 4;
    private static final int MODE_COUNT = 5;
    private static final int MODULE_TIER = 14;
    private static final List<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections.unmodifiableList(
        Arrays.asList(
            RecipeMaps.maceratorRecipes,
            RecipeMaps.multiblockChemicalReactorRecipes,
            RecipeMaps.circuitAssemblerRecipes,
            RecipeMaps.chemicalBathRecipes,
            RecipeMaps.mixerRecipes));

    public SteamIndustrialModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamIndustrialModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamIndustrialModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MODE_CHEMICAL_REACTOR -> RecipeMaps.multiblockChemicalReactorRecipes;
            case MODE_CIRCUIT_ASSEMBLER -> RecipeMaps.circuitAssemblerRecipes;
            case MODE_CHEMICAL_BATH -> RecipeMaps.chemicalBathRecipes;
            case MODE_MIXER -> RecipeMaps.mixerRecipes;
            default -> RecipeMaps.maceratorRecipes;
        };
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return AVAILABLE_RECIPE_MAPS;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        return (machineMode + 1) % MODE_COUNT;
    }

    @Override
    public void setMachineMode(int mode) {
        super.setMachineMode(mode >= 0 && mode < MODE_COUNT ? mode : MODE_MACERATOR);
        refreshInputRecipeMaps();
    }

    @Override
    public String getMachineModeName() {
        return switch (machineMode) {
            case MODE_CHEMICAL_REACTOR -> TextLocalization.SteamIndustrialModuleModeChemicalReactor;
            case MODE_CIRCUIT_ASSEMBLER -> TextLocalization.SteamIndustrialModuleModeCircuitAssembler;
            case MODE_CHEMICAL_BATH -> TextLocalization.SteamIndustrialModuleModeChemicalBath;
            case MODE_MIXER -> TextLocalization.SteamIndustrialModuleModeMixer;
            default -> TextLocalization.SteamIndustrialModuleModeMacerator;
        };
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        setMachineMode(machineMode);
    }

    private void refreshInputRecipeMaps() {
        RecipeMap<?> recipeMap = getRecipeMap();
        for (MTEHatchInputBus hatch : GTUtility.validMTEList(mInputBusses)) {
            hatch.mRecipeMap = recipeMap;
            if (hatch instanceof SuperMTEHatchCraftingInputME craftingInput) {
                craftingInput.setControllerRecipeMap(recipeMap);
            }
        }
        for (IDualInputHatch hatch : mDualInputHatches) {
            if (hatch instanceof SuperMTEHatchCraftingInputME craftingInput) {
                craftingInput.setControllerRecipeMap(recipeMap);
            }
        }
        for (MTEHatchSteamBusInput hatch : GTUtility.validMTEList(mSteamInputs)) hatch.mRecipeMap = recipeMap;
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) hatch.mRecipeMap = recipeMap;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.SteamIndustrialModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamIndustrialModule_00)
            .addInfo(TextLocalization.Tooltip_SteamIndustrialModule_01)
            .addInfo(TextLocalization.Tooltip_SteamIndustrialModule_02)
            .addInfo(TextLocalization.Tooltip_SteamIndustrialModule_03)
            .addInfo(TextLocalization.Tooltip_SteamIndustrialModule_04)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamIndustrialModule_Controller)
            .addSteamInputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addSteamOutputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addInputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addInputHatch(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addOutputHatch(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addMaintenanceHatch(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .toolTipFinisher();
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new SteamElevatorModuleGui(this).withMachineModeIcons(
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_MACERATOR,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CHEMICAL_REACTOR,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CIRCUIT_ASSEMBLER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CHEMICAL_BATH,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_MIXER);
    }
}
