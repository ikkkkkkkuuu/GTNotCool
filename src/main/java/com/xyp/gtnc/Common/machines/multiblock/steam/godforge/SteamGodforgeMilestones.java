package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.xyp.gtnc.Config.Config;

import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

/** Server-authoritative steam milestone thresholds and progress calculations. */
public final class SteamGodforgeMilestones {

    public static final int STEAM_WORK = 0;
    public static final int RECIPES = 1;
    public static final int COMPRESSED_STEAM = 2;
    public static final int COMPOSITION = 3;

    private SteamGodforgeMilestones() {}

    public static void update(ForgeOfGodsData data) {
        Progress work = calculate(
            data.getTotalPowerConsumed(),
            Config.SteamForgeOfGods.steamWorkMilestoneThresholds,
            Config.SteamForgeOfGods.chargeMilestoneMultiplier);
        Progress recipes = calculate(
            BigInteger.valueOf(data.getTotalRecipesProcessed()),
            Config.SteamForgeOfGods.recipeMilestoneThresholds,
            Config.SteamForgeOfGods.conversionMilestoneMultiplier);
        Progress compressedSteam = calculate(
            BigInteger.valueOf(data.getTotalFuelConsumed()),
            Config.SteamForgeOfGods.compressedSteamMilestoneThresholds,
            Config.SteamForgeOfGods.catalystMilestoneMultiplier);
        Progress composition = calculateComposition(
            data.getTotalExtensionsBuilt() * Config.SteamForgeOfGods.compositionMilestoneMultiplier);

        Progress[] all = { work, recipes, compressedSteam, composition };
        boolean inversion = true;
        for (int i = 0; i < all.length; i++) {
            data.setMilestoneProgress(i, all[i].level);
            if (all[i].level < 7) inversion = false;
        }
        data.setInversion(inversion);

        applyBar(data, STEAM_WORK, work, inversion);
        applyBar(data, RECIPES, recipes, inversion);
        applyBar(data, COMPRESSED_STEAM, compressedSteam, inversion);
        applyBar(data, COMPOSITION, composition, inversion);
        updateSteamInsight(data, inversion);
    }

    public static Number getNextTarget(int milestone, int currentLevel) {
        if (milestone == COMPOSITION) {
            return divideCeil(
                BigInteger.valueOf(Math.max(1L, (long) currentLevel + 1L)),
                Config.SteamForgeOfGods.compositionMilestoneMultiplier);
        }

        long[] thresholds = getThresholds(milestone);
        double multiplier = getMultiplier(milestone);
        BigInteger weightedTarget;
        if (currentLevel < 7) {
            weightedTarget = BigInteger.valueOf(thresholds[Math.max(0, currentLevel)]);
        } else {
            weightedTarget = BigInteger.valueOf(thresholds[thresholds.length - 1])
                .multiply(BigInteger.valueOf(Math.max(2L, (long) currentLevel - 5L)));
        }
        BigInteger actualTarget = divideCeil(weightedTarget, multiplier);
        return actualTarget.bitLength() < 63 ? actualTarget.longValue() : actualTarget;
    }

    private static long[] getThresholds(int milestone) {
        return switch (milestone) {
            case STEAM_WORK -> Config.SteamForgeOfGods.steamWorkMilestoneThresholds;
            case RECIPES -> Config.SteamForgeOfGods.recipeMilestoneThresholds;
            case COMPRESSED_STEAM -> Config.SteamForgeOfGods.compressedSteamMilestoneThresholds;
            default -> throw new IllegalArgumentException("No threshold array for milestone " + milestone);
        };
    }

    private static double getMultiplier(int milestone) {
        return switch (milestone) {
            case STEAM_WORK -> Config.SteamForgeOfGods.chargeMilestoneMultiplier;
            case RECIPES -> Config.SteamForgeOfGods.conversionMilestoneMultiplier;
            case COMPRESSED_STEAM -> Config.SteamForgeOfGods.catalystMilestoneMultiplier;
            default -> 1.0;
        };
    }

