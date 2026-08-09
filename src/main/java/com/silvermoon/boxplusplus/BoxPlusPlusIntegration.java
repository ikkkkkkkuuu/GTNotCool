package com.silvermoon.boxplusplus;

import com.silvermoon.boxplusplus.common.loader.BlockRegister;
import com.silvermoon.boxplusplus.common.loader.ItemRegister;
import com.silvermoon.boxplusplus.common.loader.RecipeLoader;
import com.silvermoon.boxplusplus.common.loader.TileEntitiesLoader;
import com.silvermoon.boxplusplus.network.NetworkLoader;
import com.silvermoon.boxplusplus.util.ResultModuleRequirement;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;

public final class BoxPlusPlusIntegration {

    private static boolean preInitDone;
    private static boolean initDone;
    private static boolean loadCompleteDone;

    private BoxPlusPlusIntegration() {}

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInitDone) return;
        preInitDone = true;
        BlockRegister.register();
        ItemRegister.register();
        NetworkLoader.init();
    }

    public static void init(FMLInitializationEvent event) {
        if (initDone) return;
        initDone = true;
        TileEntitiesLoader.register();
        CheckRecipeResultRegistry.register(new ResultModuleRequirement(0, false));
    }

    public static void loadComplete(FMLLoadCompleteEvent event) {
        if (loadCompleteDone) return;
        loadCompleteDone = true;
        new RecipeLoader().run();
    }
}
