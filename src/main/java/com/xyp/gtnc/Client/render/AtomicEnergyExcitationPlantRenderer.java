package com.xyp.gtnc.Client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.common.lib.References;
import com.xyp.gtnc.Common.machines.multiblock.AtomicEnergyExcitationPlant;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class AtomicEnergyExcitationPlantRenderer {

    private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(
        References.MODID.toLowerCase(),
        "textures/models/stabilizer_sphere.png");
    private static final IModelCustom STABILIZER_SPHERE_MODEL = AdvancedModelLoader
        .loadModel(new ResourceLocation(References.MODID.toLowerCase(), "models/stabilizer_sphere.obj"));

    private AtomicEnergyExcitationPlantRenderer() {}

    public static void renderTileEntity(AtomicEnergyExcitationPlant machine, double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        ChunkCoordinates pos = machine.getRenderPos();
        GL11.glTranslated(pos.posX, pos.posY, pos.posZ);
        GL11.glTranslated(0.5, 0.5, 0.5);
        GL11.glScalef(4.0F, 4.0F, 4.0F);

        float color = 200 / 255F;
        GL11.glColor4f(color, color, color, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);

        FMLClientHandler.instance()
            .getClient()
            .getTextureManager()
            .bindTexture(MODEL_TEXTURE);

        GL11.glRotatef(machine.rotation, 0F, 1F, 0F);
        STABILIZER_SPHERE_MODEL.renderAll();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GL11.glRotatef(machine.rotation * 2, 0F, -1F, 0F);
        GL11.glDepthMask(false);
        GL11.glColor4f(color, color, color, 0.5F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glScalef(1.3F, 1.3F, 1.3F);
        STABILIZER_SPHERE_MODEL.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
