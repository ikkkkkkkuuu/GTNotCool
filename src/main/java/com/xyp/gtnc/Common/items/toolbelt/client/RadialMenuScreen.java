package com.xyp.gtnc.Common.items.toolbelt.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.xyp.gtnc.Common.items.toolbelt.BeltFinder;
import com.xyp.gtnc.Common.items.toolbelt.ConfigData;
import com.xyp.gtnc.Common.items.toolbelt.ToolBeltItem;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.GenericRadialMenu;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.IRadialMenuHost;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.ItemStackRadialMenuItem;
import com.xyp.gtnc.Common.items.toolbelt.client.radial.TextRadialMenuItem;
import com.xyp.gtnc.Common.packet.SwapItems;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.utils.keybind.KeyBindManager;

public class RadialMenuScreen extends GuiScreen {

    private final BeltFinder.BeltGetter getter;
    private ItemStack stackEquipped;
    private int inventorySize;

    private boolean keyCycleBeforeL = false;
    private boolean keyCycleBeforeR = false;

    private boolean needsRecheckStacks = true;
    private final List<ItemStackRadialMenuItem> cachedMenuItems = new ArrayList<>();
    private final TextRadialMenuItem insertMenuItem;
    private final GenericRadialMenu menu;

    public RadialMenuScreen(BeltFinder.BeltGetter getter) {
        this.getter = getter;
        this.stackEquipped = getter.getBelt();
        this.inventorySize = ToolBeltItem.getBeltSize(stackEquipped);

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
                close();
            }
        };

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

        ItemStack inHand = mc.thePlayer.getHeldItem();
        if (inHand != null && !ConfigData.isItemStackAllowed(inHand)) {
            stackEquipped = null;
        } else {
            ItemStack stack = getter.getBelt();
            if (stack == null) {
                stackEquipped = null;
            } else if (stackEquipped != stack) {
                stackEquipped = stack;
                inventorySize = ToolBeltItem.getBeltSize(stackEquipped);
                needsRecheckStacks = true;
            }
        }

        if (stackEquipped == null) {
            menu.close();
        } else if (!KeyBindManager.isKeyDown(KeyBindManager.openToolMenuKeybind)) {
            // Key was released
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
        super.drawScreen(mouseX, mouseY, partialTicks);

        ItemStack inHand = mc.thePlayer.getHeldItem();
        if (inHand != null && !ConfigData.isItemStackAllowed(inHand)) return;

        if (needsRecheckStacks) {
            cachedMenuItems.clear();

            for (int i = 0; i < inventorySize; i++) {
                ItemStack inSlot = ToolBeltItem.getBeltSlot(stackEquipped, i);
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
     * Returns true if any belt slot is empty or contains a mergeable stack.
     */
    private boolean hasSpaceForItem(ItemStack stack) {
        if (stack == null || stackEquipped == null) return false;
        for (int i = 0; i < inventorySize; i++) {
            ItemStack inSlot = ToolBeltItem.getBeltSlot(stackEquipped, i);
            if (inSlot == null) {
                return true; // Empty slot available
            }
            if (inSlot.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inSlot, stack)) {
                int max = inSlot.getMaxStackSize();
                if (inSlot.stackSize + stack.stackSize <= max) {
                    return true; // Can fully merge
                }
                if (inSlot.stackSize < max) {
                    return true; // Can partially merge (some items will go in)
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
