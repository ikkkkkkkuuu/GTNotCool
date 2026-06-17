package com.xyp.gtnc.utils.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.MouseEvent;

import org.lwjgl.input.Mouse;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.client.RadialMenuScreen;
import com.xyp.gtnc.Common.items.tools.VeinMiningPickaxe;
import com.xyp.gtnc.Common.packet.SyncVeinPickaxeNBT;
import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.utils.item.SubtitleDisplay;
import com.xyp.gtnc.utils.keybind.KeyBindManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 客户端事件监听器
 * Client-side event listener for handling various client events
 */
public class SubscribeEventClientUtils {

    /**
     * 处理按键输入事件
     * Handle key input events for toolbelt and other features
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null || mc.currentScreen != null) return;

        // 打开工具腰带径向菜单
        // Open tool belt radial menu
        if (KeyBindManager.openToolMenuKeybind.isPressed()) {
            ItemStack inHand = player.getHeldItem();
            if (inHand == null || ConfigData.isItemStackAllowed(inHand)) {
                ScienceNotCool.LOG.info("Opening tool belt radial menu");
                mc.displayGuiScreen(new RadialMenuScreen(player));
            }
        }
    }

    /**
     * 处理客户端Tick事件
     * Handle client tick events
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // 处理环形菜单（如果已打开）
        // Handle radial menu input (if open)
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof RadialMenuScreen) {
            ((RadialMenuScreen) mc.currentScreen).handleKeyInput();
        }
    }

    /**
     * 处理鼠标滚轮事件
     * Handle mouse wheel events for Vein Mining Pickaxe adjustments
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return;

        if (!(held.getItem() instanceof VeinMiningPickaxe)) return;

        NBTTagCompound nbt = held.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            held.setTagCompound(nbt);
        }

        boolean rightClickHeld = Mouse.isButtonDown(1);

        // Shift + 滚轮: 调节范围 (Range)
        // Shift + Scroll: Adjust range
        if (player.isSneaking() && !rightClickHeld) {
            if (event.dwheel == 0) return;
            int oldRange = nbt.hasKey("range") ? nbt.getInteger("range") : 3;
            int newRange = oldRange;

            if (event.dwheel > 0) {
                newRange++;
            } else {
                newRange--;
            }

            // 限制范围: 0 ~ maxRange
            // Clamp range: 0 ~ maxRange
            if (newRange < 0) newRange = 0;
            if (newRange > Config.VeinMinerPickaxe.maxRange) newRange = Config.VeinMinerPickaxe.maxRange;

            if (newRange != oldRange) {
                nbt.setInteger("range", newRange);

                // 显示副标题反馈
                // Show subtitle feedback
                if (mc.theWorld.isRemote && held.getItem() instanceof SubtitleDisplay) {
                    ((SubtitleDisplay) held.getItem()).showSubtitle("Tooltip_VeinMiningPickaxe_00", newRange);
                }

                // 同步到服务器
                // Sync to server
                int slot = player.inventory.currentItem;
                ScienceNotCool.channel.sendToServer(new SyncVeinPickaxeNBT(slot, nbt));
                event.setCanceled(true);
            }
        }

        // 右键按住 + 滚轮: 调节数量 (Amount)
        // Right-click hold + Scroll: Adjust amount
        if (!player.isSneaking() && rightClickHeld) {
            if (event.dwheel == 0) return;
            int oldAmount = nbt.hasKey("amount") ? nbt.getInteger("amount") : 327670;
            int newAmount = oldAmount;

            if (event.dwheel > 0) {
                newAmount += 10000;
            } else {
                newAmount -= 10000;
            }

            // 限制数量: 0 ~ maxAmount
            // Clamp amount: 0 ~ maxAmount
            if (newAmount < 0) newAmount = 0;
            if (newAmount > Config.VeinMinerPickaxe.maxAmount) newAmount = Config.VeinMinerPickaxe.maxAmount;

            if (newAmount != oldAmount) {
                nbt.setInteger("amount", newAmount);

                // 显示副标题反馈
                // Show subtitle feedback
                if (mc.theWorld.isRemote && held.getItem() instanceof SubtitleDisplay) {
                    ((SubtitleDisplay) held.getItem()).showSubtitle("Tooltip_VeinMiningPickaxe_01", newAmount);
                }

                // 同步到服务器
                // Sync to server
                int slot = player.inventory.currentItem;
                ScienceNotCool.channel.sendToServer(new SyncVeinPickaxeNBT(slot, nbt));
                event.setCanceled(true);
            }
        }
    }
}
