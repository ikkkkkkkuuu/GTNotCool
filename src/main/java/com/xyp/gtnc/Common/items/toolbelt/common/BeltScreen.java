package com.xyp.gtnc.Common.items.toolbelt.common;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class BeltScreen extends GuiContainer {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("sciencenotcool", "textures/gui/belt.png");

    public BeltScreen(BeltContainer container, InventoryPlayer playerInventory) {
        super(container);
        this.ySize = 133;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(GUI_TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        // Draw the belt slot row background
        BeltContainer container = (BeltContainer) this.inventorySlots;
        int slots = container.inventorySize;
        int width = slots * 18;
        int slotX = 7 + ((9 - slots) * 18) / 2;
        this.drawTexturedModalRect(x + slotX, y + 19, 0, this.ySize, width, 18);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString("Belt", 8, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 2, 4210752);
    }
}
