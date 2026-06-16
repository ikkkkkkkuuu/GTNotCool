package com.xyp.gtnc.Common.items.wildcard;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.ScienceNotCool;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.items.misc.ItemEncodedPattern;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WildcardPatternItem extends ItemEncodedPattern {

    public static final String UNLOCALIZED_NAME = "wildcardpattern";
    public static final String ITEM_NAME = "WildcardPattern";

    public WildcardPatternItem() {
        super();
        setMaxStackSize(1);
        setCreativeTab(GTNCCreativeTabs.GTNCItem);
        // #tr item.wildcardpattern.name
        // # Wildcard Pattern
        // # zh_CN 通配样板符
        setUnlocalizedName(UNLOCALIZED_NAME);
        setTextureName(ScienceNotCool.RESOURCE_ROOT_ID + ":wildcard_pattern");
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        WildcardPatternGenerator.markAsWildcard(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
        if (stack != null && stack.stackTagCompound == null) {
            WildcardPatternGenerator.markAsWildcard(stack);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null) return stack;

        WildcardPatternGenerator.markAsWildcard(stack);
        if (!world.isRemote) {
            try {
                com.xyp.gtnc.Common.gui.modularui.wildcard.WildcardPatternGuiHandler.openGui(player, stack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        WildcardPatternGenerator.markAsWildcard(stack);
        return false;
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(ItemStack stack, World world) {
        return WildcardPatternGenerator.getDetailsForItem(stack, world);
    }

    @Override
    public ItemStack getOutput(ItemStack item) {
        if (!WildcardPatternGenerator.isWildcardPattern(item)) {
            return super.getOutput(item);
        }
        return WildcardPatternGenerator.getOutputForItem(item, null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCheckedInformation(ItemStack stack, EntityPlayer player, List<String> lines,
        boolean advancedTooltips) {
        if (!WildcardPatternGenerator.isWildcardPattern(stack)) {
            WildcardPatternGenerator.markAsWildcard(stack);
        }

        // 实时计算考虑排除规则后的实际配方数量(添加异常保护)
        int actualCount = 0;
        try {
            actualCount = WildcardPatternGenerator.countActualPatternsAfterExclude(stack);
        } catch (Exception e) {
            // 如果计算失败,使用保存的固定值作为fallback
            actualCount = WildcardPatternState.getExpandedPatternCount(stack);
        }

        // #tr tooltip.wildcardpattern.expand_count
        // # Expanded Patterns: %d
        // # zh_CN 展开配方数: %d
        lines.add(StatCollector.translateToLocalFormatted("tooltip.wildcardpattern.expand_count", actualCount));

        // #tr tooltip.wildcardpattern.usage
        // # Right-click to configure wildcard rules
        // # zh_CN 右键配置通配规则
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.usage"));

        // #tr tooltip.wildcardpattern.exclude_rules
        // # Exclude Rules: steel* (prefix), *Steel (suffix), *steel* (contains), exact name
        // # zh_CN 排除规则: steel*(前缀), *Steel(后缀), *steel*(包含), 精确名称
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.exclude_rules"));

        // #tr tooltip.wildcardpattern.fluid_syntax_title
        // # §6Fluid Syntax:
        // # zh_CN §6流体语法:
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.fluid_syntax_title"));

        // #tr tooltip.wildcardpattern.fluid_syntax_molten_all
        // # molten.* §7All materials with molten fluid
        // # zh_CN molten.* §7所有有熔融流体的材料
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.fluid_syntax_molten_all"));

        // #tr tooltip.wildcardpattern.fluid_syntax_molten_single
        // # molten.Iron §7Only molten iron
        // # zh_CN molten.Iron §7仅熔融铁
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.fluid_syntax_molten_single"));

        // #tr tooltip.wildcardpattern.fluid_syntax_plasma
        // # plasma.* §7All plasma
        // # zh_CN plasma.* §7所有等离子体
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.fluid_syntax_plasma"));

        // #tr tooltip.wildcardpattern.fluid_syntax_liquid
        // # liquid.* §7All liquids
        // # zh_CN liquid.* §7所有液体
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.fluid_syntax_liquid"));

        // #tr tooltip.wildcardpattern.fluid_syntax_gas
        // # gas.* §7All gases
        // # zh_CN gas.* §7所有气体
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.fluid_syntax_gas"));

        // #tr tooltip.wildcardpattern.item_syntax_title
        // # §6Item Syntax:
        // # zh_CN §6物品语法:
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.item_syntax_title"));

        // #tr tooltip.wildcardpattern.item_syntax_name
        // # Enter item name, supports * wildcard
        // # zh_CN 输入物品名称，支持*通配
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.item_syntax_name"));

        // #tr tooltip.wildcardpattern.item_syntax_oredict
        // # OreDict mode: plate.*, ingot.*, dust.* etc
        // # zh_CN 矿辞模式: plate.*, ingot.*, dust.* 等
        lines.add(StatCollector.translateToLocal("tooltip.wildcardpattern.item_syntax_oredict"));
    }
}
