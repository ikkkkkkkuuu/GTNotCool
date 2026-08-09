package com.silvermoon.boxplusplus.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.silvermoon.boxplusplus.common.tileentities.GTMachineBox;
import com.silvermoon.boxplusplus.util.Util;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import io.netty.buffer.ByteBuf;

/** Authoritative routing snapshot returned after a recipe is imported from the client-only NEI screen. */
public class MessageRoutingSnapshot implements IMessage {

    private NBTTagCompound routing;
    private NBTTagCompound recipe;
    private int recipePage;
    private int recipePageCount;
    private int dimension;
    private int x;
    private int y;
    private int z;

    public MessageRoutingSnapshot() {}

    public MessageRoutingSnapshot(NBTTagCompound routing, GTMachineBox box) {
        this.routing = routing;
        if (box != null && box.getBaseMetaTileEntity() != null) {
            this.recipe = box.recipe.RecipeToNBT();
            this.recipePage = box.getRecipePageForGui();
            this.recipePageCount = box.getRecipePageCountForGui();
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

    @Override
    public void fromBytes(ByteBuf buf) {
        routing = ByteBufUtils.readTag(buf);
        recipe = ByteBufUtils.readTag(buf);
        recipePage = buf.readInt();
        recipePageCount = buf.readInt();
        dimension = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, routing);
        ByteBufUtils.writeTag(buf, recipe);
        buf.writeInt(recipePage);
        buf.writeInt(recipePageCount);
        buf.writeInt(dimension);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<MessageRoutingSnapshot, IMessage> {

        @Override
        public IMessage onMessage(MessageRoutingSnapshot message, MessageContext ctx) {
            Minecraft.getMinecraft()
                .func_152344_a(() -> message.applyClientSnapshot());
            return null;
        }
    }

    private void applyClientSnapshot() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null) return;
        GTMachineBox box = null;
        if (player.worldObj != null && player.worldObj.provider.dimensionId == dimension) {
            TileEntity tile = player.worldObj.getTileEntity(x, y, z);
            if (tile instanceof IGregTechTileEntity base
                && base.getMetaTileEntity() instanceof GTMachineBox machineBox) {
                box = machineBox;
            }
        }
        if (box == null) box = Util.boxMap.get(player);
        if (box == null || box.getBaseMetaTileEntity() == null
            || box.getBaseMetaTileEntity()
                .isDead())
            return;
        box.applyRecipePageCountMirrorForGui(recipePageCount);
        box.applyRecipePageMirrorForGui(recipePage);
        box.applyRoutingMirrorForGui(routing);
        box.applyFinalRecipeMirrorForGui(recipe);
        Util.boxMap.put(player, box);
    }
}
