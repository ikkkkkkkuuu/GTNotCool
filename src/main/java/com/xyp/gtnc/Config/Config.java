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

    // region CutCorners 配方提速配置（仿 GTNH-CutCorners）
    /**
     * 配方时长修改模式：0=不修改 / 1=固定时长 / 2=倍率。
     * <p>
     * 全局作用于所有走 {@code RecipeMapBackend.compileRecipe} 注册的 GT 配方(即绝大多数 GT 机器)，
     * 在配方注册时改写 {@code GTRecipe.mDuration}。由 {@code CutCorners.RecipeSpeedMixin} 应用。
     */
    public static int recipeSpeedMode = 2;
    /** mode=1(固定时长)时生效：所有配方时长固定为此 tick 数。 */
    public static int recipeSpeedFixedDuration = 1;
    /** mode=2(倍率)时生效：配方时长 × 此倍率(如 0.4 = 减少 60%)。结果至少为 1 tick。 */
    public static float recipeSpeedMultiplier = 0.1F;
    /**
     * 开启后，单方块基础机器(MTEBasicMachine)自动输出流体时一次性全部输出，而非每次上限 1 桶(1000 mB)。
     * 配合高速产出，避免流体在机器内积压。由 {@code CutCorners.BasicMachineOutputMixin} 应用。
     */
    public static boolean recipeSpeedFullFluidOutput = true;
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
    public static boolean allowClickOutsideBounds = false;
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
     * 开启后，扫描源质时无视「必须先发现父源质」的顺序要求——{@code PlayerKnowledge.hasDiscoveredParentAspects}
     * 对任意源质恒返回 {@code true}。
     * <p>
     * 神秘时代原本要求：复合源质(非六大基础源质)只有当组成它的两个父源质都已发现后，才能被扫描/发现，
     * 因此拿到魔导透镜初期很多东西"扫不了"，必须按源质合成树自底向上一步步扫。开启此项后可任意乱序扫描，
     * 复合源质直接发现、直接入池。默认开启。
     */
    public static boolean tcScanIgnoreParentAspects = true;
    /**
     * 开启后，坩埚（Crucible）不再产生通量污染——{@code TileCrucible.spill()} 在 HEAD 取消。
     * spill 是坩埚溢出/失衡时生成通量气/通量泥（进而涨 warp/taint）的唯一来源，取消它即"注魔零污染"的坩埚侧。
     */
    public static boolean tcCrucibleNoFlux = true;
    /**
     * 开启后，坩埚（Crucible）内的源质不再随时间流失。
     * <p>
     * 原版 {@code TileCrucible.updateEntity} 有两处会让源质减少：
     * <ul>
     * <li><b>高温熵变</b>：坩埚够热（{@code heat > 150}）时每约 100 tick 随机侵蚀 1 个源质——复合源质被降解成其组分之一，
     * 基础源质则直接删除并溢出（{@code spill}）。这就是"源质随时间消失"的主因。</li>
     * <li><b>过量溢出</b>：源质总量超过上限 100（{@code tagAmount() > 100}）时每 5 tick 删随机 1 个并溢出。</li>
     * </ul>
     * 两处的入口条件都读 {@code this.tagAmount()}（{@code updateEntity} 内仅此两次调用）。本开关 {@code @Redirect}
     * 这两次调用、开启时返回 0，使两个衰减分支都不进入，源质既不被侵蚀也不溢出。加热、精炼（attemptSmelt）、
     * 计时器维护等其余逻辑完全不受影响。
     */
    public static boolean tcCrucibleNoDecay = true;
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
    /**
     * 开启后，研究台的六边形连连看小游戏 GUI 里多出一个「一键研究」按钮，点击自动求解并落子完成研究。
     * <p>
     * 纯客户端求解：在网格上给空格填入<b>已发现</b>的源质，使全部主源质通过「相邻两格源质互为合成组件」的链连成一片
     * （与 {@code ResearchManager.checkResearchCompletion} 判定同构），再复用原版 {@code PacketAspectPlaceToServer}
     * 逐格落子，最后一子触发原生完成判定——与手动解开走完全相同的服务端路径，不新增网络包、不改服务端逻辑。
     * <p>
     * 前置校验：所有主源质须已发现、书写工具墨水耐久足够，否则弹提示并中止（绝不半途落子）。仍照常消耗墨水。
     */
    public static boolean tcResearchAutoSolve = true;
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
     * 开启后，普通蜂箱/蜂房杂交<b>忽略突变的「维度限制」</b>——即不再要求蜂箱处于某个特定维度(如末地/下界/太空)
     * 才能杂交出对应蜜蜂。
     * <p>
     * 实现方式：redirect Forestry {@code Mutation.getChance} 与 GT {@code GTBeeMutation.getBasicChance} 遍历突变
     * 条件时对 {@code IMutationCondition.getChance} 的调用，命中 GT 的 {@code DimensionMutationCondition} 时视为满足
     * (返回 1)。作用域用 {@code genome0 instanceof IBeeGenome} 锁死在蜜蜂，不影响树木/蝴蝶育种。
     * <p>
     * <b>不影响本 mod 的蜜蜂杂交机</b>：杂交机走模板法直接生成，不经过突变条件判定。
     */
    public static boolean enableBeeIgnoreDimensionMutation = true;
    /**
     * 开启后，普通蜂箱/蜂房杂交<b>忽略「蜂箱正下方需放特定方块/运行中的 GT 机器」这一类要求</b>。
     * <p>
     * 覆盖三种条件：Forestry 的 {@code MutationConditionRequiresResource} / {@code MutationConditionRequiresResourceOreDict}
     * (正下方需特定方块 / 矿辞条目)与 GT 的 {@code ActiveGTMachineMutationCondition}(正下方需一台运行中的 GT 机器)。
     * <p>
     * 实现方式同 {@link #enableBeeIgnoreDimensionMutation}，同样只作用蜜蜂、不影响树木/蝴蝶与本 mod 杂交机。
     */
    public static boolean enableBeeIgnoreResourceMutation = true;
    /**
     * 开启后，普通蜂箱/蜂房杂交<b>必定额外产出第二只公主蜂</b>(总计 2 只公主)。雄蜂产出不受影响，
     * 仍按原版基因/生育力决定数量。
     * <p>
     * 实现方式：redirect Forestry {@code BeekeepingLogic.spawnOffspring} 内对
     * {@code PluginApiculture.getSecondPrincessChance()} 的调用，开关开启时恒返回 100(=100% 触发第二只公主)。
     * 原版该几率默认 0(仅可在 Forestry 配置里调)。此逻辑只加公主、完全不碰 {@code spawnDrones}。
     * <p>
     * <b>不影响本 mod 的蜜蜂杂交机</b>：杂交机走模板法直接生成，不经过 {@code spawnOffspring}。
     */
    public static boolean enableBeeAlwaysSecondPrincess = true;
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
    private static final String CATEGORY_CUT_CORNERS = "CutCorners";
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
            // Vein Miner Pickaxe 配置项
            VeinMinerPickaxe.maxAmount = configuration.getInt(
                "maxAmount",
                CATEGORY_VEIN_MINER_PICKAXE,
                VeinMinerPickaxe.maxAmount,
                1,
                Integer.MAX_VALUE,
                "连锁采矿镐一次连锁开采的最大方块数");

            VeinMinerPickaxe.maxRange = configuration
                .getInt("maxRange", CATEGORY_VEIN_MINER_PICKAXE, VeinMinerPickaxe.maxRange, 0, 128, "连锁采矿镐的最大连锁距离(方块)");

            // Tool Belt 配置项
            // #tr config.toolbelt.releaseToSwap
            // # Release To Swap
            // # zh_CN 释放按键交换物品
            releaseToSwap = configuration
                .getBoolean("releaseToSwap", CATEGORY_TOOL_BELT, releaseToSwap, "开启后,松开菜单键即触发物品交换。");

            // #tr config.toolbelt.allowClickOutsideBounds
            // # Allow Click Outside Bounds
            // # zh_CN 允许点击死区关闭菜单
            allowClickOutsideBounds = configuration.getBoolean(
                "allowClickOutsideBounds",
                CATEGORY_TOOL_BELT,
                allowClickOutsideBounds,
                "开启后,在环形菜单中心死区内点击也会关闭菜单。");

            // #tr config.toolbelt.displayEmptySlots
            // # Display Empty Slots
            // # zh_CN 显示空槽位
            displayEmptySlots = configuration
                .getBoolean("displayEmptySlots", CATEGORY_TOOL_BELT, displayEmptySlots, "开启后,即使槽位为空也始终显示所有槽位。");

            // #tr config.toolbelt.minecraftHasNoCircles
            // # Minecraft Has No Circles
            // # zh_CN 使用方形菜单
            minecraftHasNoCircles = configuration
                .getBoolean("minecraftHasNoCircles", CATEGORY_TOOL_BELT, minecraftHasNoCircles, "开启后,环形菜单绘制为方形。");

            // #tr config.toolbelt.radialDeadzoneOffset
            // # Radial Deadzone Offset
            // # zh_CN 径向菜单死区偏移
            radialDeadzoneOffset = configuration.getFloat(
                "radialDeadzoneOffset",
                CATEGORY_TOOL_BELT,
                radialDeadzoneOffset,
                0.0f,
                30.0f,
                "环形菜单中心额外增加的死区像素数。");

            // ME Output Hatch 配置项
            OutPutHatchMEEnable = configuration
                .getBoolean("OutPutHatchMEEnable", CATEGORY_ME_OUTPUT_HATCH, OutPutHatchMEEnable, "开启 ME 流体输出仓的无限容量。");

            OutPutBusMEEnable = configuration
                .getBoolean("OutPutBusMEEnable", CATEGORY_ME_OUTPUT_HATCH, OutPutBusMEEnable, "开启 ME 物品输出总线的无限容量。");

            // Quantum Computer 配置项
            QuantumComputer.maxMultiblockSize = configuration.getInt(
                "maxMultiblockSize",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.maxMultiblockSize,
                3,
                64,
                "量子计算机多方块立方体的最大边长(最小 3)。");

            QuantumComputer.maxMultiThreader = configuration.getInt(
                "maxMultiThreader",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.maxMultiThreader,
                0,
                Integer.MAX_VALUE,
                "每台量子计算机可容纳的多线程器方块最大数量。");

            QuantumComputer.maxDataEntangler = configuration.getInt(
                "maxDataEntangler",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.maxDataEntangler,
                0,
                Integer.MAX_VALUE,
                "每台量子计算机可容纳的数据纠缠器方块最大数量。");

            QuantumComputer.enableDebugMode = configuration.getBoolean(
                "enableDebugMode",
                CATEGORY_QUANTUM_COMPUTER,
                QuantumComputer.enableDebugMode,
                "开启量子计算机结构检查的调试日志。");

            // Time Accelerator 配置项
            timeAcceleratorTileBlacklist = configuration.getStringList(
                "tileBlacklist",
                CATEGORY_TIME_ACCELERATOR,
                timeAcceleratorTileBlacklist,
                "世界加速器 TE 模式下跳过加速的 TileEntity 类名前缀列表。" + "凡完整类名以列表中任一前缀开头的方块都不会被加速。"
                    + "默认排除 AE2(appeng.)与 AE2FC(com.glodblock.github.)，因为反复 tick ME 网络方块会造成严重卡顿。");

            timeAcceleratorTickBudgetMs = configuration.getInt(
                "tickBudgetMs",
                CATEGORY_TIME_ACCELERATOR,
                timeAcceleratorTickBudgetMs,
                1,
                1000,
                "世界加速器 TE 模式下，单个 tick 内加速所有目标机器的总时间预算(毫秒)。" + "加速循环一旦超过此预算就立即结束本 tick 的加速，未跑完的加速次数作废。"
                    + "蒸汽单方块机器每次 tick 很贵(配方查询/排气)，旧的写死 25ms 只够约 128 次，"
                    + "导致 128 倍以上无额外加速。调高此值可让高倍率真正生效，代价是重负载时更吃服务器 tick 时间(可能掉 TPS)。默认 80。");

            // CropsNH 配置项
            enableCropInstantGrowth = configuration.getBoolean(
                "enableInstantGrowth",
                CATEGORY_CROPSNH,
                enableCropInstantGrowth,
                "开启后，作物棒在下一次生长判定时直接完全成熟(瞬间成熟)。");
            enableCropMaxStats = configuration.getBoolean(
                "enableMaxStats",
                CATEGORY_CROPSNH,
                enableCropMaxStats,
                "开启后，所有生成的种子的生长/产量/抗性三项属性都拉满(31/31/31)。");
            enableCropGuaranteedSeedDrop = configuration.getBoolean(
                "enableGuaranteedSeedDrop",
                CATEGORY_CROPSNH,
                enableCropGuaranteedSeedDrop,
                "开启后，左键收获成熟作物必定掉落种子(绕过基于抗性的概率判定)。");

            // Forestry 配置项
            enableBeeAlwaysJubilant = configuration.getBoolean(
                "enableBeeAlwaysJubilant",
                CATEGORY_FORESTRY,
                enableBeeAlwaysJubilant,
                "开启后，所有蜜蜂产出时一律视为「气候满足(jubilant)」，因此普通蜂箱无论气候如何都能掉落特殊产物。");
            enableCustomBeeAlleles = configuration.getBoolean(
                "enableCustomBeeAlleles",
                CATEGORY_FORESTRY,
                enableCustomBeeAlleles,
                "开启后，本模组自行注册满值的速度/寿命蜜蜂等位基因，养蜂机会把它们写入产出的蜂中(不依赖其它蜂类模组)。");
            customBeeSpeedValue = configuration.getFloat(
                "customBeeSpeedValue",
                CATEGORY_FORESTRY,
                customBeeSpeedValue,
                0.1F,
                1000.0F,
                "自注册速度等位基因的数值(林业原版最快=1.7，MagicBees 致盲=2.0)。");
            customBeeLifespanValue = configuration.getInt(
                "customBeeLifespanValue",
                CATEGORY_FORESTRY,
                customBeeLifespanValue,
                1,
                1000000,
                "自注册寿命等位基因的数值，单位为蜜蜂刻(林业原版最长寿=70)。");

            enableBeeHomozygousOffspring = configuration.getBoolean(
                "enableHomozygousOffspring",
                CATEGORY_FORESTRY,
                enableBeeHomozygousOffspring,
                "开启后，普通蜂箱/蜂房杂交只产出纯合子后代(每条染色体的两条等位基因都相同)，因此永远不会育出杂合子。"
                    + "本模组自己的蜜蜂杂交机不受影响(它走模板法生成蜂，不经过 Chromosome.inheritChromosome)。");

            enableBeeMaxEnvironment = configuration.getBoolean(
                "enableMaxEnvironment",
                CATEGORY_FORESTRY,
                enableBeeMaxEnvironment,
                "开启后，所有蜜蜂(野生、蜂巢掉落、原版杂交、任意来源)在运行时读取的性状一律拉满：" + "温度/湿度耐性 BOTH_5(±5)、夜行、穴居、工作速度=自注册的「无尽」基因值、"
                    + "授粉速度 MAXIMUM(99)、采蜜对象=原版鲜花。这是对 BeeGenome getter 的读时覆写，"
                    + "不改动存储的基因组，也不影响本模组自己的蜜蜂杂交机(其产出的蜂本就已是满值)。");

            enableBeeMaxGenomeOnHiveDrop = configuration.getBoolean(
                "enableMaxGenomeOnHiveDrop",
                CATEGORY_FORESTRY,
                enableBeeMaxGenomeOnHiveDrop,
                "开启后，破坏蜂巢掉落的蜜蜂其存储的基因组 NBT 会被真正改写为满值" + "(保留物种，其余染色体全部拉满并写成纯合，因此能稳定遗传且分析仪可读出)。"
                    + "与 enableMaxEnvironment 不同，这里改的是实际基因组，故分析仪能显示满值数字。"
                    + "本模组自己的蜜蜂杂交机不受影响(它从不经过蜂巢掉落)。");

            enableBeeMaxGenomeOnBreed = configuration.getBoolean(
                "enableMaxGenomeOnBreed",
                CATEGORY_FORESTRY,
                enableBeeMaxGenomeOnBreed,
                "开启后，普通蜂箱/蜂房杂交产出的后代其存储的基因组 NBT 会被真正改写为满值" + "(保留物种，其余染色体全部拉满并写成纯合，因此能稳定遗传且分析仪可读出)。"
                    + "与 enableMaxGenomeOnHiveDrop 是同一套 NBT 级改动，只是作用于原版杂交(Bee.createOffspring)。"
                    + "本模组自己的蜜蜂杂交机不受影响(它从不经过 createOffspring)。");

            enableBeeIgnoreDimensionMutation = configuration.getBoolean(
                "enableBeeIgnoreDimensionMutation",
                CATEGORY_FORESTRY,
                enableBeeIgnoreDimensionMutation,
                "开启后，普通蜂箱/蜂房杂交忽略突变的「维度限制」(GT 的 DimensionMutationCondition)，" + "因此原本需要特定维度(末地/下界/太空等)才能育出的蜂可以在任意维度杂交。"
                    + "仅作用于蜜蜂突变(不影响树木/蝴蝶)，也不影响本模组自己的蜜蜂杂交机。");

            enableBeeIgnoreResourceMutation = configuration.getBoolean(
                "enableBeeIgnoreResourceMutation",
                CATEGORY_FORESTRY,
                enableBeeIgnoreResourceMutation,
                "开启后，普通蜂箱/蜂房杂交忽略「蜂箱正下方需放特定方块 / 需一台运行中的 GT 机器」这一类要求"
                    + "(Forestry 的 MutationConditionRequiresResource/OreDict 与 GT 的 ActiveGTMachineMutationCondition)。"
                    + "仅作用于蜜蜂突变(不影响树木/蝴蝶)，也不影响本模组自己的蜜蜂杂交机。");

            enableBeeAlwaysSecondPrincess = configuration.getBoolean(
                "enableBeeAlwaysSecondPrincess",
                CATEGORY_FORESTRY,
                enableBeeAlwaysSecondPrincess,
                "开启后，普通蜂箱/蜂房杂交每次都额外产出第二只公主蜂(共 2 只)。"
                    + "实现方式：redirect Forestry 的 PluginApiculture.getSecondPrincessChance 恒返回 100(=100% 触发第二只公主)。"
                    + "只影响公主蜂数量，雄蜂产出仍随原版基因(fertility)不变；也不影响本模组自己的蜜蜂杂交机。");

            mutagenicFrameMutationMultiplier = configuration.getFloat(
                "mutagenicFrameMutationMultiplier",
                CATEGORY_FORESTRY,
                mutagenicFrameMutationMultiplier,
                0.0F,
                1000.0F,
                "诱变框架插入蜂箱/蜂房框架槽后施加的突变(杂交)成功率乘数。GT++ 原版 MUTAGENIC 框架=5.0，与原版突变率相乘。");
            mutagenicFrameLifespanModifier = configuration.getFloat(
                "mutagenicFrameLifespanModifier",
                CATEGORY_FORESTRY,
                mutagenicFrameLifespanModifier,
                0.0F,
                1000.0F,
                "诱变框架的寿命倍率(累乘)。GT++ 原版 MUTAGENIC 框架=0.0001——把蜂后寿命砍到近乎为零，"
                    + "插上去几乎瞬死、疯狂重滚后代。这才是诱变框架「出杂交快」的真正原因：单次突变判定概率只 ×5，"
                    + "但单位时间的繁殖循环数暴涨。数值越低越快，1.0=不变。");
            mutagenicFrameProductionModifier = configuration.getFloat(
                "mutagenicFrameProductionModifier",
                CATEGORY_FORESTRY,
                mutagenicFrameProductionModifier,
                0.0F,
                1000.0F,
                "诱变框架的产量倍率。GT++ 原版 MUTAGENIC 框架=9.0。");
            mutagenicFrameGeneticDecay = configuration.getFloat(
                "mutagenicFrameGeneticDecay",
                CATEGORY_FORESTRY,
                mutagenicFrameGeneticDecay,
                0.0F,
                10.0F,
                "诱变框架的基因衰变系数(累乘，1.0=不变，0.0=完全不衰变)。默认 0.0=完全不衰变(GT++ 原版 MUTAGENIC 框架=1.0 中性)。");
            mutagenicFrameMaxDamage = configuration.getInt(
                "mutagenicFrameMaxDamage",
                CATEGORY_FORESTRY,
                mutagenicFrameMaxDamage,
                0,
                Integer.MAX_VALUE,
                "诱变框架的耐久(可用次数)。默认 0=永不磨损(GT++ 原版 MUTAGENIC 框架=3，用 3 次即损坏)。");

            endlessFrameLifespanModifier = configuration.getFloat(
                "endlessFrameLifespanModifier",
                CATEGORY_FORESTRY,
                endlessFrameLifespanModifier,
                0.0F,
                Float.MAX_VALUE,
                "无尽框架的寿命倍率(累乘)。默认 1000000.0，远大于 1，使 ageModifier(=1/此值)趋近 0，"
                    + "蜂后几乎不掉血，等效「寿命无限」。与诱变框架相反(那个砍到近零求瞬死)，这个拉到极大求长生，"
                    + "适合当纯产物框架用(蜂后长期驻留、稳定产出)。");
            endlessFrameProductionModifier = configuration.getFloat(
                "endlessFrameProductionModifier",
                CATEGORY_FORESTRY,
                endlessFrameProductionModifier,
                0.0F,
                1000.0F,
                "无尽框架的产量倍率(加法累加进 BeeHousingModifier)。默认 30.0。");
            endlessFrameMutationMultiplier = configuration.getFloat(
                "endlessFrameMutationMultiplier",
                CATEGORY_FORESTRY,
                endlessFrameMutationMultiplier,
                0.0F,
                1000.0F,
                "无尽框架的突变(杂交)成功率乘数。默认 0.0=完全不杂交突变(纯产物框架，保持蜂种不变)。");
            endlessFrameGeneticDecay = configuration.getFloat(
                "endlessFrameGeneticDecay",
                CATEGORY_FORESTRY,
                endlessFrameGeneticDecay,
                0.0F,
                10.0F,
                "无尽框架的基因衰变系数(累乘，1.0=不变，0.0=完全不衰变)。默认 0.0=完全不衰变。");
            endlessFrameMaxDamage = configuration.getInt(
                "endlessFrameMaxDamage",
                CATEGORY_FORESTRY,
                endlessFrameMaxDamage,
                0,
                Integer.MAX_VALUE,
                "无尽框架的耐久(可用次数)。默认 0=永不磨损。");

            // Thaumcraft 配置项
            disableWarpEvents = configuration.getBoolean(
                "disableWarpEvents",
                CATEGORY_THAUMCRAFT,
                disableWarpEvents,
                "开启后，神秘时代的「扭曲事件」永不触发(负面药水、心灵蜘蛛、古神守卫、迷雾、幻觉聊天、强制解锁古神研究)。" + "扭曲值本身(永久/临时/黏滞)完全不受影响，包括其正常的临时衰减。");

            tcUnlockAllResearch = configuration.getBoolean(
                "unlockAllResearch",
                CATEGORY_THAUMCRAFT,
                tcUnlockAllResearch,
                "开启后，所有研究都视为已完成(isResearchComplete 恒返回 true)。"
                    + "注意：这会让研究笔记本 GUI 显示异常。若只想跳过研究小游戏，请改用 freeResearchAspects。");

            tcFreeResearchAspects = configuration.getBoolean(
                "freeResearchAspects",
                CATEGORY_THAUMCRAFT,
                tcFreeResearchAspects,
                "开启后，研究点数永不消耗(抑制 PlayerKnowledge.addAspectPool 的扣减分支)，" + "研究台的六边形拼图小游戏永远够点。不破坏 GUI。");

            tcScanIgnoreParentAspects = configuration.getBoolean(
                "scanIgnoreParentAspects",
                CATEGORY_THAUMCRAFT,
                tcScanIgnoreParentAspects,
                "开启后，扫描源质时无视「必须先发现父源质」的顺序要求(hasDiscoveredParentAspects 恒返回 true)，"
                    + "因此可任意乱序扫描，复合源质直接发现入池，无需按源质合成树自底向上逐个解锁。");

            tcCrucibleNoFlux = configuration.getBoolean(
                "crucibleNoFlux",
                CATEGORY_THAUMCRAFT,
                tcCrucibleNoFlux,
                "开启后，坩埚永不产生通量污染(取消 TileCrucible.spill)。" + "spill 是坩埚溢出产生通量气/通量泥的唯一来源，这是「注魔零污染」的坩埚侧。");

            tcCrucibleNoDecay = configuration.getBoolean(
                "crucibleNoDecay",
                CATEGORY_THAUMCRAFT,
                tcCrucibleNoDecay,
                "开启后，坩埚内的源质不再随时间流失(重定向 updateEntity 内两处 tagAmount 判定返回 0)。"
                    + "原版坩埚够热(heat>150)时会周期性侵蚀源质(复合降解为组分、基础直接消失并溢出通量)，"
                    + "且源质总量超 100 时会持续溢出丢失；开启后这两处衰减均不触发。不影响加热、投料精炼等正常功能。");

            tcInfusionNoInstability = configuration.getBoolean(
                "infusionNoInstability",
                CATEGORY_THAUMCRAFT,
                tcInfusionNoInstability,
                "开启后，注魔祭坛注魔时永不失稳(每个合成周期把 instability 归零)。" + "失稳只引发负面事件(掉物/爆炸/闪电/涨扭曲/通量)；合成进度与失稳无关，故合成照常完成。");

            tcInfiniteVis = configuration.getBoolean(
                "infiniteVis",
                CATEGORY_THAUMCRAFT,
                tcInfiniteVis,
                "开启后，从 vis 网络抽取魔力永远成功且不消耗节点存量(VisNetHandler.drainVis 直接返回请求量)。" + "等效「vis 无限 + 节点永不衰减」。");

            tcResearchAutoSolve = configuration.getBoolean(
                "researchAutoSolve",
                CATEGORY_THAUMCRAFT,
                tcResearchAutoSolve,
                "开启后，研究台的连连看小游戏 GUI 多出一个「一键研究」按钮，点击自动求解并落子完成研究。" + "纯客户端求解(给空格填已发现的源质使主源质连通)，复用原版放置通道逐格落子，"
                    + "最后一子触发原生完成判定，与手动解开路径一致。主源质须已发现、墨水须充足，否则弹提示并中止。仍照常消耗墨水。");

            // Applied Energistics 2 配置项
            disableAE2ChannelLimit = configuration.getBoolean(
                "disableChannelLimit",
                CATEGORY_AE2,
                disableAE2ChannelLimit,
                "开启后，AE2 网络无视频道限制：所有需要频道的设备一律获得频道(等效局部打开 AE2 原生的「无频道模式」)。" + "只影响频道计数，不注册/注销任何方块。"
                    + "幂等：若 AE2 本就关闭了频道功能，此项无副作用。");

            // Miracle Door 配置项
            MiracleDoor.ticksOfProcessingTimeABSMode = configuration.getInt(
                "ticksOfProcessingTimeABSMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.ticksOfProcessingTimeABSMode,
                1,
                Integer.MAX_VALUE,
                "合金冶炼(ABS)模式单次运行的固定耗时(tick)。默认 512 = 25.6 秒。");
            MiracleDoor.ticksOfProcessingTimeEBFMode = configuration.getInt(
                "ticksOfProcessingTimeEBFMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.ticksOfProcessingTimeEBFMode,
                1,
                Integer.MAX_VALUE,
                "恒星锻炉(EBF)模式单次运行的固定耗时(tick)。默认 1280 = 64 秒。");
            MiracleDoor.multiplierOfEUCostABSMode = configuration.getInt(
                "multiplierOfEUCostABSMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.multiplierOfEUCostABSMode,
                1,
                Integer.MAX_VALUE,
                "合金冶炼(ABS)模式的 EU 消耗倍率。默认 1。");
            MiracleDoor.multiplierOfEUCostEBFMode = configuration.getInt(
                "multiplierOfEUCostEBFMode",
                CATEGORY_MIRACLE_DOOR,
                MiracleDoor.multiplierOfEUCostEBFMode,
                1,
                Integer.MAX_VALUE,
                "恒星锻炉(EBF)模式的 EU 消耗倍率。默认 2。");

            // CutCorners 配方提速配置项
            recipeSpeedMode = configuration.getInt(
                "mode",
                CATEGORY_CUT_CORNERS,
                recipeSpeedMode,
                0,
                2,
                "配方时长修改模式(全局作用于绝大多数 GT 机器，在配方注册时改写时长)。\n" + "0 = 不修改\n"
                    + "1 = 固定时长：所有配方时长固定为 fixedDuration tick\n"
                    + "2 = 倍率：配方时长 × multiplier(如 0.4 = 减少 60%)");
            recipeSpeedFixedDuration = configuration.getInt(
                "fixedDuration",
                CATEGORY_CUT_CORNERS,
                recipeSpeedFixedDuration,
                1,
                Integer.MAX_VALUE,
                "mode=1(固定时长)时生效：所有配方时长固定为此 tick 数。");
            recipeSpeedMultiplier = configuration.getFloat(
                "multiplier",
                CATEGORY_CUT_CORNERS,
                recipeSpeedMultiplier,
                0.0001F,
                Float.MAX_VALUE,
                "mode=2(倍率)时生效：配方时长 × 此倍率(如 0.4 = 减少 60%)。结果至少为 1 tick。");
            recipeSpeedFullFluidOutput = configuration.getBoolean(
                "fullFluidOutput",
                CATEGORY_CUT_CORNERS,
                recipeSpeedFullFluidOutput,
                "开启后，单方块机器自动输出流体时一次抽干内部储罐(原版每次上限 1000mB)，配合高速产出避免输出堵塞。");
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    /**
     * 按 CutCorners 配方提速配置计算修改后的配方时长。供 {@code CutCorners.RecipeSpeedMixin} 在配方注册时调用。
     *
     * @param original 原始配方时长(tick)
     * @return 修改后的时长；mode=0 时原样返回；结果至少为 1 tick。
     */
    public static int getModifiedRecipeDuration(int original) {
        switch (recipeSpeedMode) {
            case 1:
                return Math.max(1, recipeSpeedFixedDuration);
            case 2:
                return Math.max(1, (int) (original * recipeSpeedMultiplier));
            case 0:
            default:
                return original;
        }
    }

    private static void categoryInit() {
        configuration.addCustomCategoryComment(CATEGORY_GENERAL, "通用配置设置");
        configuration.addCustomCategoryComment(CATEGORY_VEIN_MINER_PICKAXE, "连锁挖矿镐的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_TOOL_BELT, "工具腰带的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_ME_OUTPUT_HATCH, "ME 输出仓/输出总线的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_QUANTUM_COMPUTER, "量子计算机的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_TIME_ACCELERATOR, "世界加速器的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_CROPSNH, "CropsNH 作物生长的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_FORESTRY, "林业(Forestry)蜜蜂的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_MIRACLE_DOOR, "奇迹之门的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_THAUMCRAFT, "神秘时代(Thaumcraft)的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_AE2, "应用能源2(Applied Energistics 2)的配置设置");
        configuration.addCustomCategoryComment(CATEGORY_CUT_CORNERS, "配方提速(仿 GTNH-CutCorners)的配置设置");
    }
}
