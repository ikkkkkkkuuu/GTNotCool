package com.xyp.gtnc.mixins.late.EnderIO;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.api.ITileEntityTickAcceleration;

import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.power.ICapacitor;

/** Scales EnderIO's per-tick energy input only while accelerated updates are executing. */
@Mixin(value = AbstractPoweredMachineEntity.class, remap = false)
public abstract class MixinPoweredMachineEnergyAcceleration {

    @Redirect(
        method = "getMaxEnergyRecieved",
        at = @At(value = "INVOKE", target = "Lcrazypants/enderio/power/ICapacitor;getMaxEnergyReceived()I"))
    private int gtnc$scaleEnergyInput(ICapacitor capacitor) {
        int rate = this instanceof ITileEntityTickAcceleration
            ? ((ITileEntityTickAcceleration) this).getTickAcceleratedRate()
            : 1;
        long scaled = (long) capacitor.getMaxEnergyReceived() * Math.max(1, rate);
        return (int) Math.min(Integer.MAX_VALUE, scaled);
    }
}
