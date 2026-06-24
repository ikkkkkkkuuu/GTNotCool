package com.xyp.gtnc.utils.world.steam;

import static com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager.GLOBAL_STEAM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import com.xyp.gtnc.ScienceNotCool;

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

/**
 * 无线蒸汽网络的持久化存储
 * 在世界加载时通过 WorldEvent.Load 触发 loadInstance
 */
public class GlobalSteamWorldSavedData extends WorldSavedData {

    public static GlobalSteamWorldSavedData INSTANCE;
    private static boolean sLoaded = false;

    public static final String DATA_NAME = "GTNC_WirelessSteamWorldSavedData";
    public static final String GLOBAL_STEAM_NBT_TAG = "GTNC_GlobalSteam_MapNBTTag";
    public static final String GLOBAL_STEAM_TEAM_NBT_TAG = "GTNC_GlobalSteamTeam_MapNBTTag";

    public static void loadInstance(World world) {
        if (sLoaded) return;
        GLOBAL_STEAM.clear();
        MapStorage storage = world.mapStorage;
        INSTANCE = (GlobalSteamWorldSavedData) storage.loadData(GlobalSteamWorldSavedData.class, DATA_NAME);
        if (INSTANCE == null) {
            INSTANCE = new GlobalSteamWorldSavedData();
            storage.setData(DATA_NAME, INSTANCE);
        }
        INSTANCE.markDirty();
        sLoaded = true;
    }

    public GlobalSteamWorldSavedData() {
        super(DATA_NAME);
    }

    public GlobalSteamWorldSavedData(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readFromNBT(NBTTagCompound nbt) {
        // 读取蒸汽数据
        try {
            byte[] ba = nbt.getByteArray(GLOBAL_STEAM_NBT_TAG);
            if (ba.length == 0) return;
            try (ByteArrayInputStream bis = new ByteArrayInputStream(ba);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
                HashMap<Object, BigInteger> hashData = (HashMap<Object, BigInteger>) ois.readObject();
                for (Map.Entry<Object, BigInteger> entry : hashData.entrySet()) {
                    try {
                        GLOBAL_STEAM.put(
                            UUID.fromString(
                                entry.getKey()
                                    .toString()),
                            entry.getValue());
                    } catch (RuntimeException ignored) {
                        ScienceNotCool.LOG
                            .warn("[GlobalSteamWorldSavedData] Skipping invalid UUID: {}", entry.getKey());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            ScienceNotCool.LOG.error("[GlobalSteamWorldSavedData] {} LOAD FAILED", GLOBAL_STEAM_NBT_TAG, e);
        }

        // 读取旧团队数据
        try {
            if (!nbt.hasKey(GLOBAL_STEAM_TEAM_NBT_TAG)) return;
            byte[] ba = nbt.getByteArray(GLOBAL_STEAM_TEAM_NBT_TAG);
            if (ba.length == 0) return;
            try (ByteArrayInputStream bis = new ByteArrayInputStream(ba);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
                HashMap<String, String> oldTeams = (HashMap<String, String>) ois.readObject();
                for (Map.Entry<String, String> entry : oldTeams.entrySet()) {
                    try {
                        SpaceProjectManager
                            .putInTeam(UUID.fromString(entry.getKey()), UUID.fromString(entry.getValue()));
                    } catch (RuntimeException ignored) {
                        ScienceNotCool.LOG
                            .warn("[GlobalSteamWorldSavedData] Skipping invalid team entry: {}", entry.getKey());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            ScienceNotCool.LOG.error("[GlobalSteamWorldSavedData] {} LOAD FAILED", GLOBAL_STEAM_TEAM_NBT_TAG, e);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(GLOBAL_STEAM);
            oos.flush();
            nbt.setByteArray(GLOBAL_STEAM_NBT_TAG, bos.toByteArray());
        } catch (IOException e) {
            ScienceNotCool.LOG.error("[GlobalSteamWorldSavedData] {} SAVE FAILED", GLOBAL_STEAM_NBT_TAG, e);
        }
    }
}
