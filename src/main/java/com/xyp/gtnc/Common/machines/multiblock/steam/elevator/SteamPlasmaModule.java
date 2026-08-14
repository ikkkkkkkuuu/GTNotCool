package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import static gregtech.api.util.GTRecipeConstants.FOG_PLASMA_MULTISTEP;
import static gregtech.api.util.GTRecipeConstants.FOG_PLASMA_TIER;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevatorModuleGui;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.recipe.TecTechRecipeMaps;

/** Plasma-processing module connected to a Steam Elevator controller. */
public final class SteamPlasmaModule extends SteamElevatorModuleBase {

    private static final int MODULE_TIER = 14;
    private static final Collection<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections
        .singletonList(TecTechRecipeMaps.godforgePlasmaRecipes);

    private int plasmaTier;
    private boolean multiStepPlasmaUnlocked;

    public SteamPlasmaModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamPlasmaModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamPlasmaModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return TecTechRecipeMaps.godforgePlasmaRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return AVAILABLE_RECIPE_MAPS;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.getMetadataOrDefault(FOG_PLASMA_TIER, 0) > getPlasmaTier()
                    || (recipe.getMetadataOrDefault(FOG_PLASMA_MULTISTEP, false) && !isMultiStepPlasmaUnlocked())) {
                    return SimpleCheckRecipeResult.ofFailure("missing_upgrades");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    public int getPlasmaTier() {
        return plasmaTier;
    }

    public void setPlasmaTier(int tier) {
        plasmaTier = Math.max(0, tier);
    }

    public boolean isMultiStepPlasmaUnlocked() {
        return multiStepPlasmaUnlocked;
    }

    public void setMultiStepPlasmaUnlocked(boolean unlocked) {
        multiStepPlasmaUnlocked = unlocked;
    }

    @Override
    public void disconnect() {
        super.disconnect();
        plasmaTier = 0;
        multiStepPlasmaUnlocked = false;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.SteamPlasmaModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamPlasmaModule_00)
            .addInfo(TextLocalization.Tooltip_SteamPlasmaModule_01)
            .addInfo(TextLocalization.Tooltip_SteamPlasmaModule_02)
            .addInfo(TextLocalization.Tooltip_SteamPlasmaModule_03)
            .addInfo(TextLocalization.Tooltip_SteamPlasmaModule_04)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamPlasmaModule_Controller)
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
        return new SteamElevatorModuleGui(this);
    }
}
