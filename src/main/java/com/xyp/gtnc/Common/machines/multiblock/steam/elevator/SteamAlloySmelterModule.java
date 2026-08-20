package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator.SteamElevatorModuleGui;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/** Alloy Smelter recipe module connected to a Steam Elevator controller. */
public final class SteamAlloySmelterModule extends SteamElevatorModuleBase {

    private static final int MODULE_TIER = 14;
    private static final Collection<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections
        .<RecipeMap<?>>singletonList(RecipeMaps.alloySmelterRecipes);

    public SteamAlloySmelterModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamAlloySmelterModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamAlloySmelterModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.alloySmelterRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return AVAILABLE_RECIPE_MAPS;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.SteamAlloySmelterModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamAlloySmelterModule_00)
            .addInfo(TextLocalization.Tooltip_SteamAlloySmelterModule_01)
            .addInfo(TextLocalization.Tooltip_SteamAlloySmelterModule_02)
            .addInfo(TextLocalization.Tooltip_SteamAlloySmelterModule_03)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamAlloySmelterModule_Controller)
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
