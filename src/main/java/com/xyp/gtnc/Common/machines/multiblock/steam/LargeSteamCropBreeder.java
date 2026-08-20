package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.cropsnh.init.CropsNHBlockTextures.OVERLAY_FRONT_CROP_BREEDER;
import static com.gtnewhorizon.cropsnh.init.CropsNHBlockTextures.OVERLAY_FRONT_CROP_BREEDER_ACTIVE;
import static com.gtnewhorizon.cropsnh.init.CropsNHBlockTextures.OVERLAY_FRONT_CROP_BREEDER_ACTIVE_GLOW;
import static com.gtnewhorizon.cropsnh.init.CropsNHBlockTextures.OVERLAY_FRONT_CROP_BREEDER_GLOW;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockFrames;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.api.ICropMutation;
import com.gtnewhorizon.cropsnh.api.IMutationPool;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.api.ISeedStats;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.farming.registries.CropRegistry;
import com.gtnewhorizon.cropsnh.farming.registries.MutationRegistry;
import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.LargeSteamCropBreederGui;
import com.xyp.gtnc.Common.machines.crop.CropArchive;
import com.xyp.gtnc.Common.machines.crop.CropBreedingPlanner;
import com.xyp.gtnc.Common.machines.crop.CropMutationMachineRequirements;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCAdvancedSteamMultiBlockBase;
import com.xyp.gtnc.utils.lang.TextLocalization;

