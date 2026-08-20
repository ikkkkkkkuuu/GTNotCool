package com.xyp.gtnc.Common.machines.multiblock.steam.elevator;

import static com.xyp.gtnc.Loader.BlockLoader.metaCasing02;
import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTStructureUtility.ofHatchAdderOptional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator.SteamElevatorGui;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.StructureUtils;
import com.xyp.gtnc.utils.enums.SteamTypes;
import com.xyp.gtnc.utils.lang.TextLocalization;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GTChunkManager;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;

/** Steam-powered counterpart of the GTNH Space Elevator core. */
public class SteamElevator extends GTNCSteamMultiBlockBase<SteamElevator>
    implements ISurvivalConstructable, INEIPreviewModifier {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_EXTENDED = "extended";
    private static final String STRUCTURE_PIECE_EXTENDED_MODULES = "extended_modules";
    private static final String STRUCTURE_FILE = RESOURCE_ROOT_ID + ":multiblock/steam_elevator";
    private static final String EXTENDED_STRUCTURE_FILE = RESOURCE_ROOT_ID + ":multiblock/steam_elevator_extended";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE);
    private static final String[][] EXTENDED_SHAPE = StructureUtils.readStructureFromFile(EXTENDED_STRUCTURE_FILE);
    private static final String[][] EXTENDED_MODULE_SHAPE = extractModuleSocketShape(EXTENDED_SHAPE);
    private static final int HORIZONTAL_OFFSET = 17;
    private static final int VERTICAL_OFFSET = 39;
    private static final int DEPTH_OFFSET = 14;
    private static final int EXTENDED_HORIZONTAL_OFFSET = 17;
    private static final int FIRST_EXTENDED_VERTICAL_OFFSET = -4;
    private static final int EXTENDED_DEPTH_OFFSET = 14;
    private static final int EXTENDED_LAYER_HEIGHT = 6;
    private static final int MAX_EXTENSION_LAYERS = 4;
    private static final int MODULE_CHARGE_INTERVAL = 20;
    private static final int CASING_TEXTURE_ID = StructureUtils.getTextureIndex(sBlockCasings2, 0);
    private static final int STEEL_WOOD_TEXTURE_ID = metaCasing02.getTextureIndex(25);

    private final ArrayList<SteamElevatorModuleBase> moduleHatches = new ArrayList<>();
    private final SteamElevatorEvolutionData evolutionData = new SteamElevatorEvolutionData();
    private boolean loadedChunks;
    private int recognizedExtensionLayers;
    private int previewExtensionLayers = -1;

    public SteamElevator(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public SteamElevator(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamElevator(mName);
    }

    @Override
    public IStructureDefinition<SteamElevator> getStructureDefinition() {
        return StructureDefinition.<SteamElevator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(SHAPE))
            .addShape(STRUCTURE_PIECE_EXTENDED, StructureUtility.transpose(EXTENDED_SHAPE))
            .addShape(STRUCTURE_PIECE_EXTENDED_MODULES, StructureUtility.transpose(EXTENDED_MODULE_SHAPE))
            .addElement(
                'A',
                buildSteamInput(SteamElevator.class).casingIndex(STEEL_WOOD_TEXTURE_ID)
                    .hint(1)
                    .buildAndChain(metaCasing02, 25))
            .addElement('B', StructureUtility.ofBlock(metaCasing02, 31))
            .addElement('C', StructureUtility.ofBlock(sBlockCasings1, 10))
            .addElement('D', StructureUtility.ofBlock(sBlockCasings2, 0))
            .addElement('E', StructureUtility.ofBlock(sBlockCasings3, 14))
            .addElement('F', ofFrame(Materials.Steel))
            .addElement('G', StructureUtility.ofBlock(Blocks.brick_block, 0))
            .addElement(
                'H',
                StructureUtility.ofChain(
                    buildSteamInput(SteamElevator.class).casingIndex(STEEL_WOOD_TEXTURE_ID)
                        .hint(1)
                        .build(),
                    buildHatchAdder(SteamElevator.class)
                        .atLeast(
                            SteamHatchElement.InputBus_Steam,
                            SteamHatchElement.OutputBus_Steam,
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch)
                        .casingIndex(CASING_TEXTURE_ID)
                        .hint(1)
                        .buildAndChain(sBlockCasings2, 0)))
            .addElement(
                'I',
                ofHatchAdderOptional(SteamElevator::addModuleToMachineList, CASING_TEXTURE_ID, 1, sBlockCasings2, 0))
            .addElement('J', StructureUtility.ofBlock(Blocks.stonebrick, 0))
            .addElement('K', StructureUtility.ofBlock(metaCasing02, 25))
            .build();
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
        int extensionLayers = getConstructionExtensionLayers();
        for (int layer = 0; layer < extensionLayers; layer++) {
            buildPiece(
                STRUCTURE_PIECE_EXTENDED,
                stack,
                hintsOnly,
                EXTENDED_HORIZONTAL_OFFSET,
                getExtendedVerticalOffset(layer),
                EXTENDED_DEPTH_OFFSET);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment environment) {
        int built = survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            environment,
            false,
            true);
        if (built >= 0) return built;

        int extensionLayers = getConstructionExtensionLayers();
        for (int layer = 0; layer < extensionLayers; layer++) {
            built = survivalBuildPiece(
                STRUCTURE_PIECE_EXTENDED,
                stack,
                EXTENDED_HORIZONTAL_OFFSET,
                getExtendedVerticalOffset(layer),
                EXTENDED_DEPTH_OFFSET,
                elementBudget,
                environment,
                false,
                true);
            if (built >= 0) return built;
        }
        return built;
    }

    @Override
    public void checkMachine(IGregTechTileEntity base, ItemStack stack, List<StructureError> errors) {
        disconnectModules();
        moduleHatches.clear();
        recognizedExtensionLayers = 0;
        wirelessMode = false;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;
        int unlockedExtensionLayers = evolutionData.getUnlockedExtensionLayers();
        for (int layer = 0; layer < unlockedExtensionLayers; layer++) {
            int firstModuleIndex = moduleHatches.size();
            if (!checkPiece(
                STRUCTURE_PIECE_EXTENDED_MODULES,
                EXTENDED_HORIZONTAL_OFFSET,
                getExtendedVerticalOffset(layer),
                EXTENDED_DEPTH_OFFSET,
                null)) {
                rollbackModules(firstModuleIndex);
                break;
            }
            checkPiece(
                STRUCTURE_PIECE_EXTENDED,
                EXTENDED_HORIZONTAL_OFFSET,
                getExtendedVerticalOffset(layer),
                EXTENDED_DEPTH_OFFSET,
                null);
            recognizedExtensionLayers++;
        }
        evolutionData.observeConnectedModules(moduleHatches.size());
        if (mSteamInputFluids.isEmpty()) wirelessMode = true;
        updateHatchTexture();
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        disconnectModules();
        moduleHatches.clear();
        recognizedExtensionLayers = 0;
    }

    private static int getExtendedVerticalOffset(int layer) {
        return FIRST_EXTENDED_VERTICAL_OFFSET - layer * EXTENDED_LAYER_HEIGHT;
    }

    private int getConstructionExtensionLayers() {
        return previewExtensionLayers >= 0 ? previewExtensionLayers : evolutionData.getUnlockedExtensionLayers();
    }

    private static String[][] extractModuleSocketShape(String[][] source) {
        String[][] sockets = new String[source.length][];
        for (int y = 0; y < source.length; y++) {
            sockets[y] = new String[source[y].length];
            for (int z = 0; z < source[y].length; z++) {
                char[] row = source[y][z].toCharArray();
                for (int x = 0; x < row.length; x++) {
                    if (row[x] != 'I') row[x] = ' ';
                }
                sockets[y][z] = new String(row);
            }
        }
        return sockets;
    }

    private void rollbackModules(int firstModuleIndex) {
        while (moduleHatches.size() > firstModuleIndex) {
            moduleHatches.remove(moduleHatches.size() - 1)
                .disconnect();
        }
    }

    public boolean addModuleToMachineList(IGregTechTileEntity tileEntity, int casingIndex) {
        if (tileEntity == null || !(tileEntity.getMetaTileEntity() instanceof SteamElevatorModuleBase module)) {
            return false;
        }
        applyEvolutionState(module);
        module.connect();
        if (!moduleHatches.contains(module)) moduleHatches.add(module);
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity base, long tick) {
        boolean refreshModules = base.isServerSide() && hasInvalidModuleReference();
        super.onPostTick(base, tick);
        if (!base.isServerSide()) return;

        if (refreshModules) checkStructure(true, base);
        updateChunkLoading(base);
        if (mMachine && base.isAllowedToWork()) {
            synchronizeModules();
            evolutionData.addActiveTick();
            if (tick % MODULE_CHARGE_INTERVAL == 0) chargeModules();
        } else {
            disconnectModules();
        }
        if (mEfficiency < 0) mEfficiency = 0;
    }

    private boolean hasInvalidModuleReference() {
        for (SteamElevatorModuleBase module : moduleHatches) {
            IGregTechTileEntity moduleBase = module.getBaseMetaTileEntity();
            if (moduleBase == null || moduleBase.isDead() || moduleBase.getMetaTileEntity() != module) return true;
        }
        return false;
    }

    private void synchronizeModules() {
        for (SteamElevatorModuleBase module : moduleHatches) {
            applyEvolutionState(module);
            module.connect();
        }
    }

    private void updateChunkLoading(IGregTechTileEntity base) {
        boolean shouldLoad = mMachine && base.isAllowedToWork();
        if (!shouldLoad && loadedChunks) {
            GTChunkManager.releaseTicket((TileEntity) base);
            loadedChunks = false;
            return;
        }
        if (!shouldLoad || loadedChunks) return;

        int facingX = base.getFrontFacing().offsetX;
        int facingZ = base.getFrontFacing().offsetZ;
        GTChunkManager.releaseTicket((TileEntity) base);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                GTChunkManager.requestChunkLoad(
                    (TileEntity) base,
                    new ChunkCoordIntPair(getChunkX() + facingX + x, getChunkZ() + facingZ + z));
            }
        }
        loadedChunks = true;
    }

    private void chargeModules() {
        if (moduleHatches.isEmpty()) return;
        if (wirelessMode) {
            chargeModulesFromNetwork();
            return;
        }

        long available = getEUVar();
        if (available <= 0) return;
        long perModule = Math.max(1, available / moduleHatches.size());
        for (SteamElevatorModuleBase module : moduleHatches) {
            long used = module.increaseStoredEU(Math.min(perModule, getEUVar()));
            recordModuleCharge(used);
            setEUVar(Math.max(0, getEUVar() - used));
        }
    }

    private void chargeModulesFromNetwork() {
        if (ownerUUID == null) return;
        BigInteger available = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
        for (SteamElevatorModuleBase module : moduleHatches) {
            if (available.signum() <= 0) break;
            long offer = available.min(BigInteger.valueOf(Long.MAX_VALUE))
                .longValue();
            long used = module.increaseStoredEU(offer);
            if (used <= 0) continue;
            if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, -used)) break;
            recordModuleCharge(used);
            totalSteamConsumed += used;
            available = available.subtract(BigInteger.valueOf(used));
        }
    }

    private void recordModuleCharge(long amount) {
        if (amount <= 0) return;
        evolutionData.addSteamDistributed(amount);
        evolutionData.addModuleCharge();
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (!wirelessMode) consumeBestAvailableSteam();
        return true;
    }

    private void consumeBestAvailableSteam() {
        long room = maxEUStore() - getEUVar();
        if (room <= 0) return;

        net.minecraftforge.fluids.FluidStack bestStack = null;
        SteamTypes bestType = null;
        long bestEquivalent = 0;
        for (net.minecraftforge.fluids.FluidStack candidate : getAllSteamStacks()) {
            if (candidate == null) continue;
            for (SteamTypes type : SteamTypes.getSupportedTypes()) {
                if (candidate.getFluid() != type.fluid) continue;
                long equivalent = (long) candidate.amount * type.efficiencyFactor;
                if (equivalent > bestEquivalent) {
                    bestEquivalent = equivalent;
                    bestStack = candidate;
                    bestType = type;
                }
                break;
            }
        }
        if (bestStack == null || bestType == null) return;

        long generated = Math.min(bestEquivalent, room);
        int amount = (int) Math
            .min(bestStack.amount, (generated + bestType.efficiencyFactor - 1) / bestType.efficiencyFactor);
        if (amount <= 0 || !depleteInput(new net.minecraftforge.fluids.FluidStack(bestType.fluid, amount))) return;
        long accepted = Math.min(room, (long) amount * bestType.efficiencyFactor);
        setEUVar(getEUVar() + accepted);
        totalSteamConsumed += amount;
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        if (!getBaseMetaTileEntity().isAllowedToWork()) {
            mEfficiencyIncrease = 0;
            mMaxProgresstime = 0;
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        mEfficiencyIncrease = 10_000;
        mMaxProgresstime = 10;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public void onRemoval() {
        disconnectModules();
        if (getBaseMetaTileEntity() != null) GTChunkManager.releaseTicket((TileEntity) getBaseMetaTileEntity());
        loadedChunks = false;
        super.onRemoval();
    }

    private void disconnectModules() {
        for (SteamElevatorModuleBase module : moduleHatches) module.disconnect();
    }

    public int getChunkX() {
        return getBaseMetaTileEntity().getXCoord() >> 4;
    }

    public int getChunkZ() {
        return getBaseMetaTileEntity().getZCoord() >> 4;
    }

    public int getNumberOfModules() {
        return moduleHatches.size();
    }

    public int getRecognizedExtensionLayers() {
        return recognizedExtensionLayers;
    }

    public boolean isMachineForGui() {
        return mMachine;
    }

    public boolean isAllowedToWorkForGui() {
        return getBaseMetaTileEntity() != null && getBaseMetaTileEntity().isAllowedToWork();
    }

    public int getNumberOfModulesForGui() {
        return getNumberOfModules();
    }

    public void refreshModuleConnections() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || base.isDead() || !base.isServerSide()) return;
        checkStructure(true, base);
    }

    public SteamElevatorEvolutionData getEvolutionData() {
        return evolutionData;
    }

    public boolean tryUnlockEvolutionUpgrade(ForgeOfGodsUpgrade upgrade) {
        boolean changed = evolutionData.tryUnlock(upgrade);
        if (changed) {
            refreshStructureAfterExtensionChange(upgrade);
            applyEvolutionStateToModules();
            if (getBaseMetaTileEntity() != null) getBaseMetaTileEntity().markDirty();
        }
        return changed;
    }

    public boolean tryRespecEvolutionUpgrade(ForgeOfGodsUpgrade upgrade) {
        boolean changed = evolutionData.tryRespec(upgrade);
        if (changed) {
            refreshStructureAfterExtensionChange(upgrade);
            applyEvolutionStateToModules();
            if (getBaseMetaTileEntity() != null) getBaseMetaTileEntity().markDirty();
        }
        return changed;
    }

    private void refreshStructureAfterExtensionChange(ForgeOfGodsUpgrade upgrade) {
        if (SteamElevatorEvolutionData.getExtensionLayer(upgrade) == 0) return;
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base != null && !base.isDead() && base.isServerSide()) checkStructure(true, base);
    }

    private void applyEvolutionStateToModules() {
        for (SteamElevatorModuleBase module : moduleHatches) applyEvolutionState(module);
    }

    private void applyEvolutionState(SteamElevatorModuleBase module) {
        module.setVoltageConfigUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.IGCC));
        module.setPerfectOverclockUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.STEM));
        module.setParallelUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.CFCE));
        module.setSpeedUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.GISS));
        module.setSteamEfficiencyUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.FDIM));
        module.setAdvancedParallelUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.SA));
        module.setExtremeParallelUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.SEFCP));
        if (module instanceof SteamPlasmaModule plasmaModule) {
            plasmaModule.setMultiStepPlasmaUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.GEM));
        }
        if (module instanceof SteamSmeltingModule smeltingModule) {
            smeltingModule.setHeatUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.CTCDD));
            smeltingModule.setAdvancedHeatUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.GGEBE));
        }
        if (module instanceof SteamMoltenModule moltenModule) {
            moltenModule.setHeatUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.CTCDD));
            moltenModule.setAdvancedHeatUpgradeUnlocked(evolutionData.isUpgradeActive(ForgeOfGodsUpgrade.GGEBE));
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound tag) {
        super.saveNBTData(tag);
        evolutionData.writeToNBT(tag);
    }

    @Override
    public void setItemNBT(NBTTagCompound tag) {
        super.setItemNBT(tag);
        evolutionData.writeToNBT(tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound tag) {
        super.loadNBTData(tag);
        evolutionData.readFromNBT(tag);
    }

    public void openCelestialSelection(EntityPlayer player) {
        if (!mMachine || !isAllowedToWorkForGui() || !(player instanceof EntityPlayerMP serverPlayer)) return;
        GCPlayerStats stats = GCPlayerStats.get(serverPlayer);
        stats.coordsTeleportedFromX = serverPlayer.posX;
        stats.coordsTeleportedFromZ = serverPlayer.posZ;
        try {
            WorldUtil.toCelestialSelection(serverPlayer, stats, 250, GuiCelestialSelection.MapMode.TELEPORTATION);
        } catch (RuntimeException exception) {
            com.xyp.gtnc.ScienceNotCool.LOG.error("Failed to open Steam Elevator celestial selection", exception);
        }
    }

    @Override
    public long maxEUStore() {
        return 256_000_000_000_000L;
    }

    @Override
    public boolean willExplodeInRain() {
        return false;
    }

    @Override
    public int getTierRecipes() {
        return 1;
    }

    @Override
    protected boolean isHighPressure() {
        return false;
    }

    @Override
    public boolean doesBindPlayerInventory() {
        return false;
    }

    @Override
    public String getMachineType() {
        return TextLocalization.SteamElevatorRecipeType;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(getMachineType())
            .addInfo(TextLocalization.Tooltip_SteamElevator_00)
            .addInfo(TextLocalization.Tooltip_SteamElevator_01)
            .addInfo(TextLocalization.Tooltip_SteamElevator_02)
            .addInfo(TextLocalization.Tooltip_SteamElevator_03)
            .addInfo(TextLocalization.Tooltip_SteamElevator_04)
            .addInfo(TextLocalization.Tooltip_SteamElevator_05)
            .addInfo(TextLocalization.Tooltip_SteamElevator_06)
            .beginStructureBlock(35, 43, 35, false)
            .addController("Front center")
            .addSteamHatch("1+", TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addSteamInputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addSteamOutputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addInputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_SteamElevator_Casing, 1)
            .toolTipFinisher();
    }

    @Override
    public void onPreviewConstruct(@NotNull ItemStack trigger) {
        previewExtensionLayers = Math.max(0, Math.min(MAX_EXTENSION_LAYERS, trigger.stackSize - 1));
    }

    @Override
    public void onPreviewStructureComplete(@NotNull ItemStack trigger) {
        previewExtensionLayers = -1;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                new TTRenderedExtendedFacingTexture(
                    aActive ? GTNCGuiTextures.OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE
                        : GTNCGuiTextures.OVERLAY_FRONT_TECTECH_MULTIBLOCK) };
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
        return Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return Textures.BlockIcons.OVERLAY_FRONT_MULTI_COMPRESSOR_ACTIVE;
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_WHOOUM;
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new SteamElevatorGui(this);
    }
}
