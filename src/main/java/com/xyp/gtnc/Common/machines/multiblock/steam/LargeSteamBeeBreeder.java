package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksMap;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.ofOreDictBlockMap;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.LargeSteamBeeBreederGui;
import com.xyp.gtnc.Common.machines.bee.BeeBreedingHelper;
import com.xyp.gtnc.Common.machines.bee.DronePool;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.Utils;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/**
 * 大型蒸汽养蜂机
 * <p>
 * 自动杂交繁育蜜蜂的蒸汽多方块机器。
 * <ul>
 * <li>在控制器GUI中设置目标蜜蜂品种名</li>
 * <li>输入任意基础蜜蜂（公主蜂或雄蜂）→ 品种自动进入雄蜂池</li>
 * <li>机器根据雄蜂池中已有品种，自动计算并逐步走完繁育链到达目标</li>
 * <li>繁育产生的新品种永久加入雄蜂池，下次繁育无需重复</li>
 * <li>每输入一只公主蜂，最终输出一只目标品种的公主蜂</li>
 * <li>每次繁育尝试消耗 51200L 蒸汽，每周期 6.4 秒</li>
 * </ul>
 */
// #tr NameLargeSteamBeeBreeder
// # Large Steam Bee Breeder
// # zh_CN 大型蒸汽养蜂机

// #tr LargeSteamBeeBreederRecipeType
// # Bee Breeder
// # zh_CN 养蜂机

// #tr Tooltip_LargeSteamBeeBreeder_00
// # A large steam-powered automatic bee breeding machine
// # zh_CN 大型蒸汽自动养蜂杂交机

// #tr Tooltip_LargeSteamBeeBreeder_01
// # Input any princess/drone → species enters drone pool permanently
// # zh_CN 投入任意公主蜂/雄蜂 → 品种永久记入雄蜂池

// #tr Tooltip_LargeSteamBeeBreeder_02
// # Auto-breeds up the chain using pool species, new species stay in pool
// # zh_CN 依照繁育链自动向上杂交，新品种永久留存池中

// #tr Tooltip_LargeSteamBeeBreeder_03
// # 6.4 seconds per breeding cycle, uses actual mutation chance from Forestry
// # zh_CN 每次繁育周期 3.2 秒，使用林业原版杂交概率

// #tr Tooltip_LargeSteamBeeBreeder_04
// # Each princess input → one target princess output when chain completes
// # zh_CN 每投入一只公主蜂 → 繁育完成后输出一只目标公主蜂

// #tr Tooltip_LargeSteamBeeBreeder_05
// # Consumes 51200L steam per breeding attempt
// # zh_CN 每次繁育尝试消耗 51200L 蒸汽

// #tr Tooltip_LargeSteamBeeBreeder_06
// # Insert Stainless Steel gear in controller slot for +10% mutation chance
// # zh_CN 在主机内插入不锈钢齿轮 +10% 杂交成功率

// #tr Tooltip_LargeSteamBeeBreeder_Casing
// # Machine casing
// # zh_CN 机器外壳

// #tr BeeBreeder_missing_drone
// # Drone pool missing required species!
// # zh_CN 雄蜂池缺少所需品种！

// #tr BeeBreeder_unreachable_target
// # Target species unreachable from current pool!
// # zh_CN 当前池中品种无法到达目标品种！

