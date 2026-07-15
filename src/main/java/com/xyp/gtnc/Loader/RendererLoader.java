package com.xyp.gtnc.Loader;

import net.minecraftforge.client.MinecraftForgeClient;

import com.xyp.gtnc.Client.render.MiracleStarRender;

import fox.spiteful.avaritia.render.CosmicItemRenderer;

/**
 * 客户端渲染器加载器
 * Client-side renderer loader for registering all mod renderers
 */
public class RendererLoader {

    /**
     * 注册所有客户端渲染器
     * Register all client-side renderers
     */
    public static void registerRenderers() {
        // 注册奇迹之门中心星体渲染器 (TESR)
        // Register Miracle Door center star renderer
        new MiracleStarRender();

        // 无尽框架：复用 Avaritia 的宇宙彩虹渲染器(CosmicItemRenderer + 已编译 cosmicShader)。
        // Avaritia 是硬依赖，无需移植着色器管线。
        MinecraftForgeClient.registerItemRenderer(ItemsLoader.endlessFrame, new CosmicItemRenderer());
    }
}
