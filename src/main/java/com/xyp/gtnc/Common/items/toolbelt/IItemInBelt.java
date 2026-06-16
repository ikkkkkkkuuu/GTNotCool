package com.xyp.gtnc.Common.items.toolbelt;

import net.minecraft.item.ItemStack;

/**
 * 腰带内物品接口
 * 暴露给希望在腰带内接收刻更新的物品
 */
public interface IItemInBelt {

    /**
     * 只要物品留在腰带中，每刻运行一次
     *
     * @param stack     腰带槽中的物品堆栈
     * @param container 被引用的腰带物品
     */
    void onWornTick(ItemStack stack, ItemStack container);
}
