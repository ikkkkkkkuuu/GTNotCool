package com.xyp.gtnc.mixins.late.Gregtech;

import java.math.BigInteger;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamGodforgePower;

import gregtech.common.misc.WirelessNetworkManager;
import tectech.thing.metaTileEntity.multi.godforge.MTEMoltenModule;

@Mixin(targets = "tectech.thing.metaTileEntity.multi.godforge.MTEMoltenModule$1", remap = false)
public class SteamGodforgeMoltenPowerMixin {

    @Shadow
    @Final
    private MTEMoltenModule this$0;

    @Redirect(
        method = "validateRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/misc/WirelessNetworkManager;getUserEU(Ljava/util/UUID;)Ljava/math/BigInteger;"))
    private BigInteger gtnc$getSteamBalance(UUID user) {
        if (this$0 instanceof SteamGodforgePower.ControllerAware module) {
            return SteamGodforgePower.getAvailableEU(module, user);
        }
        return WirelessNetworkManager.getUserEU(user);
    }

    @Redirect(
        method = "onRecipeStart",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/misc/WirelessNetworkManager;addEUToGlobalEnergyMap(Ljava/util/UUID;Ljava/math/BigInteger;)Z"))
    private boolean gtnc$drainSteam(UUID user, BigInteger euDelta) {
        if (this$0 instanceof SteamGodforgePower.ControllerAware module) {
            return SteamGodforgePower.drainEnergyAmount(module, user, euDelta.abs());
        }
        return WirelessNetworkManager.addEUToGlobalEnergyMap(user, euDelta);
    }
}
