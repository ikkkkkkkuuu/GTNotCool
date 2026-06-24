package com.xyp.gtnc.utils.event;

import net.minecraftforge.event.world.WorldEvent;

import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.utils.world.steam.GlobalSteamWorldSavedData;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 处理世界加载事件，初始化无线蒸汽网络的持久化存储
 */
public class SteamNetworkEventHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.isRemote) return;
        try {
            GlobalSteamWorldSavedData.loadInstance(event.world);
            ScienceNotCool.LOG
                .info("[SteamNetwork] WorldSavedData loaded for dim {}", event.world.provider.dimensionId);
        } catch (Exception e) {
            ScienceNotCool.LOG.error("[SteamNetwork] Failed to load WorldSavedData", e);
        }
    }
}
