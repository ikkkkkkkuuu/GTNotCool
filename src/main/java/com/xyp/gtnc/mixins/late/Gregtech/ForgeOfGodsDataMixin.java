package com.xyp.gtnc.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamGodforgeUpgradeHooks;

import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

@Mixin(value = ForgeOfGodsData.class, remap = false)
public class ForgeOfGodsDataMixin {

    @Inject(method = "unlockUpgrade", at = @At("HEAD"), cancellable = true)
    private void gtnc$routeSteamUpgrade(ForgeOfGodsUpgrade upgrade, CallbackInfo ci) {
        if (SteamGodforgeUpgradeHooks.interceptUnlock((ForgeOfGodsData) (Object) this, upgrade)) ci.cancel();
    }
}
