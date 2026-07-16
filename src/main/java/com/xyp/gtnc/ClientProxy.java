package com.xyp.gtnc;

import net.minecraftforge.common.MinecraftForge;

import com.xyp.gtnc.Client.utils.BlockIcons;
import com.xyp.gtnc.Loader.RecipeLoader;
import com.xyp.gtnc.Loader.RendererLoader;
import com.xyp.gtnc.utils.event.SubscribeEventClientUtils;
import com.xyp.gtnc.utils.keybind.KeyBindManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // 注册所有按键绑定
        KeyBindManager.registerAllKeyBinds();

        // 注册客户端事件处理器
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        MinecraftForge.EVENT_BUS.register(this);

        // 注册所有客户端事件监听器
        SubscribeEventClientUtils clientUtils = new SubscribeEventClientUtils();
        MinecraftForge.EVENT_BUS.register(clientUtils);
        FMLCommonHandler.instance()
            .bus()
            .register(clientUtils);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Force load BlockIcons to register textures
        BlockIcons.values();

        // 注册所有客户端渲染器（实体已在CommonProxy中注册）
        RendererLoader.registerRenderers();

        // 建筑生成器 GUI 打开时的世界内线框预览渲染器
        MinecraftForge.EVENT_BUS.register(new com.xyp.gtnc.Common.building.client.BuildingBoundsRenderer());
    }

    @Override
    public void complete(FMLLoadCompleteEvent event) {
        // super.complete 会先跑 RecipeLoader.loadRecipes()（内含 StellarForgeRecipePool.loadRecipes()→initData()，
        // 建好 IngotHots / MoltenToIngot 等映射），此时扫描所需的高炉/合金高炉配方也已注册完毕。
        super.complete(event);

        // 奇迹之门（恒星锻炉）配方是全 mod 唯一在 FMLServerStartingEvent 动态生成的，该事件只在（独立）服务端 JVM 触发。
        // 连接独立服务端的客户端从不触发它，导致客户端 NEI 看不到配方（只剩多方块预览）。这里在每个 JVM 都触发的
        // loadComplete 里为客户端补生成一次；loadRecipesServerStarted 内部有幂等守卫，单人环境不会重复添加。
        RecipeLoader.loadRecipesServerStarted();
    }

    /**
     * 检查按键是否被按住(委托给 KeyBindManager)
     * Check if key is held down (delegated to KeyBindManager)
     */
    public static boolean isKeyDown(net.minecraft.client.settings.KeyBinding key) {
        return KeyBindManager.isKeyDown(key);
    }
}
