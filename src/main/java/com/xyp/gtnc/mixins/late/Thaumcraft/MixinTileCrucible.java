package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileCrucible;

/**
 * 坩埚（Crucible）的两项 QoL：不产生通量污染，以及内含源质不随时间流失。
 * <p>
 * <b>零通量</b>：{@link TileCrucible#spill()} 是坩埚的唯一通量来源。
 * {@code @Inject} 在 HEAD 取消整个 spill。由 {@link Config#tcCrucibleNoFlux} 控制。
 * <p>
 * <b>源质不衰减</b>：{@code updateEntity} 内两处衰减入口都依赖 {@code tagAmount()} 判零：
 * <ul>
 * <li>过量溢出：{@code tagAmount() > 100} 时每 5 tick 删随机 1 个并 spill</li>
 * <li>高温熵变：{@code heat > 150} 且 {@code tagAmount() > 0} 时降解复合源质/移除基础源质</li>
 * </ul>
 * {@code @Redirect tagAmount()} 在开关开启时返回 0，两处 guard 条件均不成立，衰减路径完全阻断。
 * 加热、投料精炼（attemptSmelt）、计时器等其余逻辑完全不受影响。
 * <p>
 * <b>注意</b>：类级 {@code remap = false}，当前运行时 TC jar 保持 MCP 名（非 SRG），
 * 因此所有方法/字段名均用 MCP 名：{@code updateEntity / tagAmount / spill / aspects}。
 */
@Mixin(value = TileCrucible.class, remap = false)
public abstract class MixinTileCrucible {

    @Shadow
    public AspectList aspects;

    /** 取消坩埚溢出产生的通量污染 */
    @Inject(method = "spill", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnc$noCrucibleFlux(CallbackInfo ci) {
        if (Config.tcCrucibleNoFlux) {
            ci.cancel();
        }
    }

    /**
     * 重定向 updateEntity 内所有 {@code tagAmount()} 调用。
     * 开关开启时返回 0，使两个衰减 guard（>100 和 >0）均不成立。
     */
    @Redirect(
        method = "updateEntity",
        at = @At(value = "INVOKE", target = "Lthaumcraft/common/tiles/TileCrucible;tagAmount()I"),
        require = 2)
    private int gtnc$noCrucibleDecay(TileCrucible self) {
        return Config.tcCrucibleNoDecay ? 0 : self.tagAmount();
    }
}
