package com.xyp.gtnc.Common.packet;

import com.xyp.gtnc.Common.packet.wildcard.MessageUpdateWildcardConfig;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * 网络包注册管理器
 * Network packet registration manager
 */
public class NetWorkHandler {

    /**
     * 注册所有网络包
     * Register all network packets
     */
    public static void registerAllMessage() {
        int id = 0;

        // 工具带相关网络包
        // Toolbelt related packets
        registerMessage(SwapItems.class, SwapItems.Handler.class, id++, Side.SERVER);
        registerMessage(BeltContentsChange.class, BeltContentsChange.Handler.class, id++, Side.CLIENT);
        registerMessage(SyncBeltSlotContents.class, SyncBeltSlotContents.Handler.class, id++, Side.CLIENT);
        registerMessage(OpenBeltSlotInventory.class, OpenBeltSlotInventory.Handler.class, id++, Side.SERVER);

        // 矿脉挖掘镐网络包
        // Vein Mining Pickaxe packets
        registerMessage(SyncVeinPickaxeNBT.class, SyncVeinPickaxeNBT.Handler.class, id++, Side.SERVER);

        // 通配样板符网络包
        // Wildcard Pattern packets
        registerMessage(
            MessageUpdateWildcardConfig.class,
            MessageUpdateWildcardConfig.Handler.class,
            id++,
            Side.SERVER);
    }

    /**
     * 注册单个网络包
     * Register a single network packet
     *
     * @param messageClass 消息类 / Message class
     * @param handlerClass 处理器类 / Handler class
     * @param id           网络包ID / Packet ID
     * @param side         注册侧 / Registration side
     */
    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<REQ> messageClass,
        Class<? extends IMessageHandler<REQ, REPLY>> handlerClass, int id, Side side) {
        ScienceNotCool.channel.registerMessage(handlerClass, messageClass, id, side);
    }
}
