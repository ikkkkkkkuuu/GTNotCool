package com.xyp.gtnc.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Config.Config;

import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

/**
 * ME输出仓/总线基类缓存容量修改
 * <p>
 * 注入 {@link MTEHatchOutputMEBase}，同时影响物品和流体输出：
 * <ul>
 * <li>{@code getCacheCapacity} → 返回 Long.MAX_VALUE（无限缓存）</li>
 * <li>{@code hasAvailableSpace} → 始终返回 true（跳过空间检测）</li>
 * </ul>
 */
@Mixin(value = MTEHatchOutputMEBase.class, remap = false)
public abstract class MEOutputHatchCapacityMixin<T, F, I> {

    /**
     * @author eyeofharmonybuffer
     * @reason 修改缓存容量为无限
     */
    @Inject(method = "getCacheCapacity", at = @At("HEAD"), cancellable = true)
    private void onGetCacheCapacity(CallbackInfoReturnable<Long> cir) {
        // 检查实际类型来决定使用哪个配置
        // 由于泛型擦除,我们需要通过其他方式判断
        // 这里我们同时检查两个配置,任意一个启用就返回无限容量
        if (Config.OutPutHatchMEEnable || Config.OutPutBusMEEnable) {
            cir.setReturnValue(Long.MAX_VALUE);
            cir.cancel();
        }
    }

    /**
     * @author eyeofharmonybuffer
     * @reason 跳过空间检测逻辑
     */
    @Inject(method = "hasAvailableSpace", at = @At("HEAD"), cancellable = true)
    private void onHasAvailableSpace(CallbackInfoReturnable<Boolean> cir) {
        if (Config.OutPutHatchMEEnable || Config.OutPutBusMEEnable) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
