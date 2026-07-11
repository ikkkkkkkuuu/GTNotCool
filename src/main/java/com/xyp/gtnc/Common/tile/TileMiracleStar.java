package com.xyp.gtnc.Common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

/**
 * Render-only tile placed at the center of a running Miracle Door.
 * <p>
 * Faithful port of TST's {@code TileStar}: it holds a rotation angle (advanced every client tick) and a render size,
 * and reports an infinite render bounding box so the star model stays visible from far away.
 */
public class TileMiracleStar extends TileEntity {

    public double Rotation = 0;
    public double size = 4;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("size", size);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        size = nbt.getDouble("size");
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        Rotation = (Rotation + 1.2) % 360d;
    }
}
