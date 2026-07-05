package com.xyp.gtnc.mixins.late.CropsNH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;

@Mixin(TileEntityCropSticks.class)
public class MixinTileEntityCropSticks {

    @ModifyConstant(
        method = "updateEntity", // 修改 updateEntity 方法中的常量
        constant = @Constant(intValue = 256),
        remap = true)
    private int modifyTickRateInUpdateEntity(int original) {
        // TICK_RATE 是生长“间隔”(每隔多少 tick 生长一次)，越小越快。
        // 原版 256，改成 64 = 间隔 1/4 = 4 倍速。
        return 4;
    }

    // 彻底禁用杂草：spawnWeed(长杂草)与 spreadWeed(向邻居传播杂草/生成高草)是杂草产生的唯一入口，
    // 直接在方法头 cancel，无论 onGrowthTick 里的概率判定怎么走都不会真的生成杂草。
    // 比改 weedSpawnChance/weedSpreadChance 更彻底(改概率只是变稀有,不是消除)。
    @Inject(method = "spawnWeed", at = @At("HEAD"), cancellable = true, remap = false)
    private void gtnc$disableSpawnWeed(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "spreadWeed", at = @At("HEAD"), cancellable = true, remap = false)
    private void gtnc$disableSpreadWeed(CallbackInfo ci) {
        ci.cancel();
    }
}
