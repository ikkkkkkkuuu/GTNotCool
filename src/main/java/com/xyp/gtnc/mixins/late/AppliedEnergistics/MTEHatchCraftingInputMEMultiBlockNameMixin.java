package com.xyp.gtnc.mixins.late.AppliedEnergistics;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

/**
 * Populate and refresh the recipe map on both {@link MTEHatchCraftingInputME} (vanilla) and
 * {@link SuperMTEHatchCraftingInputME} (custom) so their AE2 interface-terminal names reflect the
 * controller's current recipe map and update on mode switches.
 * <p>
 * GT5U never sets {@code mRecipeMap} on crafting-input hatches; {@code SuperMTEHatchCraftingInputME}
 * uses a separate {@code controllerRecipeMap} field set only by GTNC/Steam base classes — so neither
 * hatch gets its map populated on vanilla controllers without this mixin.
 */
@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MTEHatchCraftingInputMEMultiBlockNameMixin {

    @Shadow
    public ArrayList<IDualInputHatch> mDualInputHatches;

    @Shadow
    public abstract RecipeMap<?> getRecipeMap();

    // ── structure formation ──────────────────────────────────────────────────

    @Inject(method = "addToMachineList", at = @At("RETURN"))
    private void gtnc$captureRecipeMapOnAdd(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        gtnc$feedRecipeMap(aTileEntity);
    }

    @Inject(method = "addInputBusToMachineList", at = @At("RETURN"))
    private void gtnc$captureRecipeMapOnAddBus(IGregTechTileEntity aTileEntity, int aBaseCasingIndex,
        CallbackInfoReturnable<Boolean> cir) {
        gtnc$feedRecipeMap(aTileEntity);
    }

    private void gtnc$feedRecipeMap(IGregTechTileEntity aTileEntity) {
        if (aTileEntity == null) return;
        RecipeMap<?> map = getRecipeMap();
        if (map == null) return;
        if (aTileEntity.getMetaTileEntity() instanceof SuperMTEHatchCraftingInputME superHatch) {
            // SuperHatch uses controllerRecipeMap (separate from mRecipeMap) for its getRawName logic.
            superHatch.setControllerRecipeMap(map);
        } else if (aTileEntity.getMetaTileEntity() instanceof MTEHatchCraftingInputME hatch) {
            hatch.mRecipeMap = map;
        }
    }

    // ── mode switch ──────────────────────────────────────────────────────────

    /**
     * After {@code setMachineMode} updates {@code machineMode}, push the new {@link #getRecipeMap()} to
     * every crafting-input hatch (both vanilla and custom) so AE2's container detects the raw-name change
     * and fires a rename packet.
     */
    @Inject(method = "setMachineMode", at = @At("TAIL"))
    private void gtnc$refreshRecipeMapOnModeSwitch(int index, CallbackInfo ci) {
        RecipeMap<?> map = getRecipeMap();
        if (map == null) return;
        for (IDualInputHatch hatch : mDualInputHatches) {
            if (hatch instanceof SuperMTEHatchCraftingInputME superHatch) {
                superHatch.setControllerRecipeMap(map);
            } else if (hatch instanceof MTEHatchCraftingInputME craftingHatch) {
                craftingHatch.mRecipeMap = map;
            }
        }
    }
}
