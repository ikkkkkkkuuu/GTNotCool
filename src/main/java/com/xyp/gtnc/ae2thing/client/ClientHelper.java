package com.xyp.gtnc.ae2thing.client;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientHelper {

    @SubscribeEvent
    public void updateTextureSheet(final TextureStitchEvent.Pre ev) {
        // All block/fluid/item texture stitching here was for features not included in this port
        // (wireless distributor, ex io port, mana fluid, toggleable view cell). The dual interface
        // terminal's item icon is registered via setTextureName, and its GUI textures are bound directly.
    }

    public static void register() {
        ClientHelper handler = new ClientHelper();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }
}
