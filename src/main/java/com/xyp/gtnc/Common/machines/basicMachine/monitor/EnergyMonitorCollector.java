package com.xyp.gtnc.Common.machines.basicMachine.monitor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEWirelessEnergy;
import gregtech.common.misc.WirelessNetworkManager;
import kekztech.common.tileentities.MTELapotronicSuperCapacitor;
import lombok.Getter;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;

/**
 * Builds the server-side snapshot consumed by the energy monitor GUI.
 *
 * <p>
 * The registry deliberately tracks GregTech base types rather than a list of
 * GTNC machine classes. This keeps the monitor useful for other GTNH addons too.
 */
public final class EnergyMonitorCollector {

    private static final Comparator<EnergyMonitorRowSnapshot> ROW_DISPLAY_ORDER = Comparator
        .comparing(
            (EnergyMonitorRowSnapshot row) -> row.getEut()
                .abs(),
            Comparator.reverseOrder())
        .thenComparing(EnergyMonitorRowSnapshot::getDisplayName)
        .thenComparing(EnergyMonitorRowSnapshot::getOwnerName)
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getDimensionId())
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getX())
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getY())
        .thenComparingInt(
            row -> row.getHighlightTarget()
                .getZ());

    private EnergyMonitorCollector() {}

    public static EnergyMonitorSnapshot collect(UUID monitorOwnerUuid) {
        if (monitorOwnerUuid == null) {
            return EnergyMonitorSnapshot.empty();
        }
        WirelessTeam.TeamContext team = WirelessTeam.resolveContext(monitorOwnerUuid);
        UUID leader = team.getLeader();
        Set<UUID> members = team.getMembers();
        if (leader == null || members.isEmpty()) {
            return EnergyMonitorSnapshot.empty();
        }

        BigInteger wiredStored = BigInteger.ZERO;
        BigInteger wiredCapacity = BigInteger.ZERO;
        List<EnergyMonitorRowSnapshot> rows = new ArrayList<>();
        for (MetaTileEntity meta : EnergyMonitorRegistry.snapshot()) {
            IGregTechTileEntity base = meta.getBaseMetaTileEntity();
            if (base == null || base.isDead() || EnergyMonitorRegistry.isInvalid(meta)) {
                EnergyMonitorRegistry.unregister(meta);
                continue;
            }
            UUID owner = base.getOwnerUuid();
            if (owner == null || !members.contains(owner)) {
                continue;
            }
            World world = base.getWorld();
            if (world == null || world.isRemote
                || !world.blockExists(base.getXCoord(), base.getYCoord(), base.getZCoord())) {
                continue;
            }
            if (meta instanceof MTELapotronicSuperCapacitor capacitor) {
                wiredStored = wiredStored.add(capacitor.getStored());
                wiredCapacity = wiredCapacity.add(capacitor.getEnergyCapacity());
            }
            rows.addAll(createMachineRows(meta, base));
        }
        rows.sort(ROW_DISPLAY_ORDER);
        return new EnergyMonitorSnapshot(rows, wiredStored, wiredCapacity, WirelessNetworkManager.getUserEU(leader));
    }

    public static EnergyMonitorSummarySnapshot createSummary(EnergyMonitorSnapshot snapshot,
        EnergyMonitorMode totalMode, EnergyMonitorMode statsMode) {
        EnergyMonitorSnapshot safe = snapshot == null ? EnergyMonitorSnapshot.empty() : snapshot;
        BigInteger wiredStored = safe.getWiredStored();
        BigInteger wiredCapacity = safe.getWiredCapacity();
        BigInteger wirelessStored = safe.getWirelessStored();
        BigInteger statisticsTotal = calculateStatisticsTotal(safe.getRows(), statsMode);
        BigInteger totalEnergy = switch (totalMode) {
            case WIRED -> wiredStored;
            case WIRELESS -> wirelessStored;
            case ALL -> wiredStored.add(wirelessStored);
        };

        EnergyMonitorSummarySnapshot summary = EnergyMonitorSummarySnapshot.empty();
        summary.setTotalEnergyText(
            formatTotalEnergyText(totalMode, totalEnergy, wiredStored, wiredCapacity, wirelessStored));
        BigInteger magnitude = statisticsTotal.abs();
        boolean consuming = statisticsTotal.signum() < 0;
        int voltageTier = magnitude.signum() == 0 ? 0 : EnergyMonitorFormatter.getVoltageTier(magnitude);
        summary.setAverageEuText(EnergyMonitorFormatter.formatBigInteger(magnitude));
        summary.setAmpText(EnergyMonitorFormatter.formatAmps(magnitude, voltageTier));
        summary.setVoltageTier(voltageTier);
        summary.setOutputMode(consuming);
        summary.setEstimatedEmpty(consuming);

        if (magnitude.signum() == 0) {
            summary.setEstimatedTimeText("gtnc.energy_monitor.never_fill");
        } else if (!consuming) {
            if (totalMode == EnergyMonitorMode.WIRED) {
                BigInteger remaining = wiredCapacity.subtract(wiredStored)
                    .max(BigInteger.ZERO);
                summary.setEstimatedTimeText(EnergyMonitorFormatter.formatDuration(remaining.divide(magnitude)));
            } else {
                summary.setEstimatedTimeText("gtnc.energy_monitor.never_fill");
            }
        } else {
            BigInteger source = totalMode == EnergyMonitorMode.WIRED ? wiredStored : totalEnergy;
            summary.setEstimatedTimeText(
                EnergyMonitorFormatter.formatDuration(
                    source.max(BigInteger.ZERO)
                        .divide(magnitude)));
        }
        return summary;
    }

    public static VisibleRowsResult getVisibleRowsResult(List<EnergyMonitorRowSnapshot> rows, EnergyMonitorMode mode,
        int visibleRowCount) {
        if (rows == null || rows.isEmpty()) {
            return new VisibleRowsResult(Collections.emptyList(), false);
        }
        int limit = Math.max(40, visibleRowCount);
        List<EnergyMonitorRowSnapshot> visible = new ArrayList<>(limit);
        boolean more = false;
        for (EnergyMonitorRowSnapshot row : rows) {
            if (!matchesMode(row, mode)) continue;
            if (visible.size() >= limit) {
                more = true;
                break;
            }
            visible.add(row);
        }
        return new VisibleRowsResult(visible, more);
    }

    public static List<EnergyMonitorRowSnapshot> getVisibleRows(List<EnergyMonitorRowSnapshot> rows,
        EnergyMonitorMode mode, int visibleRowCount) {
        return getVisibleRowsResult(rows, mode, visibleRowCount).getRows();
    }

    public static boolean hasMoreRows(List<EnergyMonitorRowSnapshot> rows, EnergyMonitorMode mode,
        int visibleRowCount) {
        return getVisibleRowsResult(rows, mode, visibleRowCount).hasMoreRows();
    }

    public static BigInteger calculateStatisticsTotal(List<EnergyMonitorRowSnapshot> rows, EnergyMonitorMode mode) {
        BigInteger total = BigInteger.ZERO;
        if (rows == null) return total;
        for (EnergyMonitorRowSnapshot row : rows) {
            if (matchesMode(row, mode)) {
                total = total.add(row.getEut());
            }
        }
        return total;
    }

    private static String formatTotalEnergyText(EnergyMonitorMode mode, BigInteger total, BigInteger wired,
        BigInteger capacity, BigInteger wireless) {
        return switch (mode) {
            case WIRED -> EnergyMonitorFormatter.formatCompactBigInteger(wired) + " / "
                + EnergyMonitorFormatter.formatCompactBigInteger(capacity)
                + " EU ("
                + EnergyMonitorFormatter.formatPercentage(wired, capacity)
                + ")";
            case WIRELESS -> EnergyMonitorFormatter.formatCompactBigInteger(wireless) + " EU";
            case ALL -> EnergyMonitorFormatter.formatCompactBigInteger(wired) + " + "
                + EnergyMonitorFormatter.formatCompactBigInteger(wireless)
                + " EU";
        };
    }

    private static boolean matchesMode(EnergyMonitorRowSnapshot row, EnergyMonitorMode mode) {
        if (row == null) return false;
        return switch (mode) {
            case ALL -> true;
            case WIRED -> !row.isWireless();
            case WIRELESS -> row.isWireless();
        };
    }

    private static List<EnergyMonitorRowSnapshot> createMachineRows(MetaTileEntity meta, IGregTechTileEntity base) {
        EnergyMonitorRowSnapshot row = null;
        if (meta instanceof MTEBasicMachine basic) {
            row = buildRow(meta, base, BigInteger.valueOf(-basic.mEUt), EnergyMonitorCategory.BASIC_MACHINE, false);
        } else if (meta instanceof MTELapotronicSuperCapacitor capacitor) {
            BigInteger eut = base.isActive() ? BigInteger.valueOf(
                capacitor.getEnergyInputValues()
                    .avgLong())
                .subtract(
                    BigInteger.valueOf(
                        capacitor.getEnergyOutputValues()
                            .avgLong()))
                : BigInteger.ZERO;
            row = buildRow(meta, base, eut, EnergyMonitorCategory.MULTIBLOCK, capacitor.isWireless_mode());
        } else if (meta instanceof MTEMultiBlockBase multiblock) {
            boolean wireless = readBooleanField(meta, "wirelessMode");
            BigInteger eut = BigInteger.valueOf(multiblock.mEUt);
            if (multiblock instanceof MTEExtendedPowerMultiBlockBase<?>extended && extended.lEUt != 0) {
                eut = BigInteger.valueOf(extended.lEUt);
            }
            row = buildRow(meta, base, eut, EnergyMonitorCategory.MULTIBLOCK, wireless);
        } else if (meta instanceof MTEHatchWirelessMulti input) {
            long voltage = GTValues.V[Math.min(input.mTier, GTValues.V.length - 1)];
            row = buildRow(
                meta,
                base,
                BigInteger.valueOf(voltage)
                    .multiply(BigInteger.valueOf(-input.getAmperes())),
                EnergyMonitorCategory.HATCH,
                true);
        } else if (meta instanceof MTEHatchWirelessDynamoMulti output) {
            long voltage = GTValues.V[Math.min(output.mTier, GTValues.V.length - 1)];
            row = buildRow(
                meta,
                base,
                BigInteger.valueOf(voltage)
                    .multiply(BigInteger.valueOf(output.Amperes)),
                EnergyMonitorCategory.HATCH,
                true);
        } else if (meta instanceof MTEWirelessEnergy wirelessEnergy) {
            long voltage = GTValues.V[Math.min(wirelessEnergy.mTier, GTValues.V.length - 1)];
            row = buildRow(
                meta,
                base,
                BigInteger.valueOf(-2L)
                    .multiply(BigInteger.valueOf(voltage)),
                EnergyMonitorCategory.HATCH,
                true);
        } else if (meta instanceof MTEHatch) {
            return Collections.emptyList();
        }
        return row == null ? Collections.emptyList() : Collections.singletonList(row);
    }

    private static EnergyMonitorRowSnapshot buildRow(MetaTileEntity meta, IGregTechTileEntity base, BigInteger eut,
        EnergyMonitorCategory category, boolean wireless) {
        if (eut == null || eut.signum() == 0 || !base.isActive()) {
            return null;
        }
        ItemStack icon = getMachineDisplayStack(meta);
        EnergyMonitorRowSnapshot row = new EnergyMonitorRowSnapshot();
        row.setIconStack(icon);
        row.setDisplayName(icon != null && icon.getDisplayName() != null ? icon.getDisplayName() : meta.getLocalName());
        String ownerName = base.getOwnerName();
        row.setOwnerName(ownerName == null || ownerName.isEmpty() ? String.valueOf(base.getOwnerUuid()) : ownerName);
        row.setEut(eut);
        row.setCategory(category);
        row.setWireless(wireless);
        row.setHighlightTarget(
            new EnergyMonitorHighlightTarget(
                base.getWorld().provider.dimensionId,
                base.getXCoord(),
                base.getYCoord(),
                base.getZCoord()));
        return row;
    }

    private static ItemStack getMachineDisplayStack(MetaTileEntity meta) {
        try {
            return meta.getStackForm(1L);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static boolean readBooleanField(Object instance, String name) {
        Class<?> type = instance.getClass();
        while (type != null) {
            try {
                java.lang.reflect.Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.getBoolean(instance);
            } catch (ReflectiveOperationException ignored) {
                type = type.getSuperclass();
            }
        }
        return false;
    }

    @Getter
    public static final class VisibleRowsResult {

        private final List<EnergyMonitorRowSnapshot> rows;
        private final boolean moreRows;

        public VisibleRowsResult(List<EnergyMonitorRowSnapshot> rows, boolean moreRows) {
            this.rows = rows;
            this.moreRows = moreRows;
        }

        public boolean hasMoreRows() {
            return moreRows;
        }
    }
}
