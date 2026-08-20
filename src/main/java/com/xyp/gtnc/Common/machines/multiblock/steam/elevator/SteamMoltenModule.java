package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator.SteamElevatorModuleGui;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.recipe.TecTechRecipeMaps;

/** Molten-processing module connected to a Steam Elevator controller. */
public final class SteamMoltenModule extends SteamElevatorModuleBase {

    private static final int MODULE_TIER = 14;
    private static final int BASE_MAX_HEAT = 15_700;
    private static final int UPGRADED_MAX_HEAT = 17_700;
    private static final int ADVANCED_MAX_HEAT = 19_700;
    private static final Collection<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections
        .singletonList(TecTechRecipeMaps.godforgeMoltenRecipes);

    private boolean heatUpgradeUnlocked;
    private boolean advancedHeatUpgradeUnlocked;

    public SteamMoltenModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamMoltenModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamMoltenModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return TecTechRecipeMaps.godforgeMoltenRecipes;
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
                if (recipe.mSpecialValue > getMaxHeat()) {
                    return CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setHeatDiscount(true)
                    .setMachineHeat(Math.max(recipe.mSpecialValue, getMaxHeat()))
                    .setHeatDiscountMultiplier(getHeatDiscountMultiplier());
            }
        };
    }

    public int getMaxHeat() {
        if (advancedHeatUpgradeUnlocked) return ADVANCED_MAX_HEAT;
        return heatUpgradeUnlocked ? UPGRADED_MAX_HEAT : BASE_MAX_HEAT;
    }

    public void setHeatUpgradeUnlocked(boolean unlocked) {
        heatUpgradeUnlocked = unlocked;
    }

    public void setAdvancedHeatUpgradeUnlocked(boolean unlocked) {
        advancedHeatUpgradeUnlocked = unlocked;
    }

    public double getHeatDiscountMultiplier() {
        return 0.95;
    }

    @Override
    public void disconnect() {
        super.disconnect();
        heatUpgradeUnlocked = false;
        advancedHeatUpgradeUnlocked = false;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.SteamMoltenModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamMoltenModule_00)
            .addInfo(TextLocalization.Tooltip_SteamMoltenModule_01)
            .addInfo(TextLocalization.Tooltip_SteamMoltenModule_02)
            .addInfo(TextLocalization.Tooltip_SteamMoltenModule_03)
            .addInfo(TextLocalization.Tooltip_SteamMoltenModule_04)
            .addInfo(TextLocalization.Tooltip_SteamMoltenModule_05)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamMoltenModule_Controller)
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
