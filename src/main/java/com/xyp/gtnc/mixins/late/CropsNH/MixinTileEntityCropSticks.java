package com.xyp.gtnc.mixins.late.CropsNH;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.tileentity.TileEntityCropSticks;
import com.xyp.gtnc.Config.Config;

@Mixin(TileEntityCropSticks.class)
public abstract class MixinTileEntityCropSticks {

    @Shadow(remap = false)
    private ISeedData seed;

    @Shadow(remap = false)
    private int growthProgress;

    @Shadow(remap = false)
    private boolean isDirty;

    @Shadow(remap = false)
    public abstract boolean hasCrop();

    @ModifyConstant(
        method = "updateEntity", // 修改 updateEntity 方法中的常量
        constant = @Constant(intValue = 256),
        remap = true)
    private int modifyTickRateInUpdateEntity(int original) {
        // TICK_RATE 是生长“间隔”(每隔多少 tick 生长一次)，越小越快。
        // 原版 256，改成 64 = 间隔 1/4 = 4 倍速。
        return 1;
    }

    // 瞬间成熟：在 doGrowth 头部直接把 growthProgress 拉满到 getGrowthDuration()，
    // 然后 cancel 掉原本按 calcGrowthRate() 逐步累加的逻辑。比调 TICK_RATE 或放大增量更彻底。
    // 由 Config.enableCropInstantGrowth 控制，默认关闭；关闭时走上面的 TICK_RATE 提速。
    @Inject(method = "doGrowth", at = @At("HEAD"), cancellable = true, remap = false)
    private void gtnc$instantGrowth(CallbackInfo ci) {
        if (!Config.enableCropInstantGrowth) return;
        if (!this.hasCrop()) return;
        int duration = this.seed.getCrop()
            .getGrowthDuration();
        if (this.growthProgress < duration) {
            this.growthProgress = duration;
            // isDirty 是 CropsNH 自有标志：updateEntity 每 tick 检测到它为 true 会自动
            // markDirty()+markForUpdate()。走原生路径，避免直接 shadow 原版 markDirty() 带来的
            // refmap 映射隐患。
            this.isDirty = true;
        }
        ci.cancel();
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