import bartworks.common.loaders.ItemRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class LargeSteamCropBreeder extends GTNCAdvancedSteamMultiBlockBase<LargeSteamCropBreeder>
    implements ISurvivalConstructable {

    private static final int MODE_DETERMINISTIC = 0;
    private static final int MODE_MUTATION_POOL = 1;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final int POOL_BREEDING_EUT = 32;
    private static final int POOL_BREEDING_DURATION = 20 * 20;
    private static final int MAX_ARCHIVE_STEPS_PER_CYCLE = 1;
    private static final byte OPERATION_NONE = 0;
    private static final byte OPERATION_ARCHIVE_BREEDING = 1;
    private static final byte OPERATION_OUTPUT = 2;
    private static final String NBT_TARGET_CROP_ID = "targetCropId";

    // 5 wide (x), 4 tall (y), 3 deep (z), following the CropsNH Industrial Farm greenhouse silhouette.
    // A = glass, B = tiered casing and hatches, C = pipe casing/seed bed, D = frame.
    private static final String[][] SHAPE = new String[][] { { " DBD ", " ACA ", " DBD " },
        { "DBBBD", "A   A", "DBBBD" }, { "DB~BD", "DCCCD", "DBBBD" }, { "D   D", "     ", "D   D" } };

    private int casingCount;
    private int machineCasingTier = -1;
    private int pipeCasingTier = -1;
    private int frameTier = -1;
    private IStructureDefinition<LargeSteamCropBreeder> structureDefinition;
    private String targetCropId = "";
    private boolean targetInputValid = true;
    private CropArchive cropArchive = new CropArchive();
    private CropBreedingPlanner.Plan breedingPlan = CropBreedingPlanner.Plan.empty("");
    private List<CropBreedingPlanner.Step> breedingChain = new ArrayList<>();
    private boolean breedingPlanDirty = true;
    private byte activeOperation = OPERATION_NONE;
    private String activeCropId = "";
    private int activeGrowth;
    private int activeGain;
    private int activeResistance;
    private int pendingSeedOutputs;
    private String missingCropId = "";
    private boolean allTasksBlocked;
    private boolean missingBreedingRequirements;
    private int chainTotalSteps;
    private int chainCompletedSteps;
    private int syncedArchiveSize;
    private String syncedMissingInfo = "";
    private List<String> syncedArchiveCrops = Collections.emptyList();
    private List<NBTTagCompound> syncedChainSteps = Collections.emptyList();
    private boolean displayDirty = true;

    public LargeSteamCropBreeder(String name) {
        super(name);
    }

    public LargeSteamCropBreeder(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    private void markDisplayDirty() {
        displayDirty = true;
    }

    private void invalidateBreedingPlan() {
        breedingPlanDirty = true;
        markDisplayDirty();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new LargeSteamCropBreeder(this.mName);
    }

    @Override
    public String getMachineType() {
        return TextLocalization.LargeSteamCropBreederMachineType;
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new LargeSteamCropBreederGui(this)
            .withMachineModeIcons(GTNCGuiTextures.OVERLAY_BUTTON_RECIPE_LOCKED, GTNCGuiTextures.OVERLAY_CYCLIC_BLUE);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public String getMachineModeName() {
        return machineMode == MODE_MUTATION_POOL ? TextLocalization.LargeSteamCropBreederModeMutationPool
            : TextLocalization.LargeSteamCropBreederModeDeterministic;
    }

    @Override
    public void setMachineMode(int index) {
        super.setMachineMode(index == MODE_MUTATION_POOL ? MODE_MUTATION_POOL : MODE_DETERMINISTIC);
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return OVERLAY_FRONT_CROP_BREEDER;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return OVERLAY_FRONT_CROP_BREEDER_ACTIVE;
    }

    @Override
    protected IIconContainer getInactiveGlowOverlay() {
        return OVERLAY_FRONT_CROP_BREEDER_GLOW;
    }

    @Override
    protected IIconContainer getActiveGlowOverlay() {
        return OVERLAY_FRONT_CROP_BREEDER_ACTIVE_GLOW;
    }

    @Override
    public IStructureDefinition<LargeSteamCropBreeder> getStructureDefinition() {
        if (structureDefinition == null) {
            structureDefinition = StructureDefinition.<LargeSteamCropBreeder>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(SHAPE))
                .addElement(
                    'B',
                    ofChain(
                        buildSteamInput(LargeSteamCropBreeder.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamCropBreeder.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(),
                        onElementPass(
                            machine -> ++machine.casingCount,
                            ofBlocksTiered(
                                this::getMachineCasingTier,
                                ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                -1,
                                (machine, tier) -> machine.machineCasingTier = tier,
                                machine -> machine.machineCasingTier))))
                .addElement('A', ofBlock(ItemRegistry.bw_realglas, 0))
                .addElement(
                    'C',
                    ofBlocksTiered(
                        LargeSteamCropBreeder::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (machine, tier) -> machine.pipeCasingTier = tier,
                        machine -> machine.pipeCasingTier))
                .addElement(
                    'D',
                    ofBlocksTiered(
                        LargeSteamCropBreeder::getTierFrame,
                        ImmutableList.of(
                            Pair.of(sBlockFrames, Materials.Bronze.mMetaItemSubID),
                            Pair.of(sBlockFrames, Materials.Steel.mMetaItemSubID)),
                        -1,
                        (machine, tier) -> machine.frameTier = tier,
                        machine -> machine.frameTier))
                .build();
        }
        return structureDefinition;
    }

    @Nullable
    public Integer getMachineCasingTier(Block block, int meta) {
        if (block == sBlockCasings1 && meta == 10) {
            casingCount++;
            return 1;
        }
        if (block == sBlockCasings2 && meta == 0) {
            casingCount++;
            return 2;
        }
        return null;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity baseTileEntity, ItemStack stack, List<StructureError> errors) {
        casingCount = 0;
        machineCasingTier = -1;
        pipeCasingTier = -1;
        frameTier = -1;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        if (machineCasingTier >= 1 && machineCasingTier == pipeCasingTier && machineCasingTier == frameTier) {
            tierMachine = machineCasingTier;
            syncTierValue = machineCasingTier;
            updateHatchTexture();
        } else {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }
        checkCasingMin(errors, casingCount, 3);
        enableHigherRecipe = getUpgradeTier(getControllerSlot());
        markDisplayDirty();
    }

    @Override
    public void onPostTick(IGregTechTileEntity baseTileEntity, long tick) {
        super.onPostTick(baseTileEntity, tick);
        if (baseTileEntity.isServerSide() && mMachine && tick % 20 == 0) {
            scanInputBuses();
            refreshBreedingPlan();
            if (displayDirty) {
                syncedArchiveSize = cropArchive.size();
                syncedArchiveCrops = new ArrayList<>(cropArchive.getAvailableCropIds());
                Collections.sort(syncedArchiveCrops);
                syncedChainSteps = buildStructuredChainSteps();
                syncedMissingInfo = allTasksBlocked && missingCropId != null ? missingCropId : "";
                displayDirty = false;
            }
        }
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        activeOperation = OPERATION_NONE;
        activeCropId = "";
        missingBreedingRequirements = false;
        scanInputBuses();

        ICropCard targetCrop = getTargetCrop();
        if (targetCropId != null && !targetCropId.isEmpty() && targetCrop == null) {
            targetInputValid = false;
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (targetCrop == null) {
            refreshBreedingPlan();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        refreshBreedingPlan();
        if (pendingSeedOutputs <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (cropArchive.hasCrop(targetCrop.getId())) {
            ItemStack output = cropArchive.createSeed(targetCrop.getId());
            if (output == null) {
                allTasksBlocked = true;
                missingCropId = targetCrop.getId();
                missingBreedingRequirements = false;
                markDisplayDirty();
                // #tr GT5U.gui.text.recipe_result.CropBreeder_unreachable_target
                // # Current crop archive cannot create the target seed!
                // # zh_CN 当前作物档案无法生成目标种子！
                return SimpleCheckRecipeResult.ofFailure("CropBreeder_unreachable_target");
            }
            if (!canOutputAll(new ItemStack[] { output })) {
                return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
            }
            allTasksBlocked = false;
            missingCropId = "";
            missingBreedingRequirements = false;
            mOutputItems = new ItemStack[] { output };
            activeOperation = OPERATION_OUTPUT;
            prepareArchiveCycle(20);
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        if (machineMode == MODE_MUTATION_POOL) {
            CheckRecipeResult poolResult = tryPrepareArchivedPoolMutation(targetCrop);
            if (poolResult != null) return poolResult;
        } else {
            List<CropBreedingPlanner.Step> readySteps = breedingPlan
                .getReadySteps(cropArchive.getAvailableCropIds(), MAX_ARCHIVE_STEPS_PER_CYCLE);
            if (!readySteps.isEmpty()) {
                CheckRecipeResult stepResult = tryPrepareArchivedDeterministicMutation(readySteps.get(0));
                if (stepResult != null) return stepResult;
                allTasksBlocked = true;
                missingCropId = "";
                missingBreedingRequirements = true;
                markDisplayDirty();
                // #tr GT5U.gui.text.recipe_result.CropBreeder_missing_requirements
                // # Missing the catalyst or condition required by the next crop mutation!
                // # zh_CN 缺少下一步作物杂交所需的催化物或条件！
                return SimpleCheckRecipeResult.ofFailure("CropBreeder_missing_requirements");
            }

            CheckRecipeResult poolFallbackResult = tryPrepareArchivedPoolFallback(targetCrop);
            if (poolFallbackResult != null) return poolFallbackResult;
        }

        allTasksBlocked = true;
        missingCropId = breedingPlan.getFirstMissingCrop();
        if (missingCropId.isEmpty()) missingCropId = targetCrop.getId();
        missingBreedingRequirements = false;
        markDisplayDirty();
        // #tr GT5U.gui.text.recipe_result.CropBreeder_missing_crop
        // # Crop archive is missing a required parent seed!
        // # zh_CN 作物档案中缺少必要亲本种子！
        return SimpleCheckRecipeResult.ofFailure("CropBreeder_missing_crop");
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (mProgresstime >= mMaxProgresstime - 1) {
            settleActiveOperation();
        }
        return super.onRunningTick(stack);
    }

    @Nullable
    private CheckRecipeResult tryPrepareArchivedDeterministicMutation(CropBreedingPlanner.Step step) {
        ICropMutation mutation = step.getMutation();
        long availableVoltage = GTValues.V[Math.min(getTierRecipes(), GTValues.V.length - 1)];
        long mutationEUt = Math.max(1L, mutation.getBreedingMachineRecipeEUt());
        if (mutationEUt > availableVoltage) {
            return CheckRecipeResultRegistry.insufficientVoltage(mutationEUt);
        }

        ItemStack[] catalystSlots = getCatalystSlots();
        ArrayList<ICropCard> parentCards = new ArrayList<>(mutation.getParents());
        int[] catalystConsumption = CropMutationMachineRequirements
            .canBreedIgnoringBlockUnder(mutation, parentCards, getBaseMetaTileEntity(), catalystSlots);
        if (catalystConsumption == null) return null;

        ISeedStats outputStats = cropArchive.averageStats(mutation.getParents());
        if (outputStats == null) return null;

        int duration = Math
            .max(1, (int) (Math.max(1, mutation.getBreedingMachineRecipeDuration()) * getUpgradeSpeedBonus()));
        long steamRequired = mutationEUt * duration;
        if (steamRequired > Integer.MAX_VALUE || !tryConsumeSteam((int) steamRequired)) {
            return CheckRecipeResultRegistry.insufficientPower(steamRequired);
        }

        consumeCatalysts(catalystSlots, catalystConsumption);
        updateSlots();
        prepareArchiveBreeding(
            mutation.getOutput()
                .getId(),
            outputStats,
            duration,
            steamRequired);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Nullable
    private CheckRecipeResult tryPrepareArchivedPoolMutation(ICropCard targetCrop) {
        ArrayList<ICropCard> participatingParents = findArchivedPoolParents(targetCrop);
        if (participatingParents == null) return null;

        ISeedStats outputStats = variedArchivedStats(participatingParents);
        if (outputStats == null) return null;

        int duration = Math.max(1, (int) (POOL_BREEDING_DURATION * getUpgradeSpeedBonus()));
        long steamRequired = (long) POOL_BREEDING_EUT * duration;
        if (!tryConsumeSteam((int) steamRequired)) {
            return CheckRecipeResultRegistry.insufficientPower(steamRequired);
        }

        prepareArchiveBreeding(targetCrop.getId(), outputStats, duration, steamRequired);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Nullable
    private CheckRecipeResult tryPrepareArchivedPoolFallback(ICropCard targetCrop) {
        ICropCard poolTarget = getPoolFallbackTarget(targetCrop);
        return poolTarget == null ? null : tryPrepareArchivedPoolMutation(poolTarget);
    }

    @Nullable
    private ICropCard getPoolFallbackTarget(ICropCard targetCrop) {
        ICropCard missingCrop = getFirstMissingCropCard();
        if (missingCrop != null && hasArchivedPoolMutationForTarget(missingCrop)) return missingCrop;
        return hasArchivedPoolMutationForTarget(targetCrop) ? targetCrop : null;
    }

    @Nullable
    private ICropCard getFirstMissingCropCard() {
        String missing = breedingPlan.getFirstMissingCrop();
        return missing == null || missing.isEmpty() ? null : CropRegistry.instance.get(missing);
    }

    private boolean hasArchivedPoolMutationForTarget(ICropCard targetCrop) {
        return findArchivedPoolParents(targetCrop) != null;
    }

    @Nullable
    private ArrayList<ICropCard> findArchivedPoolParents(ICropCard targetCrop) {
        if (targetCrop == null || targetCrop.getTier() > getTierRecipes()) return null;

        ArrayList<ICropCard> archivedCards = getArchivedCropCards();
        if (archivedCards.size() < 2) return null;

        List<IMutationPool> matchingPools = MutationRegistry.instance.getPossiblePoolMutations(archivedCards);
        if (matchingPools == null || matchingPools.isEmpty()) return null;

        matchingPools.sort(Comparator.comparing(IMutationPool::getUnlocalisedName));
        for (IMutationPool pool : matchingPools) {
            if (!pool.contains(targetCrop)) continue;

            ArrayList<ICropCard> participatingParents = new ArrayList<>();
            for (ICropCard parent : archivedCards) {
                if (pool.contains(parent)) participatingParents.add(parent);
                if (participatingParents.size() >= 4) break;
            }
            if (participatingParents.size() >= 2) return participatingParents;
        }
        return null;
    }

    private void prepareArchiveBreeding(String cropId, ISeedStats stats, int duration, long steamRequired) {
        activeOperation = OPERATION_ARCHIVE_BREEDING;
        activeCropId = cropId;
        activeGrowth = stats.getGrowth();
        activeGain = stats.getGain();
        activeResistance = stats.getResistance();
        mOutputItems = null;
        prepareArchiveCycle(duration);
        totalSteamConsumed = steamRequired;
    }

    private void prepareArchiveCycle(int duration) {
        mMaxProgresstime = Math.max(1, duration);
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = 0;
    }

    private void settleActiveOperation() {
        if (activeOperation == OPERATION_NONE) return;

        boolean changed = false;
        if (activeOperation == OPERATION_ARCHIVE_BREEDING) {
            changed = cropArchive.unlockCrop(activeCropId, activeSeedStats());
            if (changed) invalidateBreedingPlan();
        } else if (activeOperation == OPERATION_OUTPUT && pendingSeedOutputs > 0) {
            pendingSeedOutputs--;
            changed = true;
        }

        activeOperation = OPERATION_NONE;
        activeCropId = "";
        if (changed) {
            markDisplayDirty();
            markBaseTileDirty();
        }
    }

    private ISeedStats activeSeedStats() {
        return new SeedStats((byte) activeGrowth, (byte) activeGain, (byte) activeResistance, true);
    }

    private boolean scanInputBuses() {
        ArrayList<ItemStack> inputs = getStoredInputs();
        boolean stateChanged = false;
        for (ItemStack stack : inputs) {
            ISeedData seedData = CropsNHUtils.getAnalyzedSeedData(stack);
            if (seedData == null) continue;

            cropArchive.addSeed(stack);
            long pending = (long) pendingSeedOutputs + Math.max(1, stack.stackSize);
            pendingSeedOutputs = (int) Math.min(Integer.MAX_VALUE, pending);
            stack.stackSize = 0;
            stateChanged = true;
        }

        if (stateChanged) {
            updateSlots();
            invalidateBreedingPlan();
            markBaseTileDirty();
        }
        return stateChanged;
    }

    private void refreshBreedingPlan() {
        boolean previousBlocked = allTasksBlocked;
        String previousMissing = missingCropId;
        boolean previousMissingRequirements = missingBreedingRequirements;
        int previousCompleted = chainCompletedSteps;

        if (targetCropId == null || targetCropId.isEmpty()) {
            breedingPlan = CropBreedingPlanner.Plan.empty("");
            breedingChain = new ArrayList<>();
            breedingPlanDirty = false;
            allTasksBlocked = false;
            missingCropId = "";
            missingBreedingRequirements = false;
        } else {
            if (breedingPlanDirty) {
                breedingPlan = CropBreedingPlanner.plan(targetCropId, cropArchive.getAvailableCropIds());
                breedingChain = new ArrayList<>(breedingPlan.getSteps());
                breedingPlanDirty = false;
            }

            boolean targetUnlocked = cropArchive.hasCrop(targetCropId);
            boolean hasReadyStep = !breedingPlan.getReadySteps(cropArchive.getAvailableCropIds(), 1)
                .isEmpty();
            ICropCard targetCrop = CropRegistry.instance.get(targetCropId);
            boolean hasReadyPoolFallback = !targetUnlocked && getPoolFallbackTarget(targetCrop) != null;
            allTasksBlocked = pendingSeedOutputs > 0 && !targetUnlocked && !hasReadyStep && !hasReadyPoolFallback;
            missingCropId = allTasksBlocked ? breedingPlan.getFirstMissingCrop() : "";
            if (allTasksBlocked && missingCropId.isEmpty()) missingCropId = targetCropId;
            missingBreedingRequirements = false;
        }

        updateChainDisplayInfo();
        if (previousBlocked != allTasksBlocked || !java.util.Objects.equals(previousMissing, missingCropId)
            || previousMissingRequirements != missingBreedingRequirements
            || previousCompleted != chainCompletedSteps) {
            markDisplayDirty();
        }
    }

    private void updateChainDisplayInfo() {
        chainTotalSteps = breedingChain.size();
        chainCompletedSteps = countCompletedSteps();
    }

    private int countCompletedSteps() {
        int count = 0;
        for (CropBreedingPlanner.Step step : breedingChain) {
            if (cropArchive.hasCrop(step.result)) count++;
        }
        return count;
    }

    private List<NBTTagCompound> buildStructuredChainSteps() {
        if (breedingChain.isEmpty()) return Collections.emptyList();
        List<NBTTagCompound> result = new ArrayList<>(breedingChain.size());
        for (CropBreedingPlanner.Step step : breedingChain) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("result", step.result);
            tag.setInteger("parentCount", step.parents.size());
            for (int i = 0; i < step.parents.size(); i++) {
                tag.setString("parent" + i, step.parents.get(i));
            }
            tag.setInteger("duration", step.duration);
            tag.setInteger("eut", step.eut);
            if (cropArchive.hasCrop(step.result)) {
                tag.setByte("status", (byte) 2);
            } else if (cropArchive.getAvailableCropIds()
                .containsAll(step.parents)) {
                    tag.setByte("status", (byte) 1);
                } else {
                    tag.setByte("status", (byte) 0);
                }
            result.add(tag);
        }
        return result;
    }

    private ArrayList<ICropCard> getArchivedCropCards() {
        ArrayList<ICropCard> crops = new ArrayList<>();
        for (String cropId : cropArchive.getAvailableCropIds()) {
            ICropCard crop = CropRegistry.instance.get(cropId);
            if (crop != null) crops.add(crop);
        }
        crops.sort(Comparator.comparing(ICropCard::getId));
        return crops;
    }

    private ItemStack[] getCatalystSlots() {
        ArrayList<ItemStack> inputs = getStoredInputs();
        ArrayList<ItemStack> catalysts = new ArrayList<>();
        for (ItemStack stack : inputs) {
            if (CropsNHUtils.getAnalyzedSeedData(stack) == null) catalysts.add(stack);
        }
        return catalysts.toArray(new ItemStack[0]);
    }

    private static void consumeCatalysts(ItemStack[] catalystSlots, int[] catalystConsumption) {
        for (int i = 0; i < catalystConsumption.length; i++) {
            if (catalystConsumption[i] <= 0 || catalystSlots[i] == null) continue;
            catalystSlots[i].stackSize -= catalystConsumption[i];
        }
    }

    @Nullable
    private ISeedStats variedArchivedStats(Collection<ICropCard> requiredParents) {
        ArrayList<ISeedStats> parentStats = new ArrayList<>();
        for (ICropCard parent : requiredParents) {
            ISeedStats stats = cropArchive.getStats(parent.getId());
            if (stats == null) return null;
            parentStats.add(stats);
        }
        if (parentStats.size() < 2) return null;
        return new SeedStats(
            TileEntityCropSticks.variateStat(false, parentStats, ISeedStats::getGrowth),
            TileEntityCropSticks.variateStat(false, parentStats, ISeedStats::getGain),
            TileEntityCropSticks.variateStat(false, parentStats, ISeedStats::getResistance),
            true);
    }

    @Nullable
    private CheckRecipeResult tryProcessPoolMutation(List<ICropCard> parentCards, Map<ICropCard, SeedInput> parentSeeds,
        @Nullable ICropCard targetCrop) {
        List<IMutationPool> matchingPools = MutationRegistry.instance.getPossiblePoolMutations(parentCards);
        if (matchingPools == null || matchingPools.isEmpty()) return null;

        int maximumCropTier = getTierRecipes();
        Map<IMutationPool, List<ICropCard>> eligiblePools = new LinkedHashMap<>();
        for (IMutationPool pool : matchingPools) {
            ArrayList<ICropCard> candidates = new ArrayList<>();
            if (targetCrop != null) {
                if (pool.contains(targetCrop) && targetCrop.getTier() <= maximumCropTier) candidates.add(targetCrop);
            } else {
                for (ICropCard member : pool.getMembers()) {
                    if (member.getTier() <= maximumCropTier) candidates.add(member);
                }
            }
            if (!candidates.isEmpty()) eligiblePools.put(pool, candidates);
        }
        if (eligiblePools.isEmpty()) return null;

        ArrayList<IMutationPool> poolChoices = new ArrayList<>(eligiblePools.keySet());
        poolChoices.sort(Comparator.comparing(IMutationPool::getUnlocalisedName));
        IMutationPool chosenPool = targetCrop == null
            ? poolChoices.get(getBaseMetaTileEntity().getRandomNumber(poolChoices.size()))
            : poolChoices.get(0);
        List<ICropCard> outputChoices = eligiblePools.get(chosenPool);
        ICropCard outputCrop = targetCrop == null
            ? outputChoices.get(getBaseMetaTileEntity().getRandomNumber(outputChoices.size()))
            : targetCrop;

        ArrayList<ICropCard> participatingParents = new ArrayList<>();
        for (ICropCard parent : parentCards) {
            if (chosenPool.contains(parent)) participatingParents.add(parent);
        }
        ISeedStats outputStats = variedParentStats(participatingParents, parentSeeds);
        if (outputStats == null) return null;

        ItemStack output = outputCrop.getSeedItem(outputStats);
        if (output == null) return null;
        output.stackSize = 1;
        if (!canOutputAll(new ItemStack[] { output })) {
            return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
        }

        int duration = Math.max(1, (int) (POOL_BREEDING_DURATION * getUpgradeSpeedBonus()));
        long steamRequired = (long) POOL_BREEDING_EUT * duration;
        if (!tryConsumeSteam((int) steamRequired)) {
            return CheckRecipeResultRegistry.insufficientPower(steamRequired);
        }

        consumeParentSeeds(participatingParents, parentSeeds);
        updateSlots();
        finishRecipe(output, duration, steamRequired);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Nullable
    private ICropCard getTargetCrop() {
        if (targetCropId == null || targetCropId.isEmpty()) return null;
        return CropRegistry.instance.get(targetCropId);
    }

    private static boolean isSameCrop(ICropCard left, ICropCard right) {
        if (left == right) return true;
        if (left == null || right == null) return false;
        return left.getId()
            .equals(right.getId());
    }

    @Nullable
    private static ICropCard resolveCropInput(String input) {
        String candidate = input == null ? "" : input.trim();
        if (candidate.isEmpty()) return null;

        ICropCard exact = CropRegistry.instance.get(candidate);
        if (exact != null) return exact;

        String normalized = candidate.toLowerCase(java.util.Locale.ROOT);
        for (ICropCard crop : CropRegistry.instance.getAllInRegistrationOrder()) {
            if (crop == null) continue;
            if (crop.getId()
                .equalsIgnoreCase(candidate)) {
                return crop;
            }
            String unlocalizedName = crop.getUnlocalizedName();
            if (unlocalizedName != null && unlocalizedName.toLowerCase(java.util.Locale.ROOT)
                .contains(normalized)) {
                return crop;
            }
            String localizedName = getCropDisplayName(crop);
            if (localizedName != null && localizedName.toLowerCase(java.util.Locale.ROOT)
                .contains(normalized)) {
                return crop;
            }
        }
        return null;
    }

    public String getTargetCropId() {
        return targetCropId == null ? "" : targetCropId;
    }

    public void setTargetCropId(String cropInput) {
        String candidate = cropInput == null ? "" : cropInput.trim();
        if (candidate.isEmpty()) {
            targetInputValid = true;
            if (targetCropId != null && !targetCropId.isEmpty()) {
                targetCropId = "";
                invalidateBreedingPlan();
                markBaseTileDirty();
            }
            return;
        }

        ICropCard resolved = resolveCropInput(candidate);
        targetInputValid = resolved != null;
        if (resolved == null) return;

        String resolvedId = resolved.getId();
        if (!resolvedId.equals(targetCropId)) {
            targetCropId = resolvedId;
            invalidateBreedingPlan();
            markBaseTileDirty();
        }
    }

    public boolean isTargetInputValid() {
        return targetInputValid;
    }

    public int getSyncedArchiveSize() {
        return syncedArchiveSize;
    }

    public int getPendingSeedOutputs() {
        return pendingSeedOutputs;
    }

    public int getChainTotalSteps() {
        return chainTotalSteps;
    }

    public int getChainCompletedSteps() {
        return chainCompletedSteps;
    }

    public boolean isAllTasksBlocked() {
        return allTasksBlocked;
    }

    public String getSyncedMissingInfo() {
        return allTasksBlocked && missingCropId != null ? missingCropId : "";
    }

    public boolean isMissingBreedingRequirements() {
        return allTasksBlocked && missingBreedingRequirements;
    }

    public List<String> getSyncedArchiveCrops() {
        return syncedArchiveCrops;
    }

    public void setSyncedArchiveCrops(List<String> crops) {
        syncedArchiveCrops = crops == null ? Collections.emptyList() : crops;
    }

    public List<NBTTagCompound> getSyncedChainSteps() {
        return syncedChainSteps;
    }

    public void setSyncedChainSteps(List<NBTTagCompound> steps) {
        syncedChainSteps = steps == null ? Collections.emptyList() : steps;
    }

    public static String getCropDisplayName(String cropId) {
        ICropCard crop = CropRegistry.instance.get(cropId);
        return crop == null ? cropId : getCropDisplayName(crop);
    }

    public static String getCropDisplayName(ICropCard crop) {
        if (crop == null) return "";
        String localized = StatCollector.translateToLocal(crop.getUnlocalizedName());
        return localized == null || localized.isEmpty() ? crop.getId() : localized;
    }

    private void markBaseTileDirty() {
        IGregTechTileEntity baseTile = getBaseMetaTileEntity();
        if (baseTile != null) baseTile.markDirty();
    }

    private void finishRecipe(ItemStack output, int duration, long steamRequired) {
        mOutputItems = new ItemStack[] { output };
        mMaxProgresstime = duration;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = 0;
        totalSteamConsumed = steamRequired;
    }

    @Nullable
    private static ISeedStats averageParentStats(Collection<ICropCard> requiredParents,
        Map<ICropCard, SeedInput> parentSeeds) {
        int growth = 0;
        int gain = 0;
        int resistance = 0;
        int count = 0;
        for (ICropCard parent : requiredParents) {
            SeedInput input = parentSeeds.get(parent);
            if (input == null) return null;
            ISeedStats stats = input.seedData.getStats();
            growth += stats.getGrowth();
            gain += stats.getGain();
            resistance += stats.getResistance();
            count++;
        }
        if (count == 0) return null;
        return new SeedStats((byte) (growth / count), (byte) (gain / count), (byte) (resistance / count), true);
    }

    @Nullable
    private static ISeedStats variedParentStats(Collection<ICropCard> requiredParents,
        Map<ICropCard, SeedInput> parentSeeds) {
        ArrayList<ISeedStats> parentStats = new ArrayList<>();
        for (ICropCard parent : requiredParents) {
            SeedInput input = parentSeeds.get(parent);
            if (input == null) return null;
            parentStats.add(input.seedData.getStats());
        }
        if (parentStats.size() < 2) return null;
        return new SeedStats(
            TileEntityCropSticks.variateStat(false, parentStats, ISeedStats::getGrowth),
            TileEntityCropSticks.variateStat(false, parentStats, ISeedStats::getGain),
            TileEntityCropSticks.variateStat(false, parentStats, ISeedStats::getResistance),
            true);
    }

    private static void consumeInputs(Collection<ICropCard> requiredParents, Map<ICropCard, SeedInput> parentSeeds,
        ItemStack[] catalystSlots, int[] catalystConsumption) {
        consumeParentSeeds(requiredParents, parentSeeds);
        for (int i = 0; i < catalystConsumption.length; i++) {
            if (catalystConsumption[i] <= 0 || catalystSlots[i] == null) continue;
            catalystSlots[i].stackSize -= catalystConsumption[i];
        }
    }

    private static void consumeParentSeeds(Collection<ICropCard> requiredParents,
        Map<ICropCard, SeedInput> parentSeeds) {
        for (ICropCard parent : requiredParents) {
            SeedInput input = parentSeeds.get(parent);
            if (input != null) input.stack.stackSize--;
        }
    }

    private static final class SeedInput {

        private final ISeedData seedData;
        private final ItemStack stack;

        private SeedInput(ISeedData seedData, ItemStack stack) {
            this.seedData = seedData;
            this.stack = stack;
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public int getGUIHeight() {
        return 192;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setString(NBT_TARGET_CROP_ID, getTargetCropId());
        aNBT.setInteger("pendingSeedOutputs", pendingSeedOutputs);
        aNBT.setTag("cropArchive", cropArchive.toNBT());
        aNBT.setByte("cropBreederActiveOperation", activeOperation);
        aNBT.setString("cropBreederActiveCrop", activeCropId == null ? "" : activeCropId);
        aNBT.setInteger("cropBreederActiveGrowth", activeGrowth);
        aNBT.setInteger("cropBreederActiveGain", activeGain);
        aNBT.setInteger("cropBreederActiveResistance", activeResistance);
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        aNBT.setString(NBT_TARGET_CROP_ID, getTargetCropId());
        aNBT.setInteger("pendingSeedOutputs", pendingSeedOutputs);
        aNBT.setTag("cropArchive", cropArchive.toNBT());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        targetCropId = aNBT.getString(NBT_TARGET_CROP_ID);
        pendingSeedOutputs = aNBT.getInteger("pendingSeedOutputs");
        cropArchive = aNBT.hasKey("cropArchive") ? CropArchive.fromNBT(aNBT.getCompoundTag("cropArchive"))
            : new CropArchive();
        activeOperation = aNBT.getByte("cropBreederActiveOperation");
        activeCropId = aNBT.getString("cropBreederActiveCrop");
        activeGrowth = aNBT.getInteger("cropBreederActiveGrowth");
        activeGain = aNBT.getInteger("cropBreederActiveGain");
        activeResistance = aNBT.getInteger("cropBreederActiveResistance");
        if (activeOperation != OPERATION_ARCHIVE_BREEDING && activeOperation != OPERATION_OUTPUT) {
            activeOperation = OPERATION_NONE;
            activeCropId = "";
        }
        breedingPlan = CropBreedingPlanner.Plan.empty(targetCropId);
        breedingPlanDirty = true;
        breedingChain.clear();
        markDisplayDirty();
        targetInputValid = targetCropId == null || targetCropId.isEmpty()
            || CropRegistry.instance.get(targetCropId) != null;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(TextLocalization.LargeSteamCropBreederMachineType)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_00)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_01)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_02)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_03)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_04)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_05)
            .addInfo(TextLocalization.Tooltip_LargeSteamCropBreeder_06)
            .addInfo(TextLocalization.Tooltip_GTNC_SteamTierInfo)
            .addInfo(TextLocalization.Tooltip_GTNC_SteamGearInfo)
            .addInfo(TextLocalization.Tooltip_GTNC_SteamWirelessMode)
            .beginStructureBlock(5, 4, 3, false)
            .addController(TextLocalization.Tooltip_LargeSteamCropBreeder_Controller)
            .addOtherStructurePart(
                TextLocalization.Tooltip_LargeSteamCropBreeder_BorosilicateGlass,
                TextLocalization.Tooltip_LargeSteamCropBreeder_GreenhouseWalls,
                2)
            .addSteamInputBus(TextLocalization.Tooltip_LargeSteamCropBreeder_Casing, 1)
            .addSteamOutputBus(TextLocalization.Tooltip_LargeSteamCropBreeder_Casing, 1)
            .addInputBus(TextLocalization.Tooltip_LargeSteamCropBreeder_Casing, 1)
            .addOutputBus(TextLocalization.Tooltip_LargeSteamCropBreeder_Casing, 1)
            .toolTipFinisher();
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GTCEU_LOOP_REPLICATOR;
    }
}
