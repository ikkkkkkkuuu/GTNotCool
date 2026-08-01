package com.xyp.gtnc.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class FuelRefiningMetadata extends RecipeMetadataKey<Integer> {

    public static final FuelRefiningMetadata INSTANCE = new FuelRefiningMetadata();

    private FuelRefiningMetadata() {
        super(Integer.class, "fuel_refining_tier");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        // #tr GTNC.recipe.fuel_refining_tier
        // # Requires Field Restriction Coil Tier: %s
        // # zh_CN 需要场约束线圈等级：%s
        recipeInfo.drawText(StatCollector.translateToLocalFormatted("GTNC.recipe.fuel_refining_tier", cast(value, 0)));
    }
}
