package com.xyp.gtnc.Common.items.timeVial;

import static com.xyp.gtnc.Config.Config.*;
import static com.xyp.gtnc.utils.timeVial.InformationHelper.dividingLine;
import static com.xyp.gtnc.utils.timeVial.InformationHelper.holdShiftForDetails;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;
import com.xyp.gtnc.Common.entity.EntityTimeAccelerator;
import com.xyp.gtnc.Common.items.aItemCore.ItemBase;
import com.xyp.gtnc.Config.Config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.entity.EntityImmortalItem;

/**
 * 时间之瓶基类，可以存储时间并加速方块/TileEntity
 */
public class TimeVial extends ItemBase {

    protected static final int TIME_INIT_RATE = enableTimeAcceleratorBoost ? Config.timeVialInitialRate * 2
        : Config.timeVialInitialRate;
    protected static final float[] SOUND_ARRAY_F = new float[] { 0.749154F, 0.793701F, 0.890899F, 1.059463F, 0.943874F,
        0.890899F, 0.690899F };
    protected static final int MAX_ACCELERATION = enableTimeAcceleratorBoost ? Config.timeVialMaxAcceleration * 2
        : Config.timeVialMaxAcceleration;
    protected static final int NUMBER_EER = -846280;
    protected int storedTimeTick = 0;

    protected static final double tHalfSize = 0.01D;
    protected static final String NBT_STORED_TICK = "storedTimeTick";

    // #tr timeVial.name
    // # Time Vial
    // # zh_CN 时间之瓶
    public TimeVial() {
        super("TimeVial");
        setMaxStackSize(1);
        setTextureName("TimeVial/TimeVial");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, @NotNull World world, int x, int y, int z,
        int side, float hitX, float hitY, float hitZ) {

        if (world.isRemote) return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);

        double targetPosX = x + 0.5D;
        double targetPosY = y + 0.5D;
        double targetPosZ = z + 0.5D;

        double minX = targetPosX - tHalfSize;
        double minY = targetPosY - tHalfSize;
        double minZ = targetPosZ - tHalfSize;
        double maxX = targetPosX + tHalfSize;
        double maxY = targetPosY + tHalfSize;
        double maxZ = targetPosZ + tHalfSize;