    private static Progress calculate(BigInteger actualTotal, long[] thresholds, double multiplier) {
        BigInteger total = multiply(actualTotal.max(BigInteger.ZERO), multiplier);
        for (int i = 0; i < thresholds.length; i++) {
            BigInteger upper = BigInteger.valueOf(thresholds[i]);
            if (total.compareTo(upper) < 0) {
                BigInteger lower = i == 0 ? BigInteger.ZERO : BigInteger.valueOf(thresholds[i - 1]);
                float segment = fraction(total.subtract(lower), upper.subtract(lower));
                return new Progress(i, (i + segment) / 7.0f, 0.0f);
            }
        }

        BigInteger last = BigInteger.valueOf(thresholds[thresholds.length - 1]);
        BigInteger[] cycle = total.subtract(last)
            .divideAndRemainder(last);
        int completedCycles = saturatingInt(cycle[0]);
        int level = saturatingAdd(7, completedCycles);
        float fraction = fraction(cycle[1], last);
        boolean even = !cycle[0].testBit(0);
        return new Progress(level, even ? 1.0f - fraction : fraction, even ? fraction : 1.0f - fraction);
    }

    private static Progress calculateComposition(double effectiveTotal) {
        double total = Math.max(0.0, effectiveTotal);
        if (total < 7.0) return new Progress((int) Math.floor(total), (float) (total / 7.0), 0.0f);

        double rawCycle = (total - 7.0) / 7.0;
        long completedCycles = (long) Math.floor(rawCycle);
        float fraction = (float) (rawCycle - completedCycles);
        int level = total >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.floor(total);
        boolean even = (completedCycles & 1L) == 0L;
        return new Progress(level, even ? 1.0f - fraction : fraction, even ? fraction : 1.0f - fraction);
    }

    private static void applyBar(ForgeOfGodsData data, int milestone, Progress progress, boolean inversion) {
        float normal = inversion ? progress.normal : Math.min(1.0f, progress.normal);
        float inverted = inversion ? progress.inverted : 0.0f;
        switch (milestone) {
            case STEAM_WORK -> {
                data.setPowerMilestonePercentage(normal);
                data.setInvertedPowerMilestonePercentage(inverted);
            }
            case RECIPES -> {
                data.setRecipeMilestonePercentage(normal);
                data.setInvertedRecipeMilestonePercentage(inverted);
            }
            case COMPRESSED_STEAM -> {
                data.setFuelMilestonePercentage(normal);
                data.setInvertedFuelMilestonePercentage(inverted);
            }
            case COMPOSITION -> {
                data.setStructureMilestonePercentage(normal);
                data.setInvertedStructureMilestonePercentage(inverted);
            }
            default -> throw new IllegalArgumentException("Unknown milestone " + milestone);
        }
    }

    private static void updateSteamInsight(ForgeOfGodsData data, boolean inversion) {
        long totalInsight = 0;
        for (int level : data.getAllMilestoneProgress()) {
            int countedLevel = inversion ? level : Math.min(level, 7);
            long gained = (long) countedLevel * (countedLevel + 1L) / 2L;
            if (Long.MAX_VALUE - totalInsight < gained) {
                totalInsight = Long.MAX_VALUE;
                break;
            }
            totalInsight += gained;
        }
        long available = Math.max(0L, totalInsight - data.getGravitonShardsSpent());
        data.setGravitonShardsAvailable((int) Math.min(Integer.MAX_VALUE, available));
    }

    private static BigInteger multiply(BigInteger value, double multiplier) {
        return new BigDecimal(value).multiply(BigDecimal.valueOf(multiplier))
            .toBigInteger();
    }

    private static BigInteger divideCeil(BigInteger value, double divisor) {
        return new BigDecimal(value).divide(BigDecimal.valueOf(divisor), 0, RoundingMode.CEILING)
            .toBigInteger();
    }

    private static float fraction(BigInteger numerator, BigInteger denominator) {
        if (denominator.signum() <= 0) return 0.0f;
        return numerator.floatValue() / denominator.floatValue();
    }

    private static int saturatingInt(BigInteger value) {
        return value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0 ? Integer.MAX_VALUE : value.intValue();
    }

    private static int saturatingAdd(int left, int right) {
        return right > Integer.MAX_VALUE - left ? Integer.MAX_VALUE : left + right;
    }

    private static final class Progress {

        private final int level;
        private final float normal;
        private final float inverted;

        private Progress(int level, float normal, float inverted) {
            this.level = level;
            this.normal = normal;
            this.inverted = inverted;
        }
    }
}
