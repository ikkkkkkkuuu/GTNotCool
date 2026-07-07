package com.xyp.gtnc.mixins.late.AppliedEnergistics.quantumComputer;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xyp.gtnc.Common.machines.multiblock.QuantumComputer;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;
import appeng.crafting.CraftingLink;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;

@Mixin(value = CraftingGridCache.class, remap = false)
public abstract class MixinCraftingGridCache {

    @Shadow
    @Final
    private IGrid grid;

    @Shadow
    @Final
    private Set<CraftingCPUCluster> craftingCPUClusters;

    @Shadow
    public abstract void addLink(final CraftingLink link);

    /**
     * setPatternsFromCraftingMethods 遍历每张样板的 getAEOutputs()，对每个元素直接 out.copy() 不判空
     * （rv3-977 CraftingGridCache:340）。若某张样板输出数组含 null——空输出槽、被删除的物品（整合包升级后
     * 物品重 ID/移除，AEItemStack.create(已删物品) 返回 null）、或自定义 details 返回含 null 的数组——就会
     * 每 tick NPE 刷屏，AE 网络重建陷入 崩→重触发 死循环，拖死主线程 TPS，甚至开服即崩（存档无法进入）。
     * <p>
     * 这里在 AE2 侧统一拦截 getAEOutputs()，把 null 元素剔除后再交给循环，覆盖所有样板来源（本模组的
     * 通配符/超级样板、原版 AE2、AE2FC、以及升级后失效的旧样板），是比逐个样板类打补丁更彻底的防御。
     */
    @Redirect(
        method = "setPatternsFromCraftingMethods()V",
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

    @Inject(method = "updateCPUClusters()V", at = @At("RETURN"), require = 1)
    private void injectUpdateCPUClusters(final CallbackInfo ci) {
        for (final IGridNode ecNode : grid.getMachines(QuantumComputer.class)) {
            final var ec = (QuantumComputer) ecNode.getMachine();
            final List<CraftingCPUCluster> cpus = ec.getCPUs();

            for (CraftingCPUCluster cpu : cpus) {
                this.craftingCPUClusters.add(cpu);

                if (cpu.getLastCraftingLink() != null) {
                    this.addLink((CraftingLink) cpu.getLastCraftingLink());
                }
            }
        }
    }

}
