package com.xyp.gtnc.Client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.xyp.gtnc.Common.tile.TileMiracleStar;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * TileEntitySpecialRenderer that draws the rotating, glowing star at the center of a running Miracle Door. Ported from
 * TST's {@code ArtificialStarRender}; bound to {@link TileMiracleStar}.
 */
@SideOnly(Side.CLIENT)
public class MiracleStarRender extends TileEntitySpecialRenderer {

    private static final ResourceLocation STAR_TEXTURE = new ResourceLocation("sciencenotcool:model/MiracleStar.png");
    private static final IModelCustom STAR = AdvancedModelLoader
        .loadModel(new ResourceLocation("sciencenotcool:model/MiracleStar.obj"));

    public MiracleStarRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileMiracleStar.class, this);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (!(tile instanceof TileMiracleStar star)) return;
        final double size = star.size;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glRotated(star.Rotation, 1, 1, 1);
        renderStar(size);
        GL11.glPopMatrix();
    }

    private void renderStar(double size) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.bindTexture(STAR_TEXTURE);
        GL11.glScaled(size, size, size);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        STAR.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
