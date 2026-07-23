package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Config.Config;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.research.PlayerKnowledge;

/**
 * 两个 Thaumcraft 研究/扫描 QoL 注入。
 *
 * <h3>研究点免费（{@link Config#tcFreeResearchAspects}）</h3>
 * {@link PlayerKnowledge#addAspectPool(String, Aspect, short)} 是研究点池的加/减入口：
 * {@code amount < 0} 为扣减（研究台拼图小游戏消耗）。开关开启时在 HEAD 直接返回 {@code true}
 * ——谎报扣减成功，但实际不动池子，研究点永远够用。扫描加点(amount>0)照常执行。
 *
 * <h3>扫描无视父源质顺序（{@link Config#tcScanIgnoreParentAspects}）</h3>
 * {@link PlayerKnowledge#hasDiscoveredParentAspects(String, Aspect)} 是所有复合源质被发现前的前置检查：
 * 只有当组成该源质的两个父源质都已发现，才允许扫描/入池。开关开启时在 HEAD 恒返回 {@code true}，
 * 从而绕过"必须按源质合成树自底向上顺序扫"的限制，任意乱序扫描均可直接发现复合源质。
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

    /**
     * 无视父源质发现顺序——让 {@link PlayerKnowledge#hasDiscoveredParentAspects} 对任意源质恒返回 true。
     * <p>
     * 调用链：{@code ScanManager.validScan} 与 {@code ScanManager.completeScan} 都先调用此方法判断是否可扫描；
     * 原版返回 false 时客户端弹"你还不了解 xxx"并取消扫描。注入后跳过父源质要求，直接放行。
     */
    @Inject(
        method = "hasDiscoveredParentAspects(Ljava/lang/String;Lthaumcraft/api/aspects/Aspect;)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void gtnc$ignoreParentAspects(String player, Aspect aspect, CallbackInfoReturnable<Boolean> cir) {
        if (Config.tcScanIgnoreParentAspects) {
            cir.setReturnValue(true);
        }
    }
}
