package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBlockBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Intermediate base class for all GT-Not-Cool Large Steam multiblock machines.
 * Provides unified implementations for input/output hatch and bus handling,
 * including ME hatch support and color-filtered fluid/item retrieval.
 */
public abstract class GTNCSteamMultiBlockBase<T extends GTNCSteamMultiBlockBase<T>> extends MTESteamMultiBlockBase<T> {

    public GTNCSteamMultiBlockBase(String aName) {
        super(aName);
    }

    public GTNCSteamMultiBlockBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    // region Fluid I/O

    @Override
    public ArrayList<FluidStack> getAllSteamStacks() {
        ArrayList<FluidStack> stacks = super.getAllSteamStacks();
        FluidStack steam = Materials.Steam.getGas(1);
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluid : meHatch.getStoredFluids()) {
                    if (fluid != null && fluid.isFluidEqual(steam)) {
                        stacks.add(fluid);
                    }
                }
            }
        }
        return stacks;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();

        for (MTEHatchCustomFluidBase tHatch : GTUtility.validMTEList(mSteamInputFluids)) {
            byte hatchColor = tHatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            if (tHatch.getFillableStack() != null) {
                rList.add(tHatch.getFillableStack());
            }
        }

        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            byte hatchColor = tHatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            if (tHatch instanceof MTEHatchInputME meHatch) {
                if (cachedMEInputFluids != null) {
                    // Use cached FluidStack references so that ProcessingLogic
                    // modifications persist across getStoredFluidsForColor() calls
                    for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                        if (fluidStack != null) {
                            FluidStack cached = cachedMEInputFluids.get(fluidStack.getFluid());
                            if (cached != null) {
                                inputsFromME.put(cached.getFluid(), cached);
                            }
                        }
                    }
                } else {
                    for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                        if (fluidStack != null) {
                            inputsFromME.put(fluidStack.getFluid(), fluidStack);
                        }
                    }
                }
            } else {
                FluidStack fillableStack = tHatch.getFillableStack();
                if (fillableStack != null) {
                    rList.add(fillableStack);
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    @Override
    public boolean depleteInput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        // First try ME input hatches (with proper recipe processing context)
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            if (tHatch instanceof MTEHatchInputME meHatch) {
                meHatch.startRecipeProcessing();
                FluidStack drained = meHatch.drain(ForgeDirection.UNKNOWN, aLiquid, false);
                if (drained != null && drained.amount >= aLiquid.amount) {
                    meHatch.drain(ForgeDirection.UNKNOWN, aLiquid, true);
                    meHatch.endRecipeProcessing(this);
                    return true;
                }
                meHatch.endRecipeProcessing(this);
            } else {
                FluidStack tLiquid = tHatch.getFluid();
                if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                    tLiquid = tHatch.drain(aLiquid.amount, false);
                    if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                        tLiquid = tHatch.drain(aLiquid.amount, true);
                        return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                    }
                }
            }
        }
        // Fallback to regular steam hatches (mSteamInputFluids)
        return super.depleteInput(aLiquid);
    }

    @Override
    public int getTotalSteamCapacity() {
        int cap = super.getTotalSteamCapacity();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            cap += tHatch.getCapacity();
        }
        return cap;
    }

    /**
     * Overrides the base tryConsumeSteam to skip the {@code getTotalSteamStored()} pre-check.
     * <p>
     * The base implementation short-circuits when {@code getTotalSteamStored() <= 0},
     * but {@code getAllSteamStacks()} calls {@code MTEHatchInputME.getStoredFluids()} without
     * {@code startRecipeProcessing()}, so ME hatches may report zero fluid in the pre-check.
     * The actual {@link #depleteInput(FluidStack)} method properly calls
     * {@code startRecipeProcessing()} before draining, so we go straight to it.
     */
    @Override
    public boolean tryConsumeSteam(int aAmount) {
        return this.depleteInput(Materials.Steam.getGas(aAmount));
    }

    // endregion

    // region Item I/O

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) continue;
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tHatch.getStackInSlot(i);
                if (itemStack != null) rList.add(itemStack);
            }
        }
        for (MTEHatchSteamBusInput tHatch : GTUtility.validMTEList(mSteamInputs)) {
            byte hatchColor = tHatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(i);
                if (itemStack != null) rList.add(itemStack);
            }
        }
        ItemStack stackInSlot1 = getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(stackInSlot1)) rList.add(stackInSlot1);
        return rList;
    }

    @Override
    public boolean depleteInput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return false;
        FluidStack aLiquid = GTUtility.getFluidForFilledItem(aStack, true);
        if (aLiquid != null) return depleteInput(aLiquid);
        for (MTEHatchSteamBusInput tHatch : GTUtility.validMTEList(mSteamInputs)) {
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; i >= 0; i--) {
                if (GTUtility.areStacksEqual(
                    aStack,
                    tHatch.getBaseMetaTileEntity()
                        .getStackInSlot(i))) {
                    if (tHatch.getBaseMetaTileEntity()
                        .getStackInSlot(i).stackSize >= aStack.stackSize) {
                        tHatch.getBaseMetaTileEntity()
                            .decrStackSize(i, aStack.stackSize);
                        return true;
                    }
                }
            }
        }
        for (MTEHatchInputBus tHatch : GTUtility.validMTEList(mInputBusses)) {
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getSizeInventory() - 1; i >= 0; i--) {
                if (GTUtility.areStacksEqual(aStack, tHatch.getStackInSlot(i))) {
                    if (tHatch.getStackInSlot(i).stackSize >= aStack.stackSize) {
                        tHatch.getBaseMetaTileEntity()
                            .decrStackSize(i, aStack.stackSize);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // endregion

    // region Cross-Recipe Parallel Processing

    // #tr Tooltip_GTNC_CrossRecipeParallel
    // # Cross-Recipe Parallel: Can process multiple different recipes at once
    // # 跨配方并行：可一次处理多个不同配方
    // #tr Tooltip_GTNC_CrossRecipeDuration
    // # Processing time = Sum of all recipes' overclocked durations
    // # 处理时长 = 所有配方超频后时长之和

    /**
     * Enable cross-recipe parallel processing (processes multiple recipes per cycle).
     * Subclasses may override to disable.
     */
    protected boolean crossRecipeParallelEnabled = true;

    private boolean inCrossRecipeProcessing = false;

    /**
     * Cached FluidStack references from ME input hatches during cross-recipe processing.
     * <p>
     * The base ProcessingLogic modifies FluidStack amounts on local copies returned by
     * {@link #getStoredFluidsForColor(Optional)}. For regular hatches, {@code getFillableStack()}
     * returns references to the actual stored fluid, so modifications persist across calls.
     * ME hatches return copies via {@code getStoredFluids()}, breaking the exit condition
     * and causing inputs to never be consumed.
     * <p>
     * This cache ensures the same FluidStack references are returned throughout the recipe
     * check cycle, and the consumed amounts are drained from ME hatches afterwards.
     */
    private Map<Fluid, FluidStack> cachedMEInputFluids = null;
    private Map<Fluid, Integer> originalMEAmounts = null;

    @Override
    public CheckRecipeResult checkProcessing() {
        enableHigherRecipe = getUpgradeTier(getControllerSlot());
        if (!crossRecipeParallelEnabled || inCrossRecipeProcessing) {
            return super.checkProcessing();
        }
        inCrossRecipeProcessing = true;
        try {
            return checkProcessingCrossRecipe();
        } finally {
            inCrossRecipeProcessing = false;
        }
    }

    private CheckRecipeResult checkProcessingCrossRecipe() {
        // Cache ME input fluids so that ProcessingLogic modifications to FluidStack
        // amounts persist across multiple getStoredFluidsForColor() calls during
        // the cross-recipe loop. Without this, ME hatches return new copies each
        // time, causing inputs to never appear consumed.
        cachedMEInputFluids = new HashMap<>();
        originalMEAmounts = new HashMap<>();
        try {
            for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
                if (tHatch instanceof MTEHatchInputME meHatch) {
                    for (FluidStack fluid : meHatch.getStoredFluids()) {
                        if (fluid != null) {
                            FluidStack copy = fluid.copy();
                            cachedMEInputFluids.put(copy.getFluid(), copy);
                            originalMEAmounts.merge(copy.getFluid(), copy.amount, Integer::sum);
                        }
                    }
                }
            }

            CheckRecipeResult firstResult = super.checkProcessing();
            if (!firstResult.wasSuccessful()) {
                return firstResult;
            }

            long steamTicks = Math.abs(lEUt) * 10000L / Math.max(1000, mEfficiency);
            long totalSteamMb = steamTicks * mMaxProgresstime;
            int totalDuration = mMaxProgresstime;

            ArrayList<ItemStack> accItems = cloneItemArray(mOutputItems);
            ArrayList<FluidStack> accFluids = cloneFluidArray(mOutputFluids);

            int maxIterations = 200;
            for (int i = 0; i < maxIterations; i++) {
                CheckRecipeResult result = super.checkProcessing();
                if (!result.wasSuccessful()) {
                    break;
                }

                steamTicks = Math.abs(lEUt) * 10000L / Math.max(1000, mEfficiency);
                totalSteamMb += steamTicks * mMaxProgresstime;
                totalDuration += mMaxProgresstime;

                mergeItemStacks(accItems, mOutputItems);
                mergeFluidStacks(accFluids, mOutputFluids);

                if (getStoredInputsForColor(Optional.empty()).isEmpty()
                    && getStoredFluidsForColor(Optional.empty()).isEmpty()) {
                    break;
                }
            }

            // Drain ME hatches for the amounts consumed during processing.
            // The cached FluidStacks were modified in-place by ProcessingLogic;
            // the difference from original amounts is what was consumed.
            for (Map.Entry<Fluid, Integer> entry : originalMEAmounts.entrySet()) {
                Fluid fluid = entry.getKey();
                int originalAmount = entry.getValue();
                FluidStack cached = cachedMEInputFluids.get(fluid);
                if (cached == null) continue;
                int consumed = originalAmount - cached.amount;
                if (consumed > 0) {
                    depleteInput(new FluidStack(fluid, consumed));
                }
            }

            if (totalSteamMb > 0 && !tryConsumeSteam((int) totalSteamMb)) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return CheckRecipeResultRegistry.insufficientPower(totalSteamMb);
            }

            mOutputItems = accItems.toArray(new ItemStack[0]);
            mOutputFluids = accFluids.toArray(new FluidStack[0]);
            mMaxProgresstime = totalDuration;
            lEUt = 0;
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;

            return CheckRecipeResultRegistry.SUCCESSFUL;
        } finally {
            cachedMEInputFluids = null;
            originalMEAmounts = null;
        }
    }

    private static ArrayList<ItemStack> cloneItemArray(ItemStack[] items) {
        ArrayList<ItemStack> list = new ArrayList<>();
        if (items != null) {
            for (ItemStack s : items) {
                if (s != null) list.add(s.copy());
            }
        }
        return list;
    }

    private static ArrayList<FluidStack> cloneFluidArray(FluidStack[] fluids) {
        ArrayList<FluidStack> list = new ArrayList<>();
        if (fluids != null) {
            for (FluidStack f : fluids) {
                if (f != null) list.add(f.copy());
            }
        }
        return list;
    }

    /**
     * Merges output ItemStacks from a recipe into the accumulator, combining stacks of the same item.
     * This prevents unbounded array growth during cross-recipe processing, which would otherwise
     * cause the tile entity NBT to exceed the 32KB network packet limit when synced to the client.
     */
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
                    // Overflow: create a new stack for the remainder
                    ItemStack remainder = toMerge.copy();
                    remainder.stackSize = toMerge.stackSize - add;
                    toMerge = remainder;
                }
            }
            if (!merged) {
                acc.add(toMerge.copy());
            }
        }
    }

    /**
     * Merges output FluidStacks from a recipe into the accumulator, combining stacks of the same fluid.
     * This prevents unbounded array growth during cross-recipe processing, which would otherwise
     * cause the tile entity NBT to exceed the 32KB network packet limit when synced to the client.
     */
    private static void mergeFluidStacks(ArrayList<FluidStack> acc, FluidStack[] fluids) {
        if (fluids == null) return;
        for (FluidStack toMerge : fluids) {
            if (toMerge == null) continue;
            boolean merged = false;
            for (FluidStack existing : acc) {
                if (existing.isFluidEqual(toMerge)) {
                    existing.amount += toMerge.amount;
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                acc.add(toMerge.copy());
            }
        }
    }

    // endregion

    // region Batch Mode, Perfect Overclock & Input Separation

    // #tr Tooltip_GTNC_PerfectOverclock
    // # Perfect Overclock: Overclock beyond recipe voltage has no efficiency loss
    // # 无损超频：超频时无效率损失

    // #tr Tooltip_GTNC_SteamTierInfo
    // # Bronze machine recipe tier: HV, Steel machine recipe tier: EV
    // # zh_CN 青铜机器配方等级:HV 钢机器配方等级:EV

    // #tr Tooltip_GTNC_SteamGearInfo
    // # Insert Stainless Steel gear into controller for recipe tier +1
    // # zh_CN 在主机里插入不锈钢齿轮配方等级+1

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    /**
     * Unified ProcessingLogic with Perfect Overclock (无损超频).
     * Uses the ProcessingLogic default overclock calculator (no ofNoOverclock),
     * same pattern as MTELargeChemicalReactor.
     * Machines needing extra validation (e.g. CompressionTierKey) can override.
     */
    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().enablePerfectOverclock()
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    // endregion

    // region Recipe Tier, Stainless Steel Gear & WAILA
    // ============================================================
    // Common fields and methods extracted from all GTNC steam multis:
    // - tierMachine, enableHigherRecipe, syncTierValue
    // - getUpgradeTier (stainless steel gear check)
    // - getTierRecipes, checkProcessing, getInfoData
    // - WAILA tier/parallel/maxtier display
    // - updateHatchTexture (extended with I/O buses)
    // - Static utility: getTierFrame, getTierGearCasing, getTierPipeCasing
    // - NBT save/load for tier fields
    // ============================================================

    protected int tierMachine = 1;
    protected boolean enableHigherRecipe = false;
    protected int syncTierValue = -1;

    /**
     * Check if the controller slot contains a Stainless Steel gear for recipe tier +1.
     */
    public boolean getUpgradeTier(ItemStack inventory) {
        if (inventory == null) return false;
        return inventory.isItemEqual(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 1L));
    }

    @Override
    public int getTierRecipes() {
        return tierMachine + 2 + (enableHigherRecipe ? 1 : 0);
    }

    @Override
    public void onValueUpdate(byte aValue) {
        syncTierValue = aValue;
    }

    @Override
    public byte getUpdateData() {
        return (byte) syncTierValue;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(
            StatCollector.translateToLocalFormatted(
                "gtpp.infodata.multi.steam.tier",
                "" + EnumChatFormatting.YELLOW + tierMachine));
        info.add(
            StatCollector.translateToLocalFormatted(
                "gtpp.infodata.multi.steam.parallel",
                "" + EnumChatFormatting.YELLOW + getMaxParallelRecipes()));
        return info.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        currenttip.add(
            StatCollector.translateToLocal("GTPP.machines.tier") + ": "
                + EnumChatFormatting.YELLOW
                + getSteamTierTextForWaila(tag)
                + EnumChatFormatting.RESET);
        currenttip.add(
            StatCollector.translateToLocal("GT5U.multiblock.curparallelism") + ": "
                + EnumChatFormatting.BLUE
                + tag.getInteger("parallel")
                + EnumChatFormatting.RESET);
        currenttip.add(
            StatCollector.translateToLocal("GT5U.multiblock.maxtier") + ": "
                + EnumChatFormatting.YELLOW
                + GTValues.VN[tag.getInteger("tierMachine") + 1 + (tag.getBoolean("enableHigherRecipe") ? 1 : 0)]
                + EnumChatFormatting.RESET);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("tierMachine", tierMachine);
        tag.setInteger("parallel", getTrueParallel());
        tag.setBoolean("enableHigherRecipe", getUpgradeTier(getControllerSlot()));
    }

    @Override
    protected void updateHatchTexture() {
        super.updateHatchTexture();
        int id = getCasingTextureId();
        for (MTEHatch h : mInputBusses) h.updateTexture(id);
        for (MTEHatch h : mOutputBusses) h.updateTexture(id);
        for (IDualInputHatch h : mDualInputHatches) h.updateTexture(id);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("tierMachine", tierMachine);
        aNBT.setBoolean("enableHigherRecipe", enableHigherRecipe);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachine = aNBT.getInteger("tierMachine");
        enableHigherRecipe = aNBT.getBoolean("enableHigherRecipe");
    }

    // ---- Static tier resolution helpers used by structure definitions ----

    @Nullable
    public static Integer getTierFrame(Block block, int meta) {
        if (block == gregtech.api.GregTechAPI.sBlockFrames) {
            if (meta == Materials.Bronze.mMetaItemSubID) return 1;
            if (meta == Materials.Steel.mMetaItemSubID) return 2;
        }
        return null;
    }

    @Nullable
    public static Integer getTierGearCasing(Block block, int meta) {
        if (block == gregtech.api.GregTechAPI.sBlockCasings2 && 2 == meta) return 1;
        if (block == gregtech.api.GregTechAPI.sBlockCasings2 && 3 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierPipeCasing(Block block, int meta) {
        if (block == gregtech.api.GregTechAPI.sBlockCasings2 && 12 == meta) return 1;
        if (block == gregtech.api.GregTechAPI.sBlockCasings2 && 13 == meta) return 2;
        return null;
    }

    // endregion

    @Override
    public List<IOutputBus> getOutputBusses() {
        List<IOutputBus> output = new ArrayList<>();
        for (MTEHatchOutputBus bus : mSteamOutputs) {
            if (bus.isValid()) output.add(bus);
        }
        for (MTEHatchOutputBus bus : mOutputBusses) {
            if (bus.isValid()) output.add(bus);
        }
        return output;
    }
}
