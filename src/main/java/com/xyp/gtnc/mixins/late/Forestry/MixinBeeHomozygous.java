package com.xyp.gtnc.mixins.late.Forestry;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Common.machines.bee.BeeBreedingHelper;
import com.xyp.gtnc.Config.Config;

import forestry.api.apiculture.IBee;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.apiculture.genetics.Bee;
import forestry.core.genetics.Chromosome;

/**
 * 让普通蜂箱杂交产出的蜜蜂只有纯合子（两条等位基因相同），不再出杂合子。
 * <p>
 * 原版 {@link Chromosome#inheritChromosome(Random, IChromosome, IChromosome)} 从两个亲本各随机取一条等位基因，
 * 组成 {@code (choice1, choice2)}——两者不同即为杂合子。它被 {@link Bee#createOffspring} 唯一调用（每条染色体一次）。
 * <p>
 * 这里 {@link Redirect} {@code createOffspring} 内那次 {@code inheritChromosome} 调用：仍先跑原版继承（保留正常的
 * 亲本随机 + 突变逻辑），拿到结果后取其<b>激活(显性/表达)等位基因</b>，用它构造一条纯合染色体 {@code (active, active)}。
 * 于是后代每条染色体都是纯合、表达的性状与原版一致、且能稳定遗传（breed true）。
 * <p>
 * <b>不影响本 mod 的蜜蜂杂交机</b>：杂交机走模板法（{@code applyMaxGenome} + {@code templateAsGenome}），
 * 产出的本就是纯合蜂，且完全不经过 {@code inheritChromosome}。本 redirect 只作用于 {@code Bee.createOffspring}
 * （原版蜂箱/蜂巢的自然繁殖），二者天然隔离。
 * <p>
 * 由 {@link Config#enableBeeHomozygousOffspring} 控制，默认开启。
 */
@Mixin(Bee.class)
public abstract class MixinBeeHomozygous {

    @Redirect(
        method = "createOffspring",
        at = @At(
            value = "INVOKE",
            target = "Lforestry/core/genetics/Chromosome;inheritChromosome(Ljava/util/Random;Lforestry/api/genetics/IChromosome;Lforestry/api/genetics/IChromosome;)Lforestry/api/genetics/IChromosome;"),
        remap = false)
    private IChromosome gtnc$homozygousInherit(Random rand, IChromosome parent1, IChromosome parent2) {
        IChromosome inherited = Chromosome.inheritChromosome(rand, parent1, parent2);
        if (!Config.enableBeeHomozygousOffspring || inherited == null) {
            return inherited;
        }
        IAllele active = inherited.getActiveAllele();
        if (active == null) {
            return inherited;
        }
        // 用激活等位基因构造纯合染色体（单参构造器令 primary == secondary）。
        return new Chromosome(active);
    }

    /**
     * 普通蜂箱杂交后代<b>真正写满基因 NBT</b>：在 {@code createOffspring} 返回处，把后代 {@link IBee} 用
     * {@link BeeBreedingHelper#maximizeBee} 重建为满基因（保留物种、其余染色体拉满并纯合）。这样蜂箱杂交出来的蜂
     * 分析仪能读到满值、后代能 breed true，与世界蜂巢掉落（{@code MixinBlockBeehives}）表现一致。
     * <p>
     * <b>杂交机隔离</b>：本 mod 蜜蜂杂交机走 {@code createDrone/createPrincess → templateAsGenome} 直接生成，
     * 完全不经过 {@code createOffspring}，故不受影响。
     * <p>
     * 由 {@link Config#enableBeeMaxGenomeOnBreed} 控制，默认开启。
     */
    @Inject(method = "createOffspring", at = @At("RETURN"), cancellable = true, remap = false, require = 1)
    private void gtnc$maxGenomeOffspring(CallbackInfoReturnable<IBee> cir) {
        if (!Config.enableBeeMaxGenomeOnBreed) return;
        IBee offspring = cir.getReturnValue();
        if (offspring == null) return;
        IBee maxed = BeeBreedingHelper.maximizeBee(offspring);
        if (maxed != offspring) {
            cir.setReturnValue(maxed);
        }
    }
}
