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
import gregtech.api.util.GTUtility;
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
        return false;
    }

    @Override
    public int getTotalSteamCapacity() {
        int cap = super.getTotalSteamCapacity();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            cap += tHatch.getCapacity();
        }
        return cap;
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

    // region Output buses

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

    // endregion
}
