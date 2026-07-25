package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileCrucible;

/**
 * 坩埚（Crucible）的两项 QoL：不产生通量污染，以及内含源质不随时间流失。
 * <p>
 * <b>零通量</b>：{@link TileCrucible#spill()} 是坩埚在标签溢出或精炼原初 aspect 时生成通量气/通量泥的唯一来源。
 * {@code @Inject} 在 HEAD 直接取消整个 spill，坩埚照常工作但绝不产生通量方块。由 {@link Config#tcCrucibleNoFlux} 控制。
 * <p>
 * <b>源质不衰减</b>：{@code func_145845_h}（MCP 名 updateEntity）内有两处会让源质减少：
 * <ul>
 * <li>过量溢出（{@code tagAmount() > 100} 时每 5 tick 删随机 1 个并 spill）；</li>
 * <li>高温熵变（{@code heat > 150} 时每约 100 tick 删 1 个，复合降解、基础 spill）。</li>
 * </ul>
 * 之前的 {@code @Redirect tagAmount()} 方案虽理论上应阻断 guard 条件，但实际无效。
 * 改用更彻底的 save/restore：HEAD 保存 {@code this.aspects}、TAIL 恢复，
 * 无论方法内以何种方式修改源质都会被撤销。加热、精炼、计时器等其余逻辑不受影响。
 * <p>
 * <b>注意</b>：运行时 TC jar 为 SRG 混淆态（{@code func_145845_h}），类级 {@code remap = false}
 * 所以必须直接用 SRG 名而非 MCP 名。
 */
@Mixin(value = TileCrucible.class, remap = false)
public abstract class MixinTileCrucible {

    /** 用于在 func_145845_h 入口暂存源质列表 */
    private AspectList sciencenotcool$savedAspects;

    @Shadow
    public AspectList aspects;

    @Inject(method = "spill", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnc$noCrucibleFlux(CallbackInfo ci) {
        if (Config.tcCrucibleNoFlux) {
            ci.cancel();
        }
    }

    /**
     * 在 func_145845_h（updateEntity）入口快照当前 aspects。
     * 配合下面的 TAIL 注入实现"源质自动恢复"。
     */
    @Inject(method = "func_145845_h", at = @At("HEAD"))
    private void gtnc$snapshotAspects(CallbackInfo ci) {
        if (Config.tcCrucibleNoDecay) {
            this.sciencenotcool$savedAspects = this.aspects.copy();
        }
    }

    /**
     * 在 func_145845_h（updateEntity）尾部恢复为入口快照，使本 tick 内的所有源质修改无效化。
     * <p>
     * 注意：{@code attemptSmelt} 在物品落入坩埚时独立调用（不在 func_145845_h 内），
     * 故精炼配方消耗源质不受影响。
     */
    @Inject(method = "func_145845_h", at = @At("TAIL"))
    private void gtnc$restoreAspects(CallbackInfo ci) {
        if (Config.tcCrucibleNoDecay && this.sciencenotcool$savedAspects != null) {
            this.aspects = this.sciencenotcool$savedAspects;
            this.sciencenotcool$savedAspects = null;
        }
    }
}
