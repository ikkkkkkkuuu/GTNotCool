package com.silvermoon.boxplusplus.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

import com.silvermoon.boxplusplus.common.tileentities.GTMachineBox;
import com.silvermoon.boxplusplus.util.BoxRoutings;
import com.silvermoon.boxplusplus.util.Util;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import io.netty.buffer.ByteBuf;

public class MessageRouting implements IMessage {

    NBTTagCompound nbt;
    String uuid;
    int dimension;
    int x;
    int y;
    int z;
    boolean hasPosition;

    // It's needed.
    public MessageRouting() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = ByteBufUtils.readUTF8String(buf);
        nbt = ByteBufUtils.readTag(buf);
        hasPosition = buf.readBoolean();
        dimension = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, uuid);
        ByteBufUtils.writeTag(buf, nbt);
        buf.writeBoolean(hasPosition);
        buf.writeInt(dimension);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public MessageRouting(NBTTagCompound nbt, EntityPlayer player) {
        this.nbt = nbt;
        this.uuid = player.getUniqueID()
            .toString();
        GTMachineBox box = Util.boxMap.get(player);
        if (box != null && box.getBaseMetaTileEntity() != null) {
            this.hasPosition = true;
            this.dimension = box.getBaseMetaTileEntity()
                .getWorld().provider.dimensionId;
            this.x = box.getBaseMetaTileEntity()
                .getXCoord();
            this.y = box.getBaseMetaTileEntity()
                .getYCoord();
            this.z = box.getBaseMetaTileEntity()
                .getZCoord();
        }
    }

    public static class Handler implements IMessageHandler<MessageRouting, IMessage> {

        @Override
        public IMessage onMessage(MessageRouting message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                if (player != null) {
                    if (!player.getUniqueID()
                        .toString()
                        .equals(message.uuid)) return null;
                    // The position travels with the NEI request and identifies the exact machine that opened NEI.
                    // The player map is only a fallback because it may still reference a previously opened Box.
                    GTMachineBox box = message.getBoxFromPosition();
                    if (box == null) box = Util.boxMap.get(player);
                    if (box == null || box.getBaseMetaTileEntity()
                        .isDead()) return null;
                    box.addRoutingForGui(new BoxRoutings(message.nbt));
                    player.openContainer.detectAndSendChanges();
                    NetworkLoader.instance
                        .sendTo(new MessageRoutingSnapshot(box.getRoutingSnapshotForGui(), box), player);
                }
            }
            return null;
        }
    }

    private GTMachineBox getBoxFromPosition() {
        if (!hasPosition) return null;
        if (MinecraftServer.getServer() == null) return null;
        if (MinecraftServer.getServer()
            .worldServerForDimension(dimension) == null) return null;
        TileEntity tile = MinecraftServer.getServer()
            .worldServerForDimension(dimension)
            .getTileEntity(x, y, z);
        if (!(tile instanceof IGregTechTileEntity base)) return null;
        if (base.getMetaTileEntity() instanceof GTMachineBox box) return box;
        return null;
    }
}
