package com.xyp.gtnc.utils.nei;

import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.Tags;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import gregtech.api.recipe.RecipeMaps;
import tectech.recipe.TecTechRecipeMaps;

/** Main GTNC NEI integration entry point. Keep machine recipe-page bindings here. */
@SuppressWarnings("unused")
public final class NEIGTNCConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        registerSteamGodforgeCatalysts();
    }

    private static void registerSteamGodforgeCatalysts() {
        // Old-style NEI discovery requires this class name to start with "NEI" and end with "Config". Register
        // subclassed modules explicitly because they are not RecipeMapWorkable machines discovered by GT's NEI code.
        API.addRecipeCatalyst(
            GTNCItemList.SteamGodforgeSmeltingModule.get(1),
            RecipeMaps.blastFurnaceRecipes.unlocalizedName);
        API.addRecipeCatalyst(GTNCItemList.SteamGodforgeSmeltingModule.get(1), new FurnaceRecipeHandler());
        API.addRecipeCatalyst(
            GTNCItemList.SteamGodforgeMoltenModule.get(1),
            TecTechRecipeMaps.godforgeMoltenRecipes.unlocalizedName);
        API.addRecipeCatalyst(
            GTNCItemList.SteamGodforgePlasmaModule.get(1),
            TecTechRecipeMaps.godforgePlasmaRecipes.unlocalizedName);
        API.addRecipeCatalyst(
            GTNCItemList.SteamGodforgeExoticModule.get(1),
            TecTechRecipeMaps.godforgeExoticMatterRecipes.unlocalizedName);
    }

    @Override
    public String getName() {
        return ScienceNotCool.MODNAME;
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
