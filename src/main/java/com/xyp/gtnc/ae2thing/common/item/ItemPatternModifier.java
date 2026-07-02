package com.xyp.gtnc.ae2thing.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.common.tabs.AE2ThingTabs;
import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.inventory.InventoryHandler;
import com.xyp.gtnc.ae2thing.inventory.gui.GuiType;
import com.xyp.gtnc.ae2thing.inventory.item.IItemInventory;
import com.xyp.gtnc.ae2thing.inventory.item.PatternModifierInventory;
import com.xyp.gtnc.ae2thing.util.BlockPos;
import com.xyp.gtnc.ae2thing.util.GTUtil;
import com.xyp.gtnc.ae2thing.util.NameConst;

import appeng.api.parts.IPart;
import appeng.api.util.IInterfaceViewable;
import appeng.tile.networking.TileCableBus;
import appeng.util.Platform;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemPatternModifier extends BaseItem implements IItemInventory {

    public ItemPatternModifier() {
        super();
        // #tr item.pattern_modifier.name
        // # Pattern Modifier
        // # zh_CN 样板修改器
        setUnlocalizedName(NameConst.ITEM_PATTERN_MODIFIER);
        setTextureName(
            AE2Thing.resource(NameConst.ITEM_PATTERN_MODIFIER)
                .toString());
    }

    @Override
    public ItemPatternModifier register() {
        GameRegistry.registerItem(this, NameConst.ITEM_PATTERN_MODIFIER, AE2Thing.MODID);
        setCreativeTab(AE2ThingTabs.INSTANCE);
        return this;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack item, World w, EntityPlayer player) {
        if (!player.isSneaking()) {
            InventoryHandler.openGui(
                player,
                w,
                new BlockPos(player.inventory.currentItem, 0, 0),
                ForgeDirection.UNKNOWN,
                this.guiGuiType(item));
        }
        return item;
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (Platform.isServer() && player.isSneaking()) {
            TileEntity te = world.getTileEntity(x, y, z);
            PatternModifierInventory patternModifierInventory = new PatternModifierInventory(itemstack, player);
            IInterfaceViewable host;
            if (te instanceof IInterfaceViewable h) {
                host = h;
            } else if (Mods.isLegacyGt5Loaded() || Mods.isGt5UnofficialLoaded()) {
                host = GTUtil.getIInterfaceViewable(te);
            } else if (te instanceof TileCableBus bus) {
                Vec3 vec = Vec3.createVectorHelper(hitX, hitY, hitZ);
                IPart part = bus.selectPart(vec).part;
                if (part instanceof IInterfaceViewable) {
                    host = (IInterfaceViewable) part;
                } else {
                    host = null;
                }
            } else {
                host = null;
            }
            patternModifierInventory.extractToHost(host);
            return true;
        }
        return false;
    }

    private GuiType guiGuiType(ItemStack item) {
        return GuiType.PATTERN_MODIFIER;
    }

    @Override
    public Object getInventory(ItemStack stack, World world, int x, int y, int z, EntityPlayer player) {
        return new PatternModifierInventory(stack, x, player);
    }
}
