package com.xyp.gtnc.mixins.late.Forestry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Config.Config;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.apiculture.genetics.Bee;

/**
 * 让特殊产物(specialty products)不再受气候匹配(jubilance)限制。
 * <p>
 * 原版 {@code Bee.produceStacks} 只有在 {@code primary.isJubilant(...) && secondary.isJubilant(...)}
 * 成立时才产出特殊产物，而 {@link forestry.apiculture.genetics.JubilanceDefault} 要求蜂箱所在气候
 * (温度+湿度)与蜜蜂原生气候完全一致。普通蜂箱无法控制气候，因此常常拿不到特产。
 * <p>
 * 这里重定向 produceStacks 内的 isJubilant 调用：开关开启时一律视为 jubilant，
 * 于是任何蜂箱(含普通蜂箱)在任意气候下都能掉落特产。仅影响特产判定，不改动其他逻辑。
 * <p>
 * 由 {@link Config#enableBeeAlwaysJubilant} 控制，默认开启。
 */
@Mixin(Bee.class)
public abstract class MixinBee {

    @Redirect(
        method = "produceStacks",
        at = @At(
            value = "INVOKE",
            target = "Lforestry/api/apiculture/IAlleleBeeSpecies;isJubilant(Lforestry/api/apiculture/IBeeGenome;Lforestry/api/apiculture/IBeeHousing;)Z"),
        remap = false)
    private boolean gtnc$alwaysJubilant(IAlleleBeeSpecies species, IBeeGenome genome, IBeeHousing housing) {
        if (Config.enableBeeAlwaysJubilant) return true;
        return species.isJubilant(genome, housing);
    }
}
