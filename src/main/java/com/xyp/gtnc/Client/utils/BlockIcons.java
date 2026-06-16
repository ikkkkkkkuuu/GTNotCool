package com.xyp.gtnc.Client.utils;

import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IIconContainer;

public enum BlockIcons implements IIconContainer, Runnable {

    OVERLAY_FRONT_SINGULARITY_DATA_HUB,
    OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE,
    OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE_GLOW,
    OVERLAY_FRONT_ITEMVAULTPORTHATCH,
    OVERLAY_FRONT_ORE_PROCESSOR,
    OVERLAY_FRONT_ORE_PROCESSOR_ACTIVE;

    public static final String RES_PATH = RESOURCE_ROOT_ID + ":";
    private IIcon mIcon;

    BlockIcons() {
        GregTechAPI.sGTBlockIconload.add(this);
    }

    @Override
    public IIcon getIcon() {
        return mIcon;
    }

    @Override
    public IIcon getOverlayIcon() {
        return null;
    }

    @Override
    public ResourceLocation getTextureFile() {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public void run() {
        mIcon = GregTechAPI.sBlockIcons.registerIcon(RES_PATH + "iconsets/" + this);
    }
}
