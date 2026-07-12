package com.xyp.gtnc.Common.items.bee;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

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
 * 诱变框架——插入蜂箱/蜂房框架槽后，把杂交(突变)成功率乘以 {@link Config#mutagenicFrameMutationMultiplier}
 * (默认 1.8 = +80%)，并把基因衰变系数设为 {@link Config#mutagenicFrameGeneticDecay}(默认 0 = 完全不衰变)。
 * 其余属性(领地/寿命/产量/授粉，以及密封/自照明等布尔)全部沿用 {@link DefaultBeeModifier} 的中性值，即"其他都不变"。
 * <p>
 * 原理：{@link IHiveFrame#getBeeModifier()} 返回的 {@link IBeeModifier} 会被 {@code BeeHousingModifier} 累乘进
 * {@code BeeMutation.getChance}(见 {@code forestry.apiculture.genetics.BeeMutation})。因此 getMutationModifier
 * 返回 1.8 即整体成功率 ×1.8。框架只对"有框架槽的蜂箱"(Apiary/Alveary)生效。
 * <p>
 * <b>永不磨损</b>：{@link #frameUsed} 原样返回框架，不累加损耗、不消失。
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
        // #tr item.mutagenic_frame.name
        // # Mutagenic Frame
        // # zh_CN 诱变框架
        setUnlocalizedName(UNLOCALIZED_NAME);
        setTextureName(ScienceNotCool.RESOURCE_ROOT_ID + ":mutagenic_frame");
    }

    /** 永不磨损：原样返回框架。 */
    @Override
    public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
        return frame;
    }

    @Override
    public IBeeModifier getBeeModifier() {
        return beeModifier;
    }

    @Override
    public List<String> getFrameTooltip() {
        List<String> tooltip = new ArrayList<>();
        // #tr tooltip.mutagenic_frame.mutation
        // # §aMutation chance ×%s
        // # zh_CN §a杂交成功率 ×%s
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "tooltip.mutagenic_frame.mutation",
                Config.mutagenicFrameMutationMultiplier));
        // #tr tooltip.mutagenic_frame.decay
        // # §aGenetic decay: none
        // # zh_CN §a基因衰变：无
        tooltip.add(StatCollector.translateToLocal("tooltip.mutagenic_frame.decay"));
        // #tr tooltip.mutagenic_frame.durability
        // # §bNever wears out
        // # zh_CN §b永不磨损
        tooltip.add(StatCollector.translateToLocal("tooltip.mutagenic_frame.durability"));
        return tooltip;
    }

    private static class MutagenicFrameBeeModifier extends DefaultBeeModifier {

        @Override
        public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
            return Config.mutagenicFrameMutationMultiplier;
        }

        @Override
        public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
            return Config.mutagenicFrameGeneticDecay;
        }
    }
}
