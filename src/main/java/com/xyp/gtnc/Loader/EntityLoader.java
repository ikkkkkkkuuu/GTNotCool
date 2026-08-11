package com.xyp.gtnc.Loader;

import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.registry.EntityRegistry;

public final class EntityLoader {

    private EntityLoader() {}

    public static void registry() {
        EntityRegistry.registerModEntity(
            EntityTimeAccelerator.class,
            "EternityVialAccelerator",
            0,
            ScienceNotCool.instance,
            64,
            10,
            false);
    }
}
