package com.xyp.gtnc.mixins.late.Gregtech;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ggfab.mte.MTEAdvAssLine;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipe.RecipeAssemblyLine;
import gregtech.api.util.GTUtility;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;

/**
 * 让进阶装配线({@link MTEAdvAssLine})的物品/流体输入改为「池子式」：每步料可来自任意输入总线，
 * 且支持从样板输入总成(ME 样板/Super/Slave，即 {@link IDualInputHatch})取用已备料的物品与流体。
 * <p>
 * 进阶装配线是交错流水线：切片 id 固定从第 id 个输入总线取「配方第 id 步」的料
 * (见 {@code Slice.start()} → {@code getInputBusContent(id)})。这里把取料/验证改为「在所有总线 +
 * 样板总成里找匹配该步配方物品的来源」，让玩家不必按顺序摆料，也能用样板总成供料。
 * <p>
 * 消耗侧({@code getInputBusContent}/{@code drainAllFluids})与验证侧
 * ({@code maxParallelCalculatedByInputItems}/{@code maxParallelCalculatedByInputFluids})共用同一套
 * 候选池口径，避免验证通过但切片取不到料而卡住。取料入口都在外部类，不碰私有内部类 {@code Slice}。
 * <p>
 * ME 输入总线/输入仓沿用 {@code curBatchItemsFromME}/{@code curBatchFluidsFromME} 快照引用，扣减由
 * start/endRecipeProcessing 提交，语义不变；样板总成里的物料是已实拉入的实物，扣减即消耗，无需提交。
 * <p>
 * 限制：每一步的全部所需仍须来自「单个」来源(切片扣料模型如此)；同一物品散在多个来源、单堆都不够时需
 * 玩家自行合并。流体则可跨来源聚合(流体在配方开始时一次性抽取)。
 *
 * @author gtnc
 * @reason 进阶装配线输入由位置绑定改为池子聚合，并支持样板输入总成供料(物品+流体)
 */
@Mixin(value = MTEAdvAssLine.class, remap = false)
public abstract class MixinMTEAdvAssLine {

    @Shadow
    private GTRecipe.RecipeAssemblyLine currentRecipe;

    @Shadow
    private Map<GTUtility.ItemId, ItemStack> curBatchItemsFromME;

    @Shadow
    private Map<Fluid, FluidStack> curBatchFluidsFromME;

    // ==================== 物品候选池 ====================

    /**
     * 收集所有物品来源的「逻辑物品堆」live 引用：普通总线第一格、ME 总线快照引用(curBatchItemsFromME)、
     * 样板总成(IDualInputHatch)里各样板槽的物料。扣减这些引用即扣减对应来源。
     */
    private List<ItemStack> gtnc$itemCandidates(MTEMultiBlockBase base) {
        List<ItemStack> out = new ArrayList<>();
        for (MTEHatchInputBus bus : GTUtility.validMTEList(base.mInputBusses)) {
            if (bus == null || !bus.isValid()) continue;
            if (bus instanceof MTEHatchInputBusME meBus) {
                ItemStack probe = meBus.getFirstValidStack(true);
                if (probe == null) continue;
                GTUtility.ItemId id = GTUtility.ItemId.createNoCopy(probe);
                if (curBatchItemsFromME != null && curBatchItemsFromME.containsKey(id)) {
                    out.add(curBatchItemsFromME.get(id));
                }
            } else {
                ItemStack s = bus.getFirstStack();
                if (s != null) out.add(s);
            }
        }
        // 样板输入总成(含 Super/Slave)：已实拉入的实物，getAllItems 返回 live 引用。
        for (IDualInputHatch dualHatch : base.mDualInputHatches) {
            if (dualHatch == null) continue;
            for (ItemStack s : dualHatch.getAllItems()) {
                if (s != null) out.add(s);
            }
        }
        return out;
    }

    /**
     * 接管取料：把「第 index 步固定从第 index 个总线」改为「在所有来源里找匹配配方第 index 步物品、
     * 能支持最多并行(canDo = stackSize/per)的那一堆」。返回 null = 池子里没有该步物品。
     */
    @Inject(method = "getInputBusContent", at = @At("HEAD"), cancellable = true)
    private void gtnc$anyBusInput(int index, CallbackInfoReturnable<ItemStack> cir) {
        RecipeAssemblyLine recipe = this.currentRecipe;
        if (recipe == null || index < 0 || index >= recipe.mInputs.length) return; // 交回原版

        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;
        ItemStack ingredient = recipe.mInputs[index];
        ItemStack[] alts = recipe.mOreDictAlt != null ? recipe.mOreDictAlt[index] : null;

        ItemStack best = null;
        long bestCanDo = -1;
        for (ItemStack stack : gtnc$itemCandidates(base)) {
            if (stack == null || stack.stackSize <= 0) continue;
            int per = GTRecipe.RecipeAssemblyLine.getMatchedIngredientAmount(stack, ingredient, alts);
            if (per < 0) continue; // 不匹配该步
            long canDo = per == 0 ? Long.MAX_VALUE : stack.stackSize / (long) per;
            if (canDo > bestCanDo) {
                bestCanDo = canDo;
                best = stack;
            }
        }
        cir.setReturnValue(best);
        cir.cancel();
    }

