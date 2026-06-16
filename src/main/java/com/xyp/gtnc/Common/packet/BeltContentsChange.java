package com.xyp.gtnc.Common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.BeltFinder;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BeltContentsChange implements IMessage {

    private int entityId;
    private String where;
    private int slot;
    private ItemStack stack;

    public BeltContentsChange() {}

    public BeltContentsChange(int entityId, String where, int slot, ItemStack stack) {
        this.entityId = entityId;
        this.where = where;
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        where = ByteBufUtils.readUTF8String(buf);
        slot = buf.readInt();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeUTF8String(buf, where);
        buf.writeInt(slot);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static class Handler implements IMessageHandler<BeltContentsChange, IMessage> {

        @Override
        public IMessage onMessage(BeltContentsChange message, MessageContext ctx) {
            Minecraft.getMinecraft()
                .func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
                        if (entity instanceof EntityPlayer) {
                            // Handle belt contents change from server
                            // This is used when Curios or other integrations modify the belt
                            BeltFinder.BeltGetter getter = BeltFinder.findBelt((EntityPlayer) entity);
                            if (getter != null) {
                                // The belt stack itself is the ItemStack that was changed
                                // No direct "setSlot" in this simplified version
                            }
                        }
                    }
                });
            return null;
        }
    }
}
