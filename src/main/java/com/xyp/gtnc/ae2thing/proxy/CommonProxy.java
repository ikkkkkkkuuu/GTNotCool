package com.xyp.gtnc.ae2thing.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.xyp.gtnc.ae2thing.api.AE2ThingAPI;
import com.xyp.gtnc.ae2thing.api.adapter.crafting.AECraftingTerminal;
import com.xyp.gtnc.ae2thing.api.adapter.terminal.item.DualInterfaceTerminalHandler;
import com.xyp.gtnc.ae2thing.common.item.ItemPatternModifier;
import com.xyp.gtnc.ae2thing.common.item.ItemWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.inventory.item.PatternModifierInventory;
import com.xyp.gtnc.ae2thing.loader.InvLoader;
import com.xyp.gtnc.ae2thing.loader.PatternTerminalLoader;
import com.xyp.gtnc.ae2thing.loader.PatternTerminalMouseWheelLoader;
import com.xyp.gtnc.ae2thing.network.wrapper.AE2ThingNetworkWrapper;
import com.xyp.gtnc.ae2thing.util.ModAndClassUtil;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.util.Platform;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {

    public AE2ThingNetworkWrapper netHandler = new AE2ThingNetworkWrapper("ae2thing_dit");

    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        ModAndClassUtil.init();
    }

    public void init(FMLInitializationEvent event) {
        AE2ThingAPI.instance()
            .terminal()
            .registerCraftingTerminal(new AECraftingTerminal());
        new PatternTerminalMouseWheelLoader().run();
        new PatternTerminalLoader().run();
        new InvLoader().run();
    }

    public void postInit(FMLPostInitializationEvent event) {
        AE2ThingAPI.instance()
            .terminal()
            .registerTerminalItem(ItemWirelessDualInterfaceTerminal.class, new DualInterfaceTerminalHandler());
    }

    @SubscribeEvent
    public void pickUpEvent(EntityItemPickupEvent event) {
        if (Platform.isClient() || event.entityPlayer == null) return;
        try {
            EntityPlayer player = event.entityPlayer;
            ItemStack pattern = event.item.getEntityItem();
            if (pattern.getItem() != null && pattern.getItem() instanceof ICraftingPatternItem) {
                for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                    ItemStack item = player.inventory.mainInventory[i];
                    if (item != null && item.getItem() != null && item.getItem() instanceof ItemPatternModifier) {
                        PatternModifierInventory patternModifierInventory = new PatternModifierInventory(
                            item,
                            i,
                            player);
                        if (patternModifierInventory.injectItems(pattern)) {
                            event.item.setDead();
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
                if (player.inventory.addItemStackToInventory(pattern)) {
                    event.item.setDead();
                    event.setCanceled(true);
                } else if (event.item.isEntityAlive()) {
                    event.item.delayBeforeCanPickup = 20;
                }
            }
        } catch (Exception ignored) {}

    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {

    }

    public void serverStarting(FMLServerStartingEvent event) {

    }

    public void serverStopping(FMLServerStoppingEvent event) {

    }
}
