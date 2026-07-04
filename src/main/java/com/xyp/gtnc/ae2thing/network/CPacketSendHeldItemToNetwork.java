package com.xyp.gtnc.ae2thing.network;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.ae2thing.api.WirelessObject;
import com.xyp.gtnc.ae2thing.util.InvUtil;

import appeng.api.config.Actionable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Sent from the client when the player presses the "send held item to network" keybind. The server locates any wireless
 * terminal in the player's main inventory or baubles slots, resolves its ME network and injects the currently held
 * stack into network storage. Mirrors the extraction path of {@link CPacketInventoryActionExtend}'s REQUEST_ITEM.
 */
public class CPacketSendHeldItemToNetwork implements IMessage {

    private int slot;

    public CPacketSendHeldItemToNetwork() {}

    public CPacketSendHeldItemToNetwork(final int slot) {
        this.slot = slot;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
    }

    public static class Handler implements IMessageHandler<CPacketSendHeldItemToNetwork, IMessage> {

        @Nullable
        @Override
        public IMessage onMessage(CPacketSendHeldItemToNetwork message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (message.slot < 0 || message.slot >= player.inventory.mainInventory.length) return null;

            final ItemStack held = player.inventory.mainInventory[message.slot];
            if (held == null || held.getItem() == null || held.stackSize <= 0) return null;

            final IAEItemStack toStore = AEItemStack.create(held);
            if (toStore == null) return null;

            for (ItemStack terminal : InvUtil
                .matcher(player, stack -> stack != null && stack.getItem() instanceof IWirelessTermHandler)) {
                if (toStore.getStackSize() <= 0) break;
                try {
                    WirelessObject object = new WirelessObject(terminal, player.worldObj, message.slot, 0, 0, player);
                    if (!object.rangeCheck()) continue;
                    IAEItemStack remainder = object.getItemInventory()
                        .injectItems(toStore, Actionable.MODULATE, object.getSource());
                    toStore.setStackSize(remainder == null ? 0 : remainder.getStackSize());
                } catch (Exception ignored) {}
            }

            final long stored = held.stackSize - toStore.getStackSize();
            if (stored <= 0) return null;

            final int remaining = (int) (held.stackSize - stored);
            // reset the slot contents (not just the size) so the vanilla EntityPlayerMP detects the change and
            // resyncs the held stack to the client, mirroring the REQUEST_ITEM path
            if (remaining <= 0) {
                player.inventory.setInventorySlotContents(message.slot, null);
            } else {
                ItemStack updated = held.copy();
                updated.stackSize = remaining;
                player.inventory.setInventorySlotContents(message.slot, updated);
            }
            return null;
        }
    }
}
