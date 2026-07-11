package com.xyp.gtnc.Common.mebridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 跨维度 ME 网桥的全局频道注册表(纯静态,仅服务端使用)。
 * <p>
 * 频道名(可为中文)→ {@link MEBridgeChannelInfo}。全服全局唯一、所有人可见。
 * 仿照 {@code SteamWirelessNetworkManager} 的静态 Map 模式,持久化交给 {@link MEBridgeWorldSavedData}。
 */
public final class MEBridgeChannelManager {

    /** 频道名 → 频道信息。用 LinkedHashMap 保持注册顺序,GUI 列表显示更稳定。 */
    public static final Map<String, MEBridgeChannelInfo> CHANNELS = new LinkedHashMap<>();

    private MEBridgeChannelManager() {}

    /**
     * 注册 / 刷新一个频道。若频道名已存在,覆盖其坐标信息并刷新发起端 tile 引用
     * (区块重新加载时 Sender 会再次调用,以更新弱引用)。
     *
     * @return true 表示注册成功;false 表示频道名被另一个不同坐标的发起端占用(冲突)。
     */
    public static boolean register(MEBridgeChannelInfo info, TileMEBridgeSender tile) {
        if (info == null || info.name == null || info.name.isEmpty()) return false;
        MEBridgeChannelInfo existing = CHANNELS.get(info.name);
        if (existing != null && !sameLocation(existing, info)) {
            // 频道名已被另一个发起端占用
            return false;
        }
        info.setSenderTile(tile);
        CHANNELS.put(info.name, info);
        MEBridgeWorldSavedData.markDirtyIfPresent();
        return true;
    }

    /** 注销频道(发起端被拆除时调用)。只有当前占用该频道名的坐标匹配才移除,避免误删。 */
    public static void unregister(String name, int x, int y, int z, int dim) {
        if (name == null) return;
        MEBridgeChannelInfo info = CHANNELS.get(name);
        if (info == null) return;
        if (info.x == x && info.y == y && info.z == z && info.dim == dim) {
            CHANNELS.remove(name);
            MEBridgeWorldSavedData.markDirtyIfPresent();
        }
    }

    public static MEBridgeChannelInfo get(String name) {
        return name == null ? null : CHANNELS.get(name);
    }

    public static boolean exists(String name) {
        return name != null && CHANNELS.containsKey(name);
    }

    /** 频道列表快照(GUI 用,避免并发修改)。 */
    public static List<MEBridgeChannelInfo> snapshot() {
        return new ArrayList<>(CHANNELS.values());
    }

    public static void clear() {
        CHANNELS.clear();
    }

    /** 供持久化读取后批量填充(不触发发起端 tile 引用,tile 由各自 onReady 时补登记)。 */
    public static void loadEntry(MEBridgeChannelInfo info) {
        if (info == null || info.name == null || info.name.isEmpty()) return;
        CHANNELS.put(info.name, info);
    }

    public static Map<String, MEBridgeChannelInfo> view() {
        return Collections.unmodifiableMap(CHANNELS);
    }

    private static boolean sameLocation(MEBridgeChannelInfo a, MEBridgeChannelInfo b) {
        return a.x == b.x && a.y == b.y && a.z == b.z && a.dim == b.dim;
    }
}
