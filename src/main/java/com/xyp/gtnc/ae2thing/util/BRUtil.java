package com.xyp.gtnc.ae2thing.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.api.AE2ThingAPI;
import com.xyp.gtnc.ae2thing.api.Constants;
import com.xyp.gtnc.ae2thing.client.gui.GuiWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.nei.ButtonConstants;
import com.xyp.gtnc.ae2thing.nei.NEI_TH_Config;
import com.xyp.gtnc.ae2thing.nei.object.OrderStack;
import com.xyp.gtnc.ae2thing.network.CPacketTransferRecipe;

import blockrenderer6343.client.renderer.WorldSceneRenderer;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.recipe.GuiRecipe;

/**
 * Ported from AE2Things: hooks BlockRenderer6343's multiblock structure preview so the "?" (NEI overlay) button dumps
 * every block the structure needs into the pattern terminal's input slots, using a renamed paper as the placeholder
 * output.
 */
public class BRUtil {

    public static final ItemStack paper = new ItemStack(Items.paper);

    private static String multiBlockName = "";

    public interface ITransferHandler {

        ImmutablePair<List<OrderStack<?>>, List<OrderStack<?>>> handler(List<ItemStack> ingredients);
    }

    public static final ITransferHandler handler = ingredients -> {
        String defaultName = StatCollector.translateToLocal("blockrenderer6343.multiblock.structure");
        List<OrderStack<?>> in = new ArrayList<>();
        List<OrderStack<?>> out = new ArrayList<>();
        ItemStack item;
        for (int i = 0; i < ingredients.size(); i++) {
            item = ingredients.get(i);
            // When the "ignore hatches" option is on, drop functional hatch blocks so only the casing/coils are packed.
            if (!((Mods.isGt5UnofficialLoaded() || Mods.isLegacyGt5Loaded())
                && NEI_TH_Config.getConfigValue(ButtonConstants.BLOCK_RENDER)
                && GTUtil.isHatchItem(item))) {
                in.add(new OrderStack<>(item, i));
            }
        }
        try {
            ItemStack object = paper.copy();
            String name = ((GuiRecipe<?>) Minecraft.getMinecraft().currentScreen).getHandler()
                .getRecipeName();
            object.setStackDisplayName(name.equals(defaultName) ? multiBlockName : name);
            out.add(new OrderStack<>(object, 0));
        } catch (Exception ignored) {}
        return new ImmutablePair<>(in, out);
    };

    public static void setMultiBlockName(String name) {
        BRUtil.multiBlockName = name;
    }

    public static String getMultiBlockName() {
        return BRUtil.multiBlockName;
    }

    public static boolean sendToServer(List<ItemStack> ingredients) {
        if (AE2ThingAPI.instance()
            .terminal()
            .isPatternTerminal()) {
            try {
                ImmutablePair<List<OrderStack<?>>, List<OrderStack<?>>> result = handler.handler(ingredients);
                AE2Thing.proxy.netHandler.sendToServer(
                    new CPacketTransferRecipe(
                        result.left,
                        result.right,
                        false,
                        GuiScreen.isShiftKeyDown(),
                        Constants.NEI_BR));
                GuiRecipe<?> currentScreen = (GuiRecipe<?>) Minecraft.getMinecraft().currentScreen;
                Minecraft.getMinecraft()
                    .displayGuiScreen(currentScreen.firstGui);
                fillInterfaceName(currentScreen.firstGui);
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    /**
     * When overlaying a multiblock structure onto the dual interface terminal, autofill the interface name search field
     * with the fixed "Multiblock Structure" name (governed by the same toggle as recipe-name autofill), so the user can
     * jump straight to the interface they named "Multiblock Structure" and drop the pattern onto it.
     */
    private static void fillInterfaceName(GuiContainer firstGui) {
        if (firstGui instanceof GuiWirelessDualInterfaceTerminal gui
            && NEI_TH_Config.getConfigValue(ButtonConstants.DUAL_INTERFACE_TERMINAL)) {
            // #tr sciencenotcool.gui.multiblock_structure
            // # Multiblock Structure
            // # zh_CN 多方块结构
            gui.setSearchFieldText(StatCollector.translateToLocal(NameConst.GUI_MULTIBLOCK_STRUCTURE));
            gui.setHighlightSlot();
        }
    }

    public static List<ItemStack> getIngredients(WorldSceneRenderer renderer) {
        List<ItemStack> ingredients = new ArrayList<>();
        for (long renderedBlock : renderer.renderedBlocks) {
            int x = CoordinatePacker.unpackX(renderedBlock);
            int y = CoordinatePacker.unpackY(renderedBlock);
            int z = CoordinatePacker.unpackZ(renderedBlock);
            Block block = renderer.world.getBlock(x, y, z);
            if (block.equals(Blocks.air)) continue;
            int meta = renderer.world.getBlockMetadata(x, y, z);
            int qty = block.quantityDropped(renderer.world.rand);
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            if (qty != 1) {
                itemStacks.add(new ItemStack(block));
            } else {
                itemStacks = block.getDrops(renderer.world, x, y, z, meta, 0);
            }
            if (itemStacks.isEmpty()) continue;
            boolean added = false;
            for (ItemStack ingredient : ingredients) {
                if (NEIClientUtils.areStacksSameTypeWithNBT(ingredient, itemStacks.get(0))) {
                    ingredient.stackSize++;
                    added = true;
                    break;
                }
            }
            if (!added) ingredients.add(itemStacks.get(0));
        }

        return ingredients;
    }
}
