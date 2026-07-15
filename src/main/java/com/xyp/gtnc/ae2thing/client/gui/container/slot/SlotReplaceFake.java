package com.xyp.gtnc.ae2thing.client.gui.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Common.compat.FluidDropCompat;

import appeng.container.slot.SlotFake;
import codechicken.nei.recipe.StackInfo;

public class SlotReplaceFake extends SlotFake {

    public SlotReplaceFake(IInventory inv, int idx, int x, int y) {
        super(inv, idx, x, y);
    }

    @Override
    public void putStack(ItemStack is) {
        if (is != null) {
            is = is.copy();
            is.stackSize = 1;
            FluidStack fs = StackInfo.getFluid(is);
            if (fs != null) {
                // [液滴分类] 可迁原生：把放入替换伪槽的流体转液滴显示,属样板替换编辑不参与合成计算
                is = FluidDropCompat.newStack(fs);
            }
        }
        super.putStack(is);
    }
}
