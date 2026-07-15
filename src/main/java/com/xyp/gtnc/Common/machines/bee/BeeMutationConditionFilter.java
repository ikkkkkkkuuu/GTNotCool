package com.xyp.gtnc.Common.machines.bee;

import com.xyp.gtnc.Config.Config;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutationCondition;

/**
 * 蜜蜂突变(杂交)条件过滤器：判断某个 {@link IMutationCondition} 是否应被「视为满足」(跳过)。
 * <p>
 * Forestry 的突变链({@code Mutation.getChance} / GT 的 {@code GTBeeMutation.getBasicChance})会遍历该突变的
 * 全部条件，任一条件 {@code getChance} 返回 0 就令整个突变概率归零。本类用于让「维度限制」「蜂箱正下方需特定
 * 方块/运行中的 GT 机器」这两类硬性环境条件不再阻断杂交——由对应 mixin 在遍历时调用，命中则返回 1(满足)。
 * <p>
 * <b>作用域锁定在蜜蜂</b>：{@code Mutation} 基类同时被树木({@code ITreeGenome})、蝴蝶({@code IButterflyGenome})
 * 的突变共用。这里用 {@code genome0 instanceof IBeeGenome} 把作用域限制在蜜蜂突变，树木/蝴蝶育种完全不受影响。
 * <p>
 * <b>不影响本 mod 的蜜蜂杂交机</b>：杂交机走模板法直接生成基因组，根本不经过突变条件判定。
 */
public final class BeeMutationConditionFilter {

    private BeeMutationConditionFilter() {}

    /**
     * 判断遍历到的突变条件是否应被跳过(视为满足)。
     *
     * @param condition 当前遍历到的突变条件
     * @param genome0   突变的亲本基因组之一，用于把作用域限制在蜜蜂
     * @return true 表示应跳过该条件(mixin 返回 1)，false 表示按原逻辑计算
     */
    public static boolean shouldSkip(IMutationCondition condition, IGenome genome0) {
        if (condition == null) return false;
        // 仅作用于蜜蜂突变，避免波及共用 Mutation 基类的树木/蝴蝶育种。
        if (!(genome0 instanceof IBeeGenome)) return false;

        String name = condition.getClass()
            .getName();

        // 维度限制：GT 的 DimensionMutationCondition(要求蜂箱处于特定维度，如末地/下界)。
        if (Config.enableBeeIgnoreDimensionMutation && name.endsWith("DimensionMutationCondition")) {
            return true;
        }

        // 「蜂箱正下方放对应方块」这一类要求：
        // - Forestry MutationConditionRequiresResource / ...OreDict：正下方需特定方块 / 矿物辞条目
        // - GT ActiveGTMachineMutationCondition：正下方需一台运行中的 GT 机器
        if (Config.enableBeeIgnoreResourceMutation) {
            if (name.equals("forestry.core.genetics.mutations.MutationConditionRequiresResource")
                || name.equals("forestry.core.genetics.mutations.MutationConditionRequiresResourceOreDict")
                || name.endsWith("ActiveGTMachineMutationCondition")) {
                return true;
            }
        }
        return false;
    }
}
