package com.xyp.gtnc.Common.items.toolbelt;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Common.items.toolbelt.slot.BeltAttachment;

public class BeltFinderBeltSlot extends BeltFinder {

    @Override
    public String getName() {
        return "belt_slot";
    }

    @Override
    public BeltGetter findStack(EntityLivingBase player, boolean allowCosmetic) {
        if (!ConfigData.customBeltSlotEnabled) return null;

        final BeltAttachment attachment = BeltAttachment.get(player);
        if (attachment == null) return null;

        final ItemStack stack = attachment.getContents();
        if (stack == null) return null;

        return new BeltGetter() {

            @Override
            public ItemStack getBelt() {
                return attachment.getContents();
            }

            @Override
            public void syncToClients() {
                attachment.syncToTracking();
            }
        };
    }
}
