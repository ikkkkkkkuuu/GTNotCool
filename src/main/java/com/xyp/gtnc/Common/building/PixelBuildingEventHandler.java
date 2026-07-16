package com.xyp.gtnc.Common.building;

import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * 建筑生成器持久化的世界事件挂载。仿 {@code MEBridgeEventHandler}：{@link WorldEvent.Load} 时加载
 * {@link PixelBuildingData}；主世界卸载时复位，防单机切档残留。
 * <p>
 * 另挂 {@link ChunkWatchEvent.Watch}：玩家开始观察某区块时，把该区块内所有像素方块颜色补发给他（客户端
 * {@link PixelColorStore} 是内存态，重进存档/重连/新玩家会清空，靠这里按区块懒同步补回，否则重进变白）。
 */
public class PixelBuildingEventHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        PixelBuildingManager.onWorldLoad(event.world);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        PixelBuildingManager.onWorldUnload(event.world);
    }

    @SubscribeEvent
    public void onChunkWatch(ChunkWatchEvent.Watch event) {
        PixelBuildingManager.onChunkWatch(event.player, event.chunk.chunkXPos, event.chunk.chunkZPos);
    }

    /** 服务端 tick：限速处理放置/撤销队列。注册在 FML bus（TickEvent 在 FML bus）。 */
    @SubscribeEvent
    public void onServerTick(cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent event) {
        if (event.phase == cpw.mods.fml.common.gameevent.TickEvent.Phase.END) {
            PixelBuildingManager.onServerTick();
        }
    }
}
