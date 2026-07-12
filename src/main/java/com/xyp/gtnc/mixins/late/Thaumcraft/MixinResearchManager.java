package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Config.Config;

import thaumcraft.common.lib.research.ResearchManager;

/**
 * 全解锁研究：{@link ResearchManager#isResearchComplete(String, String)} 对任意键恒返回 true。
 * <p>
 * <b>默认关闭</b>（{@link Config#tcUnlockAllResearch}）——这会让研究笔记本 GUI 误以为一切已完成，可能显示异常。
 * 只想省去研究小游戏的话，用 {@link MixinPlayerKnowledge}（研究点数不消耗）即可，不破坏 GUI。
 */
@Mixin(value = ResearchManager.class, remap = false)
public class MixinResearchManager {

    @Inject(method = "isResearchComplete", at = @At("HEAD"), cancellable = true, remap = false)
    private static void gtnc$unlockAllResearch(String playername, String key, CallbackInfoReturnable<Boolean> cir) {
        if (Config.tcUnlockAllResearch) {
            cir.setReturnValue(true);
        }
    }
}
