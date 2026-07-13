package com.xyp.gtnc.Config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xyp.gtnc.ScienceNotCool;

/**
 * 配置管理类，在游戏预初始化之前提供静态配置
 */
public class Config {

    public static final Logger configLog = LogManager.getLogger(ScienceNotCool.MODID + "_Config");

    /**
     * GregTech工具制作耐久度配置
     * 控制GregTech模组中工具在制作时的耐久度相关参数
     */
    public static float gtToolsCraftingDurability = 10000F;

    // modifysomeConfig
    public static boolean enableAlwaysDisplayRecipeOwner = true;
    public static boolean enableAlwaysDisplayWailaAverageNS = true;
    public static boolean enableAlwaysDisplayNEIOriginalVoltage = true;

    // region TimeVial 配置
    public static boolean enableTimeVial = true;
    public static boolean enableBlockMode = true;
    public static int accelerateBlockInterval = 2;
    public static boolean enableLogInfo = false;
    public static boolean limitOneTimeVial = true;
    public static float timeVialDiscountValue = 0.9965F;
    public static float defaultTimeVialVolumeValue = 0.5F;
    public static boolean enableTimeAcceleratorBoost = true;
    public static boolean enableAccelerateGregTechMachine = true;
    public static float accelerateGregTechMachineDiscount = 0.8F;
    public static boolean enableResetRemainingTime = false;
    public static boolean disableShiftModification = false;
    public static int timeVialInitialRate = 32;
    public static int timeVialMaxAcceleration = 1024;
    public static int timeVialBaseDuration = 18000;
    // endregion

    // region TimeAccelerator 配置
    /**
     * 世界加速器 (MTETimeAccelerator) TE 模式下跳过加速的 TileEntity 黑名单。
     * 匹配方式：TileEntity 完整类名、或（GT 机器）其内部 MetaTileEntity 完整类名以列表中任一字符串开头。
     * 默认排除 AE2 (appeng) 与 AE2FC (com.glodblock.github)，因为反复 tick ME 网络方块会造成严重卡顿；
     * 以及本模组的超级样板仓 / 装配矩阵 / 量子计算机——这些是重型 AE/合成多方块，反复加速会拖垮 TPS。
     */
    public static String[] timeAcceleratorTileBlacklist = { "appeng.", "com.glodblock.github.",
        "com.xyp.gtnc.Common.machines.hatch.VaultPortHatch",
        "com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME",
        "com.xyp.gtnc.Common.machines.multiblock.AssemblerMatrix",
        "com.xyp.gtnc.Common.machines.multiblock.SingularityDataHub",
        "com.xyp.gtnc.Common.machines.multiblock.QuantumComputer" };
    /**
     * 世界加速器 (MTETimeAccelerator) TE 模式下,单个 tick 内加速所有目标机器的总时间预算(毫秒)。
     * <p>
     * 加速循环每调用一次 {@code updateEntity()} 就检查是否超预算,一旦超过就立即结束本 tick 的加速——
     * 没跑完的加速次数作废。蒸汽单方块机器的每次 tick 很贵(配方查询/排气),约 128 次就吃满原来写死的 25ms,
     * 于是设成 128 倍以上也跑不满,表现为"128 以上无额外加速"。调高此值可让高倍率真正生效,
     * <b>代价是重负载时更吃服务器 tick 时间(可能掉 TPS)</b>。默认 80ms。
     */
    public static int timeAcceleratorTickBudgetMs = 80;
    // endregion

    // region VeinMiningPickaxe 配置
    public static class VeinMinerPickaxe {

        public static int maxAmount = 327670;
        public static int maxRange = 32;
    }
    // endregion

    // region QuantumComputer 配置
    public static class QuantumComputer {

        public static int maxMultiblockSize = 7;
        public static int maxMultiThreader = 1;
        public static int maxDataEntangler = 1;
        public static boolean enableDebugMode = false;
    }
    // endregion

    // region MiracleDoor 配置
    public static class MiracleDoor {

        /** 合金冶炼(ABS)模式每次运行耗时，单位 tick。默认 25.6s = 512 ticks。 */
        public static int ticksOfProcessingTimeABSMode = 512;
        /** 恒星锻炉(EBF)模式每次运行耗时，单位 tick。默认 64s = 1280 ticks。 */
        public static int ticksOfProcessingTimeEBFMode = 1280;
        /** 合金冶炼(ABS)模式 EU 消耗倍率。默认 1。 */
        public static int multiplierOfEUCostABSMode = 1;
        /** 恒星锻炉(EBF)模式 EU 消耗倍率。默认 2。 */
        public static int multiplierOfEUCostEBFMode = 2;
    }
    // endregion

    // region ToolBelt 配置
    public static boolean releaseToSwap = true;
    public static boolean clipMouseToCircle = true;
    public static boolean allowClickOutsideBounds = true;
    public static boolean displayEmptySlots = true;
    public static boolean minecraftHasNoCircles = false;
    public static float radialDeadzoneOffset = 8.0f;
    // endregion

    // region ME Output Hatch 配置
    public static boolean OutPutHatchMEEnable = true;
    public static boolean OutPutBusMEEnable = true;
    // endregion

    // region CropsNH 配置
    /** 开启后，作物棒每次生长判定直接把进度拉满，即瞬间成熟。 */
    public static boolean enableCropInstantGrowth = true;
    /** 开启后，所有生成的种子的生长/产量/抗性三项属性都被拉满(31)。 */
    public static boolean enableCropMaxStats = true;
    /** 开启后，左键收获成熟作物必定掉落种子(绕过抗性概率判定)。 */
    public static boolean enableCropGuaranteedSeedDrop = true;
    // endregion

