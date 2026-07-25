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
 * <b>源质不衰减</b>：{@code func_145845_h}（MCP 名 updateEntity）有两处让源质减少，入口条件都读 {@code this.tagAmount()}：
 * <ul>
 * <li>ordinal 0 —— 过量溢出：{@code tagAmount() > 100} 时每 5 tick 删随机 1 个并溢出；</li>
 * <li>ordinal 1 —— 高温熵变：{@code heat > 150} 时每约 100 tick 随机侵蚀 1 个源质（复合降解成组分、基础直接删除并溢出），
 * 这是"源质随时间消失"的主因。</li>
 * </ul>
 * {@code @Redirect} 这两次 {@code tagAmount()} 调用、开启时返回 0，使两个衰减分支都进不去。加热、精炼（attemptSmelt）、
 * 计时器维护等其余逻辑不受影响。由 {@link Config#tcCrucibleNoDecay} 控制。
 * <p>
 * <b>注意</b>：运行时 TC jar 为 SRG 混淆态（{@code func_145845_h}），类级 {@code remap = false}
 * 所以必须直接用 SRG 名。编译时 dev jar 中是 MCP 名 {@code updateEntity}，但 Mixin 只在加载时匹配。
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
     * 拦截 {@code func_145845_h}（即 updateEntity）内对 {@code tagAmount()} 的两次调用（过量溢出 / 高温熵变的入口判断），
     * 开关开启时返回 0，使 {@code tagAmount() > 100} 与 {@code tagAmount() > 0} 两个分支都不成立，源质不再流失。
     * <p>
     * 类级 {@code remap = false} 且运行时 TC jar 为 SRG 混淆态（未 deobf），所以必须用 SRG 名 {@code func_145845_h}
     * 而非 MCP 名 {@code updateEntity}。{@code tagAmount()} 是 TC 自定义方法，不参与混淆，直接用原名。
     */
    @Redirect(
        method = "func_145845_h",
        at = @At(value = "INVOKE", target = "Lthaumcraft/common/tiles/TileCrucible;tagAmount()I"),
        require = 2)
    private int gtnc$noCrucibleDecay(TileCrucible self) {
        if (Config.tcCrucibleNoDecay) {
            return 0;
        }
        return self.tagAmount();
    }
}
