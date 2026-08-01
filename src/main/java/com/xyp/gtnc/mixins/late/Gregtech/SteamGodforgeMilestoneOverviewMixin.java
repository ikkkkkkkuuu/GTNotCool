package com.xyp.gtnc.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.SteamGodforgeMilestonePanel;
import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamForgeOfGods;

import gregtech.common.gui.modularui.multiblock.godforge.panel.MilestonePanel;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncHypervisor;

@Mixin(value = MilestonePanel.class, remap = false)
public class SteamGodforgeMilestoneOverviewMixin {

    @Inject(method = "openPanel", at = @At("HEAD"), cancellable = true)
    private static void gtnc$openSteamPanel(SyncHypervisor hypervisor, CallbackInfoReturnable<ModularPanel> cir) {
        if (hypervisor.getMultiblock() instanceof SteamForgeOfGods) {
            cir.setReturnValue(SteamGodforgeMilestonePanel.openPanel(hypervisor));
        }
    }
}