    // region Thaumcraft 配置
    /**
     * 开启后，禁止神秘时代(Thaumcraft)的「扭曲事件」发生——不再随机施加负面药水效果、
     * 生成心灵蜘蛛/古神守卫、召唤迷雾、发送幻觉聊天、强制解锁古神研究。
     * <p>
     * 实现方式：重定向 {@code WarpEvents.checkWarpEvent} 中触发事件的随机判定
     * ({@code rand.nextInt(100)})，使触发条件恒不成立，从而跳过整个事件块；
     * 而方法末尾的临时扭曲衰减({@code addWarpTemp(-1)})照常执行，
     * <b>扭曲值(perm/temp/sticky)完全不受影响</b>。
     */
    public static boolean disableWarpEvents = true;
    /**
     * 开启后，解锁全部研究——{@code ResearchManager.isResearchComplete} 对任意研究键恒返回 true。
     * <b>默认关闭</b>：这会让研究笔记本 GUI 误判全部研究已完成、显示异常，故有风险。
     * 只想省去研究小游戏而保留 GUI 的话，用 {@link #tcFreeResearchAspects}（研究点不消耗）即可。
     */
    public static boolean tcUnlockAllResearch = false;
    /**
     * 开启后，研究点数（aspects/观察点）永不因研究消耗而减少——重定向 {@code PlayerKnowledge.addAspectPool}
     * 的负增量（扣减）分支，使其不生效。研究台的六边形拼图小游戏因此永远够点，等效"研究免费"，且不破坏 GUI。
     */
    public static boolean tcFreeResearchAspects = true;
    /**
     * 开启后，坩埚（Crucible）不再产生通量污染——{@code TileCrucible.spill()} 在 HEAD 取消。
     * spill 是坩埚溢出/失衡时生成通量气/通量泥（进而涨 warp/taint）的唯一来源，取消它即"注魔零污染"的坩埚侧。
     */
    public static boolean tcCrucibleNoFlux = true;
    /**
     * 开启后，注魔祭坛（Infusion Matrix）注魔时不失稳——每个 craftCycle 开头把 {@code instability} 归零。
     * 失稳只会引发掉物/爆炸/闪电/涨 warp 等负面事件（含注魔侧的通量生成），合成进度本身与失稳无关，
     * 故归零后合成照常完成、但绝不触发坏事件。
     */
    public static boolean tcInfusionNoInstability = true;
    /**
     * 开启后，从 vis 网络抽取魔力永远成功且不消耗节点存量——{@code VisNetHandler.drainVis} 在 HEAD 直接返回请求量。
     * 等效"节点不衰减 + vis 无限"：任何需要 vis 的操作都拿得到，且节点/中继不掉存量。
     */
    public static boolean tcInfiniteVis = true;
    // endregion

    // region Applied Energistics 2 配置
    /**
     * 开启后，AE2 网络无视频道(channel)限制——所有需要频道的设备(总线/接口/终端等)一律获得频道，
     * 相当于把 AE2 原生的「无频道模式」局部打开。
     * <p>
     * 实现方式：重定向 rv3-977 中计算频道上限的两处(且仅这两处)对
     * {@code AEConfig.isFeatureEnabled(AEFeature.Channels)} 的调用，令其返回 {@code false}：
     * <ul>
     * <li>{@code GridNode.getCompressedChannelsIndex()}——有控制器网络，返回 index 3 →
     * {@code getMaxChannels()} = {@code CHANNEL_COUNT[3]} = {@code Integer.MAX_VALUE}；</li>
     * <li>{@code PathGridCache.calculateAdHocChannels()}——无控制器(ad-hoc)网络，上限取 {@code Integer.MAX_VALUE}。</li>
     * </ul>
     * 不去全局翻转该 feature 开关，因为 {@code BlockStorageReshuffle} 用它 gate 方块<b>注册</b>，
     * 全局关闭会导致该方块消失；这里只改频道计数点。
     * <p>
     * 幂等：若 AE2 配置本就关闭了 Channels 功能(已是无限频道)，{@code isFeatureEnabled} 本就返回 false，
     * 重定向无副作用。GTNH 默认开启频道,因此默认情况下这确实解除了限制。
     */
    public static boolean disableAE2ChannelLimit = true;
    // endregion

