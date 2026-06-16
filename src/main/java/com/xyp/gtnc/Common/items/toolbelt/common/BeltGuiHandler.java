package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;
import com.xyp.gtnc.Common.items.toolbelt.slot.BeltSlotMenu;
import com.xyp.gtnc.Common.items.toolbelt.slot.BeltSlotScreen;

import cpw.mods.fml.common.network.IGuiHandler;

public class BeltGuiHandler implements IGuiHandler {

    public static final int GUI_BELT = 0;
    public static final int GUI_BELT_SLOT = 1;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GUI_BELT:
                int slot = x;
                ItemStack heldItem = player.inventory.getStackInSlot(slot);
                if (heldItem != null && heldItem.getItem() instanceof ToolBeltItem) {
                    // Pass the actual reference so the container can track NBT changes
                    return new BeltContainer(player.inventory, slot, heldItem);
                }
                return null;
            case GUI_BELT_SLOT:
                return new BeltSlotMenu(player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GUI_BELT:
                int slot = x;
                ItemStack heldItem = player.inventory.getStackInSlot(slot);
                if (heldItem != null && heldItem.getItem() instanceof ToolBeltItem) {
                    return new BeltScreen(new BeltContainer(player.inventory, slot, heldItem), player.inventory);
                }
                return null;
            case GUI_BELT_SLOT:
                return new BeltSlotScreen(new BeltSlotMenu(player.inventory), player.inventory);
        }
        return null;
    }
}
