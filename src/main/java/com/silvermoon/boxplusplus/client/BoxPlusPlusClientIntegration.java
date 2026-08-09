package com.silvermoon.boxplusplus.client;

import net.minecraftforge.common.MinecraftForge;

import com.silvermoon.boxplusplus.common.render.RenderBoxRing;
import com.silvermoon.boxplusplus.common.tileentities.TEBoxRing;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public final class BoxPlusPlusClientIntegration {

    private static boolean clientInitDone;

    private BoxPlusPlusClientIntegration() {}

    public static void init(FMLInitializationEvent event) {
        if (clientInitDone) return;
        clientInitDone = true;
        ClientRegistry.bindTileEntitySpecialRenderer(TEBoxRing.class, new RenderBoxRing());
        MinecraftForge.EVENT_BUS.register(BoxNEIHandler.instance);
    }
}
