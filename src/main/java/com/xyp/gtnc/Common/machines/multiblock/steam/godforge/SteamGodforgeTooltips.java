package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.util.MultiblockTooltipBuilder;

final class SteamGodforgeTooltips {

    private SteamGodforgeTooltips() {}

    static MultiblockTooltipBuilder module(String machineType, String moduleInfo) {
        return new MultiblockTooltipBuilder().addMachineType(machineType)
            .addInfo(TextLocalization.Tooltip_SteamGodforgeModule_00)
            .addInfo(TextLocalization.Tooltip_SteamGodforgeModule_01)
            .addInfo(TextLocalization.Tooltip_SteamGodforgeModule_02)
            .addInfo(moduleInfo)
            .beginStructureBlock(13, 7, 7, false)
            .addController(TextLocalization.Tooltip_SteamGodforgeModule_Controller)
            .addCasing("16-36", TextLocalization.Tooltip_SteamGodforgeModule_BronzePlated, false)
            .addCasing("5", TextLocalization.Tooltip_SteamGodforgeModule_Reinforced, false)
            .addCasing("20", TextLocalization.Tooltip_SteamGodforgeModule_Gearbox, false)
            .addCasing("21", TextLocalization.Tooltip_SteamGodforgeModule_Pipe, false)
            .addCasing("6", TextLocalization.Tooltip_SteamGodforgeModule_Firebox, false)
            .addInputBus("0+", TextLocalization.Tooltip_SteamGodforgeModule_FrontCasing, 1)
            .addInputHatch("0+", TextLocalization.Tooltip_SteamGodforgeModule_FrontCasing, 1)
            .addOutputBus("0+", TextLocalization.Tooltip_SteamGodforgeModule_FrontCasing, 1)
            .addOutputHatch("0+", TextLocalization.Tooltip_SteamGodforgeModule_FrontCasing, 1)
            .toolTipFinisher();
    }
}
