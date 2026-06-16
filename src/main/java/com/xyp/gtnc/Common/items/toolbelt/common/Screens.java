package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;
import com.xyp.gtnc.ScienceNotCool;

public class Screens {

    public static void openBeltScreen(EntityPlayer player, int slot) {
        if (player instanceof EntityPlayerMP) {
            ItemStack heldItem = player.inventory.getStackInSlot(slot);
            if (heldItem != null && heldItem.getItem() instanceof ToolBeltItem) {
                player.openGui(ScienceNotCool.instance, BeltGuiHandler.GUI_BELT, player.worldObj, slot, 0, 0);
            }
        }
    }

    public static void openSlotScreen(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            player.openGui(ScienceNotCool.instance, BeltGuiHandler.GUI_BELT_SLOT, player.worldObj, 0, 0, 0);
        }
    }
}
