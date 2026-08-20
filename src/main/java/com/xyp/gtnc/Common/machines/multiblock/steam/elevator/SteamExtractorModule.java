package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator.SteamElevatorModuleGui;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;

/** Two-mode extraction module connected to a Steam Elevator controller. */
public final class SteamExtractorModule extends SteamElevatorModuleBase {

    private static final int MODE_EXTRACTOR = 0;
    private static final int MODE_FLUID_EXTRACTOR = 1;
    private static final int MODE_COUNT = 2;
    private static final int MODULE_TIER = 14;
    private static final List<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections
        .unmodifiableList(Arrays.asList(RecipeMaps.extractorRecipes, RecipeMaps.fluidExtractionRecipes));

    public SteamExtractorModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamExtractorModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamExtractorModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == MODE_FLUID_EXTRACTOR ? RecipeMaps.fluidExtractionRecipes : RecipeMaps.extractorRecipes;
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
        super.setMachineMode(mode == MODE_FLUID_EXTRACTOR ? MODE_FLUID_EXTRACTOR : MODE_EXTRACTOR);
        refreshInputRecipeMaps();
    }

    @Override
    public String getMachineModeName() {
        return machineMode == MODE_FLUID_EXTRACTOR ? TextLocalization.SteamExtractorModuleModeFluidExtractor
            : TextLocalization.SteamExtractorModuleModeExtractor;
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
        return TextLocalization.SteamExtractorModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamExtractorModule_00)
            .addInfo(TextLocalization.Tooltip_SteamExtractorModule_01)
            .addInfo(TextLocalization.Tooltip_SteamExtractorModule_02)
            .addInfo(TextLocalization.Tooltip_SteamExtractorModule_03)
            .addInfo(TextLocalization.Tooltip_SteamExtractorModule_04)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamExtractorModule_Controller)
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
            GTGuiTextures.TT_OVERLAY_BUTTON_FURNACE_MODE_OFF,
            GTGuiTextures.TT_OVERLAY_BUTTON_FURNACE_MODE);
    }
}