    // region Forestry 配置
    /** 开启后，所有蜜蜂无需气候匹配(jubilance)即可产出特殊产物，普通蜂箱也能出特产。 */
    public static boolean enableBeeAlwaysJubilant = true;
    /**
     * 开启后，本 mod 自行注册两个「满分」蜜蜂等位基因(速度/寿命)，
     * 养蜂机产出的蜂会写入这两个自注册基因，数值由下面两项控制，不依赖其它蜂 mod。
     */
    public static boolean enableCustomBeeAlleles = true;
    /** 自注册速度基因(名「无尽」)数值(林业原版极速=1.7，MagicBees 致盲=2.0)。 */
    public static float customBeeSpeedValue = 100.0F;
    /** 自注册寿命基因(名「不死」)数值，单位蜜蜂刻(林业原版最长寿=70)。 */
    public static int customBeeLifespanValue = 600000;
    /**
     * 开启后，普通蜂箱杂交产出的后代只出纯合子（每条染色体的两条等位基因相同），不出杂合子。
     * <p>
     * 重定向 {@code Bee.createOffspring} 内对 {@code Chromosome.inheritChromosome} 的调用：原版从两个亲本
     * 各随机取一条等位基因凑成 {@code (choice1, choice2)}，两者不同即杂合子；这里改为两条都用 {@code choice1}，
     * 于是后代每条染色体必为纯合。物种(SPECIES)染色体照常继承（杂交/突变逻辑不受影响，只是变纯合）。
     * <p>
     * 只作用于 {@code Bee.createOffspring} 这一处调用——本 mod 的蜜蜂杂交机走「模板法」
     * ({@code applyMaxGenome} + {@code templateAsGenome})直接生成纯合基因组，不经过 {@code inheritChromosome}，
     * 因此其产出的蜜蜂基因完全不受本开关影响。
     */
    public static boolean enableBeeHomozygousOffspring = true;
    /**
     * 开启后，所有蜜蜂（野生、蜂巢掉落、原版蜂箱杂交、村民蜂等一切来源）读取基因组时，环境/产出相关性状一律拉满：
     * <ul>
     * <li>温度耐性/湿度耐性 = ±5（BOTH_5）</li>
     * <li>夜行性(nocturnal) / 穴居(caveDwelling) = true</li>
     * <li>工作速度(speed) = 本 mod 自注册的「无尽」基因值（{@link #customBeeSpeedValue}），未注册时回退原值</li>
     * <li>授粉速度(flowering) = 99（MAXIMUM）</li>
     * <li>采蜜对象(flowerProvider) = 鲜花（vanilla flowers，UID {@code forestry.flowersVanilla}）</li>
     * </ul>
     * 于是任何蜂在任意气候、夜间、地下都能工作，且速度/授粉/采蜜对象统一。
     * <p>
     * 实现方式：{@code @Inject} 覆写 {@code BeeGenome} 的对应 getter
     * （{@code getToleranceTemp/getToleranceHumid/getNocturnal/getCaveDwelling/getSpeed/getFlowering/getFlowerProvider}）的返回值。
     * 这是<b>读时覆写</b>，不改动存储的基因组 NBT。
     * <p>
     * <b>不波及本 mod 的蜜蜂杂交机</b>：杂交机产出的蜂这些项本就已是满值（applyMaxGenome 设 BOTH_5/无尽/MAXIMUM/vanilla 等），
     * getter 返回值与覆写值一致，对其零影响。注意：beealyzer/分析仪的 tooltip 直读 allele 名、不走这些 getter，
     * 故显示名可能仍是原值，但蜂的实际工作判定已按满值生效。
     */
    public static boolean enableBeeMaxEnvironment = true;
    /**
     * 开启后，蜂巢(野生蜂窝)被破坏掉落的蜜蜂(公主/雄蜂/附加)基因组会被<b>真正写满</b>——物种保持原样，其余
     * 染色体全部拉满并写成纯合(可稳定遗传)。与 {@link #enableBeeMaxEnvironment} 的读时覆写不同，这里改动的是
     * 存储的基因组 NBT，因此<b>分析仪(Beealyzer)能读到满值数字</b>、后代也能 breed true。
     * <p>
     * 实现方式：{@code @Inject} 在 {@code BlockBeehives.getDrops} 的返回处遍历掉落列表，对每只蜂调用
     * {@code BeeBreedingHelper.maximizeBeeStack} 重建。这是所有蜂巢掉落的唯一出口(含 ExtraBees 等附属的
     * IHiveDrop 实现)，一处覆盖全部。
     * <p>
     * <b>不波及本 mod 的蜜蜂杂交机</b>：杂交机走 createDrone/createPrincess 直接生成，完全不经过蜂巢掉落逻辑。
     */
    public static boolean enableBeeMaxGenomeOnHiveDrop = true;
    /**
     * 开启后，普通蜂箱杂交产出的后代基因组会被<b>真正写满</b>——物种保持原样，其余染色体全部拉满并写成纯合。
     * 与 {@link #enableBeeMaxGenomeOnHiveDrop} 是同一套写 NBT 逻辑，只是作用路径不同：这里是普通蜂箱杂交后代
     * ({@code Bee.createOffspring} 返回处)，那里是世界蜂巢破坏掉落。开启后蜂箱杂交蜂分析仪也能读到满值、可 breed true。
     * <p>
     * <b>不波及本 mod 的蜜蜂杂交机</b>：杂交机走 createDrone/createPrincess 直接生成，不经过 createOffspring。
     */
    public static boolean enableBeeMaxGenomeOnBreed = true;
    /**
     * 诱变框架突变(杂交)成功率乘数。插入蜂箱/蜂房框架槽后，把突变成功率乘以此值。
     * 默认 10.0(GT++ 原版 MUTAGENIC 框架为 5.0)。只对有框架槽的蜂箱生效，不影响本 mod 的蜜蜂杂交机。
     */
    public static float mutagenicFrameMutationMultiplier = 10.0F;
    /**
     * 诱变框架寿命倍率(累乘，1.0 = 不变)。GT++ 原版为 0.0001——蜂后寿命被砍到近乎为零，插上去几乎瞬死、
     * 疯狂重滚后代。<b>这才是诱变框架"出杂交快"的真正原因</b>：单次突变判定概率只 ×5，但繁殖循环数(单位时间
     * 滚后代的次数)暴涨几个数量级，突变哗哗地出。数值越低越猛。
     */
    public static float mutagenicFrameLifespanModifier = 0.0001F;
    /**
     * 诱变框架产量倍率。GT++ 原版为 9.0。
     */
    public static float mutagenicFrameProductionModifier = 9.0F;
    /**
     * 诱变框架基因衰变系数(累乘，1.0 = 不变，0.0 = 完全不衰变)。默认 0.0——完全不衰变(GT++ 原版为 1.0 中性)。
     */
    public static float mutagenicFrameGeneticDecay = 0.0F;
    /**
     * 诱变框架耐久(可用次数)。默认 0 = 永不磨损(GT++ 原版为 3，用 3 次即损坏)。
     */
    public static int mutagenicFrameMaxDamage = 0;

