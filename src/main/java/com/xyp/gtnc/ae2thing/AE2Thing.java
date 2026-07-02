package com.xyp.gtnc.ae2thing;

import net.minecraft.util.ResourceLocation;

import com.xyp.gtnc.Tags;
import com.xyp.gtnc.ae2thing.common.Config;
import com.xyp.gtnc.ae2thing.inventory.InventoryHandler;
import com.xyp.gtnc.ae2thing.loader.ChannelLoader;
import com.xyp.gtnc.ae2thing.loader.ItemAndBlockHolder;
import com.xyp.gtnc.ae2thing.loader.RecipeLoader;
import com.xyp.gtnc.ae2thing.proxy.ClientProxy;
import com.xyp.gtnc.ae2thing.proxy.CommonProxy;
import com.xyp.gtnc.utils.enums.ModList;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;

/**
 * Formerly a standalone {@code @Mod}; now a plain facade merged into the host mod (ScienceNotCool). The dual interface
 * terminal registers through the host mod's lifecycle — see {@code com.xyp.gtnc.CommonProxy}. {@link #MODID} points at
 * the host mod id so all resource domains ({@code assets/<MODID>/...}) and the GUI/network handlers route through it.
 */
public class AE2Thing {

    public static final String MODID = ModList.ModIds.SCIENCE_NOT_COOL;
    public static final String NAME = ModList.Names.SCIENCE_NOT_COOL;
    public static final String VERSION = Tags.VERSION;

    /** The host mod instance; used for {@code player.openGui}/{@code registerGuiHandler}. */
    public static Object INSTANCE;

    public static CommonProxy proxy;

    /** Called by the host mod's preInit. {@code hostInstance} is the host {@code @Mod} singleton. */
    public static void preInit(FMLPreInitializationEvent event, Object hostInstance) {
        INSTANCE = hostInstance;
        proxy = FMLLaunchHandler.side() == Side.CLIENT ? new ClientProxy() : new CommonProxy();
        Config.run();
        ChannelLoader.INSTANCE.run();
        proxy.preInit(event);
        ItemAndBlockHolder.INSTANCE.run();
    }

    public static void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        proxy.onLoadComplete(event);
    }

    public static void postInit(FMLPostInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new InventoryHandler());
        RecipeLoader.INSTANCE.run();
        proxy.postInit(event);
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
