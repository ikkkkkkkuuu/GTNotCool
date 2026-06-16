package com.xyp.gtnc.utils.timeVial;

import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

/**
 * 时间之瓶物品信息提示工具类
 */
public class InformationHelper {

    // #tr infoHelper.dividingLine
    // # §e-----------------------------------------
    // # zh_CN §e-----------------------------------------
    public static final String dividingLine = "§e-----------------------------------------";

    /**
     * 检查是否按住Shift键显示详细信息
     * 
     * @param list 提示信息列表
     * @return 如果按住Shift返回true（显示详情），否则返回false（提示按Shift）
     */
    public static boolean holdShiftForDetails(java.util.List<String> list) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            // 按住Shift，显示详细信息
            return true;
        }
        // 没有按住Shift，提示用户按Shift
        // #tr infoHelper.holdShift
        // # §6====§f[§dHold §bShift §dfor Details§f]§6====
        // # zh_CN §6====§f[§d按住 §bShift §d查看详情§f]§6====
        list.add(I18n.format("infoHelper.holdShift"));
        return false;
    }
}
