package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static tectech.thing.casing.TTCasingsContainer.forgeOfGodsRenderBlock;
import static tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade.CD;
import static tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade.END;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.SteamForgeOfGodsGui;
import com.xyp.gtnc.Common.material.GTNCMaterials;
import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.lang.TextLocalization;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.threads.RunnableMachineUpdate;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.block.TileEntityForgeOfGods;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;
import tectech.thing.metaTileEntity.multi.godforge.MTEExoticModule;
import tectech.thing.metaTileEntity.multi.godforge.MTEForgeOfGods;
import tectech.thing.metaTileEntity.multi.godforge.MTEMoltenModule;
import tectech.thing.metaTileEntity.multi.godforge.MTEPlasmaModule;
import tectech.thing.metaTileEntity.multi.godforge.MTESmeltingModule;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

/**
 * LV-obtainable bronze Forge of Gods variant. This deliberately reuses the packaged Godforge module mathematics,
 * perfect overclocks, renderer and upgrade effects while replacing local fuels and wireless EU with team wireless
 * steam.
 */
public class SteamForgeOfGods extends MTEForgeOfGods {

    private static final long STEAM_CYCLE_TICKS = 5L * 20L;
    private static final int MAX_STEAM_PARALLEL = 65_536;
    private static final String NBT_TOTAL_STEAM = "steamGodforgeTotalSteam";
    private static final String NBT_LAST_STEAM = "steamGodforgeLastSteam";
    private static final String NBT_TOTAL_WIRELESS_STEAM = "steamGodforgeTotalWirelessSteam";
    private static final String NBT_UPGRADE_MATERIALS = "steamGodforgeUpgradeMaterials";
    private static final String NBT_INTERNAL_BATTERY = "steamGodforgeInternalBattery";
    private static final String NBT_CRITICAL_PHOTONS = "steamGodforgeCriticalPhotons";
    private static final ForgeOfGodsUpgrade[] PARALLEL_UPGRADES = { ForgeOfGodsUpgrade.SA, ForgeOfGodsUpgrade.CTCDD,
        ForgeOfGodsUpgrade.TCT, ForgeOfGodsUpgrade.EPEC, ForgeOfGodsUpgrade.POS, ForgeOfGodsUpgrade.NGMS,
        ForgeOfGodsUpgrade.TSE, ForgeOfGodsUpgrade.END };
    private static final ForgeOfGodsUpgrade[] SPEED_UPGRADES = { ForgeOfGodsUpgrade.IGCC, ForgeOfGodsUpgrade.SEFCP,
        ForgeOfGodsUpgrade.DOP, ForgeOfGodsUpgrade.CNTI, ForgeOfGodsUpgrade.EPEC, ForgeOfGodsUpgrade.NDPE,
        ForgeOfGodsUpgrade.POS, ForgeOfGodsUpgrade.DOR };

    private UUID steamOwner;
    private long steamTicker;
    private BigInteger totalSteamConsumed = BigInteger.ZERO;
    private BigInteger lastSteamConsumed = BigInteger.ZERO;
    private BigInteger totalWirelessSteamConsumed = BigInteger.ZERO;
    private int paidUpgradeMaterialMask;

