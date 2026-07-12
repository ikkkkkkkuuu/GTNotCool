package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import thaumcraft.common.tiles.TileCrucible;

/**
 * 坩埚（Crucible）不再产生通量污染。
 * <p>
 * {@link TileCrucible#spill()} 是坩埚在标签溢出（{@code tagAmount() > 100}）或精炼原初 aspect 时，
 * 在坩埚上方/周围生成通量气（blockFluxGas）/通量泥（blockFluxGoo）的唯一来源，这些方块是 warp/taint 的源头。
 * 这里 {@code @Inject} 在 HEAD 直接取消整个 spill，坩埚照常工作但绝不产生通量方块。
 * <p>
 * 由 {@link Config#tcCrucibleNoFlux} 控制，默认开启。
 */
@Mixin(value = TileCrucible.class, remap = false)
public abstract class MixinTileCrucible {

    @Inject(method = "spill", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnc$noCrucibleFlux(CallbackInfo ci) {
        if (Config.tcCrucibleNoFlux) {
            ci.cancel();
        }
    }
}
