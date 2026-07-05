package com.xyp.gtnc.mixins.late.CropsNH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import ic2.core.crop.TileEntityCrop;

@Mixin(TileEntityCrop.class)
public class MixinTileEntityCrop {

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 256), remap = false)
    private static int modifyTickRateInStaticInit(int original) {
        // tickRate 是生长“间隔”(ticker % tickRate == 0 才生长)，越小越快。
        // 原版 256，改成 64 = 原版速度的 4 倍。
        return 8;
    }
}
