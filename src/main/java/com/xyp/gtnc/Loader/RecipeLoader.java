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

    public static void loadRecipesServerStarted()

    {

        StellarForgeRecipePool.loadOnServerStarted();

    }

}
