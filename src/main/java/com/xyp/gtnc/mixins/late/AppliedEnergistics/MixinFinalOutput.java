package com.xyp.gtnc.mixins.late.AppliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;

/**
 * CraftingCPUCluster$finalOutput.addOutputs 在提交合成任务(submitJob → finalOutput.init)时，会 clone 匹配到的
 * 样板 getAEOutputs()，再逐个 out.copy()——不判空（rv3-977 CraftingCPUCluster:2290）。若这张样板的输出数组含
 * null（空输出槽、被删物品、或自定义 details 产生的 null），点击“开始计划”即触发 NPE，把玩家踢回主界面。
 *
 * 这是与 [MixinCraftingGridCache.gtnc$stripNullOutputs] 同一颗雷的另一条路径：那处堵的是每 tick 的样板缓存重建
 * (setPatternsFromCraftingMethods)，这处堵的是提交合成任务时的最终输出计算。两处都在 AE2 侧统一剔除 null，
 * 覆盖所有样板来源（本模组的通配符/超级样板、原版 AE2、AE2FC），比逐个样板类打补丁更彻底。
 */
@Mixin(targets = "appeng.me.cluster.implementations.CraftingCPUCluster$finalOutput", remap = false)
public abstract class MixinFinalOutput {

    @Redirect(
        method = "addOutputs",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingPatternDetails;getAEOutputs()[Lappeng/api/storage/data/IAEStack;"),
        remap = false,
        require = 1)
    private IAEStack<?>[] gtnc$stripNullOutputs(final ICraftingPatternDetails details) {
        final IAEStack<?>[] outputs = details.getAEOutputs();
        if (outputs == null || outputs.length == 0) return outputs;
        int nonNull = 0;
        for (IAEStack<?> s : outputs) {
            if (s != null) nonNull++;
        }
        if (nonNull == outputs.length) return outputs;
        final IAEStack<?>[] result = new IAEStack<?>[nonNull];
        int idx = 0;
        for (IAEStack<?> s : outputs) {
            if (s != null) result[idx++] = s;
        }
        return result;
    }
}
