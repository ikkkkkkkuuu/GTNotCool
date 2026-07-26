package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import thaumcraft.common.tiles.TileCrucible;

/**
 * 坩埚（Crucible）的两项 QoL：不产生通量污染，以及内含源质不随时间流失。
 * <p>
 * <b>零通量</b>：{@link TileCrucible#spill()} 是坩埚在标签溢出或精炼原初 aspect 时生成通量气/通量泥的唯一来源。
 * {@code @Inject} 在 HEAD 直接取消整个 spill，坩埚照常工作但绝不产生通量方块。由 {@link Config#tcCrucibleNoFlux} 控制。
 * <p>
 * <b>源质不衰减</b>：{@code updateEntity} 内有两处会让源质减少：
 * <ul>
 * <li>过量溢出（{@code tagAmount() > 100} 时每 5 tick 删随机 1 个并 spill）；</li>
 * <li>高温熵变（{@code heat > 150} 时每约 100 tick 删 1 个，复合降解、基础 spill）。</li>
 * </ul>
 * 直接重定向这两处 guard 调用，在源头跳过衰减逻辑，避免先扣除再恢复时向客户端同步错误数据。
 */
@Mixin(value = TileCrucible.class, remap = false)
public abstract class MixinTileCrucible {

    @Inject(method = "spill", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnc$noCrucibleFlux(CallbackInfo ci) {
        if (Config.tcCrucibleNoFlux) {
            ci.cancel();
        }
    }

    @Redirect(
        method = "updateEntity",
        at = @At(value = "INVOKE", target = "Lthaumcraft/common/tiles/TileCrucible;tagAmount()I"),
        require = 2)
    private int gtnc$skipCrucibleDecayChecks(TileCrucible crucible) {
        return Config.tcCrucibleNoDecay ? 0 : crucible.tagAmount();
    }
}
