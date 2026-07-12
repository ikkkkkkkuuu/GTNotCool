package com.xyp.gtnc.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Config.Config;

import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.me.GridNode;

/**
 * 解除 AE2「有控制器」网络的频道(channel)限制，做法与 AE2 原生「无频道模式」完全一致。
 * <p>
 * rv3-977 中，节点的频道上限来自 {@code GridNode.getMaxChannels()} =
 * {@code CHANNEL_COUNT[compressedData & 3]}，而 {@code compressedData} 的低 2 位由
 * {@code getCompressedChannelsIndex()} 计算：
 *
 * <pre>
 * 
 * private int getCompressedChannelsIndex() {
 *     if (!AEConfig.instance.isFeatureEnabled(AEFeature.Channels)) return 3; // → CHANNEL_COUNT[3] = Integer.MAX_VALUE
 *     if (hasFlag(CANNOT_CARRY)) return 0;
 *     return hasFlag(DENSE_CAPACITY) ? 2 : 1; // → 8 / 32
 * }
 * </pre>
 *
 * 这里 {@link Redirect} 该方法内对 {@code isFeatureEnabled(AEFeature.Channels)} 的调用：开关开启时强制返回
 * {@code false}，于是走 AE2 自己的 index=3 分支，节点上限变为 {@code Integer.MAX_VALUE}——即无限频道。
 * <p>
 * 只作用于 {@code getCompressedChannelsIndex} 这一处调用，不影响 {@code AEFeature.Channels} 在别处的语义
 * (例如 {@code BlockStorageReshuffle} 用它 gate 方块注册)，因此不会误删方块或改动其它逻辑。
 * <p>
 * 幂等：若 AE2 配置本就关闭了 Channels 功能，{@code isFeatureEnabled} 本就返回 false，本重定向不产生任何变化。
 * 由 {@link Config#disableAE2ChannelLimit} 控制，默认开启。
 *
 * @see MixinPathGridCacheChannels 无控制器(ad-hoc)网络的对应处理
 */
@Mixin(value = GridNode.class, remap = false)
public abstract class MixinGridNodeChannels {

    @Redirect(
        method = "getCompressedChannelsIndex",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/AEConfig;isFeatureEnabled(Lappeng/core/features/AEFeature;)Z"),
        remap = false,
        require = 1)
    private boolean gtnc$ignoreChannelFeature(AEConfig config, AEFeature feature) {
        if (Config.disableAE2ChannelLimit && feature == AEFeature.Channels) return false;
        return config.isFeatureEnabled(feature);
    }
}
