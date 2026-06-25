package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.common.misc.WirelessNetworkManager;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class GTNCWirelessEnergyMultiMachineBase<T extends GTNCWirelessEnergyMultiMachineBase<T>>
    extends GTNCMultiBlockBase<T> {

    // #tr Waila_WirelessMode
    // # Wireless Mode
    // # zh_CN 无线模式

    // #tr Waila_CurrentEuCost
    // # EU Cost
    // # zh_CN EU消耗

    protected UUID ownerUUID;
    protected boolean wirelessMode = false;
    protected BigInteger costingEU = BigInteger.ZERO;
    protected String costingEUText = "0";

    public GTNCWirelessEnergyMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTNCWirelessEnergyMultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (ownerUUID == null) {
            ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0 && wirelessMode && (!mEnergyHatches.isEmpty() || !mExoticEnergyHatches.isEmpty())) {
            wirelessMode = false;
        }
    }

    @Override
    protected void checkUpgrade(IGregTechTileEntity aBaseMetaTileEntity) {
        ItemStack aGuiStack = this.getControllerSlot();
        if (aGuiStack == null) return;
        for (int i = 7; i >= 1; i--) {
            GTNCItemList chip = GTNCItemList.valueOf("ChipTier" + i);
            if (chip.hasBeenSet() && GTUtility.areStacksEqual(aGuiStack, chip.get(1))) {
                if (i > mUpgradeTier) {
                    mUpgraded = true;
                    mUpgradeTier = i;
                    aGuiStack.stackSize--;
                    if (aGuiStack.stackSize <= 0) {
                        this.mInventory[1] = null;
                    }
                    if (i == 7 && mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty()) {
                        this.wirelessMode = true;
                    }
                }
                return;
            }
        }
    }

    @Override
    public long getMaxInputVoltage() {
        if (wirelessMode) return Long.MAX_VALUE;
        return super.getMaxInputVoltage();
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        costingEU = BigInteger.ZERO;
        costingEUText = "0";
        if (!wirelessMode || ownerUUID == null || !mEnergyHatches.isEmpty() || !mExoticEnergyHatches.isEmpty()) {
            return super.checkProcessing();
        }

        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        int cycleNum = getWirelessCycleNum();
        ArrayList<ItemStack> accItems = new ArrayList<>();
        ArrayList<FluidStack> accFluids = new ArrayList<>();

        for (int i = 0; i < cycleNum; i++) {
            CheckRecipeResult r = wirelessModeProcessOnce();
            if (!r.wasSuccessful()) {
                if (!succeeded) finalResult = r;
                break;
            }
            mergeItemStacks(accItems, mOutputItems);
            mergeFluidStacks(accFluids, mOutputFluids);
            succeeded = true;
        }

        mOutputItems = accItems.toArray(new ItemStack[0]);
        mOutputFluids = accFluids.toArray(new FluidStack[0]);
        if (!succeeded) return finalResult;

        costingEUText = NumberFormatUtil.formatNumber(costingEU);
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = getWirelessModeProcessingTime();
        lEUt = 0;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    private CheckRecipeResult wirelessModeProcessOnce() {
        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) return result;

        long euCost = Math.abs(lEUt) > 0 ? Math.abs(lEUt) : Math.abs(mEUt) * (long) mMaxProgresstime;
        if (euCost <= 0) euCost = 1;
        if (!WirelessNetworkManager.addEUToGlobalEnergyMap(ownerUUID, -euCost)) {
            return CheckRecipeResultRegistry.insufficientPower(euCost);
        }
        costingEU = costingEU.add(BigInteger.valueOf(euCost));
        return result;
    }

    protected int getWirelessCycleNum() {
        return 200;
    }

    protected int getWirelessModeProcessingTime() {
        return 128;
    }

    private static String formatEUCost(long cost) {
        if (cost <= 0) return "0 EU";
        int tier = 0;
        for (int i = GTValues.V.length - 1; i >= 0; i--) {
            if (cost >= GTValues.V[i]) {
                tier = i;
                break;
            }
        }
        long amps = Math.max(1, cost / GTValues.V[tier]);
        return amps + "A " + GTValues.VN[tier] + " (" + NumberFormatUtil.formatNumber(cost) + " EU)";
    }

    /** Merge same items into one stack (up to maxStackSize), creating overflow stacks as needed. */
    private static void mergeItemStacks(ArrayList<ItemStack> acc, ItemStack[] items) {
        if (items == null) return;
        for (ItemStack toMerge : items) {
            if (toMerge == null) continue;
            boolean merged = false;
            for (ItemStack existing : acc) {
                if (GTUtility.areStacksEqual(toMerge, existing) && existing.stackSize < existing.getMaxStackSize()) {
                    int space = existing.getMaxStackSize() - existing.stackSize;
                    int add = Math.min(toMerge.stackSize, space);
                    existing.stackSize += add;
                    if (add >= toMerge.stackSize) {
                        merged = true;
                        break;
                    }
                    ItemStack remainder = toMerge.copy();
                    remainder.stackSize = toMerge.stackSize - add;
                    toMerge = remainder;
                }
            }
            if (!merged) acc.add(toMerge.copy());
        }
    }

    /** Merge same fluids (up to Integer.MAX_VALUE), creating overflow stacks as needed. */
    private static void mergeFluidStacks(ArrayList<FluidStack> acc, FluidStack[] fluids) {
        if (fluids == null) return;
        for (FluidStack toMerge : fluids) {
            if (toMerge == null) continue;
            boolean merged = false;
            for (FluidStack existing : acc) {
                if (existing.isFluidEqual(toMerge)) {
                    long sum = (long) existing.amount + toMerge.amount;
                    if (sum <= Integer.MAX_VALUE) {
                        existing.amount = (int) sum;
                        merged = true;
                        break;
                    }
                    existing.amount = Integer.MAX_VALUE;
                    FluidStack overflow = toMerge.copy();
                    overflow.amount = (int) (sum - Integer.MAX_VALUE);
                    toMerge = overflow;
                }
            }
            if (!merged) acc.add(toMerge.copy());
        }
    }

    protected void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            logic.setAvailableVoltage(Long.MAX_VALUE);
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(false);
            return;
        }
        logic.setAvailableVoltage(getMaxInputEu());
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(true);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currenttip.add(
                EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode")
                    + EnumChatFormatting.RESET);
            currenttip.add(
                StatCollector.translateToLocal("Waila_CurrentEuCost") + ": "
                    + EnumChatFormatting.GOLD
                    + formatEUCost(tag.getLong("costingEU"))
                    + EnumChatFormatting.RESET);
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setBoolean("wirelessMode", wirelessMode);
        if (wirelessMode) {
            tag.setLong("costingEU", costingEU.longValue());
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessMode", wirelessMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        wirelessMode = aNBT.getBoolean("wirelessMode");
    }

    @Override
    public String[] getInfoData() {
        List<String> infoData = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode) {
            infoData.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            infoData.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + formatEUCost(costingEU.longValue())
                    + EnumChatFormatting.RESET);
        }
        return infoData.toArray(new String[0]);
    }
}
