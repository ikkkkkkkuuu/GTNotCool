package com.xyp.gtnc.Common.blocks.casings;

import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SideOnly(Side.CLIENT)
public class BlockStaticDataClientOnly {

    public static final Int2ObjectMap<IIcon> BASE_ICONS = new Int2ObjectOpenHashMap<>();

    public static final Int2ObjectMap<IIcon> GLOW_ICONS = new Int2ObjectOpenHashMap<>();

    public static final Int2ObjectMap<IIcon> GLASS_ICONS = new Int2ObjectOpenHashMap<>();
}