    /**
     * 接管物品并行验证：每步在所有来源(总线+样板总成)里找能支持最多并行的一堆，取各步最小值。
     * 与 gtnc$anyBusInput 取料口径一致(每步单来源供料)。
     */
    @Inject(method = "maxParallelCalculatedByInputItems", at = @At("HEAD"), cancellable = true)
    private void gtnc$poolItemParallel(RecipeAssemblyLine tRecipe, int maxParallel,
        CallbackInfoReturnable<Integer> cir) {
        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;
        List<ItemStack> candidates = gtnc$itemCandidates(base);
        long parallel = maxParallel;

        for (int i = 0; i < tRecipe.mInputs.length; i++) {
            ItemStack ingredient = tRecipe.mInputs[i];
            if (ingredient == null) continue;
            ItemStack[] alts = tRecipe.mOreDictAlt != null ? tRecipe.mOreDictAlt[i] : null;

            long bestForStep = -1;
            int perOfBest = 0;
            for (ItemStack stack : candidates) {
                if (stack == null || stack.stackSize <= 0) continue;
                int per = GTRecipe.RecipeAssemblyLine.getMatchedIngredientAmount(stack, ingredient, alts);
                if (per < 0) continue;
                long canDo = per == 0 ? Long.MAX_VALUE : stack.stackSize / per;
                if (canDo > bestForStep) {
                    bestForStep = canDo;
                    perOfBest = per;
                }
            }
            if (bestForStep < 0) { // 没有任何来源含该步物品
                cir.setReturnValue(0);
                cir.cancel();
                return;
            }
            if (perOfBest == 0) continue; // 非消耗输入，不限制并行
            parallel = Math.min(parallel, bestForStep);
            if (parallel <= 0) {
                cir.setReturnValue(0);
                cir.cancel();
                return;
            }
        }

        cir.setReturnValue((int) Math.min(parallel, Integer.MAX_VALUE));
        cir.cancel();
    }

    // ==================== 流体候选池 ====================

    /**
     * 收集所有流体来源的「逻辑流体堆」live 引用：ME 输入仓快照(curBatchFluidsFromME)、多输入仓、普通输入仓、
     * 样板总成(IDualInputHatch)里各样板槽的流体。扣减这些引用即扣减对应来源。
     */
    private List<FluidStack> gtnc$fluidCandidates(MTEMultiBlockBase base) {
        List<FluidStack> out = new ArrayList<>();
        for (MTEHatchInput hatch : GTUtility.validMTEList(base.mInputHatches)) {
            if (hatch == null || !hatch.isValid()) continue;
            if (hatch instanceof MTEHatchInputME meHatch) {
                FluidStack probe = meHatch.getFirstValidStack(true);
                if (probe == null) continue;
                if (curBatchFluidsFromME != null && curBatchFluidsFromME.containsKey(probe.getFluid())) {
                    out.add(curBatchFluidsFromME.get(probe.getFluid()));
                }
            } else if (hatch instanceof MTEHatchMultiInput multiInput) {
                FluidStack f = multiInput.getFluid();
                if (f != null) out.add(f);
            } else {
                FluidStack f = hatch.getFillableStack();
                if (f != null) out.add(f);
            }
        }
        for (IDualInputHatch dualHatch : base.mDualInputHatches) {
            if (dualHatch == null || !dualHatch.supportsFluids()) continue;
            for (FluidStack f : dualHatch.getAllFluids()) {
                if (f != null) out.add(f);
            }
        }
        return out;
    }

