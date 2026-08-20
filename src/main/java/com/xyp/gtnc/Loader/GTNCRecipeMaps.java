package com.xyp.gtnc.Loader;

import java.util.Comparator;

import com.xyp.gtnc.Common.gui.recipe.FallingTowerFrontend;
import com.xyp.gtnc.Common.gui.recipe.GTNCLogoFrontend;
import com.xyp.gtnc.Common.gui.recipe.IndustrialInfusionCraftingRecipesFrontend;
import com.xyp.gtnc.Common.gui.recipe.StellarForgeAlloySmelterRecipesFrontend;
import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.recipes.format.BloodSoulFormat;
import com.xyp.gtnc.utils.recipes.metadata.FuelRefiningMetadata;

import gregtech.api.enums.Mods;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.util.GTRecipe;
import gregtech.nei.formatter.HeatingCoilSpecialValueFormatter;
import gregtech.nei.formatter.SimpleSpecialValueFormatter;

public class GTNCRecipeMaps {

    // #tr gtnc.recipe.PlatinumBasedTreatmentRecipes
    // # Platinum-Based Treatment
    // # zh_CN 铂系处理
    public static final RecipeMap<RecipeMapBackend> PlatinumBasedTreatmentRecipes = RecipeMapBuilder
        .of("gtnc.recipe.PlatinumBasedTreatmentRecipes")
        .maxIO(8, 12, 4, 4)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNCItemList.PlatinumBasedTreatment.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    // #tr gtnc.recipe.PetrochemicalPlantRecipes
    // # Petrochemical Plant
    // # zh_CN 石油化工厂
    public static final RecipeMap<RecipeMapBackend> PetrochemicalPlantRecipes = RecipeMapBuilder
        .of("gtnc.recipe.PetrochemicalPlantRecipes")
        .maxIO(4, 4, 4, 12)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(GTNCItemList.PetrochemicalPlant.get(1))
                .setMaxRecipesPerPage(1))
        .build();

    // #tr gtnc.recipe.FuelRefiningComplexRecipes
    // # Fuel Refining Complex
    // # zh_CN 燃料精炼复合体
    public static RecipeMap<RecipeMapBackend> FuelRefiningComplexRecipes = RecipeMapBuilder
        .of("gtnc.recipe.FuelRefiningComplexRecipes")
        .maxIO(4, 0, 8, 1)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(GTNCLogoFrontend::new)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.FuelRefiningComplex.get(1)))
        .neiSpecialInfoFormatter(HeatingCoilSpecialValueFormatter.INSTANCE)
        .neiRecipeComparator(
            Comparator
                .<GTRecipe, Integer>comparing(recipe -> recipe.getMetadataOrDefault(FuelRefiningMetadata.INSTANCE, 0))
                .thenComparing(GTRecipe::compareTo))
        .build();

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
        .maxIO(9, 9, 0, 3)
        .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.LargeSteamCombProcessor.get(1)))
        .frontend(GTNCLogoFrontend::new)
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

    // #tr gtnc.recipe.FallingTowerRecipes
    // # Falling Tower
    // # zh_CN 模拟坠星标位
    public static final RecipeMap<RecipeMapBackend> FallingTowerRecipes = Mods.BloodMagic.isModLoaded()
        ? RecipeMapBuilder.of("gtnc.recipe.FallingTowerRecipes")
            .maxIO(1, 81, 0, 0)
            .progressBar(GTUITextures.PROGRESSBAR_COMPRESS)
            .frontend(FallingTowerFrontend::new)
            .neiHandlerInfo(
                builder -> builder.setDisplayStack(GTNCItemList.BloodSoulSacrificialArray.get(1))
                    .setMaxRecipesPerPage(1))
            .build()
        : null;

    // #tr gtnc.recipe.BloodDemonInjectionRecipes
    // # Blood Demon Injection
    // # zh_CN 血魔注入
    public static final RecipeMap<RecipeMapBackend> BloodDemonInjectionRecipes = Mods.BloodMagic.isModLoaded()
        ? RecipeMapBuilder.of("gtnc.recipe.BloodDemonInjectionRecipes")
            .maxIO(4, 1, 0, 0)
            .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
            .frontend(GTNCLogoFrontend::new)
            .neiSpecialInfoFormatter(BloodSoulFormat.INSTANCE)
            .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.BloodSoulSacrificialArray.get(1)))
            .build()
        : null;

    // #tr gtnc.recipe.AlchemicChemistrySetRecipes
    // # Alchemic Chemistry
    // # zh_CN 秘法炼金
    public static final RecipeMap<RecipeMapBackend> AlchemicChemistrySetRecipes = Mods.BloodMagic.isModLoaded()
        ? RecipeMapBuilder.of("gtnc.recipe.AlchemicChemistrySetRecipes")
            .maxIO(5, 1, 0, 0)
            .progressBar(GTUITextures.PROGRESSBAR_ARROW_MULTIPLE)
            .frontend(GTNCLogoFrontend::new)
            .neiSpecialInfoFormatter(BloodSoulFormat.INSTANCE)
            .neiHandlerInfo(builder -> builder.setDisplayStack(GTNCItemList.BloodSoulSacrificialArray.get(1)))
            .build()
        : null;

}
