package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.xyp.gtnc.Common.gui.modularui.multiblock.BaseGui.GTNCSteamMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.utils.enums.SteamTypes;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBlockBase;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Clean GT-Not-Cool steam multiblock foundation.
 *
 * <p>
 * This class owns reusable steam, ME I/O and wireless-steam infrastructure only. Upgrade trees,
 * cross-recipe execution and machine-specific policies belong in more specialized subclasses.
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

    public boolean wirelessMode;
    protected UUID ownerUUID;
    protected long totalSteamConsumed;

    protected GTNCSteamMultiBlockBase(String aName) {
        super(aName);
    }

    protected GTNCSteamMultiBlockBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity base) {
        super.onFirstTick(base);
        if (ownerUUID == null) ownerUUID = base.getOwnerUuid();
        if (ownerUUID != null) SteamWirelessNetworkManager.strongCheckOrAddUser(ownerUUID);
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        if (side != getBaseMetaTileEntity().getFrontFacing()) return;
        wirelessMode = !wirelessMode;
        GTUtility.sendChatToPlayer(
            player,
            StatCollector.translateToLocal(
                wirelessMode ? "GT5U.chat.wireless_mode.enabled" : "GT5U.chat.wireless_mode.disabled"));
    }

    @Override
    public boolean addInputBusToMachineList(IGregTechTileEntity tile, int casingIndex) {
        boolean added = super.addInputBusToMachineList(tile, casingIndex);
        if (tile != null && tile.getMetaTileEntity() instanceof SuperMTEHatchCraftingInputME craftingInput) {
            craftingInput.setControllerRecipeMap(getRecipeMap());
        }
        return added;
    }

    @Override
    public ArrayList<ItemStack> getAllStoredInputs() {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch hatch : mDualInputHatches) appendNonNullInputItems(inputs, hatch.getAllItems());
        }

        Map<ItemId, ItemStack> meInputs = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInputBus hatch : GTUtility.validMTEList(mInputBusses)) {
            if (hatch instanceof MTEHatchCraftingInputME) continue;
            hatch.mRecipeMap = getRecipeMap();
            boolean meBus = hatch instanceof MTEHatchInputBusME;
            for (int slot = hatch.getSizeInventory() - 1; slot >= 0; slot--) {
                ItemStack stack = hatch.getStackInSlot(slot);
                if (stack == null) continue;
                if (meBus) meInputs.put(ItemId.createNoCopy(stack), stack);
                else inputs.add(stack);
            }
        }
        for (MTEHatchSteamBusInput hatch : GTUtility.validMTEList(mSteamInputs)) {
            hatch.mRecipeMap = getRecipeMap();
            for (int slot = hatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; slot >= 0; slot--) {
                ItemStack stack = hatch.getBaseMetaTileEntity()
                    .getStackInSlot(slot);
                if (stack != null) inputs.add(stack);
            }
        }
        ItemStack controllerStack = getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(controllerStack)) inputs.add(controllerStack);
        inputs.addAll(meInputs.values());
        return inputs;
    }

    protected void appendNonNullInputItems(List<ItemStack> target, ItemStack[] stacks) {
        if (stacks == null) return;
        for (ItemStack stack : stacks) if (stack != null) target.add(stack);
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        Map<ItemId, ItemStack> meInputs = new Object2ObjectOpenHashMap<>();
        for (MTEHatchInputBus hatch : GTUtility.validMTEList(mInputBusses)) {
            if (hatch instanceof MTEHatchCraftingInputME) continue;
            byte hatchColor = hatch.getColor();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            hatch.mRecipeMap = getRecipeMap();
            boolean meBus = hatch instanceof MTEHatchInputBusME;
            for (int slot = hatch.getSizeInventory() - 1; slot >= 0; slot--) {
                ItemStack stack = hatch.getStackInSlot(slot);
                if (stack == null) continue;
                if (meBus) meInputs.put(ItemId.createNoCopy(stack), stack);
                else inputs.add(stack);
            }
        }
        for (MTEHatchSteamBusInput hatch : GTUtility.validMTEList(mSteamInputs)) {
            byte hatchColor = hatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            hatch.mRecipeMap = getRecipeMap();
            for (int slot = hatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; slot >= 0; slot--) {
                ItemStack stack = hatch.getBaseMetaTileEntity()
                    .getStackInSlot(slot);
                if (stack != null) inputs.add(stack);
            }
        }
        ItemStack controllerStack = getStackInSlot(1);
        if (GTUtility.isAnyIntegratedCircuit(controllerStack)) inputs.add(controllerStack);
        inputs.addAll(meInputs.values());
        return inputs;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> fluids = new ArrayList<>();
        Map<Fluid, FluidStack> meFluids = new HashMap<>();
        for (MTEHatchCustomFluidBase hatch : GTUtility.validMTEList(mSteamInputFluids)) {
            byte hatchColor = hatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            if (hatch.getFillableStack() != null) fluids.add(hatch.getFillableStack());
        }
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) {
            byte hatchColor = hatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            hatch.mRecipeMap = getRecipeMap();
            if (hatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack stack : meHatch.getStoredFluids()) {
                    if (stack != null) meFluids.put(stack.getFluid(), stack);
                }
            } else if (hatch.getFillableStack() != null) {
                fluids.add(hatch.getFillableStack());
            }
        }
        fluids.addAll(meFluids.values());
        return fluids;
    }

    @Override
    public ArrayList<FluidStack> getAllSteamStacks() {
        ArrayList<FluidStack> steamStacks = new ArrayList<>();
        for (MTEHatchCustomFluidBase hatch : GTUtility.validMTEList(mSteamInputFluids)) {
            FluidStack stack = hatch.getFluid();
            if (stack != null && isSupportedSteam(stack.getFluid())) steamStacks.add(stack);
        }
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) {
            if (hatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack stack : meHatch.getStoredFluids()) {
                    if (stack != null && isSupportedSteam(stack.getFluid())) steamStacks.add(stack);
                }
            } else {
                FluidStack stack = hatch.getFillableStack();
                if (stack != null && isSupportedSteam(stack.getFluid())) steamStacks.add(stack);
            }
        }
        return steamStacks;
    }

    private boolean isSupportedSteam(Fluid fluid) {
        for (SteamTypes type : SteamTypes.getSupportedTypes()) {
            if (type.fluid == fluid) return true;
        }
        return false;
    }

    @Override
    public boolean depleteInput(FluidStack requested) {
        if (requested == null) return false;
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) {
            if (hatch instanceof MTEHatchInputME meHatch) {
                meHatch.startRecipeProcessing();
                FluidStack simulated = meHatch.drain(ForgeDirection.UNKNOWN, requested, false);
                if (simulated != null && simulated.amount >= requested.amount) {
                    meHatch.drain(ForgeDirection.UNKNOWN, requested, true);
                    meHatch.endRecipeProcessing(this);
                    return true;
                }
                meHatch.endRecipeProcessing(this);
            } else {
                FluidStack stored = hatch.getFluid();
                if (stored != null && stored.isFluidEqual(requested)) {
                    FluidStack simulated = hatch.drain(requested.amount, false);
                    if (simulated != null && simulated.amount >= requested.amount) {
                        return hatch.drain(requested.amount, true) != null;
                    }
                }
            }
        }
        return super.depleteInput(requested);
    }

    @Override
    public boolean depleteInput(ItemStack requested) {
        if (GTUtility.isStackInvalid(requested)) return false;
        FluidStack contained = GTUtility.getFluidForFilledItem(requested, true);
        if (contained != null) return depleteInput(contained);
        for (MTEHatchSteamBusInput hatch : GTUtility.validMTEList(mSteamInputs)) {
            hatch.mRecipeMap = getRecipeMap();
            if (depleteFromBus(hatch, requested)) return true;
        }
        for (MTEHatchInputBus hatch : GTUtility.validMTEList(mInputBusses)) {
            hatch.mRecipeMap = getRecipeMap();
            if (depleteFromBus(hatch, requested)) return true;
        }
        return false;
    }

    private boolean depleteFromBus(MTEHatchInputBus hatch, ItemStack requested) {
        for (int slot = hatch.getSizeInventory() - 1; slot >= 0; slot--) {
            ItemStack stored = hatch.getStackInSlot(slot);
            if (GTUtility.areStacksEqual(requested, stored) && stored.stackSize >= requested.stackSize) {
                hatch.getBaseMetaTileEntity()
                    .decrStackSize(slot, requested.stackSize);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getTotalSteamCapacity() {
        int capacity = super.getTotalSteamCapacity();
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) capacity += hatch.getCapacity();
        return capacity;
    }

    public long getTotalSteamCapacityLong() {
        long capacity = 0;
        for (MTEHatchCustomFluidBase hatch : GTUtility.validMTEList(mSteamInputFluids)) {
            capacity += hatch.getRealCapacity();
        }
        for (MTEHatchInput hatch : GTUtility.validMTEList(mInputHatches)) capacity += hatch.getCapacity();
        return capacity;
    }

    public long getLongTotalSteamStored() {
        long stored = 0;
        for (FluidStack stack : getAllSteamStacks()) if (stack != null) stored += stack.amount;
        return stored;
    }

    @Override
    public boolean tryConsumeSteam(int amount) {
        if (amount <= 0) return true;
        boolean consumed;
        if (wirelessMode && ownerUUID != null) {
            consumed = SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, -amount);
        } else {
            consumed = depleteInput(Materials.Steam.getGas(amount));
        }
        if (consumed) totalSteamConsumed += amount;
        return consumed;
    }

    @Override
    public List<IOutputBus> getOutputBusses() {
        List<IOutputBus> outputs = new ArrayList<>();
        for (MTEHatchOutputBus bus : GTUtility.validMTEList(mSteamOutputs)) outputs.add(bus);
        for (MTEHatchOutputBus bus : GTUtility.validMTEList(mOutputBusses)) outputs.add(bus);
        return outputs;
    }

    public ArrayList<ItemStack> getStoredOutputs() {
        ArrayList<ItemStack> outputs = new ArrayList<>();
        appendBusContents(outputs, mSteamOutputs);
        appendBusContents(outputs, mOutputBusses);
        return outputs;
    }

    private void appendBusContents(List<ItemStack> outputs, List<MTEHatchOutputBus> buses) {
        for (MTEHatchOutputBus bus : GTUtility.validMTEList(buses)) {
            for (int slot = bus.getSizeInventory() - 1; slot >= 0; slot--) {
                ItemStack stack = bus.getStackInSlot(slot);
                if (stack != null) outputs.add(stack);
            }
        }
    }

    @Override
    protected void updateHatchTexture() {
        super.updateHatchTexture();
        int textureId = getCasingTextureId();
        for (MTEHatch hatch : mInputBusses) hatch.updateTexture(textureId);
        for (MTEHatch hatch : mOutputBusses) hatch.updateTexture(textureId);
        for (IDualInputHatch hatch : mDualInputHatches) hatch.updateTexture(textureId);
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>();
        for (String line : super.getInfoData()) info.add(line);
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
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setBoolean("wirelessMode", wirelessMode);
        tag.setLong("steamConsumed", totalSteamConsumed);
        if (ownerUUID != null) tag.setString("ownerUUID", ownerUUID.toString());
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        tag.setBoolean("wirelessMode", wirelessMode);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        wirelessMode = tag.getBoolean("wirelessMode");
        totalSteamConsumed = tag.getLong("steamConsumed");
        if (tag.hasKey("ownerUUID")) {
            try {
                ownerUUID = UUID.fromString(tag.getString("ownerUUID"));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public void getWailaBody(ItemStack stack, List<String> tip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(stack, tip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            tip.add(
                StatCollector.translateToLocal("GT5U.turbine.wireless_mode") + ": "
                    + EnumChatFormatting.GREEN
                    + "ON"
                    + EnumChatFormatting.RESET);
            tip.add(
                StatCollector.translateToLocal("GTNC.info.wireless_steam") + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("networkSteam")
                    + EnumChatFormatting.RESET
                    + " L");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setBoolean("wirelessMode", wirelessMode);
        if (wirelessMode && ownerUUID != null) {
            tag.setString(
                "networkSteam",
                SteamWirelessNetworkManager.getUserSteam(ownerUUID)
                    .toString());
        }
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new GTNCSteamMultiBlockBaseGui(this);
    }

    @Override
    protected IIconContainer getInactiveGlowOverlay() {
        return getInactiveOverlay();
    }

    @Override
    protected IIconContainer getActiveGlowOverlay() {
        return getActiveOverlay();
    }
}
