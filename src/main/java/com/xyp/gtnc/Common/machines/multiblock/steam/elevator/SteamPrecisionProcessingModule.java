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

/** Four-mode precision-processing module connected to a Steam Elevator controller. */
public final class SteamPrecisionProcessingModule extends SteamElevatorModuleBase {

    private static final int MODE_LASER_ENGRAVER = 0;
    private static final int MODE_CUTTER = 1;
    private static final int MODE_BENDER = 2;
    private static final int MODE_WIREMILL = 3;
    private static final int MODE_COUNT = 4;
    private static final int MODULE_TIER = 14;
    private static final List<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections.unmodifiableList(
        Arrays.asList(
            RecipeMaps.laserEngraverRecipes,
            RecipeMaps.cutterRecipes,
            RecipeMaps.benderRecipes,
            RecipeMaps.wiremillRecipes));

    public SteamPrecisionProcessingModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamPrecisionProcessingModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamPrecisionProcessingModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MODE_CUTTER -> RecipeMaps.cutterRecipes;
            case MODE_BENDER -> RecipeMaps.benderRecipes;
            case MODE_WIREMILL -> RecipeMaps.wiremillRecipes;
            default -> RecipeMaps.laserEngraverRecipes;
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
        super.setMachineMode(mode >= 0 && mode < MODE_COUNT ? mode : MODE_LASER_ENGRAVER);
        refreshInputRecipeMaps();
    }

    @Override
    public String getMachineModeName() {
        return switch (machineMode) {
            case MODE_CUTTER -> TextLocalization.SteamPrecisionProcessingModuleModeCutter;
            case MODE_BENDER -> TextLocalization.SteamPrecisionProcessingModuleModeBender;
            case MODE_WIREMILL -> TextLocalization.SteamPrecisionProcessingModuleModeWiremill;
            default -> TextLocalization.SteamPrecisionProcessingModuleModeLaserEngraver;
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
        return TextLocalization.SteamPrecisionProcessingModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamPrecisionProcessingModule_00)
            .addInfo(TextLocalization.Tooltip_SteamPrecisionProcessingModule_01)
            .addInfo(TextLocalization.Tooltip_SteamPrecisionProcessingModule_02)
            .addInfo(TextLocalization.Tooltip_SteamPrecisionProcessingModule_03)
            .addInfo(TextLocalization.Tooltip_SteamPrecisionProcessingModule_04)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamPrecisionProcessingModule_Controller)
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
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LASER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CUTTER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_BENDER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_WIREMILL);
    }
}
