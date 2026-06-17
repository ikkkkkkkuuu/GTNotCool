package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

public class BeltGuiHandler implements IGuiHandler {

    public static final int GUI_BELT = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GUI_BELT) {
            return new BeltContainer(player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == GUI_BELT) {
            return new BeltScreen(new BeltContainer(player.inventory), player.inventory);
        }
        return null;
    }
}
