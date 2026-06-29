package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.xyp.gtnc.Common.gui.modularui.multiblock.GTNCMultiBlockBaseGui;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Base class for electric multi-block machines with chip upgrade support.
 * Submit High Computing Power Chips through the Upgrade Tree panel to boost speed and parallel.
 * <ul>
 * <li>Each chip tier: +20% speed, +16 parallel recipes</li>
 * <li>Tier 1: 1.20x speed, +16 parallel</li>
 * <li>Tier 7: 2.40x speed, +112 parallel</li>
 * </ul>
 */
public abstract class GTNCMultiBlockBase<T extends GTNCMultiBlockBase<T>> extends MTEExtendedPowerMultiBlockBase<T> {

    protected int mUpgradeTier = 0;
    protected boolean mUpgraded = false;
    /** Paid cost indices for the upgrade tree. Persisted across chunk reloads. */
    public Set<Integer> paidUpgradeCostIndices = new HashSet<>();

    /**
     * Override in subclass to define required items for upgrade.
     * Default: ChipTier1 as example.
     */
    public List<ItemStack> getUpgradeCosts() {
        List<ItemStack> costs = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            GTNCItemList chip = GTNCItemList.valueOf("ChipTier" + i);
            if (chip.hasBeenSet()) {
                costs.add(chip.get(1));
            }
        }
        return costs;
    }

    public GTNCMultiBlockBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTNCMultiBlockBase(String aName) {
        super(aName);
    }

    // #tr Tooltip_GTNC_PerfectOverclock
    // # Perfect Overclock: Overclock beyond recipe voltage has no efficiency loss
    // # §b超频时不会损失效率

    // #tr Tooltip_GTNC_SupportsTecTechMultiAmp
    // # §3Supports TecTech Multi-Amp Energy Hatches
    // # zh_CN §3支持TecTech多A能源仓

    // #tr Tooltip_GTNC_Upgrade_00
    // # §bSubmit High Computing Power Chips through the Upgrade Tree to upgrade
    // # zh_CN §b通过天途提交高算力芯片以升级机器

    // #tr Tooltip_GTNC_Upgrade_01
    // # §bEach chip tier increases recipe tier, speed and parallel processing
    // # zh_CN §b芯片每提升一级,提升速度加成和并行处理

    // #tr Tooltip_GTNC_Upgrade_02
    // # §bEach tier provides 20% speed boost and 16 parallel
    // # zh_CN §b芯片等级每提升一级,提供20%的运行速度加成和16的并行

    /**
     * Speed bonus from upgrade: 1.0 / (1.0 + tier * 0.2), e.g. T3 → ÷1.6 ≈ 0.625x time
     */
    protected float getUpgradeSpeedBonus() {
        if (!mUpgraded || mUpgradeTier <= 0) return 1.0f;
        return (float) Math.max(0.01, 1.0 / (1.0 + mUpgradeTier * 0.2));
    }

    /**
     * Parallel bonus from upgrade: tier * 16
     */
    protected int getUpgradeParallelBonus() {
        if (!mUpgraded) return 0;
        return mUpgradeTier * 16;
    }

    /**
     * Called by the upgrade tree GUI after materials are consumed.
     * Finds the highest chip tier among costs and applies the upgrade.
     * Override in subclass for additional behavior (e.g. wireless mode).
     */
    public void onUpgradeComplete() {
        int highestTier = 0;
        List<ItemStack> costs = getUpgradeCosts();
        for (int idx : paidUpgradeCostIndices) {
            if (idx >= costs.size()) continue;
            ItemStack cost = costs.get(idx);
            for (int i = 7; i >= 1; i--) {
                GTNCItemList chip = GTNCItemList.valueOf("ChipTier" + i);
                if (chip.hasBeenSet() && GTUtility.areStacksEqual(cost, chip.get(1))) {
                    if (i > highestTier) highestTier = i;
                    break;
                }
            }
        }
        if (highestTier > mUpgradeTier) {
            mUpgradeTier = highestTier;
            mUpgraded = true;
        }
    }

    // #tr GTNC.info.upgradeTier
    // # Chip Tier
    // # zh_CN 芯片等级

    // #tr GTNC.info.upgradeSpeed
    // # Speed
    // # zh_CN 速度

    // #tr GTNC.info.upgradeParallel
    // # Parallel
    // # zh_CN 并行

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        int tier = tag.getInteger("upgradeTier");
        if (tier > 0) {
            currenttip.add(
                StatCollector.translateToLocal("GTNC.info.upgradeTier") + ": "
                    + EnumChatFormatting.GOLD
                    + tier
                    + EnumChatFormatting.RESET);
            currenttip.add(
                StatCollector.translateToLocal("GTNC.info.upgradeSpeed") + ": "
                    + EnumChatFormatting.GREEN
                    + String.format("%.2fx", 1.0f / tag.getFloat("upgradeSpeed"))
                    + EnumChatFormatting.RESET);
            currenttip.add(
                StatCollector.translateToLocal("GTNC.info.upgradeParallel") + ": "
                    + EnumChatFormatting.AQUA
                    + "+"
                    + tag.getInteger("upgradeParallel")
                    + EnumChatFormatting.RESET);
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("upgradeTier", mUpgradeTier);
        tag.setFloat("upgradeSpeed", getUpgradeSpeedBonus());
        tag.setInteger("upgradeParallel", getUpgradeParallelBonus());
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mUpgradeTier", mUpgradeTier);
        aNBT.setBoolean("mUpgraded", mUpgraded);
        if (!paidUpgradeCostIndices.isEmpty()) {
            int[] arr = new int[paidUpgradeCostIndices.size()];
            int i = 0;
            for (int idx : paidUpgradeCostIndices) {
                arr[i++] = idx;
            }
            aNBT.setIntArray("paidUpgradeCostIndices", arr);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mUpgradeTier = aNBT.getInteger("mUpgradeTier");
        mUpgraded = aNBT.getBoolean("mUpgraded");
        paidUpgradeCostIndices.clear();
        if (aNBT.hasKey("paidUpgradeCostIndices")) {
            for (int idx : aNBT.getIntArray("paidUpgradeCostIndices")) {
                paidUpgradeCostIndices.add(idx);
            }
        }
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new GTNCMultiBlockBaseGui<>(this);
    }

}
