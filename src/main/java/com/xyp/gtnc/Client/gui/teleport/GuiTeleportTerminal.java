package com.xyp.gtnc.Client.gui.teleport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.teleport.MessageTeleportAction;
import com.xyp.gtnc.Common.teleport.TeleportDestination;
import com.xyp.gtnc.ScienceNotCool;

/**
 * Client-only terminal styled after the Draconic Evolution teleporter. The server owns all data and movement.
 */
public class GuiTeleportTerminal extends GuiScreen {

    // #tr gui.gtnc.teleport_terminal.title
    // # Teleport Terminal
    // # zh_CN 传送终端
    private static final String TITLE_KEY = "gui.gtnc.teleport_terminal.title";
    // #tr gui.gtnc.teleport_terminal.destinations
    // # Saved Destinations
    // # zh_CN 已保存坐标
    private static final String DESTINATIONS_KEY = "gui.gtnc.teleport_terminal.destinations";
    // #tr gui.gtnc.teleport_terminal.destination_name
    // # Destination Name
    // # zh_CN 坐标名称
    private static final String DESTINATION_NAME_KEY = "gui.gtnc.teleport_terminal.destination_name";
    // #tr gui.gtnc.teleport_terminal.save_current
    // # Save Current Position
    // # zh_CN 保存当前位置
    private static final String SAVE_CURRENT_KEY = "gui.gtnc.teleport_terminal.save_current";
    // #tr gui.gtnc.teleport_terminal.teleport
    // # Teleport
    // # zh_CN 传送
    private static final String TELEPORT_KEY = "gui.gtnc.teleport_terminal.teleport";
    // #tr gui.gtnc.teleport_terminal.delete
    // # Delete
    // # zh_CN 删除
    private static final String DELETE_KEY = "gui.gtnc.teleport_terminal.delete";
    // #tr gui.gtnc.teleport_terminal.no_destination
    // # No destination selected
    // # zh_CN 未选择坐标
    private static final String NO_DESTINATION_KEY = "gui.gtnc.teleport_terminal.no_destination";
    // #tr gui.gtnc.teleport_terminal.coordinates
    // # Coordinates
    // # zh_CN 坐标
    private static final String COORDINATES_KEY = "gui.gtnc.teleport_terminal.coordinates";
    // #tr gui.gtnc.teleport_terminal.dimension
    // # Dimension
    // # zh_CN 维度
    private static final String DIMENSION_KEY = "gui.gtnc.teleport_terminal.dimension";
    // #tr gui.gtnc.teleport_terminal.online
    // # Personal Link Ready
    // # zh_CN 个人链路就绪
    private static final String ONLINE_KEY = "gui.gtnc.teleport_terminal.online";
    // #tr gui.gtnc.teleport_terminal.rename
    // # Rename
    // # zh_CN 重命名
    private static final String RENAME_KEY = "gui.gtnc.teleport_terminal.rename";
    // #tr gui.gtnc.teleport_terminal.lock
    // # Lock
    // # zh_CN 锁定
    private static final String LOCK_KEY = "gui.gtnc.teleport_terminal.lock";
    // #tr gui.gtnc.teleport_terminal.unlock
    // # Unlock
    // # zh_CN 解锁
    private static final String UNLOCK_KEY = "gui.gtnc.teleport_terminal.unlock";
    // #tr gui.gtnc.teleport_terminal.status_locked
    // # Protected
    // # zh_CN 已锁定保护
    private static final String STATUS_LOCKED_KEY = "gui.gtnc.teleport_terminal.status_locked";
    // #tr gui.gtnc.teleport_terminal.status_unlocked
    // # Editable
    // # zh_CN 可编辑
    private static final String STATUS_UNLOCKED_KEY = "gui.gtnc.teleport_terminal.status_unlocked";

    private static final ResourceLocation BACKGROUND = GTNCGuiTextures.MODERN_BACKGROUND_LOCATION;
    private static final ResourceLocation BUTTON = GTNCGuiTextures.MODERN_BUTTON_COMPACT_LOCATION;

    private static final int GUI_WIDTH = 352;
    private static final int GUI_HEIGHT = 300;
    private static final int ROW_HEIGHT = 21;
    private static final int ROW_COUNT = 7;

    private static List<TeleportDestination> destinations = Collections.emptyList();

    private GuiTextField nameField;
    private int left;
    private int top;
    private int selectedIndex = -1;
    private int page;

