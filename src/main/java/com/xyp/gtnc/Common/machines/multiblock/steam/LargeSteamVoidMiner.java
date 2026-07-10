package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockFrames;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.getCasingTextureForId;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachine;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachineActive;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.LargeSteamVoidMinerGui;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import bwcrossmod.galacticgreg.VoidMinerUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

// #tr NameLargeSteamVoidMiner
// # Large Steam Void Miner
// # zh_CN 大型蒸汽虚空采矿场

// #tr LargeSteamVoidMinerRecipeType
// # Void Miner
// # zh_CN 虚空采矿场

// #tr Tooltip_LargeSteamVoidMiner_00
// # A steam-powered void miner that extracts ores from the void
// # zh_CN 一台从虚空中提取矿石的蒸汽虚空采矿场

// #tr Tooltip_LargeSteamVoidMiner_01
// # Ores selected in the Controller UI or added to an Input Bus are added to the Whitelist/Blacklist
// # zh_CN 在控制器UI中选择或添加到输入总线的矿石将加入白名单/黑名单

// #tr Tooltip_LargeSteamVoidMiner_02
// # Use the Controller UI or a screwdriver to toggle Whitelist/Blacklist
// # zh_CN 使用控制器UI或螺丝刀切换白名单/黑名单

// #tr Tooltip_LargeSteamVoidMiner_03
// # Blacklisted or non Whitelisted Ore will be VOIDED
// # zh_CN 黑名单内或不在白名单内的矿石将被销毁

// #tr Tooltip_LargeSteamVoidMiner_Tier
// # Bronze: 6,400 L/cycle, 1 ore/s | Steel: 12,800 L/cycle, 2 ore/s
// # zh_CN 青铜：6,400 L/周期，1 矿石/秒 | 钢：12,800 L/周期，2 矿石/秒

// #tr Tooltip_LargeSteamVoidMiner_Output
// # Ore output depends on the dimension the machine is built in
// # zh_CN 矿石产出取决于机器所在维度

// #tr Tooltip_LargeSteamVoidMiner_Wireless
// # Right-click front face with Screwdriver to toggle Wireless Steam Mode
// # zh_CN GUI内切换无线蒸汽模式

// #tr GT5U.chat.steam_void_miner.mode.white_list
// # Whitelist Mode: Only selected ores will be kept
// # zh_CN 白名单模式：仅保留选中的矿石

// #tr GT5U.chat.steam_void_miner.mode.black_list
// # Blacklist Mode: Selected ores will be voided
// # zh_CN 黑名单模式：选中的矿石将被销毁

