package com.xyp.gtnc.mixins.late.EnderIO;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.enderio.core.common.TileEntityEnder;
import com.xyp.gtnc.api.ITileEntityTickAcceleration;

/** Lets EnderIO machines bypass their one-update-per-world-tick guard while the Eternity Vial is active. */
@Mixin(value = TileEntityEnder.class, remap = false)
public abstract class MixinTileEntityEnderTimeAcceleration implements ITileEntityTickAcceleration {

    @Shadow
    private long lastUpdate;

    @Shadow(remap = true)
    public abstract void updateEntity();

    @Unique
    private int gtnc$acceleratedRate = 1;

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public int getTickAcceleratedRate() {
        return gtnc$acceleratedRate;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean tickAcceleration(int multiplier) {
        long deadline = System.nanoTime() + 1_000_000L;
        gtnc$acceleratedRate = multiplier;
        try {
            for (int i = 0; i < multiplier && System.nanoTime() <= deadline; i++) {
                lastUpdate = -1L;
                updateEntity();
            }
        } finally {
            gtnc$acceleratedRate = 1;
        }
        return true;
    }
}
