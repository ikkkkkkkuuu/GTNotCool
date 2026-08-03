package com.xyp.gtnc.utils.enums.mode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.cleanroommc.modularui.drawable.UITexture;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

/**
 * 蒸汽神锻加工模块的唯一模式定义表。
 *
 * <p>
 * 编号、语言键、RecipeMap、GUI 图标以及模式特殊行为全部集中在这里。
 * enum 的声明顺序就是 machineMode 编号：
 * </p>
 *
 * <pre>
 * 0 = LASER_ENGRAVER
 * 1 = CUTTER
 * ...
 * </pre>
 *
 * <p>
 * 以后新增模式时，只需要：
 * </p>
 *
 * <ol>
 * <li>在下面的 enum 常量列表末尾增加一项；</li>
 * <li>在语言文件中增加该语言键的翻译。</li>
 * </ol>
 *
 * <p>
 * 机器的模式数量、RecipeMap 列表、GUI 图标列表和弹窗按钮都会自动更新。
 * </p>
 */
public enum SteamGodforgeProcessingMode {

    /*
     * 新模式统一加在这里。
     * 参数顺序：
     * 语言键、RecipeMap、按钮图标、是否启用组装机坏配方保护。
     */

    // #tr gtnc.machine.steam_godforge_processing.mode.laser_engraver
    // # Laser Engraver
    // # zh_CN 激光蚀刻机
    LASER_ENGRAVER("gtnc.machine.steam_godforge_processing.mode.laser_engraver", RecipeMaps.laserEngraverRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_SCANNER, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.cutter
    // # Cutting Machine
    // # zh_CN 切割机
    CUTTER("gtnc.machine.steam_godforge_processing.mode.cutter", RecipeMaps.cutterRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_DISTILLATION_TOWER, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.bender
    // # Bending Machine
    // # zh_CN 卷板机
    BENDER("gtnc.machine.steam_godforge_processing.mode.bender", RecipeMaps.benderRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_BENDING, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.wiremill
    // # Wiremill
    // # zh_CN 线材轧机
    WIREMILL("gtnc.machine.steam_godforge_processing.mode.wiremill", RecipeMaps.wiremillRecipes,
        GTGuiTextures.OVERLAY_BUTTON_WIRELESS_ON, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.gtpp_mixer
    // # Industrial Mixer
    // # zh_CN 工业搅拌机
    GTPP_MIXER("gtnc.machine.steam_godforge_processing.mode.gtpp_mixer", GTPPRecipeMaps.mixerNonCellRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_RECYCLING, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.assembler
    // # Assembler
    // # zh_CN 组装机
    ASSEMBLER("gtnc.machine.steam_godforge_processing.mode.assembler", RecipeMaps.assemblerRecipes,
        GTGuiTextures.OVERLAY_BUTTON_ASSEMBLER_MODE, true),

    // #tr gtnc.machine.steam_godforge_processing.mode.forming_press
    // # Forming Press
    // # zh_CN 压模机
    FORMING_PRESS("gtnc.machine.steam_godforge_processing.mode.forming_press", RecipeMaps.formingPressRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_FORMING, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.fluid_solidifier
    // # Fluid Solidifier
    // # zh_CN 流体固化机
    FLUID_SOLIDIFIER("gtnc.machine.steam_godforge_processing.mode.fluid_solidifier", RecipeMaps.fluidSolidifierRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_PACKAGER, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.compressor
    // # Compressor
    // # zh_CN 压缩机
    COMPRESSOR("gtnc.machine.steam_godforge_processing.mode.compressor", RecipeMaps.compressorRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.arc_furnace
    // # Arc Furnace
    // # zh_CN 电弧炉
    ARC_FURNACE("gtnc.machine.steam_godforge_processing.mode.arc_furnace", RecipeMaps.arcFurnaceRecipes,
        GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_ARC, false),

    // #tr gtnc.machine.steam_godforge_processing.mode.industrial_arcane_assembler
    // # Industrial Arcane Assembler
    // # zh_CN 工业奥术装配室
    INDUSTRIAL_ARCANE_ASSEMBLER("gtnc.machine.steam_godforge_processing.mode.industrial_arcane_assembler",
        GTNCRecipeMaps.IndustrialShapedArcaneCraftingRecipes, GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID,
        false),

    // #tr gtnc.machine.steam_godforge_processing.mode.IndustrialInfusionCrafting
    // # Industrial Infusion Matrix
    // # zh_CN 工业注魔矩阵
    INDUSTRIAL_INFUSION_CRAFTING("gtnc.machine.steam_godforge_processing.mode.IndustrialInfusionCrafting",
        GTNCRecipeMaps.IndustrialInfusionCraftingRecipes, GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL, false);

    private static final SteamGodforgeProcessingMode[] VALUES = values();
    private static final List<RecipeMap<?>> AVAILABLE_RECIPE_MAPS;
    private static final UITexture[] MODE_ICONS;

    static {
        List<RecipeMap<?>> recipeMaps = new ArrayList<>(VALUES.length);
        UITexture[] icons = new UITexture[VALUES.length];

        for (int i = 0; i < VALUES.length; i++) {
            SteamGodforgeProcessingMode mode = VALUES[i];
            recipeMaps.add(mode.recipeMap);
            icons[i] = mode.icon;
        }

        AVAILABLE_RECIPE_MAPS = Collections.unmodifiableList(recipeMaps);
        MODE_ICONS = icons;
    }

    private final String translationKey;
    private final RecipeMap<?> recipeMap;
    private final UITexture icon;
    private final boolean strictRecipeValidation;

    SteamGodforgeProcessingMode(String translationKey, RecipeMap<?> recipeMap, UITexture icon,
        boolean strictRecipeValidation) {

        this.translationKey = translationKey;
        this.recipeMap = recipeMap;
        this.icon = icon;
        this.strictRecipeValidation = strictRecipeValidation;
    }

    /**
     * machineMode 使用的稳定编号。
     *
     * <p>
     * 编号来自 enum 声明顺序，因此不要随意调整已有常量的顺序。
     * 新模式应追加在列表末尾，避免旧存档中的模式编号改变含义。
     * </p>
     */
    public int getId() {
        return ordinal();
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public RecipeMap<?> getRecipeMap() {
        return recipeMap;
    }

    public UITexture getIcon() {
        return icon;
    }

    /**
     * 当前只用于组装机异常空输入/null 输出配方保护。
     */
    public boolean requiresStrictRecipeValidation() {
        return strictRecipeValidation;
    }

    public boolean matches(int machineMode) {
        return getId() == machineMode;
    }

    public static int count() {
        return VALUES.length;
    }

    /**
     * 非法编号回退到第一个模式。
     */
    public static SteamGodforgeProcessingMode fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return VALUES[0];
        }
        return VALUES[id];
    }

    /**
     * 用于左键/右键循环切换，支持负数。
     */
    public static int wrapId(int id) {
        int count = VALUES.length;
        int wrapped = id % count;
        return wrapped < 0 ? wrapped + count : wrapped;
    }

    public static Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return AVAILABLE_RECIPE_MAPS;
    }

    /**
     * 返回副本，防止 GUI 或外部代码改写全局图标数组。
     */
    public static UITexture[] createIconArray() {
        return MODE_ICONS.clone();
    }
}
