package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;
import com.xyp.gtnc.Common.items.toolbelt.slot.BeltAttachment;
import com.xyp.gtnc.Loader.ItemsLoader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class BeltEvents {

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityLivingBase) {
            BeltAttachment.register((EntityLivingBase) event.entity);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!ConfigData.customBeltSlotEnabled) return;
        EntityPlayer player = event.player;
        if (player.worldObj.isRemote) return;
        BeltAttachment attachment = BeltAttachment.get(player);
        if (attachment != null) {
            attachment.syncToSelf();
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!ConfigData.customBeltSlotEnabled) return;
        EntityPlayer player = event.player;
        if (player.worldObj.isRemote) return;
        BeltAttachment attachment = BeltAttachment.get(player);
        if (attachment != null) {
            attachment.syncToSelf();
        }
    }

    @SubscribeEvent
    public void onStartTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!ConfigData.customBeltSlotEnabled) return;
        Entity target = event.target;
        if (target instanceof EntityPlayer) {
            BeltAttachment attachment = BeltAttachment.get((EntityPlayer) target);
            if (attachment != null) {
                attachment.syncToSelf();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        BeltAttachment attachment = BeltAttachment.get(player);
        if (attachment != null) {
            if (ConfigData.customBeltSlotEnabled) {
                attachment.onWornTick();
            } else {
                dropContents(attachment);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!ConfigData.customBeltSlotEnabled) return;
        EntityLivingBase entity = event.entityLiving;

        BeltAttachment attachment = BeltAttachment.get(entity);
        if (attachment == null) return;

        ItemStack stack = attachment.getContents();
        if (stack == null || stack.stackSize <= 0) return;

        // Check for curse of vanishing
        if (stack.isItemEnchanted()) {
            // In 1.7.10, we just check if there's an enchant
            // The enchantment curse of vanishing may not exist in vanilla 1.7.10
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.worldObj.getGameRules()
                .getGameRuleBooleanValue("keepInventory") && !player.capabilities.isCreativeMode) {
                // Drop the belt slot item
                event.drops.add(new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, stack.copy()));
                attachment.setContents(null);
            }
        } else {
            event.drops.add(new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, stack.copy()));
            attachment.setContents(null);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if (!ConfigData.customBeltSlotEnabled) return;
        EntityPlayer oldPlayer = event.original;
        EntityPlayer newPlayer = event.entityPlayer;

        BeltAttachment oldAttachment = BeltAttachment.get(oldPlayer);
        BeltAttachment newAttachment = BeltAttachment.get(newPlayer);

        if (oldAttachment != null && newAttachment != null) {
            ItemStack stack = oldAttachment.getContents();
            newAttachment.setContents(stack != null ? stack.copy() : null);
        }
    }

    /**
     * Anvil upgrade: combine a Tool Belt with a Pouch to increase the belt's slot capacity.
     * Max 9 slots. Preserves stored items.
     * Cost is set to 1 because vanilla ContainerRepair.canTakeStack() requires maximumCost > 0.
     * The 1 XP level is refunded in onAnvilRepair to achieve zero net cost.
     */
    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!ConfigData.enableAnvilUpgrading) return; // 检查配置是否启用
        ItemStack left = event.left;
        ItemStack right = event.right;

        if (left == null || right == null) return;
        if (!(left.getItem() instanceof ToolBeltItem)) return;
        if (right.getItem() != ItemsLoader.pouch) return;

        int currentSize = ToolBeltItem.getBeltSize(left);
        if (currentSize >= 9) return; // Already at max capacity

        // Create upgraded belt: copy input belt, increment size, preserve items
        ItemStack result = left.copy();
        ToolBeltItem.setBeltSize(result, currentSize + 1);

        event.output = result;
        // Vanilla requires maximumCost > 0 to allow taking the output (canTakeStack check).
        // The 1 XP level will be refunded in onAnvilRepair.
        event.cost = 1;
        event.materialCost = 1; // Consume exactly 1 pouch
    }

    /**
     * Refund the XP cost for belt+pouch anvil upgrades, making them effectively free.
     * This fires after the XP is deducted, so we add it back.
     */
    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event) {
        if (!ConfigData.enableAnvilUpgrading) return; // 检查配置是否启用
        ItemStack left = event.left;
        ItemStack right = event.right;

        if (left == null || right == null) return;
        if (!(left.getItem() instanceof ToolBeltItem)) return;
        if (right.getItem() != ItemsLoader.pouch) return;

        // Refund the 1 XP level that was deducted for this upgrade
        event.entityPlayer.addExperienceLevel(1);
    }

    private void dropContents(BeltAttachment attachment) {
        ItemStack stack = attachment.getContents();
        if (stack == null || stack.stackSize <= 0) return;

        EntityLivingBase owner = attachment.getOwner();
        if (owner instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) owner;
            if (!player.inventory.addItemStackToInventory(stack)) {
                player.dropPlayerItemWithRandomChoice(stack, false);
            }
        } else if (!owner.worldObj.isRemote) {
            owner.entityDropItem(stack, 0.1f);
        }
        attachment.setContents(null);
    }
}
