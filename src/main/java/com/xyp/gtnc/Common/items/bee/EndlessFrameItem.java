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
