package com.xyp.gtnc.mixins.late.CutCorners;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.xyp.gtnc.Config.Config;

import gregtech.common.tileentities.machines.steam.MTESteamFurnaceSteel;

/**
 * 钢制蒸汽熔炉(MTESteamFurnaceSteel)冶炼提速——仿 GTNH-CutCorners。
 * <p>
 * 与青铜蒸汽炉同理：{@code checkRecipe()} 硬编码 {@code mMaxProgresstime = 128}，不走 RecipeMap 时长，
 * 需单独 hook。这里 {@link ModifyConstant} 把 128 按 {@link Config#getModifiedRecipeDuration(int)} 改写。
 * <p>
 * {@code require = 0}：常量未命中时静默跳过、不崩。
 */
@Mixin(value = MTESteamFurnaceSteel.class, remap = false)
public abstract class SteamFurnaceSteelMixin {

    @ModifyConstant(method = "checkRecipe", constant = @Constant(intValue = 128), remap = false, require = 0)
    private int gtnc$modifySteamFurnaceTime(int constant) {
        if (Config.recipeSpeedMode == 0) {
            return constant;
        }
        return Config.getModifiedRecipeDuration(constant);
    }
}
