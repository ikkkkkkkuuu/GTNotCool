package com.xyp.gtnc.Common.gui.modularui.widget;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.integration.recipeviewer.RecipeViewerGhostIngredientSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.xyp.gtnc.Common.machines.bee.BeeBreedingHelper;

/**
 * A text field that accepts bee items dragged from NEI and auto-fills the species name.
 * <p>
 * Implements {@link RecipeViewerGhostIngredientSlot} for CleanroomMC ModularUI compat.
 */
public class BeeSpeciesDropTextField extends TextFieldWidget implements RecipeViewerGhostIngredientSlot<ItemStack> {

    public BeeSpeciesDropTextField() {
        autoUpdateOnChange(true);
    }

    @Override
    public boolean handleDragAndDrop(@NotNull ItemStack draggedStack, int button) {
        if (draggedStack == null) return false;
        if (button != 0) return false;

        if (!BeeBreedingHelper.isBee(draggedStack)) return false;

        // 获取未本地化的内部标识名（客户端/服务端一致，如 "for.bees.species.steel"）
        String speciesKey = BeeBreedingHelper.getBeeSpecies(draggedStack);
        if (speciesKey == null || speciesKey.isEmpty()) return false;

        // 显示友好名称（如 "Steel"），setText 会触发 StringSyncValue 同步
        String displayName = BeeBreedingHelper.getSpeciesDisplayName(speciesKey);
        setText(displayName);
        onTextChanged();
        return true;
    }
}
