package com.xyp.gtnc.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Config.Config;

import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.me.cache.PathGridCache;

/**
 * 解除 AE2 无控制器(ad-hoc)网络的频道上限。
 * <p>
 * {@link PathGridCache#calculateAdHocChannels()} 里
 * {@code final int maxChannels = AEConfig.instance.isFeatureEnabled(AEFeature.Channels) ? 8 : Integer.MAX_VALUE;}
 * ——Channels 功能开启时 ad-hoc 网络最多 8 频道，否则无限。这里重定向那次
 * {@code isFeatureEnabled(Channels)}，开关开启时返回 {@code false}，使上限取 {@code Integer.MAX_VALUE}。
 * <p>
 * 有控制器网络的上限走 {@link MixinGridNodeChannels}(另一处 {@code getCompressedChannelsIndex} 的同名判定)。
 * 两处只改频道计数，不动方块注册(那由 {@code BlockStorageReshuffle.setFeature} 单独持有 Channels 功能位)。
 * <p>
 * 由 {@link Config#disableAE2ChannelLimit} 控制，默认开启。幂等：AE2 本就关闭 Channels 时该调用已返回 false。
 */
@Mixin(value = PathGridCache.class, remap = false)
public class MixinPathGridCacheChannels {

    @Redirect(
        method = "calculateAdHocChannels",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/AEConfig;isFeatureEnabled(Lappeng/core/features/AEFeature;)Z"),
        remap = false,
        require = 1)
    private boolean gtnc$unlimitedAdHocChannels(AEConfig config, AEFeature feature) {
        if (Config.disableAE2ChannelLimit && feature == AEFeature.Channels) {
            return false;
        }
        return config.isFeatureEnabled(feature);
    }
}
