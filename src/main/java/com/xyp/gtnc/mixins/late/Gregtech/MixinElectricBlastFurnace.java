package com.xyp.gtnc.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.logic.ProcessingLogic;
import gregtech.common.tileentities.machines.multi.MTEElectricBlastFurnace;

@Mixin(value = MTEElectricBlastFurnace.class, remap = false)
public abstract class MixinElectricBlastFurnace {

    @Shadow
    private int mHeatingCapacity;

    @Inject(method = "createProcessingLogic", at = @At("RETURN"), cancellable = false)
    private void gtnc$injectSpeedBonus(CallbackInfoReturnable<ProcessingLogic> cir) {
        cir.getReturnValue()
            .setSpeedBonus(0.3);
    }

    @Inject(method = "checkMachine", at = @At("TAIL"))
    private void gtnc$increaseHeat(CallbackInfo ci) {
        mHeatingCapacity += 1300;
    }
}
