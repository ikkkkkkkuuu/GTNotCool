package com.xyp.gtnc.mixins.late.Gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import gregtech.api.covers.CoverRegistry;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEBasicTank;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/**
 * 单方块机器 I/O 面朝向重写
 * <p>
 * 通过继承 {@link MTEBasicMachine} 并重写多个方法，使所有面均可输入/输出：
 * <ul>
 * <li>{@code isInputFacing} → 所有面均可输入物品</li>
 * <li>{@code isLiquidInput} → 所有面均可输入流体（正面需配置允许）</li>
 * <li>{@code isLiquidOutput} → 所有面均可输出流体</li>
 * <li>{@code allowCoverOnSide} → 仅允许可点击GUI的覆盖板</li>
 * <li>{@code allowPullStack} → 输出槽可从任意面自动抽出</li>
 * <li>{@code allowPutStack} → 输入槽可从任意面输入（支持堆叠限制/过滤器）</li>
 * </ul>
 */
@Mixin(value = MTEBasicMachine.class, remap = false)
public abstract class MixinMTEBasicMachineFacing extends MTEBasicTank {

    @Shadow
    public boolean mAllowInputFromOutputSide;

    @Shadow
    @Final
    public int mInputSlotCount;

    @Shadow
    @Final
    public ItemStack[] mOutputItems;

    @Shadow
    public boolean mDisableMultiStack;

    @Shadow
    public boolean mDisableFilter;

    @Shadow
    protected abstract boolean allowPutStackValidated(IGregTechTileEntity aBaseMetaTileEntity, int aIndex,
        ForgeDirection side, ItemStack aStack);

    public MixinMTEBasicMachineFacing(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount,
        String aDescription, ITexture... aTextures) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean isLiquidInput(ForgeDirection side) {
        return mAllowInputFromOutputSide || side != getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isLiquidOutput(ForgeDirection side) {
        return true;
    }

    @Override
    public boolean allowCoverOnSide(ForgeDirection side, ItemStack coverItem) {
        return CoverRegistry.getCoverPlacer(coverItem)
            .isGuiClickable();
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return aIndex >= getOutputSlot() && aIndex < getOutputSlot() + mOutputItems.length;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        if (aIndex < getInputSlot()) return false;
        if (aIndex >= getInputSlot() + mInputSlotCount) return false;
        if (!mAllowInputFromOutputSide && side == aBaseMetaTileEntity.getFrontFacing()) return false;

        for (int i = getInputSlot(), j = i + mInputSlotCount; i < j; i++) {
            if (GTUtility.areStacksEqual(GTOreDictUnificator.get(aStack), mInventory[i]) && mDisableMultiStack) {
                return i == aIndex;
            }
        }

        return mDisableFilter || allowPutStackValidated(aBaseMetaTileEntity, aIndex, side, aStack);
    }
}
