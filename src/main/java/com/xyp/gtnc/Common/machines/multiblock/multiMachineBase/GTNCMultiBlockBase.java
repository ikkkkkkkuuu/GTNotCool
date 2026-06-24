package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Base class for electric multi-block machines with upgrade support.
 * Insert a High Computing Power Chip into the controller slot to boost speed and parallel.
 * <ul>
 * <li>Each chip tier: +20% speed, +16 parallel recipes</li>
 * <li>Tier 1: 1.20x speed, +16 parallel</li>
 * <li>Tier 7: 2.40x speed, +112 parallel</li>
 * </ul>
 */
public abstract class GTNCMultiBlockBase<T extends GTNCMultiBlockBase<T>> extends MTEExtendedPowerMultiBlockBase<T> {

    protected int mUpgradeTier = 0;
    protected boolean mUpgraded = false;

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
    // # §bInsert chip into controller UI to upgrade machine
    // # zh_CN §b可以在机器主机UI中插入高算力芯片来升级机器

    // #tr Tooltip_GTNC_Upgrade_01
    // # §bInsert High Computing Power into controller to boost speed and parallel
    // # zh_CN §b在控制器中插入高算力芯片以提升速度和并行

    // #tr Tooltip_GTNC_Upgrade_02
    // # §bEach tier provides 20% speed boost and 16 parallel
    // # zh_CN §b芯片等级每提升一级,提供20%的运行速度加成和16的并行

    /**
     * Check controller slot for upgrade item and apply upgrade.
     * Call this from subclass onPostTick if subclass overrides it.
     */
    protected void checkUpgrade(IGregTechTileEntity aBaseMetaTileEntity) {
        if (mUpgraded) return;
        ItemStack aGuiStack = this.getControllerSlot();
        if (aGuiStack == null) return;
        for (int i = 7; i >= 1; i--) {
            GTNCItemList chip = GTNCItemList.valueOf("ChipTier" + i);
            if (chip.hasBeenSet() && GTUtility.areStacksEqual(aGuiStack, chip.get(1))) {
                this.mUpgraded = true;
                this.mUpgradeTier = i;
                return;
            }
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0) {
            checkUpgrade(aBaseMetaTileEntity);
        }
    }

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
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mUpgradeTier = aNBT.getInteger("mUpgradeTier");
        mUpgraded = aNBT.getBoolean("mUpgraded");
    }

}