public class LargeSteamVoidMiner extends GTNCSteamMultiBlockBase<LargeSteamVoidMiner>
    implements ISurvivalConstructable {

    // Same shape as VMLUV (7x9x7) with tiered steam blocks
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 7;
    private static final int DEPTH_OFF_SET = 1;

    // Tier-keyed operating values (index by tierMachine: 1 = Bronze, 2 = Steel).
    // Steam consumed per tick and ores produced per 20-tick cycle.
    private static final long STEAM_PER_TICK_BRONZE = 320L;
    private static final long STEAM_PER_TICK_STEEL = 640L;
    private static final int ORE_PER_CYCLE_BRONZE = 1;
    private static final int ORE_PER_CYCLE_STEEL = 2;
    private static final int CYCLE_TICKS = 20;

    // 'B' = tiered casing + hatches, 'D' = frame, 'C' = tiered casing center
    private static final String[][] shape = new String[][] {
        { "       ", "       ", "       ", "   B   ", "       ", "       ", "       " },
        { "       ", "       ", "       ", "   B   ", "       ", "       ", "       " },
        { "       ", "       ", "       ", "   B   ", "       ", "       ", "       " },
        { "       ", "       ", "   B   ", "  BCB  ", "   B   ", "       ", "       " },
        { "       ", "       ", "   B   ", "  BCB  ", "   B   ", "       ", "       " },
        { "       ", "       ", "   B   ", "  BCB  ", "   B   ", "       ", "       " },
        { "       ", " B   B ", "  DAD  ", "  ACA  ", "  DAD  ", " B   B ", "       " },
        { "  D D  ", " BA~AB ", " A   A ", " B C B ", " A   A ", " BABAB ", "       " },
        { "  E E  ", " BBBBB ", "EB   BE", " B C B ", "EB   BE", " BBBBB ", "  E E  " } };

    private int mCountCasing = 0;
    private int tierMachineCasing = -1;
    private int tierFrame = -1;
    private int tierCenterCasing = -1;
    private IStructureDefinition<LargeSteamVoidMiner> STRUCTURE_DEFINITION = null;

    // ---- Void Miner specific fields ----
    private boolean canVoidMine = true;
    public VoidMinerUtility.DropMap dropMap = null;
    public VoidMinerUtility.DropMap extraDropMap = null;
    private float totalWeight;
    public ItemStackHandler selected = new ItemStackHandler();
    public boolean blacklist = false;
    /** Override dimension for drop map, null = use world dimension */
    public String targetDimName = null;
    /** Persisted dimension block item, survives GUI close/chunk reload */
    public ItemStack dimensionBlock = null;

    public LargeSteamVoidMiner(String aName) {
        super(aName);
    }

    public LargeSteamVoidMiner(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamVoidMiner(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamVoidMinerRecipeType");
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return oMCDIndustrialCuttingMachine;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return oMCDIndustrialCuttingMachineActive;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int casingId = getCasingTextureId();
        if (side == aFacing) {
            if (aActive) return new ITexture[] { getCasingTextureForId(casingId), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ORE_DRILL_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ORE_DRILL_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { getCasingTextureForId(casingId), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ORE_DRILL)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ORE_DRILL_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { getCasingTextureForId(casingId) };
    }

    @Override
    public IStructureDefinition<LargeSteamVoidMiner> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamVoidMiner>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    ofBlocksTiered(
                        LargeSteamVoidMiner::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierCenterCasing = m,
                        t -> t.tierCenterCasing))
                .addElement(
                    'B',
                    ofChain(
                        buildSteamInput(LargeSteamVoidMiner.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamVoidMiner.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(),
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofBlocksTiered(
                                this::getTierMachineCasing,
                                ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                -1,
                                (t, m) -> t.tierMachineCasing = m,
                                t -> t.tierMachineCasing))))
                .addElement(
                    'C',
                    ofChain(
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofBlocksTiered(
                                this::getTierCenterBlockCasing,
                                ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                -1,
                                (t, m) -> t.tierCenterCasing = m,
                                t -> t.tierCenterCasing))))
                .addElement(
                    'D',
                    ofBlocksTiered(
                        LargeSteamVoidMiner::getTierFrame,
                        ImmutableList.of(
                            Pair.of(sBlockFrames, Materials.Bronze.mMetaItemSubID),
                            Pair.of(sBlockFrames, Materials.Steel.mMetaItemSubID)),
                        -1,
                        (t, m) -> t.tierFrame = m,
                        t -> t.tierFrame))
                .addElement(
                    'E',
                    ofChain(
                        buildSteamInput(LargeSteamVoidMiner.class).casingIndex(10)
                            .hint(2)
                            .build(),
                        buildHatchAdder(LargeSteamVoidMiner.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch)
                            .casingIndex(10)
                            .hint(2)
                            .buildAndChain(),
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofBlocksTiered(
                                this::getTierMachineCasing,
                                ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                -1,
                                (t, m) -> t.tierMachineCasing = m,
                                t -> t.tierMachineCasing))))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Nullable
    public Integer getTierMachineCasing(Block block, int meta) {
        if (block == sBlockCasings1 && 10 == meta) return 1;
        if (block == sBlockCasings2 && 0 == meta) return 2;
        return null;
    }

    @Nullable
    public Integer getTierCenterBlockCasing(Block block, int meta) {
        return getTierMachineCasing(block, meta);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
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
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mCountCasing = 0;
        tierMachineCasing = -1;
        tierFrame = -1;
        tierCenterCasing = -1;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;

        if (tierMachineCasing >= 1 && tierMachineCasing == tierFrame && tierMachineCasing == tierCenterCasing) {
            tierMachine = tierMachineCasing;
            syncTierValue = tierMachineCasing;
            updateHatchTexture();
        } else {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }
        checkCasingMin(errors, mCountCasing, 26);
        enableHigherRecipe = getUpgradeTier(getControllerSlot());
    }

    // ============================================================
    // Void Miner logic (adapted from MTEVoidMinerBase)
    // ============================================================

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        calculateDropMap();
        if (this.dropMap == null) return;
        int size = dropMap.getOres().length;
        if (selected.getSlots() != size) selected.setSize(size);
    }

    private void calculateDropMap() {
        this.dropMap = null;
        this.extraDropMap = null;
        this.totalWeight = 0;
        this.canVoidMine = false;

        String dimName;
        if (targetDimName != null && !targetDimName.isEmpty()) {
            dimName = targetDimName;
        } else {
            // World is not attached yet during NBT load (e.g. WorldEdit paste
            // creates the tile before setWorldObj). getDefForWorld would NPE on
            // world.provider — skip; onFirstTick recomputes once the world exists.
            IGregTechTileEntity mte = getBaseMetaTileEntity();
            World world = mte != null ? mte.getWorld() : null;
            if (world == null) return;
            ModDimensionDef worldDef = DimensionDef.getDefForWorld(world);
            dimName = worldDef != null ? worldDef.getDimensionName() : null;
        }
        if (dimName == null) return;

        ModDimensionDef dimensionDef = DimensionDef.getDefByName(dimName);
        if (dimensionDef == null || !dimensionDef.canBeVoidMined()) return;

        this.canVoidMine = true;
        this.dropMap = VoidMinerUtility.dropMapsByDimName.getOrDefault(dimName, new VoidMinerUtility.DropMap());
        this.extraDropMap = VoidMinerUtility.extraDropsByDimName.getOrDefault(dimName, new VoidMinerUtility.DropMap());
        this.dropMap.isDistributionCached(this.extraDropMap);
        this.totalWeight = dropMap.getTotalWeight() + extraDropMap.getTotalWeight();
    }

    /** Called by GUI when a dimension block is inserted */
    public void recalculateDropMap() {
        calculateDropMap();
        if (this.dropMap != null) {
            selected.setSize(dropMap.getOres().length);
        }
    }

    /**
     * Recalculate dropMap without modifying selected filter.
     * Bypasses DimensionDef checks that may fail on the client.
     */
    public void refreshDropMap() {
        String dimName = this.targetDimName;
        if (dimName == null || dimName.isEmpty()) {
            calculateDropMap();
            return;
        }
        this.dropMap = VoidMinerUtility.dropMapsByDimName.get(dimName);
        this.extraDropMap = VoidMinerUtility.extraDropsByDimName.get(dimName);
        if (this.dropMap != null && this.extraDropMap != null) {
            this.dropMap.isDistributionCached(this.extraDropMap);
        }
        this.totalWeight = (this.dropMap != null ? this.dropMap.getTotalWeight() : 0)
            + (this.extraDropMap != null ? this.extraDropMap.getTotalWeight() : 0);
        this.canVoidMine = this.dropMap != null;
    }

    /** Steam consumed per tick for the current machine tier. */
    private long getSteamPerTick() {
        return tierMachine == 2 ? STEAM_PER_TICK_STEEL : STEAM_PER_TICK_BRONZE;
    }

    /** Ores produced per 20-tick cycle for the current machine tier. */
    private int getOrePerCycle() {
        return tierMachine == 2 ? ORE_PER_CYCLE_STEEL : ORE_PER_CYCLE_BRONZE;
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        if (!canVoidMine || totalWeight == 0.f) {
            stopMachine(ShutDownReasonRegistry.NONE);
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        lEUt = -getSteamPerTick();
        mProgresstime = 0;
        mMaxProgresstime = CYCLE_TICKS;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputItems = new ItemStack[0];

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    protected void outputAfterRecipe() {
        // Track per-cycle steam for WAILA
        totalSteamConsumed = getSteamPerTick() * CYCLE_TICKS;
        if (this.canVoidMine && this.totalWeight != 0.f) {
            handleOutputs();
        }
    }

    private void handleOutputs() {
        final ItemStack output = this.dropMap.nextOre()
            .getItemStack();
        output.stackSize = getOrePerCycle();

        final List<ItemStack> inputOres = this.getStoredInputs()
            .stream()
            .filter(GTUtility::isOre)
            .collect(Collectors.toList());

        boolean matchesFilter = contains(selected.getStacks(), output) || contains(inputOres, output);

        if ((isSelectedEmpty() && inputOres.isEmpty()) || (this.blacklist ? !matchesFilter : matchesFilter)) {
            this.addOutputPartial(output);
        }

        this.updateSlots();
    }

    private static boolean contains(List<ItemStack> list, ItemStack stack) {
        for (ItemStack cursor : list) {
            if (GTUtility.areStacksEqual(cursor, stack)) return true;
        }
        return false;
    }

    private boolean isSelectedEmpty() {
        for (ItemStack stack : selected.getStacks()) {
            if (stack != null) return false;
        }
        return true;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (side == getBaseMetaTileEntity().getFrontFacing()) {
            // Use wireless mode toggle from base class (front face)
            super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ, aTool);
        } else {
            // Toggle blacklist/whitelist on other faces
            this.blacklist = !this.blacklist;
            GTUtility.sendChatToPlayer(
                aPlayer,
                this.blacklist ? StatCollector.translateToLocal("GT5U.chat.steam_void_miner.mode.black_list")
                    : StatCollector.translateToLocal("GT5U.chat.steam_void_miner.mode.white_list"));
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1; // Void miner doesn't use recipe parallel
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("tierMachineCasing", tierMachineCasing);
        aNBT.setInteger("tierFrame", tierFrame);
        aNBT.setInteger("tierCenterCasing", tierCenterCasing);
        aNBT.setBoolean("mBlacklist", this.blacklist);
        aNBT.setTag("selected", selected.serializeNBT());
        if (targetDimName != null) {
            aNBT.setString("targetDimName", targetDimName);
        }
        if (dimensionBlock != null) {
            NBTTagCompound dimTag = new NBTTagCompound();
            dimensionBlock.writeToNBT(dimTag);
            aNBT.setTag("dimensionBlock", dimTag);
        }
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachineCasing = aNBT.getInteger("tierMachineCasing");
        tierFrame = aNBT.getInteger("tierFrame");
        tierCenterCasing = aNBT.getInteger("tierCenterCasing");
        this.blacklist = aNBT.getBoolean("mBlacklist");
        this.selected.deserializeNBT(aNBT.getCompoundTag("selected"));
        this.targetDimName = aNBT.hasKey("targetDimName") ? aNBT.getString("targetDimName") : null;
        this.dimensionBlock = aNBT.hasKey("dimensionBlock")
            ? ItemStack.loadItemStackFromNBT(aNBT.getCompoundTag("dimensionBlock"))
            : null;
        // Recalculate dropMap on client after NBT restore
        refreshDropMap();
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamVoidMinerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Tier"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Output"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Wireless"))
            .beginStructureBlock(7, 9, 7, false)
            .addController("Front center, 2nd layer")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Casing"), 1)
            .addInputBus(
                StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Casing") + " (Optional, for ores)",
                1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamVoidMiner_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new LargeSteamVoidMinerGui(this);
    }

    // ============================================================
    // WAILA
    // ============================================================

    // #tr GT5U.tooltip.tier.steel
    // # Steel
    // # zh_CN 钢

    // #tr GT5U.tooltip.tier.bronze
    // # Bronze
    // # zh_CN 青铜
    @Override
    protected boolean showWailaExtraInfo() {
        return false;
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
        tag.setLong("steamConsumed", totalSteamConsumed);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_INDUCTION_LOOP;
    }
}
