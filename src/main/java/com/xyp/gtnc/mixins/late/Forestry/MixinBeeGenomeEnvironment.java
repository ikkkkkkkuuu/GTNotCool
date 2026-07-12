package com.xyp.gtnc.mixins.late.Forestry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.xyp.gtnc.Config.Config;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IFlowerProvider;
import forestry.apiculture.genetics.BeeGenome;

/**
 * 让一切来源的蜜蜂（野生、蜂巢掉落、原版杂交、村民蜂等）环境相关性状全部拉满：
 * <ul>
 * <li>温度适应度 {@code getToleranceTemp} → {@link EnumTolerance#BOTH_5}（±5）</li>
 * <li>湿度适应度 {@code getToleranceHumid} → {@link EnumTolerance#BOTH_5}（±5）</li>
 * <li>夜行性 {@code getNocturnal} → true（夜间/黑暗也能工作）</li>
 * <li>穴居性 {@code getCaveDwelling} → true（地下/无天空也能工作）</li>
 * <li>工作速度 {@code getSpeed} → 本 mod 自注册「无尽」基因数值（{@link Config#customBeeSpeedValue}）</li>
 * <li>授粉速度 {@code getFlowering} → 99（林业最大档 MAXIMUM）</li>
 * <li>采蜜对象 {@code getFlowerProvider} → 鲜花（vanilla flowers）</li>
 * </ul>
 * 这些 getter 是 {@link forestry.apiculture.genetics.Bee#canWork}/产蜜/授粉逻辑的输入，
 * 在 HEAD {@code @Inject} 返回满值即让蜂在任意气候、夜间、地下工作，且速度/授粉拉满、只认鲜花。
 * <p>
 * <b>读时覆写，不改存储基因组</b>：这里覆写的是 BeeGenome 的读取结果，不动 NBT 里的染色体。
 * 因此天然覆盖所有来源的蜂，且<b>不波及本 mod 的蜜蜂杂交机</b>——杂交机产出的蜂这四项本就已被
 * {@code applyMaxGenome} 设为 BOTH_5 / BOTH_5 / nocturnal / cave，覆写值与其原值完全一致，零影响。
 * <p>
 * 由 {@link Config#enableBeeMaxEnvironment} 控制，默认开启。
 */
@Mixin(BeeGenome.class)
public abstract class MixinBeeGenomeEnvironment {

    @Inject(method = "getToleranceTemp", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$maxTempTolerance(CallbackInfoReturnable<EnumTolerance> cir) {
        if (Config.enableBeeMaxEnvironment) {
            cir.setReturnValue(EnumTolerance.BOTH_5);
        }
    }

    @Inject(method = "getToleranceHumid", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$maxHumidTolerance(CallbackInfoReturnable<EnumTolerance> cir) {
        if (Config.enableBeeMaxEnvironment) {
            cir.setReturnValue(EnumTolerance.BOTH_5);
        }
    }

    @Inject(method = "getNocturnal", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$forceNocturnal(CallbackInfoReturnable<Boolean> cir) {
        if (Config.enableBeeMaxEnvironment) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getCaveDwelling", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$forceCaveDwelling(CallbackInfoReturnable<Boolean> cir) {
        if (Config.enableBeeMaxEnvironment) {
            cir.setReturnValue(true);
        }
    }

    /** 工作速度：返回本 mod 自注册「无尽」基因的数值（{@link Config#customBeeSpeedValue}）。 */
    @Inject(method = "getSpeed", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$maxSpeed(CallbackInfoReturnable<Float> cir) {
        if (Config.enableBeeMaxEnvironment) {
            cir.setReturnValue(Config.customBeeSpeedValue);
        }
    }

    /** 授粉速度：返回林业最大档 MAXIMUM 的数值 99。 */
    @Inject(method = "getFlowering", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$maxFlowering(CallbackInfoReturnable<Integer> cir) {
        if (Config.enableBeeMaxEnvironment) {
            cir.setReturnValue(99);
        }
    }

    /** 采蜜对象：返回「鲜花(VANILLA)」provider（懒解析已注册的 forestry.flowersVanilla 等位基因并缓存）。 */
    @Inject(method = "getFlowerProvider", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private void gtnc$vanillaFlowers(CallbackInfoReturnable<IFlowerProvider> cir) {
        if (!Config.enableBeeMaxEnvironment) return;
        IFlowerProvider vanilla = gtnc$vanillaFlowerProvider();
        if (vanilla != null) {
            cir.setReturnValue(vanilla);
        }
    }

    @Unique
    private static IFlowerProvider gtnc$cachedVanillaFlowers;
    @Unique
    private static boolean gtnc$vanillaFlowersResolved;

    @Unique
    private static IFlowerProvider gtnc$vanillaFlowerProvider() {
        if (gtnc$vanillaFlowersResolved) return gtnc$cachedVanillaFlowers;
        gtnc$vanillaFlowersResolved = true;
        if (AlleleManager.alleleRegistry != null) {
            // vanilla flowers allele 的 UID：modId + category + Capitalize(name) = forestry.flowersVanilla
            IAllele allele = AlleleManager.alleleRegistry.getAllele("forestry.flowersVanilla");
            if (allele instanceof IAlleleFlowers) {
                gtnc$cachedVanillaFlowers = ((IAlleleFlowers) allele).getProvider();
            }
        }
        return gtnc$cachedVanillaFlowers;
    }
}
