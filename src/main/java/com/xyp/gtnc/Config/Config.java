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
    // endregion

    // region 分类定义
    private static final String CATEGORY_TIME_VIAL = "Time_Vial";
    private static final String CATEGORY_GENERAL = "General";
    private static final String CATEGORY_VEIN_MINER_PICKAXE = "Vein_Miner_Pickaxe";
    private static final String CATEGORY_TOOL_BELT = "Tool_Belt";
    private static final String CATEGORY_ME_OUTPUT_HATCH = "ME_Output_Hatch";
    private static final String CATEGORY_QUANTUM_COMPUTER = "Quantum_Computer";
    private static final String CATEGORY_TIME_ACCELERATOR = "Time_Accelerator";
    private static final String CATEGORY_CROPSNH = "CropsNH";
    private static final String CATEGORY_FORESTRY = "Forestry";
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
    }
}
