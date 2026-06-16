package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.registry.EntityRegistry;

/**
 * 实体加载器
 * Entity loader for registering all mod entities
 */
public class EntityLoader {

    /**
     * 注册所有实体
     * Register all entities
     */
    public static void registerEntities() {
        // 注册时间加速器实体
        // Register Time Accelerator entity
        EntityRegistry.registerModEntity(
            EntityTimeAccelerator.class,
            "EntityTimeAccelerator",
            0,
            ScienceNotCool.instance,
            64,
            1,
            true);
    }
}
