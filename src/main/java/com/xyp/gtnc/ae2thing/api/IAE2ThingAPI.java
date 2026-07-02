package com.xyp.gtnc.ae2thing.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.Grid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public interface IAE2ThingAPI {

    void putText(String text);

    void putText(Class<?> cls, String text);

    String getText(Class<?> cls);

    String getText();

    boolean isBlacklistedInStorage(Item item);

    void blacklistItemInStorage(Class<? extends Item> item);

    void addBackpackItem(Class<? extends Item> item);

    boolean isBackpackItem(Item item);

    boolean isBackpackItem(ItemStack is);

    IInventory getBackpackInv(ItemStack is);

    boolean isBackpackItemInv(ItemStack is);

    Pinned getPinned();

    @SideOnly(Side.CLIENT)
    void openDualinterfaceTerminal();

    ItemStack getFluidContainer(IAEFluidStack fluid);

    ItemStack getFluidContainer(FluidStack fluid);

    void setDefaultFluidContainer(ItemStack item);

    ItemStack getDefaultFluidContainer();

    String getVersion();

    long getStorageMyID(Grid grid);

    @SideOnly(Side.CLIENT)
    void addCraftingCompleteNotification(IAEItemStack item);

    @SideOnly(Side.CLIENT)
    void addNotification(String tile, String Content, ItemStack item);
}
