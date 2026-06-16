package com.xyp.gtnc.utils.keybind;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

/**
 * 按键绑定管理器
 * Key binding manager for all mod key bindings
 */
public class KeyBindManager {

    // ==================== 工具带相关按键绑定 ====================
    // Toolbelt related key bindings

    /**
     * 打开工具带环形菜单
     * Open toolbelt radial menu
     */
    public static KeyBinding openToolMenuKeybind;

    /**
     * 打开腰带槽位背包
     * Open belt slot inventory
     */
    public static KeyBinding openBeltSlotKeybind;

    /**
     * 环形菜单向左切换
     * Cycle radial menu left
     */
    public static KeyBinding cycleToolMenuLeft;

    /**
     * 环形菜单向右切换
     * Cycle radial menu right
     */
    public static KeyBinding cycleToolMenuRight;

    /**
     * 注册所有按键绑定
     * Register all key bindings
     */
    public static void registerAllKeyBinds() {
        // 工具带按键绑定
        // Toolbelt key bindings

        // #tr key.categories.sciencenotcool
        // # GT-Not-Cool
        // # zh_CN 格雷不酷
        openToolMenuKeybind = new KeyBinding("key.toolbelt.open", Keyboard.KEY_R, "key.categories.sciencenotcool");

        // #tr key.toolbelt.open
        // # Open Tool Belt Menu
        // # zh_CN 打开工具腰带菜单
        openBeltSlotKeybind = new KeyBinding("key.toolbelt.slot", Keyboard.KEY_V, "key.categories.sciencenotcool");

        // #tr key.toolbelt.slot
        // # Open Belt Slot
        // # zh_CN 打开腰带槽
        cycleToolMenuLeft = new KeyBinding(
            "key.toolbelt.cycle.left",
            Keyboard.KEY_NONE,
            "key.categories.sciencenotcool");

        // #tr key.toolbelt.cycle.left
        // # Cycle Left
        // # zh_CN 左切换
        cycleToolMenuRight = new KeyBinding(
            "key.toolbelt.cycle.right",
            Keyboard.KEY_NONE,
            "key.categories.sciencenotcool");

        // #tr key.toolbelt.cycle.right
        // # Cycle Right
        // # zh_CN 右切换

        // 注册到客户端
        // Register to client
        ClientRegistry.registerKeyBinding(openToolMenuKeybind);
        ClientRegistry.registerKeyBinding(openBeltSlotKeybind);
        ClientRegistry.registerKeyBinding(cycleToolMenuLeft);
        ClientRegistry.registerKeyBinding(cycleToolMenuRight);
    }

    /**
     * 检查按键绑定的物理按键是否当前被按住
     * Check if the physical key of a key binding is currently held down
     *
     * @param key 按键绑定 / Key binding
     * @return 是否按住 / Is held down
     */
    public static boolean isKeyDown(KeyBinding key) {
        return key != null && Keyboard.isKeyDown(key.getKeyCode());
    }

    /**
     * 消耗按键状态(防止重复触发)
     * Consume key state (prevent repeated triggering)
     *
     * @param key 按键绑定 / Key binding
     */
    public static void consumeKey(KeyBinding key) {
        while (key != null && key.isPressed()) {
            // 消耗按键事件 / Consume key event
        }
    }
}
