package com.xyp.gtnc.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.diagnostics.CraftingDiagnosticSessionId;

@Mixin(value = CraftingCPUCluster.TaskProgress.class, remap = false)
public interface AccessorTaskProgress {

    @Accessor
    long getValue();

    @Accessor
    void setValue(long value);

    @Invoker("consumeCraftSession")
    CraftingDiagnosticSessionId invokeConsumeCraftSession();
}
