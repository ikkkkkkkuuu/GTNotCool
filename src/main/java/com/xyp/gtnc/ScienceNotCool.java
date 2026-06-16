package com.xyp.gtnc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xyp.gtnc.utils.enums.ModList;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(
    modid = ScienceNotCool.MODID,
    version = Tags.VERSION,
    name = ScienceNotCool.MODNAME,
    dependencies = "after:AWWayofTime;" + "required-after:Avaritia;"
        + "after:BloodArsenal;"
        + "required-after:Botania;"
        + "required-after:bartworks;"
        + "after:eternalsingularity;"
        + "after:etfuturum;"
        + "after:GalacticraftCore;"
        + "after:GalacticraftMars;"
        + "after:GalacticraftPlanets;"
        + "required-after:gtnhintergalactic;"
        + "required-after:gregtech;"
        + "required-after:galacticgreg;"
        + "required-after:IC2;"
        + "required-after:modularui;"
        + "after:miscutils;"
        + "before:neicustomdiagram;"
        + "after:dreamcraft;"
        + "required-after:structurelib;"
        + "required-after:Thaumcraft;",
    acceptedMinecraftVersions = "1.7.10")
public class ScienceNotCool {

    @Mod.Instance(ModList.ModIds.SCIENCE_NOT_COOL)
    public static ScienceNotCool instance;
    public static final String MODID = ModList.ModIds.SCIENCE_NOT_COOL;
    public static final String MODNAME = ModList.Names.SCIENCE_NOT_COOL;
    public static final Logger LOG = LogManager.getLogger(MODID);
    public static final String RESOURCE_ROOT_ID = ModList.ModIds.SCIENCE_NOT_COOL;
    @SidedProxy(clientSide = "com.xyp.gtnc.ClientProxy", serverSide = "com.xyp.gtnc.CommonProxy")
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper channel;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void completeInit(FMLLoadCompleteEvent event) {
        proxy.complete(event);
    }
}