    public static void applyDestinations(List<TeleportDestination> updatedDestinations) {
        destinations = Collections.unmodifiableList(new ArrayList<>(updatedDestinations));
        if (Minecraft.getMinecraft().currentScreen instanceof GuiTeleportTerminal) {
            ((GuiTeleportTerminal) Minecraft.getMinecraft().currentScreen).refreshSelection();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        left = (width - GUI_WIDTH) / 2;
        top = (height - GUI_HEIGHT) / 2;
        buttonList.clear();
        buttonList.add(new ModernTeleportButton(0, left + 18, top + 260, 94, 20, translate(SAVE_CURRENT_KEY)));
        buttonList.add(new ModernTeleportButton(5, left + 115, top + 260, 51, 20, translate(RENAME_KEY)));
        buttonList.add(new ModernTeleportButton(1, left + 235, top + 260, 98, 20, translate(TELEPORT_KEY)));
        buttonList.add(new ModernTeleportButton(2, left + 235, top + 234, 98, 20, translate(DELETE_KEY)));
        buttonList.add(new ModernTeleportButton(6, left + 235, top + 208, 98, 20, translate(LOCK_KEY)));
        buttonList.add(new ModernTeleportButton(3, left + 171, top + 237, 20, 16, "<"));
        buttonList.add(new ModernTeleportButton(4, left + 193, top + 237, 20, 16, ">"));
        nameField = new GuiTextField(fontRendererObj, left + 18, top + 237, 148, 16);
        nameField.setMaxStringLength(32);
        nameField.setFocused(true);
        refreshSelection();
        ScienceNotCool.channel
            .sendToServer(new MessageTeleportAction(MessageTeleportAction.Action.REQUEST_DESTINATIONS, -1, ""));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            ScienceNotCool.channel.sendToServer(
                new MessageTeleportAction(MessageTeleportAction.Action.ADD_CURRENT_POSITION, -1, nameField.getText()));
            nameField.setText("");
        } else if (button.id == 1 && hasSelection()) {
            ScienceNotCool.channel.sendToServer(
                new MessageTeleportAction(MessageTeleportAction.Action.TELEPORT_TO_DESTINATION, selectedIndex, ""));
        } else if (button.id == 2 && selectedIndex >= 0) {
            ScienceNotCool.channel.sendToServer(
                new MessageTeleportAction(MessageTeleportAction.Action.REMOVE_DESTINATION, selectedIndex, ""));
        } else if (button.id == 5 && hasEditableSelection()) {
            ScienceNotCool.channel.sendToServer(
                new MessageTeleportAction(
                    MessageTeleportAction.Action.RENAME_DESTINATION,
                    selectedIndex,
                    nameField.getText()));
        } else if (button.id == 6 && selectedIndex >= 0) {
            ScienceNotCool.channel.sendToServer(
                new MessageTeleportAction(MessageTeleportAction.Action.TOGGLE_DESTINATION_LOCK, selectedIndex, ""));
        } else if (button.id == 3 && page > 0) {
            page--;
        } else if (button.id == 4 && page < getPageCount() - 1) {
            page++;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
        if ((mouseButton == 0 || mouseButton == 1) && mouseX >= left + 18
            && mouseX < left + 211
            && mouseY >= top + 63
            && mouseY < top + 63 + ROW_COUNT * ROW_HEIGHT) {
            int index = page * ROW_COUNT + (mouseY - (top + 63)) / ROW_HEIGHT;
            if (index < destinations.size()) {
                selectDestination(index);
                if (mouseButton == 1) {
                    ScienceNotCool.channel.sendToServer(
                        new MessageTeleportAction(MessageTeleportAction.Action.TELEPORT_TO_DESTINATION, index, ""));
                }
            }
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
            return;
        }
        if (keyCode == Keyboard.KEY_RETURN && nameField.isFocused()) {
            actionPerformed((GuiButton) buttonList.get(0));
            return;
        }
        nameField.textboxKeyTyped(character, keyCode);
        super.keyTyped(character, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.renderEngine.bindTexture(BACKGROUND);
        drawScaledTexture(left, top, GUI_WIDTH, GUI_HEIGHT, 176, 166);

        drawRect(left + 5, top + 5, left + GUI_WIDTH - 5, top + 30, 0xCC30394D);
        drawPanel(left + 12, top + 39, 205, 249);
        drawPanel(left + 222, top + 39, 118, 249);
        drawCenteredString(fontRendererObj, translate(TITLE_KEY), left + GUI_WIDTH / 2, top + 13, 0xFFEAF5FF);
        fontRendererObj.drawString(translate(ONLINE_KEY), left + GUI_WIDTH - 100, top + 13, 0xFF65F5A3);

        fontRendererObj.drawString(translate(DESTINATIONS_KEY), left + 18, top + 47, 0xFF64CBFF);
        drawDestinationRows(mouseX, mouseY);
        drawDetails();
        fontRendererObj.drawString(translate(DESTINATION_NAME_KEY), left + 18, top + 220, 0xFFBCC6D8);
        nameField.drawTextBox();
        updateActionButtons();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawPanel(int x, int y, int panelWidth, int panelHeight) {
        drawRect(x, y, x + panelWidth, y + panelHeight, 0xFF10141D);
        drawRect(x + 2, y + 2, x + panelWidth - 2, y + panelHeight - 2, 0xFF2D3547);
        drawRect(x + 5, y + 5, x + panelWidth - 5, y + panelHeight - 5, 0xEE1B202C);
    }

    private void drawDestinationRows(int mouseX, int mouseY) {
        int firstIndex = page * ROW_COUNT;
        for (int row = 0; row < ROW_COUNT && firstIndex + row < destinations.size(); row++) {
            int index = firstIndex + row;
            int y = top + 63 + row * ROW_HEIGHT;
            boolean selected = index == selectedIndex;
            boolean hovered = mouseX >= left + 18 && mouseX < left + 211 && mouseY >= y && mouseY < y + ROW_HEIGHT;
            if (selected || hovered) {
                drawRect(left + 16, y, left + 213, y + ROW_HEIGHT - 1, selected ? 0xFF3B5A77 : 0xFF29364A);
            }
            TeleportDestination destination = destinations.get(index);
            String marker = destination.locked ? "◆" : "•";
            int markerColor = destination.locked ? 0xFFFFC857 : selected ? 0xFF58EEFF : 0xFF8397B9;
            fontRendererObj.drawString(marker, left + 22, y + 6, markerColor);
            fontRendererObj.drawString(destination.name, left + 36, y + 6, 0xFFE5ECFA);
            fontRendererObj.drawString("[" + destination.dimension + "]", left + 177, y + 6, 0xFF78CDFF);
        }
        fontRendererObj.drawString((page + 1) + "/" + getPageCount(), left + 174, top + 221, 0xFF9CAAC4);
    }

    private void drawDetails() {
        if (selectedIndex < 0 || selectedIndex >= destinations.size()) {
            drawCenteredString(fontRendererObj, translate(NO_DESTINATION_KEY), left + 281, top + 91, 0xFFB3BDCF);
            return;
        }
        TeleportDestination destination = destinations.get(selectedIndex);
        drawCenteredString(fontRendererObj, destination.name, left + 281, top + 55, 0xFF58EEFF);
        fontRendererObj.drawString(translate(DIMENSION_KEY), left + 234, top + 83, 0xFFB8C4D8);
        fontRendererObj.drawString(String.valueOf(destination.dimension), left + 234, top + 97, 0xFF78CDFF);
        fontRendererObj.drawString(translate(COORDINATES_KEY), left + 234, top + 124, 0xFFB8C4D8);
        fontRendererObj
            .drawString(destination.x + ", " + destination.y + ", " + destination.z, left + 234, top + 138, 0xFFE4EBF8);
        fontRendererObj.drawString(
            destination.locked ? translate(STATUS_LOCKED_KEY) : translate(STATUS_UNLOCKED_KEY),
            left + 234,
            top + 163,
            destination.locked ? 0xFFFFC857 : 0xFF65F5A3);
    }

    private void refreshSelection() {
        if (selectedIndex >= destinations.size()) selectedIndex = destinations.isEmpty() ? -1 : destinations.size() - 1;
        if (page >= getPageCount()) page = getPageCount() - 1;
    }

    private void selectDestination(int index) {
        selectedIndex = index;
        if (nameField != null) nameField.setText(destinations.get(index).name);
    }

    private boolean hasEditableSelection() {
        return hasSelection() && !destinations.get(selectedIndex).locked;
    }

    private boolean hasSelection() {
        return selectedIndex >= 0 && selectedIndex < destinations.size();
    }

    private void updateActionButtons() {
        for (Object buttonObject : buttonList) {
            GuiButton button = (GuiButton) buttonObject;
            if (button.id == 1) {
                button.enabled = hasSelection();
            } else if (button.id == 2 || button.id == 5) {
                button.enabled = hasEditableSelection();
            } else if (button.id == 6) {
                button.enabled = hasSelection();
                if (button.enabled) {
                    button.displayString = destinations.get(selectedIndex).locked ? translate(UNLOCK_KEY)
                        : translate(LOCK_KEY);
                }
            }
        }
    }

    private int getPageCount() {
        return Math.max(1, (destinations.size() + ROW_COUNT - 1) / ROW_COUNT);
    }

    private static String translate(String key) {
        return StatCollector.translateToLocal(key);
    }

    private static void drawScaledTexture(int x, int y, int width, int height, int textureWidth, int textureHeight) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glScalef((float) width / textureWidth, (float) height / textureHeight, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, textureHeight, 0, 0, 1);
        tessellator.addVertexWithUV(textureWidth, textureHeight, 0, 1, 1);
        tessellator.addVertexWithUV(textureWidth, 0, 0, 1, 0);
        tessellator.addVertexWithUV(0, 0, 0, 0, 0);
        tessellator.draw();
        GL11.glPopMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static final class ModernTeleportButton extends GuiButton {

        private ModernTeleportButton(int id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height, text);
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (!visible) return;
            field_146123_n = mouseX >= xPosition && mouseY >= yPosition
                && mouseX < xPosition + width
                && mouseY < yPosition + height;
            minecraft.renderEngine.bindTexture(BUTTON);
            int shade = !enabled ? 0xFF777B86 : field_146123_n ? 0xFFFFFFFF : 0xFFE4E9F6;
            drawScaledTexture(xPosition, yPosition, width, height, 18, 18);
            drawCenteredString(
                minecraft.fontRenderer,
                displayString,
                xPosition + width / 2,
                yPosition + (height - 8) / 2,
                shade);
        }
    }
}
