package com.xyp.gtnc.mixins.late.CutCorners;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.xyp.gtnc.Config.Config;

import gregtech.common.tileentities.machines.steam.MTESteamFurnaceBronze;

/**
 * 青铜蒸汽熔炉(MTESteamFurnaceBronze)冶炼提速——仿 GTNH-CutCorners。
 * <p>
 * 594 里蒸汽炉不走 RecipeMap 时长：其 {@code checkRecipe()} 直接<b>硬编码</b>
 * {@code mMaxProgresstime = 256}，故 RecipeSpeedMixin(改 mDuration)覆盖不到它，需单独 hook。
 * 这里 {@link ModifyConstant} 把 checkRecipe 里的 256 按 {@link Config#getModifiedRecipeDuration(int)} 改写。
 * <p>
 * {@code require = 0}：常量未命中时静默跳过、不崩。
 */
@Mixin(value = MTESteamFurnaceBronze.class, remap = false)
public abstract class SteamFurnaceBronzeMixin {

    @ModifyConstant(method = "checkRecipe", constant = @Constant(intValue = 256), remap = false, require = 0)
    private int gtnc$modifySteamFurnaceTime(int constant) {
        if (Config.recipeSpeedMode == 0) {
            return constant;
        }
        return Config.getModifiedRecipeDuration(constant);
    }
}
