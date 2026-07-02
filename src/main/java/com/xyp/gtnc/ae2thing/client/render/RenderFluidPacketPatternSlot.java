package com.xyp.gtnc.ae2thing.client.render;

import static appeng.client.gui.AEBaseGui.aeRenderItem;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidDrop;
import com.glodblock.github.common.item.ItemFluidPacket;
import com.xyp.gtnc.ae2thing.client.gui.IGuiDrawSlot;

import appeng.api.storage.data.IAEItemStack;

public class RenderFluidPacketPatternSlot implements ISlotRender {

    private static final Set<Integer> renderingSlots = new HashSet<>();

    @Override
    public Predicate<Slot> get() {
        return slot -> {
            ItemStack stack = slot.getStack();
            return stack != null
                && (stack.getItem() instanceof ItemFluidPacket || stack.getItem() instanceof ItemFluidDrop);
        };
    }

    @Override
    public boolean drawSlot(Slot slot, IAEItemStack stack, IGuiDrawSlot draw, boolean display) {
        FluidStack fluidStack = null;
        ItemStack itemStack = stack.getItemStack();
        if (itemStack.getItem() instanceof ItemFluidDrop) {
            fluidStack = ItemFluidDrop.getFluidStack(itemStack);
        } else if (itemStack.getItem() instanceof ItemFluidPacket) {
            fluidStack = ItemFluidPacket.getFluidStack(itemStack);
        }
        if (fluidStack != null && fluidStack.amount > 0) {
            int key = System.identityHashCode(slot);
            if (renderingSlots.add(key)) {
                try {
                    draw.getAEBaseGui()
                        .func_146977_a(slot);
                } finally {
                    renderingSlots.remove(key);
                }
            }
            IAEItemStack fake = stack.copy();
            fake.setStackSize(fluidStack.amount);
            aeRenderItem.setAeStack(fake);
            draw.renderStackSize(display, stack, slot);
            return false;
        }
        return true;
    }
}
