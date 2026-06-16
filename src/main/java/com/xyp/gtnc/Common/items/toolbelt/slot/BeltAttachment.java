package com.xyp.gtnc.Common.items.toolbelt.slot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.packet.SyncBeltSlotContents;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * Belt attachment using IExtendedEntityProperties.
 * Stores one item in the "belt slot" on the player.
 */
public class BeltAttachment implements IExtendedEntityProperties {

    public static final String PROP_NAME = "ToolBeltAttachment";

    private final EntityLivingBase owner;
    private ItemStack contents;

    public BeltAttachment(EntityLivingBase owner) {
        this.owner = owner;
        this.contents = null;
    }

    public static BeltAttachment get(EntityLivingBase entity) {
        if (entity == null) return null;
        return (BeltAttachment) entity.getExtendedProperties(PROP_NAME);
    }

    public static void register(EntityLivingBase entity) {
        entity.registerExtendedProperties(PROP_NAME, new BeltAttachment(entity));
    }

    public EntityLivingBase getOwner() {
        return owner;
    }

    public ItemStack getContents() {
        return contents;
    }

    public void setContents(ItemStack stack) {
        ItemStack oldStack = this.contents;
        if (oldStack == stack) return;

        if (oldStack != null) {
            notifyUnequip(oldStack);
        }
        this.contents = stack;
        if (stack != null) {
            notifyEquip(stack);
        }
        onContentsChanged();
    }

    private void notifyEquip(ItemStack stack) {
        if (stack.getItem() instanceof IBeltSlotItem) {
            ((IBeltSlotItem) stack.getItem()).onEquipped(stack, this);
        }
    }

    private void notifyUnequip(ItemStack stack) {
        if (stack.getItem() instanceof IBeltSlotItem) {
            ((IBeltSlotItem) stack.getItem()).onUnequipped(stack, this);
        }
    }

    public void onWornTick() {
        ItemStack stack = getContents();
        if (stack != null && stack.getItem() instanceof IBeltSlotItem) {
            ((IBeltSlotItem) stack.getItem()).onWornTick(stack, this);
        }
    }

    public boolean canEquip(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (stack.getItem() instanceof IBeltSlotItem) {
            return ((IBeltSlotItem) stack.getItem()).canEquip(stack, this);
        }
        return false;
    }

    public boolean canUnequip() {
        ItemStack stack = getContents();
        if (stack == null || stack.getItem() == null) return false;
        if (stack.getItem() instanceof IBeltSlotItem) {
            return ((IBeltSlotItem) stack.getItem()).canUnequip(stack, this);
        }
        return false;
    }

    public void onContentsChanged() {
        if (!ConfigData.customBeltSlotEnabled) return;
        if (!owner.worldObj.isRemote) {
            syncToTracking();
        }
    }

    public void syncToTracking() {
        if (!(owner instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) owner;
        NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(
            owner.dimension,
            owner.posX,
            owner.posY,
            owner.posZ,
            64.0);
        ScienceNotCool.channel.sendToAllAround(new SyncBeltSlotContents(owner, this), point);
    }

    public void syncToSelf() {
        if (!(owner instanceof EntityPlayerMP)) return;
        ScienceNotCool.channel.sendTo(new SyncBeltSlotContents(owner, this), (EntityPlayerMP) owner);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound data = new NBTTagCompound();
        if (contents != null) {
            contents.writeToNBT(data);
        }
        compound.setTag(PROP_NAME, data);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound data = compound.getCompoundTag(PROP_NAME);
        if (data.hasNoTags()) {
            contents = null;
        } else {
            contents = ItemStack.loadItemStackFromNBT(data);
        }
    }

    @Override
    public void init(Entity entity, World world) {}
}
