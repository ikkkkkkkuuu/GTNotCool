package com.xyp.gtnc.Common.items.toolbelt.client;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
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

    // Virtual cursor offset from screen centre, in GUI pixels.
    // Tracked via absolute-position delta (getX/getY relative to pinned centre)
    // rather than getDX/getDY to avoid the counter-delta that setCursorPosition injects.
    private float virtualX = 0;
    private float virtualY = 0;
    // Sensitivity multiplier: 1.0 = 1 GUI-px per screen-px moved.
    private static final float SENSITIVITY = 1.5f;

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
    public void initGui() {
        super.initGui();
        virtualX = 0;
        virtualY = 0;
        // Pin the real cursor to screen centre so it never drifts to the edge.
        Mouse.setCursorPosition(mc.displayWidth / 2, mc.displayHeight / 2);
        // Hide the OS cursor.
        try {
            IntBuffer buf = BufferUtils.createIntBuffer(1);
            buf.put(0)
                .rewind();
            Mouse.setNativeCursor(new Cursor(1, 1, 0, 0, 1, buf, null));
        } catch (Exception ignored) {}
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // --- Virtual cursor: absolute-position delta approach ----------------
        // We pinned the real cursor to screen centre at the end of the previous frame.
        // Current Mouse.getX/Y reflects where the user moved it since then, so
        // delta = current - centre is the actual user movement this frame.
        // We THEN re-pin (before reading again next frame) to prevent screen-edge stall.
        // Using getX/Y instead of getDX/getDY avoids the spurious counter-delta that
        // setCursorPosition injects into the getDX/getDY event queue.
        net.minecraft.client.gui.ScaledResolution sr = new net.minecraft.client.gui.ScaledResolution(
            mc,
            mc.displayWidth,
            mc.displayHeight);
        float scale = sr.getScaleFactor();
        int cx = mc.displayWidth / 2;
        int cy = mc.displayHeight / 2;

        // Raw screen-pixel delta since last frame (LWJGL Y is from bottom, so flip).
        int rawDX = Mouse.getX() - cx;
        int rawDY = -(Mouse.getY() - cy); // flip: LWJGL up = positive, GUI down = positive

        // Re-pin cursor to centre NOW so next frame starts clean.
        Mouse.setCursorPosition(cx, cy);

        // PUBG style: direct position mapping. virtualX/Y = where the cursor IS,
        // not a running total of how far it has travelled. Moving the mouse right
        // immediately puts the virtual cursor to the right; moving back re-centres.
        virtualX = rawDX * SENSITIVITY / scale;
        virtualY = rawDY * SENSITIVITY / scale;
        float radiusOut = 60f;
        float dist = (float) Math.sqrt(virtualX * virtualX + virtualY * virtualY);
        if (dist > radiusOut) {
            virtualX = virtualX / dist * radiusOut;
            virtualY = virtualY / dist * radiusOut;
        }

        int effectiveMouseX = sr.getScaledWidth() / 2 + (int) virtualX;
        int effectiveMouseY = sr.getScaledHeight() / 2 + (int) virtualY;

        super.drawScreen(effectiveMouseX, effectiveMouseY, partialTicks);

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

        menu.draw(effectiveMouseX, effectiveMouseY, partialTicks);
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
        // Restore the default OS cursor
        try {
            Mouse.setNativeCursor(null);
        } catch (Exception ignored) {}
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
