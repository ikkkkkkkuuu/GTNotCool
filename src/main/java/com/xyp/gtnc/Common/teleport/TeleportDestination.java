package com.xyp.gtnc.Common.teleport;

import net.minecraft.nbt.NBTTagCompound;

/** A personal teleport destination shared by the server store and the client terminal. */
public final class TeleportDestination {

    private static final String KEY_NAME = "Name";
    private static final String KEY_DIMENSION = "Dimension";
    private static final String KEY_X = "X";
    private static final String KEY_Y = "Y";
    private static final String KEY_Z = "Z";
    private static final String KEY_LOCKED = "Locked";

    public final String name;
    public final int dimension;
    public final int x;
    public final int y;
    public final int z;
    public final boolean locked;

    public TeleportDestination(String name, int dimension, int x, int y, int z) {
        this(name, dimension, x, y, z, false);
    }

    public TeleportDestination(String name, int dimension, int x, int y, int z, boolean locked) {
        this.name = name;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.locked = locked;
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(KEY_NAME, name);
        tag.setInteger(KEY_DIMENSION, dimension);
        tag.setInteger(KEY_X, x);
        tag.setInteger(KEY_Y, y);
        tag.setInteger(KEY_Z, z);
        tag.setBoolean(KEY_LOCKED, locked);
        return tag;
    }

    public static TeleportDestination readFromNbt(NBTTagCompound tag) {
        return new TeleportDestination(
            tag.getString(KEY_NAME),
            tag.getInteger(KEY_DIMENSION),
            tag.getInteger(KEY_X),
            tag.getInteger(KEY_Y),
            tag.getInteger(KEY_Z),
            tag.getBoolean(KEY_LOCKED));
    }

    public TeleportDestination withName(String newName) {
        return new TeleportDestination(newName, dimension, x, y, z, locked);
    }

    public TeleportDestination withLocked(boolean newLocked) {
        return new TeleportDestination(name, dimension, x, y, z, newLocked);
    }
}
