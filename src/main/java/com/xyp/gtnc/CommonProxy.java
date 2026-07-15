package com.xyp.gtnc;

import net.minecraftforge.common.MinecraftForge;

import com.xyp.gtnc.Common.command.CommandSteamNetwork;
import com.xyp.gtnc.Common.items.toolbelt.common.BeltEvents;
import com.xyp.gtnc.Common.items.toolbelt.common.BeltGuiHandler;
import com.xyp.gtnc.Common.machines.hatch.SuperMTEHatchCraftingInputME;
import com.xyp.gtnc.Common.machines.multiblock.AssemblerMatrix;
import com.xyp.gtnc.Common.packet.NetWorkHandler;
import com.xyp.gtnc.Loader.BlockLoader;
import com.xyp.gtnc.Loader.ItemsLoader;
import com.xyp.gtnc.Loader.MachineLoader;
import com.xyp.gtnc.Loader.RecipeLoader;
import com.xyp.gtnc.utils.event.SteamNetworkEventHandler;

import appeng.api.AEApi;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import gregtech.api.util.FakeCleanroom;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        // 配置已经在静态块中自动加载
        ScienceNotCool.LOG.info("Configuration loaded successfully");
        ScienceNotCool.LOG.info("I am MyMod at version " + Tags.VERSION);

        // 绕过所有配方超净间需求
        FakeCleanroom.CLEANROOM_BYPASS = true;

        // 注册网络通道并注册所有网络包
        // Register network channel and all packets
        ScienceNotCool.channel = NetworkRegistry.INSTANCE.newSimpleChannel(ScienceNotCool.MODID);
        NetWorkHandler.registerAllMessage();

        // 注册物品（包括工具带）
        ItemsLoader.registry();

        // 注册方块
        BlockLoader.registry();

        // 注册工具带GUI处理器
        NetworkRegistry.INSTANCE.registerGuiHandler(ScienceNotCool.instance, new BeltGuiHandler());
        // 通配样板符 GUI 改用 MUI2 PlayerInventoryGuiFactory 打开，不再走 FML IGuiHandler

        // 注册工具带事件处理器
        BeltEvents beltEvents = new BeltEvents();
        MinecraftForge.EVENT_BUS.register(beltEvents);
        FMLCommonHandler.instance()
            .bus()
            .register(beltEvents);

        // 注册无线蒸汽网络事件处理器
        MinecraftForge.EVENT_BUS.register(new SteamNetworkEventHandler());

        // 注册跨维度 ME 网桥频道注册表事件处理器
        MinecraftForge.EVENT_BUS.register(new com.xyp.gtnc.Common.mebridge.MEBridgeEventHandler());

        // 初始化ME无线二合一接口终端（并入本mod的ae2thing移植）
        com.xyp.gtnc.ae2thing.AE2Thing.preInit(event, ScienceNotCool.instance);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        // 注册自定义蜜蜂等位基因（无尽速度 / 不死寿命），供养蜂机写入产出蜂
        com.xyp.gtnc.Common.machines.bee.GTNCBeeAlleles.register();

        com.xyp.gtnc.ae2thing.AE2Thing.init(event);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        // 注册机器
        MachineLoader.registry();

        // 注册AE接口终端
        var interfaceTerminal = AEApi.instance()
            .registries()
            .interfaceTerminal();
        interfaceTerminal.register(SuperMTEHatchCraftingInputME.class);
        interfaceTerminal.register(AssemblerMatrix.class);

        com.xyp.gtnc.ae2thing.AE2Thing.postInit(event);
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSteamNetwork());
        // Generate Miracle Door (Stellar Forge) recipes by scanning live EBF / GTPP ABS recipe maps.
        RecipeLoader.loadRecipesServerStarted();
    }

    // 服务器启动完成后（晚于 VendingMachine 在 serverStarting 里的 loadDatabase）注入本 mod 的无条件交易。
    public void serverStarted(FMLServerStartedEvent event) {
        com.xyp.gtnc.Common.vending.VMTradeRegistry.injectAll();
    }

    public void complete(FMLLoadCompleteEvent event) {
        // 加载配方
        RecipeLoader.loadRecipes();

        com.xyp.gtnc.ae2thing.AE2Thing.onLoadComplete(event);
    }

}
