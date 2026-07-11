package com.xyp.gtnc.Common.mebridge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 跨维度 ME 网桥 - 接收端连接计数(纯静态,仅服务端)。
 * <p>
 * 记录每个频道当前有多少接收端已连入,供发起端 GUI 显示"N 个接收端连入"。
 * 不持久化:连接本身在世界重载后由各接收端 tile 的懒重连重新建立,计数随之重建。
 */
public final class MEBridgeReceiverRegistry {

    /** 频道名 → 已连入的接收端 tile 集合。 */
    private static final Map<String, Set<TileMEBridgeReceiver>> BY_CHANNEL = new HashMap<>();

    private MEBridgeReceiverRegistry() {}

    public static void add(String channel, TileMEBridgeReceiver tile) {
        if (channel == null || channel.isEmpty() || tile == null) return;
        BY_CHANNEL.computeIfAbsent(channel, k -> new HashSet<>())
            .add(tile);
    }

    public static void remove(String channel, TileMEBridgeReceiver tile) {
        if (channel == null || tile == null) return;
        Set<TileMEBridgeReceiver> set = BY_CHANNEL.get(channel);
        if (set != null) {
            set.remove(tile);
            if (set.isEmpty()) BY_CHANNEL.remove(channel);
        }
    }

    public static int count(String channel) {
        if (channel == null) return 0;
        Set<TileMEBridgeReceiver> set = BY_CHANNEL.get(channel);
        if (set == null) return 0;
        // 自愈：统计时剔除已失效 / 已断开 / 频道已改的条目，避免手动 add/remove 漏配平导致计数残留。
        set.removeIf(t -> t == null || t.isInvalid() || !t.isConnected() || !channel.equals(t.getChannelName()));
        if (set.isEmpty()) {
            BY_CHANNEL.remove(channel);
            return 0;
        }
        return set.size();
    }

    public static void clear() {
        BY_CHANNEL.clear();
    }
}