        Optional<EntityTimeAccelerator> box = world
            .getEntitiesWithinAABB(
                EntityTimeAccelerator.class,
                AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ))
            .stream()
            .findFirst();

        @NotNull
        var eta = box.orElseGet(() -> new EntityTimeAccelerator(world, x, y, z));
        if (box.isPresent()) {
            if (player.isSneaking()) recyclingTime(stack, eta);
            else applyNextAcceleration(stack, eta);
        } else if (consumeTimeData(stack, (int) (TIME_INIT_RATE * 600 * timeVialDiscountValue))) {
            // set the GregTechMachineMode
            if (player.isSneaking() && !disableShiftModification) eta.setDead();
            world.spawnEntityInWorld(eta);
        }
        etaInteract(eta, world, targetPosX, targetPosY, targetPosZ);
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, @NotNull World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        return true;
    }

    protected void applyNextAcceleration(ItemStack stack, EntityTimeAccelerator eta) {
        var currentRate = eta.getTimeRate();
        if (currentRate < MAX_ACCELERATION) {
            var remained = currentRate * eta.getRemainingTime();
            var nextRateTimeRequired = enableResetRemainingTime
                ? (int) ((2 * currentRate * EntityTimeAccelerator.ACCELERATION_TICK - remained) * timeVialDiscountValue)
                : (int) (remained * timeVialDiscountValue);
            if (consumeTimeData(stack, nextRateTimeRequired)) {
                eta.setTimeRate(currentRate * 2);
                if (enableResetRemainingTime) eta.setRemainingTime(EntityTimeAccelerator.ACCELERATION_TICK);
            }
        }
    }

    protected static void recyclingTime(ItemStack stack, EntityTimeAccelerator eta) {
        NBTTagCompound nbtTagCompound = stack.getTagCompound();
        if (nbtTagCompound != null) {
            nbtTagCompound.setInteger(
                NBT_STORED_TICK,
                nbtTagCompound.getInteger(NBT_STORED_TICK) + eta.getTimeRate() * eta.getRemainingTime());
            stack.setTagCompound(nbtTagCompound);
        }
        eta.setDead();
    }

    protected void etaInteract(@NotNull EntityTimeAccelerator eta, World world, double targetPosX, double targetPosY,
        double targetPosZ) {
        int i = (int) (Math.log(eta.getTimeRate()) / Math.log(2)) - (enableTimeAcceleratorBoost ? 6 : 2);
        // security considerations
        if (i < 0 || i >= SOUND_ARRAY_F.length) i = 0;
        world.playSoundEffect(
            targetPosX,
            targetPosY,
            targetPosZ,
            "note.harp",
            defaultTimeVialVolumeValue,
            SOUND_ARRAY_F[i]);
        if (enableLogInfo) {
            var remainingTime = eta.getRemainingTime();
            configLog.info("xxxxx remainingTime: {} xxxxx", remainingTime);
            configLog.info(
                "An entity entityTimeAccelerator has been spawned ({}, {}, {}).",
                targetPosX,
                targetPosY,
                targetPosZ);
        }
    }

    protected boolean consumeTimeData(@NotNull ItemStack stack, int consumedTick) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        int timeTick = tagCompound.getInteger(NBT_STORED_TICK);
        if (timeTick >= consumedTick) {
            tagCompound.setInteger(NBT_STORED_TICK, timeTick - consumedTick);
            return true;
        }
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity playerIn, int slot, boolean isHeld) {
        if (worldIn.isRemote) return;
        if (!(playerIn instanceof EntityPlayer player)) return;
        NBTTagCompound nbtTagCompound = stack.getTagCompound();
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setInteger(NBT_STORED_TICK, storedTimeTick);
        } else if (worldIn.getTotalWorldTime() % 20 == 0) {
            int t = nbtTagCompound.getInteger(NBT_STORED_TICK);
            if (t == NUMBER_EER) return;
            nbtTagCompound.setInteger(NBT_STORED_TICK, t + 20);
        }
        stack.setTagCompound(nbtTagCompound);
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new EntityImmortalItem(world, location, itemstack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final @NotNull ItemStack stack, final EntityPlayer player, final List<String> list,
        final boolean extraInformation) {
        getInfoFromNBT(stack, list);
        if (holdShiftForDetails(list)) {
            list.add(dividingLine);
            // #tr text.TimeVial.details_0
            // # Possesses 6 acceleration gradients.
            // # zh_CN 拥有6个加速梯度
            list.add(I18n.format("text.TimeVial.details_0"));

            // #tr text.TimeVial.details_1
            // # Default adjustable range [8->256].
            // # zh_CN 默认可调整范围[8->256]
            list.add(I18n.format("text.TimeVial.details_1"));

            // #tr text.TimeVial.details_2
            // # Can be modified in the configuration to [16->512].
            // # zh_CN 可在配置修改为[16->512]
            list.add(I18n.format("text.TimeVial.details_2"));
            list.add(dividingLine);

            // #tr text.TimeVial.details_3
            // # Checks every 30 seconds if the player has multiple time vials.
            // # zh_CN 间隔30秒检测一次玩家是否有多个时间瓶
            list.add(I18n.format("text.TimeVial.details_3"));

            // #tr text.TimeVial.details_4
            // # When multiples are present, executes time accumulation, adding the most time to one vial.
            // # zh_CN 当有多个时执行时间累加，添加最多时间的瓶子
            list.add(I18n.format("text.TimeVial.details_4"));

            // #tr text.TimeVial.details_5
            // # While setting the remaining time vials to:
            // # zh_CN 同时把其余时间瓶时间修改为：
            list.add(I18n.format("text.TimeVial.details_5"));

            // #tr text.TimeVial.details_6
            // # Uh...114514. -11 hours -45 minutes -14 seconds.
            // # zh_CN 额..114514. -11小时 -45分钟 -14秒
            list.add(I18n.format("text.TimeVial.details_6"));

            // #tr text.TimeVial.details_7
            // # Can be modified without limits in the config file.
            // # zh_CN 可在配置文件修改无限制
            list.add(I18n.format("text.TimeVial.details_7"));
            list.add(dividingLine);

            // #tr text.TimeVial.details_8
            // # The rules for GT machine acceleration are as follows:
            // # zh_CN GT机器加速的规则如下：
            list.add(I18n.format("text.TimeVial.details_8"));

            // #tr text.TimeVial.details_9
            // # By default, the acceleration method is applied by accumulating ticks if Shift is not held down.
            // # zh_CN 如果不按住shift默认执行加速方法 tick累加
            list.add(I18n.format("text.TimeVial.details_9"));

            // #tr text.TimeVial.details_10
            // # Machines require processing time and time must be >= 2 ticks.
            // # zh_CN 即机器需要加工时间time且 time >= 2tick
            list.add(I18n.format("text.TimeVial.details_10"));

            // #tr text.TimeVial.details_11
            // # Executes time + acceleration multiplier * discount (default 0.8f), which is adjustable.
            // # zh_CN 执行time + 加速倍率 * 折扣(默认0.8f) 可调整
            list.add(I18n.format("text.TimeVial.details_11"));

            // #tr text.TimeVial.details_12
            // # In simple terms, adds the corresponding time to the machine's working time.
            // # zh_CN 简单说就是给机器工作时间加上相应的时间
            list.add(I18n.format("text.TimeVial.details_12"));
            list.add(dividingLine);

            // #tr text.TimeVial.details_13
            // # Configuration file notes
            // # zh_CN 配置文件说明
            list.add(I18n.format("text.TimeVial.details_13"));

            // #tr text.TimeVial.details_14
            // # Allows adjusting the volume of the vial sounds and whether to enable number textures.
            // # zh_CN 允许调整瓶子声音大小，是否启用数字贴图
            list.add(I18n.format("text.TimeVial.details_14"));
            list.add(dividingLine);
        }
    }

    @Desugar
    protected record TimeComponents(int hours, int minutes, int seconds) {}

    @SideOnly(Side.CLIENT)
    protected TimeComponents getStoredTimeComponents(ItemStack stack) {
        NBTTagCompound nbtTagCompound = stack.getTagCompound();
        if (nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();
        int storedTimeSeconds = nbtTagCompound.getInteger(NBT_STORED_TICK) / 20;
        int hours = storedTimeSeconds / 3600;
        int minutes = (storedTimeSeconds % 3600) / 60;
        int seconds = storedTimeSeconds % 60;
        return new TimeComponents(hours, minutes, seconds);
    }

    @SideOnly(Side.CLIENT)
    protected void getInfoFromNBT(@NotNull ItemStack stack, List<String> list) {
        TimeComponents time = getStoredTimeComponents(stack);
        list.add(I18n.format("text.TimeVial.tips", time.hours, time.minutes, time.seconds));
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.format(this.getUnlocalizedNameInefficiently(stack) + ".name") + " "
            + I18n.format(
                "text.TimeVial.tips",
                getStoredTimeComponents(stack).hours,
                getStoredTimeComponents(stack).minutes,
                getStoredTimeComponents(stack).seconds)
            + "§r";
    }
}
