package com.xyp.gtnc.Common.packet.building;

import net.minecraft.entity.player.EntityPlayerMP;

import com.xyp.gtnc.Common.building.PixelBuildingManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * 客户端 → 服务端：撤销某生成器上次生成的建筑（一键还原）。
 * <p>
 * 服务端从 {@link com.xyp.gtnc.Common.building.PixelBuildingData} 取回该生成器记录的全部像素坐标，逐格设回空气、
 * 清除记录，并通知范围内客户端清颜色 + 刷新渲染。
 */
public class MessageUndoBuilding implements IMessage {

    public int genX, genY, genZ;

    public MessageUndoBuilding() {}

    public MessageUndoBuilding(int genX, int genY, int genZ) {
        this.genX = genX;
        this.genY = genY;
        this.genZ = genZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        genX = buf.readInt();
        genY = buf.readInt();
        genZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(genX);
        buf.writeInt(genY);
        buf.writeInt(genZ);
    }

    public static class Handler implements IMessageHandler<MessageUndoBuilding, IMessage> {

        @Override
        public IMessage onMessage(MessageUndoBuilding message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            PixelBuildingManager.undo(player, message.genX, message.genY, message.genZ);
            return null;
        }
    }
}
