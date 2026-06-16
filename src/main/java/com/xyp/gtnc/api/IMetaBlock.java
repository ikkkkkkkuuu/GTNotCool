package com.xyp.gtnc.api;

import net.minecraft.util.IIcon;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public interface IMetaBlock {

    IntSet getUsedMetaSet();

    Int2ObjectMap<String[]> getTooltipsMap();

    Int2ObjectMap<IIcon> getIconMap();

    default String[] getTooltips(int meta) {
        return getTooltipsMap().get(meta);
    }
}