public class LargeSteamBeeBreeder extends GTNCSteamMultiBlockBase<LargeSteamBeeBreeder>
    implements ISurvivalConstructable {

    // ==================== 常量 ====================

    /** 每次繁育消耗的蒸汽量 (L) */
    private static final int STEAM_PER_BREEDING = 51200;

    /** 每周期繁育耗时 (ticks) */
    private static final int TICKS_PER_BREEDING = 64;

    /** 每周期最大并行繁育步骤数 */
    private static final int MAX_PARALLEL_STEPS = 16;

    // ==================== 结构定义 ====================

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 8;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;
    private int tierMachine = 1;

    /** 控制器槽是否有不锈钢齿轮（+10%成功率） */
    private boolean hasStainlessSteelGear = false;

    private IStructureDefinition<LargeSteamBeeBreeder> STRUCTURE_DEFINITION = null;

    // 15 wide (x), 17 tall (y), 15 deep (z)
    // A=glass, B=dirt/grass, G=casing+hatches, H=wood planks, I=wood slabs, J/K/L/N/O/P=bronze frame
    // W(water) and F(flowers) replaced with spaces (air)
    private final String[][] shape = transpose(new String[][] {
        {"               ","               ","               ","      HHH      ","    HHAAAHH    ","    HAPLPAH    ","   HAPAAAPAH   ","   HALAAALAH   ","   HAPAAAPAH   ","    HAPLPAH    ","    HHAAAHH    ","      HHH      ","               ","               ","               "},
        {"               ","               ","      GGG      ","    GG   GG    ","   G       G   ","   G       G   ","  G         G  ","  G         G  ","  G         G  ","   G       G   ","   G       G   ","    GG   GG    ","      GGG      ","               ","               "},
        {"               ","      HHH      ","   HHH   HHH   ","  HG       GH  ","  H         H  ","  H         H  "," H           H "," H           H "," H           H ","  H         H  ","  H         H  ","  HG       GH  ","   HHH   HHH   ","      HHH      ","               "},
        {"      GGG      ","   GGG   GGG   ","  G         G  "," G           G "," G           G "," G           G ","G             G","G             G","G             G"," G           G "," G           G "," G           G ","  G         G  ","   GGG   GGG   ","      GGG      "},
        {"      AAA      ","   OLA   ALO   ","  P         P  "," O           O "," L           L "," A           A ","A             A","A             A","A             A"," A           A "," L           L "," O           O ","  P         P  ","   OLA   ALO   ","      AAA      "},
        {"     AAAAA     ","   NA     AO   ","  P         P  "," N           O "," A           A ","A             A","A     III     A","A     III     A","A     III     A","A             A"," A           A "," N           N ","  P         P  ","   NA     AN   ","     AAAAA     "},
        {"     AAAAA     ","   NA     AO   ","  P         P  "," N           O "," A           A ","A             A","A     JJJ     A","A     JKJ     A","A     JJJ     A","A             A"," A           A "," N           N ","  P         P  ","   NA     AN   ","     AAAAA     "},
        {"      AAA      ","   OLA   ALO   ","  P         P  "," O           O "," L           L "," A           A ","A             A","A             A","A             A"," A           A "," L           L "," O           O ","  P         P  ","   OLA   ALO   ","      AAA      "},
        {"      G~G      ","   GGGBBBGGG   ","  GBB     BBG  "," GBB       BBG "," GB         BG "," G           G ","GB           BG","GB           BG","GB           BG"," G           G "," GB         BG "," GBB       BBG ","  GBB     BBG  ","   GGGBBBGGG   ","      GGG      "},
        {"      HHH      ","    HHBBBHH    ","  HHBBBBBBBHH  ","  HBBB   BBBH  "," HBB       BBH "," HBB BBBBB BBH ","HBB  BBBBBB BBH","HBB BBBBBBB BBH","HBB BBBBBB  BBH"," HB  BBBBB BBH "," HBB   BB BBH ","  HBBB    BBH  ","  HHBBBBBBBHH  ","    HHBBBHH    ","      HHH      "},
        {"               ","     GGGGG     ","   GGBBBBBGG   ","  GBBBBBBBBBG  ","  GBBBBBBBBBG  "," GBBBBBBBBBBBG "," GBBBBBBBBBBBG "," GBBBBBBBBBBBG "," GBBBBBBBBBBBG "," GBBBBBBBBBBBG ","  GBBBBBBBBBG  ","  GBBBBBBBBBG  ","   GGBBBBBGG   ","     GGGGG     ","               "},
        {"               ","      HHH      ","    HHBBBHH    ","   HBBBBBBBH   ","  HBBBBBBBBBH  ","  HBBBBBBBBBH  "," HBBBBBBBBBBBH "," HBBBBBBBBBBBH "," HBBBBBBBBBBBH ","  HBBBBBBBBBH  ","  HBBBBBBBBBH  ","   HBBBBBBBH   ","    HHBBBHH    ","      HHH      ","               "},
        {"               ","               ","      GGG      ","    GGBBBGG    ","   GBBBBBBBG   ","   GBBBBBBBG   ","  GBBBBBBBBBG  ","  GBBBBBBBBBG  ","  GBBBBBBBBBG  ","   GBBBBBBBG   ","   GBBBBBBBG   ","    GGBBBGG    ","      GGG      ","               ","               "},
        {"               ","               ","       H       ","     HHBHH     ","    HBBBBBH    ","   HBBBBBBBH   ","   HBBBBBBBH   ","  HBBBBBBBBBH  ","   HBBBBBBBH   ","   HBBBBBBBH   ","    HBBBBBH    ","     HHBHH     ","       H       ","               ","               "},
        {"               ","               ","               ","       G       ","     GGBGG     ","    GBBBBBG    ","    GBBBBBG    ","   GBBBBBBBG   ","    GBBBBBG    ","    GBBBBBG    ","     GGBGG     ","       G       ","               ","               ","               "},
        {"               ","               ","               ","               ","      HHH      ","     HHHHH     ","    HHBBBHH    ","    HHBBBHH    ","    HHBBBHH    ","     HHHHH     ","      HHH      ","               ","               ","               ","               "},
        {"               ","               ","               ","               ","               ","               ","      GGG      ","      GHG      ","      GGG      ","               ","               ","               ","               ","               ","               "}
    });

    // ==================== 机器状态 ====================

    /** 目标蜜蜂品种名 */
    private String targetBeeSpecies = "";

    /** 上次计算繁育链时的目标品种 */
    private String lastChainTargetSpecies = "";

    /** 雄蜂池 */
    private DronePool dronePool = new DronePool();

    /** 当前繁育链 */
    private List<BeeBreedingHelper.BreedingStep> breedingChain = new ArrayList<>();

    /** 待输出的目标公主蜂数量 */
    private int pendingPrincessOutputs = 0;

    /** 当前缺少的品种名（用于GUI显示） */
    private String missingDroneSpecies = "";

    /** 是否因缺少品种而无法继续 */
    private boolean allTasksBlocked = false;

    /** 繁育链总步数 */
    private int chainTotalSteps = 0;

    /** 繁育链已完成步数 */
    private int chainCompletedSteps = 0;

    /** 用于客户端同步的雄蜂池品种数 */
    private int syncedPoolSize = 0;

    /** 用于客户端同步的雄蜂池摘要文本 */
    private String syncedPoolSummary = "";

    /** 用于客户端同步的繁育链摘要文本 */
    private String syncedChainSummary = "";

    // ==================== 构造函数 ====================

    public LargeSteamBeeBreeder(String aName) {
        super(aName);
    }

    public LargeSteamBeeBreeder(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamBeeBreeder(this.mName);
    }

    // ==================== 机器类型 ====================

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamBeeBreederRecipeType");
    }

    @Override
    public int getTierRecipes() {
        return 1;
    }

    // ==================== 纹理覆盖 ====================

    @Override
    protected gregtech.api.interfaces.IIconContainer getInactiveOverlay() {
        return OVERLAY_FRONT_DISTILLATION_TOWER;
    }

    @Override
    protected gregtech.api.interfaces.IIconContainer getActiveOverlay() {
        return OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE;
    }

    // ==================== 结构定义 ====================

    @Override
    public void onValueUpdate(byte aValue) {}

    @Override
    public byte getUpdateData() {
        return 0;
    }

    @Override
    protected boolean isHighPressure() {
        return false;
    }

    @Override
    public IStructureDefinition<LargeSteamBeeBreeder> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamBeeBreeder>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shape)
                .addElement('A', chainAllGlasses())
                .addElement('B', ofChain(ofBlockAnyMeta(Blocks.dirt, 0), ofBlock(Blocks.grass, 0)))
                .addElement(
                    'G',
                    ofChain(
                        buildSteamInput(LargeSteamBeeBreeder.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamBeeBreeder.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                InputHatch,
                                OutputBus)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(),
                        onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 10))))
                .addElement('H', ofBlocksMap(ofOreDictBlockMap("plankWood"), Blocks.planks, 0))
                .addElement('I', ofBlocksMap(ofOreDictBlockMap("slabWood"), Blocks.wooden_slab, 0))
                .addElement('J', ofBlock(gregtech.api.GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('K', ofBlock(gregtech.api.GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('L', ofBlock(gregtech.api.GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('N', ofBlock(gregtech.api.GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('O', ofBlock(gregtech.api.GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .addElement('P', ofBlock(gregtech.api.GregTechAPI.sBlockFrames, (int) Materials.Bronze.mMetaItemSubID))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        tierMachine = 1;
        updateHatchTexture();
        checkHasInputBus(errors);
        checkHasInputHatch(errors);
        checkHasOutputBus(errors);
        hasStainlessSteelGear = checkStainlessSteelGear(getControllerSlot());
    }

    private boolean checkStainlessSteelGear(ItemStack stack) {
        if (stack == null) return false;
        return stack.isItemEqual(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 1L));
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && mMachine && aTick % 20 == 0) {
            hasStainlessSteelGear = checkStainlessSteelGear(getControllerSlot());
            scanInputBuses();
            analyzeBreedingChain();
            syncedPoolSummary = buildPoolSummary();
            syncedChainSummary = buildChainSummary();
            syncedPoolSize = dronePool.getAvailableSpecies()
                .size();
        }
    }

    // ==================== 纹理 ====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(10),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(10),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(10) };
    }

    // ==================== 核心处理逻辑 ====================

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        boolean hasNewInput = scanInputBuses();

        if (targetBeeSpecies == null || targetBeeSpecies.isEmpty()) {
            updateChainDisplayInfo();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (dronePool.hasDrone(targetBeeSpecies) && pendingPrincessOutputs > 0) {
            int beforeOutput = pendingPrincessOutputs;
            outputPendingPrincesses();
            updateChainDisplayInfo();
            if (pendingPrincessOutputs < beforeOutput) {
                mMaxProgresstime = TICKS_PER_BREEDING;
                mEfficiency = 10000;
                mEfficiencyIncrease = 10000;
                lEUt = 0;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }

        if (pendingPrincessOutputs <= 0 && dronePool.hasDrone(targetBeeSpecies)) {
            updateChainDisplayInfo();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (pendingPrincessOutputs <= 0 && !hasNewInput) {
            analyzeBreedingChain();
            updateChainDisplayInfo();
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        if (!targetBeeSpecies.equals(lastChainTargetSpecies)) {
            recalculateBreedingChain();
            lastChainTargetSpecies = targetBeeSpecies;
        }
        updateChainDisplayInfo();

        if (breedingChain.isEmpty()) {
            if (dronePool.hasDrone(targetBeeSpecies)) {
                mMaxProgresstime = TICKS_PER_BREEDING;
                mEfficiency = 10000;
                mEfficiencyIncrease = 10000;
                lEUt = 0;
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            List<BeeBreedingHelper.MutationData> fallbackMutations = BeeBreedingHelper
                .getMutationsForSpecies(targetBeeSpecies);
            BeeBreedingHelper.MutationData usable = null;
            String actualMissing = "";
            for (BeeBreedingHelper.MutationData m : fallbackMutations) {
                if (m.parent1.equals(targetBeeSpecies) || m.parent2.equals(targetBeeSpecies)) continue;
                boolean hp1 = dronePool.hasDrone(m.parent1);
                boolean hp2 = dronePool.hasDrone(m.parent2);
                if (hp1 && hp2) {
                    usable = m;
                    break;
                }
                if (actualMissing.isEmpty()) {
                    actualMissing = !hp1 ? m.parent1 : m.parent2;
                }
            }

            if (usable != null) {
                breedingChain = new ArrayList<>();
                breedingChain.add(
                    new BeeBreedingHelper.BreedingStep(
                        usable.parent1,
                        usable.parent2,
                        targetBeeSpecies,
                        usable.chance));
            } else {
                allTasksBlocked = true;
                String missing = actualMissing.isEmpty() ? targetBeeSpecies : actualMissing;
                missingDroneSpecies = findMostBasicMissing(missing);
                // #tr GT5U.gui.text.recipe_result.BeeBreeder_unreachable_target
                // # Current pool species cannot reach target species!
                // # zh_CN 当前池中品种无法到达目标品种！
                return SimpleCheckRecipeResult.ofFailure("BeeBreeder_unreachable_target");
            }
        }

        int executableSteps = 0;
        String firstMissing = "";

        for (BeeBreedingHelper.BreedingStep step : breedingChain) {
            if (dronePool.hasDrone(step.result)) continue;
            boolean hasParent1 = dronePool.hasDrone(step.parent1);
            boolean hasParent2 = dronePool.hasDrone(step.parent2);
            if (hasParent1 && hasParent2) {
                executableSteps++;
            } else {
                if (firstMissing.isEmpty()) {
                    firstMissing = !hasParent1 ? step.parent1 : step.parent2;
                }
            }
        }

        if (executableSteps == 0) {
            if (!firstMissing.isEmpty()) {
                missingDroneSpecies = firstMissing;
                allTasksBlocked = true;
                // #tr GT5U.gui.text.recipe_result.BeeBreeder_missing_drone
                // # Drone pool missing required species!
                // # zh_CN 雄蜂池缺少所需品种！
                return SimpleCheckRecipeResult.ofFailure("BeeBreeder_missing_drone");
            }
            // If drone available but recipe has no steps, handle output in next cycle
            if (dronePool.hasDrone(targetBeeSpecies) && pendingPrincessOutputs > 0) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        int breedingCount = Math.min(executableSteps, MAX_PARALLEL_STEPS);
        long steamNeeded = (long) breedingCount * STEAM_PER_BREEDING;

        if (!tryConsumeSteam((int) Math.min(steamNeeded, Integer.MAX_VALUE))) {
            return CheckRecipeResultRegistry.insufficientPower(steamNeeded);
        }

        mMaxProgresstime = TICKS_PER_BREEDING;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        lEUt = 0;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (mProgresstime >= mMaxProgresstime - 1) {
            processBreedingResults();
        }
        return super.onRunningTick(aStack);
    }

    // ==================== 物品处理 ====================

    private boolean scanInputBuses() {
        ArrayList<ItemStack> inputs = getStoredInputs();
        List<ItemStack> toRemove = new ArrayList<>();
        boolean hasNew = false;

        for (ItemStack stack : inputs) {
            if (stack == null) continue;

            if (BeeBreedingHelper.isDrone(stack)) {
                dronePool.addDrone(stack);
                toRemove.add(stack);
                hasNew = true;
            } else if (BeeBreedingHelper.isPrincess(stack)) {
                String species = BeeBreedingHelper.getBeeSpecies(stack);
                if (species != null) {
                    ItemStack virtualDrone = BeeBreedingHelper.createDrone(species);
                    if (virtualDrone != null) {
                        virtualDrone.stackSize = 1;
                        dronePool.addDrone(virtualDrone);
                    }
                }
                pendingPrincessOutputs++;
                toRemove.add(stack);
                hasNew = true;
            }
        }

        for (ItemStack stack : toRemove) {
            stack.stackSize = 0;
        }
        if (!toRemove.isEmpty()) updateSlots();

        return hasNew;
    }

    private void processBreedingResults() {
        int processed = 0;

        for (BeeBreedingHelper.BreedingStep step : breedingChain) {
            if (processed >= MAX_PARALLEL_STEPS) break;

            if (dronePool.hasDrone(step.result)) continue;

            if (!dronePool.hasDrone(step.parent1) || !dronePool.hasDrone(step.parent2)) {
                continue;
            }

            processed++;

            double effectiveChance = step.chance + (hasStainlessSteelGear ? 10.0 : 0.0);
            if (BeeBreedingHelper.tryMutation(effectiveChance)) {
                ItemStack newDrone = BeeBreedingHelper.createDrone(step.result);
                if (newDrone != null) {
                    newDrone.stackSize = 8;
                    dronePool.addDrone(newDrone);
                }
            }
        }

    }

    private void outputPendingPrincesses() {
        if (targetBeeSpecies == null || targetBeeSpecies.isEmpty()) {
            pendingPrincessOutputs = 0;
            return;
        }

        if (pendingPrincessOutputs > 0) {
            ItemStack princess = BeeBreedingHelper.createPrincess(targetBeeSpecies);
            if (princess != null) {
                mOutputItems = new ItemStack[] { princess };
                pendingPrincessOutputs--;
            } else {
                allTasksBlocked = true;
                missingDroneSpecies = targetBeeSpecies;
            }
        }
    }

    private void analyzeBreedingChain() {
        if (targetBeeSpecies == null || targetBeeSpecies.isEmpty()) return;

        missingDroneSpecies = "";
        allTasksBlocked = false;

        if (!targetBeeSpecies.equals(lastChainTargetSpecies)) {
            recalculateBreedingChain();
            lastChainTargetSpecies = targetBeeSpecies;
        }

        updateChainDisplayInfo();

        if (breedingChain.isEmpty()) {
            // 池中已有目标 → 不需要繁育链
            if (dronePool.hasDrone(targetBeeSpecies)) return;

            List<BeeBreedingHelper.MutationData> fallbackMutations = BeeBreedingHelper
                .getMutationsForSpecies(targetBeeSpecies);
            BeeBreedingHelper.MutationData usable = null;
            String actualMissing = "";
            for (BeeBreedingHelper.MutationData m : fallbackMutations) {
                if (m.parent1.equals(targetBeeSpecies) || m.parent2.equals(targetBeeSpecies)) continue;
                boolean hp1 = dronePool.hasDrone(m.parent1);
                boolean hp2 = dronePool.hasDrone(m.parent2);
                if (hp1 && hp2) {
                    usable = m;
                    break;
                }
                if (actualMissing.isEmpty()) {
                    actualMissing = !hp1 ? m.parent1 : m.parent2;
                }
            }

            if (usable != null) {
                breedingChain = new ArrayList<>();
                breedingChain.add(
                    new BeeBreedingHelper.BreedingStep(
                        usable.parent1,
                        usable.parent2,
                        targetBeeSpecies,
                        usable.chance));
            } else {
                allTasksBlocked = true;
                String missing = actualMissing.isEmpty() ? targetBeeSpecies : actualMissing;
                missingDroneSpecies = findMostBasicMissing(missing);
            }
            return;
        }

        // 检查是否有可执行的步骤
        String firstMissing = "";
        for (BeeBreedingHelper.BreedingStep step : breedingChain) {
            if (dronePool.hasDrone(step.result)) continue;
            boolean hasParent1 = dronePool.hasDrone(step.parent1);
            boolean hasParent2 = dronePool.hasDrone(step.parent2);
            if (hasParent1 && hasParent2) {
                return; // 有步骤可执行，无需报告缺失
            }
            if (firstMissing.isEmpty()) {
                firstMissing = !hasParent1 ? step.parent1 : step.parent2;
            }
        }

        if (!firstMissing.isEmpty()) {
            missingDroneSpecies = firstMissing;
            allTasksBlocked = true;
        }
    }

    private void recalculateBreedingChain() {
        Set<String> poolSpecies = new HashSet<>();
        for (String species : dronePool.getAvailableSpecies()) {
            if (dronePool.hasDrone(species)) {
                poolSpecies.add(species);
            }
        }
        breedingChain = BeeBreedingHelper.createBreedingChain(targetBeeSpecies, poolSpecies);
    }

    private String findMostBasicMissing(String species) {
        return findMostBasicMissing(species, new HashSet<>());
    }

    private String findMostBasicMissing(String species, Set<String> visited) {
        if (dronePool.hasDrone(species)) return null;
        if (!visited.add(species)) return species;
        List<BeeBreedingHelper.MutationData> mutations = BeeBreedingHelper.getMutationsForSpecies(species);
        if (mutations.isEmpty()) return species;
        for (BeeBreedingHelper.MutationData m : mutations) {
            if (m.parent1.equals(species) || m.parent2.equals(species)) continue;
            String missing = findMostBasicMissing(m.parent1, visited);
            if (missing != null) return missing;
            missing = findMostBasicMissing(m.parent2, visited);
            if (missing != null) return missing;
        }
        return species;
    }

    private int countCompletedSteps() {
        int count = 0;
        for (BeeBreedingHelper.BreedingStep step : breedingChain) {
            if (dronePool.hasDrone(step.result)) count++;
        }
        return count;
    }

    private void updateChainDisplayInfo() {
        chainTotalSteps = breedingChain.size();
        chainCompletedSteps = countCompletedSteps();
    }

    private String buildPoolSummary() {
        Set<String> species = dronePool.getAvailableSpecies();
        if (species.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : species) {
            if (dronePool.hasDrone(s)) {
                if (sb.length() > 0) sb.append("|");
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private String buildChainSummary() {
        if (breedingChain.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (BeeBreedingHelper.BreedingStep step : breedingChain) {
            if (sb.length() > 0) sb.append("|");
            String status;
            if (dronePool.hasDrone(step.result)) {
                status = "D";
            } else if (dronePool.hasDrone(step.parent1) && dronePool.hasDrone(step.parent2)) {
                status = "R";
            } else {
                status = "B";
            }
            sb.append(status)
                .append(",")
                .append(step.parent1)
                .append(",")
                .append(step.parent2)
                .append(",")
                .append(step.result)
                .append(",")
                .append(String.format("%.1f", step.chance + (hasStainlessSteelGear ? 10.0 : 0.0)));
        }
        return sb.toString();
    }

    // ==================== GUI 交互 ====================

    @Override
    protected MTEMultiBlockBaseGui<LargeSteamBeeBreeder> getGui() {
        return new LargeSteamBeeBreederGui(this);
    }

    @Override
    public boolean doesBindPlayerInventory() {
        return true;
    }

    @Override
    public int getGUIHeight() {
        return 192;
    }

    // ==================== NBT 存档 ====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        aNBT.setString("targetBeeSpecies", targetBeeSpecies != null ? targetBeeSpecies : "");
        aNBT.setInteger("pendingPrincessOutputs", pendingPrincessOutputs);
        aNBT.setTag("dronePool", dronePool.toNBT());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        targetBeeSpecies = aNBT.getString("targetBeeSpecies");
        pendingPrincessOutputs = aNBT.getInteger("pendingPrincessOutputs");

        if (aNBT.hasKey("dronePool")) {
            dronePool = DronePool.fromNBT(aNBT.getCompoundTag("dronePool"));
        } else {
            dronePool = new DronePool();
        }

        breedingChain.clear();
    }

    // ==================== 访问器 ====================

    public String getTargetBeeSpecies() {
        return targetBeeSpecies;
    }

    public void setTargetBeeSpecies(String species) {
        this.targetBeeSpecies = species;
    }

    public DronePool getDronePool() {
        return dronePool;
    }

    public int getSyncedPoolSize() {
        return syncedPoolSize;
    }

    public int getPendingPrincessOutputs() {
        return pendingPrincessOutputs;
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

    public String getMissingDroneSpecies() {
        return missingDroneSpecies;
    }

    public String getSyncedPoolSummary() {
        return syncedPoolSummary;
    }

    public String getSyncedChainSummary() {
        return syncedChainSummary;
    }

    // ==================== Tooltip ====================

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamBeeBreederRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_06"))
            .beginStructureBlock(15, 17, 15, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamBeeBreeder_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ==================== 信息显示 ====================

    @Override
    public String[] getInfoData() {
        String[] superInfo = super.getInfoData();
        String[] info = new String[superInfo.length + 5];
        System.arraycopy(superInfo, 0, info, 0, superInfo.length);

        info[superInfo.length] = "Target: " + (targetBeeSpecies.isEmpty() ? "Not Set" : targetBeeSpecies);
        info[superInfo.length + 1] = "Pool Species: " + dronePool.getAvailableSpecies()
            .size();
        info[superInfo.length + 2] = "Chain Progress: " + countCompletedSteps() + "/" + breedingChain.size();
        info[superInfo.length + 3] = "Pending Output: " + pendingPrincessOutputs;
        info[superInfo.length + 4] = "Steam/Cycle: " + Utils.formatNumbers(STEAM_PER_BREEDING) + "L";

        return info;
    }
}
