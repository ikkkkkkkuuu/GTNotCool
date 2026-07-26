package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import thaumcraft.common.tiles.TileCrucible;

/**
 * 专用服务器坩埚优化：阻止通量生成与源质自然流失。
 * 客户端不注册此 Mixin，由服务端同步坩埚状态。
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
        require = 2,
        remap = true)
    private int gtnc$skipCrucibleDecayChecks(TileCrucible crucible) {
        return Config.tcCrucibleNoDecay ? 0 : crucible.tagAmount();
    }
}
