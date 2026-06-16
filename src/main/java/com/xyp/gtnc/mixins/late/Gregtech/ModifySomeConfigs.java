package com.xyp.gtnc.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import gregtech.GTMod;
import gregtech.loaders.preload.GTPreLoad;

@SuppressWarnings("UnusedMixin")
/**
 * GT 客户端配置强制启用
 * <p>
 * 在 GT 客户端配置加载完成后，根据本模组配置强制启用以下功能：
 * <ul>
 * <li>{@code mNEIRecipeOwner} → NEI 显示配方所属模组</li>
 * <li>{@code wailaAverageNS} → Waila 显示平均耗时</li>
 * <li>{@code mNEIOriginalVoltage} → NEI 显示原始电压等级</li>
 * </ul>
 */
@Mixin(value = GTPreLoad.class, remap = false)
public class ModifySomeConfigs {

    @Inject(
        method = "loadClientConfig",
        at = @At(value = "INVOKE", target = "Lgregtech/common/GTProxy;reloadNEICache()V", shift = At.Shift.BEFORE),
        require = 1)
    private static void nhu$modification(CallbackInfo ci) {
        if (Config.enableAlwaysDisplayRecipeOwner) GTMod.gregtechproxy.mNEIRecipeOwner = true;
        if (Config.enableAlwaysDisplayWailaAverageNS) GTMod.gregtechproxy.wailaAverageNS = true;
        if (Config.enableAlwaysDisplayNEIOriginalVoltage) GTMod.gregtechproxy.mNEIOriginalVoltage = true;
    }
}
