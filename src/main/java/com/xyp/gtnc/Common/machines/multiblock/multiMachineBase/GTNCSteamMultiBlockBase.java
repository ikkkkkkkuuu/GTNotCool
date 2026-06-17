package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBlockBase;

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
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        inputsFromME.put(fluidStack.getFluid(), fluidStack);
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

    private static void mergeItemStacks(ArrayList<ItemStack> acc, ItemStack[] items) {
        if (items != null) {
            for (ItemStack s : items) {
                if (s != null) acc.add(s.copy());
            }
        }
    }

    private static void mergeFluidStacks(ArrayList<FluidStack> acc, FluidStack[] fluids) {
        if (fluids != null) {
            for (FluidStack f : fluids) {
                if (f != null) acc.add(f.copy());
            }
        }
    }

    // endregion Output buses

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
