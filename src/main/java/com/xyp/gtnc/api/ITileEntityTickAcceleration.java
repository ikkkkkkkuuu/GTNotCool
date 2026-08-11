package com.xyp.gtnc.api;

/**
 * Optional fast path used by the Eternity Vial accelerator. Implementations return {@code true} when they handled the
 * acceleration themselves; returning {@code false} asks the accelerator to fall back to repeated tile ticks.
 */
public interface ITileEntityTickAcceleration {

    boolean tickAcceleration(int multiplier);

    default int getTickAcceleratedRate() {
        return 1;
    }
}
