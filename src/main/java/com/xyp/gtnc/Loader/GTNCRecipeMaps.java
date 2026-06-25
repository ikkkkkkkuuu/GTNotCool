package com.xyp.gtnc.Loader;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.recipe.maps.LargeNEIFrontend;
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
        .frontend(LargeNEIFrontend::new)
        .build();

    // #tr gtnc.recipe.GeneralChemicalFactory
    // # General Chemical Factory
    // # zh_CN 通用化工厂
    public static RecipeMap<RecipeMapBackend> GeneralChemicalFactoryRecipes = RecipeMapBuilder
        .of("gtnc.recipe.GeneralChemicalFactory")
        .maxIO(12, 12, 9, 9)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(LargeNEIFrontend::new)
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
        .frontend(LargeNEIFrontend::new)
        .build();

}
