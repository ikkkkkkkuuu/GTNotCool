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

        // 获取唯一 UID（而非 unlocalizedName），确保同 unlocalizedName 但不同 UID 的品种区分开
        String uid = BeeBreedingHelper.getBeeUID(draggedStack);
        if (uid == null || uid.isEmpty()) return false;

        // 直接用 UID 设置文本，StringSyncValue 会将其送到 setTargetBeeSpecies
        // 文本框显示 UID，终端区域会显示 getSpeciesDisplayName 转换后的友好名称
        setText(uid);
        onTextChanged();
        return true;
    }
}
