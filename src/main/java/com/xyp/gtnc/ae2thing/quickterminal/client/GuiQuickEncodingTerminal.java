package com.xyp.gtnc.ae2thing.quickterminal.client;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.glodblock.github.client.gui.GuiFCImgButton;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.ae2thing.AE2Thing;
import com.xyp.gtnc.ae2thing.client.gui.widget.CompactItemTabButton;
import com.xyp.gtnc.ae2thing.coremod.mixin.ae.AccessorGuiScrollbar;
import com.xyp.gtnc.ae2thing.inventory.gui.GuiType;
import com.xyp.gtnc.ae2thing.nei.QuickTerminalRecipeTransferHandler;
import com.xyp.gtnc.ae2thing.network.CPacketSwitchGuis;
import com.xyp.gtnc.ae2thing.network.CPacketTeleportToInterface;
import com.xyp.gtnc.ae2thing.network.CPacketToggleInterfaceVisibility;
import com.xyp.gtnc.ae2thing.quickterminal.ContainerQuickEncodingTerminal;
import com.xyp.gtnc.ae2thing.quickterminal.InterfacePatternTarget;
import com.xyp.gtnc.ae2thing.quickterminal.RecipeTransferPayload;

import appeng.api.config.ActionItems;
import appeng.api.config.ItemSubstitution;
import appeng.api.config.PatternBeSubstitution;
import appeng.api.config.PatternSlotConfig;
import appeng.api.config.PinsRows;
import appeng.api.config.Settings;
import appeng.api.config.StringOrder;
import appeng.api.config.TerminalStyle;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import appeng.client.gui.IInterfaceTerminalPostUpdate;
import appeng.client.gui.ScreenColor;
import appeng.client.gui.implementations.GuiInterfaceTerminal;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.slots.VirtualMEMonitorableSlot;
import appeng.client.gui.slots.VirtualMEPatternSlot;
import appeng.client.gui.slots.VirtualMEPinSlot;
import appeng.client.gui.slots.VirtualMESlot;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.client.gui.widgets.TypeToggleButton;
import appeng.client.me.ItemRepo;
import appeng.client.texture.ExtraBlockTextures;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotPatternTerm;
import appeng.container.slot.SlotRestrictedInput;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.core.sync.packets.PacketInterfaceTerminalUpdate.PacketEntry;

/**
 * AE2Things-style single page: ME inventory on the left, the native interface
 * terminal in the centre, and the native pattern encoder on the right.
 */
public final class GuiQuickEncodingTerminal extends GuiPatternTerm implements IInterfaceTerminalPostUpdate {

    private static final int ITEM_PANEL_WIDTH = 101;
    private static final int BUTTON_COLUMN_WIDTH = 18;
    private static final int ITEM_PANEL_X = -ITEM_PANEL_WIDTH - BUTTON_COLUMN_WIDTH;
    private static final int BUTTON_COLUMN_X = -BUTTON_COLUMN_WIDTH;
    private static final int PATTERN_PANEL_X = 209;
    private static final int PATTERN_PANEL_WIDTH = 101;
    private static final int PATTERN_PANEL_TOP = -12;
    private static final int CRAFTING_PANEL_TEXTURE_HEIGHT = 196;
    private static final int PROCESSING_PANEL_TEXTURE_HEIGHT = 255;
    private static final int VIEW_CELL_PANEL_GAP = 4;
    private static final int VIEW_CELL_PANEL_HEIGHT = 24;
    private static final int VIEW_CELL_SLOT_X = 5;
    private static final int VIEW_CELL_SLOT_Y = 4;
    private static final int PATTERN_PROCESSING_GRID_X = 15;
    private static final int PATTERN_PROCESSING_GRID_Y = 11;
    private static final int PATTERN_PROCESSING_OUTPUT_GRID_Y = 106;
    private static final int PATTERN_CRAFTING_GRID_X = 24;
    private static final int PATTERN_CRAFTING_GRID_Y = 20;
    private static final int PATTERN_CRAFTING_OUTPUT_Y = 98;
    private static final int PATTERN_BLANK_SLOT_X = 20;
    private static final int PATTERN_ENCODE_BUTTON_X = 42;
    private static final int PATTERN_ENCODED_SLOT_X = 64;
    private static final int CRAFTING_PATTERN_ROW_Y = 139;
    private static final int PROCESSING_PATTERN_ROW_Y = 198;
    private static final int CRAFTING_TAB_Y = 167;
    private static final int PROCESSING_TAB_Y = 226;
    private static final int ITEM_COLUMNS = 4;
    private static final int DEFAULT_ITEM_ROWS = 4;
    private static final int DEFAULT_STORAGE_BUTTONS_PER_COLUMN = 4;
    private static final int GL_CLIENT_ALL_ATTRIB_BITS = 0xFFFFFFFF;

    private static final ResourceLocation ITEM_PANEL = new ResourceLocation(
        ScienceNotCool.MODID,
        "textures/gui/quick_terminal/items.png");
    private static final ResourceLocation VIEW_CELL_PANEL = new ResourceLocation(
        ScienceNotCool.MODID,
        "textures/gui/quick_terminal/view_cells.png");
    private static final ResourceLocation PATTERN_PANEL = new ResourceLocation(
        ScienceNotCool.MODID,
        "textures/gui/quick_terminal/encoding.png");
    private static final ResourceLocation PATTERN_PANEL_4X4 = new ResourceLocation(
        ScienceNotCool.MODID,
        "textures/gui/quick_terminal/encoding4.png");

    private static final Field ITEM_REPO = findField(GuiMEMonitorable.class, "repo");
    private static final Field TYPE_TOGGLE_BUTTONS = findField(GuiMEMonitorable.class, "typeToggleButtons");
    private static final Field PATTERN_GUI_CRAFTING_MODE = findField(GuiPatternTerm.class, "craftingMode");

    private final ContainerQuickEncodingTerminal patternContainer;
    private final EmbeddedInterfaceTerminal interfaceTerminal;
    private final List<GuiButton> interfaceButtons = new ArrayList<>();
    private final List<GuiButton> storageButtons = new ArrayList<>();
    private final List<GuiButton> patternButtons = new ArrayList<>();

    private ItemRepo itemRepo;
    private int itemRows = DEFAULT_ITEM_ROWS;
    private int pinDisplayRows;
    private int visibleItemSlots = DEFAULT_ITEM_ROWS * ITEM_COLUMNS;
    private int itemPanelHeight = panelHeight(DEFAULT_ITEM_ROWS);
    private int itemPanelY;
    private VirtualMEPinSlot[] visiblePinSlots = new VirtualMEPinSlot[0];
    private TerminalStyle configuredTerminalStyle;
    private GuiFCImgButton combineEnabledButton;
    private GuiFCImgButton combineDisabledButton;
    private GuiFCImgButton prioritizeEnabledButton;
    private GuiFCImgButton prioritizeDisabledButton;
    private GuiImgButton pinStateButton;
    private ModeButton craftingModeButton;
    private ModeButton processing4ModeButton;
    private CompactItemTabButton switchToCraftingButton;
    private GuiTabButton craftingStatusButton;
    private RightEncodingButton encodeButton;
    private boolean initializedOnce;
    private String pendingInterfaceSearch;
    private int pendingTargetSelectionTicks;
    private boolean pendingAutoPlace;
    private long suppressExtensionClickUntil;
    private int suppressExtensionButton = -1;
    private boolean draggingInterfaceScrollBar;

    public GuiQuickEncodingTerminal(InventoryPlayer inventoryPlayer, ITerminalHost host) {
        this(inventoryPlayer, host, new ContainerQuickEncodingTerminal(inventoryPlayer, host));
    }

    private GuiQuickEncodingTerminal(InventoryPlayer inventoryPlayer, ITerminalHost host,
        ContainerQuickEncodingTerminal container) {
        super(inventoryPlayer, host, container);
        patternContainer = container;
        interfaceTerminal = new EmbeddedInterfaceTerminal(container);
    }

