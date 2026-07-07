package com.xyp.gtnc.mixins.late.Gregtech;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe.RecipeAssemblyLine;
import gregtech.api.util.GTUtility;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.api.util.VoidProtectionHelper;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.multi.MTEAssemblyLine;

/**
 * 让原版装配线改用「池子式」输入匹配。
 * <p>
 * 原版 {@link MTEAssemblyLine#checkProcessing()} 的输入检测是「第 N 步料 ↔ 第 N 个输入总线、
 * 且每个总线只看第一格」的严格位置绑定。这里整体接管 checkProcessing，改成像普通多方块一样：
 * 把所有输入总线/仓聚合成一个池子（{@link MTEMultiBlockBase#getStoredInputs()} /
 * {@link MTEMultiBlockBase#getStoredFluids()}，本身已包含并去重 ME 总线内容），只要池子里能凑齐
 * 配方就能运行，不再要求按顺序摆料。
 * <p>
 * 保留数据棒 + 数据仓提供配方；保留电压门槛、并行/超频等原版行为。用
 * {@code startRecipeProcessing()/endRecipeProcessing()} 包裹，保证 ME 总线提交语义正确。
 * <p>
 * 保留原版的批处理(batch mode)与溢出保护(void protection)；超频改用完美超频(enablePerfectOC，每级耗时 ÷4)。
 *
 * @author gtnc
 * @reason 装配线输入检测由位置绑定改为池子聚合，遍历所有舱室
 */
@Mixin(value = MTEAssemblyLine.class, remap = false)
public class MixinMTEAssemblyLine {

    /**
     * 原版装配线未重写 {@code getMaxBatchSize()}，用的就是 {@link MTEMultiBlockBase} 的默认值 128。
     * 这里直接用常量而非 {@code @Shadow} 一个继承来的 protected 方法，避免 APT「找不到目标」告警，
     * 以及生产环境下 shadow 沿继承链解析失败导致批处理静默失效的隐患。
     */
    private static final int MAX_BATCH_SIZE = 128;

