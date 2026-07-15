package com.xyp.gtnc.mixins.late.Gregtech;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Common.machines.bee.BeeMutationConditionFilter;

import forestry.api.core.IClimateProvider;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import gregtech.common.bees.GTBeeMutation;

/**
 * GT 蜜蜂突变版的「取消维度 / 下方方块-机器 限制」，与 {@code Forestry.MixinMutationConditions} 对应。
 * <p>
 * {@link GTBeeMutation} 重写了 {@code getChance} 并调用自己的私有 {@code getBasicChance}，不走 Forestry 基类
 * {@code Mutation.getChance}，因此必须单独在这里注入。{@code getBasicChance} 同样遍历 {@code mutationConditions}，
 * 任一条件返回 0 即整体归零。这里 {@link Redirect} 那次 {@code mutationCondition.getChance(...)} 调用：命中
 * {@link BeeMutationConditionFilter#shouldSkip 应跳过的条件类型}(维度 {@code DimensionMutationCondition} /
 * 下方需运行的 GT 机器 {@code ActiveGTMachineMutationCondition} / Forestry 方块条件)时返回 1，其余照常。
 * <p>
 * GT 蜜蜂突变的 genome 亦为 {@code IBeeGenome}，过滤器的 {@code instanceof IBeeGenome} 判定自然成立。
 * <p>
 * 由 {@code Config.enableBeeIgnoreDimensionMutation} / {@code Config.enableBeeIgnoreResourceMutation} 控制。
 */
@Mixin(GTBeeMutation.class)
public abstract class MixinGTBeeMutation {

    @Redirect(
        method = "getBasicChance",
        at = @At(
            value = "INVOKE",
            target = "Lforestry/api/genetics/IMutationCondition;getChance(Lnet/minecraft/world/World;IIILforestry/api/genetics/IAllele;Lforestry/api/genetics/IAllele;Lforestry/api/genetics/IGenome;Lforestry/api/genetics/IGenome;Lforestry/api/core/IClimateProvider;)F"),
        remap = false)
    private float gtnc$skipBeeEnvConditions(IMutationCondition condition, World world, int x, int y, int z,
        IAllele allele0, IAllele allele1, IGenome genome0, IGenome genome1, IClimateProvider climate) {
        if (BeeMutationConditionFilter.shouldSkip(condition, genome0)) {
            return 1f;
        }
        return condition.getChance(world, x, y, z, allele0, allele1, genome0, genome1, climate);
    }
}
