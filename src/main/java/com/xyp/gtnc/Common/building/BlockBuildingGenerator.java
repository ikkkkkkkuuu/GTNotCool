package com.xyp.gtnc.Common.building;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 建筑生成器方块。
 * <p>
 * 读取一台多方块机器的结构定义，在世界里生成一个<b>等比例放大</b>的空壳版本：原结构每个方块 → 放大到
 * {@code scale×scale×scale}（scale = 贴图像素宽，16x 材质为 16）的区域，只在朝外部的面铺一层 {@link BlockPixel}，
 * 每像素方块按原方块该面材质的对应像素上色，内部中空。
 * <p>
 * <b>交互</b>：右键打开 MUI2 GUI（{@link TileBuildingGenerator}）——放入机器物品、调旋转/偏移、看 3D 预览、
 * 点保存生成、点撤销还原。
 * <p>
 * <b>破坏生成器 = 放弃管理权，建筑永久保留</b>：破坏时<b>不</b>撤销已生成的建筑（颜色记录也保留），
 * 建筑作为永久像素方块群留存世界。若要整体还原，须在生成器<b>仍在时</b>用 GUI 撤销按钮；破坏后只能逐块手动拆。
 */
public class BlockBuildingGenerator extends Block {

    @SideOnly(Side.CLIENT)
    private net.minecraft.util.IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private net.minecraft.util.IIcon sideIcon;

    public BlockBuildingGenerator() {
        super(Material.iron);
        // #tr tile.gtnc.building_generator.name
        // # Building Generator
        // # zh_CN 建筑生成器
        setBlockName("gtnc.building_generator");
        setHardness(5.0f);
        setResistance(10.0f);
        setCreativeTab(GTNCCreativeTabs.GTNCItemBlock);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileBuildingGenerator();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.topIcon = reg.registerIcon(ScienceNotCool.RESOURCE_ROOT_ID + ":building_generator_top");
        this.sideIcon = reg.registerIcon(ScienceNotCool.RESOURCE_ROOT_ID + ":building_generator_side");
        this.blockIcon = this.sideIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.IIcon getIcon(int side, int meta) {
        return (side == 0 || side == 1) ? topIcon : sideIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBuildingGenerator && player instanceof EntityPlayerMP) {
            // MUI2：服务端打开面板并把 UI 包推给客户端。
            com.cleanroommc.modularui.factory.TileEntityGuiFactory.INSTANCE.open(player, x, y, z);
        }
        return true;
    }

    // 破坏生成器不撤销建筑：建筑永久保留（颜色记录随 PixelBuildingData 持久化，与生成器存亡无关）。
    // 整体撤销须在生成器仍在时用 GUI 撤销按钮。
}
