package com.xyp.gtnc.utils.recipes.format;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

public final class BloodSoulFormat implements INEISpecialInfoFormatter {

    public static final BloodSoulFormat INSTANCE = new BloodSoulFormat();

    private BloodSoulFormat() {}

    @NotNull
    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        if (recipeInfo.recipe.mSpecialValue <= 0) return Collections.emptyList();
        return Collections.singletonList(
            String.format(
                StatCollector.translateToLocal("NEI.BloodSoulSacrificialArray.specialValue"),
                recipeInfo.recipe.mSpecialValue));
    }
}
