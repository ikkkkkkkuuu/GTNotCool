package com.xyp.gtnc.utils;

import org.jetbrains.annotations.Nullable;

import com.xyp.gtnc.Common.machines.multiblock.QuantumComputer;

import appeng.me.cluster.implementations.CraftingCPUCluster;

/**
 * Duck-typing bridge onto AE2's {@link CraftingCPUCluster}, implemented by the Quantum Computer's virtual-CPU mixin.
 * Lets the {@link QuantumComputer} treat CPU clusters it spawns as owned "virtual" CPUs (no backing tile), driving
 * their storage/accelerator capacity, owner, name and destruction state. Ported from GT-Not-Leisure.
 */
public interface ECPUCluster {

    static ECPUCluster from(final CraftingCPUCluster cluster) {
        return (ECPUCluster) (Object) cluster;
    }

    void ec$setAvailableStorage(final long availableStorage);

    void ec$setAccelerators(final int accelerators);

    QuantumComputer ec$getVirtualCPUOwner();

    void ec$setVirtualCPUOwner(@Nullable final QuantumComputer isVirtualCPUOwner);

    void ec$markDestroyed();

    void ec$setName(String name);
}
