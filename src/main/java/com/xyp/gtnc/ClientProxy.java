package com.xyp.gtnc;

import net.minecraftforge.common.MinecraftForge;

import com.xyp.gtnc.Client.utils.BlockIcons;
import com.xyp.gtnc.Loader.RendererLoader;
import com.xyp.gtnc.utils.event.SubscribeEventClientUtils;
import com.xyp.gtnc.utils.keybind.KeyBindManager;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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
    }

    /**
     * 检查按键是否被按住(委托给 KeyBindManager)
     * Check if key is held down (delegated to KeyBindManager)
     */
    public static boolean isKeyDown(net.minecraft.client.settings.KeyBinding key) {
        return KeyBindManager.isKeyDown(key);
    }
}
