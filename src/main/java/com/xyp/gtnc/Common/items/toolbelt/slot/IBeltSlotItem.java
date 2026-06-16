package com.xyp.gtnc.Common.items.toolbelt.slot;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

/**
 * Exposed as an interface by items that want to be accepted in the belt slot,
 * and optionally to provide custom processing for insertion, ticking, etc.
 */
public interface IBeltSlotItem {

    /**
     * Runs once per tick for as long as the item remains equipped in the given slot.
     */
    void onWornTick(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot);

    /**
     * Called when the item is equipped to the belt slot.
     */
    void onEquipped(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot);

    /**
     * Called when the item is removed from the belt slot.
     */
    void onUnequipped(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot);

    /**
     * Queries whether or not the stack can be placed in the slot.
     */
    boolean canEquip(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot);

    /**
     * Queries whether or not the stack can be removed from the slot.
     */
    boolean canUnequip(@Nonnull ItemStack stack, @Nonnull BeltAttachment slot);
}
