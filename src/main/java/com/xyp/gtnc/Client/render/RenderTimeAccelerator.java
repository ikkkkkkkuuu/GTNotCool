package com.xyp.gtnc.Client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;
import com.xyp.gtnc.ScienceNotCool;

/** Renders the accelerator as one animated ring on every face of its target block. */
public final class RenderTimeAccelerator extends Render {

    private static final ResourceLocation[] STAGE_TEXTURES = new ResourceLocation[EntityTimeAccelerator.STAGE_COUNT];
    private static final double ROTATION_SPEED = 7.12D;
    private static final double RADIUS = 0.34D;
    private static final double OFFSET = 0.51D;

    static {
        for (int stage = 0; stage < STAGE_TEXTURES.length; stage++) {
            STAGE_TEXTURES[stage] = new ResourceLocation(
                ScienceNotCool.MODID,
                "textures/entity/Circle/time_" + stage + ".png");
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityTimeAccelerator accelerator = (EntityTimeAccelerator) entity;
        bindTexture(STAGE_TEXTURES[accelerator.getStageForRender()]);

        double angle = ROTATION_SPEED * (accelerator.worldObj.getTotalWorldTime() + partialTicks) % 360.0D;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(1.0D);

        Tessellator tessellator = Tessellator.instance;
        drawHorizontal(tessellator, x, y + OFFSET, z, -angle, false);
        drawHorizontal(tessellator, x, y - OFFSET, z, angle, true);
        drawXFace(tessellator, x + OFFSET, y, z, -angle, false);
        drawXFace(tessellator, x - OFFSET, y, z, angle, true);
        drawZFace(tessellator, x, y, z + OFFSET, -angle, false);
        drawZFace(tessellator, x, y, z - OFFSET, angle, true);

        Minecraft.getMinecraft().entityRenderer.enableLightmap(1.0D);
        GL11.glPopAttrib();
    }

    private static void drawHorizontal(Tessellator tessellator, double x, double y, double z, double angle,
        boolean reverse) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotated(angle, 0.0D, 1.0D, 0.0D);
        tessellator.startDrawingQuads();
        if (reverse) {
            tessellator.addVertexWithUV(RADIUS, 0.0D, -RADIUS, 0.0D, 0.0D);
            tessellator.addVertexWithUV(-RADIUS, 0.0D, -RADIUS, 1.0D, 0.0D);
            tessellator.addVertexWithUV(-RADIUS, 0.0D, RADIUS, 1.0D, 1.0D);
            tessellator.addVertexWithUV(RADIUS, 0.0D, RADIUS, 0.0D, 1.0D);
        } else {
            tessellator.addVertexWithUV(RADIUS, 0.0D, RADIUS, 0.0D, 0.0D);
            tessellator.addVertexWithUV(-RADIUS, 0.0D, RADIUS, 1.0D, 0.0D);
            tessellator.addVertexWithUV(-RADIUS, 0.0D, -RADIUS, 1.0D, 1.0D);
            tessellator.addVertexWithUV(RADIUS, 0.0D, -RADIUS, 0.0D, 1.0D);
        }
        tessellator.draw();
        GL11.glPopMatrix();
    }

    private static void drawXFace(Tessellator tessellator, double x, double y, double z, double angle,
        boolean reverse) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotated(angle, 1.0D, 0.0D, 0.0D);
        tessellator.startDrawingQuads();
        if (reverse) {
            tessellator.addVertexWithUV(0.0D, RADIUS, -RADIUS, 0.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, RADIUS, RADIUS, 1.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, -RADIUS, RADIUS, 1.0D, 1.0D);
            tessellator.addVertexWithUV(0.0D, -RADIUS, -RADIUS, 0.0D, 1.0D);
        } else {
            tessellator.addVertexWithUV(0.0D, RADIUS, RADIUS, 0.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, RADIUS, -RADIUS, 1.0D, 0.0D);
            tessellator.addVertexWithUV(0.0D, -RADIUS, -RADIUS, 1.0D, 1.0D);
            tessellator.addVertexWithUV(0.0D, -RADIUS, RADIUS, 0.0D, 1.0D);
        }
        tessellator.draw();
        GL11.glPopMatrix();
    }

    private static void drawZFace(Tessellator tessellator, double x, double y, double z, double angle,
        boolean reverse) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotated(angle, 0.0D, 0.0D, 1.0D);
        tessellator.startDrawingQuads();
        if (reverse) {
            tessellator.addVertexWithUV(RADIUS, RADIUS, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV(-RADIUS, RADIUS, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(-RADIUS, -RADIUS, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(RADIUS, -RADIUS, 0.0D, 0.0D, 1.0D);
        } else {
            tessellator.addVertexWithUV(-RADIUS, RADIUS, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV(RADIUS, RADIUS, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(RADIUS, -RADIUS, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(-RADIUS, -RADIUS, 0.0D, 0.0D, 1.0D);
        }
        tessellator.draw();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return STAGE_TEXTURES[((EntityTimeAccelerator) entity).getStageForRender()];
    }
}
