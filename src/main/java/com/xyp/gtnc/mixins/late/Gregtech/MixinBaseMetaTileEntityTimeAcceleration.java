package com.xyp.gtnc.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.xyp.gtnc.api.ITileEntityTickAcceleration;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.CommonBaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.common.tileentities.machines.multi.MTEBrickedBlastFurnace;

/** Advances GT recipe progress directly instead of recursively running the entire base tile tick. */
@Mixin(value = BaseMetaTileEntity.class, remap = false)
public abstract class MixinBaseMetaTileEntityTimeAcceleration extends CommonBaseMetaTileEntity
    implements ITileEntityTickAcceleration {

    private static final float GT_ACCELERATION_EFFICIENCY = 0.8F;

    @Shadow
    public abstract int getProgress();

    @Shadow
    public abstract int getMaxProgress();

    @Shadow
    public abstract IMetaTileEntity getMetaTileEntity();

    @Shadow
    public abstract boolean isActive();

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean tickAcceleration(int multiplier) {
        if (!isActive()) return true;

        int maxProgress = getMaxProgress();
        if (maxProgress < 2) return true;

        int acceleratedTicks = Math.max(1, (int) (multiplier * GT_ACCELERATION_EFFICIENCY));
        int progress = Math.min(maxProgress, getProgress() + acceleratedTicks);
        IMetaTileEntity metaTileEntity = getMetaTileEntity();

        if (metaTileEntity instanceof MTEBasicMachine) {
            ((MTEBasicMachine) metaTileEntity).mProgresstime = progress;
            return true;
        }
        if (metaTileEntity instanceof MTEMultiBlockBase) {
            ((MTEMultiBlockBase) metaTileEntity).mProgresstime = progress;
            return true;
        }
        if (metaTileEntity instanceof MTEBrickedBlastFurnace) {
            ((MTEBrickedBlastFurnace) metaTileEntity).mProgresstime = progress;
            return true;
        }

        return false;
    }
}
