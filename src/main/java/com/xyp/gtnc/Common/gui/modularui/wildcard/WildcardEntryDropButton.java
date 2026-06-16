package com.xyp.gtnc.Common.gui.modularui.wildcard;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;

import com.gtnewhorizons.modularui.api.widget.IDragAndDropHandler;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;

public class WildcardEntryDropButton extends ButtonWidget implements IDragAndDropHandler {

    private final Consumer<ItemStack> dropHandler;

    public WildcardEntryDropButton(Consumer<ItemStack> dropHandler) {
        this.dropHandler = dropHandler;
    }

    @Override
    public boolean handleDragAndDrop(ItemStack draggedStack, int button) {
        if (draggedStack == null || this.dropHandler == null) {
            return false;
        }
        ItemStack copy = draggedStack.copy();
        copy.stackSize = 1;
        this.dropHandler.accept(copy);
        markForUpdate();
        return true;
    }
}
