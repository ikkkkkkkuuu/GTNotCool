package com.xyp.gtnc.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.FMLCommonHandler;

@SuppressWarnings("unused")
public class Utils {

    public static ArrayList<ItemStack> multiplyItemStacksSize(List<ItemStack> itemStacks, float mult,
        boolean splitStacks) {
        ArrayList<ItemStack> newItemStacks = new ArrayList<>(itemStacks.size() * 2);
        for (ItemStack is : itemStacks) {
            if (is == null) {
                continue;
            }
            is.stackSize = (int) (is.stackSize * mult);
            newItemStacks.add(is);
            // In case of stack size > max
            while (is.stackSize > (splitStacks ? 1 : is.getMaxStackSize())) {
                newItemStacks.add(is.splitStack(is.getMaxStackSize()));
            }
        }
        return newItemStacks;
    }

    public static ArrayList<ItemStack> multiplyItemStacksSize(List<ItemStack> itemStacks, float mult) {
        return multiplyItemStacksSize(itemStacks, mult, false);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ItemStack newItemStack(Item aItem) {
        return new ItemStack(aItem, 1, 0);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ItemStack newItemStack(Block aBlock) {
        return new ItemStack(aBlock, 1, 0);
    }

    public static boolean isClientSide() {
        return FMLCommonHandler.instance()
            .getSide()
            .isClient();
    }

    public static boolean isServerSide() {
        return FMLCommonHandler.instance()
            .getSide()
            .isServer();
    }

    public static boolean isClientThreaded() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient();
    }

    /**
     * 确保 NBT 中存在 UUID，如果不存在则生成一个新的
     *
     * @param aNBT NBT 标签
     * @return UUID 字符串
     */
    public static String ensureUUID(NBTTagCompound aNBT) {
        if (!aNBT.hasKey("storeUUID")) {
            aNBT.setString(
                "storeUUID",
                UUID.randomUUID()
                    .toString());
        }
        return aNBT.getString("storeUUID");
    }

    public static final String ZERO_STRING = "0";

    public static long toLongSafe(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0L;
        if (value > Long.MAX_VALUE) return Long.MAX_VALUE;
        if (value < Long.MIN_VALUE) return Long.MIN_VALUE;
        return (long) value;
    }

    public static String shortFormat(long value) {
        if (value < 1000) return String.valueOf(value);
        if (value < 1_000_000) return String.format("%.1fK", value / 1000.0);
        if (value < 1_000_000_000L) return String.format("%.1fM", value / 1_000_000.0);
        if (value < 1_000_000_000_000L) return String.format("%.1fB", value / 1_000_000_000.0);
        return String.format("%.1fT", value / 1_000_000_000_000.0);
    }

    public static String formatNumbers(long value) {
        if (value < 1000) return String.valueOf(value);
        if (value < 1_000_000) return String.format("%.1fK", value / 1000.0);
        if (value < 1_000_000_000L) return String.format("%.1fM", value / 1_000_000.0);
        if (value < 1_000_000_000_000L) return String.format("%.1fB", value / 1_000_000_000.0);
        return String.format("%.1fT", value / 1_000_000_000_000.0);
    }

}
