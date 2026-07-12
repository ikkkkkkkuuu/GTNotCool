package com.xyp.gtnc.mixins.late.Thaumcraft;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Config.Config;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;

/**
 * 节点不衰减 + vis 无限。
 * <p>
 * {@link VisNetHandler#drainVis(World, int, int, int, Aspect, int)} 是所有从 vis 网络抽取魔力的统一入口：
 * 它遍历附近节点，对每个节点调用 {@code node.consumeVis(aspect, amount)} 真正扣减节点存量，返回实际抽到的量。
 * <p>
 * 这里在 HEAD 用 {@code @Inject} 直接返回请求量 {@code amount}：调用方（法杖、法术、各种耗 vis 的 tile）
 * 永远认为抽取全额成功，而 {@code node.consumeVis} 根本不会被执行，故节点/中继存量不减 —— 等效"节点不衰减 + vis 无限"。
 * <p>
 * 由 {@link Config#tcInfiniteVis} 控制，默认开启。
 */
@Mixin(value = VisNetHandler.class, remap = false)
public class MixinVisNetHandler {

    @Inject(method = "drainVis", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void gtnc$infiniteVis(World world, int x, int y, int z, Aspect aspect, int amount,
        CallbackInfoReturnable<Integer> cir) {
        if (Config.tcInfiniteVis) {
            cir.setReturnValue(amount);
        }
    }
}
