package com.xyp.gtnc.Common.items.bee;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IHiveFrame;
import fox.spiteful.avaritia.render.ICosmicRenderItem;

/**
 * 无尽框架——纯产物/驻留框架，用「无尽贪婪」的宇宙彩虹动态材质呈现。插入蜂箱/蜂房框架槽后：
 * <ul>
 * <li><b>寿命 ×{@link Config#endlessFrameLifespanModifier}</b>(默认 1000000)——{@code IndividualLiving.age} 里
 * {@code ageModifier = 1/lifespanModifier} 趋近 0，蜂后几乎不掉血，等效"寿命无限"、长期驻留稳定产出；</li>
 * <li><b>产量 ×{@link Config#endlessFrameProductionModifier}</b>(默认 30，加法累加进 {@code BeeHousingModifier})；</li>
 * <li><b>突变率 ×{@link Config#endlessFrameMutationMultiplier}</b>(默认 0 = 完全不杂交，保持蜂种纯净)；</li>
 * <li>基因衰变 ×{@link Config#endlessFrameGeneticDecay}(默认 0 = 完全不衰变)、耐久
 * {@link Config#endlessFrameMaxDamage}(默认 0 = 永不磨损)。</li>
 * </ul>
 * <p>
 * <b>宇宙彩虹材质</b>：Avaritia 是本 mod 的硬依赖({@code required-after:Avaritia})，故直接实现其
 * {@link ICosmicRenderItem}——base 贴图是深色框架底，{@link #getMaskTexture} 返回白色框架 mask，Avaritia 的
 * {@code CosmicItemRenderer} + 已编译的 {@code cosmicShader} 会在 mask 白色区域透出动态星空彩虹(与无尽剑/无尽甲同款)。
 * 渲染器在 {@code RendererLoader} 里客户端注册。
 * <p>
 * <b>tooltip 自动生成</b>：仿 GT++ 不重写 {@link IHiveFrame#getFrameTooltip()},Forestry 自动按 modifier 数值渲染。
 * <p>
 * <b>与本 mod 蜜蜂杂交机隔离</b>：杂交机走模板法,不经过蜂箱框架逻辑,故不受本框架影响。
 */
public class EndlessFrameItem extends Item implements IHiveFrame, ICosmicRenderItem {

    public static final String UNLOCALIZED_NAME = "endless_frame";
    public static final String ITEM_NAME = "EndlessFrame";

    private final IBeeModifier beeModifier = new EndlessFrameBeeModifier();

    /**
     * 判断某个 {@link IBeeModifier} 是否来自无尽框架。{@code BeekeepingLogic.spawnOffspring} 拿到的
     * {@code IBeeHousing.getBeeModifiers()} 会把每个插入框架的 modifier 聚合进去，mixin 遍历它调用本方法即可
     * 判断蜂箱里是否插了无尽框架，从而决定是否抑制第二只公主蜂。
     * <p>
     * 覆盖两条路径：
     * <ul>
     * <li><b>原生蜂箱 (Forestry Apiary)</b>：直接把框架的 {@code getBeeModifier()} 加进迭代器，故迭代到的就是
     * {@link EndlessFrameBeeModifier} 本体，{@code instanceof} 快速命中。</li>
     * <li><b>Extra Bees 蜂箱组框架外壳</b>：框架被 {@code binnie...FrameComponentModifier} <b>包装</b>后加入
     * ({@code AlvearyController.getBeeModifiers()} 里出现的是 wrapper，不是本类)，{@code instanceof} 失效；但 wrapper
     * 会把 {@code getLifespanModifier/getMutationModifier} 转发给框架自己的 modifier。</li>
     * </ul>
     * 因此 fallback 用<b>行为指纹</b>识别：无尽框架的寿命倍率 = {@link Config#endlessFrameLifespanModifier}(默认 1e6)
     * 且突变倍率 = {@link Config#endlessFrameMutationMultiplier}(默认 0) 这一组合在所有框架里独一无二，无论直连还是
     * 经 wrapper 转发都成立。传 null 基因组安全(本类 getter 忽略入参)；对不忽略入参的其它 modifier 用 try/catch 兜底。
     * <p>
     * <b>注意</b>：指纹依赖上面两个配置项保持「反常值」(极大寿命 + 零突变)。若用户把它们改成中性值(如寿命 1.0)，
     * 蜂箱组里的指纹识别可能失准；此时可用全局开关 {@code enableBeeAlwaysSecondPrincess} 关闭本特性。原生蜂箱不受影响
     * (走 instanceof)。
     */
    public static boolean isEndlessFrameModifier(IBeeModifier modifier) {
        if (modifier == null) return false;
        // 快速路径：原生蜂箱直接加入本 modifier 本体。
        if (modifier instanceof EndlessFrameBeeModifier) return true;
        // Fallback：Extra Bees 蜂箱组的框架外壳把本 modifier 包成 FrameComponentModifier，instanceof 失效，
        // 改用行为指纹(寿命 + 突变倍率)识别，wrapper 会把这两个 getter 转发到本 modifier。
        try {
            float lifespan = modifier.getLifespanModifier(null, null, 1.0f);
            float mutation = modifier.getMutationModifier(null, null, 1.0f);
            return lifespan == Config.endlessFrameLifespanModifier && mutation == Config.endlessFrameMutationMultiplier;
        } catch (Throwable t) {
            return false;
        }
    }

    private IIcon cosmicMask;

    public EndlessFrameItem() {
        setMaxStackSize(1);
        setCreativeTab(GTNCCreativeTabs.GTNCItem);
        if (Config.endlessFrameMaxDamage > 0) {
            setMaxDamage(Config.endlessFrameMaxDamage);
        }
        // #tr item.endless_frame.name
        // # Endless Frame
        // # zh_CN 无尽框架
        setUnlocalizedName(UNLOCALIZED_NAME);
        setTextureName(ScienceNotCool.RESOURCE_ROOT_ID + ":endless_frame");
    }

    /** 耐久:maxDamage=0 时永不磨损,否则累加损耗、耗尽返回 null。 */
    @Override
    public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
        if (Config.endlessFrameMaxDamage <= 0) {
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

    // 不重写 getFrameTooltip():返回默认 null,让 Forestry 自动按 modifier 数值生成 tooltip。

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        super.registerIcons(ir);
        this.cosmicMask = ir.registerIcon(ScienceNotCool.RESOURCE_ROOT_ID + ":endless_frame_mask");
    }

    @Override
    public IIcon getMaskTexture(ItemStack stack, EntityPlayer player) {
        return cosmicMask;
    }

    @Override
    public float getMaskMultiplier(ItemStack stack, EntityPlayer player) {
        return 1.0f;
    }

    private static class EndlessFrameBeeModifier extends DefaultBeeModifier {

        @Override
        public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
            return Config.endlessFrameMutationMultiplier;
        }

        @Override
        public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
            return Config.endlessFrameLifespanModifier;
        }

        @Override
        public float getProductionModifier(IBeeGenome genome, float currentModifier) {
            return Config.endlessFrameProductionModifier;
        }

        @Override
        public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
            return Config.endlessFrameGeneticDecay;
        }
    }
}
