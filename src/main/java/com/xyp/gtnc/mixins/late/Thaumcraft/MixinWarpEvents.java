package com.xyp.gtnc.mixins.late.Thaumcraft;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Config.Config;

import thaumcraft.common.lib.WarpEvents;

/**
 * 禁止神秘时代(Thaumcraft)的「扭曲事件」发生，同时完全不影响扭曲值。
 * <p>
 * 原版 {@link WarpEvents#checkWarpEvent(EntityPlayer)} 的结构：
 * <ul>
 * <li>方法开头 {@code int r = player.worldObj.rand.nextInt(100);}，随后
 * {@code if (warpCounter > 0 && warp > 0 && r <= Math.sqrt(warpCounter))} 才进入事件块——
 * 事件块负责随机施加负面药水、生成心灵蜘蛛/古神守卫、召唤迷雾、发送幻觉聊天、强制解锁古神研究。</li>
 * <li>方法末尾(事件块之外，永远执行) {@code addWarpTemp(-1)} + 同步包，这是临时扭曲的衰减，
 * 属于扭曲<b>数值</b>的正常维护。</li>
 * </ul>
 * 这里 {@link Redirect} 那次 {@code rand.nextInt(100)}，开关开启时返回一个极大值，
 * 使 {@code r <= Math.sqrt(warpCounter)}(右侧最大约 10)恒不成立，于是整个事件块被跳过；
 * 而末尾的临时扭曲衰减照常执行，因此 perm/temp/sticky 三种扭曲值与原版完全一致。
 * <p>
 * 相比直接在 HEAD 取消整个方法，本做法保留了扭曲值的衰减逻辑，避免临时扭曲异常累积。
 * <p>
 * 由 {@link Config#disableWarpEvents} 控制，默认开启。
 */
@Mixin(WarpEvents.class)
public class MixinWarpEvents {

    @Redirect(
        method = "checkWarpEvent",
        at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0),
        remap = false)
    private static int gtnc$suppressWarpEventRoll(Random rand, int bound) {
        if (Config.disableWarpEvents) {
            // 返回大于任何可能的 sqrt(warpCounter) 的值，令事件触发条件恒为假。
            return Integer.MAX_VALUE;
        }
        return rand.nextInt(bound);
    }
}
