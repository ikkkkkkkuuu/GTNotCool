package com.xyp.gtnc.Common.items.toolbelt;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Common.items.toolbelt.common.Screens;
import com.xyp.gtnc.Common.items.toolbelt.slot.BeltAttachment;
import com.xyp.gtnc.Common.items.toolbelt.slot.IBeltSlotItem;
import com.xyp.gtnc.Loader.ItemsLoader;
import com.xyp.gtnc.utils.keybind.KeyBindManager;

public class ToolBeltItem extends Item implements IBeltSlotItem {

    public static final String NBT_BELT_SIZE = "BeltSize";
    public static final String NBT_ITEMS = "Items";

    // #tr item.toolbelt.name
    // # Tool Belt
    // # zh_CN 工具腰带
    public ToolBeltItem() {
        setMaxStackSize(1);
        setUnlocalizedName("toolbelt");
        setTextureName("sciencenotcool:belt");
        setCreativeTab(GTNCCreativeTabs.GTNCItem);
    }

    /**
     * 创建指定大小的腰带物品堆栈
     */
    public static ItemStack of(int size) {
        ItemStack stack = new ItemStack(ItemsLoader.toolBelt);
        setBeltSize(stack, Math.max(2, Math.min(9, size)));
        return stack;
    }

    public static int getBeltSize(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ToolBeltItem)) return 2;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_BELT_SIZE)) return 2;

        int size = tag.getInteger(NBT_BELT_SIZE);
        return Math.max(2, Math.min(9, size));
    }

    public static void setBeltSize(ItemStack stack, int newSize) {
        if (stack == null || !(stack.getItem() instanceof ToolBeltItem)) return;

        newSize = Math.max(2, Math.min(9, newSize));
        int oldSize = getBeltSize(stack);

        if (newSize != oldSize) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }

            // Resize inventory
            ItemStack[] oldInv = getBeltInventory(stack);
            ItemStack[] newInv = new ItemStack[newSize];
            int fill = Math.min(oldInv.length, Math.min(oldSize, newSize));
            for (int i = 0; i < fill; i++) {
                newInv[i] = oldInv[i] != null ? oldInv[i].copy() : null;
            }

            tag.setInteger(NBT_BELT_SIZE, newSize);
            writeInventoryToNBT(tag, newInv);
        }
    }

    /**
     * Get the belt's internal inventory as an array.
     */
    public static ItemStack[] getBeltInventory(ItemStack stack) {
        int size = getBeltSize(stack);
        ItemStack[] inv = new ItemStack[size];

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(NBT_ITEMS)) return inv;

        NBTTagList list = tag.getTagList(NBT_ITEMS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < Math.min(list.tagCount(), size); i++) {
            NBTTagCompound slotTag = list.getCompoundTagAt(i);
            int slot = slotTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < size) {
                inv[slot] = ItemStack.loadItemStackFromNBT(slotTag);
            }
        }
        return inv;
    }

    /**
     * Set a single slot in the belt's internal inventory.
     */
    public static void setBeltSlot(ItemStack beltStack, int slot, ItemStack stack) {
        int size = getBeltSize(beltStack);
        if (slot < 0 || slot >= size) return;

        ItemStack[] inv = getBeltInventory(beltStack);
        inv[slot] = stack;

        NBTTagCompound tag = beltStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            beltStack.setTagCompound(tag);
        }
        writeInventoryToNBT(tag, inv);
    }

    /**
     * Get a single slot from the belt's internal inventory.
     */
    public static ItemStack getBeltSlot(ItemStack beltStack, int slot) {
        int size = getBeltSize(beltStack);
        if (slot < 0 || slot >= size) return null;

        ItemStack[] inv = getBeltInventory(beltStack);
        return inv[slot];
    }

    private static void writeInventoryToNBT(NBTTagCompound tag, ItemStack[] inv) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inv.length; i++) {
            if (inv[i] != null) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                inv[i].writeToNBT(slotTag);
                list.appendTag(slotTag);
            }
        }
        tag.setTag(NBT_ITEMS, list);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            Screens.openBeltScreen(player, player.inventory.currentItem);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Open belt screen on right click
        if (!world.isRemote) {
            Screens.openBeltScreen(player, player.inventory.currentItem);
        }
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        int size = getBeltSize(stack);
        tooltip.add("\u00a77Slots: \u00a7e" + size);
        if (size < 9) {
            tooltip.add("\u00a77Surround with leather to upgrade");
        }

        // 添加空行分隔
        tooltip.add("");

        // 添加按键提示（动态读取按键绑定）
        String beltSlotKey = KeyBindManager.openBeltSlotKeybind != null
            ? org.lwjgl.input.Keyboard.getKeyName(KeyBindManager.openBeltSlotKeybind.getKeyCode())
            : "V";
        String menuKey = KeyBindManager.openToolMenuKeybind != null
            ? org.lwjgl.input.Keyboard.getKeyName(KeyBindManager.openToolMenuKeybind.getKeyCode())
            : "R";

        // #tr tooltip.toolbelt.belt_slot
        // # Press %s to open belt slot
        // # zh_CN 按 %s 打开腰带栏
        tooltip.add("\u00a7e" + StatCollector.translateToLocalFormatted("tooltip.toolbelt.belt_slot", beltSlotKey));

        // #tr tooltip.toolbelt.radial_menu
        // # Press %s to open radial menu
        // # zh_CN 按 %s 打开菜单栏
        tooltip.add("\u00a7e" + StatCollector.translateToLocalFormatted("tooltip.toolbelt.radial_menu", menuKey));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        tickAllSlots(stack, world, entity);
    }

    @Override
    public void onWornTick(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot) {
        tickAllSlots(stack, slot.getOwner().worldObj, slot.getOwner());
    }

    @Override
    public void onEquipped(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot) {}

    @Override
    public void onUnequipped(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot) {}

    @Override
    public boolean canEquip(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot) {
        return true;
    }

    @Override
    public boolean canUnequip(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot) {
        return true;
    }

    private void tickAllSlots(ItemStack beltStack, World world, Entity entity) {
        int size = getBeltSize(beltStack);
        for (int i = 0; i < size; i++) {
            ItemStack contained = getBeltSlot(beltStack, i);
            if (contained != null && contained.getItem() instanceof IItemInBelt) {
                ((IItemInBelt) contained.getItem()).onWornTick(contained, beltStack);
            }
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    /**
     * Create an upgraded copy of the belt (+1 slot).
     */
    public static ItemStack makeUpgradedStack(ItemStack stack) {
        int slots = getBeltSize(stack);
        return of(slots + 1);
    }
}
