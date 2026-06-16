package com.xyp.gtnc.Common.items.toolbelt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class BeltFinder {

    private static final List<BeltFinder> instances = new ArrayList<>();

    public static synchronized void addFinder(BeltFinder finder) {
        instances.add(0, finder);
    }

    public static BeltGetter findBelt(EntityLivingBase player) {
        return findBelt(player, false);
    }

    public static BeltGetter findBelt(EntityLivingBase player, boolean allowCosmetic) {
        for (BeltFinder finder : instances) {
            BeltGetter result = finder.findStack(player, allowCosmetic);
            if (result != null && result.getBelt() != null) {
                return result;
            }
        }
        return null;
    }

    public static void sendSync(EntityPlayer player) {
        BeltGetter getter = findBelt(player);
        if (getter != null) {
            getter.syncToClients();
        }
    }

    public abstract String getName();

    public abstract BeltGetter findStack(EntityLivingBase player, boolean allowCosmetic);

    public interface BeltGetter {

        ItemStack getBelt();

        void syncToClients();
    }

    // Initialize the default belt finder
    static {
        addFinder(new BeltFinderBeltSlot());
    }
}
