package com.xyp.gtnc.Common.items.toolbelt.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;

import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltData;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.GenericRadialMenu;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.IRadialMenuHost;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.ItemStackRadialMenuItem;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.TextRadialMenuItem;
import com.xyp.gtnc.Common.packet.SwapItems;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.utils.keybind.KeyBindManager;

public class RadialMenuScreen extends GuiScreen {

    private final EntityPlayer player;
    private int inventorySize;

    private boolean keyCycleBeforeL = false;
    private boolean keyCycleBeforeR = false;

    private boolean needsRecheckStacks = true;
    private final List<ItemStackRadialMenuItem> cachedMenuItems = new ArrayList<>();
    private final TextRadialMenuItem insertMenuItem;
    private final GenericRadialMenu menu;

    public RadialMenuScreen(EntityPlayer player) {
        this.player = player;
        this.inventorySize = ToolBeltData.SLOT_COUNT;

        this.menu = new GenericRadialMenu(Minecraft.getMinecraft(), new IRadialMenuHost() {

            @Override
            public void renderTooltip(ItemStack stack, int mouseX, int mouseY) {
                RadialMenuScreen.this.renderToolTip(stack, mouseX, mouseY);
            }

            @Override
            public GuiScreen getScreen() {
                return RadialMenuScreen.this;
            }

            @Override
            public net.minecraft.client.gui.FontRenderer getFontRenderer() {
                return fontRendererObj;
            }
        }) {

            @Override
            public void onClickOutside() {
                if (ConfigData.allowClickOutsideBounds) {
                    close();
                }
            }
        };

        // #tr text.toolbelt.insert
        // # Insert
        // # zh_CN 放入
        this.insertMenuItem = new TextRadialMenuItem(menu, StatCollector.translateToLocal("text.toolbelt.insert")) {

            @Override
            public boolean onClick() {
                return RadialMenuScreen.this.trySwap(-1, null);
            }
        };
    }

    public void handleKeyInput() {
        if (KeyBindManager.cycleToolMenuLeft != null && KeyBindManager.cycleToolMenuLeft.isPressed()) {
            if (!keyCycleBeforeL) {
                // TODO: cycle
            }
            keyCycleBeforeL = true;
        } else {
            keyCycleBeforeL = false;
        }

        if (KeyBindManager.cycleToolMenuRight != null && KeyBindManager.cycleToolMenuRight.isPressed()) {
            if (!keyCycleBeforeR) {
                // TODO: cycle
            }
            keyCycleBeforeR = true;
        } else {
            keyCycleBeforeR = false;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        menu.tick();

        // When animation is fully closed, remove the GUI screen
        if (menu.isClosed()) {
            Minecraft.getMinecraft()
                .displayGuiScreen(null);
            return;
        }

        // Check if key is still held, close if released
        if (!KeyBindManager.isKeyDown(KeyBindManager.openToolMenuKeybind)) {
            if (ConfigData.releaseToSwap) {
                processClick(false);
            }
            close();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            processClick(true);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
    }

    protected void processClick(boolean triggeredByMouse) {
        menu.clickItem();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Clamp the OS cursor to the radial ring every render frame (not just every tick)
        // so the cursor cannot visually leave the circle.
        if (menu.isReady()) {
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            float scale = sr.getScaleFactor();
            int screenCenterX = mc.displayWidth / 2;
            int screenCenterY = mc.displayHeight / 2;
            // LWJGL Mouse: X from left, Y from bottom
            float dx = Mouse.getX() - screenCenterX;
            float dy = (mc.displayHeight - Mouse.getY()) - screenCenterY; // convert to Y-from-top
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float radiusPx = 60f * scale; // radiusOut (60 GUI px) in screen pixels
            if (dist > radiusPx && dist > 0f) {
                int cx = (int) (screenCenterX + dx / dist * radiusPx);
                int cy = (int) (screenCenterY + dy / dist * radiusPx);
                Mouse.setCursorPosition(cx, mc.displayHeight - cy); // back to LWJGL Y-from-bottom
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        ItemStack inHand = mc.thePlayer.getHeldItem();
        if (inHand != null && !ConfigData.isItemStackAllowed(inHand)) return;

        if (needsRecheckStacks) {
            cachedMenuItems.clear();

            ToolBeltData data = ToolBeltData.get(player);
            if (data != null) {
                for (int i = 0; i < inventorySize; i++) {
                    ItemStack inSlot = data.getStackInSlot(i);
                    if (inSlot != null) {
                        final int slot = i;
                        ItemStackRadialMenuItem item = new ItemStackRadialMenuItem(menu, inSlot) {

                            @Override
                            public boolean onClick() {
                                return RadialMenuScreen.this.trySwap(slot, null);
                            }
                        };
                        item.setVisible(true);
                        cachedMenuItems.add(item);
                    }
                }
            }

            menu.clear();
            if (inHand != null && hasSpaceForItem(inHand)) {
                menu.add(insertMenuItem);
            }
            for (ItemStackRadialMenuItem item : cachedMenuItems) {
                menu.add(item);
            }

            needsRecheckStacks = false;
        }

        menu.draw(mouseX, mouseY, partialTicks);
    }

    /**
     * Check if the belt has space to insert the given item stack.
     */
    private boolean hasSpaceForItem(ItemStack stack) {
        if (stack == null) return false;
        ToolBeltData data = ToolBeltData.get(player);
        if (data == null) return false;
        for (int i = 0; i < inventorySize; i++) {
            ItemStack inSlot = data.getStackInSlot(i);
            if (inSlot == null) {
                return true;
            }
            if (inSlot.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inSlot, stack)) {
                int max = inSlot.getMaxStackSize();
                if (inSlot.stackSize < max) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        KeyBindManager.consumeKey(KeyBindManager.openToolMenuKeybind);
    }

    public boolean trySwap(int slotNumber, ItemStack stackSwapped) {
        ScienceNotCool.channel.sendToServer(new SwapItems(slotNumber));
        menu.close();
        return true;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    void close() {
        Minecraft.getMinecraft()
            .displayGuiScreen(null);
    }
}
