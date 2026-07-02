package com.xyp.gtnc.ae2thing.nei;

import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.client.gui.GuiWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.nei.recipes.FluidRecipe;

import codechicken.lib.config.ConfigTagParent;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

@SuppressWarnings("unused")
public class NEI_TH_Config implements IConfigureNEI {

    private static final ConfigTagParent tag = NEIClientConfig.global.config;

    @Override
    public void loadConfig() {
        API.registerNEIGuiHandler(new AE2TH_NEIGuiHandler());
        for (String identifier : FluidRecipe.getSupportRecipes()) {
            if (!API.hasGuiOverlayHandler(GuiWirelessDualInterfaceTerminal.class, identifier)) {
                API.registerGuiOverlayHandler(
                    GuiWirelessDualInterfaceTerminal.class,
                    PatternTerminalRecipeTransferHandler.INSTANCE,
                    identifier);
            }
        }
        API.addOption(new BaseToggleButton(ButtonConstants.HISTORY, false));
        API.addOption(new BaseToggleButton(ButtonConstants.INVENTORY_STATE));
        API.addOption(new BaseToggleButton(ButtonConstants.ULTRA_TERMINAL_MODE));
        API.addOption(new BaseToggleButton(ButtonConstants.DUAL_INTERFACE_TERMINAL, false));
        API.addOption(new BaseToggleButton(ButtonConstants.DUAL_INTERFACE_TERMINAL_APPEND_CIRCUIT_DAMAGE));
        // API.addOption(new BaseToggleButton(ButtonConstants.PINNED_BAR)); //remove
        // API.addOption(new BaseToggleButton(ButtonConstants.PINNED_BAR_REMOVE));
        // API.addOption(new BaseToggleButton(ButtonConstants.PINNED_BAR_CRAFTING_STATE));
        API.addOption(new BaseToggleButton(ButtonConstants.CRAFTING_NOTIFICATION));
        API.addOption(new BaseToggleButton(ButtonConstants.NEI_CRAFT_ITEM));
        if (Mods.PROGRAMMABLE_HATCHES.isModLoaded()) {
            API.addOption(new BaseToggleButton(ButtonConstants.DUAL_INTERFACE_TERMINAL_FILL_CIRCUIT, false));
        }
        if (Mods.BLOCK_RENDERER.isModLoaded()) {
            API.addOption(new BaseToggleButton(ButtonConstants.BLOCK_RENDER));
        }
    }

    public static boolean getConfigValue(String identifier) {
        return tag.getTag(identifier)
            .getBooleanValue(true);
    }

    @Override
    public String getName() {
        return AE2Thing.NAME;
    }

    @Override
    public String getVersion() {
        return AE2Thing.VERSION;
    }
}
