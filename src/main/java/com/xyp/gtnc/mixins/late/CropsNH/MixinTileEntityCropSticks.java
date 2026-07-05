package com.xyp.gtnc.mixins.late.CropsNH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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
        return 8;
    }
}
