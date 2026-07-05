package com.xyp.gtnc.ae2thing.common.item;

import java.util.EnumSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import com.xyp.gtnc.ae2thing.inventory.InventoryHandler;
import com.xyp.gtnc.ae2thing.inventory.gui.GuiType;
import com.xyp.gtnc.ae2thing.inventory.item.IItemInventory;
import com.xyp.gtnc.ae2thing.util.BlockPos;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.features.IWirelessTermRegistry;
import appeng.core.features.AEFeature;
import appeng.core.localization.PlayerMessages;
import appeng.items.tools.powered.ToolWirelessTerminal;
import appeng.util.Platform;
import baubles.api.IBauble;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;

public abstract class ItemBaseWirelessTerminal extends ToolWirelessTerminal
    implements IItemInventory, IBauble, IBaubleExpanded {

    public ItemBaseWirelessTerminal() {
        super();
        this.setFeature(EnumSet.of(AEFeature.WirelessAccessTerminal, AEFeature.PoweredTools));
    }

    public ItemStack stack() {
        return new ItemStack(this, 1);
    }

    @Override
    public boolean canHandle(final ItemStack is) {
        return is != null && is.getItem() instanceof ItemBaseWirelessTerminal;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    /**
     * Allow the terminal to be placed into every registered bauble slot type, not just AE2's dedicated terminal slot.
     */
    @Override
    public String[] getBaubleTypes(ItemStack itemstack) {
        java.util.List<String> types = new java.util.ArrayList<>();
        for (String type : BaubleExpandedSlots.getCurrentlyRegisteredTypes()) {
            if (type == null || type.equals(BaubleExpandedSlots.unknownType)
                || type.equals(BaubleExpandedSlots.invalidType)) {
                continue;
            }
            types.add(type);
        }
        return types.toArray(new String[0]);
    }

    @Override
    public ItemStack onItemRightClick(final ItemStack item, final World w, final EntityPlayer player) {
        if (ForgeEventFactory.onItemUseStart(player, item, 1) > 0) {
            if (Platform.isClient()) return item;
            IWirelessTermRegistry term = AEApi.instance()
                .registries()
                .wireless();
            if (!term.isWirelessTerminal(item)) {
                player.addChatMessage(PlayerMessages.DeviceNotWirelessTerminal.get());
                return item;
            }
            final IWirelessTermHandler handler = term.getWirelessTerminalHandler(item);
            final String unparsedKey = handler.getEncryptionKey(item);
            if (unparsedKey.isEmpty()) {
                player.addChatMessage(PlayerMessages.DeviceNotLinked.get());
                return item;
            }
            final long parsedKey = Long.parseLong(unparsedKey);
            final ILocatable securityStation = AEApi.instance()
                .registries()
                .locatable()
                .getLocatableBy(parsedKey);
            if (securityStation == null) {
                player.addChatMessage(PlayerMessages.StationCanNotBeLocated.get());
                return item;
            }
            if (handler.hasPower(player, 0.5, item)) {
                InventoryHandler.openGui(
                    player,
                    w,
                    new BlockPos(player.inventory.currentItem, 0, 0),
                    ForgeDirection.UNKNOWN,
                    this.guiGuiType(item));
            } else {
                player.addChatMessage(PlayerMessages.DeviceNotPowered.get());
            }
        }

        return item;
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return super.onItemUseFirst(itemstack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    /**
     * When the AE2 wireless crafting terminal path (WirelessTerminalGuiObject / AEBaseContainer.portableSourceTick)
     * drives this terminal it drains real AE power unless the handler reports infinite power. Our own two-in-one GUI
     * already treats the infinity energy card as "no drain" (WirelessObject#extractPower), so mirror that here so both
     * GUIs behave the same: energy card -> infinite power, infinity booster card -> infinite range.
     */
    @Override
    public boolean hasInfinityPower(ItemStack is) {
        return com.xyp.gtnc.ae2thing.api.WirelessObject.hasEnergyCard(is);
    }

    @Override
    public boolean hasInfinityRange(ItemStack is) {
        return com.xyp.gtnc.ae2thing.api.WirelessObject.hasInfinityBoosterCard(is);
    }

    protected abstract GuiType guiGuiType(ItemStack item);
}
