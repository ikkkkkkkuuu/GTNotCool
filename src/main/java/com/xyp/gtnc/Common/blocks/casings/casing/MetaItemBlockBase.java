package com.xyp.gtnc.Common.blocks.casings.casing;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.xyp.gtnc.utils.item.MetaTooltipUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class MetaItemBlockBase extends ItemBlock {

    public MetaItemBlockBase(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public Block getThisBlock() {
        return field_150939_a;
    }

    public abstract boolean canCreatureSpawn();

    @SideOnly(Side.CLIENT)
    public abstract String[] getTooltips(int meta);

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        return this.field_150939_a.getUnlocalizedName() + "." + this.getDamage(aStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List<String> theTooltipsList,
        boolean p_77624_4_) {
        if (null == aItemStack) return;
        String[] tooltips = getTooltips(aItemStack.getItemDamage());
        MetaTooltipUtils.appendTooltips(tooltips, theTooltipsList);
        if (!canCreatureSpawn()) {
            // #tr Tooltip_NoMobsSpawnInThisBlock
            // # Mobs cannot Spawn on this Block
            // # zh_CN 生物不会在这个方块上生成
            theTooltipsList.add(StatCollector.translateToLocal("Tooltip_NoMobsSpawnInThisBlock"));
            // #tr Tooltip_NoTileEntitySpawnInThisBlock
            // # This is NOT a TileEntity!
            // # zh_CN 这不是一个实体！
            theTooltipsList.add(StatCollector.translateToLocal("Tooltip_NoTileEntitySpawnInThisBlock"));
        }
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

}
