package com.xyp.gtnc.utils.enums;

import java.util.Arrays;
import java.util.List;

import net.minecraftforge.fluids.Fluid;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;

public enum SteamTypes {

    STEAM("Steam", Materials.Steam.mGas, 1),
    SH_STEAM("Superheated Steam", GTModHandler.getSuperHeatedSteam(1)
        .getFluid(), 10),
    DSC_STEAM("Dense Supercritical Steam", Materials.DenseSupercriticalSteam.mGas, 50);

    public static final SteamTypes[] VALUES = values();

    public final String displayName;
    public final Fluid fluid;
    /** 蒸汽效率换算因子：rawAmount * efficiencyFactor = 基础蒸汽当量 */
    public final int efficiencyFactor;

    SteamTypes(String name, Fluid fluid, int efficiency) {
        this.displayName = name;
        this.fluid = fluid;
        this.efficiencyFactor = efficiency;
    }

    public static List<SteamTypes> getSupportedTypes() {
        return Arrays.asList(VALUES);
    }
}
