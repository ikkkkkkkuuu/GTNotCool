package com.xyp.gtnc.Loader;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;

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

}
