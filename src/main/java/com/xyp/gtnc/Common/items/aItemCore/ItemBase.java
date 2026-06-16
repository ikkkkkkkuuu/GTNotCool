package com.xyp.gtnc.Common.items.aItemCore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 物品基类，简化物品注册和纹理设置
 */
@SuppressWarnings("unused")
public class ItemBase extends Item {

    protected String name;

    // #tr itemBase.null.name
    // # Null Item
    // # zh_CN 空物品
    public ItemBase() {
        this("_null_");
    }

    public ItemBase(String name) {
        this(name, CreativeTabs.tabMisc);
    }

    public ItemBase(String name, CreativeTabs creativeTabs) {
        this.setTextureName(this.name = name);
        this.setCreativeTab(creativeTabs);
    }

    @Override
    public Item setTextureName(String textureName) {
        this.iconString = "sciencenotcool:" + textureName;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getIconString() {
        return this.iconString == null ? "MISSING_ICON_ITEM_" + itemRegistry.getIDForObject(this) + "_" + this.name
            : this.iconString;
    }

    @Override
    public Item setUnlocalizedName(String unlocalizedName) {
        this.name = unlocalizedName;
        return this;
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + this.name;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }

    public String getRegisterName() {
        return this.name;
    }
}
