package com.silvermoon.boxplusplus.client;

import net.minecraft.entity.player.EntityPlayer;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.silvermoon.boxplusplus.common.tileentities.GTMachineBox;
import com.silvermoon.boxplusplus.util.Util;

import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiRecipeButton;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.nei.GTNEIDefaultHandler;

public class BoxNEIHandler {

    public static final BoxNEIHandler instance = new BoxNEIHandler();

    private BoxNEIHandler() {}

    @SubscribeEvent
    public void onUpdateRecipeButtons(GuiRecipeButton.UpdateRecipeButtonsEvent.Post event) {
        if (event.gui instanceof GuiRecipe<?>gui) {
            if (isGuiEligible(gui)) {
                BoxOverlayButton.updateRecipeButtons(gui, event.buttonList);
            }
        }
    }

    private boolean isGuiEligible(GuiRecipe<?> gui) {
        if (gui.firstGui instanceof IMuiScreen muiScreen) {
            ModularScreen screen = muiScreen.getScreen();
            if (screen != null && !screen.isClientOnly()) {
                EntityPlayer player = screen.getSyncManager()
                    .getPlayer();
                return isPlayerBoxEligible(player == null ? BoxClientRoutingContext.player() : player, gui);
            }
        }
        return false;
    }

    private boolean isPlayerBoxEligible(EntityPlayer player, GuiRecipe<?> gui) {
        GTMachineBox box = Util.boxMap.get(player);
        if (box == null || box.getBaseMetaTileEntity()
            .isDead() || box.recipe.islocked) {
            return false;
        }
        return gui.getHandler() instanceof GTNEIDefaultHandler;
    }
}
