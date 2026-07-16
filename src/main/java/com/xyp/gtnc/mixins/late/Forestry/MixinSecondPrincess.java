package com.xyp.gtnc.mixins.late.Forestry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.xyp.gtnc.Common.items.bee.EndlessFrameItem;
import com.xyp.gtnc.Config.Config;

import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.BeekeepingLogic;
import forestry.plugins.PluginApiculture;

/**
 * 让普通蜂箱/蜂房杂交每次都额外产出第二只公主蜂（共 2 只公主），<b>但插了无尽框架时不产第二只</b>。
 * <p>
 * Forestry 的 {@link BeekeepingLogic} 私有静态方法 {@code spawnOffspring(IBee, IBeeHousing)} 里，公主蜂数量由一次
 * 概率判定决定：{@code world.rand.nextInt(10000) < PluginApiculture.getSecondPrincessChance() * 100}，命中则产 2 只、
 * 否则 1 只。原版 {@code secondPrincessChance} 默认 0（仅可在 Forestry 配置里手动调），所以正常只出 1 只公主。
 * <p>
 * 这里 {@link Redirect} 那次 {@code getSecondPrincessChance()} 调用。{@code @Redirect} 的 handler 可在「原调用参数」
 * 后追加「被注入方法的参数」，故这里能拿到 {@code spawnOffspring} 的 {@code queen} 与 {@code beeHousing}。开关开启且
 * <b>蜂箱未插无尽框架</b>时恒返回 100 → {@code 100 * 100 = 10000}，而 {@code nextInt(10000) ∈ [0, 9999]} 必定小于它
 * → 100% 触发第二只公主；一旦检出无尽框架则回退原值（默认 0 = 只出 1 只），保证纯产物场景不被多出的公主污染。
 * <p>
 * <b>为何用无尽框架作开关</b>：无尽框架本就是「纯产物/驻留」语义（{@code endlessFrameMutationMultiplier=0}，完全不杂交），
 * 插它即代表玩家要稳定产物而非繁殖，此时抑制第二只公主与框架语义自洽。框架是否插入通过 {@code IBeeHousing.getBeeModifiers()}
 * 判断——蜂箱把每个插入框架的 {@code getBeeModifier()} 都聚合进该迭代器（见 {@code TileApiary#getBeeModifiers}）。
 * <p>
 * <b>只影响公主蜂</b>：雄蜂由同方法里独立的 {@code queen.spawnDrones(...)} 产出，本 redirect 完全不碰它，
 * 雄蜂数量仍随原版基因（fertility/生育力）决定。
 * <p>
 * <b>不影响本 mod 的蜜蜂杂交机</b>：杂交机走模板法（{@code createPrincess/createDrone → templateAsGenome}）直接生成，
 * 完全不经过 {@code BeekeepingLogic.spawnOffspring}，二者天然隔离。
 * <p>
 * 由 {@link Config#enableBeeAlwaysSecondPrincess} 控制，默认开启。
 */
@Mixin(BeekeepingLogic.class)
public abstract class MixinSecondPrincess {

    @Redirect(
        method = "spawnOffspring",
        at = @At(value = "INVOKE", target = "Lforestry/plugins/PluginApiculture;getSecondPrincessChance()D"),
        remap = false)
    private static double gtnc$alwaysSecondPrincess(IBee queen, IBeeHousing beeHousing) {
        if (!Config.enableBeeAlwaysSecondPrincess) return PluginApiculture.getSecondPrincessChance();
        // 插了无尽框架（纯产物框架）时不额外出公主，避免污染产物蜂箱。
        if (beeHousing != null) {
            for (IBeeModifier modifier : beeHousing.getBeeModifiers()) {
                if (EndlessFrameItem.isEndlessFrameModifier(modifier)) {
                    return PluginApiculture.getSecondPrincessChance();
                }
            }
        }
        return 100.0D;
    }
}
