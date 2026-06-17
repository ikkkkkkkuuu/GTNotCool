package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Client.render.RenderTimeAccelerator;
import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;

import cpw.mods.fml.client.registry.RenderingRegistry;

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
        // 注册时间加速器实体渲染器
        // Register Time Accelerator entity renderer
        RenderingRegistry.registerEntityRenderingHandler(EntityTimeAccelerator.class, new RenderTimeAccelerator());

    }
}
