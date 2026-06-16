package com.xyp.gtnc.Common.gui.modularui.wildcard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * 通配样板符GUI处理器
 * 负责打开和管理通配样板符的配置界面
 */
public final class WildcardPatternGuiHandler {

    private WildcardPatternGuiHandler() {}

    /**
     * 打开通配样板符配置GUI
     * 
     * @param player        玩家
     * @param wildcardStack 通配样板符物品堆
     */
    public static void openGui(EntityPlayer player, ItemStack wildcardStack) {
        if (player == null || wildcardStack == null) return;

        // 在服务器端发送打开GUI的请求
        if (!player.worldObj.isRemote) {
            // 找到物品在玩家背包中的槽位
            int slot = player.inventory.currentItem;
            if (slot < 0) slot = 0;

            // 通过FML网络打开通用GUI
            player.openGui(com.xyp.gtnc.ScienceNotCool.instance, GUI_WILDCARD_PATTERN, player.worldObj, slot, 0, 0);
        }
    }

    /**
     * GUI ID常量
     */
    public static final int GUI_WILDCARD_PATTERN = 1000;
}
