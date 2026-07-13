package com.xyp.gtnc.Common.items.bee;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;

import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;

/**
 * 诱变框架——完全复刻 GT++ 原版 {@code MUTAGENIC} 框架的行为。插入蜂箱/蜂房框架槽后：
 * <ul>
 * <li><b>突变(杂交)成功率 ×{@link Config#mutagenicFrameMutationMultiplier}</b>(GT++ = 5.0)；</li>
 * <li><b>寿命 ×{@link Config#mutagenicFrameLifespanModifier}</b>(GT++ = 0.0001)——蜂后寿命被砍到近乎为零，
 * 几乎瞬死、疯狂重滚后代。<b>这才是诱变框架"出杂交快"的真正原因</b>：单次突变判定概率只 ×5，但繁殖循环数暴涨，突变哗哗地出；</li>
 * <li>产量 ×{@link Config#mutagenicFrameProductionModifier}(GT++ = 9.0)、基因衰变
 * ×{@link Config#mutagenicFrameGeneticDecay}(GT++ = 1.0)。</li>
 * </ul>
 * <p>
 * 原理：{@link IHiveFrame#getBeeModifier()} 返回的 {@link IBeeModifier} 会被 {@code BeeHousingModifier} 累乘进
 * {@code BeeMutation.getChance} / {@code Bee} 的寿命计算(见 {@code forestry.apiculture.BeeHousingModifier})。框架只对
 * "有框架槽的蜂箱"(Apiary/Alveary)生效。
 * <p>
 * <b>tooltip 自动生成</b>：仿 GT++ 不重写 {@link IHiveFrame#getFrameTooltip()}(返回 null)，Forestry 的
 * {@code EventHandlerApiculture.addFrameTooltip} 会在按住 Shift 时自动按 modifier 数值渲染耐久/领地/突变率/寿命/
 * 产量/授粉/衰变(寿命与衰变越低越绿)，无需自己写 lang。
 * <p>
 * <b>耐久</b>：{@link Config#mutagenicFrameMaxDamage} 控制可用次数(GT++ = 3)；设为 0 则永不磨损。
 * <p>
 * <b>与本 mod 蜜蜂杂交机隔离</b>：杂交机走模板法({@code createDrone/createPrincess → templateAsGenome})，
 * 完全不经过蜂箱框架逻辑，故不受本框架影响。
 */
public class MutagenicFrameItem extends Item implements IHiveFrame {

    public static final String UNLOCALIZED_NAME = "mutagenic_frame";
    public static final String ITEM_NAME = "MutagenicFrame";

    private final IBeeModifier beeModifier = new MutagenicFrameBeeModifier();

    public MutagenicFrameItem() {
        setMaxStackSize(1);
        setCreativeTab(GTNCCreativeTabs.GTNCItem);
        // GT++ MUTAGENIC 用 3 次即损坏；配置 0 表示永不磨损(不设 maxDamage，恒返回原框架)。
        if (Config.mutagenicFrameMaxDamage > 0) {
            setMaxDamage(Config.mutagenicFrameMaxDamage);
        }
        // #tr item.mutagenic_frame.name
        // # Mutagenic Frame
        // # zh_CN 诱变框架
        setUnlocalizedName(UNLOCALIZED_NAME);
        setTextureName(ScienceNotCool.RESOURCE_ROOT_ID + ":mutagenic_frame");
    }

    /** 仿 GT++ {@code MBItemFrame.frameUsed}：累加损耗，耐久耗尽返回 null；maxDamage=0 时永不磨损。 */
    @Override
    public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
        if (Config.mutagenicFrameMaxDamage <= 0) {
            return frame;
        }
        frame.setItemDamage(frame.getItemDamage() + wear);
        if (frame.getItemDamage() >= frame.getMaxDamage()) {
            return null;
        }
        return frame;
    }

    @Override
    public IBeeModifier getBeeModifier() {
        return beeModifier;
    }

    // 不重写 getFrameTooltip()：返回默认的 null，让 Forestry 自动按 modifier 数值生成 tooltip。

    private static class MutagenicFrameBeeModifier extends DefaultBeeModifier {

        @Override
        public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
            return Config.mutagenicFrameMutationMultiplier;
        }

        @Override
        public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
            return Config.mutagenicFrameLifespanModifier;
        }

        @Override
        public float getProductionModifier(IBeeGenome genome, float currentModifier) {
            return Config.mutagenicFrameProductionModifier;
        }

        @Override
        public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
            return Config.mutagenicFrameGeneticDecay;
        }
    }
}
