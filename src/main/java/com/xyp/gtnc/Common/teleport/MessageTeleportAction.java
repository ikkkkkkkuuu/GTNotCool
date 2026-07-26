package com.xyp.gtnc.Common.teleport;

import net.minecraft.entity.player.EntityPlayerMP;

import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** Client request for a server-authoritative teleport terminal action. */
public class MessageTeleportAction implements IMessage {

    public enum Action {
        REQUEST_DESTINATIONS,
        ADD_CURRENT_POSITION,
        REMOVE_DESTINATION,
        TELEPORT_TO_DESTINATION,
        RENAME_DESTINATION,
        TOGGLE_DESTINATION_LOCK
    }

    private Action action;
    private int destinationIndex;
    private String name;

    public MessageTeleportAction() {}

    public MessageTeleportAction(Action action, int destinationIndex, String name) {
        this.action = action;
        this.destinationIndex = destinationIndex;
        this.name = name == null ? "" : name;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        int actionOrdinal = buffer.readUnsignedByte();
        Action[] actions = Action.values();
        action = actionOrdinal < actions.length ? actions[actionOrdinal] : Action.REQUEST_DESTINATIONS;
        destinationIndex = buffer.readInt();
        name = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(action.ordinal());
        buffer.writeInt(destinationIndex);
        ByteBufUtils.writeUTF8String(buffer, name);
    }

    public static class Handler implements IMessageHandler<MessageTeleportAction, IMessage> {

        @Override
        public IMessage onMessage(MessageTeleportAction message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            switch (message.action) {
                case ADD_CURRENT_POSITION:
                    TeleportManager.addCurrentPosition(player, message.name);
                    break;
                case REMOVE_DESTINATION:
                    TeleportManager.removeDestination(player, message.destinationIndex);
                    break;
                case TELEPORT_TO_DESTINATION:
                    TeleportManager.teleport(player, message.destinationIndex);
                    break;
                case RENAME_DESTINATION:
                    TeleportManager.renameDestination(player, message.destinationIndex, message.name);
                    break;
                case TOGGLE_DESTINATION_LOCK:
                    TeleportManager.toggleLock(player, message.destinationIndex);
                    break;
                case REQUEST_DESTINATIONS:
                default:
                    break;
            }
            ScienceNotCool.channel
                .sendTo(new MessageTeleportDestinations(TeleportManager.getDestinations(player)), player);
            return null;
        }
    }
}
