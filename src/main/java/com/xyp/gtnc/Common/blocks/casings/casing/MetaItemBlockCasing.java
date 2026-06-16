package com.xyp.gtnc.Common.blocks.casings.casing;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.api.IMetaBlock;
import com.xyp.gtnc.utils.item.MetaItemStackUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.render.TextureFactory;

public class MetaItemBlockCasing extends MetaItemBlockBase {

    public MetaItemBlockCasing(Block block) {
        super(block);
    }

    @Override
    public boolean canCreatureSpawn() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String[] getTooltips(int meta) {
        if (getThisBlock() instanceof IMetaBlock thisBlock) {
            return thisBlock.getTooltips(meta);
        }
        return new String[0];
    }

    public static ItemStack initMetaBlock(int meta, MetaCasingBase basicBlock) {
        return MetaItemStackUtils.initMetaItemStack(meta, basicBlock, basicBlock.getUsedMetaSet());
    }

    public static ItemStack initMetaBlock(int meta, MetaCasingBase basicBlock, String[] tooltips) {
        basicBlock.getTooltipsMap()
            .put(meta, tooltips);
        return MetaItemStackUtils.initMetaItemStack(meta, basicBlock, basicBlock.getUsedMetaSet());
    }

    public static void setCasingTextureForMetaBlock(int meta, MetaBlockCasingBase basicBlock) {
        Textures.BlockIcons
            .setCasingTextureForId(basicBlock.getTextureIndex(meta), TextureFactory.of(basicBlock, meta));
    }

    public static ItemStack initMetaBlockCasing(int meta, MetaBlockCasingBase basicBlock) {
        setCasingTextureForMetaBlock(meta, basicBlock);
        return initMetaBlock(meta, basicBlock);
    }

    public static ItemStack initMetaBlockCasing(int meta, MetaBlockCasingBase basicBlock, String[] tooltips) {
        setCasingTextureForMetaBlock(meta, basicBlock);
        return initMetaBlock(meta, basicBlock, tooltips);
    }
}
