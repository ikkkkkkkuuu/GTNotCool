package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.math.BigInteger;
import java.util.UUID;

import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

/** Wireless-steam power adapter shared by the four Steam Godforge modules. */
public final class SteamGodforgePower {

    /** Steam Godforge modules consume one ten-thousandth of their calculated recipe EU as wireless steam. */
    private static final BigInteger RECIPE_EU_RATIO_DENOMINATOR = BigInteger.valueOf(10_000L);

    public interface ControllerAware {

        void setSteamController(SteamForgeOfGods controller);

        SteamForgeOfGods getSteamController();
    }

    private SteamGodforgePower() {}

    public static boolean drainEnergyInput(SteamForgeOfGods controller, UUID fallbackOwner, long euPerTick,
        long amperes) {
        BigInteger eu = BigInteger.valueOf(euPerTick)
            .multiply(BigInteger.valueOf(amperes))
            .abs();
        return drainEnergyAmount(controller, fallbackOwner, eu);
    }

    public static boolean drainEnergyAmount(ControllerAware module, UUID fallbackOwner, BigInteger eu) {
        SteamForgeOfGods controller = module == null ? null : module.getSteamController();
        return drainEnergyAmount(controller, fallbackOwner, eu);
    }

    public static boolean drainEnergyAmount(SteamForgeOfGods controller, UUID fallbackOwner, BigInteger eu) {
        UUID owner = controller == null ? fallbackOwner : controller.getSteamOwner();
        if (owner == null) return false;
        if (eu == null || eu.signum() == 0) return true;
        BigInteger steamCost = euToSteamCeil(eu.abs());
        boolean drained = SteamWirelessNetworkManager.addSteamToGlobalSteamMap(owner, steamCost.negate());
        if (drained && controller != null) {
            controller.recordWirelessSteamConsumed(steamCost);
        }
        return drained;
    }

    public static BigInteger getAvailableEU(ControllerAware module, UUID fallbackOwner) {
        SteamForgeOfGods controller = module == null ? null : module.getSteamController();
        UUID owner = controller == null ? fallbackOwner : controller.getSteamOwner();
        if (owner == null) return BigInteger.ZERO;
        return steamToEuFloor(SteamWirelessNetworkManager.getUserSteam(owner));
    }

    public static BigInteger euToSteamCeil(BigInteger eu) {
        if (eu == null || eu.signum() <= 0) return BigInteger.ZERO;
        BigInteger numerator = BigInteger.valueOf(Config.SteamForgeOfGods.steamPerEUNumerator)
            .multiply(BigInteger.valueOf(Config.SteamForgeOfGods.moduleSteamMultiplierNumerator));
        BigInteger denominator = BigInteger.valueOf(Config.SteamForgeOfGods.steamPerEUDenominator)
            .multiply(BigInteger.valueOf(Config.SteamForgeOfGods.moduleSteamMultiplierDenominator))
            .multiply(RECIPE_EU_RATIO_DENOMINATOR);
        return eu.multiply(numerator)
            .add(denominator.subtract(BigInteger.ONE))
            .divide(denominator);
    }

    public static BigInteger steamToEuFloor(BigInteger steam) {
        if (steam == null || steam.signum() <= 0) return BigInteger.ZERO;
        BigInteger numerator = BigInteger.valueOf(Config.SteamForgeOfGods.steamPerEUNumerator)
            .multiply(BigInteger.valueOf(Config.SteamForgeOfGods.moduleSteamMultiplierNumerator));
        BigInteger denominator = BigInteger.valueOf(Config.SteamForgeOfGods.steamPerEUDenominator)
            .multiply(BigInteger.valueOf(Config.SteamForgeOfGods.moduleSteamMultiplierDenominator))
            .multiply(RECIPE_EU_RATIO_DENOMINATOR);
        return steam.multiply(denominator)
            .divide(numerator);
    }
}
