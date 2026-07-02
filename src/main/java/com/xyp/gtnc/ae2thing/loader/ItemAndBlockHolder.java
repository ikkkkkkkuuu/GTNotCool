package com.xyp.gtnc.ae2thing.loader;

import com.xyp.gtnc.ae2thing.common.item.ItemPatternModifier;
import com.xyp.gtnc.ae2thing.common.item.ItemWirelessDualInterfaceTerminal;

public class ItemAndBlockHolder implements Runnable {

    public static final ItemAndBlockHolder INSTANCE = new ItemAndBlockHolder();

    public static ItemWirelessDualInterfaceTerminal ITEM_WIRELESS_DUAL_INTERFACE_TERMINAL = new ItemWirelessDualInterfaceTerminal()
        .register();
    public static ItemPatternModifier ITEM_PATTERN_MODIFIER = new ItemPatternModifier().register();

    @Override
    public void run() {}
}
