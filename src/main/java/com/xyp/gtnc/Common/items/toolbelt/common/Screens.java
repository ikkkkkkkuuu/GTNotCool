package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.xyp.gtnc.ScienceNotCool;

public class Screens {

    public static void openBeltScreen(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            player.openGui(ScienceNotCool.instance, BeltGuiHandler.GUI_BELT, player.worldObj, 0, 0, 0);
        }
    }
}
