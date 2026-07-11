package com.xyp.gtnc.Common.machines.multiblock.multiMachineBase;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.ArrayUtils;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Wireless-energy multiblock base for the Miracle Door port.
 * <p>
 * Faithful merge of TST's {@code GTCM_MultiMachineBase} (maintenance immunity + machine-mode framework +
 * special-input reading) and {@code WirelessEnergyMultiMachineBase} (wireless EU billing loop), extending GT's
 * {@link MTEExtendedPowerMultiBlockBase} directly so that it carries NO chip-upgrade tree and uses the stock
 * multiblock GUI plus a mode-switch button (matching the source machine).
 * <p>
 * Wireless mode is ON by default here (see {@link #getDefaultWirelessMode()}); it draws EU straight from the global
 * wireless energy network keyed on the owner UUID and needs no energy hatches and no tier-7 chip.
 */
public abstract class GTNCWirelessBase<T extends GTNCWirelessBase<T>> extends MTEExtendedPowerMultiBlockBase<T>
    implements IConstructable, ISurvivalConstructable {

    // #tr Waila_WirelessMode
    // # Wireless Mode
    // # zh_CN 无线模式

    // #tr Waila_CurrentEuCost
    // # EU Cost
    // # zh_CN EU消耗

    public static final String ZERO_STRING = "0";
    protected static final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);

    protected UUID ownerUUID;
    protected boolean isRecipeProcessing = false;
    protected boolean wirelessMode = getDefaultWirelessMode();
    protected BigInteger costingEU = BigInteger.ZERO;
    protected String costingEUText = ZERO_STRING;
    protected int cycleNum = 200;

    public GTNCWirelessBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTNCWirelessBase(String aName) {
        super(aName);
    }

    // region maintenance immunity

    public void repairMachine() {
        mHardHammer = true;
        mSoftMallet = true;
        mScrewdriver = true;
        mCrowbar = true;
        mSolderingTool = true;
        mWrench = true;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public final boolean shouldCheckMaintenance() {
        return false;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    protected long getActualEnergyUsage() {
        return -this.lEUt;
    }

    // endregion

    // region input reading

    /**
     * Get input items from all input busses plus the controller programmed-circuit slot, ignoring dual-input hatches
     * and separation mode. Used to locate the Critical Photon fuel.
     */
    public ArrayList<ItemStack> getStoredInputsWithoutDualInputHatch() {
        ArrayList<ItemStack> rList = new ArrayList<>();
        for (MTEHatchInputBus tHatch : GTUtility.filterValidMTEs(mInputBusses)) {
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    rList.add(itemStack);
                }
            }
        }
        if (getStackInSlot(1) != null && getStackInSlot(1).getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(getStackInSlot(1));
        return rList;
    }

    // endregion

    // region wireless processing

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void startRecipeProcessing() {
        isRecipeProcessing = true;
        super.startRecipeProcessing();
    }

    @Override
    public void endRecipeProcessing() {
        super.endRecipeProcessing();
        isRecipeProcessing = false;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic();
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        costingEU = BigInteger.ZERO;
        costingEUText = ZERO_STRING;
        prepareProcessing();
        if (!wirelessMode || ownerUUID == null) return super.checkProcessing();

        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (int i = 0; i < cycleNum; i++) {
            CheckRecipeResult r = wirelessModeProcessOnce();
            if (!r.wasSuccessful()) {
                finalResult = r;
                break;
            }
            succeeded = true;
        }

        updateSlots();
        if (!succeeded) return finalResult;
        costingEUText = NumberFormatUtil.formatNumber(costingEU);

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = getWirelessModeProcessingTime();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public CheckRecipeResult wirelessModeProcessOnce() {
        if (!isRecipeProcessing) startRecipeProcessing();
        setupProcessingLogic(processingLogic);
        setupWirelessProcessingPowerLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        if (!result.wasSuccessful()) {
            return result;
        }

        BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf(processingLogic.getDuration()));

        int m = getExtraEUCostMultiplier();
        if (m > 1) {
            costEU = costEU.multiply(BigInteger.valueOf(m));
        }

        if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
        }

        costingEU = costingEU.add(costEU);

        mOutputItems = ArrayUtils.addAll(mOutputItems, processingLogic.getOutputItems());
        mOutputFluids = ArrayUtils.addAll(mOutputFluids, processingLogic.getOutputFluids());

        endRecipeProcessing();
        return result;
    }

    protected void prepareProcessing() {}

    protected void setupWirelessProcessingPowerLogic(ProcessingLogic logic) {
        // wireless mode ignores voltage limit
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(false);
    }

    public int getExtraEUCostMultiplier() {
        return 1;
    }

    public abstract int getWirelessModeProcessingTime();

    public boolean getDefaultWirelessMode() {
        return false;
    }

    @Override
    public long getMaxInputVoltage() {
        if (wirelessMode) return Long.MAX_VALUE;
        return super.getMaxInputVoltage();
    }

    // endregion

    // region machine mode framework

    public int totalMachineMode() {
        return 1;
    }

    public String getMachineModeName(int mode) {
        return "Unknown Mode " + mode;
    }

    @Override
    public final String getMachineModeName() {
        return getMachineModeName(machineMode);
    }

    @Override
    public void setMachineModeIcons() {
        for (int i = 0; i < totalMachineMode(); i++) {
            machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
        }
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return totalMachineMode() > 1;
    }

    @Override
    public int nextMachineMode() {
        if (machineMode + 1 >= totalMachineMode()) {
            return 0;
        }
        return machineMode + 1;
    }

    public boolean canButtonSwitchMode() {
        return supportsMachineModeSwitch();
    }

    @Override
    public ButtonWidget createModeSwitchButton(IWidgetBuilder<?> builder) {
        if (!supportsMachineModeSwitch()) return null;
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            if (canButtonSwitchMode()) {
                onMachineModeSwitchClick();
                setMachineMode(nextMachineMode());
            }
        })
            .setPlayClickSound(supportsMachineModeSwitch())
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                if (supportsMachineModeSwitch()) {
                    ret.add(GTUITextures.BUTTON_STANDARD);
                    ret.add(getMachineModeIcon(getMachineMode()));
                } else return null;
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.IntegerSyncer(this::getMachineMode, this::setMachineMode), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.gui.button.mode_switch"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(getMachineModeSwitchButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack tool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (supportsMachineModeSwitch()) {
                setMachineMode(nextMachineMode());
                GTUtility.sendChatToPlayer(aPlayer, getMachineModeName());
            } else {
                super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ, tool);
            }
        }
    }

    public boolean showModeInWaila() {
        return supportsMachineModeSwitch();
    }

    // endregion

    // region waila + nbt

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) tag.setString("costingEUText", costingEUText);
        }
        if (showModeInWaila()) {
            tag.setInteger("modeTST", machineMode);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
        if (tag.hasKey("modeTST")) {
            // #tr TST.machines.running_mode
            // # Running Mode :
            // # zh_CN 运行模式 :
            currentTip.add(
                "" + EnumChatFormatting.YELLOW
                    + StatCollector.translateToLocal("TST.machines.running_mode")
                    + " "
                    + EnumChatFormatting.WHITE
                    + getMachineModeName(tag.getInteger("modeTST"))
                    + EnumChatFormatting.RESET);
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

    // endregion
}