    @Inject(method = "checkProcessing", at = @At("HEAD"), cancellable = true)
    private void gtnc$poolCheckProcessing(CallbackInfoReturnable<CheckRecipeResult> cir) {
        MTEAssemblyLine self = (MTEAssemblyLine) (Object) this;
        MTEMultiBlockBase base = (MTEMultiBlockBase) (Object) this;

        // ---- 收集候选配方：数据棒 + 数据仓 ----
        ArrayList<RecipeAssemblyLine> availableRecipes = new ArrayList<>();
        ItemStack controllerStack = self.mInventory[1];
        if (AssemblyLineUtils.isItemDataStick(controllerStack)) {
            availableRecipes.addAll(AssemblyLineUtils.findALRecipeFromDataStick(controllerStack));
        }
        for (var dataAccess : GTUtility.validMTEList(self.mDataAccessHatches)) {
            availableRecipes.addAll(dataAccess.getAssemblyLineRecipes());
        }

        if (availableRecipes.isEmpty()) {
            cir.setReturnValue(CheckRecipeResultRegistry.NO_DATA_STICKS);
            cir.cancel();
            return;
        }

        long averageVoltage = base.getAverageInputVoltage();
        long maxAmp = base.getMaxInputAmps();

        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        base.startRecipeProcessing();
        try {
            // 建池：物品/流体各聚合一份（对普通总线为 live 引用，扣减即减少仓室内容）。
            List<ItemStack> itemPool = base.getStoredInputs();
            List<FluidStack> fluidPool = base.getStoredFluids();

            // 并入样板输入总成(mDualInputHatches，含 Super/Slave)：getStoredInputs/Fluids 会跳过
            // MTEHatchCraftingInputME，这里把各样板槽里已实拉入的物料/流体作为"存货源"加进池子。
            // getAllItems/getAllFluids 返回的是样板槽内物料的 live 引用，扣减即消耗，无需额外 AE 提交。
            for (IDualInputHatch dualHatch : base.mDualInputHatches) {
                if (dualHatch == null) continue;
                for (ItemStack item : dualHatch.getAllItems()) {
                    if (item != null && item.stackSize > 0) itemPool.add(item);
                }
                if (dualHatch.supportsFluids()) {
                    for (FluidStack fluid : dualHatch.getAllFluids()) {
                        if (fluid != null && fluid.amount > 0) fluidPool.add(fluid);
                    }
                }
            }

            for (RecipeAssemblyLine recipe : availableRecipes) {
                // 电压门槛（配方等级限制在仓室等级 +1）。
                if (recipe.mEUt > averageVoltage * 4 || recipe.mEUt > maxAmp * averageVoltage) {
                    result = CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                    continue;
                }

                // 超频先行：完美超频 + under-one-tick 并行，作为并行上限的基准。
                int originalMaxParallel = 1;
                OverclockCalculator calculator = new OverclockCalculator().setRecipeEUt(recipe.mEUt)
                    .setEUt(averageVoltage)
                    .setAmperage(maxAmp)
                    .setAmperageOC(base.mEnergyHatches.size() != 1)
                    .setDuration(recipe.mDuration)
                    .setParallel(originalMaxParallel)
                    .enablePerfectOC();
                int maxParallel = GTUtility
                    .safeInt((long) (originalMaxParallel * calculator.calculateMultiplierUnderOneTick()), 0);
                int maxParallelBeforeBatchMode = maxParallel;
                if (base.isBatchModeEnabled()) {
                    maxParallel = GTUtility.safeInt((long) maxParallel * MAX_BATCH_SIZE, 0);
                }

                // 溢出保护：按产物空间压低并行；产物满则报 ITEM_OUTPUT_FULL。
                if (self.protectsExcessItem()) {
                    VoidProtectionHelper voidProtectionHelper = new VoidProtectionHelper();
                    voidProtectionHelper.setMachine(self)
                        .setItemOutputs(new ItemStack[] { recipe.mOutput })
                        .setMaxParallel(maxParallel)
                        .build();
                    maxParallel = Math.min(voidProtectionHelper.getMaxParallel(), maxParallel);
                    if (voidProtectionHelper.isItemFull()) {
                        result = CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                        continue;
                    }
                }

                // 池子匹配：算输入能支持的最大并行，再用上面的上限夹紧。
                long inputParallel = matchPool(recipe, itemPool, fluidPool);
                if (inputParallel <= 0) {
                    continue;
                }
                int currentParallel = (int) Math.min(inputParallel, maxParallel);
                if (currentParallel <= 0) {
                    continue;
                }

                // 批处理时长：先按 batch 前并行算超频，再把超出部分折算成延长的运行时间。
                int currentParallelBeforeBatchMode = Math.min(currentParallel, maxParallelBeforeBatchMode);
                calculator.setCurrentParallel(currentParallelBeforeBatchMode)
                    .calculate();

                double batchMultiplierMax = 1;
                if (currentParallel > maxParallelBeforeBatchMode && calculator.getDuration() < MAX_BATCH_SIZE) {
                    batchMultiplierMax = (double) MAX_BATCH_SIZE / calculator.getDuration();
                    batchMultiplierMax = Math
                        .min(batchMultiplierMax, (double) currentParallel / maxParallelBeforeBatchMode);
                }
                int finalParallel = (int) (batchMultiplierMax * currentParallelBeforeBatchMode);

                // ---- 实际扣料 ----
                consumePool(recipe, itemPool, fluidPool, finalParallel);

                long lEUt = calculator.getConsumption();
                if (lEUt > 0) lEUt = -lEUt;
                ((MTEExtendedPowerMultiBlockBase<?>) (Object) this).lEUt = lEUt;
                base.mMaxProgresstime = (int) (calculator.getDuration() * batchMultiplierMax);
                base.mEfficiency = 10000 - (base.getIdealStatus() - base.getRepairStatus()) * 1000;
                base.mEfficiencyIncrease = 10000;

                ArrayList<ItemStack> outputs = new ArrayList<>();
                ParallelHelper.addItemsLong(outputs, recipe.mOutput, (long) recipe.mOutput.stackSize * finalParallel);
                base.mOutputItems = outputs.toArray(new ItemStack[0]);

                base.updateSlots();
                result = CheckRecipeResultRegistry.SUCCESSFUL;
                break;
            }
        } finally {
            base.endRecipeProcessing();
        }

        cir.setReturnValue(result);
        cir.cancel();
    }

