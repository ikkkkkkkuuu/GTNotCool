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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.cropsnh.api.ICropCard;
import com.gtnewhorizon.cropsnh.api.ICropMutation;
import com.gtnewhorizon.cropsnh.api.IMutationPool;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.api.ISeedStats;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.farming.registries.MutationRegistry;
import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.LargeSteamCropBreederGui;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCAdvancedSteamMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
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
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
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

    // 5 wide (x), 4 tall (y), 3 deep (z), following the CropsNH Industrial Farm greenhouse silhouette.
    // A = glass, B = tiered casing and hatches, C = pipe casing/seed bed, D = frame.
    private static final String[][] SHAPE = new String[][] { { " DBD ", " ACA ", " DBD " },
        { "DBBBD", "A   A", "DBBBD" }, { "DB~BD", "DCCCD", "DBBBD" }, { "D   D", "     ", "D   D" } };

    private int casingCount;
    private int machineCasingTier = -1;
    private int pipeCasingTier = -1;
    private int frameTier = -1;
    private IStructureDefinition<LargeSteamCropBreeder> structureDefinition;

    public LargeSteamCropBreeder(String name) {
        super(name);
    }

    public LargeSteamCropBreeder(int id, String name, String regionalName) {
        super(id, name, regionalName);
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
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        ArrayList<ItemStack> inputs = getStoredInputs();
        if (inputs.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;

        Map<ICropCard, SeedInput> parentSeeds = new LinkedHashMap<>();
        ItemStack[] catalystSlots = new ItemStack[inputs.size()];
        for (int i = 0; i < inputs.size(); i++) {
            ItemStack stack = inputs.get(i);
            ISeedData seedData = CropsNHUtils.getAnalyzedSeedData(stack);
            if (seedData == null) {
                catalystSlots[i] = stack;
                continue;
            }
            parentSeeds.putIfAbsent(seedData.getCrop(), new SeedInput(seedData, stack));
        }
        if (parentSeeds.size() < 2 || parentSeeds.size() > 4) return CheckRecipeResultRegistry.NO_RECIPE;

        ArrayList<ICropCard> parentCards = new ArrayList<>(parentSeeds.keySet());
        if (machineMode == MODE_MUTATION_POOL) {
            CheckRecipeResult poolResult = tryProcessPoolMutation(parentCards, parentSeeds);
            return poolResult == null ? CheckRecipeResultRegistry.NO_RECIPE : poolResult;
        }

        List<ICropMutation> mutations = MutationRegistry.instance.getPossibleDeterministicMutations(parentCards);
        long availableVoltage = GTValues.V[Math.min(getTierRecipes(), GTValues.V.length - 1)];
        long minimumRequiredVoltage = Long.MAX_VALUE;
        boolean hasAffordableMutation = false;
        if (mutations != null && !mutations.isEmpty()) {
            mutations.sort(
                Comparator.comparingInt(ICropMutation::getParentCount)
                    .reversed());
            for (ICropMutation mutation : mutations) {
                if (mutation.getBreedingMachineRecipeEUt() > availableVoltage) {
                    minimumRequiredVoltage = Math.min(minimumRequiredVoltage, mutation.getBreedingMachineRecipeEUt());
                    continue;
                }
                hasAffordableMutation = true;

                int[] catalystConsumption = mutation.canBreed(parentCards, getBaseMetaTileEntity(), catalystSlots);
                if (catalystConsumption == null) continue;

                ISeedStats outputStats = averageParentStats(mutation.getParents(), parentSeeds);
                if (outputStats == null) continue;

                ItemStack output = mutation.getOutput()
                    .getSeedItem(outputStats);
                if (output == null) continue;
                output.stackSize = 1;
                if (!canOutputAll(new ItemStack[] { output })) {
                    return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                }

                int duration = Math
                    .max(1, (int) (Math.max(1, mutation.getBreedingMachineRecipeDuration()) * getUpgradeSpeedBonus()));
                long steamRequired = Math.max(1L, Math.abs((long) mutation.getBreedingMachineRecipeEUt())) * duration;
                if (steamRequired > Integer.MAX_VALUE || !tryConsumeSteam((int) steamRequired)) {
                    return CheckRecipeResultRegistry.insufficientPower(steamRequired);
                }

                consumeInputs(mutation.getParents(), parentSeeds, catalystSlots, catalystConsumption);
                updateSlots();

                finishRecipe(output, duration, steamRequired);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        if (!hasAffordableMutation && minimumRequiredVoltage < Long.MAX_VALUE) {
            return CheckRecipeResultRegistry.insufficientVoltage(minimumRequiredVoltage);
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Nullable
    private CheckRecipeResult tryProcessPoolMutation(List<ICropCard> parentCards,
        Map<ICropCard, SeedInput> parentSeeds) {
        List<IMutationPool> matchingPools = MutationRegistry.instance.getPossiblePoolMutations(parentCards);
        if (matchingPools == null || matchingPools.isEmpty()) return null;

        int maximumCropTier = getTierRecipes();
        Map<IMutationPool, List<ICropCard>> eligiblePools = new LinkedHashMap<>();
        for (IMutationPool pool : matchingPools) {
            ArrayList<ICropCard> candidates = new ArrayList<>();
            for (ICropCard member : pool.getMembers()) {
                if (member.getTier() <= maximumCropTier) candidates.add(member);
            }
            if (!candidates.isEmpty()) eligiblePools.put(pool, candidates);
        }
        if (eligiblePools.isEmpty()) return null;

        ArrayList<IMutationPool> poolChoices = new ArrayList<>(eligiblePools.keySet());
        IMutationPool chosenPool = poolChoices.get(getBaseMetaTileEntity().getRandomNumber(poolChoices.size()));
        List<ICropCard> outputChoices = eligiblePools.get(chosenPool);
        ICropCard outputCrop = outputChoices.get(getBaseMetaTileEntity().getRandomNumber(outputChoices.size()));

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
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.LargeSteamCropBreederRecipes;
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
