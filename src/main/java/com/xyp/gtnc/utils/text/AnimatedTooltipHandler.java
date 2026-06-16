package com.xyp.gtnc.utils.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.util.map.ItemStackMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
public class AnimatedTooltipHandler {

    public static Map<ItemStack, List<Supplier<String>>> tooltipMap = new ItemStackMap<>(false);
    public static Map<ItemStack, List<Supplier<String>>> tooltipMapShift = new ItemStackMap<>(false);
    public static Map<ItemStack, List<Supplier<String>>> tooltipMapCtrl = new ItemStackMap<>(false);

    public static final String BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY,
        BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET;

    public static final Supplier<String> NEW_LINE;

    @SafeVarargs
    public static Supplier<String> buildTextWithAnimatedEnd(Supplier<String>... texts) {
        if (texts == null || texts.length == 0) {
            throw new IllegalArgumentException("Required at least one text!");
        }
        Supplier<String> prefixText = chain(Arrays.copyOf(texts, texts.length - 1));
        Supplier<String> lastText = texts[texts.length - 1];
        Supplier<String> animatedText = animatedText(
            lastText.get(),
            1,
            80,
            RED,
            GOLD,
            YELLOW,
            GREEN,
            AQUA,
            BLUE,
            LIGHT_PURPLE);
        return chain(prefixText, animatedText);
    }

