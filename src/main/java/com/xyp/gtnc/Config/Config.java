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

    // region VeinMiningPickaxe 配置
    public static class VeinMinerPickaxe {

        public static int maxAmount = 327670;
        public static int maxRange = 32;
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

    // region 分类定义
    private static final String CATEGORY_TIME_VIAL = "Time_Vial";
    private static final String CATEGORY_GENERAL = "General";
    private static final String CATEGORY_VEIN_MINER_PICKAXE = "Vein_Miner_Pickaxe";
    private static final String CATEGORY_TOOL_BELT = "Tool_Belt";
    private static final String CATEGORY_ME_OUTPUT_HATCH = "ME_Output_Hatch";
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
    }
}
