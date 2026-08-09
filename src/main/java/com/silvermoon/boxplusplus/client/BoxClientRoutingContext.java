package com.silvermoon.boxplusplus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.silvermoon.boxplusplus.common.tileentities.GTMachineBox;
import com.silvermoon.boxplusplus.util.Util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class BoxClientRoutingContext {

    private BoxClientRoutingContext() {}

    public static void bind(GTMachineBox box) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null && box != null) Util.boxMap.put(player, box);
    }

    public static EntityPlayer player() {
        return Minecraft.getMinecraft().thePlayer;
    }
}
