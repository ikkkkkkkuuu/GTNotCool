package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import net.minecraft.nbt.NBTTagCompound;

import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.UpgradeStorage;

/** Independent milestone and evolution-tree state owned by one Steam Elevator controller. */
public final class SteamElevatorEvolutionData {

    public static final int MILESTONE_COUNT = 4;
    public static final int MAX_MILESTONE_LEVEL = 7;

    public static final int STEAM_DISTRIBUTED = 0;
    public static final int MODULE_CHARGES = 1;
    public static final int ACTIVE_TIME = 2;
    public static final int MODULE_COMPOSITION = 3;

    private static final ForgeOfGodsUpgrade[] EXTENSION_LAYER_UPGRADES = { ForgeOfGodsUpgrade.GPCI,
        ForgeOfGodsUpgrade.REC, ForgeOfGodsUpgrade.QGPIU, ForgeOfGodsUpgrade.TCT };

    private static final long[] FIRST_THRESHOLDS = { 1_000_000L, 1_000L, 72_000L, 1L };
    private static final int[] THRESHOLD_MULTIPLIERS = { 9, 4, 3, 1 };

    private final UpgradeStorage upgrades = new UpgradeStorage();

    private long totalSteamDistributed;
    private long totalModuleCharges;
    private long totalActiveTicks;
    private int maximumConnectedModules;

    public void addSteamDistributed(long amount) {
        totalSteamDistributed = saturatingAdd(totalSteamDistributed, amount);
    }

    public void addModuleCharge() {
        totalModuleCharges = saturatingAdd(totalModuleCharges, 1);
    }

    public void addActiveTick() {
        totalActiveTicks = saturatingAdd(totalActiveTicks, 1);
    }

    public void observeConnectedModules(int count) {
        maximumConnectedModules = Math.max(maximumConnectedModules, Math.max(0, count));
    }

    public long getMilestoneValue(int milestone) {
        return switch (milestone) {
            case STEAM_DISTRIBUTED -> totalSteamDistributed;
            case MODULE_CHARGES -> totalModuleCharges;
            case ACTIVE_TIME -> totalActiveTicks;
            case MODULE_COMPOSITION -> maximumConnectedModules;
            default -> 0;
        };
    }

    public int getMilestoneLevel(int milestone) {
        long value = getMilestoneValue(milestone);
        int level = 0;
        while (level < MAX_MILESTONE_LEVEL && value >= getMilestoneThreshold(milestone, level + 1)) level++;
        return level;
    }

    public static long getMilestoneThreshold(int milestone, int level) {
        if (milestone < 0 || milestone >= MILESTONE_COUNT || level <= 0) return 0;
        int boundedLevel = Math.min(level, MAX_MILESTONE_LEVEL);
        if (milestone == MODULE_COMPOSITION) return boundedLevel;

        long threshold = FIRST_THRESHOLDS[milestone];
        for (int current = 1; current < boundedLevel; current++) {
            threshold = saturatingMultiply(threshold, THRESHOLD_MULTIPLIERS[milestone]);
        }
        return threshold;
    }

    public double getMilestoneProgress(int milestone) {
        int level = getMilestoneLevel(milestone);
        if (level >= MAX_MILESTONE_LEVEL) return 1;
        long previous = level == 0 ? 0 : getMilestoneThreshold(milestone, level);
        long next = getMilestoneThreshold(milestone, level + 1);
        if (next <= previous) return 1;
        return Math.max(0, Math.min(1, (getMilestoneValue(milestone) - previous) / (double) (next - previous)));
    }

    public int getEvolutionPointsEarned() {
        int total = 0;
        for (int milestone = 0; milestone < MILESTONE_COUNT; milestone++) {
            int level = getMilestoneLevel(milestone);
            total += level * (level + 1) / 2;
        }
        return total;
    }

    public int getEvolutionPointsSpent() {
        int total = 0;
        for (ForgeOfGodsUpgrade upgrade : ForgeOfGodsUpgrade.VALUES) {
            if (upgrades.isUpgradeActive(upgrade)) total += upgrade.getShardCost();
        }
        return total;
    }

    public int getEvolutionPointsAvailable() {
        return Math.max(0, getEvolutionPointsEarned() - getEvolutionPointsSpent());
    }

    public boolean isUpgradeActive(ForgeOfGodsUpgrade upgrade) {
        return upgrades.isUpgradeActive(upgrade);
    }

    public int getUnlockedExtensionLayers() {
        int unlockedLayers = 0;
        for (ForgeOfGodsUpgrade upgrade : EXTENSION_LAYER_UPGRADES) {
            if (!upgrades.isUpgradeActive(upgrade)) break;
            unlockedLayers++;
        }
        return unlockedLayers;
    }

    public static int getExtensionLayer(ForgeOfGodsUpgrade upgrade) {
        for (int index = 0; index < EXTENSION_LAYER_UPGRADES.length; index++) {
            if (EXTENSION_LAYER_UPGRADES[index] == upgrade) return index + 1;
        }
        return 0;
    }

    public boolean tryUnlock(ForgeOfGodsUpgrade upgrade) {
        if (upgrade == null || upgrades.isUpgradeActive(upgrade)) return false;
        int extensionLayer = getExtensionLayer(upgrade);
        if (extensionLayer > 1 && !upgrades.isUpgradeActive(EXTENSION_LAYER_UPGRADES[extensionLayer - 2])) {
            return false;
        }
        if (!upgrades.checkPrerequisites(upgrade)) return false;
        if (upgrade.getShardCost() > getEvolutionPointsAvailable()) return false;
        upgrades.unlockUpgrade(upgrade);
        return true;
    }

    public boolean tryRespec(ForgeOfGodsUpgrade upgrade) {
        if (upgrade == null || !upgrades.isUpgradeActive(upgrade)) return false;
        int extensionLayer = getExtensionLayer(upgrade);
        if (extensionLayer > 0 && extensionLayer < EXTENSION_LAYER_UPGRADES.length
            && upgrades.isUpgradeActive(EXTENSION_LAYER_UPGRADES[extensionLayer])) {
            return false;
        }
        if (!upgrades.checkDependents(upgrade)) return false;
        upgrades.respecUpgrade(upgrade);
        return true;
    }

    public void writeToNBT(NBTTagCompound parent) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("totalSteamDistributed", totalSteamDistributed);
        tag.setLong("totalModuleCharges", totalModuleCharges);
        tag.setLong("totalActiveTicks", totalActiveTicks);
        tag.setInteger("maximumConnectedModules", maximumConnectedModules);
        upgrades.serializeToNBT(tag, true);
        parent.setTag("steamElevatorEvolution", tag);
    }

    public void readFromNBT(NBTTagCompound parent) {
        if (!parent.hasKey("steamElevatorEvolution")) return;
        NBTTagCompound tag = parent.getCompoundTag("steamElevatorEvolution");
        totalSteamDistributed = Math.max(0, tag.getLong("totalSteamDistributed"));
        totalModuleCharges = Math.max(0, tag.getLong("totalModuleCharges"));
        totalActiveTicks = Math.max(0, tag.getLong("totalActiveTicks"));
        maximumConnectedModules = Math.max(0, tag.getInteger("maximumConnectedModules"));
        upgrades.rebuildFromNBT(tag);
    }

    private static long saturatingAdd(long left, long right) {
        if (right <= 0) return left;
        return left > Long.MAX_VALUE - right ? Long.MAX_VALUE : left + right;
    }

    private static long saturatingMultiply(long value, int multiplier) {
        return value > Long.MAX_VALUE / multiplier ? Long.MAX_VALUE : value * multiplier;
    }
}
