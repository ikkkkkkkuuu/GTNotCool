package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevatorModuleGui;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.utils.lang.TextLocalization;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtnhintergalactic.recipe.IGRecipeMaps;

/** Three-mode assembly processor connected to a Steam Elevator controller. */
public final class SteamAssemblerModule extends SteamElevatorModuleBase {

    private static final int MODE_ASSEMBLER = 0;
    private static final int MODE_PRECISE_ASSEMBLER = 1;
    private static final int MODE_SPACE_ASSEMBLER = 2;
    private static final int MODE_COUNT = 3;
    private static final int MODULE_TIER = 14;
    private static final List<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections.unmodifiableList(
        Arrays.asList(
            RecipeMaps.assemblerRecipes,
            GoodGeneratorRecipeMaps.preciseAssemblerRecipes,
            IGRecipeMaps.spaceAssemblerRecipes));

    public SteamAssemblerModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamAssemblerModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamAssemblerModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return switch (machineMode) {
            case MODE_PRECISE_ASSEMBLER -> GoodGeneratorRecipeMaps.preciseAssemblerRecipes;
            case MODE_SPACE_ASSEMBLER -> IGRecipeMaps.spaceAssemblerRecipes;
            default -> RecipeMaps.assemblerRecipes;
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
        super.setMachineMode(mode >= 0 && mode < MODE_COUNT ? mode : MODE_ASSEMBLER);
        refreshInputRecipeMaps();
    }

    @Override
    public String getMachineModeName() {
        return switch (machineMode) {
            case MODE_PRECISE_ASSEMBLER -> TextLocalization.SteamAssemblerModuleModePreciseAssembler;
            case MODE_SPACE_ASSEMBLER -> TextLocalization.SteamAssemblerModuleModeSpaceAssembler;
            default -> TextLocalization.SteamAssemblerModuleModeAssembler;
        };
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        setMachineMode(machineMode);
    }

    private static boolean hasRealInput(GTRecipe recipe) {
        if (recipe.mInputs != null) {
            for (ItemStack input : recipe.mInputs) {
                if (input != null && input.getItem() != null && input.stackSize > 0) return true;
            }
        }
        if (recipe.mFluidInputs != null) {
            for (FluidStack input : recipe.mFluidInputs) {
                if (input != null && input.getFluid() != null && input.amount > 0) return true;
            }
        }
        return false;
    }

    private static boolean hasSafeItemOutputs(GTRecipe recipe) {
        if (recipe.mOutputs == null || recipe.mOutputs.length == 0) return false;
        for (ItemStack output : recipe.mOutputs) {
            if (output == null || output.getItem() == null || output.stackSize <= 0) return false;
        }
        return true;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (machineMode == MODE_ASSEMBLER && (recipe.mFakeRecipe || !recipe.mEnabled
                    || !hasRealInput(recipe)
                    || !hasSafeItemOutputs(recipe))) {
                    return CheckRecipeResultRegistry.NO_RECIPE;
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
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
        return TextLocalization.SteamAssemblerModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamAssemblerModule_00)
            .addInfo(TextLocalization.Tooltip_SteamAssemblerModule_01)
            .addInfo(TextLocalization.Tooltip_SteamAssemblerModule_02)
            .addInfo(TextLocalization.Tooltip_SteamAssemblerModule_03)
            .addInfo(TextLocalization.Tooltip_SteamAssemblerModule_04)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamAssemblerModule_Controller)
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
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_ASSEMBLER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_PRECISE_ASSEMBLER,
            GTNCGuiTextures.OVERLAY_BUTTON_MACHINEMODE_ARCANE_ASSEMBLER);
    }
}
