package com.xyp.gtnc.ae2thing.loader;

import static com.glodblock.github.loader.ItemAndBlockHolder.WIRELESS_INTERFACE_TERM;
import static com.glodblock.github.loader.ItemAndBlockHolder.WIRELESS_PATTERN_TERM;
import static com.xyp.gtnc.ae2thing.loader.ItemAndBlockHolder.ITEM_PATTERN_MODIFIER;
import static com.xyp.gtnc.ae2thing.loader.ItemAndBlockHolder.ITEM_WIRELESS_DUAL_INTERFACE_TERMINAL;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.xyp.gtnc.ae2thing.loader.recipe.WirelessTerminalEnergyRecipe;
import com.xyp.gtnc.ae2thing.loader.recipe.WirelessTerminalQuantumBridgeRecipe;

import appeng.api.AEApi;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeLoader implements Runnable {

    public static final RecipeLoader INSTANCE = new RecipeLoader();

    public static final ItemStack AE2_BLANK_PATTERN = AEApi.instance()
        .definitions()
        .materials()
        .blankPattern()
        .maybeStack(1)
        .get();
    public static final ItemStack AE2_PROCESS_LOG = AEApi.instance()
        .definitions()
        .materials()
        .logicProcessor()
        .maybeStack(1)
        .get();

    @Override
    public void run() {
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                ITEM_PATTERN_MODIFIER.stack(),
                "   ",
                "GPG",
                " L ",
                'G',
                "dyeGreen",
                'P',
                AE2_BLANK_PATTERN,
                'L',
                AE2_PROCESS_LOG));
        GameRegistry.addShapelessRecipe(
            ITEM_WIRELESS_DUAL_INTERFACE_TERMINAL.stack(),
            WIRELESS_INTERFACE_TERM,
            WIRELESS_PATTERN_TERM.stack());
        WirelessTerminalQuantumBridgeRecipe.register(ITEM_WIRELESS_DUAL_INTERFACE_TERMINAL.stack());
        WirelessTerminalEnergyRecipe.register(ITEM_WIRELESS_DUAL_INTERFACE_TERMINAL.stack());
    }
}
