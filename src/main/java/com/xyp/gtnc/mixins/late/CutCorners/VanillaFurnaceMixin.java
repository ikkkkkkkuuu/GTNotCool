package com.xyp.gtnc.mixins.late.CutCorners;

import net.minecraft.tileentity.TileEntityFurnace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.xyp.gtnc.Config.Config;

/**
 * 原版熔炉(TileEntityFurnace)冶炼提速——仿 GTNH-CutCorners。
 * <p>
 * 原版熔炉每次冶炼固定 200 tick(10 秒)，该常量在冷却计时方法 {@code func_145948_k} 里。
 * 这里 {@link ModifyConstant} 把 200 按 {@link Config#getModifiedRecipeDuration(int)} 改写
 * (mode 0=不改 / 1=固定 / 2=倍率)，与 GT 机器共用同一套配方提速配置。
 * <p>
 * 用 {@code require = 0}：若该常量因映射差异未命中，本注入静默跳过、不崩游戏，其余提速不受影响。
 * <p>
 * 注意：这是原版熔炉，非 GregTech，故不 gate 在 gregtech；但通过 LateMixinsLoader 随本组注册。
 */
@Mixin(TileEntityFurnace.class)
public abstract class VanillaFurnaceMixin {

    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 200), require = 0)
    private int gtnc$modifyFurnaceCookTime(int constant) {
        if (Config.recipeSpeedMode == 0) {
            return constant;
        }
        return Config.getModifiedRecipeDuration(constant);
    }
}
