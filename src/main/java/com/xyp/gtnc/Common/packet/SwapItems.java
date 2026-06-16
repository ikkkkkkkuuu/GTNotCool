package com.xyp.gtnc.Common.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.BeltFinder;
import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;

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
        BeltFinder.BeltGetter getter = BeltFinder.findBelt(player);
        if (getter == null) return;

        ItemStack beltStack = getter.getBelt();
        if (beltStack == null || beltStack.stackSize <= 0) return;

        ItemStack inHand = player.getHeldItem();
        int size = ToolBeltItem.getBeltSize(beltStack);

        if (swapWith < 0) {
            // Insert mode: try to merge or find empty slot (requires inHand)
            if (inHand == null) return;
            if (!ConfigData.isItemStackAllowed(inHand)) return;

            for (int i = 0; i < size; i++) {
                ItemStack inSlot = ToolBeltItem.getBeltSlot(beltStack, i);
                if (inSlot != null && inSlot.isItemEqual(inHand) && ItemStack.areItemStackTagsEqual(inSlot, inHand)) {
                    int max = inSlot.getMaxStackSize();
                    int acc = inSlot.stackSize + inHand.stackSize;
                    if (acc <= max) {
                        inSlot.stackSize = acc;
                        ToolBeltItem.setBeltSlot(beltStack, i, inSlot);
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        break;
                    } else {
                        inSlot.stackSize = max;
                        ToolBeltItem.setBeltSlot(beltStack, i, inSlot);
                        inHand.stackSize = acc - max;
                        player.inventory.setInventorySlotContents(
                            player.inventory.currentItem,
                            inHand.stackSize > 0 ? inHand : null);
                    }
                } else if (inSlot == null) {
                    ToolBeltItem.setBeltSlot(beltStack, i, inHand.copy());
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    break;
                }
            }
        } else {
            // Swap or pickup mode
            if (swapWith >= size) return;

            ItemStack inSlot = ToolBeltItem.getBeltSlot(beltStack, swapWith);

            if (inHand != null) {
                // Swap: inHand <-> belt slot
                if (!ConfigData.isItemStackAllowed(inHand)) return;
                ToolBeltItem.setBeltSlot(beltStack, swapWith, inHand.copy());
            } else {
                // Pickup: belt slot -> hand, belt slot becomes empty
                if (inSlot == null) return; // Nothing to pick up
                ToolBeltItem.setBeltSlot(beltStack, swapWith, null);
            }
            player.inventory.setInventorySlotContents(player.inventory.currentItem, inSlot);
        }

        getter.syncToClients();
    }
}
