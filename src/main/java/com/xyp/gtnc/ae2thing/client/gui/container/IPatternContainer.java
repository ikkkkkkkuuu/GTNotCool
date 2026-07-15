package com.xyp.gtnc.ae2thing.client.gui.container;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.xyp.gtnc.Common.compat.FluidDropCompat;
import com.xyp.gtnc.ae2thing.inventory.IPatternTerminal;

import appeng.container.slot.SlotFake;

public interface IPatternContainer {

    boolean isPatternTerminal();

    boolean hasRefillerUpgrade();

    void refillBlankPatterns(Slot slot);

    void encode();

    void encodeAndMoveToInventory();

    void encodeAllItemAndMoveToInventory();

    IPatternTerminal getPatternTerminal();

    void clear();

    void doubleStacks(int value);

    default int getStackSize(ItemStack stack) {
        // [液滴分类] 可迁原生：仅读液滴在样板编辑槽的数量供加减倍操作,属样板内容编辑不参与合成计算
        if (FluidDropCompat.isFluidDrop(stack.getItem())) {
            return stack.stackSize;
        } else if (stack.getItem() instanceof ItemFluidPacket) {
            return (int) ItemFluidPacket.getFluidAmount(stack);
        } else {
            return stack.stackSize;
        }
    }

    default boolean canDouble(SlotFake[] slots, int mult) {
        if (mult == 0) return false;
        for (Slot s : slots) {
            ItemStack st = s.getStack();
            if (st != null) {
                long result;
                if (mult < 0) {
                    result = (long) getStackSize(st) / Math.abs(mult);
                } else {
                    result = (long) getStackSize(st) * mult;
                }
                if (result > Integer.MAX_VALUE || result <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    default void doubleStacksInternal(SlotFake[] slots, int mult) {
        if (mult == 0) return;
        for (final SlotFake s : slots) {
            if (!s.isEnabled()) continue;
            ItemStack st = s.getStack();
            if (st != null) {
                if (mult < 0) {
                    // [液滴分类] 可迁原生：缩小样板编辑槽内液滴数量,属样板内容编辑不参与合成计算
                    if (FluidDropCompat.isFluidDrop(st.getItem())) {
                        st.stackSize /= Math.abs(mult);
                    } else if (st.getItem() instanceof ItemFluidPacket) {
                        ItemFluidPacket.setFluidAmount(st, ItemFluidPacket.getFluidAmount(st) / Math.abs(mult));
                    } else {
                        st.stackSize /= Math.abs(mult);
                    }
                } else {
                    // [液滴分类] 可迁原生：样板槽数量放大时按液滴类型改 stackSize,属样板内容编辑不参与合成
                    if (FluidDropCompat.isFluidDrop(st.getItem())) {
                        st.stackSize *= mult;
                    } else if (st.getItem() instanceof ItemFluidPacket) {
                        ItemFluidPacket.setFluidAmount(st, ItemFluidPacket.getFluidAmount(st) * mult);
                    } else {
                        st.stackSize *= mult;
                    }
                }
            }
        }
    }

    Slot getPatternOutputSlot();
}