    @Override
    public void initGui() {
        super.initGui();

        int oldLeft = guiLeft;
        int oldTop = guiTop;
        List<GuiButton> nativeButtons = new ArrayList<>(buttonList);
        ensureRegisteredTypeButtons(nativeButtons, oldLeft, oldTop);

        interfaceTerminal.initialize(mc, width, height);
        guiLeft = interfaceTerminal.left();
        guiTop = interfaceTerminal.top();
        xSize = interfaceTerminal.guiWidth();
        ySize = interfaceTerminal.guiHeight();
        updateItemGeometry();

        buttonList.clear();
        classifyNativeButtons(nativeButtons, oldLeft, oldTop);
        placeRegisteredTypeButtonsFirst();
        buttonList.addAll(storageButtons);
        buttonList.addAll(patternButtons);

        interfaceButtons.clear();
        for (GuiButton button : interfaceTerminal.buttons()) {
            // The embedded terminal's tab buttons include Crafting Status and
            // "return to original GUI". They do not belong in the compact
            // settings column and were the source of the odd long button.
            if (button instanceof GuiImgButton) interfaceButtons.add(button);
        }
        buttonList.addAll(interfaceButtons);

        // #tr sciencenotcool.terminal.quick.mode.crafting
        // # Crafting Pattern
        // # zh_CN 合成样板
        craftingModeButton = createModeButton(Blocks.crafting_table, "sciencenotcool.terminal.quick.mode.crafting");
        // #tr sciencenotcool.terminal.quick.mode.processing_4x4
        // # 4x4 Processing Pattern
        // # zh_CN 4x4 处理样板
        processing4ModeButton = createModeButton(Blocks.dispenser, "sciencenotcool.terminal.quick.mode.processing_4x4");
        encodeButton = new RightEncodingButton();
        patternButtons.add(craftingModeButton);
        patternButtons.add(processing4ModeButton);
        patternButtons.add(encodeButton);
        buttonList.add(craftingModeButton);
        buttonList.add(processing4ModeButton);
        buttonList.add(encodeButton);

        // #tr sciencenotcool.tooltip.switch_to_crafting_terminal
        // # Switch to Wireless Crafting Terminal
        // # zh_CN 切换到无线合成终端
        switchToCraftingButton = new CompactItemTabButton(
            new ItemStack(Blocks.crafting_table),
            StatCollector.translateToLocal("sciencenotcool.tooltip.switch_to_crafting_terminal"),
            itemRender);
        buttonList.add(switchToCraftingButton);

        craftingStatusButton = new GuiTabButton(0, 0, 2 + 11 * 16, GuiText.CraftingStatus.getLocal(), itemRender);
        craftingStatusButton.setHideEdge(13);
        buttonList.add(craftingStatusButton);

        combineEnabledButton = createFluidCraftingButton("FORCE_COMBINE", "DO_COMBINE");
        combineDisabledButton = createFluidCraftingButton("NOT_COMBINE", "DONT_COMBINE");
        prioritizeEnabledButton = createFluidCraftingButton("FORCE_PRIO", "DO_PRIO");
        prioritizeDisabledButton = createFluidCraftingButton("NOT_PRIO", "DONT_PRIO");
        buttonList.add(combineEnabledButton);
        buttonList.add(combineDisabledButton);
        buttonList.add(prioritizeEnabledButton);
        buttonList.add(prioritizeDisabledButton);

        configureItemPanel();
        layoutButtons();
        layoutPatternSlots();
        layoutContainerSlots();

        // Returning from NEI or another GUI calls initGui again on the same
        // screen. Focus only the first initialization so recipe hotkeys such as
        // R and A are not consumed when the player comes back.
        searchField.setFocused(false);
        if (initializedOnce) {
            interfaceTerminal.clearSearchFocus();
        } else {
            initializedOnce = true;
            interfaceTerminal.focusNameSearch();
        }
    }

    private void classifyNativeButtons(List<GuiButton> buttons, int oldLeft, int oldTop) {
        storageButtons.clear();
        patternButtons.clear();
        pinStateButton = null;

        for (GuiButton button : buttons) {
            if (button.xPosition < oldLeft) {
                storageButtons.add(button);
            } else if (button instanceof GuiTabButton && button.yPosition > oldTop + 20) {
                // Replaced below with three always-visible, directly selectable
                // mode buttons: crafting and the terminal's single 4x4 processing mode.
                continue;
            } else if (button instanceof GuiImgButton image && isPatternButton(image)) {
                // Keep AE2's native Encode Pattern button, but discard its
                // native slot-direction button. The explicit action below is
                // required for this combined container's client/server sync.
                Enum<?> value = image.getCurrentValue();
                if (value != ActionItems.ENCODE && !(value instanceof PatternSlotConfig)) patternButtons.add(button);
            } else if (button instanceof GuiImgButton image) {
                // The pin-state control is placed on the right by AE2, while all
                // other monitor controls start on the left. They still belong
                // to the same ME Storage control group on this combined GUI.
                if (image.getCurrentValue() == ActionItems.PINS) pinStateButton = image;
                storageButtons.add(button);
            }
        }
    }

    /** Keeps third-party AE stack types such as Thaumic Energistics essentia visible in the compact storage column. */
    @SuppressWarnings("unchecked")
    private void ensureRegisteredTypeButtons(List<GuiButton> buttons, int oldLeft, int oldTop) {
        Object value = readField(this, TYPE_TOGGLE_BUTTONS);
        if (!(value instanceof Map<?, ?>rawMap) || typeFilters == null) return;

        Map<TypeToggleButton, IAEStackType<?>> toggleButtons = (Map<TypeToggleButton, IAEStackType<?>>) rawMap;
        int typeIndex = 0;
        for (IAEStackType<?> type : AEStackTypeRegistry.getSortedTypes()) {
            if (toggleButtons.containsValue(type)) {
                typeIndex++;
                continue;
            }
            if (type.getButtonTexture() == null || type.getButtonIcon() == null) continue;

            TypeToggleButton button = new TypeToggleButton(
                oldLeft - 36,
                oldTop + 8 + typeIndex * 20,
                type.getButtonTexture(),
                type.getButtonIcon(),
                type.getDisplayName());
            button.setEnabled(typeFilters.isEnabled(type));
            toggleButtons.put(button, type);
            buttons.add(button);
            typeIndex++;
        }
    }

    /**
     * Keeps every registered type toggle, including essentia, in the visible first positions of the storage controls.
     */
    @SuppressWarnings("unchecked")
    private void placeRegisteredTypeButtonsFirst() {
        Object value = readField(this, TYPE_TOGGLE_BUTTONS);
        if (!(value instanceof Map<?, ?>rawMap)) return;
        Map<TypeToggleButton, IAEStackType<?>> toggleButtons = (Map<TypeToggleButton, IAEStackType<?>>) rawMap;
        List<GuiButton> ordered = new ArrayList<>();
        for (IAEStackType<?> type : AEStackTypeRegistry.getSortedTypes()) {
            for (Map.Entry<TypeToggleButton, IAEStackType<?>> entry : toggleButtons.entrySet()) {
                if (entry.getValue() == type) {
                    ordered.add(entry.getKey());
                    break;
                }
            }
        }
        storageButtons.removeAll(ordered);
        storageButtons.addAll(0, ordered);
    }

    private ModeButton createModeButton(net.minecraft.block.Block icon, String translationKey) {
        return new ModeButton(new ItemStack(icon), StatCollector.translateToLocal(translationKey), itemRender);
    }

    private static GuiFCImgButton createFluidCraftingButton(String icon, String tooltip) {
        GuiFCImgButton button = new GuiFCImgButton(0, 0, icon, tooltip);
        button.setHalfSize(true);
        return button;
    }

    private static boolean isPatternButton(GuiImgButton button) {
        Enum<?> value = button.getCurrentValue();
        return value == ActionItems.ENCODE || value == ActionItems.CLOSE
            || value == ActionItems.DOUBLE
            || value instanceof ItemSubstitution
            || value instanceof PatternBeSubstitution
            || value instanceof PatternSlotConfig;
    }

    private void configureItemPanel() {
        itemRepo = readItemRepo();
        if (itemRepo != null) itemRepo.setRowSize(ITEM_COLUMNS);
        ensureItemPanelSlots();

        layoutPinSlots();
        if (monitorableSlots != null) {
            int visiblePinSlotCount = pinDisplayRows * ITEM_COLUMNS;
            for (int i = 0; i < monitorableSlots.length; i++) {
                VirtualMEMonitorableSlot slot = monitorableSlots[i];
                boolean visible = i < visibleItemSlots;
                slot.setHidden(!visible);
                if (visible) {
                    int panelIndex = visiblePinSlotCount + i;
                    slot.setX(ITEM_PANEL_X + 5 + panelIndex % ITEM_COLUMNS * 18);
                    slot.setY(itemPanelY + 18 + panelIndex / ITEM_COLUMNS * 18);
                }
            }
        }

        searchField.x = guiLeft + ITEM_PANEL_X + 3;
        searchField.y = guiTop + itemPanelY + 4;
        updateItemScrollBar();
        getScrollBar().setVisible(false);
    }

    private void ensureItemPanelSlots() {
        if (itemRepo == null || monitorableSlots == null) return;
        int requiredSlots = visibleItemSlots;
        if (monitorableSlots.length >= requiredSlots) return;

        int oldLength = monitorableSlots.length;
        monitorableSlots = Arrays.copyOf(monitorableSlots, requiredSlots);
        for (int i = oldLength; i < requiredSlots; i++) {
            VirtualMEMonitorableSlot slot = new VirtualMEMonitorableSlot(
                0,
                0,
                itemRepo,
                i,
                type -> typeFilters == null || typeFilters.isEnabled(type));
            monitorableSlots[i] = slot;
            registerVirtualSlots(slot);
        }
    }

    /**
     * The server still allocates pins in AE2's native groups of nine, while this
     * terminal exposes independently configured visual rows of four. Only the
     * requested prefix of each crafting/player section is visible; surplus
     * native capacity remains hidden and available to AE2's original backend.
     */
    private void layoutPinSlots() {
        if (pinSlots == null || pinSlots.length == 0) {
            visiblePinSlots = new VirtualMEPinSlot[0];
            return;
        }

        int craftingLimit = Math.max(0, patternContainer.craftingPinRowsSync.get()) * ITEM_COLUMNS;
        int playerLimit = Math.max(0, patternContainer.playerPinRowsSync.get()) * ITEM_COLUMNS;
        int craftingIndex = 0;
        int playerIndex = 0;
        List<VirtualMEPinSlot> visible = new ArrayList<>();
        for (VirtualMEPinSlot slot : pinSlots) {
            int sectionIndex = slot.isCraftingSlot() ? craftingIndex++ : playerIndex++;
            int sectionLimit = slot.isCraftingSlot() ? craftingLimit : playerLimit;
            boolean show = sectionIndex < sectionLimit;
            slot.setHidden(!show);
            if (!show) continue;

            int panelIndex = visible.size();
            slot.setX(ITEM_PANEL_X + 5 + panelIndex % ITEM_COLUMNS * 18);
            slot.setY(itemPanelY + 18 + panelIndex / ITEM_COLUMNS * 18);
            visible.add(slot);
        }
        visiblePinSlots = visible.toArray(new VirtualMEPinSlot[visible.size()]);
    }

