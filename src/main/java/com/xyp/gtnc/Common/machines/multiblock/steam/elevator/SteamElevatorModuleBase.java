package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.util.GTStructureUtility.ofHatchAdderOptional;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;

/**
 * Common shell for Steam Elevator modules.
 *
 * <p>
 * No concrete module is registered here. Future modules only need to extend this class and implement their own
 * processing policy; the elevator supplies their internal steam-equivalent energy buffer.
 */
public abstract class SteamElevatorModuleBase extends GTNCSteamMultiBlockBase<SteamElevatorModuleBase>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE = RESOURCE_ROOT_ID + ":multiblock/steam_elevator_module";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE);
    private static final int HORIZONTAL_OFFSET = 0;
    private static final int VERTICAL_OFFSET = 1;
    private static final int DEPTH_OFFSET = 0;
    private static final int CASING_TEXTURE_ID = StructureUtils.getTextureIndex(sBlockCasings2, 0);
    private static final String NBT_STORED_STEAM = "steamElevatorModuleSteam";
    private static IIconContainer moduleScreenOn;
    private static IIconContainer moduleScreenOff;

    protected final int moduleTier;
    protected long steamBufferSize;
    private boolean connected;
    private boolean voltageConfigUnlocked;
    private boolean perfectOverclockUnlocked;
    private boolean parallelUpgradeUnlocked;
    private boolean speedUpgradeUnlocked;
    private boolean steamEfficiencyUpgradeUnlocked;
    private boolean advancedParallelUpgradeUnlocked;
    private boolean extremeParallelUpgradeUnlocked;
    private long configuredProcessingVoltage;
    private long storedModuleSteam;

    protected SteamElevatorModuleBase(int id, String name, String regionalName, int tier) {
        super(id, name, regionalName);
        moduleTier = tier;
        steamBufferSize = 640_000L * (1L << Math.max(0, Math.min(tier, 43)));
        configuredProcessingVoltage = getDefaultProcessingVoltage();
    }

    protected SteamElevatorModuleBase(String name, int tier) {
        super(name);
        moduleTier = tier;
        steamBufferSize = 640_000L * (1L << Math.max(0, Math.min(tier, 43)));
        configuredProcessingVoltage = getDefaultProcessingVoltage();
    }

    @Override
    public IStructureDefinition<SteamElevatorModuleBase> getStructureDefinition() {
        return StructureDefinition.<SteamElevatorModuleBase>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(SHAPE))
            .addElement(
                'A',
                ofHatchAdderOptional(
                    SteamElevatorModuleBase::addOptionalModuleIOToMachineList,
                    CASING_TEXTURE_ID,
                    1,
                    sBlockCasings2,
                    0))
            .build();
    }

    private boolean addOptionalModuleIOToMachineList(IGregTechTileEntity tile, int casingIndex) {
        return addSteamBusInput(tile, casingIndex) || addSteamBusOutput(tile, casingIndex)
            || addInputToMachineList(tile, casingIndex)
            || addOutputToMachineList(tile, casingIndex);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment environment) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            environment,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity base, ItemStack stack, List<StructureError> errors) {
        wirelessMode = false;
        if (checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) {
            updateHatchTexture();
        }
    }

    /** Modules never select a steam source; the connected Steam Elevator is their sole supplier. */
    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer player, float x, float y, float z,
        ItemStack tool) {
        wirelessMode = false;
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        if (!base.isServerSide() || connected) super.onPostTick(base, tick);
        if (base.isServerSide() && connected && storedModuleSteam <= 0 && mMaxProgresstime > 0) {
            stopMachine(ShutDownReasonRegistry.POWER_LOSS);
        }
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (lEUt > 0) lEUt = -lEUt;
        if (lEUt < 0) {
            long required = getRequiredSteamForTick(lEUt, mEfficiency);
            if (steamEfficiencyUpgradeUnlocked) required = applySteamConsumptionDiscount(required);
            if (!tryConsumeSteam(required)) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        super.setProcessingLogicPower(logic);
        logic.setAvailableVoltage(getEffectiveProcessingVoltage());
        logic.setOverclock(perfectOverclockUnlocked ? 4.0 : 2.0, 4.0);
        logic.setSpeedBonus(speedUpgradeUnlocked ? 0.9 : 1.0);
    }

    @Override
    public boolean tryConsumeSteam(int amount) {
        return tryConsumeSteam((long) amount);
    }

    private boolean tryConsumeSteam(long amount) {
        if (amount <= 0) return true;
        if (storedModuleSteam < amount) return false;
        storedModuleSteam -= amount;
        totalSteamConsumed = totalSteamConsumed > Long.MAX_VALUE - amount ? Long.MAX_VALUE
            : totalSteamConsumed + amount;
        return true;
    }

    private static long getRequiredSteamForTick(long eut, int efficiency) {
        long positiveEut = eut == Long.MIN_VALUE ? Long.MAX_VALUE : -eut;
        long safeEfficiency = Math.max(1_000, efficiency);
        long whole = positiveEut / safeEfficiency;
        if (whole > Long.MAX_VALUE / 10_000L) return Long.MAX_VALUE;
        long steam = whole * 10_000L;
        long remainderSteam = (positiveEut % safeEfficiency) * 10_000L / safeEfficiency;
        return steam > Long.MAX_VALUE - remainderSteam ? Long.MAX_VALUE : steam + remainderSteam;
    }

    private static long applySteamConsumptionDiscount(long amount) {
        long whole = amount / 100L;
        long remainder = amount % 100L;
        return whole * 95L + (remainder * 95L + 99L) / 100L;
    }

    public long increaseStoredEU(long maximumIncrease) {
        if (maximumIncrease <= 0 || getBaseMetaTileEntity() == null) return 0;
        connect();
        long room = Math.max(0, steamBufferSize - storedModuleSteam);
        long increased = Math.min(room, maximumIncrease);
        if (increased <= 0) return 0;
        storedModuleSteam += increased;
        getBaseMetaTileEntity().markDirty();
        return increased;
    }

    @Override
    public long getTotalSteamCapacityLong() {
        return steamBufferSize;
    }

    @Override
    public long getLongTotalSteamStored() {
        return storedModuleSteam;
    }

    @Override
    public long maxEUStore() {
        return 0;
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public int getTierRecipes() {
        return moduleTier;
    }

    @Override
    protected boolean isHighPressure() {
        return false;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    public int getModuleTier() {
        return moduleTier;
    }

    @Override
    public final int getMaxParallelRecipes() {
        int baseParallel = Math.max(1, getBaseMaxParallelRecipes());
        int upgradedParallel = parallelUpgradeUnlocked ? Math.max(64, baseParallel) : baseParallel;
        int advancedParallel = advancedParallelUpgradeUnlocked ? Math.max(128, upgradedParallel) : upgradedParallel;
        return extremeParallelUpgradeUnlocked ? Math.max(256, advancedParallel) : advancedParallel;
    }

    protected int getBaseMaxParallelRecipes() {
        return 1;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    public long getDefaultProcessingVoltage() {
        return GTValues.V[Math.max(0, Math.min(moduleTier, GTValues.V.length - 1))];
    }

    public long getConfiguredProcessingVoltage() {
        return configuredProcessingVoltage;
    }

    public long getEffectiveProcessingVoltage() {
        long requested = voltageConfigUnlocked ? configuredProcessingVoltage : getDefaultProcessingVoltage();
        return Math.min(requested, Long.MAX_VALUE / Math.max(1, getTrueParallel()));
    }

    public void setConfiguredProcessingVoltageFromGui(long voltage) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null && base.isServerSide() && !voltageConfigUnlocked) return;
        configuredProcessingVoltage = Math.max(1L, voltage);
        if (base != null && base.isServerSide()) base.markDirty();
    }

    public void setPowerPanelMaxParallelFromGui(int parallel) {
        int maximum = Math.max(1, getMaxParallelRecipes());
        setPowerPanelMaxParallel(Math.max(1, Math.min(maximum, parallel)));
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null && base.isServerSide()) base.markDirty();
    }

    public void setAlwaysMaxParallelFromGui(boolean alwaysMax) {
        setAlwaysMaxParallel(alwaysMax);
        if (alwaysMax) setPowerPanelMaxParallel(getMaxParallelRecipes());
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null && base.isServerSide()) base.markDirty();
    }

    public boolean isVoltageConfigUnlocked() {
        return voltageConfigUnlocked;
    }

    public void setVoltageConfigUnlocked(boolean unlocked) {
        voltageConfigUnlocked = unlocked;
    }

    public void setPerfectOverclockUnlocked(boolean unlocked) {
        perfectOverclockUnlocked = unlocked;
    }

    public void setParallelUpgradeUnlocked(boolean unlocked) {
        parallelUpgradeUnlocked = unlocked;
        refreshPowerPanelParallelLimit();
    }

    public void setSpeedUpgradeUnlocked(boolean unlocked) {
        speedUpgradeUnlocked = unlocked;
    }

    public void setSteamEfficiencyUpgradeUnlocked(boolean unlocked) {
        steamEfficiencyUpgradeUnlocked = unlocked;
    }

    public void setAdvancedParallelUpgradeUnlocked(boolean unlocked) {
        advancedParallelUpgradeUnlocked = unlocked;
        refreshPowerPanelParallelLimit();
    }

    public void setExtremeParallelUpgradeUnlocked(boolean unlocked) {
        extremeParallelUpgradeUnlocked = unlocked;
        refreshPowerPanelParallelLimit();
    }

    private void refreshPowerPanelParallelLimit() {
        int maximum = getMaxParallelRecipes();
        setPowerPanelMaxParallel(isAlwaysMaxParallel() ? maximum : Math.min(maximum, getPowerPanelMaxParallel()));
    }

    public int getMachineEffectRange() {
        return 0;
    }

    public boolean isConnected() {
        return connected;
    }

    public void connect() {
        connected = true;
    }

    public void disconnect() {
        connected = false;
        voltageConfigUnlocked = false;
        perfectOverclockUnlocked = false;
        parallelUpgradeUnlocked = false;
        speedUpgradeUnlocked = false;
        steamEfficiencyUpgradeUnlocked = false;
        advancedParallelUpgradeUnlocked = false;
        extremeParallelUpgradeUnlocked = false;
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        tag.setLong("steamElevatorProcessingVoltage", configuredProcessingVoltage);
        tag.setLong(NBT_STORED_STEAM, storedModuleSteam);
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        tag.setLong("steamElevatorProcessingVoltage", configuredProcessingVoltage);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        wirelessMode = false;
        long legacyStoredSteam = Math.max(0, getEUVar());
        storedModuleSteam = Math.min(
            steamBufferSize,
            Math.max(0, tag.hasKey(NBT_STORED_STEAM) ? tag.getLong(NBT_STORED_STEAM) : legacyStoredSteam));
        setEUVar(0);
        configuredProcessingVoltage = tag.hasKey("steamElevatorProcessingVoltage")
            ? Math.max(1L, tag.getLong("steamElevatorProcessingVoltage"))
            : getDefaultProcessingVoltage();
        voltageConfigUnlocked = false;
        perfectOverclockUnlocked = false;
        parallelUpgradeUnlocked = false;
        speedUpgradeUnlocked = false;
        steamEfficiencyUpgradeUnlocked = false;
        advancedParallelUpgradeUnlocked = false;
        extremeParallelUpgradeUnlocked = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        moduleScreenOn = Textures.BlockIcons.custom("iconsets/GODFORGE_MODULE_ACTIVE");
        moduleScreenOff = Textures.BlockIcons.custom("iconsets/SCREEN_OFF");
        super.registerIcons(iconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(moduleScreenOn)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(moduleScreenOn)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(moduleScreenOff)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public int getCasingTextureID() {
        return CASING_TEXTURE_ID;
    }

    @Override
    protected int getCasingTextureId() {
        return CASING_TEXTURE_ID;
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return moduleScreenOff;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return moduleScreenOn;
    }
}
