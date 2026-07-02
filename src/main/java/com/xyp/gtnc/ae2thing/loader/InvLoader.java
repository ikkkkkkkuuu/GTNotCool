package com.xyp.gtnc.ae2thing.loader;

import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.util.BaublesUtil;
import com.xyp.gtnc.ae2thing.util.InvUtil;

public class InvLoader implements Runnable {

    @Override
    public void run() {
        InvUtil.INVENTORY.add(player -> player.inventory);
        if (Mods.BAUBLES.isModLoaded()) {
            InvUtil.INVENTORY.add(BaublesUtil::getBaublesInv);
        }
    }
}
