package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator.SteamElevatorModuleGui;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

/** Alloy Blast Smelter recipe module connected to a Steam Elevator controller. */
public final class SteamAlloyBlastSmelterModule extends SteamElevatorModuleBase {

    private static final int MODULE_TIER = 14;
    private static final Collection<RecipeMap<?>> AVAILABLE_RECIPE_MAPS = Collections
        .<RecipeMap<?>>singletonList(GTPPRecipeMaps.alloyBlastSmelterRecipes);

    public SteamAlloyBlastSmelterModule(int id, String name, String regionalName) {
        super(id, name, regionalName, MODULE_TIER);
    }

    public SteamAlloyBlastSmelterModule(String name) {
        super(name, MODULE_TIER);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamAlloyBlastSmelterModule(mName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.alloyBlastSmelterRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return AVAILABLE_RECIPE_MAPS;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.SteamAlloyBlastSmelterModuleMachineType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamAlloyBlastSmelterModule_00)
            .addInfo(TextLocalization.Tooltip_SteamAlloyBlastSmelterModule_01)
            .addInfo(TextLocalization.Tooltip_SteamAlloyBlastSmelterModule_02)
            .addInfo(TextLocalization.Tooltip_SteamAlloyBlastSmelterModule_03)
            .beginStructureBlock(1, 2, 1, false)
            .addController(TextLocalization.Tooltip_SteamAlloyBlastSmelterModule_Controller)
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
