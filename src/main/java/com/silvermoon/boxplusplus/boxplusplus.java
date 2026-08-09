package com.silvermoon.boxplusplus;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.silvermoon.boxplusplus.common.loader.BlockRegister;

public class boxplusplus {

    public static final Logger LOG = LogManager.getLogger(Tags.MODID);

    public static final CreativeTabs BoxTab = new CreativeTabs("BoxPlusPlus") {

        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(BlockRegister.BoxRing2);
        }
    };
}
