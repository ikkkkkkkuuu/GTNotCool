package com.xyp.gtnc.ae2thing.nei;

import static com.xyp.gtnc.ae2thing.nei.NEI_TH_Config.getConfigValue;
import static com.xyp.gtnc.ae2thing.proxy.ClientProxy.mouseHandlers;
import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.xyp.gtnc.Common.compat.FluidDropCompat;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.api.Constants;
import com.xyp.gtnc.ae2thing.client.gui.GuiWirelessDualInterfaceTerminal;
import com.xyp.gtnc.ae2thing.integration.Mods;
import com.xyp.gtnc.ae2thing.nei.object.OrderStack;
import com.xyp.gtnc.ae2thing.nei.recipes.FluidRecipe;
import com.xyp.gtnc.ae2thing.network.CPacketTransferRecipe;
import com.xyp.gtnc.ae2thing.proxy.ClientProxy;
import com.xyp.gtnc.ae2thing.util.GTUtil;

import appeng.api.AEApi;
import appeng.client.gui.AEBaseGui;
import appeng.container.slot.SlotFake;
import appeng.util.Platform;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.GuiOverlayButton;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class PatternTerminalRecipeTransferHandler implements IOverlayHandler {

    public static final PatternTerminalRecipeTransferHandler INSTANCE = new PatternTerminalRecipeTransferHandler();

    public static final HashSet<String> notOtherSet = new HashSet<>();
    public static final HashSet<String> craftSet = new HashSet<>();

    static {
        notOtherSet.add("smelting");
        notOtherSet.add("brewing");
        craftSet.add("crafting");
        craftSet.add("crafting2x2");
    }

    private static ItemStack findSameItem(ItemStack[] items, ItemStack item, Constants.MouseWheel wheel) {
        for (int i = 0; i < items.length; i++) {
            if (Platform.isSameItemPrecise(item, items[i])) {
                int index = i + wheel.direction;
                return items[index < 0 ? items.length - 1 : index % items.length];
            }
        }
        return null;
    }

    /**
     * 与 {@link #findSameItem} 对应的流体版：在 NEI 候选原料里定位当前样板槽的流体，滚轮切到相邻的等价流体。
     * NEI 里流体原料以 GT {@code ItemFluidDisplay} 呈现，样板槽里则是 AE2FC 的 {@code ItemFluidDrop}，
     * 两者用物品比较永远不相等，所以先统一抽成 {@link FluidStack} 再按流体类型比对。
     */
    private static FluidStack findSameFluid(ItemStack[] items, FluidStack fluid, Constants.MouseWheel wheel) {
        for (int i = 0; i < items.length; i++) {
            FluidStack candidate = extractFluid(items[i]);
            if (candidate != null && candidate.getFluid() == fluid.getFluid()) {
                int index = i + wheel.direction;
                return extractFluid(items[index < 0 ? items.length - 1 : index % items.length]);
            }
        }
        return null;
    }

    /**
     * 从任意流体载体物品里抽出 {@link FluidStack}：GT {@code ItemFluidDisplay}（NEI 原料显示用）、
     * AE2FC 液滴 {@code ItemFluidDrop} 与流体包 {@code ItemFluidPacket}（样板槽内容）。都不是则返回 null。
     */
    private static FluidStack extractFluid(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        if (stack.getItem() instanceof gregtech.common.items.ItemFluidDisplay) {
            if (stack.getTagCompound() == null) return null;
            Fluid fluid = FluidRegistry.getFluid(stack.getItemDamage());
            int amt = (int) stack.getTagCompound()
                .getLong("mFluidDisplayAmount");
            return amt > 0 && fluid != null ? new FluidStack(fluid, amt) : null;
        }
        if (FluidDropCompat.isFluidDrop(stack)) {
            return FluidDropCompat.getFluidStack(stack);
        }
        if (stack.getItem() instanceof ItemFluidPacket) {
            return ItemFluidPacket.getFluidStack(stack);
        }
        return null;
    }

    public PatternTerminalRecipeTransferHandler() {
        mouseHandlers.add((event, overlayButton) -> {
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof AEBaseGui g && overlayButton != null && GuiScreen.isShiftKeyDown()) {
                GuiOverlayButton btn = ClientProxy.getOverlayButton();
                if (btn != null && g.theSlot instanceof SlotFake slot) {
                    ItemStack slotItem = slot.getStack();
                    if (slotItem == null) return false;

                    Constants.MouseWheel wheel = event.scrollAmount == -1 ? Constants.MouseWheel.NEXT
                        : Constants.MouseWheel.PREVIEW;
                    List<PositionedStack> list = btn.handlerRef.handler.getIngredientStacks(btn.handlerRef.recipeIndex);

                    // 样板槽里是流体（液滴/流体包）时走流体切换分支
                    FluidStack slotFluid = extractFluid(slotItem);
                    if (slotFluid != null) {
                        for (PositionedStack stack : list) {
                            FluidStack result = findSameFluid(stack.items, slotFluid, wheel);
                            if (result != null && result.getFluid() != slotFluid.getFluid()) {
                                List<OrderStack<?>> in = new ArrayList<>();
                                List<OrderStack<?>> out = new ArrayList<>();
                                in.add(new OrderStack<>(slotFluid, 0));
                                out.add(new OrderStack<>(result, 0));
                                AE2Thing.proxy.netHandler.sendToServer(
                                    new CPacketTransferRecipe(
                                        in,
                                        out,
                                        shouldCraft(btn.handlerRef.handler),
                                        isShiftKeyDown(),
                                        Constants.NEI_MOUSE_WHEEL));
                                return true;
                            }
                        }
                        return false;
                    }

                    for (PositionedStack stack : list) {
                        ItemStack result = findSameItem(stack.items, slotItem, wheel);
                        if (result != null) {
                            List<OrderStack<?>> in = new ArrayList<>();
                            List<OrderStack<?>> out = new ArrayList<>();
                            in.add(new OrderStack<>(slotItem, 0));
                            out.add(new OrderStack<>(result, 0));
                            AE2Thing.proxy.netHandler.sendToServer(
                                new CPacketTransferRecipe(
                                    in,
                                    out,
                                    shouldCraft(btn.handlerRef.handler),
                                    isShiftKeyDown(),
                                    Constants.NEI_MOUSE_WHEEL));
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        if (firstGui instanceof GuiWirelessDualInterfaceTerminal) {
            boolean priority = ((GuiWirelessDualInterfaceTerminal) firstGui).container.prioritize;
            boolean craft = shouldCraft(recipe);
            List<OrderStack<?>> in;
            in = FluidRecipe.getPackageInputs(recipe, recipeIndex, !craft && priority);
            setSuggestion(craft, recipe, (GuiWirelessDualInterfaceTerminal) firstGui, in);
            List<OrderStack<?>> out = FluidRecipe.getPackageOutputs(recipe, recipeIndex, !notUseOther(recipe));
            AE2Thing.proxy.netHandler.sendToServer(new CPacketTransferRecipe(in, out, craft, shift));
        }
    }

    private void setSuggestion(boolean craft, IRecipeHandler recipe, GuiWirelessDualInterfaceTerminal gui,
        List<OrderStack<?>> in) {
        String suggestion;
        if (craft) {
            com.google.common.base.Optional<ItemStack> molecular = AEApi.instance()
                .definitions()
                .blocks()
                .molecularAssembler()
                .maybeStack(1);
            if (molecular.isPresent()) {
                suggestion = Platform.getItemDisplayName(molecular.get());
            } else {
                suggestion = "";
            }
        } else if (Mods.isGt5UnofficialLoaded() || Mods.isLegacyGt5Loaded()) {
            suggestion = GTUtil.getRecipeName(recipe, in);
        } else {
            suggestion = recipe.getRecipeName();
        }
        if (getConfigValue(ButtonConstants.DUAL_INTERFACE_TERMINAL)) {
            gui.setSearchFieldText(suggestion);
            gui.setHighlightSlot();
        } else {
            gui.setSearchFieldSuggestion(suggestion);
        }
    }

    private boolean notUseOther(IRecipeHandler recipeHandler) {
        TemplateRecipeHandler tRecipe = (TemplateRecipeHandler) recipeHandler;
        return notOtherSet.contains(tRecipe.getOverlayIdentifier());
    }

    private boolean shouldCraft(IRecipeHandler recipeHandler) {
        TemplateRecipeHandler tRecipe = (TemplateRecipeHandler) recipeHandler;
        return craftSet.contains(tRecipe.getOverlayIdentifier());
    }

}
