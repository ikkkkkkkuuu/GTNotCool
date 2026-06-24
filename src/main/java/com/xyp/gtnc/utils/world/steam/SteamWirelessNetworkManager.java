package com.xyp.gtnc.utils.world.steam;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;

import com.xyp.gtnc.ScienceNotCool;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;

/**
 * 无线蒸汽网络全局管理器
 * 按 SpaceProjectManager 团队组织，key 为团队领袖 UUID
 */
public class SteamWirelessNetworkManager {

    public static HashMap<UUID, BigInteger> GLOBAL_STEAM = new HashMap<>(100, 0.9f);

    private SteamWirelessNetworkManager() {}

    public static void strongCheckOrAddUser(UUID user_uuid) {
        SpaceProjectManager.checkOrCreateTeam(user_uuid);
        user_uuid = SpaceProjectManager.getLeader(user_uuid);
        GLOBAL_STEAM.putIfAbsent(user_uuid, BigInteger.ZERO);
    }

    /**
     * 向玩家的全局蒸汽存储中添加/减去蒸汽
     * 传入负数表示消耗，返回值低于 0 时操作不执行并返回 false
     */
    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, BigInteger steamAmount) {
        try {
            GlobalSteamWorldSavedData.INSTANCE.markDirty();
        } catch (Exception exception) {
            ScienceNotCool.LOG
                .error("[SteamWirelessNetworkManager] Could not mark GlobalSteam dirty in addSteam", exception);
        }

        UUID teamUUID = SpaceProjectManager.getLeader(user_uuid);
        BigInteger totalSteam = GLOBAL_STEAM.getOrDefault(teamUUID, BigInteger.ZERO);
        totalSteam = totalSteam.add(steamAmount);

        if (totalSteam.signum() >= 0) {
            GLOBAL_STEAM.put(teamUUID, totalSteam);
            return true;
        }
        return false;
    }

    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, long steamAmount) {
        return addSteamToGlobalSteamMap(user_uuid, BigInteger.valueOf(steamAmount));
    }

    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, int steamAmount) {
        return addSteamToGlobalSteamMap(user_uuid, BigInteger.valueOf(steamAmount));
    }

    public static BigInteger getUserSteam(UUID user_uuid) {
        return GLOBAL_STEAM.getOrDefault(SpaceProjectManager.getLeader(user_uuid), BigInteger.ZERO);
    }

    public static int getUserSteamInt(UUID user_uuid) {
        BigInteger value = GLOBAL_STEAM.getOrDefault(SpaceProjectManager.getLeader(user_uuid), BigInteger.ZERO);
        if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) return Integer.MAX_VALUE;
        if (value.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) return Integer.MIN_VALUE;
        return value.intValue();
    }

    /** 直接覆盖蒸汽量（仅管理员使用） */
    public static void setUserSteam(UUID user_uuid, BigInteger steamAmount) {
        try {
            GlobalSteamWorldSavedData.INSTANCE.markDirty();
        } catch (Exception exception) {
            ScienceNotCool.LOG
                .error("[SteamWirelessNetworkManager] Could not mark GlobalSteam dirty in setSteam", exception);
        }
        GLOBAL_STEAM.put(SpaceProjectManager.getLeader(user_uuid), steamAmount);
    }

    public static void clearGlobalSteamInformationMaps() {
        GLOBAL_STEAM.clear();
    }

    public static UUID processInitialSettings(final IGregTechTileEntity machine) {
        final UUID uuid = machine.getOwnerUuid();
        SpaceProjectManager.checkOrCreateTeam(uuid);
        return uuid;
    }
}
