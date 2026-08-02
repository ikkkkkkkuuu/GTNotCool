package com.xyp.gtnc.utils.recipes.metadata;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;

import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SolorMuonCatalystMetadata extends RecipeMetadataKey<Boolean> {

    public static final SolorMuonCatalystMetadata INSTANCE = new SolorMuonCatalystMetadata();

    private SolorMuonCatalystMetadata() {
        super(Boolean.class, "solor_muon");
    }

    @Override
    public void drawInfo(RecipeDisplayInfo recipeInfo, @Nullable Object value) {
        boolean needRing = cast(value, false);
        // #tr SolorMuonCatalystMetadata.0
        // # Need three rings and all upgrades
        // # zh_CN 需要神锻三环和全部升级
        if (needRing) recipeInfo.drawText(StatCollector.translateToLocal("SolorMuonCatalystMetadata.0"));
    }
}
