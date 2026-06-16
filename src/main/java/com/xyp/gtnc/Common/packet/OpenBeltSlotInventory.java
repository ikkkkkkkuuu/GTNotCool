package com.xyp.gtnc.Common.packet;

import net.minecraft.entity.player.EntityPlayerMP;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.common.Screens;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class OpenBeltSlotInventory implements IMessage {

    public OpenBeltSlotInventory() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<OpenBeltSlotInventory, IMessage> {

        @Override
        public IMessage onMessage(OpenBeltSlotInventory message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (ConfigData.customBeltSlotEnabled) {
                Screens.openSlotScreen(player);
            }
            return null;
        }
    }
}
