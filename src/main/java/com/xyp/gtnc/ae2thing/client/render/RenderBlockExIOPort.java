package com.xyp.gtnc.ae2thing.client.render;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.xyp.gtnc.ae2thing.client.textures.BlockTexture;
import com.xyp.gtnc.ae2thing.common.block.BlockExIOPort;
import com.xyp.gtnc.ae2thing.common.tile.TileExIOPort;

import appeng.block.storage.BlockIOPort;
import appeng.client.render.BlockRenderInfo;
import appeng.client.render.blocks.RenderIOPort;

public class RenderBlockExIOPort extends RenderIOPort {

    public RenderBlockExIOPort() {
        super();
    }

    @Override
    public boolean renderInWorld(final BlockIOPort block, final IBlockAccess world, final int x, final int y,
        final int z, final RenderBlocks renderer) {
        if (!(block instanceof BlockExIOPort exBlock)) {
            return super.renderInWorld(block, world, x, y, z, renderer);
        }
        final TileExIOPort ti = exBlock.getTileEntity(world, x, y, z);
        final BlockRenderInfo info = block.getRendererInstance();
        if (ti != null) {
            final IIcon bottom = BlockTexture.ExIOPort_Bottom.getIcon();
            final IIcon side;
            final IIcon top;
            if (ti.isActive()) {
                side = BlockTexture.ExIOPort_Side.getIcon();
                top = BlockTexture.ExIOPort_Top.getIcon();
            } else {
                side = BlockTexture.ExIOPort_Side_Off.getIcon();
                top = BlockTexture.ExIOPort_Top_Off.getIcon();
            }
            info.setTemporaryRenderIcons(top, bottom, side, side, side, side);
        }

        final boolean fz = super.renderInWorld(block, world, x, y, z, renderer);

        info.setTemporaryRenderIcon(null);

        return fz;
    }
}