    @SafeVarargs
    public static Supplier<String> chain(Supplier<String>... parts) {
        return () -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (Supplier<String> text : parts) {
                stringBuilder.append(text.get());
            }
            return stringBuilder.toString();
        };
    }

    public static Supplier<String> text(String text) {
        return () -> text;
    }

    public static Supplier<String> text(String format, Object... args) {
        return () -> String.format(Locale.ROOT, format, args);
    }

    public static Supplier<String> translatedText(String translationKey) {
        return () -> StatCollector.translateToLocal(translationKey);
    }

    public static Supplier<String> translatedText(String translationKey, Object... args) {
        return () -> StatCollector.translateToLocalFormatted(translationKey, args);
    }

    public static Supplier<String> animatedText(String text, int posstep, int delay, String... formattingArray) {
        if (text == null || text.isEmpty() || formattingArray == null || formattingArray.length == 0) return () -> "";

        final int finalDelay = Math.max(delay, 1);
        final int finalPosstep = posstep == 0 ? 1 : posstep;

        return () -> {
            StringBuilder sb = new StringBuilder(text.length() * 3);
            int len = formattingArray.length;
            int offset = (int) ((System.currentTimeMillis() / finalDelay) % len);

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);

                int indexColorArray = Math.floorMod(i * finalPosstep - offset, len);

                sb.append(formattingArray[indexColorArray]);
                sb.append(c);
            }
            return sb.toString();
        };
    }

    public static Supplier<String> animatedText(String format, Object[] args, int posstep, int delay,
        String... formattingArray) {
        return animatedText(String.format(Locale.ROOT, format, args), posstep, delay, formattingArray);
    }

    public static Supplier<String> translatedAnimatedText(String translationKey, int posstep, int delay,
        String... formattingArray) {
        return animatedText(StatCollector.translateToLocal(translationKey), posstep, delay, formattingArray);
    }

    public static Supplier<String> translatedAnimatedText(String translationKey, Object[] args, int posstep, int delay,
        String... formattingArray) {
        return animatedText(
            StatCollector.translateToLocalFormatted(translationKey, args),
            posstep,
            delay,
            formattingArray);
    }

    public static void addOredictTooltip(String oredictName, Supplier<String> tooltip) {
        for (ItemStack item : OreDictionary.getOres(oredictName)) {
            addItemTooltip(item, tooltip);
        }
    }

    public static void addItemTooltip(String modID, String registryName, int meta, Supplier<String> tooltip) {
        Item item = GameRegistry.findItem(modID, registryName);
        if (item == null || tooltip == null) return;
        addItemTooltip(new ItemStack(item, 1, meta), tooltip);
    }

    public static void addItemTooltip(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = tooltipMap.computeIfAbsent(item, k -> new ArrayList<>());
        list.add(tooltip);
    }

    public static void removeItemTooltip(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = tooltipMap.get(item);
        if (list != null) {
            list.removeIf(t -> t.equals(tooltip));
            if (list.isEmpty()) {
                tooltipMap.remove(item);
            }
        }
    }

    public static void clearItemTooltips(ItemStack item) {
        if (item == null) return;
        tooltipMap.remove(item);
    }

    public static void addItemTooltipShift(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = tooltipMapShift.computeIfAbsent(item, k -> new ArrayList<>());
        list.add(tooltip);
    }

    public static void removeItemTooltipShift(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = tooltipMapShift.get(item);
        if (list != null) {
            list.removeIf(t -> t.equals(tooltip));
            if (list.isEmpty()) {
                tooltipMapShift.remove(item);
            }
        }
    }

    public static void clearItemTooltipsShift(ItemStack item) {
        if (item == null) return;
        tooltipMapShift.remove(item);
    }

    public static void addItemTooltipCtrl(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = tooltipMapCtrl.computeIfAbsent(item, k -> new ArrayList<>());
        list.add(tooltip);
    }

    public static void removeItemTooltipCtrl(ItemStack item, Supplier<String> tooltip) {
        if (item == null || tooltip == null) return;
        List<Supplier<String>> list = tooltipMapCtrl.get(item);
        if (list != null) {
            list.removeIf(t -> t.equals(tooltip));
            if (list.isEmpty()) {
                tooltipMapCtrl.remove(item);
            }
        }
    }

    public static void clearItemTooltipsCtrl(ItemStack item) {
        if (item == null) return;
        tooltipMapCtrl.remove(item);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void renderTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;

        List<Supplier<String>> baseTooltips = tooltipMap.get(stack);
        if (baseTooltips != null) {
            for (Supplier<String> tooltip : baseTooltips) {
                String text = tooltip.get();
                if (text != null) {
                    event.toolTip.addAll(Arrays.asList(text.split("\n")));
                }
            }
        }

        List<Supplier<String>> shiftTooltips = tooltipMapShift.get(stack);
        if (shiftTooltips != null && !shiftTooltips.isEmpty()) {
            if (GuiScreen.isShiftKeyDown()) {
                for (Supplier<String> tooltip : shiftTooltips) {
                    String text = tooltip.get();
                    if (text != null) {
                        event.toolTip.addAll(Arrays.asList(text.split("\n")));
                    }
                }
            } else {
                event.toolTip.add(StatCollector.translateToLocal("Tooltip_PressShift"));
            }
        }

        List<Supplier<String>> ctrlTooltips = tooltipMapCtrl.get(stack);
        if (ctrlTooltips != null && !ctrlTooltips.isEmpty()) {
            if (GuiScreen.isCtrlKeyDown()) {
                for (Supplier<String> tooltip : ctrlTooltips) {
                    String text = tooltip.get();
                    if (text != null) {
                        event.toolTip.addAll(Arrays.asList(text.split("\n")));
                    }
                }
            } else {
                event.toolTip.add(StatCollector.translateToLocal("Tooltip_PressCtrl"));
            }
        }
    }

    static {
        AQUA = EnumChatFormatting.AQUA.toString();
        BLACK = EnumChatFormatting.BLACK.toString();
        BLUE = EnumChatFormatting.BLUE.toString();
        BOLD = EnumChatFormatting.BOLD.toString();
        DARK_AQUA = EnumChatFormatting.DARK_AQUA.toString();
        DARK_BLUE = EnumChatFormatting.DARK_BLUE.toString();
        DARK_GRAY = EnumChatFormatting.DARK_GRAY.toString();
        DARK_GREEN = EnumChatFormatting.DARK_GREEN.toString();
        DARK_PURPLE = EnumChatFormatting.DARK_PURPLE.toString();
        DARK_RED = EnumChatFormatting.DARK_RED.toString();
        GOLD = EnumChatFormatting.GOLD.toString();
        GRAY = EnumChatFormatting.GRAY.toString();
        GREEN = EnumChatFormatting.GREEN.toString();
        ITALIC = EnumChatFormatting.ITALIC.toString();
        LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE.toString();
        OBFUSCATED = EnumChatFormatting.OBFUSCATED.toString();
        RED = EnumChatFormatting.RED.toString();
        RESET = EnumChatFormatting.RESET.toString();
        STRIKETHROUGH = EnumChatFormatting.STRIKETHROUGH.toString();
        UNDERLINE = EnumChatFormatting.UNDERLINE.toString();
        WHITE = EnumChatFormatting.WHITE.toString();
        YELLOW = EnumChatFormatting.YELLOW.toString();

        NEW_LINE = () -> "\n";
    }
}
