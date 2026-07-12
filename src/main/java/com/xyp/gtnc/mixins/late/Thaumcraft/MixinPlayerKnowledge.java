package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Config.Config;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.research.PlayerKnowledge;

/**
 * 让研究点数（aspects/观察点）永不因研究消耗而减少，实现"研究免费"但不破坏研究台 GUI。
 * <p>
 * {@link PlayerKnowledge#addAspectPool(String, Aspect, short)} 是研究点池的加/减入口：
 * {@code amount > 0} 时向池中加点（扫描获得），{@code amount < 0} 时扣点（研究台拼图小游戏消耗）。
 * 这里 {@code @Inject} 在 HEAD：开关开启且 {@code amount < 0}（扣减）时直接返回 {@code true}
 * ——即"扣减已成功"骗过调用方，但实际不动池子，于是研究点永远够用。
 * <p>
 * 只拦截扣减分支，加点(amount>0)照常执行，扫描仍能正常涨点、发现新 aspect。
 * <p>
 * 由 {@link Config#tcFreeResearchAspects} 控制，默认开启。
 */
@Mixin(value = PlayerKnowledge.class, remap = false)
public abstract class MixinPlayerKnowledge {

    @Inject(
        method = "addAspectPool(Ljava/lang/String;Lthaumcraft/api/aspects/Aspect;S)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void gtnc$freeResearchAspects(String username, Aspect aspect, short amount,
        CallbackInfoReturnable<Boolean> cir) {
        if (Config.tcFreeResearchAspects && aspect != null && amount < 0) {
            // 谎报扣减成功，但不真正减少池中点数。
            cir.setReturnValue(true);
        }
    }
}
