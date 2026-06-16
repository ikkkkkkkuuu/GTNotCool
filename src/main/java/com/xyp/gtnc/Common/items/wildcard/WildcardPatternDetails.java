package com.xyp.gtnc.Common.items.wildcard;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidDrop;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.helpers.PatternHelper;
import appeng.util.item.AEFluidStack;
import gregtech.api.util.GTUtility;

/**
 * 通配样板符配方详情实现
 * 包装AE2的PatternHelper,让AE2能识别通配样板符
 */
public class WildcardPatternDetails implements ICraftingPatternDetails {

    private final PatternHelper delegate;

    public WildcardPatternDetails(ItemStack stack, World world) {
        this.delegate = new PatternHelper(stack, world);
    }

    @Override
    public ItemStack getPattern() {
        return this.delegate.getPattern();
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        return this.delegate.isValidItemForSlot(slotIndex, itemStack, world);
    }

    @Override
    public boolean isCraftable() {
        return this.delegate.isCraftable();
    }

    @Override
    public IAEItemStack[] getInputs() {
        return this.delegate.getInputs();
    }

    /**
     * AE2FC方法:返回混合的IAEItemStack和IAEFluidStack数组
     * 新版GTNH的合成CPU和PatternSlot都通过此方法识别流体请求
     */
    @Override
    public IAEStack<?>[] getAEInputs() {
        return convertToAeStacks(this.delegate.getInputs());
    }

    @Override
    public IAEStack<?>[] getCondensedAEInputs() {
        return convertToAeStacks(this.delegate.getCondensedInputs());
    }

    @Override
    public IAEStack<?>[] getAEOutputs() {
        return convertToAeStacks(this.delegate.getOutputs());
    }

    @Override
    public IAEStack<?>[] getCondensedAEOutputs() {
        return convertToAeStacks(this.delegate.getCondensedOutputs());
    }

    private IAEStack<?>[] convertToAeStacks(IAEItemStack[] items) {
        if (items == null || items.length == 0) {
            return new IAEStack<?>[0];
        }
        IAEStack<?>[] result = new IAEStack<?>[items.length];
        for (int i = 0; i < items.length; i++) {
            IAEItemStack ais = items[i];
            if (ais == null) {
                result[i] = null;
                continue;
            }
            ItemStack stack = ais.getItemStack();
            // 检测是否为流体输入(GT5 ItemFluidDisplay NEI拖入 或 AE2FC ItemFluidDrop 样板编码)
            FluidStack fluidStack = null;
            if (stack != null) {
                fluidStack = GTUtility.getFluidFromDisplayStack(stack);
                if (fluidStack == null) {
                    fluidStack = ItemFluidDrop.getFluidStack(stack);
                }
            }
            if (fluidStack != null) {
                result[i] = AEFluidStack.create(fluidStack);
            } else {
                result[i] = ais;
            }
        }
        return result;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return this.delegate.getCondensedInputs();
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return this.delegate.getCondensedOutputs();
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return this.delegate.getOutputs();
    }

    @Override
    public boolean canSubstitute() {
        return this.delegate.canSubstitute();
    }

    @Override
    public boolean canBeSubstitute() {
        return this.delegate.canBeSubstitute();
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInv, World world) {
        return this.delegate.getOutput(craftingInv, world);
    }

    @Override
    public int getPriority() {
        return this.delegate.getPriority();
    }

    @Override
    public void setPriority(int priority) {
        this.delegate.setPriority(priority);
    }

    @Override
    public int hashCode() {
        ItemStack pattern = this.getPattern();
        int result = pattern.getItem() != null ? System.identityHashCode(pattern.getItem()) : 0;
        result = 31 * result + pattern.getItemDamage();
        result = 31 * result + WildcardPatternGenerator.getPatternIdentity(pattern)
            .hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WildcardPatternDetails other)) {
            return false;
        }
        return arePatternStacksEqual(this.getPattern(), other.getPattern());
    }

    private static boolean arePatternStacksEqual(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (left.getItem() != right.getItem() || left.getItemDamage() != right.getItemDamage()) {
            return false;
        }
        String leftId = WildcardPatternGenerator.getGeneratedPatternId(left);
        String rightId = WildcardPatternGenerator.getGeneratedPatternId(right);
        if (!leftId.isEmpty() || !rightId.isEmpty()) {
            return leftId.equals(rightId);
        }
        NBTTagCompound leftTag = left.getTagCompound();
        NBTTagCompound rightTag = right.getTagCompound();
        if (leftTag == rightTag) {
            return true;
        }
        if (leftTag == null || rightTag == null) {
            return false;
        }
        return leftTag.equals(rightTag);
    }
}
