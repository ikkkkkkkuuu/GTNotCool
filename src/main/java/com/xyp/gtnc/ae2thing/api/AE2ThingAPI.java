package com.xyp.gtnc.ae2thing.api;

import static com.xyp.gtnc.ae2thing.nei.NEI_TH_Config.getConfigValue;
import static net.minecraft.init.Items.glass_bottle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.MutablePair;

import com.glodblock.github.util.Util;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.client.event.NotificationEvent;
import com.xyp.gtnc.ae2thing.common.Config;
import com.xyp.gtnc.ae2thing.nei.ButtonConstants;
import com.xyp.gtnc.ae2thing.network.CPacketSwitchGuis;
import com.xyp.gtnc.ae2thing.util.Ae2Reflect;

import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.Grid;
import appeng.util.ReadableNumberConverter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class AE2ThingAPI implements IAE2ThingAPI {

    public static final ItemStack BUCKET = new ItemStack(Items.bucket, 1);
    public static final ItemStack GLASS_BOTTLE = new ItemStack(glass_bottle, 1);

    public static int maxSelectionRows = 5;
    private static final AE2ThingAPI API = new AE2ThingAPI();
    public static final int CRAFTING_HISTORY_SIZE = Config.craftingHistorySize;
    private static final Set<Class<? extends Item>> backpackItems = new HashSet<>();
    private static final HashMap<Class<?>, String> inputFields = new HashMap<>();

    private static ItemStack fluidContainer = BUCKET;
    public static final ReadableNumberConverter readableNumber = ReadableNumberConverter.INSTANCE;

    public static AE2ThingAPI instance() {
        return API;
    }

    @Override
    public void putText(String text) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen != null) {
            putText(screen.getClass(), text);
        }
    }

    @Override
    public void putText(Class<?> cls, String text) {
        inputFields.put(cls, text);
    }

    @Override
    public String getText(Class<?> cls) {
        return inputFields.getOrDefault(cls, "");
    }

    @Override
    public String getText() {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen != null) {
            return getText(screen.getClass());
        }
        return "";
    }

    public Terminal terminal() {
        return Terminal.API;
    }

    @Override
    public boolean isBlacklistedInStorage(Item item) {
        for (Class<? extends Item> cls : backpackItems) {
            if (cls.isInstance(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void blacklistItemInStorage(Class<? extends Item> item) {
        backpackItems.add(item);
    }

    @Override
    public void addBackpackItem(Class<? extends Item> item) {
        blacklistItemInStorage(item);
    }

    @Override
    public boolean isBackpackItem(Item item) {
        return isBlacklistedInStorage(item);
    }

    @Override
    public boolean isBackpackItem(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() != null && isBackpackItem(itemStack.getItem());
    }

    @Override
    public IInventory getBackpackInv(ItemStack is) {
        return null;
    }

    @Override
    public boolean isBackpackItemInv(ItemStack is) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Pinned getPinned() {
        return Pinned.INSTANCE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void openDualinterfaceTerminal() {
        // Reopen whichever view the player last switched to on the terminal, defaulting to the dual interface terminal.
        // Let the server resolve the saved mode from the terminal's authoritative NBT: when the terminal sits in a
        // Baubles slot the server-side NBT change is not synced back to the client, so reading it here would always
        // see the stale (default) value and, worse, send that stale value back and overwrite the real saved mode.
        AE2Thing.proxy.netHandler.sendToServer(CPacketSwitchGuis.restoreLast());
    }

    @Override
    public ItemStack getFluidContainer(IAEFluidStack fluid) {
        return getFluidContainer(fluid.getFluidStack());
    }

    @Override
    public ItemStack getFluidContainer(FluidStack fluid) {
        if (canFillContainer(BUCKET, fluid)) {
            return BUCKET;
        } else if (getDefaultFluidContainer() != BUCKET && canFillContainer(getDefaultFluidContainer(), fluid)) {
            return getDefaultFluidContainer();
        } else {
            return GLASS_BOTTLE;
        }
    }

    @Override
    public void setDefaultFluidContainer(ItemStack item) {
        fluidContainer = item;
    }

    @Override
    public ItemStack getDefaultFluidContainer() {
        return fluidContainer;
    }

    private boolean canFillContainer(ItemStack is, FluidStack fluidStack) {
        MutablePair<Integer, ItemStack> result = Util.FluidUtil.fillStack(is, fluidStack);
        return result != null && result.left != 0;
    }

    @Override
    public String getVersion() {
        return AE2Thing.VERSION;
    }

    @Override
    public long getStorageMyID(Grid grid) {
        return Ae2Reflect.getMyStorage(grid)
            .getID();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCraftingCompleteNotification(IAEItemStack item) {
        if (getConfigValue(ButtonConstants.CRAFTING_NOTIFICATION)) {
            MinecraftForge.EVENT_BUS.post(new NotificationEvent(item));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addNotification(String tile, String Content, ItemStack item) {
        if (getConfigValue(ButtonConstants.CRAFTING_NOTIFICATION)) {
            MinecraftForge.EVENT_BUS.post(new NotificationEvent(tile, Content, item));
        }
    }

}
