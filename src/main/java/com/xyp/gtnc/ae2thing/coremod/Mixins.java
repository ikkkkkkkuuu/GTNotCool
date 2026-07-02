package com.xyp.gtnc.ae2thing.coremod;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import com.xyp.gtnc.ae2thing.integration.Mods;

public enum Mixins implements IMixins {

    AE_CLIENT(new MixinBuilder()
        .addClientMixins(
            "ae.AccessorGuiScrollbar",
            "ae.MixinAEBaseGui",
            "ae.MixinContainerCraftConfirm",
            "ae.MixinCraftingCPUCluster",
            "ae.MixinGuiCraftAmount",
            "ae.MixinGuiCraftConfirm",
            "ae.MixinItemRepo",
            "ae.MixinTileIOPort",
            "ae.MixinContainerCraftAmount")
        .addRequiredMod(Mods.AE2)
        .setPhase(Phase.LATE)),

    AE_SERVER(new MixinBuilder()
        .addCommonMixins(
            "ae.MixinContainerCraftConfirm",
            "ae.MixinCraftingCPUCluster",
            "ae.MixinTileIOPort",
            "ae.MixinContainerCraftAmount")
        .addRequiredMod(Mods.AE2)
        .setPhase(Phase.LATE)),

    NEI(new MixinBuilder()
        .addClientMixins(
            "nei.MixinGuiContainerManager",
            "nei.MixinGuiOverlayButton",
            "nei.MixinIOverlayHandler",
            "nei.MixinPanelWidget",
            "nei.MixinRecipeItemInputHandler")
        .addRequiredMod(Mods.NOT_ENOUGH_ITEMS)
        .setPhase(Phase.LATE));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
