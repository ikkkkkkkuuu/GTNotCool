package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.godforge.MTEMoltenModule;

public class SteamGodforgeMoltenModule extends MTEMoltenModule implements SteamGodforgePower.ControllerAware {

    private SteamForgeOfGods steamController;

    public SteamGodforgeMoltenModule(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public SteamGodforgeMoltenModule(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamGodforgeMoltenModule(mName);
    }

    @Override
    public IStructureDefinition<? extends TTMultiblockBase> getStructure_EM() {
        return SteamGodforgeStructures.module();
    }

    @Override
    public boolean drainEnergyInput(long euPerTick, long amperes) {
        return SteamGodforgePower.drainEnergyInput(steamController, userUUID, euPerTick, amperes);
    }

    @Override
    public void setSteamController(SteamForgeOfGods controller) {
        steamController = controller;
    }

    @Override
    public SteamForgeOfGods getSteamController() {
        return steamController;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return SteamGodforgeTooltips.module(
            TextLocalization.SteamGodforgeMoltenModuleMachineType,
            TextLocalization.Tooltip_SteamGodforgeMoltenModule_00);
    }
}
