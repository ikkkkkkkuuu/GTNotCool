package com.xyp.gtnc.mixins.late.Gregtech;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.xyp.gtnc.Config.Config;

import appeng.api.storage.data.IAEItemStack;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputBusME;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

/**
 * ME输出总线（物品）信息显示覆写
 * <p>
 * 覆写 {@link MTEHatchOutputBusME#getInfoData} 方法，
 * 在配置启用时将缓存容量显示为无限（∞），
 * 并列出当前缓存中的所有物品及其数量。
 */
@Mixin(value = MTEHatchOutputBusME.class, remap = false)
public abstract class HatchOutputBusMEMixin {

    @Shadow(remap = false)
    @Final
    private MTEHatchOutputMEBase<IAEItemStack> provider;

    /**
     * @author eyeofharmonybuffer
     * @reason 覆写信息显示以显示无限容量
     */
    @Inject(method = "getInfoData", at = @At("HEAD"), cancellable = true)
    private void onGetInfoData(CallbackInfoReturnable<String[]> cir) {
        if (!Config.OutPutBusMEEnable) return;

        List<String> ss = new ArrayList<>();

        // 获取在线状态
        try {
            boolean isActive = ((MTEHatchOutputBusME) (Object) this).isActive();
            ss.add(
                "The bus is " + (isActive ? EnumChatFormatting.GREEN + "online" : EnumChatFormatting.RED + "offline")
                    + EnumChatFormatting.RESET);
        } catch (Exception e) {
            ss.add("The bus status unknown");
        }

        ss.add("Item cache capacity: " + EnumChatFormatting.GOLD + "∞" + EnumChatFormatting.RESET);

        // 使用 getCacheList() 获取缓存列表
        List<IAEItemStack> cacheList = provider.getCacheList();
        if (cacheList.isEmpty()) {
            ss.add("The bus has no cached items");
        } else {
            ss.add("The bus contains cached stacks:");
            int counter = 0;
            for (IAEItemStack item : cacheList) {
                ss.add(
                    item.getItem()
                        .getItemStackDisplayName(item.getItemStack()) + ": "
                        + EnumChatFormatting.GOLD
                        + NumberFormatUtil.formatNumber(item.getStackSize())
                        + EnumChatFormatting.RESET);
                counter++;
                if (counter > 100) break;
            }
        }

        cir.setReturnValue(ss.toArray(new String[0]));
        cir.cancel();
    }
}
