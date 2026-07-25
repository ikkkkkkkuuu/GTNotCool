package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Common.gui.recipe.GTNCLogoFrontend;
import com.xyp.gtnc.Common.gui.recipe.IndustrialInfusionCraftingRecipesFrontend;
import com.xyp.gtnc.Common.gui.recipe.StellarForgeAlloySmelterRecipesFrontend;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.nei.formatter.SimpleSpecialValueFormatter;
import gtPlusPlus.api.recipe.QuantumForceTransformerFrontend;

public class GTNCRecipeMaps {

    // #tr gtnc.recipe.OreProcessingRecipes
    // # Ore Processing
    // # zh_CN 矿石处理
    public static RecipeMap<RecipeMapBackend> OreProcessingRecipes = RecipeMapBuilder
        .of("gtnc.recipe.OreProcessingRecipes")
        .maxIO(1, 9, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.LargeOreProcessor.get(1)))
        .build();

    // #tr gtnc.recipe.SteamCombProcessingRecipes
    // # Steam Comb Processing
    // # zh_CN 蜂窝处理
    public static RecipeMap<RecipeMapBackend> SteamCombProcessingRecipes = RecipeMapBuilder
        .of("gtnc.recipe.SteamCombProcessingRecipes")
        .maxIO(9, 9, 9, 9)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.LargeSteamCombProcessor.get(1)))
        .frontend(QuantumForceTransformerFrontend::new)
        .build();

    // #tr gtnc.recipe.SteamCrucibleRecipes
    // # Crucible
    // # zh_CN 坩埚
    public static RecipeMap<RecipeMapBackend> SteamCrucibleRecipes = RecipeMapBuilder
        .of("gtnc.recipe.SteamCrucibleRecipes")
        .maxIO(6, 1, 6, 6)
        // #tr value.crucible_tier
        // # Requires Crucible Tier: %s
        // # zh_CN 需要坩埚等级：%s
        .neiSpecialInfoFormatter(new SimpleSpecialValueFormatter("value.crucible_tier"))
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .build();

    // #tr gtnc.recipe.GeneralChemicalFactory
    // # General Chemical Factory
    // # zh_CN 通用化工厂
    public static RecipeMap<RecipeMapBackend> GeneralChemicalFactoryRecipes = RecipeMapBuilder
        .of("gtnc.recipe.GeneralChemicalFactory")
        .maxIO(12, 12, 9, 9)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .build();

    // #tr gtnc.recipe.DrillingRigRecipes
    // # Drilling Rig
    // # zh_CN 钻井平台
    public static RecipeMap<RecipeMapBackend> DrillingRigRecipes = RecipeMapBuilder.of("gtnc.recipe.DrillingRigRecipes")
        .maxIO(6, 6, 6, 6)
        // #tr value.drilling_tier
        // # Requires Drilling Tier: %s
        // # zh_CN 需要管道等级：%s
        .neiSpecialInfoFormatter(new SimpleSpecialValueFormatter("value.drilling_tier"))
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .build();

    // #tr gtnc.recipe.MiningRigRecipes
    // # Mining Rig
    // # zh_CN 矿机平台
    public static RecipeMap<RecipeMapBackend> MiningRigRecipes = RecipeMapBuilder.of("gtnc.recipe.MiningRigRecipes")
        .maxIO(1, 9, 1, 0)
        .neiSpecialInfoFormatter(new SimpleSpecialValueFormatter("value.drilling_tier"))
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .build();

    // #tr gtnc.recipe.StellarForgeRecipes
    // # Miracle Door
    // # zh_CN 恒星锻炉
    public static RecipeMap<RecipeMapBackend> StellarForgeRecipes = RecipeMapBuilder
        .of("gtnc.recipe.StellarForgeRecipes")
        .maxIO(8, 8, 1, 2)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.MiracleDoor.get(1)))
        .useSpecialSlot()
        .build();

    // #tr gtnc.recipe.StellarForgeAlloySmelterRecipes
    // # Miracle Door
    // # zh_CN 恒星锻炉:合金冶炼
    public static RecipeMap<RecipeMapBackend> StellarForgeAlloySmelterRecipes = RecipeMapBuilder
        .of("gtnc.recipe.StellarForgeAlloySmelterRecipes")
        .maxIO(10, 12, 3, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(StellarForgeAlloySmelterRecipesFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.MiracleDoor.get(1)))
        .useSpecialSlot()
        .build();

    // #tr gtnc.recipe.IndustrialShapedArcaneCraftingRecipes
    // # Industrial Arcane Assembler
    // # zh_CN 工业奥术装配室
    public static RecipeMap<RecipeMapBackend> IndustrialShapedArcaneCraftingRecipes = RecipeMapBuilder
        .of("gtnc.recipe.IndustrialShapedArcaneCraftingRecipes")
        .maxIO(9, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.IndustrialArcaneAssembler.get(1)))
        .build();

    // #tr gtnc.recipe.IndustrialInfusionCraftingRecipes
    // # Industrial Infusion Matrix
    // # zh_CN 工业注魔矩阵
    public static RecipeMap<RecipeMapBackend> IndustrialInfusionCraftingRecipes = RecipeMapBuilder
        .of("gtnc.recipe.IndustrialInfusionCraftingRecipes")
        .maxIO(25, 1, 0, 0)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(IndustrialInfusionCraftingRecipesFrontend::new)
        .neiTransferRect(100, 45, 18, 72)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNCItemList.IndustrialArcaneAssembler.get(1))
                .setMaxRecipesPerPage(1))
        .build();

}