    /**
     * 无尽框架寿命倍率(累乘，1.0 = 不变)。默认 1000000.0——远大于 1，使 {@code age()} 里的 ageModifier=1/此值
     * 趋近 0，蜂后几乎不掉血，等效"寿命无限"。与诱变框架相反(那个砍到近零求瞬死)，这个拉到极大求长生，
     * 适合当纯产物框架用(蜂后长期驻留、稳定产出)。
     */
    public static float endlessFrameLifespanModifier = 1000000.0F;
    /**
     * 无尽框架产量倍率(加法累加进 {@code BeeHousingModifier.getProductionModifier})。默认 30.0。
     */
    public static float endlessFrameProductionModifier = 30.0F;
    /**
     * 无尽框架突变(杂交)成功率乘数。默认 0.0——完全不杂交突变(纯产物框架，保持蜂种不变)。
     */
    public static float endlessFrameMutationMultiplier = 0.0F;
    /**
     * 无尽框架基因衰变系数(累乘，1.0 = 不变，0.0 = 完全不衰变)。默认 0.0——完全不衰变。
     */
    public static float endlessFrameGeneticDecay = 0.0F;
    /**
     * 无尽框架耐久(可用次数)。默认 0 = 永不磨损(耐久不变)。
     */
    public static int endlessFrameMaxDamage = 0;
    // endregion

    // region 分类定义
    private static final String CATEGORY_TIME_VIAL = "Time_Vial";
    private static final String CATEGORY_GENERAL = "General";
    private static final String CATEGORY_VEIN_MINER_PICKAXE = "Vein_Miner_Pickaxe";
    private static final String CATEGORY_TOOL_BELT = "Tool_Belt";
    private static final String CATEGORY_ME_OUTPUT_HATCH = "ME_Output_Hatch";
    private static final String CATEGORY_QUANTUM_COMPUTER = "Quantum_Computer";
    private static final String CATEGORY_MIRACLE_DOOR = "Miracle_Door";
    private static final String CATEGORY_TIME_ACCELERATOR = "Time_Accelerator";
    private static final String CATEGORY_CROPSNH = "CropsNH";
    private static final String CATEGORY_FORESTRY = "Forestry";
    private static final String CATEGORY_THAUMCRAFT = "Thaumcraft";
    private static final String CATEGORY_AE2 = "Applied_Energistics_2";
    // endregion

    // region 配置文件
    static final File cfgDirPath = new File(System.getProperty("user.dir"), "config/" + ScienceNotCool.MODID);
    static final Configuration configuration = new Configuration(
        new File(cfgDirPath, ScienceNotCool.MODID + ".cfg"),
        true);
    // endregion