    private int countPinDisplayRows() {
        return Math.max(0, patternContainer.craftingPinRowsSync.get())
            + Math.max(0, patternContainer.playerPinRowsSync.get());
    }

    private void updateItemScrollBar() {
        int size = itemRepo == null ? 0 : itemRepo.size();
        int scrollRows = Math.max(1, itemRows - pinDisplayRows);
        getScrollBar().setLeft(ITEM_PANEL_X + ITEM_PANEL_WIDTH - 20)
            .setTop(itemPanelY + 18 + pinDisplayRows * 18)
            .setHeight(scrollRows * 18 - 2)
            .setRange(
                0,
                Math.max(0, (size - visibleItemSlots + ITEM_COLUMNS - 1) / ITEM_COLUMNS),
                Math.max(1, scrollRows / 6));
    }

    private void layoutButtons() {
        // Interface and storage have independent controls. Interface controls
        // keep their original column beside the central terminal.
        int interfaceColumnX = guiLeft + BUTTON_COLUMN_X;
        int y = guiTop;
        for (GuiButton button : interfaceButtons) {
            button.xPosition = interfaceColumnX;
            button.yPosition = y;
            y += 18;
        }
        if (switchToCraftingButton != null) {
            switchToCraftingButton.xPosition = interfaceColumnX;
            switchToCraftingButton.yPosition = y;
        }
        if (craftingStatusButton != null) {
            craftingStatusButton.xPosition = guiLeft + PATTERN_PANEL_X - 25;
            craftingStatusButton.yPosition = guiTop - 4;
        }

        // Storage controls start immediately to the left of ME Storage. Keep
        // no more than four in a vertical strip; additional controls form new
        // strips further to the left, as in AE2Things' item panel.
        int storageColumnX = guiLeft + ITEM_PANEL_X - BUTTON_COLUMN_WIDTH;
        int storageTop = guiTop + itemPanelY;
        int storageButtonsPerColumn = configuredTerminalStyle == TerminalStyle.TALL
            ? Math.max(DEFAULT_STORAGE_BUTTONS_PER_COLUMN, itemPanelHeight / 18)
            : DEFAULT_STORAGE_BUTTONS_PER_COLUMN;
        for (int i = 0; i < storageButtons.size(); i++) {
            GuiButton button = storageButtons.get(i);
            int column = i / storageButtonsPerColumn;
            int row = i % storageButtonsPerColumn;
            button.xPosition = storageColumnX - column * BUTTON_COLUMN_WIDTH;
            button.yPosition = storageTop + row * 18;
        }

        int right = guiLeft + PATTERN_PANEL_X;
        int panelTop = guiTop + patternPanelY();
        for (GuiButton button : patternButtons) {
            if (button instanceof GuiTabButton) {
                button.xPosition = right + (button == craftingModeButton ? 25 : 55);
                button.yPosition = panelTop + patternTabY();
                continue;
            }
            GuiImgButton image = (GuiImgButton) button;
            Enum<?> value = image.getCurrentValue();
            boolean centralGrid = patternContainer.isCraftingMode();
            int controlX = 13;
            int controlY = centralGrid ? 75 : 84;
            if (value == ActionItems.ENCODE) {
                button.xPosition = right + PATTERN_ENCODE_BUTTON_X;
                button.yPosition = panelTop + patternRowY();
            } else if (value == ActionItems.CLOSE) {
                button.xPosition = right + controlX;
                button.yPosition = panelTop + controlY;
            } else if (value == ActionItems.DOUBLE) {
                button.xPosition = right + controlX + 10;
                button.yPosition = panelTop + controlY;
            } else if (value instanceof ItemSubstitution) {
                button.xPosition = right + controlX + 20;
                button.yPosition = panelTop + controlY;
            } else if (value instanceof PatternBeSubstitution) {
                button.xPosition = right + controlX + 66;
                button.yPosition = panelTop + controlY;
            }
        }
        int processingControlY = panelTop + 84;
        layoutFluidCraftingButtonPair(combineEnabledButton, combineDisabledButton, right + 59, processingControlY);
        layoutFluidCraftingButtonPair(
            prioritizeEnabledButton,
            prioritizeDisabledButton,
            right + 69,
            processingControlY);
    }

    private static void layoutFluidCraftingButtonPair(GuiButton enabled, GuiButton disabled, int x, int y) {
        if (enabled != null) {
            enabled.xPosition = x;
            enabled.yPosition = y;
        }
        if (disabled != null) {
            disabled.xPosition = x;
            disabled.yPosition = y;
        }
    }

    private void layoutPatternSlots() {
        boolean crafting = patternContainer.isCraftingMode();
        synchronizeNativeCraftingMode(crafting);
        int panelY = patternPanelY();
        if (craftingSlots != null) {
            for (int i = 0; i < craftingSlots.length; i++) {
                VirtualMEPatternSlot slot = craftingSlots[i];
                if (crafting) {
                    slot.setHidden(i >= 9);
                    if (i < 9) {
                        slot.setX(PATTERN_PANEL_X + PATTERN_CRAFTING_GRID_X + i % 3 * 18);
                        slot.setY(panelY + PATTERN_CRAFTING_GRID_Y + i / 3 * 18);
                    }
                } else {
                    int x = i % 4;
                    int y = i / 4;
                    slot.setHidden(i >= RecipeTransferPayload.SLOT_COUNT);
                    slot.setX(PATTERN_PANEL_X + PATTERN_PROCESSING_GRID_X + x * 18);
                    slot.setY(panelY + PATTERN_PROCESSING_GRID_Y + y * 18);
                }
                slot.setShowAmount(!crafting);
            }
        }
        if (outputSlots != null) {
            for (int i = 0; i < outputSlots.length; i++) {
                VirtualMEPatternSlot slot = outputSlots[i];
                int x = i % 4;
                int y = i / 4;
                slot.setHidden(crafting || i >= RecipeTransferPayload.SLOT_COUNT);
                slot.setX(PATTERN_PANEL_X + PATTERN_PROCESSING_GRID_X + x * 18);
                slot.setY(panelY + PATTERN_PROCESSING_OUTPUT_GRID_Y + y * 18);
            }
        }

        if (craftingModeButton != null) craftingModeButton.setSelected(crafting);
        if (processing4ModeButton != null) processing4ModeButton.setSelected(!crafting);
        for (GuiButton button : patternButtons) {
            if (!(button instanceof GuiImgButton image)) continue;
            Enum<?> value = image.getCurrentValue();
            if (value == ActionItems.DOUBLE) button.visible = !crafting;
            if (value instanceof PatternSlotConfig) button.visible = !crafting;
            if (value == ItemSubstitution.ENABLED) button.visible = patternContainer.substituteSync.get();
            if (value == ItemSubstitution.DISABLED) button.visible = !patternContainer.substituteSync.get();
            if (value == PatternBeSubstitution.ENABLED) button.visible = patternContainer.beSubstituteSync.get();
            if (value == PatternBeSubstitution.DISABLED) button.visible = !patternContainer.beSubstituteSync.get();
        }
        if (combineEnabledButton != null)
            combineEnabledButton.visible = !crafting && patternContainer.isCombineEnabled();
        if (combineDisabledButton != null)
            combineDisabledButton.visible = !crafting && !patternContainer.isCombineEnabled();
        if (prioritizeEnabledButton != null)
            prioritizeEnabledButton.visible = !crafting && patternContainer.isPrioritizeFluidsEnabled();
        if (prioritizeDisabledButton != null)
            prioritizeDisabledButton.visible = !crafting && !patternContainer.isPrioritizeFluidsEnabled();
    }

    private void synchronizeNativeCraftingMode(boolean crafting) {
        if (PATTERN_GUI_CRAFTING_MODE == null) return;
        try {
            PATTERN_GUI_CRAFTING_MODE.setBoolean(this, crafting);
        } catch (IllegalAccessException ignored) {}
    }

    public void transferRecipe(RecipeTransferPayload payload, String interfaceSearch) {
        // NEI invokes the overlay while its recipe screen is current, then
        // reinitializes this terminal when returning. Defer all interface-list
        // work until drawScreen runs on the restored terminal; otherwise AE2's
        // init replaces both the search value and the selected entry.
        interfaceTerminal.clearHighlight();
        if (interfaceSearch != null) pendingInterfaceSearch = interfaceSearch;
        pendingTargetSelectionTicks = 0;
        pendingAutoPlace = payload.shouldEncode();
        patternContainer.requestRecipeTransfer(payload);
        layoutPatternExtension();
    }

    private void layoutPatternExtension() {
        layoutButtons();
        layoutPatternSlots();
        layoutContainerSlots();
    }

    public boolean shouldCombine() {
        return patternContainer.isCombineEnabled();
    }

    public boolean shouldPrioritizeFluids() {
        return patternContainer.isPrioritizeFluidsEnabled();
    }

    public IAEStack<?> getHoveredRecipeInput() {
        VirtualMESlot hovered = getVirtualMESlotUnderMouse();
        if (hovered == null || craftingSlots == null) return null;
        for (VirtualMEPatternSlot input : craftingSlots) {
            if (input == hovered && !input.isHidden()) return input.getAEStack();
        }
        return null;
    }

