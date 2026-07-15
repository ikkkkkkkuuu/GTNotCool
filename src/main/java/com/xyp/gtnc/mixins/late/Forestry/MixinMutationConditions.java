package com.xyp.gtnc.mixins.late.Forestry;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Common.machines.bee.BeeMutationConditionFilter;

import forestry.api.core.IClimateProvider;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;
import forestry.core.genetics.mutations.Mutation;

/**
 * 取消普通蜂箱杂交的「维度限制」和「蜂箱正下方需特定方块」这两类硬性环境条件。
 * <p>
 * {@link Mutation#getChance} 会遍历该突变的全部 {@link IMutationCondition}，任一条件 {@code getChance} 返回 0
 * 就令整个突变概率归零(杂交无法发生)。这里 {@link Redirect} 遍历中对 {@code mutationCondition.getChance(...)}
 * 的调用：若该条件属于 {@link BeeMutationConditionFilter#shouldSkip 应跳过的类型}(维度 / 下方方块/机器)，直接返回
 * 1(视为满足)，其余条件(温度、湿度、生物群系、时段等)照常按原逻辑计算。
 * <p>
 * <b>只作用蜜蜂</b>：{@code Mutation} 基类被树木/蝴蝶突变共用，过滤器内用 {@code genome0 instanceof IBeeGenome}
 * 把作用域锁死在蜜蜂突变，树木/蝴蝶育种完全不受影响。GT 蜜蜂({@code GTBeeMutation})重写了 {@code getChance} 走
 * 自己的 {@code getBasicChance}，由 {@code Gregtech.MixinGTBeeMutation} 单独覆盖。
 * <p>
 * <b>不影响本 mod 的蜜蜂杂交机</b>：杂交机走模板法直接生成，根本不经过突变条件判定。
 * <p>
 * 由 {@code Config.enableBeeIgnoreDimensionMutation} / {@code Config.enableBeeIgnoreResourceMutation} 控制。
 */
@Mixin(Mutation.class)
public abstract class MixinMutationConditions {

    @Redirect(
        method = "getChance",
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
