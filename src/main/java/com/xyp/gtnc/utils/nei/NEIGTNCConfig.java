package com.xyp.gtnc.utils.nei;

import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.Tags;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import tectech.recipe.TecTechRecipeMaps;

/** Main GTNC NEI integration entry point. Keep machine recipe-page bindings here. */
@SuppressWarnings("unused")
public final class NEIGTNCConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        registerSteamElevatorCatalysts();
    }

    private static void registerSteamElevatorCatalysts() {
        // Old-style NEI discovery requires this class name to start with "NEI" and end with "Config". Register
        // subclassed modules explicitly because they are not RecipeMapWorkable machines discovered by GT's NEI code.
        API.addRecipeCatalyst(
            GTNCItemList.SteamPlasmaModule.get(1),
            TecTechRecipeMaps.godforgePlasmaRecipes.unlocalizedName);
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
