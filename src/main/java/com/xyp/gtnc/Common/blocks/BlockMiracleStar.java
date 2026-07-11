package com.xyp.gtnc.Common.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.xyp.gtnc.Common.tile.TileMiracleStar;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Invisible carrier block that hosts the {@link TileMiracleStar} used for the Miracle Door running animation. The block
 * itself renders nothing (a TESR draws the rotating star model); it is placed at the structure center while the machine
 * runs and removed when it stops. Ported from TST's {@code BlockStar}.
 */
public class BlockMiracleStar extends Block {

    public BlockMiracleStar() {
        super(Material.iron);
        this.setResistance(20f);
        this.setHardness(-1.0f);
        this.setBlockName("miracleStar");
        this.setLightLevel(100.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        // Never actually drawn (renderAsNormalBlock=false); point at an existing texture to avoid missing-icon spam.
        blockIcon = iconRegister.registerIcon("sciencenotcool:MetaCasing02/10");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canRenderInPass(int a) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileMiracleStar();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        return new ArrayList<>();
    }
}
