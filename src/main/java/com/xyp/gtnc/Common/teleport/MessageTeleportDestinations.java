package com.xyp.gtnc.Common.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

import com.xyp.gtnc.Client.gui.teleport.GuiTeleportTerminal;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** Server snapshot of the player's teleport destinations. */
public class MessageTeleportDestinations implements IMessage {

    private List<TeleportDestination> destinations = new ArrayList<>();

    public MessageTeleportDestinations() {}

    public MessageTeleportDestinations(List<TeleportDestination> destinations) {
        this.destinations = new ArrayList<>(destinations);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        int count = Math.min(buffer.readUnsignedByte(), TeleportManager.MAX_DESTINATIONS);
        destinations = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            destinations.add(
                new TeleportDestination(
                    ByteBufUtils.readUTF8String(buffer),
                    buffer.readInt(),
                    buffer.readInt(),
                    buffer.readInt(),
                    buffer.readInt(),
                    buffer.readBoolean()));
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(destinations.size());
        for (TeleportDestination destination : destinations) {
            ByteBufUtils.writeUTF8String(buffer, destination.name);
            buffer.writeInt(destination.dimension);
            buffer.writeInt(destination.x);
            buffer.writeInt(destination.y);
            buffer.writeInt(destination.z);
            buffer.writeBoolean(destination.locked);
        }
    }

    public static class Handler implements IMessageHandler<MessageTeleportDestinations, IMessage> {

        @Override
        public IMessage onMessage(final MessageTeleportDestinations message, MessageContext context) {
            Minecraft.getMinecraft()
                .func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        GuiTeleportTerminal.applyDestinations(message.destinations);
                    }
                });
            return null;
        }
    }
}