    /**
     * 接管流体并行验证：在所有流体来源(输入仓+样板总成)里按配方每种流体累加匹配量，算并行取最小。
     */
    @Inject(method = "maxParallelCalculatedByInputFluids", at = @At("HEAD"), cancellable = true)
    private void gtnc$poolFluidParallel(RecipeAssemblyLine tRecipe, int maxParallel,
        CallbackInfoReturnable<Integer> cir) {
        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;
        List<FluidStack> candidates = gtnc$fluidCandidates(base);
        long parallel = maxParallel;

        for (FluidStack needed : tRecipe.mFluidInputs) {
            if (needed == null || needed.amount <= 0) continue;
            long available = 0;
            for (FluidStack cand : candidates) {
                if (cand == null || cand.amount <= 0) continue;
                if (GTUtility.areFluidsEqual(cand, needed)) available += cand.amount;
            }
            if (available < needed.amount) {
                cir.setReturnValue(0);
                cir.cancel();
                return;
            }
            parallel = Math.min(parallel, available / needed.amount);
            if (parallel <= 0) {
                cir.setReturnValue(0);
                cir.cancel();
                return;
            }
        }
        cir.setReturnValue((int) Math.min(parallel, Integer.MAX_VALUE));
        cir.cancel();
    }

    /**
     * 接管流体消耗：按并行份数在所有流体来源里跨来源扣减(与验证口径一致)。
     */
    @Inject(method = "drainAllFluids", at = @At("HEAD"), cancellable = true)
    private void gtnc$poolDrainFluids(RecipeAssemblyLine recipe, int parallel, CallbackInfo ci) {
        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;
        List<FluidStack> candidates = gtnc$fluidCandidates(base);

        for (FluidStack needed : recipe.mFluidInputs) {
            if (needed == null || needed.amount <= 0) continue;
            long remaining = (long) needed.amount * parallel;
            for (FluidStack cand : candidates) {
                if (remaining <= 0) break;
                if (cand == null || cand.amount <= 0) continue;
                if (GTUtility.areFluidsEqual(cand, needed)) {
                    int take = (int) Math.min(cand.amount, remaining);
                    cand.amount -= take;
                    remaining -= take;
                }
            }
        }
        for (MTEHatchInput tHatch : GTUtility.validMTEList(base.mInputHatches)) tHatch.updateSlots();
        ci.cancel();
    }

    /**
     * 接管流体预估取料：原版在配方设定前用 getInputHatchContent(0) 估算并行上限。这里改为在所有流体
     * 来源里返回可用量最大的一堆(configRecipe 已设定时优先返回匹配该步的)，作为上限种子；真正的
     * 并行由 gtnc$poolFluidParallel 夹紧，故此处偏大无害，非 null 即可让流程继续。
     */
    @Inject(method = "getInputHatchContent", at = @At("HEAD"), cancellable = true)
    private void gtnc$anyHatchFluid(int index, CallbackInfoReturnable<FluidStack> cir) {
        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;
        List<FluidStack> candidates = gtnc$fluidCandidates(base);
        RecipeAssemblyLine recipe = this.currentRecipe;

        FluidStack best = null;
        // 配方已知且该步有指定流体：优先返回匹配该步、量最大的一堆。
        if (recipe != null && index >= 0 && index < recipe.mFluidInputs.length && recipe.mFluidInputs[index] != null) {
            FluidStack needed = recipe.mFluidInputs[index];
            for (FluidStack cand : candidates) {
                if (cand == null || cand.amount <= 0) continue;
                if (GTUtility.areFluidsEqual(cand, needed) && (best == null || cand.amount > best.amount)) best = cand;
            }
        }
        // 否则(预估阶段 currentRecipe 尚未设定)：返回量最大的一堆作为上限种子。
        if (best == null) {
            for (FluidStack cand : candidates) {
                if (cand == null || cand.amount <= 0) continue;
                if (best == null || cand.amount > best.amount) best = cand;
            }
        }
        cir.setReturnValue(best);
        cir.cancel();
    }

    // ==================== 兜底清理 ====================

    /**
     * 每 tick 先清理所有普通输入总线的零残留。因为取料可能来自「非 index 对应」的来源，而 Slice.start 里
     * 扣料后只对 mInputBusses.get(id) 调 updateSlots，真正被抽干的总线不会被及时清理——这里统一兜底。
     * ME 总线有自己的影子库存语义，不在此触碰；样板总成的空槽由其自身 isEmpty()/updateSlot* 清理。
     */
    @Inject(method = "onRunningTick", at = @At("HEAD"))
    private void gtnc$cleanupBuses(ItemStack aStack, CallbackInfoReturnable<Boolean> cir) {
        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;
        for (MTEHatchInputBus bus : GTUtility.validMTEList(base.mInputBusses)) {
            if (bus instanceof MTEHatchInputBusME) continue;
            bus.updateSlots();
        }
    }
}