    public boolean isCraftingEncodingMode() {
        return patternContainer.isCraftingMode();
    }

    public void replaceRecipeIngredient(IAEStack<?> from, IAEStack<?> to) {
        patternContainer.requestRecipeIngredientReplacement(from, to);
    }

    public void showProcessingLimit(boolean inputs) {
        if (mc.thePlayer == null) return;
        if (inputs) {
            // #tr sciencenotcool.terminal.quick.too_many_processing_inputs
            // # Processing pattern has more than 16 inputs
            // # zh_CN 处理样板的输入超过 16 项
            mc.thePlayer.addChatMessage(
                new ChatComponentTranslation("sciencenotcool.terminal.quick.too_many_processing_inputs"));
        } else {
            // #tr sciencenotcool.terminal.quick.too_many_processing_outputs
            // # Processing pattern has more than 16 outputs
            // # zh_CN 处理样板的输出超过 16 项
            mc.thePlayer.addChatMessage(
                new ChatComponentTranslation("sciencenotcool.terminal.quick.too_many_processing_outputs"));
        }
    }

    private void layoutContainerSlots() {
        int panelY = patternPanelY();
        for (Object value : inventorySlots.inventorySlots) {
            if (value instanceof AppEngSlot slot && slot.isPlayerSide()) {
                // GuiInterfaceTerminal changes height according to GUI scale. Its
                // own container starts the player inventory at x=14 and applies
                // this exact dynamic y transform; mirror that on our shared
                // pattern container so the click boxes stay on the texture.
                slot.xDisplayPosition = 14 + slot.getX();
                slot.yDisplayPosition = ySize + slot.getY() - 82;
            } else if (value instanceof SlotRestrictedInput slot
                && slot.getItemType() == SlotRestrictedInput.PlacableItemType.BLANK_PATTERN) {
                    slot.xDisplayPosition = PATTERN_PANEL_X + PATTERN_BLANK_SLOT_X;
                    slot.yDisplayPosition = panelY + patternRowY();
                } else if (value instanceof SlotRestrictedInput slot
                    && slot.getItemType() == SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN) {
                        slot.xDisplayPosition = PATTERN_PANEL_X + PATTERN_ENCODED_SLOT_X;
                        slot.yDisplayPosition = panelY + patternRowY();
                    } else if (value instanceof SlotRestrictedInput slot
                        && slot.getItemType() == SlotRestrictedInput.PlacableItemType.VIEW_CELL) {
                            slot.xDisplayPosition = PATTERN_PANEL_X + VIEW_CELL_SLOT_X + slot.getSlotIndex() * 18;
                            slot.yDisplayPosition = viewCellPanelY() + VIEW_CELL_SLOT_Y;
                        } else if (value instanceof SlotPatternTerm slot) {
                            slot.xDisplayPosition = patternContainer.isCraftingMode() ? PATTERN_PANEL_X + 42 : -9000;
                            slot.yDisplayPosition = panelY + PATTERN_CRAFTING_OUTPUT_Y;
                        }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (configuredTerminalStyle != currentTerminalStyle()) {
            setWorldAndResolution(mc, width, height);
        }
        synchronizeNativePinRows();
        interfaceTerminal.refreshButtons();
        applyPendingRecipeSearch();
        updateItemGeometry();
        configureItemPanel();
        layoutButtons();
        layoutPatternSlots();
        layoutContainerSlots();
        // GuiMEMonitorable may recalculate its own scrollbar immediately before
        // drawing. Suppress that native draw; draw it at our compact panel's
        // coordinates from drawFG instead.
        getScrollBar().setVisible(false);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void synchronizeNativePinRows() {
        int craftingGroups = (Math.max(0, patternContainer.craftingPinRowsSync.get()) * ITEM_COLUMNS + 8) / 9;
        int playerGroups = (Math.max(0, patternContainer.playerPinRowsSync.get()) * ITEM_COLUMNS + 8) / 9;
        // The custom four-wide row values commonly arrive after AE2 has already
        // built its initial nine-wide virtual pin array. Updating AE2's private
        // row state through its public hook rebuilds that array on the first
        // synchronized frame; subsequent calls are no-ops.
        setPinsRows(PinsRows.fromOrdinal(craftingGroups), PinsRows.fromOrdinal(playerGroups));
    }

    private void applyPendingRecipeSearch() {
        if (pendingInterfaceSearch != null) {
            interfaceTerminal.setNameSearchText(pendingInterfaceSearch);
            pendingInterfaceSearch = null;
            pendingTargetSelectionTicks = 60;
            interfaceTerminal.clearSearchFocus();
            searchField.setFocused(false);
            return;
        }
        if (pendingTargetSelectionTicks <= 0) return;

        InterfacePatternTarget target = interfaceTerminal.highlightFirstEmptyPatternSlot();
        if (target == null) {
            pendingTargetSelectionTicks--;
            if (pendingTargetSelectionTicks == 0) pendingAutoPlace = false;
            return;
        }

        pendingTargetSelectionTicks = 0;
        if (pendingAutoPlace) patternContainer.requestPlaceEncodedPattern(target);
        pendingAutoPlace = false;
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        interfaceTerminal.drawCentralBackground(offsetX, offsetY, mouseX, mouseY);
        interfaceTerminal.drawHighlightedPatternSlot(offsetX, offsetY);

        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager()
            .bindTexture(ITEM_PANEL);
        int left = offsetX + ITEM_PANEL_X;
        int top = offsetY + itemPanelY;
        drawTexturedModalRect(left, top, 0, 0, ITEM_PANEL_WIDTH, 18);
        for (int row = 0; row < itemRows; row++) {
            drawTexturedModalRect(left, top + 18 + row * 18, 0, 18, ITEM_PANEL_WIDTH, 18);
        }
        drawTexturedModalRect(left, top + 18 + itemRows * 18, 0, 90, ITEM_PANEL_WIDTH, 6);
        searchField.drawTextBox();

        // MEGuiTextField changes the current GL color. Without restoring white,
        // the encoder is tinted dark until another widget happens to reset it.
        GL11.glColor4f(1, 1, 1, 1);
        boolean crafting = patternContainer.isCraftingMode();
        ResourceLocation texture = crafting ? PATTERN_PANEL : PATTERN_PANEL_4X4;
        mc.getTextureManager()
            .bindTexture(texture);
        int right = offsetX + PATTERN_PANEL_X;
        int patternTop = offsetY + patternPanelY();
        drawTexturedModalRect(right, patternTop, 0, 0, PATTERN_PANEL_WIDTH, patternPanelTextureHeight());
        drawViewCellPanel(offsetX, offsetY);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        currentMouseX = mouseX;
        currentMouseY = mouseY;
        if (visiblePinSlots.length > 0) {
            VirtualMEPinSlot.drawSlotsBackground(visiblePinSlots, mc, zLevel);
        }
        updateItemScrollBar();
        updateFluidCraftingButtonVisibility();
        getScrollBar().setVisible(true);
        getScrollBar().draw(this);
        getScrollBar().setVisible(false);
        interfaceTerminal.drawCentralForeground(offsetX, offsetY, mouseX, mouseY);
        interfaceTerminal.drawCentralScrollBar();
        fontRendererObj.drawString(
            // #tr sciencenotcool.terminal.quick.storage
            // # ME Storage
            // # zh_CN ME 库存
            StatCollector.translateToLocal("sciencenotcool.terminal.quick.storage"),
            ITEM_PANEL_X + 5,
            itemPanelY - 10,
            0x404040);
        fontRendererObj.drawString(
            // #tr sciencenotcool.terminal.quick.encoding
            // # Encoding
            // # zh_CN 编码
            StatCollector.translateToLocal("sciencenotcool.terminal.quick.encoding"),
            PATTERN_PANEL_X + 8,
            patternPanelY() - 10,
            0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == pinStateButton) {
            int craftingRows = patternContainer.craftingPinRowsSync.get();
            int playerRows = patternContainer.playerPinRowsSync.get();
            int change = Mouse.isButtonDown(1) ? -1 : 1;
            if (isCtrlKeyDown()) {
                craftingRows += change;
            } else {
                playerRows += change;
            }
            patternContainer.requestPinRows(craftingRows, playerRows);
            updateItemGeometry();
            configureItemPanel();
            layoutButtons();
            return;
        }
        if (button == craftingModeButton) {
            patternContainer.craftingModeSync.set(true);
            layoutPatternExtension();
            return;
        }
        if (button == processing4ModeButton) {
            patternContainer.requestProcessingGridSize(4);
            patternContainer.craftingModeSync.set(false);
            layoutPatternExtension();
            return;
        }
        if (button == switchToCraftingButton) {
            AE2Thing.proxy.netHandler.sendToServer(new CPacketSwitchGuis(GuiType.WIRELESS_CRAFTING_TERMINAL));
            return;
        }
        if (button == craftingStatusButton) {
            AE2Thing.proxy.netHandler.sendToServer(new CPacketSwitchGuis(GuiType.CRAFTING_STATUS_ITEM));
            return;
        }
        if (button == encodeButton) {
            if (isAltDown() && !isShiftKeyDown() && !isCtrlKeyDown() && interfaceTerminal.highlightedTarget() != null) {
                patternContainer.quickEncodeAction.send();
                patternContainer.requestPlaceEncodedPattern(interfaceTerminal.highlightedTarget());
            } else if (isShiftKeyDown()) {
                patternContainer.quickEncodeAndMoveToInventoryAction.send(isCtrlKeyDown());
            } else {
                patternContainer.quickEncodeAction.send();
            }
            return;
        }
        if (interfaceButtons.contains(button)) {
            boolean changesTerminalStyle = button instanceof GuiImgButton image
                && image.getSetting() == Settings.TERMINAL_STYLE;
            interfaceTerminal.perform(button);
            if (changesTerminalStyle) setWorldAndResolution(mc, width, height);
            return;
        }
        if (button == combineEnabledButton || button == combineDisabledButton) {
            patternContainer.requestCombine(!patternContainer.isCombineEnabled());
            layoutPatternSlots();
            return;
        }
        if (button == prioritizeEnabledButton || button == prioritizeDisabledButton) {
            boolean sortOnly = isShiftKeyDown();
            patternContainer.requestPrioritizeFluids(
                sortOnly ? patternContainer.isPrioritizeFluidsEnabled() : !patternContainer.isPrioritizeFluidsEnabled(),
                sortOnly);
            layoutPatternSlots();
            return;
        }
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (handleFluidCraftingControlClick(mouseX, mouseY, button)) return;
        if (button == 0 && interfaceTerminal.clickScrollBar(mouseX, mouseY)) {
            ((AccessorGuiScrollbar) getScrollBar()).setIsLatestClickOnScrollbar(false);
            draggingInterfaceScrollBar = true;
            return;
        }
        if ((button == 0 || button == 1) && isInsideStoragePanel(mouseX, mouseY)) {
            // The storage panel is deliberately outside xSize. Mark the whole
            // visible panel as GUI space so clicking its search/background never
            // turns a held item into an ordinary "drop outside" action. Actual
            // virtual slots still flow through GuiMEMonitorable below.
            suppressExtensionVanillaClick(button);
        }
        // GuiPatternTerm checks virtual slots before real container slots. In
        // this composite layout the two pattern inventory slots sit outside the
        // native central GUI, so dispatch their real slot click before the
        // virtual-slot handler gets a chance to consume it.
        if (button == 0 || button == 1) {
            for (Object value : inventorySlots.inventorySlots) {
                if (!(value instanceof SlotRestrictedInput slot)) continue;
                if (slot.getItemType() != SlotRestrictedInput.PlacableItemType.BLANK_PATTERN
                    && slot.getItemType() != SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN) continue;
                int x = guiLeft + slot.xDisplayPosition;
                int y = guiTop + slot.yDisplayPosition;
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    int action = button | (isShiftKeyDown() ? 2 : 0);
                    if (slot.getItemType() == SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN) {
                        suppressExtensionVanillaClick(button);
                        patternContainer.takeEncodedPatternAction.send(action);
                        return;
                    }
                    suppressExtensionVanillaClick(button);
                    patternContainer.takeBlankPatternAction.send(action);
                    return;
                }
            }
        }
        if (mouseX >= guiLeft && mouseX < guiLeft + xSize && mouseY >= guiTop && mouseY < guiTop + ySize - 98) {
            if (interfaceTerminal.clickEntryOption(mouseX, mouseY, button)) return;
            InterfacePatternTarget target = interfaceTerminal.patternSlotAt(mouseX, mouseY);
            if (target != null && (button == 0 || button == 1) && !isCtrlKeyDown()) {
                patternContainer.requestInterfacePatternClick(target, isShiftKeyDown());
                return;
            }
            if (interfaceTerminal.click(mouseX, mouseY, button)) return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleFluidCraftingControlClick(int mouseX, int mouseY, int mouseButton) {
        if (patternContainer.isCraftingMode() || mouseButton != 0 && mouseButton != 1) return false;
        if (isOverHalfSizeButton(combineEnabledButton, mouseX, mouseY)) {
            patternContainer.requestCombine(!patternContainer.isCombineEnabled());
            layoutPatternSlots();
            return true;
        }
        if (isOverHalfSizeButton(prioritizeEnabledButton, mouseX, mouseY)) {
            boolean sortOnly = isShiftKeyDown();
            patternContainer.requestPrioritizeFluids(
                sortOnly ? patternContainer.isPrioritizeFluidsEnabled() : !patternContainer.isPrioritizeFluidsEnabled(),
                sortOnly);
            layoutPatternSlots();
            return true;
        }
        return false;
    }

    private static boolean isOverHalfSizeButton(GuiButton button, int mouseX, int mouseY) {
        return button != null && mouseX >= button.xPosition
            && mouseX < button.xPosition + 8
            && mouseY >= button.yPosition
            && mouseY < button.yPosition + 8;
    }

    @Override
    protected boolean handleVirtualSlotClick(VirtualMESlot slot, int mouseButton) {
        boolean handled = super.handleVirtualSlotClick(slot, mouseButton);
        if (handled && slot instanceof VirtualMEMonitorableSlot) suppressExtensionVanillaClick(mouseButton);
        return handled;
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotIdx, int clickedButton, int clickType) {
        boolean extensionDuplicate = slotIdx == -999 || slot instanceof SlotRestrictedInput restricted
            && (restricted.getItemType() == SlotRestrictedInput.PlacableItemType.BLANK_PATTERN
                || restricted.getItemType() == SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN);
        if (extensionDuplicate && clickedButton == suppressExtensionButton
            && System.currentTimeMillis() <= suppressExtensionClickUntil) {
            return;
        }
        super.handleMouseClick(slot, slotIdx, clickedButton, clickType);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        super.mouseMovedOrUp(mouseX, mouseY, state);
        if (draggingInterfaceScrollBar && state == 0) {
            interfaceTerminal.stopDraggingScrollBar();
            draggingInterfaceScrollBar = false;
        }
        if (state == suppressExtensionButton) {
            suppressExtensionButton = -1;
            suppressExtensionClickUntil = 0;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (draggingInterfaceScrollBar && button == 0) {
            interfaceTerminal.dragScrollBar(mouseY);
            return;
        }
        super.mouseClickMove(mouseX, mouseY, button, timeSinceLastClick);
    }

    private void suppressExtensionVanillaClick(int button) {
        suppressExtensionButton = button;
        suppressExtensionClickUntil = System.currentTimeMillis() + 1000;
    }

    private boolean isInsideStoragePanel(int mouseX, int mouseY) {
        int left = guiLeft + ITEM_PANEL_X;
        int top = guiTop + itemPanelY;
        return mouseX >= left && mouseX < left + ITEM_PANEL_WIDTH && mouseY >= top && mouseY < top + itemPanelHeight;
    }

    @Override
    protected boolean mouseWheelEvent(int mouseX, int mouseY, int wheel) {
        if (isShiftKeyDown() && QuickTerminalRecipeTransferHandler.cycleRecipeIngredient(this, wheel)) return true;
        if (isInsideStoragePanel(mouseX, mouseY)) {
            if (super.mouseWheelEvent(mouseX, mouseY, wheel)) return true;
            getScrollBar().wheel(wheel);
            return true;
        }
        if (mouseX >= guiLeft && mouseX < guiLeft + xSize) {
            // AEBaseGui's fallback only checks the central GUI's broad X range,
            // so an unhandled wheel event here would incorrectly move the ME
            // storage scrollbar. Keep the two embedded panels independent.
            interfaceTerminal.wheel(mouseX, mouseY, wheel);
            return true;
        }
        return super.mouseWheelEvent(mouseX, mouseY, wheel);
    }

    private void updateFluidCraftingButtonVisibility() {
        boolean processing = !patternContainer.isCraftingMode();
        if (combineEnabledButton != null)
            combineEnabledButton.visible = processing && patternContainer.isCombineEnabled();
        if (combineDisabledButton != null)
            combineDisabledButton.visible = processing && !patternContainer.isCombineEnabled();
        if (prioritizeEnabledButton != null)
            prioritizeEnabledButton.visible = processing && patternContainer.isPrioritizeFluidsEnabled();
        if (prioritizeDisabledButton != null)
            prioritizeDisabledButton.visible = processing && !patternContainer.isPrioritizeFluidsEnabled();
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (interfaceTerminal.handleSearchKey(character, key)) return;
        super.keyTyped(character, key);
    }

    @Override
    public boolean isOverTextField(int mouseX, int mouseY) {
        return super.isOverTextField(mouseX, mouseY) || interfaceTerminal.isOverTextField(mouseX, mouseY);
    }

    @Override
    public ItemStack getHoveredStack() {
        ItemStack interfaceStack = interfaceTerminal.getHoveredStack();
        return interfaceStack == null ? super.getHoveredStack() : interfaceStack;
    }

    @Override
    public void setTextFieldValue(String displayName, int mouseX, int mouseY, ItemStack stack) {
        if (interfaceTerminal.isOverTextField(mouseX, mouseY)) {
            interfaceTerminal.setSearchFieldValue(displayName, mouseX, mouseY, stack);
        } else {
            super.setTextFieldValue(displayName, mouseX, mouseY, stack);
        }
    }

    @Override
    public void postUpdate(List<PacketEntry> updates, int statusFlags) {
        interfaceTerminal.postUpdate(updates, statusFlags);
    }

    @Override
    public void postUpdate(List<IAEStack<?>> updates) {
        super.postUpdate(updates);
        updateItemScrollBar();
    }

    @Override
    public boolean hideItemPanelSlot(int x, int y, int width, int height) {
        if (intersects(x, y, width, height, guiLeft + BUTTON_COLUMN_X, guiTop, BUTTON_COLUMN_WIDTH, ySize)
            || intersects(
                x,
                y,
                width,
                height,
                guiLeft + ITEM_PANEL_X,
                guiTop + itemPanelY,
                ITEM_PANEL_WIDTH,
                itemPanelHeight)
            || intersects(
                x,
                y,
                width,
                height,
                guiLeft + PATTERN_PANEL_X,
                guiTop + patternPanelY() + PATTERN_PANEL_TOP,
                PATTERN_PANEL_WIDTH,
                patternPanelTotalHeight()))
            return true;

        for (GuiButton button : storageButtons) {
            if (intersects(x, y, width, height, button.xPosition, button.yPosition, button.width, button.height))
                return true;
        }
        return false;
    }

    private void updateItemGeometry() {
        configuredTerminalStyle = currentTerminalStyle();
        pinDisplayRows = countPinDisplayRows();
        int availableRows = Math.max(DEFAULT_ITEM_ROWS, (ySize - 36) / 18);
        itemRows = configuredTerminalStyle == TerminalStyle.TALL ? Math.max(availableRows, pinDisplayRows + 1)
            : Math.max(DEFAULT_ITEM_ROWS, pinDisplayRows + 1);
        visibleItemSlots = Math.max(ITEM_COLUMNS, (itemRows - pinDisplayRows) * ITEM_COLUMNS);
        itemPanelHeight = panelHeight(itemRows);
        itemPanelY = ySize - itemPanelHeight;
    }

    private static int panelHeight(int rows) {
        return 24 + rows * 18;
    }

    private int patternPanelY() {
        return configuredTerminalStyle == TerminalStyle.TALL ? itemPanelY : 0;
    }

    private int patternPanelTextureHeight() {
        return patternContainer.isCraftingMode() ? CRAFTING_PANEL_TEXTURE_HEIGHT : PROCESSING_PANEL_TEXTURE_HEIGHT;
    }

    private int patternPanelTotalHeight() {
        return patternPanelTextureHeight() + VIEW_CELL_PANEL_GAP + VIEW_CELL_PANEL_HEIGHT - PATTERN_PANEL_TOP;
    }

    private int patternRowY() {
        return patternContainer.isCraftingMode() ? CRAFTING_PATTERN_ROW_Y : PROCESSING_PATTERN_ROW_Y;
    }

    private int patternTabY() {
        return patternContainer.isCraftingMode() ? CRAFTING_TAB_Y : PROCESSING_TAB_Y;
    }

    private int viewCellPanelY() {
        return patternPanelY() + patternPanelTextureHeight() + VIEW_CELL_PANEL_GAP;
    }

    private void drawViewCellPanel(int offsetX, int offsetY) {
        int left = offsetX + PATTERN_PANEL_X;
        int top = offsetY + viewCellPanelY();
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager()
            .bindTexture(VIEW_CELL_PANEL);
        drawTexturedModalRect(left, top, 0, 0, PATTERN_PANEL_WIDTH, VIEW_CELL_PANEL_HEIGHT);
    }

    private static TerminalStyle currentTerminalStyle() {
        return (TerminalStyle) AEConfig.instance.settings.getSetting(Settings.TERMINAL_STYLE);
    }

    private static boolean isAltDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    private static boolean intersects(int x, int y, int width, int height, int panelX, int panelY, int panelWidth,
        int panelHeight) {
        return width > 0 && height > 0
            && x < panelX + panelWidth
            && x + width > panelX
            && y < panelY + panelHeight
            && y + height > panelY;
    }

    private ItemRepo readItemRepo() {
        if (ITEM_REPO == null) return null;
        try {
            return (ItemRepo) ITEM_REPO.get(this);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static Field findField(Class<?> owner, String name) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object readField(Object owner, Field field) {
        if (owner == null || field == null) return null;
        try {
            return field.get(owner);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    /** AE2's native Encode Pattern button, rotated so its arrow follows the horizontal pattern flow. */
    private static final class RightEncodingButton extends GuiImgButton {

        private RightEncodingButton() {
            super(0, 0, Settings.ACTIONS, ActionItems.ENCODE);
        }

        @Override
        public String getMessage() {
            return super.getMessage() + "\n"
            // #tr sciencenotcool.terminal.quick.encode_alt_upload
            // # Hold ALT to encode and upload directly
            // # zh_CN 按住 ALT 编码并直接上传
                + StatCollector.translateToLocal("sciencenotcool.terminal.quick.encode_alt_upload");
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (!visible) return;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushClientAttrib(GL_CLIENT_ALL_ATTRIB_BITS);
            try {
                field_146123_n = mouseX >= xPosition && mouseY >= yPosition
                    && mouseX < xPosition + width
                    && mouseY < yPosition + height;
                if (enabled) {
                    ScreenColor.setGuiColor();
                } else {
                    ScreenColor.setDimmedGuiColor();
                }
                minecraft.renderEngine.bindTexture(ExtraBlockTextures.GuiTexture("guis/states.png"));
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                GL11.glPushMatrix();
                try {
                    GL11.glTranslatef(xPosition, yPosition, 0);

                    // Keep AE2's original frame unrotated so its upper/left shadow and
                    // lower/right highlight match every other GUI button.
                    drawTexturedModalRect(0, 0, 240, 240, 16, 16);
                    if (enabled) ScreenColor.resetGuiColor();

                    // ActionItems.ENCODE is icon 8 in states.png. Rotate only that
                    // glyph; the button's bevel and hover area remain unchanged.
                    GL11.glTranslatef(8, 8, 0);
                    GL11.glRotatef(-90, 0, 0, 1);
                    GL11.glTranslatef(-8, -8, 0);
                    drawTexturedModalRect(0, 0, 128, 0, 16, 16);
                } finally {
                    GL11.glPopMatrix();
                }
                mouseDragged(minecraft, mouseX, mouseY);
            } finally {
                ScreenColor.resetGuiColor();
                GL11.glPopClientAttrib();
                GL11.glPopAttrib();
            }
        }
    }

    /**
     * The stock AE2 tab ignores {@link #enabled}, so three adjacent mode tabs
     * otherwise look selected at the same time. Keep the raised AE2 tab for
     * the active mode and use AE2's recessed image-button frame for the two
     * directly selectable alternatives.
     */
    private static final class ModeButton extends GuiTabButton {

        private static final float ICON_SCALE = 0.75F;

        private final ItemStack icon;
        private final RenderItem itemRenderer;
        private boolean selected;

        private ModeButton(ItemStack icon, String message, RenderItem itemRenderer) {
            super(0, 0, icon, message, itemRenderer);
            this.icon = icon;
            this.itemRenderer = itemRenderer;
        }

        private void setSelected(boolean selected) {
            this.selected = selected;
            enabled = !selected;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (!visible) return;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushClientAttrib(GL_CLIENT_ALL_ATTRIB_BITS);
            try {
                field_146123_n = mouseX >= xPosition && mouseY >= yPosition
                    && mouseX < xPosition + width
                    && mouseY < yPosition + height;
                ScreenColor.setGuiColor();
                minecraft.renderEngine.bindTexture(ExtraBlockTextures.GuiTexture("guis/states.png"));
                if (selected) {
                    drawTexturedModalRect(xPosition, yPosition, 208, 0, 25, 22);
                } else {
                    drawTexturedModalRect(xPosition + 3, yPosition + 3, 240, 240, 16, 16);
                }
                ScreenColor.resetGuiColor();

                drawCrispIcon(minecraft);
                mouseDragged(minecraft, mouseX, mouseY);
            } finally {
                RenderHelper.disableStandardItemLighting();
                itemRenderer.zLevel = 0.0F;
                zLevel = 0.0F;
                ScreenColor.resetGuiColor();
                GL11.glPopClientAttrib();
                GL11.glPopAttrib();
            }
        }

        private void drawCrispIcon(Minecraft minecraft) {
            minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            int previousMinFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
            int previousMagFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glPushMatrix();
            try {
                zLevel = 100.0F;
                itemRenderer.zLevel = 100.0F;
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(32826); // GL_RESCALE_NORMAL (LWJGL 2 exposes it through GL12).
                RenderHelper.enableGUIStandardItemLighting();
                float centerX = xPosition + 11.0F;
                float centerY = yPosition + 11.0F;
                GL11.glTranslatef(centerX, centerY, 0.0F);
                GL11.glScalef(ICON_SCALE, ICON_SCALE, 1.0F);
                GL11.glTranslatef(-centerX, -centerY, 0.0F);
                itemRenderer.renderItemAndEffectIntoGUI(
                    minecraft.fontRenderer,
                    minecraft.renderEngine,
                    icon,
                    xPosition + 3,
                    yPosition + 3);
            } finally {
                GL11.glPopMatrix();
                minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, previousMinFilter);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, previousMagFilter);
            }
        }
    }

    private static final class EmbeddedInterfaceTerminal extends GuiInterfaceTerminal {

        private static final int VIEWPORT_LEFT = 10;
        private static final int VIEWPORT_TOP = 52;
        private static final int VIEWPORT_WIDTH = 174;

        private static final Field INPUT_SEARCH = findField(GuiInterfaceTerminal.class, "searchFieldInputs");
        private static final Field OUTPUT_SEARCH = findField(GuiInterfaceTerminal.class, "searchFieldOutputs");
        private static final Field NAME_SEARCH = findField(GuiInterfaceTerminal.class, "searchFieldNames");
        private static final Field MASTER_LIST = findField(GuiInterfaceTerminal.class, "masterList");
        private static final Field ENTRIES_BY_ID = findField(
            MASTER_LIST == null ? GuiInterfaceTerminal.class : MASTER_LIST.getType(),
            "list");
        private static final Field VIEW_HEIGHT = findField(GuiInterfaceTerminal.class, "viewHeight");
        private static final Field EXTRA_OPTIONS_TEXT = findField(GuiInterfaceTerminal.class, "extraOptionsText");

        private Object highlightedEntry;
        private InterfacePatternTarget highlightedTarget;

        private EmbeddedInterfaceTerminal(Container container) {
            super(container);
        }

        private void initialize(Minecraft minecraft, int width, int height) {
            setWorldAndResolution(minecraft, width, height);
            ensureCtrlTeleportTooltip();
        }

        @SuppressWarnings("unchecked")
        private void ensureCtrlTeleportTooltip() {
            Object value = objectField(this, EXTRA_OPTIONS_TEXT);
            if (!(value instanceof List<?>rawList)) return;
            List<String> lines = (List<String>) rawList;
            // #tr sciencenotcool.terminal.quick.interface_ctrl_teleport
            // # Ctrl-click to teleport beside this interface
            // # zh_CN Ctrl+点击传送到该接口旁边
            String tooltip = StatCollector.translateToLocal("sciencenotcool.terminal.quick.interface_ctrl_teleport");
            if (!lines.contains(tooltip)) lines.add(tooltip);
        }

        private List<GuiButton> buttons() {
            return new ArrayList<>(buttonList);
        }

        private int left() {
            return guiLeft;
        }

        private int top() {
            return guiTop;
        }

        private int guiWidth() {
            return xSize;
        }

        private int guiHeight() {
            return ySize;
        }

        private void drawCentralBackground(int x, int y, int mouseX, int mouseY) {
            super.drawBG(x, y, mouseX, mouseY);
        }

        private void drawHighlightedPatternSlot(int offsetX, int offsetY) {
            if (highlightedEntry == null || highlightedTarget == null) return;
            IInventory inventory = entryInventory(highlightedEntry);
            int targetSlot = highlightedTarget.getSlot();
            if (inventory == null || targetSlot < 0 || targetSlot >= inventory.getSizeInventory()) return;

            int rowSize = intField(highlightedEntry, "rowSize", 0);
            int displayY = intField(highlightedEntry, "dispY", -9999);
            int viewHeight = VIEW_HEIGHT == null ? 0 : intField(this, VIEW_HEIGHT, 0);
            if (rowSize <= 0 || displayY <= -9000 || viewHeight <= 0) return;

            int row = targetSlot / rowSize;
            int column = targetSlot % rowSize;
            int relativeY = displayY + row * 18 + 1;
            if (relativeY < 0 || relativeY + 16 > viewHeight) return;

            int x = offsetX + 10 + 174 - rowSize * 18 + column * 18 + 1;
            int y = offsetY + 52 + relativeY;
            drawRainbowBorder(x, y);
        }

        private void drawCentralForeground(int x, int y, int mouseX, int mouseY) {
            super.drawFG(x, y, mouseX, mouseY);
        }

        private void drawCentralScrollBar() {
            getScrollBar().draw(this);
        }

        private boolean click(int mouseX, int mouseY, int button) {
            // This GUI is a renderer/controller embedded in the real current
            // screen. Calling GuiInterfaceTerminal.mouseClicked would fall
            // through to GuiContainer, which assumes `this` is currentScreen
            // and dereferences a null drag state. Dispatch only the interface
            // terminal's own search fields and entry list here; the outer GUI
            // handles real container slots and buttons.
            for (Field search : new Field[] { INPUT_SEARCH, OUTPUT_SEARCH, NAME_SEARCH }) {
                MEGuiTextField field = textField(search);
                if (field != null) field.mouseClicked(mouseX, mouseY, button);
            }
            if (MASTER_LIST == null) return false;
            try {
                Object masterList = MASTER_LIST.get(this);
                Method click = masterList.getClass()
                    .getMethod("mouseClicked", int.class, int.class, int.class);
                click.setAccessible(true);
                return Boolean.TRUE
                    .equals(click.invoke(masterList, mouseX - guiLeft - 10, mouseY - guiTop - 52, button));
            } catch (ReflectiveOperationException ignored) {}
            return false;
        }

        /** Dispatches the per-interface option button before pattern-slot hit testing can consume modified clicks. */
        private boolean clickEntryOption(int mouseX, int mouseY, int button) {
            Object masterList = objectField(this, MASTER_LIST);
            if (masterList == null) return false;
            int relativeX = mouseX - guiLeft - 10;
            int relativeY = mouseY - guiTop - 52;
            Object value = objectField(masterList, ENTRIES_BY_ID);
            if (!(value instanceof Map<?, ?>entries)) return false;
            for (Map.Entry<?, ?> mapEntry : entries.entrySet()) {
                Object entry = mapEntry.getValue();
                Object optionValue = objectField(entry, findField(entry.getClass(), "optionsButton"));
                if (!(optionValue instanceof GuiButton option) || option.yPosition < 0) continue;
                if (relativeX < option.xPosition || relativeX >= option.xPosition + option.width
                    || relativeY <= option.yPosition
                    || relativeY > option.yPosition + option.height) continue;

                long entryId = mapEntry.getKey() instanceof Number number ? number.longValue()
                    : longField(entry, "id", -1);
                boolean ctrlDown = isCtrlKeyDown() || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                    || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
                boolean altDown = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
                if (ctrlDown && entryId >= 0) {
                    option.func_146113_a(mc.getSoundHandler());
                    AE2Thing.proxy.netHandler.sendToServer(new CPacketTeleportToInterface(entryId));
                    return true;
                }
                if (altDown && entryId >= 0) {
                    option.func_146113_a(mc.getSoundHandler());
                    AE2Thing.proxy.netHandler.sendToServer(new CPacketToggleInterfaceVisibility(entryId));
                    return true;
                }

                try {
                    Method click = entry.getClass()
                        .getDeclaredMethod("mouseClicked", int.class, int.class, int.class);
                    click.setAccessible(true);
                    return Boolean.TRUE.equals(click.invoke(entry, relativeX, relativeY, button));
                } catch (ReflectiveOperationException ignored) {
                    return false;
                }
            }
            return false;
        }

        private boolean wheel(int mouseX, int mouseY, int wheel) {
            if (!isInsideViewport(mouseX, mouseY) && !isOverScrollBar(mouseX, mouseY)) return false;
            if (super.mouseWheelEvent(mouseX, mouseY, wheel)) return true;
            getScrollBar().wheel(wheel);
            return true;
        }

        private boolean clickScrollBar(int mouseX, int mouseY) {
            if (!isOverScrollBar(mouseX, mouseY)) return false;
            getScrollBar().click(this, mouseX - guiLeft, mouseY - guiTop);
            return true;
        }

        private void dragScrollBar(int mouseY) {
            getScrollBar().clickMove(mouseY - guiTop);
        }

        private void stopDraggingScrollBar() {
            ((AccessorGuiScrollbar) getScrollBar()).setIsLatestClickOnScrollbar(false);
        }

        private boolean isInsideViewport(int mouseX, int mouseY) {
            int viewHeight = VIEW_HEIGHT == null ? 0 : intField(this, VIEW_HEIGHT, 0);
            return mouseX >= guiLeft + VIEWPORT_LEFT && mouseX < guiLeft + VIEWPORT_LEFT + VIEWPORT_WIDTH
                && mouseY >= guiTop + VIEWPORT_TOP
                && mouseY < guiTop + VIEWPORT_TOP + viewHeight;
        }

        private boolean isOverScrollBar(int mouseX, int mouseY) {
            int relativeX = mouseX - guiLeft;
            int relativeY = mouseY - guiTop;
            return getScrollBar().contains(relativeX, relativeY);
        }

        private void perform(GuiButton button) {
            super.actionPerformed(button);
        }

        private boolean handleSearchKey(char character, int key) {
            // Never invoke GuiInterfaceTerminal.keyTyped here: this embedded renderer is
            // not Minecraft.currentScreen, and its GuiContainer close path is invalid.
            // Only the three native interface search fields receive text input.
            MEGuiTextField[] fields = { textField(INPUT_SEARCH), textField(OUTPUT_SEARCH), textField(NAME_SEARCH) };
            for (int i = 0; i < fields.length; i++) {
                MEGuiTextField field = fields[i];
                if (field == null || !field.isFocused()) continue;
                if (key == 1 || key == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
                    return false;
                }
                if (character == '\t') {
                    field.setFocused(false);
                    for (int step = 1; step < fields.length; step++) {
                        MEGuiTextField next = fields[(i + step) % fields.length];
                        if (next != null) {
                            next.setFocused(true);
                            break;
                        }
                    }
                    return true;
                }
                String oldText = field.getText();
                boolean handled = field.textboxKeyTyped(character, key);
                if (!oldText.equals(field.getText())) clearHighlight();
                return handled;
            }
            return false;
        }

        private void setSearchFieldValue(String displayName, int mouseX, int mouseY, ItemStack stack) {
            String[] previous = searchTexts();
            super.setTextFieldValue(displayName, mouseX, mouseY, stack);
            if (!java.util.Arrays.equals(previous, searchTexts())) clearHighlight();
        }

        private String[] searchTexts() {
            MEGuiTextField[] fields = { textField(INPUT_SEARCH), textField(OUTPUT_SEARCH), textField(NAME_SEARCH) };
            String[] texts = new String[fields.length];
            for (int i = 0; i < fields.length; i++) texts[i] = fields[i] == null ? "" : fields[i].getText();
            return texts;
        }

        private void clearSearchFocus() {
            for (Field search : new Field[] { INPUT_SEARCH, OUTPUT_SEARCH, NAME_SEARCH }) {
                MEGuiTextField field = textField(search);
                if (field != null) field.setFocused(false);
            }
        }

        private void focusNameSearch() {
            clearSearchFocus();
            MEGuiTextField field = textField(NAME_SEARCH);
            if (field != null) field.setFocused(true);
        }

        private void setNameSearchText(String text) {
            MEGuiTextField field = textField(NAME_SEARCH);
            String newText = text == null ? "" : text;
            if (field != null) {
                if (!field.getText()
                    .equals(newText)) clearHighlight();
                field.setText(newText);
            }
            getScrollBar().setCurrentScroll(0);
        }

        private InterfacePatternTarget highlightFirstEmptyPatternSlot() {
            clearHighlight();
            Object masterList = objectField(this, MASTER_LIST);
            if (masterList == null) return null;
            try {
                Method visibleSections = masterList.getClass()
                    .getDeclaredMethod("getVisibleSections");
                visibleSections.setAccessible(true);
                for (Object section : (List<?>) visibleSections.invoke(masterList)) {
                    Method visibleEntries = section.getClass()
                        .getDeclaredMethod("getVisible");
                    visibleEntries.setAccessible(true);
                    Iterator<?> entries = (Iterator<?>) visibleEntries.invoke(section);
                    while (entries.hasNext()) {
                        Object entry = entries.next();
                        IInventory inventory = entryInventory(entry);
                        int numSlots = intField(
                            entry,
                            "numSlots",
                            inventory == null ? 0 : inventory.getSizeInventory());
                        if (inventory == null) continue;
                        for (int slot = 0; slot < Math.min(numSlots, inventory.getSizeInventory()); slot++) {
                            if (inventory.getStackInSlot(slot) != null) continue;
                            highlightedEntry = entry;
                            highlightedTarget = new InterfacePatternTarget(longField(entry, "id", -1), slot);
                            return highlightedTarget.getEntryId() < 0 ? null : highlightedTarget;
                        }
                    }
                }
            } catch (ReflectiveOperationException ignored) {}
            return null;
        }

        private InterfacePatternTarget highlightedTarget() {
            return highlightedTarget;
        }

        private InterfacePatternTarget patternSlotAt(int mouseX, int mouseY) {
            Object masterList = objectField(this, MASTER_LIST);
            if (masterList == null) return null;
            InterfacePatternTarget hovered = hoveredPatternTarget(masterList);
            if (hovered != null) return hovered;
            int relativeX = mouseX - guiLeft - 10;
            int relativeY = mouseY - guiTop - 52;
            int viewHeight = VIEW_HEIGHT == null ? 0 : intField(this, VIEW_HEIGHT, 0);
            if (relativeX < 0 || relativeX >= 174 || relativeY < 0 || relativeY >= viewHeight) return null;

            try {
                Method visibleSections = masterList.getClass()
                    .getDeclaredMethod("getVisibleSections");
                visibleSections.setAccessible(true);
                for (Object section : (List<?>) visibleSections.invoke(masterList)) {
                    Method visibleEntries = section.getClass()
                        .getDeclaredMethod("getVisible");
                    visibleEntries.setAccessible(true);
                    Iterator<?> entries = (Iterator<?>) visibleEntries.invoke(section);
                    while (entries.hasNext()) {
                        Object entry = entries.next();
                        int rowSize = intField(entry, "rowSize", 0);
                        int numSlots = intField(entry, "numSlots", 0);
                        int displayY = intField(entry, "dispY", -9999);
                        if (rowSize <= 0 || numSlots <= 0 || displayY <= -9000) continue;

                        int offsetX = relativeX - (174 - rowSize * 18) - 1;
                        int offsetY = relativeY - displayY - 1;
                        if (offsetX < 0 || offsetX >= rowSize * 18 || offsetY < 0) continue;
                        int slot = offsetY / 18 * rowSize + offsetX / 18;
                        if (slot < 0 || slot >= numSlots) continue;
                        long id = longField(entry, "id", -1);
                        return id < 0 ? null : new InterfacePatternTarget(id, slot);
                    }
                }
            } catch (ReflectiveOperationException ignored) {}
            return null;
        }

        /** Uses AE2's own per-frame hover result, so wrapped section titles and scrolling cannot skew the hit box. */
        private InterfacePatternTarget hoveredPatternTarget(Object masterList) {
            Object entry = objectField(masterList, findField(masterList.getClass(), "hoveredEntry"));
            if (entry == null) return null;
            int slot = intField(entry, "hoveredSlotIdx", -1);
            int numSlots = intField(entry, "numSlots", 0);
            if (slot < 0 || slot >= numSlots) return null;
            long id = longField(entry, "id", -1);
            return id < 0 ? null : new InterfacePatternTarget(id, slot);
        }

        private void clearHighlight() {
            highlightedEntry = null;
            highlightedTarget = null;
        }

        private static IInventory entryInventory(Object entry) {
            Field field = entry == null ? null : findField(entry.getClass(), "inv");
            Object value = objectField(entry, field);
            return value instanceof IInventory inventory ? inventory : null;
        }

        private static Object objectField(Object owner, Field field) {
            if (owner == null || field == null) return null;
            try {
                return field.get(owner);
            } catch (IllegalAccessException ignored) {
                return null;
            }
        }

        private static int intField(Object owner, String name, int fallback) {
            return intField(owner, owner == null ? null : findField(owner.getClass(), name), fallback);
        }

        private static int intField(Object owner, Field field, int fallback) {
            if (owner == null || field == null) return fallback;
            try {
                return field.getInt(owner);
            } catch (IllegalAccessException ignored) {
                return fallback;
            }
        }

        private static long longField(Object owner, String name, long fallback) {
            Field field = owner == null ? null : findField(owner.getClass(), name);
            if (field == null) return fallback;
            try {
                return field.getLong(owner);
            } catch (IllegalAccessException ignored) {
                return fallback;
            }
        }

        /** AE2Things' animated HSB border for the pending destination slot. */
        private static void drawRainbowBorder(int x, int y) {
            float hue = (System.currentTimeMillis() % 2000L) / 2000.0F;
            int color = 0x80000000 | Color.HSBtoRGB(hue, 1.0F, 1.0F) & 0x00FFFFFF;
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(0, 0, 250);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                drawRect(x - 1, y - 1, x + 17, y, color);
                drawRect(x - 1, y + 16, x + 17, y + 17, color);
                drawRect(x - 1, y, x, y + 16, color);
                drawRect(x + 16, y, x + 17, y + 16, color);
            } finally {
                GL11.glPopMatrix();
                GL11.glPopAttrib();
                GL11.glColor4f(1, 1, 1, 1);
            }
        }

        private MEGuiTextField textField(Field field) {
            if (field == null) return null;
            try {
                return (MEGuiTextField) field.get(this);
            } catch (IllegalAccessException ignored) {
                return null;
            }
        }

        private void refreshButtons() {
            setActionButton(
                "guiButtonAssemblersOnly",
                bool("onlyMolecularAssemblers") ? ActionItems.MOLECULAR_ASSEMBLEERS_ON
                    : ActionItems.MOLECULAR_ASSEMBLEERS_OFF);
            setActionButton(
                "guiButtonHideFull",
                AEConfig.instance.showOnlyInterfacesWithFreeSlotsInInterfaceTerminal
                    ? ActionItems.TOGGLE_SHOW_FULL_INTERFACES_OFF
                    : ActionItems.TOGGLE_SHOW_FULL_INTERFACES_ON);
            setActionButton(
                "guiButtonBrokenRecipes",
                bool("onlyBrokenRecipes") ? ActionItems.TOGGLE_SHOW_ONLY_INVALID_PATTERN_OFF
                    : ActionItems.TOGGLE_SHOW_ONLY_INVALID_PATTERN_ON);
            setActionButton(
                "guiButtonUseSubstitute",
                bool("onlySubstitute") ? ActionItems.TOGGLE_SHOW_ONLY_SUBSTITUTE_OFF
                    : ActionItems.TOGGLE_SHOW_ONLY_SUBSTITUTE_ON);
            setActionButton(
                "guiButtonShowHidden",
                bool("showHidden") ? ActionItems.TOGGLE_SHOW_HIDDEN_INTERFACES_ON
                    : ActionItems.TOGGLE_SHOW_HIDDEN_INTERFACES_OFF);
            setActionButton(
                "guiButtonSectionOrder",
                (StringOrder) AEConfig.instance.settings.getSetting(Settings.INTERFACE_TERMINAL_SECTION_ORDER));
            setActionButton(
                "terminalStyleBox",
                (TerminalStyle) AEConfig.instance.settings.getSetting(Settings.TERMINAL_STYLE));
        }

        private boolean bool(String fieldName) {
            Field field = findField(GuiInterfaceTerminal.class, fieldName);
            if (field == null) return false;
            try {
                return field.getBoolean(this);
            } catch (IllegalAccessException ignored) {
                return false;
            }
        }

        private void setActionButton(String fieldName, Enum<?> value) {
            Field field = findField(GuiInterfaceTerminal.class, fieldName);
            if (field == null) return;
            try {
                ((GuiImgButton) field.get(this)).set(value);
            } catch (IllegalAccessException ignored) {}
        }
    }
}
