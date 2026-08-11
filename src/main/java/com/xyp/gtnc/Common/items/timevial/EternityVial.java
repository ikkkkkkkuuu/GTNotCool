package com.xyp.gtnc.Common.items.timevial;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;
import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.entity.EntityImmortalItem;
import fox.spiteful.avaritia.items.LudicrousItems;
import fox.spiteful.avaritia.render.ICosmicRenderItem;

/** The only Time Vial variant ported from NH-Utilities: infinite charge and six fixed acceleration stages. */
public final class EternityVial extends Item implements ICosmicRenderItem {

    public static final String ITEM_NAME = "EternityVial";
    private static final double SEARCH_HALF_SIZE = 0.05D;
    private static final float[] STAGE_PITCHES = { 0.749154F, 0.793701F, 0.890899F, 1.059463F, 0.943874F, 0.890899F };

    private IIcon cosmicMask;

    public EternityVial() {
        setMaxStackSize(1);
        setCreativeTab(GTNCCreativeTabs.GTNCItem);
        // #tr item.eternity_vial.name
        // # Eternity Vial
        // # zh_CN 永恒之瓶
        setUnlocalizedName("eternity_vial");
        setTextureName(ScienceNotCool.MODID + ":TimeVial/EternityVial");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Returning true on the client prevents PlayerControllerMP from sending the block-use packet to the server.
        // Let the client continue the normal interaction path; the server-side call below owns the accelerator entity.
        if (world.isRemote) return false;

        EntityTimeAccelerator accelerator = findAccelerator(world, x, y, z);
        if (accelerator == null) {
            accelerator = new EntityTimeAccelerator(world, x, y, z);
            world.spawnEntityInWorld(accelerator);
        } else if (player.isSneaking()) {
            accelerator.setDead();
        } else {
            accelerator.advanceStage();
        }

        int stage = accelerator.getStage();
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "note.harp", 0.5F, STAGE_PITCHES[stage]);
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return true;
    }

    private static EntityTimeAccelerator findAccelerator(World world, int x, int y, int z) {
        double centerX = x + 0.5D;
        double centerY = y + 0.5D;
        double centerZ = z + 0.5D;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            centerX - SEARCH_HALF_SIZE,
            centerY - SEARCH_HALF_SIZE,
            centerZ - SEARCH_HALF_SIZE,
            centerX + SEARCH_HALF_SIZE,
            centerY + SEARCH_HALF_SIZE,
            centerZ + SEARCH_HALF_SIZE);
        List<EntityTimeAccelerator> accelerators = world.getEntitiesWithinAABB(EntityTimeAccelerator.class, box);
        for (EntityTimeAccelerator accelerator : accelerators) {
            if (!accelerator.isDead) return accelerator;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        cosmicMask = register.registerIcon(ScienceNotCool.MODID + ":TimeVial/EternityVial_mask");
    }

    @Override
    public IIcon getMaskTexture(ItemStack stack, EntityPlayer player) {
        return cosmicMask;
    }

    @Override
    public float getMaskMultiplier(ItemStack stack, EntityPlayer player) {
        return 1.0F;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return LudicrousItems.cosmic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        // #tr tooltip.eternity_vial.eternity
        // # Eternity
        // # zh_CN 永恒
        tooltip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("tooltip.eternity_vial.eternity"));
        // #tr tooltip.eternity_vial.use
        // # Six stages, doubling after each right-click:
        // # zh_CN 六档加速，每次右键自动翻倍：
        tooltip.add(
            EnumChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.eternity_vial.use")
                + " "
                + Config.eternityVialInitialMultiplier
                + "x - "
                + EntityTimeAccelerator.multiplierForStage(EntityTimeAccelerator.STAGE_COUNT - 1)
                + "x");
        // #tr tooltip.eternity_vial.remove
        // # Shift + right-click: remove acceleration
        // # zh_CN Shift + 右键：移除加速
        tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.eternity_vial.remove"));
        // #tr tooltip.eternity_vial.duration
        // # Duration (seconds):
        // # zh_CN 持续时间（秒）：
        tooltip.add(
            EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.eternity_vial.duration")
                + " "
                + Config.eternityVialDurationSeconds);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        return new EntityImmortalItem(world, location, stack);
    }
}