    /**
     * 逐步在池子里匹配配方，返回可支持的最大并行（0 = 凑不齐）。仅计算，不扣料。
     */
    private static long matchPool(RecipeAssemblyLine recipe, List<ItemStack> itemPool, List<FluidStack> fluidPool) {
        long parallel = Long.MAX_VALUE;

        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack ingredient = recipe.mInputs[i];
            if (ingredient == null) continue;
            ItemStack[] alts = recipe.mOreDictAlt != null ? recipe.mOreDictAlt[i] : null;
            int needPer = ingredient.stackSize;
            if (needPer <= 0) continue; // 非消耗输入
            long available = 0;
            for (ItemStack pooled : itemPool) {
                if (pooled == null || pooled.stackSize <= 0) continue;
                if (gtnc$ingredientMatches(pooled, ingredient, alts)) {
                    available += pooled.stackSize;
                }
            }
            if (available < needPer) return 0;
            parallel = Math.min(parallel, available / needPer);
            if (parallel <= 0) return 0;
        }

        for (int i = 0; i < recipe.mFluidInputs.length; i++) {
            FluidStack fluid = recipe.mFluidInputs[i];
            if (fluid == null) continue;
            int needPer = fluid.amount;
            if (needPer <= 0) continue;
            long available = 0;
            for (FluidStack pooled : fluidPool) {
                if (pooled == null || pooled.amount <= 0) continue;
                if (GTUtility.areFluidsEqual(pooled, fluid)) {
                    available += pooled.amount;
                }
            }
            if (available < needPer) return 0;
            parallel = Math.min(parallel, available / needPer);
            if (parallel <= 0) return 0;
        }

        return parallel == Long.MAX_VALUE ? 1 : parallel;
    }

    /**
     * 按并行份数在池子里扣料。直接递减 live 引用（getStoredInputs 对普通总线返回的是仓室内物品的
     * live 引用，扣减即减少仓室内容；ME 总线由 start/endRecipeProcessing 包裹提交）。
     */
    private static void consumePool(RecipeAssemblyLine recipe, List<ItemStack> itemPool, List<FluidStack> fluidPool,
        int parallel) {
        for (int i = 0; i < recipe.mInputs.length; i++) {
            ItemStack ingredient = recipe.mInputs[i];
            if (ingredient == null || ingredient.stackSize <= 0) continue;
            ItemStack[] alts = recipe.mOreDictAlt != null ? recipe.mOreDictAlt[i] : null;
            long remaining = (long) ingredient.stackSize * parallel;
            for (ItemStack pooled : itemPool) {
                if (remaining <= 0) break;
                if (pooled == null || pooled.stackSize <= 0) continue;
                if (gtnc$ingredientMatches(pooled, ingredient, alts)) {
                    int take = (int) Math.min(pooled.stackSize, remaining);
                    pooled.stackSize -= take;
                    remaining -= take;
                }
            }
        }

        for (int i = 0; i < recipe.mFluidInputs.length; i++) {
            FluidStack fluid = recipe.mFluidInputs[i];
            if (fluid == null || fluid.amount <= 0) continue;
            long remaining = (long) fluid.amount * parallel;
            for (FluidStack pooled : fluidPool) {
                if (remaining <= 0) break;
                if (pooled == null || pooled.amount <= 0) continue;
                if (GTUtility.areFluidsEqual(pooled, fluid)) {
                    int take = (int) Math.min(pooled.amount, remaining);
                    pooled.amount -= take;
                    remaining -= take;
                }
            }
        }
    }

    private static boolean gtnc$ingredientMatches(ItemStack pooled, ItemStack ingredient, ItemStack[] alts) {
        if (alts == null || alts.length == 0) {
            return GTUtility.areStacksEqual(pooled, ingredient, true);
        }
        for (ItemStack alt : alts) {
            if (alt != null && GTUtility.areStacksEqual(pooled, alt, true)) return true;
        }
        return false;
    }
}
