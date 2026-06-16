package com.xyp.gtnc.Common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.slot.BeltAttachment;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SyncBeltSlotContents implements IMessage {

    private ItemStack stack;
    private int entityId;

    public SyncBeltSlotContents() {}

    public SyncBeltSlotContents(Entity entity, BeltAttachment attachment) {
        this.stack = attachment.getContents();
        this.entityId = entity.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static class Handler implements IMessageHandler<SyncBeltSlotContents, IMessage> {

        @Override
        public IMessage onMessage(SyncBeltSlotContents message, MessageContext ctx) {
            Minecraft.getMinecraft()
                .func_152344_a(new Runnable() {

                    @Override
                    public void run() {
                        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
                        if (entity instanceof EntityLivingBase) {
                            BeltAttachment attachment = BeltAttachment.get((EntityLivingBase) entity);
                            if (attachment != null) {
                                attachment.setContents(message.stack);
                            }
                        }
                    }
                });
            return null;
        }
    }
}
