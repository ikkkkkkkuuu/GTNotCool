package com.xyp.gtnc.Client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Loader.ItemsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public class GTNCCreativeTabs {

    public static CreativeTabs GTNCItem = new CreativeTabs("GTNCItemItem") {

        @Override
        public Item getTabIconItem() {
            return ItemsLoader.eternityVial != null ? ItemsLoader.eternityVial : Items.apple;
        }
    };

    public static CreativeTabs GTNCItemBlock = new CreativeTabs("GTNCItemBlock") {

        @Override
        public Item getTabIconItem() {
            return ItemsLoader.eternityVial != null ? ItemsLoader.eternityVial : Items.apple;
        }

        @SideOnly(Side.CLIENT)
        public int func_151243_f() {
            return 1;
        }

    };

    private static final List<ItemStack> GTNCItemMachineStack = new ArrayList<>();

    public static void addToMachineList(ItemStack stack) {
        GTNCItemMachineStack.add(stack);
    }

    public static CreativeTabs GTNCItemMachine = new CreativeTabs("GTNCItemMachine") {

        @Override
        public Item getTabIconItem() {
            return ItemsLoader.eternityVial != null ? ItemsLoader.eternityVial : Items.apple;
        }

        @SideOnly(Side.CLIENT)
        public int func_151243_f() {
            return 21006;
        }

        @Override
        public void displayAllReleventItems(List<ItemStack> stackList) {
            stackList.addAll(GTNCItemMachineStack);
            super.displayAllReleventItems(stackList);
        }
    };

}
