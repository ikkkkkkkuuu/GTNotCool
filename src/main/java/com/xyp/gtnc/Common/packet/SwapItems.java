package com.xyp.gtnc.Common.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltData;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class SwapItems implements IMessage {

    private int swapWith;

    public SwapItems() {}

    public SwapItems(int swapWith) {
        this.swapWith = swapWith;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        swapWith = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(swapWith);
    }

    public static class Handler implements IMessageHandler<SwapItems, IMessage> {

        @Override
        public IMessage onMessage(SwapItems message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            swapItem(message.swapWith, player);
            return null;
        }
    }

    public static void swapItem(int swapWith, EntityPlayer player) {
        ToolBeltData data = ToolBeltData.get(player);
        if (data == null) {
            ToolBeltData.register(player);
            data = ToolBeltData.get(player);
        }
        if (data == null) return;

        ItemStack inHand = player.getHeldItem();
        int size = ToolBeltData.SLOT_COUNT;

        if (swapWith < 0) {
            // Insert mode: try to merge or find empty slot (requires inHand)
            if (inHand == null) return;
            if (!ConfigData.isItemStackAllowed(inHand)) return;

            for (int i = 0; i < size; i++) {
                ItemStack inSlot = data.getStackInSlot(i);
                if (inSlot != null && inSlot.isItemEqual(inHand) && ItemStack.areItemStackTagsEqual(inSlot, inHand)) {
                    int max = inSlot.getMaxStackSize();
                    int acc = inSlot.stackSize + inHand.stackSize;
                    if (acc <= max) {
                        inSlot.stackSize = acc;
                        data.setStackInSlot(i, inSlot);
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        break;
                    } else {
                        inSlot.stackSize = max;
                        data.setStackInSlot(i, inSlot);
                        inHand.stackSize = acc - max;
                        player.inventory.setInventorySlotContents(
                            player.inventory.currentItem,
                            inHand.stackSize > 0 ? inHand : null);
                    }
                } else if (inSlot == null) {
                    data.setStackInSlot(i, inHand.copy());
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    break;
                }
            }
        } else {
            // Swap or pickup mode
            if (swapWith >= size) return;

            ItemStack inSlot = data.getStackInSlot(swapWith);

            if (inHand != null) {
                // Swap: inHand <-> belt slot
                if (!ConfigData.isItemStackAllowed(inHand)) return;
                data.setStackInSlot(swapWith, inHand.copy());
            } else {
                // Pickup: belt slot -> hand, belt slot becomes empty
                if (inSlot == null) return; // Nothing to pick up
                data.setStackInSlot(swapWith, null);
            }
            player.inventory.setInventorySlotContents(player.inventory.currentItem, inSlot);
        }

        data.syncToTracking();
    }
}
