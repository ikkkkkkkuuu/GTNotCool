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

        String species = BeeBreedingHelper.getBeeSpecies(draggedStack);
        if (species == null || species.isEmpty()) return false;

        setText(species);
        onTextChanged();
        return true;
    }
}
