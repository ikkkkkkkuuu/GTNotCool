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
 * <b>零通量</b>：{@link TileCrucible#spill()} 是坩埚在标签溢出（{@code tagAmount() > 100}）或精炼原初 aspect 时，
 * 在坩埚上方/周围生成通量气（blockFluxGas）/通量泥（blockFluxGoo）的唯一来源，这些方块是 warp/taint 的源头。
 * {@code @Inject} 在 HEAD 直接取消整个 spill，坩埚照常工作但绝不产生通量方块。由 {@link Config#tcCrucibleNoFlux} 控制。
 * <p>
 * <b>源质不衰减</b>：{@code updateEntity} 有两处让源质减少，入口条件都读 {@code this.tagAmount()}：
 * <ul>
 * <li>ordinal 0 —— 过量溢出：{@code tagAmount() > 100} 时每 5 tick 删随机 1 个并溢出；</li>
 * <li>ordinal 1 —— 高温熵变：{@code heat > 150} 时每约 100 tick 随机侵蚀 1 个源质（复合降解成组分、基础直接删除并溢出），
 * 这是"源质随时间消失"的主因。</li>
 * </ul>
 * {@code @Redirect} 这两次 {@code tagAmount()} 调用、开启时返回 0，使两个衰减分支都进不去。加热、精炼（attemptSmelt）、
 * 计时器维护等其余逻辑不受影响。由 {@link Config#tcCrucibleNoDecay} 控制。
 */
@Mixin(value = TileCrucible.class, remap = false)
public abstract class MixinTileCrucible {

    @Inject(method = "spill", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnc$noCrucibleFlux(CallbackInfo ci) {
        if (Config.tcCrucibleNoFlux) {
            ci.cancel();
        }
    }

    /**
     * 拦截 {@code updateEntity} 内对 {@code tagAmount()} 的两次调用（过量溢出 / 高温熵变的入口判断），
     * 开关开启时返回 0，使 {@code tagAmount() > 100} 与 {@code tagAmount() > 0} 两个分支都不成立，源质不再流失。
     * <p>
     * <b>注意 {@code remap = true}</b>：{@code updateEntity} 是 Minecraft 继承来的方法，生产（混淆）环境下被混淆成 SRG 名
     * {@code func_145845_h}。类级 {@code remap = false} 会让 mixin 按字面找 {@code updateEntity} 从而在生产环境找不到目标而崩
     * （dev 环境是 MCP 名故能编译/运行、不暴露）。这里单独开 {@code remap = true} 覆盖类级设置，把方法名映射到 SRG。
     * {@code spill} 那个 {@code @Inject} 无需 remap，因为 {@code spill} 是 Thaumcraft 自定义方法名、不参与混淆；
     * {@code @At} 的 target {@code tagAmount()} 同理是 TC 方法，不在 MC 映射表里，remap 会原样保留。
     */
    @Redirect(
        method = "updateEntity",
        at = @At(value = "INVOKE", target = "Lthaumcraft/common/tiles/TileCrucible;tagAmount()I"),
        require = 2,
        remap = true)
    private int gtnc$noCrucibleDecay(TileCrucible self) {
        if (Config.tcCrucibleNoDecay) {
            return 0;
        }
        return self.tagAmount();
    }
}
