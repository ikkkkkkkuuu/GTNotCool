package com.xyp.gtnc.mixins.late.CropsNH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.xyp.gtnc.Config.Config;

/**
 * 让所有生成的种子三项属性拉满。
 *
 * SeedStats 的 growth/gain/resistance 是 final 字段，取值范围 1..31，31 为满值。
 * 所有构造器最终都委托到 (byte,byte,byte,boolean) 这个构造器，且在其内部做
 * Math.max(1, Math.min(31, x)) 的钳制。所以在该构造器入口把三个入参改成 31，
 * 字段、getter、写入 NBT 的值就全部一致为满。
 *
 * 由 Config.enableCropMaxStats 控制，默认开启。
 */
@Mixin(SeedStats.class)
public abstract class MixinSeedStats {

    @ModifyVariable(method = "<init>(BBBZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false)
    private static byte gtnc$maxGrowth(byte original) {
        return Config.enableCropMaxStats ? (byte) 31 : original;
    }

    @ModifyVariable(method = "<init>(BBBZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, remap = false)
    private static byte gtnc$maxGain(byte original) {
        return Config.enableCropMaxStats ? (byte) 31 : original;
    }

    @ModifyVariable(method = "<init>(BBBZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 2, remap = false)
    private static byte gtnc$maxResistance(byte original) {
        return Config.enableCropMaxStats ? (byte) 31 : original;
    }
}
