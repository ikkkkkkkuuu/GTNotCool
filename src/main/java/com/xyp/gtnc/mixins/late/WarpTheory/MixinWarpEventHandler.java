package com.xyp.gtnc.mixins.late.WarpTheory;

import net.minecraftforge.event.entity.living.LivingEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

/**
 * 禁止 WarpTheory 独立实现的扭曲事件。
 * <p>
 * GTNH 整合包中的 WarpTheory 不调用 Thaumcraft 的 {@code WarpEvents.checkWarpEvent}，而是在
 * {@code WarpEventHandler.livingUpdate} 中独立判定、排队并执行扭曲事件。配置开启时在该处理器入口返回，
 * 可同时阻止新事件入队和已入队事件执行，并且不会修改玩家的永久、黏滞或临时扭曲值。
 */
@Pseudo
@Mixin(targets = "shukaro.warptheory.handlers.WarpEventHandler", remap = false)
public class MixinWarpEventHandler {

    @Inject(
        method = "livingUpdate(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void gtnc$disableWarpTheoryEvents(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (Config.disableWarpEvents) {
            ci.cancel();
        }
    }
}