    static {
        categoryInit();
        {
            // Time Vial 配置项
            enableTimeVial = configuration
                .getBoolean("enableTimeVial", CATEGORY_TIME_VIAL, enableTimeVial, "Enable Time Vial item");

            enableBlockMode = configuration.getBoolean(
                "enableBlockMode",
                CATEGORY_TIME_VIAL,
                enableBlockMode,
                "Enable Block Mode for time acceleration");

            enableLogInfo = configuration
                .getBoolean("enableLogInfo", CATEGORY_TIME_VIAL, enableLogInfo, "Enable debug log info");

            limitOneTimeVial = configuration.getBoolean(
                "limitOneTimeVial",
                CATEGORY_TIME_VIAL,
                limitOneTimeVial,
                "Limit player to only one Time Vial (merges time from multiple vials)");

            defaultTimeVialVolumeValue = configuration.getFloat(
                "defaultTimeVialVolume",
                CATEGORY_TIME_VIAL,
                defaultTimeVialVolumeValue,
                0.0F,
                5.0F,
                "Set time vial sound volume");

            timeVialDiscountValue = configuration.getFloat(
                "timeVialDiscountValue",
                CATEGORY_TIME_VIAL,
                timeVialDiscountValue,
                0.0F,
                1.0F,
                "Set time vial discount value for acceleration cost");

            enableTimeAcceleratorBoost = configuration.getBoolean(
                "enableTimeAcceleratorBoost",
                CATEGORY_TIME_VIAL,
                enableTimeAcceleratorBoost,
                "Enable Time Accelerator Boost (boost to 256X instead of 128X)");

            enableAccelerateGregTechMachine = configuration.getBoolean(
                "enableAccelerateGregTechMachine",
                CATEGORY_TIME_VIAL,
                enableAccelerateGregTechMachine,
                "Enable Accelerate GregTech Machine");

            accelerateGregTechMachineDiscount = configuration.getFloat(
                "accelerateGregTechMachineDiscount",
                CATEGORY_TIME_VIAL,
                accelerateGregTechMachineDiscount,
                0.0F,
                1.0F,
                "Accelerate GregTech Machine cost discount");

            enableResetRemainingTime = configuration.getBoolean(
                "enableResetRemainingTime",
                CATEGORY_TIME_VIAL,
                enableResetRemainingTime,
                "Enable Reset Remaining Time when applying Time Vial acceleration");

            disableShiftModification = configuration.getBoolean(
                "disableShiftModification",
                CATEGORY_TIME_VIAL,
                disableShiftModification,
                "Disable shift key modification for GT mode");

            accelerateBlockInterval = configuration.getInt(
                "accelerateBlockInterval",
                CATEGORY_TIME_VIAL,
                accelerateBlockInterval,
                2,
                200,
                "Accelerate Block update interval (ticks)");

            timeVialInitialRate = configuration.getInt(
                "timeVialInitialRate",
                CATEGORY_TIME_VIAL,
                timeVialInitialRate,
                1,
                64,
                "Initial acceleration rate for Time Vial (default: 8, boosted: 16)");

            timeVialMaxAcceleration = configuration.getInt(
                "timeVialMaxAcceleration",
                CATEGORY_TIME_VIAL,
                timeVialMaxAcceleration,
                4,
                1024,
                "Maximum acceleration rate for Time Vial (default: 256, boosted: 512)");

            timeVialBaseDuration = configuration.getInt(
                "timeVialBaseDuration",
                CATEGORY_TIME_VIAL,
                timeVialBaseDuration,
                100,
                7200,
                "Base duration in ticks for Time Vial acceleration (default: 1200 = 60 seconds)");

            // Vein Miner Pickaxe 配置项
            VeinMinerPickaxe.maxAmount = configuration.getInt(
                "maxAmount",
                CATEGORY_VEIN_MINER_PICKAXE,
                VeinMinerPickaxe.maxAmount,
                1,
                Integer.MAX_VALUE,
                "Set maximum number of chained blocks for Vein Mining Pickaxe");

            VeinMinerPickaxe.maxRange = configuration.getInt(
                "maxRange",
                CATEGORY_VEIN_MINER_PICKAXE,
                VeinMinerPickaxe.maxRange,
                0,
                128,
                "Set maximum block distance for Vein Mining Pickaxe");

            // Tool Belt 配置项
            // #tr config.toolbelt.releaseToSwap
            // # Release To Swap
            // # zh_CN 释放按键交换物品
            releaseToSwap = configuration.getBoolean(
                "releaseToSwap",
                CATEGORY_TOOL_BELT,
                releaseToSwap,
                "If set to TRUE, releasing the menu key will activate the swap.");

            // #tr config.toolbelt.clipMouseToCircle
            // # Clip Mouse To Circle
            // # zh_CN 鼠标限制在圆圈内
            clipMouseToCircle = configuration.getBoolean(
                "clipMouseToCircle",
                CATEGORY_TOOL_BELT,
                clipMouseToCircle,
                "If set to TRUE, the radial menu will try to prevent the mouse from leaving the outer circle.");

            // #tr config.toolbelt.allowClickOutsideBounds
            // # Allow Click Outside Bounds
            // # zh_CN 允许点击边界外
            allowClickOutsideBounds = configuration.getBoolean(
                "allowClickOutsideBounds",
                CATEGORY_TOOL_BELT,
                allowClickOutsideBounds,
                "If set to TRUE, the radial menu will allow clicking outside the outer circle.");

            // #tr config.toolbelt.displayEmptySlots
            // # Display Empty Slots
            // # zh_CN 显示空槽位
            displayEmptySlots = configuration.getBoolean(
                "displayEmptySlots",
                CATEGORY_TOOL_BELT,
                displayEmptySlots,
                "If set to TRUE, always display all slots even when empty.");

            // #tr config.toolbelt.minecraftHasNoCircles
            // # Minecraft Has No Circles
            // # zh_CN 使用方形菜单
            minecraftHasNoCircles = configuration.getBoolean(
                "minecraftHasNoCircles",
                CATEGORY_TOOL_BELT,
                minecraftHasNoCircles,
                "If set to TRUE, the radial menu will be drawn as squares.");

            // #tr config.toolbelt.radialDeadzoneOffset
            // # Radial Deadzone Offset
            // # zh_CN 径向菜单死区偏移
            radialDeadzoneOffset = configuration.getFloat(
                "radialDeadzoneOffset",
                CATEGORY_TOOL_BELT,
                radialDeadzoneOffset,
                0.0f,
                30.0f,
                "Extra deadzone pixels added to the center of the radial menu.");

            // ME Output Hatch 配置项
            OutPutHatchMEEnable = configuration.getBoolean(
                "OutPutHatchMEEnable",
                CATEGORY_ME_OUTPUT_HATCH,
                OutPutHatchMEEnable,
                "Enable infinite capacity for ME Fluid Output Hatch");

            OutPutBusMEEnable = configuration.getBoolean(
                "OutPutBusMEEnable",
                CATEGORY_ME_OUTPUT_HATCH,
                OutPutBusMEEnable,
                "Enable infinite capacity for ME Item Output Bus");

            // Quantum Computer 配置项
            QuantumComputer.maxMultiblockSize = configuration.getInt(
                "maxMultiblockSize",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.maxMultiblockSize,
                3,
                64,
                "Maximum edge length of the Quantum Computer multiblock cube (minimum 3)");

            QuantumComputer.maxMultiThreader = configuration.getInt(
                "maxMultiThreader",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.maxMultiThreader,
                0,
                Integer.MAX_VALUE,
                "Maximum number of Multi-Threader blocks per Quantum Computer");

            QuantumComputer.maxDataEntangler = configuration.getInt(
                "maxDataEntangler",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.maxDataEntangler,
                0,
                Integer.MAX_VALUE,
                "Maximum number of Data Entangler blocks per Quantum Computer");

            QuantumComputer.enableDebugMode = configuration.getBoolean(
                "enableDebugMode",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.enableDebugMode,
                "Enable Quantum Computer structure-check debug logging");

            // Time Accelerator 配置项
            timeAcceleratorTileBlacklist = configuration.getStringList(
                "tileBlacklist",
                CATEGORY_TIME_ACCELERATOR,
                timeAcceleratorTileBlacklist,
                "TileEntity class-name prefixes skipped by the World Accelerator in TE mode. "
                    + "Any tile whose full class name starts with one of these is not accelerated. "
                    + "Default excludes AE2 (appeng.) and AE2FC (com.glodblock.github.) to avoid severe lag from ticking ME network blocks.");

            timeAcceleratorTickBudgetMs = configuration.getInt(
                "tickBudgetMs",
                CATEGORY_TIME_ACCELERATOR,
                timeAcceleratorTickBudgetMs,
                1,
                1000,
                "Per-tick time budget (ms) the World Accelerator spends accelerating all target machines in TE mode. "
                    + "The acceleration loop aborts for this tick once it exceeds this budget, discarding remaining "
                    + "iterations. Steam single-block machines have expensive ticks (recipe lookup / venting), so the "
                    + "old hardcoded 25ms only fit about 128 iterations, making speeds above 128x ineffective. Raise "
                    + "this to let high multipliers actually run, at the cost of more server tick time (possible TPS drop). Default 80.");

            // CropsNH 配置项
            enableCropInstantGrowth = configuration.getBoolean(
                "enableInstantGrowth",
                CATEGORY_CROPSNH,
                enableCropInstantGrowth,
                "If set to TRUE, crop sticks reach full maturity on their very next growth tick (instant growth).");
            enableCropMaxStats = configuration.getBoolean(
                "enableMaxStats",
                CATEGORY_CROPSNH,
                enableCropMaxStats,
                "If set to TRUE, every seed is created with maxed-out growth/gain/resistance stats (31/31/31).");
            enableCropGuaranteedSeedDrop = configuration.getBoolean(
                "enableGuaranteedSeedDrop",
                CATEGORY_CROPSNH,
                enableCropGuaranteedSeedDrop,
                "If set to TRUE, left-clicking a mature crop always drops a seed (bypasses the resistance-based chance check).");

            // Forestry 配置项
            enableBeeAlwaysJubilant = configuration.getBoolean(
                "enableBeeAlwaysJubilant",
                CATEGORY_FORESTRY,
                enableBeeAlwaysJubilant,
                "If set to TRUE, bees always count as jubilant when producing, so specialty products drop even in a basic apiary regardless of climate.");
            enableCustomBeeAlleles = configuration.getBoolean(
                "enableCustomBeeAlleles",
                CATEGORY_FORESTRY,
                enableCustomBeeAlleles,
                "If set to TRUE, this mod registers its own max speed/lifespan bee alleles and the bee breeder writes them into produced bees (independent of other bee mods).");
            customBeeSpeedValue = configuration.getFloat(
                "customBeeSpeedValue",
                CATEGORY_FORESTRY,
                customBeeSpeedValue,
                0.1F,
                1000.0F,
                "Value of the self-registered speed allele (Forestry Fastest=1.7, MagicBees Blinding=2.0).");
            customBeeLifespanValue = configuration.getInt(
                "customBeeLifespanValue",
                CATEGORY_FORESTRY,
                customBeeLifespanValue,
                1,
                1000000,
                "Value of the self-registered lifespan allele, in bee ticks (Forestry Longest=70).");

            enableBeeHomozygousOffspring = configuration.getBoolean(
                "enableHomozygousOffspring",
                CATEGORY_FORESTRY,
                enableBeeHomozygousOffspring,
                "If set to TRUE, vanilla apiary/alveary breeding only produces homozygous offspring (both alleles of "
                    + "every chromosome are identical), so no hybrids are ever bred. This mod's own bee breeder is "
                    + "unaffected (it builds bees from templates, not via Chromosome.inheritChromosome).");

            enableBeeMaxEnvironment = configuration.getBoolean(
                "enableMaxEnvironment",
                CATEGORY_FORESTRY,
                enableBeeMaxEnvironment,
                "If set to TRUE, every bee (wild, hive-dropped, vanilla-bred, any source) reads maxed traits at "
                    + "runtime: temperature/humidity tolerance BOTH_5 (+/-5), nocturnal, cave-dwelling, work speed = "
                    + "the custom Endless allele value, flowering (pollination) speed MAXIMUM (99), and flower provider "
                    + "= vanilla flowers. This is a read-time override of BeeGenome getters, so it does not alter "
                    + "stored genomes and does not affect this mod's own bee breeder (whose bees already have these maxed).");

            enableBeeMaxGenomeOnHiveDrop = configuration.getBoolean(
                "enableMaxGenomeOnHiveDrop",
                CATEGORY_FORESTRY,
                enableBeeMaxGenomeOnHiveDrop,
                "If set to TRUE, bees dropped from breaking a beehive have their stored genome NBT rewritten to max "
                    + "(species kept, all other chromosomes maxed and made homozygous, so it breeds true and shows up in "
                    + "the Beealyzer). Unlike enableMaxEnvironment this alters the actual genome, so the analyzer displays "
                    + "the maxed values. This mod's own bee breeder is unaffected (it never goes through hive drops).");

            enableBeeMaxGenomeOnBreed = configuration.getBoolean(
                "enableMaxGenomeOnBreed",
                CATEGORY_FORESTRY,
                enableBeeMaxGenomeOnBreed,
                "If set to TRUE, offspring bred in a normal apiary/bee house have their stored genome NBT rewritten to "
                    + "max (species kept, all other chromosomes maxed and made homozygous, so it breeds true and shows "
                    + "up in the Beealyzer). Same NBT-level change as enableMaxGenomeOnHiveDrop but for vanilla breeding "
                    + "(Bee.createOffspring). This mod's own bee breeder is unaffected (it never goes through createOffspring).");

            mutagenicFrameMutationMultiplier = configuration.getFloat(
                "mutagenicFrameMutationMultiplier",
                CATEGORY_FORESTRY,
                mutagenicFrameMutationMultiplier,
                0.0F,
                1000.0F,
                "Mutation (breeding) chance multiplier applied by the Mutagenic Frame when placed in an apiary/alveary "
                    + "frame slot. GT++ MUTAGENIC frame = 5.0. Multiplies with vanilla mutation chance.");
            mutagenicFrameLifespanModifier = configuration.getFloat(
                "mutagenicFrameLifespanModifier",
                CATEGORY_FORESTRY,
                mutagenicFrameLifespanModifier,
                0.0F,
                1000.0F,
                "Lifespan modifier applied by the Mutagenic Frame (multiplicative). GT++ MUTAGENIC frame = 0.0001, "
                    + "which slashes queen lifespan to near-zero so she dies almost instantly and re-rolls offspring "
                    + "over and over. THIS is why the frame produces mutations fast: not the per-roll chance (only x5), "
                    + "but the huge increase in breeding cycles per unit time. Lower = faster. 1.0 = unchanged.");
            mutagenicFrameProductionModifier = configuration.getFloat(
                "mutagenicFrameProductionModifier",
                CATEGORY_FORESTRY,
                mutagenicFrameProductionModifier,
                0.0F,
                1000.0F,
                "Production modifier applied by the Mutagenic Frame. GT++ MUTAGENIC frame = 9.0.");
            mutagenicFrameGeneticDecay = configuration.getFloat(
                "mutagenicFrameGeneticDecay",
                CATEGORY_FORESTRY,
                mutagenicFrameGeneticDecay,
                0.0F,
                10.0F,
                "Genetic decay modifier applied by the Mutagenic Frame (multiplicative, 1.0 = unchanged, 0.0 = no decay). "
                    + "Default 0.0 = no genetic decay (GT++ MUTAGENIC frame = 1.0 neutral).");
            mutagenicFrameMaxDamage = configuration.getInt(
                "mutagenicFrameMaxDamage",
                CATEGORY_FORESTRY,
                mutagenicFrameMaxDamage,
                0,
                Integer.MAX_VALUE,
                "Durability (number of uses) of the Mutagenic Frame. Default 0 = never wears out "
                    + "(GT++ MUTAGENIC frame = 3, breaks after 3 uses).");

            endlessFrameLifespanModifier = configuration.getFloat(
                "endlessFrameLifespanModifier",
                CATEGORY_FORESTRY,
                endlessFrameLifespanModifier,
                0.0F,
                Float.MAX_VALUE,
                "Lifespan modifier applied by the Endless Frame (multiplicative). Default 1000000.0, far above 1, so "
                    + "the ageModifier (1/this) approaches 0 and the queen barely ages, effectively an infinite "
                    + "lifespan. Opposite of the Mutagenic Frame (which slashes it to near-zero for instant death). "
                    + "Meant as a pure production frame (queen stays resident, produces steadily).");
            endlessFrameProductionModifier = configuration.getFloat(
                "endlessFrameProductionModifier",
                CATEGORY_FORESTRY,
                endlessFrameProductionModifier,
                0.0F,
                1000.0F,
                "Production modifier applied by the Endless Frame (additive into BeeHousingModifier). Default 30.0.");
            endlessFrameMutationMultiplier = configuration.getFloat(
                "endlessFrameMutationMultiplier",
                CATEGORY_FORESTRY,
                endlessFrameMutationMultiplier,
                0.0F,
                1000.0F,
                "Mutation (breeding) chance multiplier applied by the Endless Frame. Default 0.0 = no breeding "
                    + "mutation at all (pure production frame, keeps the bee species unchanged).");
            endlessFrameGeneticDecay = configuration.getFloat(
                "endlessFrameGeneticDecay",
                CATEGORY_FORESTRY,
                endlessFrameGeneticDecay,
                0.0F,
                10.0F,
                "Genetic decay modifier applied by the Endless Frame (multiplicative, 1.0 = unchanged, 0.0 = no decay). "
                    + "Default 0.0 = no genetic decay.");
            endlessFrameMaxDamage = configuration.getInt(
                "endlessFrameMaxDamage",
                CATEGORY_FORESTRY,
                endlessFrameMaxDamage,
                0,
                Integer.MAX_VALUE,
                "Durability (number of uses) of the Endless Frame. Default 0 = never wears out.");

            // Thaumcraft 配置项
            disableWarpEvents = configuration.getBoolean(
                "disableWarpEvents",
                CATEGORY_THAUMCRAFT,
                disableWarpEvents,
                "If set to TRUE, Thaumcraft warp events (debuff potions, mind spiders, eldritch guardians, "
                    + "fog, hallucination messages, forced eldritch research) never trigger. "
                    + "The warp value itself (perm/temp/sticky) is left completely unchanged, including its normal temp decay.");

            tcUnlockAllResearch = configuration.getBoolean(
                "unlockAllResearch",
                CATEGORY_THAUMCRAFT,
                tcUnlockAllResearch,
                "If set to TRUE, every research counts as complete (isResearchComplete always returns true). "
                    + "Note: this can confuse the research-notebook GUI. To just skip the research minigame instead, use freeResearchAspects.");

            tcFreeResearchAspects = configuration.getBoolean(
                "freeResearchAspects",
                CATEGORY_THAUMCRAFT,
                tcFreeResearchAspects,
                "If set to TRUE, research aspect points are never consumed (the negative/reduce branch of PlayerKnowledge.addAspectPool is suppressed), "
                    + "so the research-table hex minigame always has enough points. Does not break the GUI.");

            tcCrucibleNoFlux = configuration.getBoolean(
                "crucibleNoFlux",
                CATEGORY_THAUMCRAFT,
                tcCrucibleNoFlux,
                "If set to TRUE, the Crucible never spills flux (TileCrucible.spill is cancelled). "
                    + "spill is the only source of flux gas/goo from crucible overflow, so this is the crucible side of zero-pollution alchemy.");

            tcInfusionNoInstability = configuration.getBoolean(
                "infusionNoInstability",
                CATEGORY_THAUMCRAFT,
                tcInfusionNoInstability,
                "If set to TRUE, the Infusion Matrix never becomes unstable (instability is forced to 0 each craft cycle). "
                    + "Instability only causes negative events (item ejection, explosions, lightning, warp, flux); crafting progress is independent, so crafting still completes normally.");

            tcInfiniteVis = configuration.getBoolean(
                "infiniteVis",
                CATEGORY_THAUMCRAFT,
                tcInfiniteVis,
                "If set to TRUE, draining vis from the network always succeeds and never depletes node reserves (VisNetHandler.drainVis returns the full requested amount). "
                    + "Equivalent to infinite vis + nodes that never decay.");

            // Applied Energistics 2 配置项
            disableAE2ChannelLimit = configuration.getBoolean(
                "disableChannelLimit",
                CATEGORY_AE2,
                disableAE2ChannelLimit,
                "If set to TRUE, AE2 networks ignore channel limits: every channel-requiring device always gets a channel "
                    + "(equivalent to locally enabling AE2's built-in no-channels mode). "
                    + "This only affects channel counting; nothing is registered or unregistered. "
                    + "Idempotent: if AE2's own Channels feature is already disabled, this changes nothing.");

            // Miracle Door 配置项
            MiracleDoor.ticksOfProcessingTimeABSMode = configuration.getInt(
                "ticksOfProcessingTimeABSMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.ticksOfProcessingTimeABSMode,
                1,
                Integer.MAX_VALUE,
                "Fixed processing time (ticks) of a single run in Alloy Smelter mode (default 512 = 25.6s).");
            MiracleDoor.ticksOfProcessingTimeEBFMode = configuration.getInt(
                "ticksOfProcessingTimeEBFMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.ticksOfProcessingTimeEBFMode,
                1,
                Integer.MAX_VALUE,
                "Fixed processing time (ticks) of a single run in Stellar Forge mode (default 1280 = 64s).");
            MiracleDoor.multiplierOfEUCostABSMode = configuration.getInt(
                "multiplierOfEUCostABSMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.multiplierOfEUCostABSMode,
                1,
                Integer.MAX_VALUE,
                "EU cost multiplier in Alloy Smelter mode (default 1).");
            MiracleDoor.multiplierOfEUCostEBFMode = configuration.getInt(
                "multiplierOfEUCostEBFMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.multiplierOfEUCostEBFMode,
                1,
                Integer.MAX_VALUE,
                "EU cost multiplier in Stellar Forge mode (default 2).");
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private static void categoryInit() {
        configuration.addCustomCategoryComment(CATEGORY_TIME_VIAL, "Configuration settings for Time Vial items");
        configuration.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration settings");
        configuration
            .addCustomCategoryComment(CATEGORY_VEIN_MINER_PICKAXE, "Configuration settings for Vein Mining Pickaxe");
        configuration.addCustomCategoryComment(CATEGORY_TOOL_BELT, "Configuration settings for Tool Belt");
        configuration
            .addCustomCategoryComment(CATEGORY_ME_OUTPUT_HATCH, "Configuration settings for ME Output Hatch and Bus");
        configuration
            .addCustomCategoryComment(CATEGORY_QUANTUM_COMPUTER, "Configuration settings for the Quantum Computer");
        configuration
            .addCustomCategoryComment(CATEGORY_TIME_ACCELERATOR, "Configuration settings for the World Accelerator");
        configuration.addCustomCategoryComment(CATEGORY_CROPSNH, "Configuration settings for CropsNH crop growth");
        configuration.addCustomCategoryComment(CATEGORY_FORESTRY, "Configuration settings for Forestry bees");
        configuration.addCustomCategoryComment(CATEGORY_MIRACLE_DOOR, "Configuration settings for the Miracle Door");
        configuration.addCustomCategoryComment(CATEGORY_THAUMCRAFT, "Configuration settings for Thaumcraft");
        configuration.addCustomCategoryComment(CATEGORY_AE2, "Configuration settings for Applied Energistics 2");
    }
}
