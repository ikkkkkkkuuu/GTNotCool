package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

/** Routes the upstream upgrade-tree confirmation action back to its owning steam controller. */
public final class SteamGodforgeUpgradeHooks {

    private static final Map<ForgeOfGodsData, WeakReference<SteamForgeOfGods>> OWNERS = new WeakHashMap<>();

    private SteamGodforgeUpgradeHooks() {}

    public static synchronized void register(SteamForgeOfGods forge) {
        OWNERS.put(forge.getData(), new WeakReference<>(forge));
    }

    public static synchronized boolean interceptUnlock(ForgeOfGodsData data, ForgeOfGodsUpgrade upgrade) {
        WeakReference<SteamForgeOfGods> reference = OWNERS.get(data);
        SteamForgeOfGods forge = reference == null ? null : reference.get();
        if (forge == null) return false;
        forge.tryUnlockSteamUpgrade(upgrade);
        return true;
    }
}
