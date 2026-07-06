package com.xyp.gtnc.mixins.minecraft;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.xyp.gtnc.utils.LargeInventoryCrafting;

@Mixin(value = InventoryCrafting.class, remap = true)
public class MixinInventoryCrafting implements LargeInventoryCrafting {

    @Unique
    private long gtnc$assembler;

    @Intrinsic
    public void setAssemblerSize(long value) {
        gtnc$assembler = value;
    }

    @Intrinsic
    public long getAssemblerSize() {
        return gtnc$assembler;
    }
}
