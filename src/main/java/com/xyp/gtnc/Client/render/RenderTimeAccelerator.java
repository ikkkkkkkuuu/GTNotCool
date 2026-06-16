package com.xyp.gtnc.Client.render;

import static com.xyp.gtnc.Config.Config.enableTimeAcceleratorBoost;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;

/**
 * 时间加速器实体渲染器
 */
public class RenderTimeAccelerator extends Render {

    private static final ResourceLocation[] TEXTURE_ARRAY = new ResourceLocation[6];
    private static final double ROTATE_SPEED = 7.12d;
    private static final double RADIUS = 0.34d;
    private static final double OFFSET = 0.51d;

    static {
        final String pathURL = "sciencenotcool:textures/entity/";
        for (int i = 0; i < TEXTURE_ARRAY.length; i++) {
            TEXTURE_ARRAY[i] = new ResourceLocation(String.format(pathURL + "Circle/time_%d.png", i));
        }
    }

    private void doRender(@NotNull EntityTimeAccelerator entityTimeAccelerator, double x, double y, double z) {
        double angle = (ROTATE_SPEED * entityTimeAccelerator.worldObj.getTotalWorldTime()) % 360;
        int timeRate = entityTimeAccelerator.getTimeRateForRender();

        // 防止timeRate为0或负数导致Math.log计算错误
        if (timeRate <= 0) {
            timeRate = enableTimeAcceleratorBoost ? 8 : 4; // 使用默认值
        }

        int i = (int) (Math.log(timeRate) / Math.log(2)) - (enableTimeAcceleratorBoost ? 6 : 2);

        if (i >= TEXTURE_ARRAY.length || i < 0) i = 0;

        this.bindTexture(TEXTURE_ARRAY[i]);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        Tessellator tessellator = Tessellator.instance;

        drawAllSide(tessellator, x, y, z, angle);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private static void drawAllSide(@NotNull Tessellator tessellator, double x, double y, double z, double angle) {
        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        GL11.glTranslated(x, y + OFFSET, z);
        GL11.glRotated(angle, 0.0d, -1.0d, 0.0d);
        tessellator.addVertexWithUV(RADIUS, 0, RADIUS, 0.0d, 0.0d);
        tessellator.addVertexWithUV(-RADIUS, 0, RADIUS, 1.0d, 0.0d);
        tessellator.addVertexWithUV(-RADIUS, 0, -RADIUS, 1.0d, 1.0d);
        tessellator.addVertexWithUV(RADIUS, 0, -RADIUS, 0.0d, 1.0d);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        GL11.glTranslated(x, y - OFFSET, z);
        GL11.glRotated(angle, 0.0d, 1.0d, 0.0d);
        tessellator.addVertexWithUV(RADIUS, 0, -RADIUS, 0.0d, 0.0d);
        tessellator.addVertexWithUV(-RADIUS, 0, -RADIUS, 1.0d, 0.0d);
        tessellator.addVertexWithUV(-RADIUS, 0, RADIUS, 1.0d, 1.0d);
        tessellator.addVertexWithUV(RADIUS, 0, RADIUS, 0.0d, 1.0d);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        GL11.glTranslated(x + OFFSET, y, z);
        GL11.glRotated(angle, -1.0d, 0.0d, 0.0d);
        tessellator.addVertexWithUV(0, RADIUS, RADIUS, 0.0d, 0.0d);
        tessellator.addVertexWithUV(0, RADIUS, -RADIUS, 1.0d, 0.0d);
        tessellator.addVertexWithUV(0, -RADIUS, -RADIUS, 1.0d, 1.0d);
        tessellator.addVertexWithUV(0, -RADIUS, RADIUS, 0.0d, 1.0d);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        GL11.glTranslated(x - OFFSET, y, z);
        GL11.glRotated(angle, 1.0d, 0.0d, 0.0d);
        tessellator.addVertexWithUV(0, RADIUS, -RADIUS, 0.0d, 0.0d);
        tessellator.addVertexWithUV(0, RADIUS, RADIUS, 1.0d, 0.0d);
        tessellator.addVertexWithUV(0, -RADIUS, RADIUS, 1.0d, 1.0d);
        tessellator.addVertexWithUV(0, -RADIUS, -RADIUS, 0.0d, 1.0d);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        GL11.glTranslated(x, y, z + OFFSET);
        GL11.glRotated(angle, 0.0d, 0.0d, -1.0d);
        tessellator.addVertexWithUV(-RADIUS, RADIUS, 0, 0.0d, 0.0d);
        tessellator.addVertexWithUV(RADIUS, RADIUS, 0, 1.0d, 0.0d);
        tessellator.addVertexWithUV(RADIUS, -RADIUS, 0, 1.0d, 1.0d);
        tessellator.addVertexWithUV(-RADIUS, -RADIUS, 0, 0.0d, 1.0d);
        tessellator.draw();
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        GL11.glTranslated(x, y, z - OFFSET);
        GL11.glRotated(angle, 0.0d, 0.0d, 1.0d);
        tessellator.addVertexWithUV(RADIUS, RADIUS, 0, 0.0d, 0.0d);
        tessellator.addVertexWithUV(-RADIUS, RADIUS, 0, 1.0d, 0.0d);
        tessellator.addVertexWithUV(-RADIUS, -RADIUS, 0, 1.0d, 1.0d);
        tessellator.addVertexWithUV(RADIUS, -RADIUS, 0, 0.0d, 1.0d);
        tessellator.draw();
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        doRender((EntityTimeAccelerator) entity, x, y, z);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return null;
    }
}
