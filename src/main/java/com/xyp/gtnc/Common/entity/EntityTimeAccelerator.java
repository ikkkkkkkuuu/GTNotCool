package com.xyp.gtnc.Common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.api.ITileEntityTickAcceleration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Server-authoritative, short-lived accelerator placed by the Eternity Vial.
 *
 * <p>
 * The six stages are intentionally fixed. The renderer consumes the stage index directly, so a gameplay multiplier
 * can never drift away from its matching {@code time_0..time_5} texture.
 * </p>
 */
public final class EntityTimeAccelerator extends Entity {

    public static final int STAGE_COUNT = 6;

    private static final int WATCHER_STAGE = 20;
    private static final long TICK_BUDGET_NS = 1_000_000L;

    private int stage;
    private int remainingTicks;
    private int targetX;
    private int targetY;
    private int targetZ;

    public EntityTimeAccelerator(World world) {
        super(world);
        remainingTicks = Config.eternityVialDurationSeconds * 20;
        noClip = true;
        preventEntitySpawning = false;
        setSize(0.02F, 0.02F);
    }

    public EntityTimeAccelerator(World world, int x, int y, int z) {
        this(world);
        targetX = x;
        targetY = y;
        targetZ = z;
        setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
        setStage(0);
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(WATCHER_STAGE, 0);
    }

    public int getStageForRender() {
        return clampStage(dataWatcher.getWatchableObjectInt(WATCHER_STAGE));
    }

    public int getStage() {
        return stage;
    }

    public int getMultiplier() {
        return multiplierForStage(stage);
    }

    public static int multiplierForStage(int stage) {
        return Config.eternityVialInitialMultiplier << clampStage(stage);
    }

    public boolean advanceStage() {
        if (stage >= STAGE_COUNT - 1) return false;
        setStage(stage + 1);
        return true;
    }

    private void setStage(int requestedStage) {
        stage = clampStage(requestedStage);
        dataWatcher.updateObject(WATCHER_STAGE, stage);
    }

    private static int clampStage(int value) {
        return Math.max(0, Math.min(STAGE_COUNT - 1, value));
    }

    @Override
    public void onEntityUpdate() {
        if (worldObj.isRemote) return;

        if (remainingTicks-- > 0) {
            accelerateTarget();
        }
        if (remainingTicks <= 0) {
            setDead();
        }
    }

    private void accelerateTarget() {
        Block block = worldObj.getBlock(targetX, targetY, targetZ);
        TileEntity tile = worldObj.getTileEntity(targetX, targetY, targetZ);
        long deadline = System.nanoTime() + TICK_BUDGET_NS;

        if (block != null && block.getTickRandomly() && worldObj.getTotalWorldTime() % 2L == 0L) {
            accelerateBlock(block, deadline);
        }

        if (tile != null && !tile.isInvalid() && tile.canUpdate()) {
            if (tile instanceof ITileEntityTickAcceleration
                && ((ITileEntityTickAcceleration) tile).tickAcceleration(getMultiplier())) {
                return;
            }
            accelerateTile(tile, deadline);
        }
    }

    private void accelerateTile(TileEntity tile, long deadline) {
        try {
            for (int i = 0; i < getMultiplier() && System.nanoTime() <= deadline; i++) {
                tile.updateEntity();
            }
        } catch (Throwable error) {
            ScienceNotCool.LOG.warn(
                "Eternity Vial failed to accelerate TileEntity at ({}, {}, {})",
                targetX,
                targetY,
                targetZ,
                error);
        }
    }

    private void accelerateBlock(Block block, long deadline) {
        try {
            for (int i = 0; i < getMultiplier() && System.nanoTime() <= deadline; i++) {
                block.updateTick(worldObj, targetX, targetY, targetZ, worldObj.rand);
            }
        } catch (Throwable error) {
            ScienceNotCool.LOG
                .warn("Eternity Vial failed to accelerate block at ({}, {}, {})", targetX, targetY, targetZ, error);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        targetX = tag.getInteger("TargetX");
        targetY = tag.getInteger("TargetY");
        targetZ = tag.getInteger("TargetZ");
        remainingTicks = tag.getInteger("RemainingTicks");
        setStage(tag.getInteger("Stage"));
        setPosition(targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("TargetX", targetX);
        tag.setInteger("TargetY", targetY);
        tag.setInteger("TargetZ", targetZ);
        tag.setInteger("RemainingTicks", remainingTicks);
        tag.setInteger("Stage", stage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 128.0D * 128.0D;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public void moveEntity(double x, double y, double z) {}

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        ySize = 0.0F;
        prevPosX = posX = x;
        prevPosY = posY = y;
        prevPosZ = posZ = z;
        setPosition(x, y, z);
    }

    /**
     * Keeps the client-side entity inside the target block. Vanilla's implementation moves entities out of colliding
     * blocks, which would displace the six face rings away from the right-clicked position.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        yOffset = 0.0F;
        prevPosX = posX = x;
        prevPosY = posY = y;
        prevPosZ = posZ = z;
        setPosition(x, y, z);
    }

    @Override
    protected void updateFallState(double distanceFallenThisTick, boolean isOnGround) {}

    @Override
    public void mountEntity(Entity entity) {}

    @Override
    public void moveFlying(float strafe, float forward, float friction) {}

    @Override
    public void applyEntityCollision(Entity entity) {}

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return true;
    }
}