    public SteamForgeOfGods(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public SteamForgeOfGods(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamForgeOfGods(mName);
    }

    @Override
    public IStructureDefinition<MTEForgeOfGods> getStructure_EM() {
        return SteamGodforgeStructures.controller();
    }

    public boolean addSteamModuleToMachineList(IGregTechTileEntity tileEntity, int casingIndex) {
        if (tileEntity == null) return false;
        IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();
        if (!SteamGodforgeStructures.isSteamModule(metaTileEntity)) return false;
        ((SteamGodforgePower.ControllerAware) metaTileEntity).setSteamController(this);
        MTEBaseModule module = (MTEBaseModule) metaTileEntity;
        ensureChargeableModuleParameters(module);
        return moduleHatches.add(module);
    }

    public int getSteamModuleCount() {
        return moduleHatches.size();
    }

    @Override
    public void checkMachine(IGregTechTileEntity base, ItemStack stack, List<StructureError> errors) {
        ForgeOfGodsData data = getData();
        moduleHatches.clear();

        if (data.isRenderActive()) {
            if (!checkPiece(SteamGodforgeStructures.SHAFT, 63, 14, 1, errors)
                || !checkPiece(SteamGodforgeStructures.FIRST_RING_AIR, 63, 14, -59, errors)) {
                destroyRenderer();
                return;
            }
        } else if (!checkPiece(SteamGodforgeStructures.MAIN, 63, 14, 1, errors)) {
            return;
        }

        if (data.getInternalBattery() != 0 && !data.isRenderActive() && !data.isRendererDisabled()) {
            createSteamRenderer();
        }

        if (!mEnergyHatches.isEmpty() || !mExoticEnergyHatches.isEmpty()) {
            errors.add(StructureErrorRegistry.NO_ENERGY_HATCH_NEEDED);
        }
        checkHatchExact(errors, InputBus, 1);
        checkHatchExact(errors, InputHatch, 1);
        checkHatchExact(errors, OutputBus, 1);
        if (!errors.isEmpty()) return;

        if (data.isUpgradeActive(CD)) {
            if (checkPiece(SteamGodforgeStructures.SECOND_RING, 55, 11, -67, errors)) {
                data.setRingAmount(2);
                if (!data.isRendererDisabled()) {
                    buildPiece(SteamGodforgeStructures.SECOND_RING_AIR, null, false, 55, 11, -67);
                    updateRenderer();
                }
            }
            if (data.isRenderActive() && data.getRingAmount() >= 2
                && !checkPiece(SteamGodforgeStructures.SECOND_RING_AIR, 55, 11, -67, errors)) {
                destroyRenderer();
            }
        } else if (data.getRingAmount() >= 2) {
            if (data.getRingAmount() == 3) {
                buildPiece(SteamGodforgeStructures.THIRD_RING, null, false, 47, 13, -76);
            }
            data.setRingAmount(1);
            buildPiece(SteamGodforgeStructures.SECOND_RING, null, false, 55, 11, -67);
            updateRenderer();
        }

        if (data.isUpgradeActive(END)) {
            if (checkPiece(SteamGodforgeStructures.THIRD_RING, 47, 13, -76, errors)) {
                data.setRingAmount(3);
                if (!data.isRendererDisabled()) {
                    buildPiece(SteamGodforgeStructures.THIRD_RING_AIR, null, false, 47, 13, -76);
                    updateRenderer();
                }
            }
            if (data.isRenderActive() && data.getRingAmount() == 3
                && !checkPiece(SteamGodforgeStructures.THIRD_RING_AIR, 47, 13, -76, errors)) {
                destroyRenderer();
            }
        } else if (data.getRingAmount() == 3) {
            data.setRingAmount(2);
            buildPiece(SteamGodforgeStructures.THIRD_RING, null, false, 47, 13, -76);
            updateRenderer();
        }

        // Optional rings never prevent the base machine from forming.
        errors.clear();
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        if (!base.isServerSide()) {
            super.onPostTick(base, tick);
            return;
        }

        if (steamOwner == null) steamOwner = SteamWirelessNetworkManager.processInitialSettings(base);
        SteamGodforgeUpgradeHooks.register(this);
        for (MTEBaseModule module : moduleHatches) {
            if (module instanceof SteamGodforgePower.ControllerAware controllerAware) {
                controllerAware.setSteamController(this);
            }
            ensureChargeableModuleParameters(module);
        }
        steamTicker++;
        boolean steamCycle = steamTicker % STEAM_CYCLE_TICKS == 0;
        ForgeOfGodsData data = getData();

        data.setSelectedFuelType(0);
        data.setNeededStartupFuel(Config.SteamForgeOfGods.startupCriticalPhotons);
        primeUpstreamExtraCosts();

        if (mMachine && data.getInternalBattery() == 0) {
            absorbCriticalPhotons();
            if (data.getStellarFuelAmount() >= data.getNeededStartupFuel()) {
                data.setStellarFuelAmount(data.getStellarFuelAmount() - data.getNeededStartupFuel());
                data.setInternalBattery(Math.max(1, data.getMaxBatteryCharge()));
                if (!data.isRenderActive() && !data.isRendererDisabled()) createSteamRenderer();
            }
        }

        int batteryBeforeCycle = data.getInternalBattery();
        int fuelFactor = Math.max(1, data.getFuelConsumptionFactor());
        long compressedSteamCost = getCompressedSteamCost(fuelFactor, data.isBatteryCharging());
        data.setFuelConsumption(compressedSteamCost);
        // A loaded multiblock restores its saved data before StructureLib has necessarily rebuilt the hatch lists.
        // Treat that short interval as "not ready" instead of failing upkeep and destroying the persisted battery.
        boolean steamInfrastructureReady = mMachine && !mInputHatches.isEmpty();
        boolean paidUpkeep = !steamCycle || batteryBeforeCycle == 0
            || !steamInfrastructureReady
            || consumeCompressedSteam(compressedSteamCost);

        // The upstream Godforge drains its own stellar fuel before it connects modules. Its inputs are deliberately
        // hidden below, so protect one cycle of battery charge from that failed drain. Without this temporary charge
        // the upstream code reaches zero battery and skips module.connect(), leaving valid steam modules forever at
        // "Starting up..." even though this controller has already found them.
        if (steamCycle && batteryBeforeCycle > 0) {
            data.setInternalBattery(
                batteryBeforeCycle > Integer.MAX_VALUE - fuelFactor ? Integer.MAX_VALUE
                    : batteryBeforeCycle + fuelFactor);
        }

        // Prevent the upstream controller from consuming DTR, stellar plasma, stellar fuel or graviton shards.
        ArrayList<MTEHatchInput> hiddenFluidInputs = new ArrayList<>(mInputHatches);
        ArrayList<MTEHatchInputBus> hiddenItemInputs = new ArrayList<>(mInputBusses);
        mInputHatches.clear();
        mInputBusses.clear();
        try {
            super.onPostTick(base, tick);
        } finally {
            mInputHatches.addAll(hiddenFluidInputs);
            mInputBusses.addAll(hiddenItemInputs);
        }
        data.setFuelConsumption(compressedSteamCost);
        data.setTotalPowerConsumed(totalWirelessSteamConsumed);
        data.setTotalFuelConsumed(saturatingLong(totalSteamConsumed));

        if (steamCycle && batteryBeforeCycle > 0 && paidUpkeep) {
            int restoredBattery = data.isBatteryCharging()
                ? Math.min(data.getMaxBatteryCharge(), batteryBeforeCycle + fuelFactor)
                : batteryBeforeCycle;
            data.setInternalBattery(restoredBattery);
        } else if (steamCycle && batteryBeforeCycle > 0) {
            // Steam upkeep was not paid. Stop the forge instead of retaining the temporary protection charge.
            data.setInternalBattery(0);
        }
        if (steamCycle) {
            if (mMachine && data.getInternalBattery() > 0) {
                applySteamModuleParameters(fuelFactor);
            } else {
                disconnectSteamModules();
            }
            updateSteamCompositionTotal(data);
            SteamGodforgeMilestones.update(data);
        }
        // The steam forge keeps milestone currency virtual; never eject upstream Graviton Shard items.
        data.setGravitonShardEjection(false);
    }

    private void absorbCriticalPhotons() {
        int remaining = Math.max(0, getData().getNeededStartupFuel() - getData().getStellarFuelAmount());
        if (remaining == 0) return;
        ItemStack photon = GTNCItemList.MiracleDoorPhoton.get(1);
        for (MTEHatchInputBus bus : GTUtility.validMTEList(mInputBusses)) {
            for (int slot = 0; slot < bus.getSizeInventory() && remaining > 0; slot++) {
                ItemStack stack = bus.getStackInSlot(slot);
                if (stack == null || !GTUtility.areStacksEqual(stack, photon)) continue;
                int consumed = Math.min(remaining, stack.stackSize);
                bus.decrStackSize(slot, consumed);
                getData().setStellarFuelAmount(getData().getStellarFuelAmount() + consumed);
                remaining -= consumed;
            }
            bus.updateSlots();
            if (remaining == 0) break;
        }
    }

    private long getCompressedSteamCost(int fuelFactor, boolean charging) {
        long multiplier = charging ? 2L : 1L;
        long base = Config.SteamForgeOfGods.compressedSteamPerFuelFactor;
        if (getData().isUpgradeActive(ForgeOfGodsUpgrade.STEM)) base = Math.max(1L, Math.round(base * 0.8));
        if (base > Long.MAX_VALUE / fuelFactor / multiplier) return Long.MAX_VALUE;
        return base * fuelFactor * multiplier;
    }

    private boolean consumeCompressedSteam(long amount) {
        if (amount <= 0) return true;
        if (amount > Integer.MAX_VALUE) return false;
        FluidStack wanted = GTNCMaterials.CompressedSteam.getMolten((int) amount);
        if (wanted == null) return false;

        int available = 0;
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) {
            FluidStack simulated = hatch
                .drain(ForgeDirection.UNKNOWN, new FluidStack(wanted, wanted.amount - available), false);
            if (simulated != null && simulated.isFluidEqual(wanted)) available += simulated.amount;
            if (available >= wanted.amount) break;
        }
        if (available < wanted.amount) return false;

        int remaining = wanted.amount;
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) {
            FluidStack drained = hatch.drain(ForgeDirection.UNKNOWN, new FluidStack(wanted, remaining), true);
            if (drained != null && drained.isFluidEqual(wanted)) remaining -= drained.amount;
            if (remaining == 0) break;
        }
        if (remaining != 0) return false;
        lastSteamConsumed = BigInteger.valueOf(amount);
        totalSteamConsumed = totalSteamConsumed.add(lastSteamConsumed);
        return true;
    }

    /**
     * Replaces the upstream composition counter with a Steam-Godforge-specific one.
     *
     * <p>
     * The upstream Forge groups modules only by the four original base classes
     * ({@code MTESmeltingModule}, {@code MTEMoltenModule}, {@code MTEPlasmaModule} and
     * {@code MTEExoticModule}). Several GTNC modules inherit {@code MTESmeltingModule} only to reuse the
     * Godforge GUI/sync framework, so the upstream counter treats the extractor, processing, alloy and
     * Solar Muon modules as one smelting extension.
     *
     * <p>
     * Here every concrete Steam module class counts as its own extension. Exotic and Magmatter modes remain
     * separate extension types, matching the upstream behavior. After inversion, duplicate modules contribute
     * fractional progress using the original family weights.
     */
    private void updateSteamCompositionTotal(ForgeOfGodsData data) {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Float> duplicateWeights = new HashMap<>();

        for (MTEBaseModule module : moduleHatches) {
            if (module == null) continue;

            String key = module.getClass()
                .getName();
            float duplicateWeight = 0.2f;

            if (module instanceof MTEExoticModule exoticModule) {
                boolean magmatter = exoticModule.isMagmatterModeOn();
                key += magmatter ? "#magmatter" : "#exotic";
                duplicateWeight = magmatter ? 1.0f : 0.8f;
            } else if (module instanceof MTEPlasmaModule) {
                duplicateWeight = 0.6f;
            } else if (module instanceof MTEMoltenModule) {
                duplicateWeight = 0.4f;
            }

            counts.merge(key, 1, Integer::sum);
            duplicateWeights.put(key, duplicateWeight);
        }

        float total = counts.size() + Math.max(0, data.getRingAmount() - 1);

        if (data.isInversion()) {
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                int duplicates = Math.max(0, entry.getValue() - 1);
                total += duplicates * duplicateWeights.getOrDefault(entry.getKey(), 0.2f);
            }
        }

        data.setTotalExtensionsBuilt(total);
    }

    private void applySteamModuleParameters(int fuelFactor) {
        double speedPenalty = Config.SteamForgeOfGods.speedPenaltyStages[getSpeedStage()];
        for (MTEBaseModule module : moduleHatches) {
            ensureChargeableModuleParameters(module);
            module.setCalculatedMaxParallel(calculateSteamParallel(module, fuelFactor));
            module.setSpeedBonus(module.getSpeedBonus() * speedPenalty);
            if (!getData().isUpgradeActive(ForgeOfGodsUpgrade.TBF)) {
                module.setProcessingVoltage(calculateSteamProcessingVoltage(fuelFactor));
            }
            if (!Config.SteamForgeOfGods.enablePerfectOverclock) module.setOverclockTimeFactor(2.0);
        }
    }

    /**
     * Upstream modules initialize both modifiers to zero and normally receive their first usable values from the
     * stellar Godforge's periodic parameter pass. Steam modules can become connected between those passes, so leaving
     * either value at zero creates a free recipe: ProcessingLogic calculates zero EU and the wireless-steam redirect
     * therefore has nothing to drain. A positive upstream upgrade value is always preserved.
     */
    private static void ensureChargeableModuleParameters(MTEBaseModule module) {
        if (!Double.isFinite(module.getEnergyDiscount()) || module.getEnergyDiscount() <= 0.0) {
            module.setEnergyDiscount(1.0);
        }
        if (!Double.isFinite(module.getSpeedBonus()) || module.getSpeedBonus() <= 0.0) {
            module.setSpeedBonus(1.0);
        }
    }

    private void disconnectSteamModules() {
        for (MTEBaseModule module : moduleHatches) module.disconnect();
    }

    private int calculateSteamParallel(MTEBaseModule module, int fuelFactor) {
        int stage = getParallelStage();
        int cap = Math.min(MAX_STEAM_PARALLEL, Config.SteamForgeOfGods.parallelStages[stage]);
        if (stage == 0) return cap;

        int floor = Math.min(cap, Config.SteamForgeOfGods.parallelStages[stage - 1]);
        int effective = tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath
            .calculateEffectiveFuelFactor(fuelFactor);
        int effectiveCap = tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath.calculateEffectiveFuelFactor(
            tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath.calculateMaxFuelFactor(getData()));
        double pressure = Math.max(0.0, effective - 1.0) / Math.max(1.0, effectiveCap - 1.0);
        pressure *= Config.SteamForgeOfGods.pressureParallelMultiplier;
        if (getData().isUpgradeActive(ForgeOfGodsUpgrade.TCT)) {
            pressure *= module instanceof MTESmeltingModule || module instanceof MTEMoltenModule ? 3.0 : 2.0;
        }
        if (getData().isUpgradeActive(ForgeOfGodsUpgrade.EPEC)) {
            double heatDivisor = module instanceof MTESmeltingModule || module instanceof MTEMoltenModule ? 15_000.0
                : 25_000.0;
            pressure *= 1.0 + module.getHeat() / heatDivisor;
        }
        if (getData().isUpgradeActive(ForgeOfGodsUpgrade.POS)) {
            double upgradeDivisor = module instanceof MTESmeltingModule || module instanceof MTEMoltenModule ? 5.0
                : 8.0;
            pressure *= 1.0 + getData().getUpgrades()
                .getTotalActiveUpgrades() / upgradeDivisor;
        }
        pressure = Math.min(1.0, pressure);
        return Math.min(MAX_STEAM_PARALLEL, floor + (int) Math.round((cap - floor) * pressure));
    }

    private long calculateSteamProcessingVoltage(int fuelFactor) {
        long voltage = Config.SteamForgeOfGods.baseProcessingVoltage;
        if (getData().isUpgradeActive(ForgeOfGodsUpgrade.GISS)) {
            int effective = tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath
                .calculateEffectiveFuelFactor(fuelFactor);
            long pressure = Math.max(0L, effective - 1L);
            long perPressure = Config.SteamForgeOfGods.processingVoltagePerPressure;
            if (perPressure > 0 && pressure > (Long.MAX_VALUE - voltage) / perPressure) return Long.MAX_VALUE;
            voltage += pressure * perPressure;
        }
        if (getData().isUpgradeActive(ForgeOfGodsUpgrade.NGMS)) {
            for (int i = 0; i < getData().getRingAmount(); i++) {
                if (voltage > Long.MAX_VALUE / 4L) return Long.MAX_VALUE;
                voltage *= 4L;
            }
        }
        return Math.max(1L, voltage);
    }

    private int getParallelStage() {
        return Math.min(Config.SteamForgeOfGods.parallelStages.length - 1, countActive(PARALLEL_UPGRADES));
    }

    private int getSpeedStage() {
        return Math.min(Config.SteamForgeOfGods.speedPenaltyStages.length - 1, countActive(SPEED_UPGRADES));
    }

    private int countActive(ForgeOfGodsUpgrade[] upgrades) {
        int active = 0;
        for (ForgeOfGodsUpgrade upgrade : upgrades) {
            if (getData().isUpgradeActive(upgrade)) active++;
        }
        return active;
    }

    private static long saturatingLong(BigInteger value) {
        if (value.signum() <= 0) return 0L;
        return value.bitLength() >= 63 ? Long.MAX_VALUE : value.longValue();
    }

    public int getSteamUpgradeMask() {
        int mask = 0;
        for (ForgeOfGodsUpgrade upgrade : ForgeOfGodsUpgrade.VALUES) {
            if (getData().isUpgradeActive(upgrade)) mask |= 1 << upgrade.ordinal();
        }
        return mask;
    }

    public boolean tryUnlockSteamUpgrade(int ordinal) {
        if (ordinal < 0 || ordinal >= ForgeOfGodsUpgrade.VALUES.length) return false;
        return tryUnlockSteamUpgrade(ForgeOfGodsUpgrade.VALUES[ordinal]);
    }

    public boolean tryUnlockSteamUpgrade(ForgeOfGodsUpgrade upgrade) {
        if (getData().isUpgradeActive(upgrade)) return false;
        if (!getData().getUpgrades()
            .checkPrerequisites(upgrade)) return false;
        if (!getData().getUpgrades()
            .checkSplit(upgrade, getData().getRingAmount())) return false;
        if (getData().getGravitonShardsAvailable() < upgrade.getShardCost()) return false;

        List<ItemStack> costs = SteamGodforgeUpgradeCosts.get(upgrade);
        int bit = 1 << upgrade.ordinal();
        if ((paidUpgradeMaterialMask & bit) == 0) {
            if (!hasUpgradeMaterials(costs)) return false;
            consumeUpgradeMaterials(costs);
            paidUpgradeMaterialMask |= bit;
        }
        getData().getUpgrades()
            .unlockUpgrade(upgrade);
        getData().setGravitonShardsAvailable(getData().getGravitonShardsAvailable() - upgrade.getShardCost());
        getData().setGravitonShardsSpent(getData().getGravitonShardsSpent() + upgrade.getShardCost());
        updateSlots();
        getBaseMetaTileEntity().issueTextureUpdate();
        return true;
    }

    public boolean isSteamUpgradeMaterialPaid(ForgeOfGodsUpgrade upgrade) {
        return (paidUpgradeMaterialMask & 1 << upgrade.ordinal()) != 0;
    }

    private void primeUpstreamExtraCosts() {
        for (ForgeOfGodsUpgrade upgrade : ForgeOfGodsUpgrade.VALUES) {
            if (!upgrade.hasExtraCost() || getData().getUpgrades()
                .isCostPaid(upgrade)) continue;
            ItemStack[] originalCosts = upgrade.getExtraCostNoNulls();
            ItemStack[] copies = new ItemStack[originalCosts.length];
            for (int i = 0; i < originalCosts.length; i++) copies[i] = originalCosts[i].copy();
            getData().getUpgrades()
                .payCost(upgrade, copies);
        }
    }

    public List<ItemStack> getSteamUpgradeCosts(int ordinal) {
        if (ordinal < 0 || ordinal >= ForgeOfGodsUpgrade.VALUES.length) return Collections.emptyList();
        return SteamGodforgeUpgradeCosts.get(ForgeOfGodsUpgrade.VALUES[ordinal]);
    }

    private boolean hasUpgradeMaterials(List<ItemStack> costs) {
        for (ItemStack cost : costs) {
            int found = 0;
            for (MTEHatchInputBus bus : GTUtility.validMTEList(mInputBusses)) {
                for (ItemStack stack : bus.getRealInventory()) {
                    if (stack != null && GTUtility.areStacksEqual(stack, cost)) found += stack.stackSize;
                    if (found >= cost.stackSize) break;
                }
                if (found >= cost.stackSize) break;
            }
            if (found < cost.stackSize) return false;
        }
        return true;
    }

    private void consumeUpgradeMaterials(List<ItemStack> costs) {
        for (ItemStack cost : costs) {
            int remaining = cost.stackSize;
            for (MTEHatchInputBus bus : GTUtility.validMTEList(mInputBusses)) {
                for (int slot = 0; slot < bus.getSizeInventory() && remaining > 0; slot++) {
                    ItemStack stack = bus.getStackInSlot(slot);
                    if (stack == null || !GTUtility.areStacksEqual(stack, cost)) continue;
                    int consumed = Math.min(remaining, stack.stackSize);
                    bus.decrStackSize(slot, consumed);
                    remaining -= consumed;
                }
                if (remaining == 0) break;
            }
        }
    }

    private void createSteamRenderer() {
        ChunkCoordinates renderPos = getSteamRenderPos();
        getBaseMetaTileEntity().getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, Blocks.air);
        getBaseMetaTileEntity().getWorld()
            .setBlock(renderPos.posX, renderPos.posY, renderPos.posZ, forgeOfGodsRenderBlock);
        TileEntity tile = getBaseMetaTileEntity().getWorld()
            .getTileEntity(renderPos.posX, renderPos.posY, renderPos.posZ);
        if (!(tile instanceof TileEntityForgeOfGods renderer)) return;

        boolean checksEnabled = RunnableMachineUpdate.isEnabled();
        RunnableMachineUpdate.setEnabled(false);
        try {
            buildPiece(SteamGodforgeStructures.FIRST_RING_AIR, null, false, 63, 14, -59);
            if (getData().getRingAmount() >= 2) {
                buildPiece(SteamGodforgeStructures.SECOND_RING_AIR, null, false, 55, 11, -67);
            }
            if (getData().getRingAmount() >= 3) {
                buildPiece(SteamGodforgeStructures.THIRD_RING_AIR, null, false, 47, 13, -76);
            }
        } finally {
            RunnableMachineUpdate.setEnabled(checksEnabled);
        }

        renderer.setRenderRotation(getRotation(), getDirection());
        getData().setRenderActive(true);
        updateRenderer();
        enableWorking();
    }

    private ChunkCoordinates getSteamRenderPos() {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        int x = tile.getXCoord() + 122 * getExtendedFacing().getRelativeBackInWorld().offsetX;
        int y = tile.getYCoord() + 122 * getExtendedFacing().getRelativeBackInWorld().offsetY;
        int z = tile.getZCoord() + 122 * getExtendedFacing().getRelativeBackInWorld().offsetZ;
        return new ChunkCoordinates(x, y, z);
    }

    public int getSteamParallel() {
        return Math.min(MAX_STEAM_PARALLEL, Config.SteamForgeOfGods.parallelStages[getParallelStage()]);
    }

    public double getSteamSpeedPenalty() {
        return Config.SteamForgeOfGods.speedPenaltyStages[getSpeedStage()];
    }

    UUID getSteamOwner() {
        return steamOwner;
    }

    void recordWirelessSteamConsumed(BigInteger amount) {
        if (amount != null && amount.signum() > 0) totalWirelessSteamConsumed = totalWirelessSteamConsumed.add(amount);
    }

    public String getWirelessSteamForGui() {
        return (steamOwner == null ? BigInteger.ZERO : SteamWirelessNetworkManager.getUserSteam(steamOwner)).toString();
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(
            EnumChatFormatting.AQUA + TextLocalization.STEAM_GODFORGE_INFO_WIRELESS_STEAM
                + ": "
                + EnumChatFormatting.GOLD
                + (steamOwner == null ? "0" : SteamWirelessNetworkManager.getUserSteam(steamOwner))
                + " L");
        info.add(
            EnumChatFormatting.AQUA + TextLocalization.STEAM_GODFORGE_INFO_PARALLEL
                + ": "
                + EnumChatFormatting.GREEN
                + getSteamParallel());
        info.add(
            EnumChatFormatting.AQUA + TextLocalization.STEAM_GODFORGE_INFO_SPEED_PENALTY
                + ": "
                + EnumChatFormatting.YELLOW
                + "x"
                + getSteamSpeedPenalty());
        info.add(
            EnumChatFormatting.AQUA + TextLocalization.STEAM_GODFORGE_INFO_LAST_CONTROLLER_COST
                + ": "
                + EnumChatFormatting.GOLD
                + lastSteamConsumed
                + " L");
        return info.toArray(new String[0]);
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setByteArray(NBT_TOTAL_STEAM, totalSteamConsumed.toByteArray());
        nbt.setByteArray(NBT_LAST_STEAM, lastSteamConsumed.toByteArray());
        nbt.setByteArray(NBT_TOTAL_WIRELESS_STEAM, totalWirelessSteamConsumed.toByteArray());
        nbt.setInteger(NBT_UPGRADE_MATERIALS, paidUpgradeMaterialMask);
        // Keep steam-specific mirrors of the two cold-start fields. Upstream also serializes them, but these keys make
        // the steam controller independent of upstream load ordering and protect existing charge across reloads.
        nbt.setInteger(NBT_INTERNAL_BATTERY, getData().getInternalBattery());
        nbt.setInteger(NBT_CRITICAL_PHOTONS, getData().getStellarFuelAmount());
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        if (nbt.hasKey(NBT_TOTAL_STEAM)) totalSteamConsumed = new BigInteger(nbt.getByteArray(NBT_TOTAL_STEAM));
        if (nbt.hasKey(NBT_LAST_STEAM)) lastSteamConsumed = new BigInteger(nbt.getByteArray(NBT_LAST_STEAM));
        totalWirelessSteamConsumed = nbt.hasKey(NBT_TOTAL_WIRELESS_STEAM)
            ? new BigInteger(nbt.getByteArray(NBT_TOTAL_WIRELESS_STEAM))
            : getData().getTotalPowerConsumed();
        paidUpgradeMaterialMask = nbt.getInteger(NBT_UPGRADE_MATERIALS);
        if (nbt.hasKey(NBT_INTERNAL_BATTERY)) {
            getData().setInternalBattery(Math.max(0, nbt.getInteger(NBT_INTERNAL_BATTERY)));
        }
        if (nbt.hasKey(NBT_CRITICAL_PHOTONS)) {
            getData().setStellarFuelAmount(Math.max(0, nbt.getInteger(NBT_CRITICAL_PHOTONS)));
        }
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamForgeOfGodsGui(this);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(TextLocalization.SteamForgeOfGodsMachineType)
            .addInfo(TextLocalization.Tooltip_SteamForgeOfGods_00)
            .addInfo(TextLocalization.Tooltip_SteamForgeOfGods_01)
            .addInfo(TextLocalization.Tooltip_SteamForgeOfGods_02)
            .beginStructureBlock(186, 127, 29, true)
            .addController(TextLocalization.Tooltip_SteamForgeOfGods_Controller)
            .addInputBus("1", TextLocalization.Tooltip_SteamForgeOfGods_Casing, 1)
            .addInputHatch("1", TextLocalization.Tooltip_SteamForgeOfGods_Casing, 1)
            .addOutputBus("1", TextLocalization.Tooltip_SteamForgeOfGods_Casing, 1)
            .addMiscHatch(
                "0-16",
                TextLocalization.Tooltip_SteamForgeOfGods_Module,
                TextLocalization.Tooltip_SteamForgeOfGods_ModuleSocket,
                2)
            .toolTipFinisher();
    }
}
