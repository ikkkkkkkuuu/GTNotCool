package com.xyp.gtnc.mixins.early.energyMonitor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Common.machines.basicMachine.monitor.EnergyMonitorRegistry;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;

@Mixin(value = CommonBaseMetaTileEntity.class, remap = false)
public class MixinCommonMetaTileEntityEnergyMonitor {

    @Inject(method = "handleFirstTick", at = @At("TAIL"))
    private void gtnc$registerEnergyMonitorEntry(boolean isServerSide, CallbackInfo callbackInfo) {
        MetaTileEntity metaTileEntity = gtnc$resolveTrackedMetaTileEntity();
        if (metaTileEntity != null) {
            EnergyMonitorRegistry.register(metaTileEntity);
        }
    }

    private MetaTileEntity gtnc$resolveTrackedMetaTileEntity() {
        if (!(this instanceof IGregTechTileEntity gregTechTileEntity)) {
            return null;
        }
        IMetaTileEntity metaTileEntity = gregTechTileEntity.getMetaTileEntity();
        return metaTileEntity instanceof MetaTileEntity trackedMetaTileEntity ? trackedMetaTileEntity : null;
    }
}
