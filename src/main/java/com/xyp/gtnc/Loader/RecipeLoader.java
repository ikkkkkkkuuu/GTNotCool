package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Common.recipe.gregtech.AssemblerRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.BenderRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.FurnaceRecipes;
import com.xyp.gtnc.Common.recipe.gregtech.LaserEngraverRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.CombProcessingRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.CraftingTableRecipes;
import com.xyp.gtnc.Common.recipe.gtnc.OreProcessingRecipes;

public class RecipeLoader {

    public static void loadRecipes() {

        BenderRecipes.loadRecipes();
        CraftingTableRecipes.loadRecipes();
        AssemblerRecipes.loadRecipes();
        OreProcessingRecipes.loadOreProcessingRecipes();
        FurnaceRecipes.loadRecipes();
        LaserEngraverRecipes.loadRecipes();
        CombProcessingRecipes.loadRecipes();
    }

}
