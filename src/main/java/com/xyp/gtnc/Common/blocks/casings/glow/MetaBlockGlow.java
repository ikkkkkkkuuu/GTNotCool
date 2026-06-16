package com.xyp.gtnc.Common.blocks.casings.glow;

import static com.xyp.gtnc.Common.blocks.casings.glow.ItemBlockGlow.META_SET;
import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Common.blocks.casings.BlockStaticDataClientOnly;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MetaBlockGlow extends Block {

    public String unlocalizedName;

    public MetaBlockGlow(Material materialIn) {
        super(materialIn);
    }

    public MetaBlockGlow() {
        this(Material.iron);
        this.setLightLevel(1);
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(GTNCCreativeTabs.GTNCItemBlock);
    }

    public MetaBlockGlow(String unlocalizedName) {
        this();
        this.unlocalizedName = unlocalizedName;
    }

    @Override
    public String getUnlocalizedName() {
        return "tile." + this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return meta < BlockStaticDataClientOnly.GLOW_ICONS.size() ? BlockStaticDataClientOnly.GLOW_ICONS.get(meta)
            : BlockStaticDataClientOnly.GLOW_ICONS.get(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlockGlow/0");
        for (int Meta : META_SET) {
            BlockStaticDataClientOnly.GLOW_ICONS
                .put(Meta, reg.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaBlockGlow/" + Meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> list) {
        for (int Meta : META_SET) {
            list.add(new ItemStack(aItem, 1, Meta));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
