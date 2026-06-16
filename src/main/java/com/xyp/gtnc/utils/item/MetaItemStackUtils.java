package com.xyp.gtnc.utils.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.xyp.gtnc.ScienceNotCool;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;

public class MetaItemStackUtils {

    public static ItemStack initMetaItemStack(int meta, Item basicItem, IntSet aContainerSet) {
        StatCollector.translateToLocal(basicItem.getUnlocalizedName() + "." + meta + ".name");
        aContainerSet.add(meta);
        return new ItemStack(basicItem, 1, meta);
    }

    public static ItemStack initMetaItemStack(int meta, Block baseBlock, IntSet aContainerSet) {
        StatCollector.translateToLocal(baseBlock.getUnlocalizedName() + "." + meta + ".name");
        aContainerSet.add(meta);
        return new ItemStack(baseBlock, 1, meta);
    }

    public static void metaItemStackTooltipsAdd(Int2ObjectMap<String[]> tooltipsMap, int meta, String[] tooltips) {
        if (tooltipsMap.containsKey(meta)) {
            ScienceNotCool.LOG.info("failed to Replace a tooltips:{} ...", tooltips[0]);
            return;
        }
        tooltipsMap.put(meta, tooltips);
    }
}
