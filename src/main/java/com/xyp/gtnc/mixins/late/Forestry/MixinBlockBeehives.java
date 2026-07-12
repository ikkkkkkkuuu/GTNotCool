package com.xyp.gtnc.mixins.late.Forestry;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Common.machines.bee.BeeBreedingHelper;
import com.xyp.gtnc.Config.Config;

import forestry.apiculture.blocks.BlockBeehives;

/**
 * 让世界蜂巢破坏掉落的蜜蜂本身就是「满基因」——把基因真正写进蜂的 NBT，
 * 分析仪可见、后代能稳定遗传（breed true），不再是「读时覆写、分析显示原样」。
 * <p>
 * 注入点 {@link BlockBeehives#getDrops}（Forge 加的 harvest 掉落方法）是<b>所有蜂巢掉落的唯一出口</b>：
 * 它汇总该蜂巢所有 {@code IHiveDrop}（公主 / 雄蜂 / 附加产物，含 ExtraBees 等附属自己的实现）后返回一个
 * {@code ArrayList<ItemStack>}。在 RETURN 处遍历该列表，把其中每只蜂用
 * {@link BeeBreedingHelper#maximizeBeeStack} 就地重建为满基因（保留物种与蜂型），非蜂物品原样保留。
 * <p>
 * <b>与本 mod 的蜜蜂杂交机彻底隔离</b>：杂交机走 {@code createDrone/createPrincess → templateAsGenome}，
 * 完全不经过 {@code getDrops}，本 mixin 只作用于世界蜂巢掉落这一条路径。
 * <p>
 * 由 {@link Config#enableBeeMaxGenomeOnHiveDrop} 控制，默认开启。
 */
@Mixin(BlockBeehives.class)
public abstract class MixinBlockBeehives {

    @Inject(
        method = "getDrops(Lnet/minecraft/world/World;IIIII)Ljava/util/ArrayList;",
        at = @At("RETURN"),
        remap = false,
        require = 1)
    private void gtnc$maxGenomeHiveDrops(CallbackInfoReturnable<ArrayList<ItemStack>> cir) {
        if (!Config.enableBeeMaxGenomeOnHiveDrop) return;
        ArrayList<ItemStack> drops = cir.getReturnValue();
        if (drops == null || drops.isEmpty()) return;
        for (int i = 0; i < drops.size(); i++) {
            ItemStack stack = drops.get(i);
            if (stack == null) continue;
            ItemStack maxed = BeeBreedingHelper.maximizeBeeStack(stack);
            if (maxed != stack) {
                drops.set(i, maxed);
            }
        }
    }
}
