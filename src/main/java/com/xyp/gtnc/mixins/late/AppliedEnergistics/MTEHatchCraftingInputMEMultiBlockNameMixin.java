package com.xyp.gtnc.mixins.late.AppliedEnergistics;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

/**
 * 在原版 {@link MTEHatchCraftingInputME} 上填充并刷新 {@code mRecipeMap} 以便
 * {@link MTEHatchCraftingInputMENameMixin} 可以读取它以显示正确的配方类别名称。
 * <p>
 * GT5U从不在制作输入舱口设置{@code mRecipeMap}——所有明确设置的地方
 * {@code继续} 经过 {@code MTEHatchCraftingInputME}。两个注入点覆盖了两个常见的加法器路径
 * （机器将输入总线元素连接到它们偏好的那一个），加上一个{@code setMachineMode}钩子
 * 当控制器切换模式时推送新映射，保持AE2接口-终端名称同步。
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
        if (aTileEntity.getMetaTileEntity() instanceof MTEHatchCraftingInputME hatch) {
            RecipeMap<?> map = getRecipeMap();
            if (map != null) hatch.mRecipeMap = map;
        }
    }

    // ── mode switch ──────────────────────────────────────────────────────────

    /**
     * After {@code setMachineMode} updates {@code machineMode}, push the new {@link #getRecipeMap()} to every
     * vanilla crafting-input hatch so AE2's container detects the raw-name change and fires a rename packet.
     */
    @Inject(method = "setMachineMode", at = @At("TAIL"))
    private void gtnc$refreshRecipeMapOnModeSwitch(int index, CallbackInfo ci) {
        RecipeMap<?> map = getRecipeMap();
        if (map == null) return;
        for (IDualInputHatch hatch : mDualInputHatches) {
            if (hatch instanceof MTEHatchCraftingInputME craftingHatch) {
                craftingHatch.mRecipeMap = map;
            }
        }
    }
}
