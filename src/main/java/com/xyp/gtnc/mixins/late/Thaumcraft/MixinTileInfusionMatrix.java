package com.xyp.gtnc.mixins.late.Thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Config.Config;

import thaumcraft.common.tiles.TileInfusionMatrix;

/**
 * 注魔祭坛（Infusion Matrix）注魔时不失稳。
 * <p>
 * 原版 {@link TileInfusionMatrix#craftCycle()} 里，失稳值 {@code instability} 越高，
 * {@code worldObj.rand.nextInt(500) <= instability} 越容易命中，从而触发掉物/闪电/爆炸/涨 warp/生成通量等负面事件
 * （见 {@code inEvEjectItem/inEvZap/inEvHarm/inEvWarp}）。而合成进度（消耗 essentia、消耗基座物品、最终 craftingFinish）
 * 与失稳无关——失稳只决定"是否插入一次坏事件"。
 * <p>
 * 这里在 craftCycle 的 HEAD 用 {@code @Inject} 把本 tile 的 {@code instability} 归零（不 cancel 方法本体）：
 * 合成照常推进、正常完成，但失稳判定 {@code instability > 0} 永不成立，坏事件（含注魔侧通量生成）永不触发。
 * <p>
 * {@code instability} 是 public 字段，运行时本 mixin 已并入目标类，故 {@code (TileInfusionMatrix)(Object)this}
 * 强转后可直接写该字段。
 * <p>
 * 由 {@link Config#tcInfusionNoInstability} 控制，默认开启。
 */
@Mixin(value = TileInfusionMatrix.class, remap = false)
public abstract class MixinTileInfusionMatrix {

    @Inject(method = "craftCycle", at = @At("HEAD"), require = 1)
    private void gtnc$noInstability(CallbackInfo ci) {
        if (Config.tcInfusionNoInstability) {
            ((TileInfusionMatrix) (Object) this).instability = 0;
        }
    }
}
