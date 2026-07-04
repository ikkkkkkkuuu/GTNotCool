package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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

import com.xyp.gtnc.Common.gui.modularui.multiblock.GTNCSteamMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
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

    // #tr GT5U.chat.wireless_mode.enabled
    // # Wireless Mode: Enabled
    // # zh_CN §d无线模式：已启用

    // #tr GT5U.chat.wireless_mode.disabled
    // # Wireless Mode: Disabled
    // # zh_CN §7无线模式：已禁用

    // #tr GT5U.turbine.wireless_mode
    // # Wireless Mode
    // # zh_CN §d无线模式

    // #tr GTNC.info.wireless_steam
    // # Network Steam
    // # zh_CN 网络蒸汽

    // #tr GTNC.info.steam_consumed
    // # Steam Used：
    // # zh_CN 本次蒸汽消耗

    public boolean wirelessMode = false;
    protected UUID ownerUUID;
    protected long totalSteamConsumed = 0;
    protected int mUpgradeTier = 0;
    protected boolean mUpgraded = false;
    /** Paid cost indices for the upgrade tree. Persisted across chunk reloads. */
    public Set<Integer> paidUpgradeCostIndices = new HashSet<>();

    public GTNCSteamMultiBlockBase(String aName) {
        super(aName);
    }

    public GTNCSteamMultiBlockBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (ownerUUID == null) {
            ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
        }
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (side == getBaseMetaTileEntity().getFrontFacing()) {
            wirelessMode = !wirelessMode;
            GTUtility.sendChatToPlayer(
                aPlayer,
                wirelessMode ? StatCollector.translateToLocal("GT5U.chat.wireless_mode.enabled")
                    : StatCollector.translateToLocal("GT5U.chat.wireless_mode.disabled"));
        }
    }

    /**
     * Override in subclass to define required items for upgrade.
     * Default: all registered ChipTiers.
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

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    /**
     * Called by the upgrade tree GUI after materials are consumed.
     * Finds the highest chip tier among paid costs and applies the upgrade.
     * Override in subclass for additional behavior.
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
            this.enableHigherRecipe = true;
        }
    }

    protected float getUpgradeSpeedBonus() {
        if (!mUpgraded || mUpgradeTier <= 0) return 1.0f;
        return (float) Math.max(0.01, 1.0 / (1.0 + mUpgradeTier * 0.2));
    }

    protected int getUpgradeParallelBonus() {
        if (!mUpgraded) return 0;
        return mUpgradeTier * 16;
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
        if (wirelessMode && ownerUUID != null) {
            return SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, -aAmount);
        }
        return this.depleteInput(Materials.Steam.getGas(aAmount));
    }

    // endregion

    // region Item I/O

    /**
     * Feed the controller's recipe map to our Super Crafting Input hatch when it's added on structure form. Our hatch
     * is
     * an {@link IDualInputHatch} stored in {@code mDualInputHatches}; GT5's {@code addInputBusToMachineList} returns on
     * that branch WITHOUT setting {@code mRecipeMap} (only the plain-input-bus branch below it does). The structure
     * scanner adds InputBus elements via {@code addInputBusToMachineList} (see HatchElement.InputBus), NOT
     * {@code addToMachineList} — so this is the method to hook. With the recipe map in hand the hatch shows the
     * recipe-map name ("Assembler") instead of the machine icon name, matching NEI-overwrite auto-fill naming.
     */
    @Override
    public boolean addInputBusToMachineList(final IGregTechTileEntity aTileEntity, final int aBaseCasingIndex) {
        boolean result = super.addInputBusToMachineList(aTileEntity, aBaseCasingIndex);
        if (aTileEntity != null
            && aTileEntity.getMetaTileEntity() instanceof SuperMTEHatchCraftingInputME craftingInput) {
            craftingInput.setControllerRecipeMap(getRecipeMap());
        }
        return result;
    }

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

            totalSteamConsumed = totalSteamMb;
            if (totalSteamMb > 0 && !tryConsumeSteam((int) totalSteamMb)) {
                totalSteamConsumed = 0;
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return CheckRecipeResultRegistry.insufficientPower(totalSteamMb);
            }

            mOutputItems = accItems.toArray(new ItemStack[0]);
            mOutputFluids = accFluids.toArray(new FluidStack[0]);
            mMaxProgresstime = totalDuration;
            lEUt = 0;
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            // Apply chip speed bonus at final settlement (wireless steam mode)
            mMaxProgresstime = Math.max(1, (int) (mMaxProgresstime * getUpgradeSpeedBonus()));

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

    // #tr Tooltip_GTNC_SteamTierInfo
    // # Bronze machine recipe tier: LV, Steel machine recipe tier: MV
    // # zh_CN 青铜机器配方等级:LV 钢机器配方等级:MV

    // #tr Tooltip_GTNC_SteamGearInfo
    // # §bSubmit High Computing Power Chips through the Upgrade Tree to boost speed and parallel
    // # zh_CN §b通过天途提交高算力芯片以提升速度和并行和配方等级

    // #tr Tooltip_GTNC_SteamGearInfo_02
    // # §bEach tier provides 20% speed boost and 16 parallel
    // # zh_CN §b芯片等级每提升一级,提供20%的运行速度加成和16的并行

    // #tr Tooltip_GTNC_SteamWirelessMode
    // # §dRight-click front face with Screwdriver to toggle Wireless Steam Mode
    // # zh_CN §d螺丝刀右键或gui里点击按钮切换无线蒸汽模式

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
        return new ProcessingLogic() {}.enablePerfectOverclock()
            .setMaxParallelSupplier(() -> getTrueParallel() + getUpgradeParallelBonus());
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

    /** @deprecated Use mUpgradeTier instead */
    @Deprecated
    public boolean getUpgradeTier(ItemStack inventory) {
        return mUpgradeTier > 0;
    }

    @Override
    public int getTierRecipes() {
        return tierMachine + mUpgradeTier;
    }

    @Override
    protected boolean isHighPressure() {
        return tierMachine == 2;
    }

    @Override
    public void onValueUpdate(byte aValue) {
        syncTierValue = aValue;
        tierMachine = aValue;
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
        if (mUpgraded && mUpgradeTier > 0) {
            info.add(
                StatCollector.translateToLocal("GTNC.info.upgradeTier") + ": "
                    + EnumChatFormatting.GOLD
                    + mUpgradeTier);
        }
        info.add(
            StatCollector.translateToLocal("GT5U.turbine.wireless_mode") + ": "
                + (wirelessMode ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF")
                + EnumChatFormatting.RESET);
        if (wirelessMode && ownerUUID != null) {
            info.add(
                StatCollector.translateToLocal("GTNC.info.wireless_steam") + ": "
                    + EnumChatFormatting.GOLD
                    + SteamWirelessNetworkManager.getUserSteam(ownerUUID)
                    + EnumChatFormatting.RESET
                    + " L");
        }
        info.add(
            StatCollector.translateToLocal("GTNC.info.steam_consumed") + ": "
                + EnumChatFormatting.AQUA
                + totalSteamConsumed
                + EnumChatFormatting.RESET
                + " L");
        return info.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (showWailaExtraInfo()) {
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
                    + GTValues.VN[tag.getInteger("tierMachine") + tag.getInteger("upgradeTier")]
                    + EnumChatFormatting.RESET);
            if (tag.getInteger("upgradeTier") > 0) {
                currenttip.add(
                    StatCollector.translateToLocal("GTNC.info.upgradeTier") + ": "
                        + EnumChatFormatting.GOLD
                        + tag.getInteger("upgradeTier"));
                currenttip.add(
                    StatCollector.translateToLocal("GTNC.info.upgradeSpeed") + ": "
                        + EnumChatFormatting.GREEN
                        + String.format("%.2fx", 1.0f / tag.getFloat("upgradeSpeed")));
                currenttip.add(
                    StatCollector.translateToLocal("GTNC.info.upgradeParallel") + ": "
                        + EnumChatFormatting.AQUA
                        + "+"
                        + tag.getInteger("upgradeParallel"));
            }
        }
        if (tag.getBoolean("wirelessMode")) {
            currenttip.add(
                StatCollector.translateToLocal("GT5U.turbine.wireless_mode") + ": "
                    + EnumChatFormatting.GREEN
                    + "ON"
                    + EnumChatFormatting.RESET);
            currenttip.add(
                StatCollector.translateToLocal("GTNC.info.wireless_steam") + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("networkSteam")
                    + EnumChatFormatting.RESET
                    + " L");
        }
        currenttip.add(
            StatCollector.translateToLocal("GTNC.info.steam_consumed") + ": "
                + EnumChatFormatting.AQUA
                + tag.getLong("steamConsumed")
                + EnumChatFormatting.RESET
                + " L");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("tierMachine", tierMachine);
        tag.setInteger("parallel", getTrueParallel());
        tag.setInteger("upgradeTier", mUpgradeTier);
        tag.setFloat("upgradeSpeed", getUpgradeSpeedBonus());
        tag.setInteger("upgradeParallel", getUpgradeParallelBonus());
        tag.setBoolean("wirelessMode", wirelessMode);
        if (wirelessMode && ownerUUID != null) {
            tag.setString(
                "networkSteam",
                SteamWirelessNetworkManager.getUserSteam(ownerUUID)
                    .toString());
        }
        tag.setLong("steamConsumed", totalSteamConsumed);
    }

    protected boolean showWailaExtraInfo() {
        return true;
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
        aNBT.setInteger("mUpgradeTier", mUpgradeTier);
        aNBT.setBoolean("mUpgraded", mUpgraded);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        if (!paidUpgradeCostIndices.isEmpty()) {
            int[] arr = new int[paidUpgradeCostIndices.size()];
            int i = 0;
            for (int idx : paidUpgradeCostIndices) {
                arr[i++] = idx;
            }
            aNBT.setIntArray("paidUpgradeCostIndices", arr);
        }
        if (ownerUUID != null) {
            aNBT.setString("ownerUUID", ownerUUID.toString());
        }
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachine = aNBT.getInteger("tierMachine");
        mUpgradeTier = aNBT.getInteger("mUpgradeTier");
        mUpgraded = aNBT.getBoolean("mUpgraded");
        wirelessMode = aNBT.getBoolean("wirelessMode");
        paidUpgradeCostIndices.clear();
        if (aNBT.hasKey("paidUpgradeCostIndices")) {
            for (int idx : aNBT.getIntArray("paidUpgradeCostIndices")) {
                paidUpgradeCostIndices.add(idx);
            }
        }
        if (aNBT.hasKey("ownerUUID")) {
            try {
                ownerUUID = UUID.fromString(aNBT.getString("ownerUUID"));
            } catch (IllegalArgumentException ignored) {}
        }
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

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new GTNCSteamMultiBlockBaseGui(this);
    }
}
