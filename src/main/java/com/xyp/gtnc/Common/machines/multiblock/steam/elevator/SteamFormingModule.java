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

/** Six-mode solid-forming processor connected to a Steam Elevator controller. */
public final class SteamFormingModule extends SteamElevatorModuleBase {

    private static final int MODE_FLUID_SOLIDIFIER = 0;
    private static final int MODE_EXTRUDER = 1;
    private static final int MODE_LATHE = 2;
    private static final int MODE_COMPRESSOR = 3;
    private static final int MODE_STAMPING_MACHINE = 4;
    private static final int MODE_HAMMER = 5;
    private static final int MODE_COUNT = 6;
    private static final int MODULE_TIER = 14;
    private static final List<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections.unmodifiableList(
        Arrays.asList(
            RecipeMaps.fluidSolidifierRecipes,
            RecipeMaps.extruderRecipes,
            RecipeMaps.latheRecipes,
            RecipeMaps.compressorRecipes,
            RecipeMaps.formingPressRecipes,
            RecipeMaps.hammerRecipes));

    public SteamFormingModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamFormingModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamFormingModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MODE_EXTRUDER -> RecipeMaps.extruderRecipes;
            case MODE_LATHE -> RecipeMaps.latheRecipes;
            case MODE_COMPRESSOR -> RecipeMaps.compressorRecipes;
            case MODE_STAMPING_MACHINE -> RecipeMaps.formingPressRecipes;
            case MODE_HAMMER -> RecipeMaps.hammerRecipes;
            default -> RecipeMaps.fluidSolidifierRecipes;
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
        super.setMachineMode(mode >= 0 && mode < MODE_COUNT ? mode : MODE_FLUID_SOLIDIFIER);
        refreshInputRecipeMaps();
    }

    @Override
    public String getMachineModeName() {
        return switch (machineMode) {
            case MODE_EXTRUDER -> TextLocalization.SteamFormingModuleModeExtruder;
            case MODE_LATHE -> TextLocalization.SteamFormingModuleModeLathe;
            case MODE_COMPRESSOR -> TextLocalization.SteamFormingModuleModeCompressor;
            case MODE_STAMPING_MACHINE -> TextLocalization.SteamFormingModuleModeStampingMachine;
            case MODE_HAMMER -> TextLocalization.SteamFormingModuleModeHammer;
            default -> TextLocalization.SteamFormingModuleModeFluidSolidifier;
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
        return TextLocalization.SteamFormingModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamFormingModule_00)
            .addInfo(TextLocalization.Tooltip_SteamFormingModule_01)
            .addInfo(TextLocalization.Tooltip_SteamFormingModule_02)
            .addInfo(TextLocalization.Tooltip_SteamFormingModule_03)
            .addInfo(TextLocalization.Tooltip_SteamFormingModule_04)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamFormingModule_Controller)
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
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_FLUID_SOLIDIFIER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_FORMING_PRESS,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LATHE,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSOR,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_STAMPING,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_HAMMER);
    }
}
