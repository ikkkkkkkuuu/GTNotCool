package com.xyp.gtnc.mixins.late.Gregtech;

import static com.xyp.gtnc.Config.Config.gtToolsCraftingDurability;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import gregtech.api.items.MetaGeneratedTool;

/**
 * GT工具合成耐久度倍率修改
 * <p>
 * 拦截 {@link MetaGeneratedTool#getContainerItem} 中的 doDamage 调用，
 * 将默认耐久消耗除以配置的倍率因子（{@code gtToolsCraftingDurability}），
 * 减少工具在合成配方中的损耗。
 */
@Mixin(value = MetaGeneratedTool.class, remap = false)
public abstract class GTMetaTools {

    @Shadow
    public abstract boolean doDamage(ItemStack aStack, long aAmount);

    @Redirect(
        method = "getContainerItem*",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/items/MetaGeneratedTool;doDamage(Lnet/minecraft/item/ItemStack;J)Z"))
    private boolean onDamagingCraftingTool(MetaGeneratedTool instance, ItemStack tNewDamage, long tStats) {
        return doDamage(tNewDamage, (int) Math.ceil(tStats / gtToolsCraftingDurability));
    }
}
