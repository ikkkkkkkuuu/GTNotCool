package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Common.recipe.gregtech.AssemblerRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.AssemblingLineRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.BenderRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.FurnaceRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.LaserEngraverRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.CombProcessingRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.CraftingTableRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.CrucibleRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.DrillingRigRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.GeneralChemicalFactoryRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.MiningRigRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.OreProcessingRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.StellarForgeRecipePool;
import com.xyp.gtnc.Common.recipe.machine.AssemblerMatrixRecipes;
import com.xyp.gtnc.Common.recipe.machine.MEBridgeRecipes;
import com.xyp.gtnc.Common.recipe.machine.MiracleDoorRecipes;
import com.xyp.gtnc.Common.recipe.machine.QuantumComputerRecipes;

public class RecipeLoader {

    public static void loadRecipes() {

        AssemblingLineRecipes.loadrecipes();
        DrillingRigRecipes.loadRecipes();
        MiningRigRecipes.loadRecipes();
        GeneralChemicalFactoryRecipes.loadRecipes();
        CrucibleRecipes.loadRecipes();
        BenderRecipes.loadRecipes();
        CraftingTableRecipes.loadRecipes();
        AssemblerRecipes.loadRecipes();
        AssemblerMatrixRecipes.loadRecipes();
        QuantumComputerRecipes.loadRecipes();
        OreProcessingRecipes.loadOreProcessingRecipes();
        FurnaceRecipes.loadRecipes();
        LaserEngraverRecipes.loadRecipes();
        CombProcessingRecipes.loadRecipes();
        MiracleDoorRecipes.loadRecipes();
        StellarForgeRecipePool.loadRecipes();
        MEBridgeRecipes.loadRecipes();
    }

    /**
     * Guards the one-time generation of the Miracle Door (Stellar Forge) recipes, which are produced by scanning live
     * EBF / GTPP ABS recipe maps. These are the only recipes generated at {@link FMLServerStartingEvent}, which fires
     * exclusively in the (dedicated) server JVM. On a client connected to a dedicated server that event never fires, so
     * without a client-side trigger the client's StellarForge recipe maps stay empty and NEI shows no recipes (only the
     * multiblock structure preview, which is registered at RecipeMap build time). The client therefore also calls this
     * from {@code FMLLoadCompleteEvent} (see ClientProxy). The flag makes it idempotent so single-player (where both
     * events fire in the same JVM) does not add the recipes twice. FMLServerStartingEvent fires only in the (dedicated)
     * server JVM; a client connected to a dedicated server relies on the FMLLoadCompleteEvent path in ClientProxy.
     */
    private static boolean stellarForgeGenerated = false;

    public static void loadRecipesServerStarted()

    {
        if (stellarForgeGenerated) return;
        stellarForgeGenerated = true;

        StellarForgeRecipePool.loadOnServerStarted();

    }

}
