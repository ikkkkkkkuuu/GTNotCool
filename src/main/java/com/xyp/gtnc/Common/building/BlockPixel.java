package com.xyp.gtnc.Common.building;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;

import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 建筑生成器的<b>像素方块</b>——放大建筑外壳的唯一组成方块。
 * <p>
 * 贴图是一张纯白 16×16。真正的颜色靠 {@link #colorMultiplier} 在<b>区块渲染重建</b>时按坐标查
 * {@link PixelColorStore} 返回任意 {@code 0xRRGGBB},白底 × 颜色 = 该像素的真实颜色。因此:
 * <ul>
 * <li><b>全彩</b>:2^24 种颜色,无 meta/调色板量化损失;</li>
 * <li><b>零 TileEntity</b>:普通方块进静态区块渲染批次,几十万个也流畅;</li>
 * <li>{@code colorMultiplier} 只在区块重建时调用一次(结果烤进顶点缓存),非每帧。</li>
 * </ul>
 * <p>
 * <b>不可手动获得/无掉落</b>:它只由建筑生成器放置、由撤销清除,不进创造栏、破坏不掉落
 * (避免污染正常游戏)。M1 阶段颜色由生成时客户端直接填充 {@link PixelColorStore}。
 */
public class BlockPixel extends Block {

    @SideOnly(Side.CLIENT)
    private net.minecraft.util.IIcon whiteIcon;

    public BlockPixel() {
        super(Material.rock);
        // #tr tile.gtnc.pixel.name
        // # Pixel Block
        // # zh_CN 像素方块
        setBlockName("gtnc.pixel");
        setHardness(1.0f);
        setResistance(6.0f);
        // 不进任何创造标签页:玩家拿不到,只能由生成器产生。
        setCreativeTab((net.minecraft.creativetab.CreativeTabs) null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.whiteIcon = reg.registerIcon(ScienceNotCool.RESOURCE_ROOT_ID + ":pixel_white");
        this.blockIcon = this.whiteIcon;
    }

    /**
     * 区块渲染重建时按坐标返回该像素的 RGB。这是全彩的核心:白色贴图被此返回值相乘染成任意颜色。
     * 只在客户端、且只在区块重建时被调用一次。
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        // 渲染时传入的 IBlockAccess 实际是 ChunkCache，拿不到维度；客户端同时只渲染一个世界，
        // 故直接用 Minecraft.theWorld 的维度查颜色表。
        net.minecraft.client.multiplayer.WorldClient w = net.minecraft.client.Minecraft.getMinecraft().theWorld;
        int dim = w != null ? w.provider.dimensionId : 0;
        return PixelColorStore.get(dim, x, y, z);
    }

    // 破坏掉落自身、可重新放置：玩家挖掉一格像素方块后能拿在手里、填回原位。
    // 颜色不随破坏清除（服务端 PixelBuildingData + 客户端 PixelColorStore 都保留该坐标的 RGB），
    // 故放回原位时 colorMultiplier 自动查回原本颜色；放到没有颜色记录的新位置则渲染为白色。
}
