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

import appeng.api.storage.data.IAEFluidStack;
import gregtech.common.tileentities.machines.outputme.MTEHatchOutputME;
import gregtech.common.tileentities.machines.outputme.base.MTEHatchOutputMEBase;

/**
 * ME输出仓（流体）信息显示覆写
 * <p>
 * 覆写 {@link MTEHatchOutputME#getInfoData} 方法，
 * 在配置启用时将流体缓存容量显示为无限（∞ L），
 * 并列出当前缓存中的所有流体及其数量。
 */
@Mixin(value = MTEHatchOutputME.class, remap = false)
public abstract class HatchOutputMEMixin {

    @Shadow(remap = false)
    @Final
    private MTEHatchOutputMEBase<IAEFluidStack> provider;

    /**
     * @author eyeofharmonybuffer
     * @reason 覆写信息显示以显示无限容量
     */
    @Inject(method = "getInfoData", at = @At("HEAD"), cancellable = true)
    private void onGetInfoData(CallbackInfoReturnable<String[]> cir) {
        if (!Config.OutPutHatchMEEnable) return;

        List<String> ss = new ArrayList<>();

        // 获取在线状态
        try {
            boolean isActive = ((MTEHatchOutputME) (Object) this).isActive();
            ss.add(
                "The hatch is " + (isActive ? EnumChatFormatting.GREEN + "online" : EnumChatFormatting.RED + "offline")
                    + EnumChatFormatting.RESET);
        } catch (Exception e) {
            ss.add("The hatch status unknown");
        }

        ss.add("Fluid cache capacity: " + EnumChatFormatting.GOLD + "∞ L" + EnumChatFormatting.RESET);

        // 使用 getCacheList() 获取缓存列表
        List<IAEFluidStack> cacheList = provider.getCacheList();
        if (cacheList.isEmpty()) {
            ss.add("The hatch has no cached fluids");
        } else {
            ss.add("The hatch contains cached fluids:");
            int counter = 0;
            for (IAEFluidStack fluid : cacheList) {
                ss.add(
                    fluid.getFluid()
                        .getLocalizedName(fluid.getFluidStack()) + ": "
                        + EnumChatFormatting.GOLD
                        + NumberFormatUtil.formatNumber(fluid.getStackSize())
                        + "L"
                        + EnumChatFormatting.RESET);
                counter++;
                if (counter > 100) break;
            }
        }

        cir.setReturnValue(ss.toArray(new String[0]));
        cir.cancel();
    }
}
