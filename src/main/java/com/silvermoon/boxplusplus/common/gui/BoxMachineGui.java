package com.silvermoon.boxplusplus.common.gui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.FluidDisplayWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.silvermoon.boxplusplus.common.tileentities.GTMachineBox;
import com.silvermoon.boxplusplus.util.BoxRoutings;
import com.silvermoon.boxplusplus.util.Util;
import com.xyp.gtnc.Common.gui.modularui.multiblock.BaseGui.GTNCModernMultiBlockBaseGui;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.modularui2.widget.SlotLikeButtonWidget;

/**
 * MUI2 port of the original Box++ GUI.
 *
 * <p>
 * Keep this class as a faithful GUI layer: the machine owns state and processing, this class owns windows, buttons and
 * panel layout.
 */
public final class BoxMachineGui extends GTNCModernMultiBlockBaseGui<GTMachineBox> {

    // #tr tile.boxplusplus.boxUI.available_routing
    // # Available processes:
    // # zh_CN 可用工序：
    private static final String AVAILABLE_ROUTING_KEY = "tile.boxplusplus.boxUI.available_routing";
    // #tr tile.boxplusplus.boxUI.available_parallel
    // # Available parallel:
    // # zh_CN 可用并行：
    private static final String AVAILABLE_PARALLEL_KEY = "tile.boxplusplus.boxUI.available_parallel";
    // #tr tile.boxplusplus.boxUI.recipe_page_previous
    // # Previous recipe page
    // # zh_CN 上一配方页
    // #tr tile.boxplusplus.boxUI.recipe_page_next
    // # Add or open the next recipe page
    // # zh_CN 添加或打开下一配方页

    private static final String PANEL_ROUTING = "boxplusplus_original_routing";
    private static final String PANEL_ROUTE_DETAIL = "boxplusplus_original_route_detail";
    private static final String PANEL_FINAL_RECIPE = "boxplusplus_original_final_recipe";
    private static final String PANEL_MODULES = "boxplusplus_original_modules";
    private static final String PANEL_EXPORT_PATTERN = "boxplusplus_original_export_pattern";
    private static final String PANEL_IMPORT = "boxplusplus_original_import";
    private static final String PANEL_CLEAR = "boxplusplus_original_clear";
    private static final String PANEL_WIKI = "boxplusplus_original_wiki";

    private static final String SYNC_LOCKED = "boxLocked";
    private static final String SYNC_ROUTING_COUNT = "boxRoutingCount";
    private static final String SYNC_ROUTING_STATUS = "boxRoutingStatus";
    private static final String SYNC_ROUTING_REVISION = "boxRoutingRevision";
    private static final String SYNC_ROUTING_DATA = "boxRoutingData";
    private static final String SYNC_FINAL_RECIPE_DATA = "boxFinalRecipeData";
    private static final String SYNC_RING_COUNT = "boxRingCount";
    private static final String SYNC_RECIPE_PAGE = "boxRecipePage";
    private static final String SYNC_RECIPE_PAGE_COUNT = "boxRecipePageCount";
    private static final String SYNC_MAX_RECIPE_PAGES = "boxMaxRecipePages";
    private static final String SYNC_MAX_ROUTING = "boxMaxRouting";
    private static final String SYNC_MAX_PARALLEL = "boxMaxParallel";
    private static final String SYNC_AVAILABLE_ROUTING = "boxAvailableRouting";
    private static final String SYNC_AVAILABLE_PARALLEL = "boxAvailableParallel";
    private static final String SYNC_ROUTE_INDEX = "boxRouteIndex";
    private static final String SYNC_ROUTE_PARALLEL = "boxRouteParallel";
    private static final String SYNC_IMPORT_TEXT = "boxImportText";
    private static final String SYNC_EXPORT_ITEM_RANGE = "boxExportItemRange";
    private static final String SYNC_EXPORT_FLUID_RANGE = "boxExportFluidRange";
    private static final int MAX_ROUTING_ROWS = 128;
    private static final int MAX_ROUTE_INGREDIENT_ROWS = 64;
    private static final int MAX_FINAL_INGREDIENT_ROWS = 64;

    private int selectedRoute = 1;
    private int wikiPage = 1;
    private int wikiModule = 1;
    private String importText = "";
    private String exportItemRange = "";
    private String exportFluidRange = "";

    public BoxMachineGui(GTMachineBox multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            SYNC_RECIPE_PAGE_COUNT,
            new IntSyncValue(multiblock::getRecipePageCountForGui, multiblock::applyRecipePageCountMirrorForGui));
        syncManager.syncValue(
            SYNC_RECIPE_PAGE,
            new IntSyncValue(multiblock::getRecipePageForGui, multiblock::applyRecipePageMirrorForGui));
        syncManager.syncValue(SYNC_MAX_RECIPE_PAGES, new IntSyncValue(multiblock::getMaxRecipePagesForGui));
        syncManager.syncValue(SYNC_MAX_ROUTING, new IntSyncValue(multiblock::getMaxRoutingForGui));
        syncManager.syncValue(SYNC_MAX_PARALLEL, new IntSyncValue(multiblock::getMaxParallelForGui));
        syncManager.syncValue(SYNC_AVAILABLE_ROUTING, new IntSyncValue(multiblock::getAvailableRoutingForGui));
        syncManager.syncValue(SYNC_AVAILABLE_PARALLEL, new IntSyncValue(multiblock::getAvailableParallelForGui));
        syncManager.syncValue(SYNC_LOCKED, new BooleanSyncValue(multiblock::isBoxRecipeLockedForGui));
        syncManager.syncValue(SYNC_ROUTING_COUNT, new IntSyncValue(multiblock::getRoutingCountForGui));
        syncManager.syncValue(SYNC_ROUTING_STATUS, new IntSyncValue(multiblock::getRoutingStatusForGui));
        syncManager.syncValue(SYNC_ROUTING_REVISION, new IntSyncValue(multiblock::getRoutingGuiRevisionForGui));
        syncManager.syncValue(
            SYNC_ROUTING_DATA,
            new StringSyncValue(multiblock::getRoutingConfigForGui, multiblock::applyRoutingMirrorForGui));
        syncManager.syncValue(
            SYNC_FINAL_RECIPE_DATA,
            new StringSyncValue(multiblock::getFinalRecipeConfigForGui, multiblock::applyFinalRecipeMirrorForGui));
        syncManager.syncValue(SYNC_RING_COUNT, new IntSyncValue(multiblock::getRingCountForGui));
        syncManager.syncValue(
            SYNC_ROUTE_INDEX,
            new IntSyncValue(() -> selectedRoute, value -> selectedRoute = Math.max(1, value)).allowC2S());
        syncManager.syncValue(
            SYNC_ROUTE_PARALLEL,
            new IntSyncValue(() -> multiblock.getRoutingParallelForGui(selectedRoute), value -> {
                int parallel = MathHelper.clamp_int(value, 1, Math.max(1, multiblock.getMaxParallelForGui()));
                multiblock.updateRoutingParallelDirectForGui(selectedRoute, parallel);
            }).allowC2S());
        syncManager
            .syncValue(SYNC_IMPORT_TEXT, new StringSyncValue(() -> importText, value -> importText = value).allowC2S());
        syncManager.syncValue(
            SYNC_EXPORT_ITEM_RANGE,
            new StringSyncValue(
                () -> exportItemRange,
                value -> exportItemRange = Util.validator(multiblock.recipe, value, false)).allowC2S());
        syncManager.syncValue(
            SYNC_EXPORT_FLUID_RANGE,
            new StringSyncValue(
                () -> exportFluidRange,
                value -> exportFluidRange = Util.validator(multiblock.recipe, value, true)).allowC2S());

        serverAction(syncManager, "boxToggleRingRender", multiblock::toggleRingRender);
        serverAction(syncManager, "boxRing1", () -> multiblock.setRingCountForGui(1));
        serverAction(syncManager, "boxRing2", () -> multiblock.setRingCountForGui(2));
        serverAction(syncManager, "boxRing3", () -> multiblock.setRingCountForGui(3));
        serverAction(syncManager, "boxPreviousRecipePage", () -> {
            selectedRoute = 1;
            multiblock.switchRecipePageForGui(multiblock.getRecipePageForGui() - 1);
        });
        serverAction(syncManager, "boxNextRecipePage", () -> {
            selectedRoute = 1;
            multiblock.switchRecipePageForGui(multiblock.getRecipePageForGui() + 1);
        });
        for (int i = 0; i < 14; i++) {
            int index = i;
            serverAction(syncManager, "boxEnableModule" + index, () -> multiblock.setModuleEnabledForGui(index, true));
            serverAction(
                syncManager,
                "boxDisableModule" + index,
                () -> multiblock.setModuleEnabledForGui(index, false));
        }
        for (int i = 1; i <= 128; i++) {
            int route = i;
            syncManager.syncValue("boxSelectRoute" + route, new InteractionSyncHandler().setOnMousePressed(mouse -> {
                if (mouse.mouseButton != 0) return;
                selectedRoute = route;
            }));
            syncManager.syncValue("boxRemoveRoute" + route, new InteractionSyncHandler().setOnMousePressed(mouse -> {
                if (!mouse.isClient() && mouse.mouseButton == 0 && mouse.shift) {
                    multiblock.removeRoutingForGui(route);
                }
            }));
        }
        syncManager.syncValue("boxCheckRouting", new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (mouse.mouseButton == 0) {
                EntityPlayer player = syncManager.getPlayer();
                if (player != null) multiblock.bindRoutingContext(player);
            }
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                multiblock.checkNextRoutingForGui();
            }
        }));
        syncManager.syncValue("boxBindRouting", new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (mouse.mouseButton == 0) {
                EntityPlayer player = syncManager.getPlayer();
                if (player != null) multiblock.bindRoutingContext(player);
            }
        }));
        serverAction(
            syncManager,
            "boxBuildRecipe",
            () -> { if (!multiblock.rebuildFinalRecipeForGui()) sendFailure(syncManager); });
        serverAction(
            syncManager,
            "boxDoubleRecipe",
            () -> { if (!multiblock.tryDoubleRecipe()) sendFailure(syncManager); });
        serverAction(
            syncManager,
            "boxHalveRecipe",
            () -> { if (!multiblock.tryHalveRecipe()) sendFailure(syncManager); });
        syncManager.syncValue(
            "boxLockRecipe",
            new InteractionSyncHandler().setOnMousePressed(
                mouse -> { if (!mouse.isClient() && mouse.mouseButton == 0) multiblock.lockRecipeForGui(); }));
        syncManager.syncValue("boxImportRouting", new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                multiblock.importRoutingConfigForGui(importText, syncManager.getPlayer());
            }
        }));
        syncManager.syncValue("boxExportPattern", new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                EntityPlayer player = syncManager.getPlayer();
                if (player != null) multiblock.exportAE2Pattern(player, exportItemRange, exportFluidRange);
            }
        }));
        for (int i = 1; i <= 5; i++) {
            int number = i;
            syncManager.syncValue("boxClearNumber" + i, new InteractionSyncHandler().setOnMousePressed(mouse -> {
                if (!mouse.isClient() && mouse.mouseButton == 0) {
                    multiblock.submitClearNumberForGui(number, syncManager.getPlayer());
                }
            }));
        }
    }

    @Override
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        IPanelHandler routeDetail = syncManager
            .syncedPanel(PANEL_ROUTE_DETAIL, false, (manager, handler) -> createRouteDetailPanel(syncManager));
        IPanelHandler finalRecipe = syncManager
            .syncedPanel(PANEL_FINAL_RECIPE, false, (manager, handler) -> createFinalRecipePanel(syncManager));
        IPanelHandler importPanel = syncManager
            .syncedPanel(PANEL_IMPORT, false, (manager, handler) -> createImportPanel(syncManager));
        IPanelHandler exportPattern = syncManager
            .syncedPanel(PANEL_EXPORT_PATTERN, false, (manager, handler) -> createExportPatternPanel(syncManager));
        IPanelHandler clearPanel = syncManager
            .syncedPanel(PANEL_CLEAR, false, (manager, handler) -> createClearPanel(syncManager));
        IPanelHandler routing = syncManager.syncedPanel(
            PANEL_ROUTING,
            false,
            (manager, handler) -> createRoutingPanel(
                syncManager,
                routeDetail,
                finalRecipe,
                importPanel,
                exportPattern,
                clearPanel));
        IPanelHandler[] moduleDetails = new IPanelHandler[14];
        for (int i = 0; i < moduleDetails.length; i++) {
            int index = i;
            moduleDetails[i] = syncManager.syncedPanel(
                "boxplusplus_original_module_" + index,
                false,
                (manager, handler) -> createSingleModulePanel(syncManager, index));
        }
        IPanelHandler modules = syncManager
            .syncedPanel(PANEL_MODULES, false, (manager, handler) -> createModulePanel(syncManager, moduleDetails));
        IPanelHandler wiki = syncManager.syncedPanel(PANEL_WIKI, false, (manager, handler) -> createWikiPanel());

        return Flow.row()
            .coverChildrenWidth()
            .fullHeight()
            .childPadding(2)
            .child(panelButton(GTGuiTextures.OVERLAY_BUTTON_WHITELIST, "tile.boxplusplus.boxUI.01", modules))
            .child(routingButton(syncManager, routing))
            .child(
                actionButton(
                    syncManager,
                    "boxToggleRingRender",
                    GTGuiTextures.OVERLAY_BUTTON_CYCLIC,
                    "tile.boxplusplus.boxUI.02",
                    this::mirrorToggleRingRender))
            .child(panelButton(GTGuiTextures.OVERLAY_BUTTON_INVERT_REDSTONE, "tile.boxplusplus.boxwiki.1", wiki));
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .coverChildrenWidth()
            .fullHeight();
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .child(createPowerSwitchButton())
            .child(createStructureUpdateButton(syncManager));
    }

    @Override
    protected boolean shouldDisplayVoidExcess() {
        return false;
    }

    @Override
    protected boolean shouldDisplayInputSeparation() {
        return false;
    }

    @Override
    protected boolean shouldDisplayBatchMode() {
        return false;
    }

    @Override
    protected boolean shouldDisplayRecipeLock() {
        return false;
    }

    @Override
    protected boolean usesLockToSingleRecipeButton() {
        return false;
    }

    private ModularPanel createModulePanel(PanelSyncManager syncManager, IPanelHandler[] details) {
        IntSyncValue ring = syncManager.findSyncHandler(SYNC_RING_COUNT, IntSyncValue.class);
        int[] x = { 64, 117, 164, 117, 35, 117, 195, 117, 8, 117, 225, 117, 117, 117 };
        int[] y = { 117, 67, 117, 167, 117, 38, 117, 196, 117, 11, 117, 223, 107, 127 };
        ModularPanel panel = GTGuis.createPopUpPanel(PANEL_MODULES)
            .size(250, 250)
            .background(
                new DynamicDrawable(() -> BoxGuiTextures.RINGS[Math.max(0, Math.min(2, ring.getIntValue() - 1))]))
            .disableHoverBackground()
            .child(ringButton(syncManager, "boxRing1", "tile.boxplusplus.boxUI.module.20", () -> {
                multiblock.setRingCountForGui(1);
                return true;
            }).pos(8, 8))
            .child(ringButton(syncManager, "boxRing2", "tile.boxplusplus.boxUI.module.21", () -> {
                multiblock.setRingCountForGui(2);
                return true;
            }).pos(8, 26))
            .child(ringButton(syncManager, "boxRing3", "tile.boxplusplus.boxUI.module.22", () -> {
                multiblock.setRingCountForGui(3);
                return true;
            }).pos(8, 44));
        for (int i = 0; i < 14; i++) {
            if (i == 11) continue;
            int index = i;
            panel.child(
                new ButtonWidget<>().size(16, 16)
                    .pos(x[i], y[i])
                    .disableThemeBackground(true)
                    .disableHoverThemeBackground(true)
                    .background(BoxGuiTextures.moduleButton(i))
                    .hoverBackground(BoxGuiTextures.moduleButton(i))
                    .overlay(
                        i == 4 ? BoxGuiTextures.MODULE_FIVE_FRAME
                            : i < 12 ? BoxGuiTextures.MODULE_FRAME : IDrawable.EMPTY)
                    .onMousePressed(mouseButton -> {
                        if (mouseButton == 0 && isModuleAvailable(index, ring.getIntValue())) {
                            details[index].openPanel();
                            return true;
                        }
                        return false;
                    })
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.module." + (i + 1)))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .setEnabledIf(widget -> isModuleAvailable(index, ring.getIntValue())));
        }
        return panel;
    }

    private ModularPanel createSingleModulePanel(PanelSyncManager syncManager, int index) {
        return GTGuis.createPopUpPanel("boxplusplus_original_module_" + index)
            .size(150, 200)
            .child(
                BoxGuiTextures.DREAM.asWidget()
                    .pos(5, 5)
                    .size(16, 16))
            .child(
                text("tile.boxplusplus.boxUI.module." + (index + 1), 0.58f).pos(25, 8)
                    .size(112, 12))
            .child(
                BoxGuiTextures.modulePicture(index)
                    .asWidget()
                    .pos(20, 25)
                    .size(110, 73))
            .child(
                text("tile.boxplusplus.boxUI.module.context." + (index + 1) + "a", 0.45f).alignment(Alignment.TopCenter)
                    .pos(10, 100)
                    .size(130, 28))
            .child(
                text("tile.boxplusplus.boxwiki.26", 0.45f).pos(20, 130)
                    .size(110, 10))
            .child(
                text("tile.boxplusplus.boxUI.module.context." + (index + 1) + "b", 0.45f).pos(20, 140)
                    .size(110, 26))
            .child(
                small(IKey.dynamic(() -> moduleStateLine(index)), 0.45f).pos(20, 175)
                    .size(82, 12))
            .child(
                actionButton(
                    syncManager,
                    "boxEnableModule" + index,
                    GTGuiTextures.OVERLAY_BUTTON_CHECKMARK,
                    "tile.boxplusplus.boxUI.module.16",
                    () -> {
                        multiblock.setModuleEnabledForGui(index, true);
                        return true;
                    }).size(20, 20)
                        .pos(100, 170)
                        .setEnabledIf(widget -> !multiblock.isModuleEnabledForGui(index)))
            .child(
                actionButton(
                    syncManager,
                    "boxDisableModule" + index,
                    GTGuiTextures.OVERLAY_BUTTON_CROSS,
                    "tile.boxplusplus.boxUI.module.16a",
                    () -> {
                        multiblock.setModuleEnabledForGui(index, false);
                        return true;
                    }).size(20, 20)
                        .pos(100, 170)
                        .setEnabledIf(widget -> multiblock.isModuleEnabledForGui(index)));
    }

    private ModularPanel createRoutingPanel(PanelSyncManager syncManager, IPanelHandler routeDetail,
        IPanelHandler finalRecipe, IPanelHandler importPanel, IPanelHandler exportPattern, IPanelHandler clearPanel) {
        BooleanSyncValue locked = syncManager.findSyncHandler(SYNC_LOCKED, BooleanSyncValue.class);
        IntSyncValue status = syncManager.findSyncHandler(SYNC_ROUTING_STATUS, IntSyncValue.class);
        IntSyncValue page = syncManager.findSyncHandler(SYNC_RECIPE_PAGE, IntSyncValue.class);
        IntSyncValue pageCount = syncManager.findSyncHandler(SYNC_RECIPE_PAGE_COUNT, IntSyncValue.class);
        IntSyncValue maximumPages = syncManager.findSyncHandler(SYNC_MAX_RECIPE_PAGES, IntSyncValue.class);
        IntSyncValue maximumRouting = syncManager.findSyncHandler(SYNC_MAX_ROUTING, IntSyncValue.class);
        IntSyncValue maximumParallel = syncManager.findSyncHandler(SYNC_MAX_PARALLEL, IntSyncValue.class);
        IntSyncValue availableRouting = syncManager.findSyncHandler(SYNC_AVAILABLE_ROUTING, IntSyncValue.class);
        IntSyncValue availableParallel = syncManager.findSyncHandler(SYNC_AVAILABLE_PARALLEL, IntSyncValue.class);
        return GTGuis.createPopUpPanel(PANEL_ROUTING)
            .size(260, 215)
            .child(
                BoxGuiTextures.ARROW_GREEN_UP.asWidget()
                    .pos(5, 5)
                    .size(16, 16))
            .child(
                raw(
                    () -> Util.i18n(AVAILABLE_ROUTING_KEY) + availableRouting.getIntValue()
                        + " / "
                        + maximumRouting.getIntValue(),
                    0.5f).pos(25, 8)
                        .size(125, 12))
            .child(
                raw(
                    () -> Util.i18n(AVAILABLE_PARALLEL_KEY) + availableParallel.getIntValue()
                        + " / "
                        + maximumParallel.getIntValue(),
                    0.45f).pos(153, 8)
                        .size(102, 12))
            .child(
                createRoutingList(syncManager, routeDetail).pos(21, 20)
                    .size(110, 180))
            .child(
                panelButton(GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_ITEM, "tile.boxplusplus.boxUI.30", importPanel)
                    .pos(200, 25)
                    .setEnabledIf(widget -> multiblock.routingMap.isEmpty() && !multiblock.recipe.islocked))
            .child(
                exportRoutingButton(syncManager).pos(200, 25)
                    .setEnabledIf(widget -> multiblock.recipe.islocked))
            .child(
                GTGuiTextures.OVERLAY_BUTTON_CROSS.asWidget()
                    .pos(140, 71)
                    .size(24, 24)
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.ErrorCode." + status.getIntValue()))
                    .setEnabledIf(widget -> status.getIntValue() != 0 && !locked.getBoolValue()))
            .child(
                GTGuiTextures.OVERLAY_BUTTON_CHECKMARK.asWidget()
                    .pos(140, 71)
                    .size(36, 36)
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.19"))
                    .setEnabledIf(widget -> status.getIntValue() == 0 && !locked.getBoolValue()))
            .child(
                actionOpenButton(
                    syncManager,
                    "boxBuildRecipe",
                    GTGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_ON,
                    "tile.boxplusplus.boxUI.20",
                    finalRecipe,
                    () -> {
                        multiblock.rebuildFinalRecipeForGui();
                        return true;
                    }).size(32, 32)
                        .pos(140, 26)
                        .setEnabledIf(widget -> !multiblock.routingMap.isEmpty() && !multiblock.recipe.islocked))
            .child(
                actionButton(syncManager, "boxDoubleRecipe", BoxGuiTextures.DOUBLE, "tile.boxplusplus.boxUI.33", () -> {
                    multiblock.tryDoubleRecipe();
                    return true;
                }).size(14, 14)
                    .pos(175, 26)
                    .setEnabledIf(widget -> !multiblock.routingMap.isEmpty() && !multiblock.recipe.islocked))
            .child(
                actionButton(syncManager, "boxHalveRecipe", BoxGuiTextures.HALVE, "tile.boxplusplus.boxUI.34", () -> {
                    multiblock.tryHalveRecipe();
                    return true;
                }).size(14, 14)
                    .pos(175, 44)
                    .setEnabledIf(widget -> !multiblock.routingMap.isEmpty() && !multiblock.recipe.islocked))
            .child(
                panelButton(GTGuiTextures.OVERLAY_BUTTON_WHITELIST, "tile.boxplusplus.boxUI.21", finalRecipe)
                    .size(32, 32)
                    .pos(140, 26)
                    .setEnabledIf(widget -> multiblock.recipe.islocked))
            .child(
                panelButton(BoxGuiTextures.AE, "tile.boxplusplus.boxUI.36", exportPattern).size(14, 14)
                    .pos(175, 26)
                    .setEnabledIf(widget -> multiblock.recipe.islocked))
            .child(
                panelButton(BoxGuiTextures.CLEAR, "tile.boxplusplus.boxUI.41", clearPanel).size(14, 14)
                    .pos(175, 44)
                    .setEnabledIf(widget -> multiblock.recipe.islocked))
            .child(
                actionButton(
                    syncManager,
                    "boxPreviousRecipePage",
                    IKey.str("<"),
                    "tile.boxplusplus.boxUI.recipe_page_previous",
                    () -> {
                        selectedRoute = 1;
                        return multiblock.switchRecipePageForGui(multiblock.getRecipePageForGui() - 1);
                    }).pos(5, 195)
                        .setEnabledIf(widget -> page.getIntValue() > 1))
            .child(
                raw(
                    () -> page.getIntValue() + " / "
                        + pageCount.getIntValue()
                        + " ("
                        + maximumPages.getIntValue()
                        + ")",
                    0.5f).alignment(Alignment.Center)
                        .pos(95, 198)
                        .size(70, 10))
            .child(
                actionButton(
                    syncManager,
                    "boxNextRecipePage",
                    IKey.str("+"),
                    "tile.boxplusplus.boxUI.recipe_page_next",
                    () -> {
                        selectedRoute = 1;
                        return multiblock.switchRecipePageForGui(multiblock.getRecipePageForGui() + 1);
                    }).pos(239, 195)
                        .setEnabledIf(
                            widget -> page.getIntValue()
                                < Math.max(pageCount.getIntValue(), maximumPages.getIntValue())));
    }

    private ListWidget<IWidget, ?> createRoutingList(PanelSyncManager syncManager, IPanelHandler routeDetail) {
        ListWidget<IWidget, ?> list = new RoutingListWidget();
        list.size(110, 180);
        for (int route = 1; route <= MAX_ROUTING_ROWS; route++) {
            list.child(createRoutingRow(syncManager, routeDetail, route));
        }
        list.child(
            new ParentWidget<>().size(110, 20)
                .setEnabledIf(
                    widget -> !multiblock.recipe.islocked
                        && multiblock.routingMap.size() < multiblock.getMaxRoutingForGui())
                .child(
                    raw(() -> Util.i18n("tile.boxplusplus.boxUI.07") + (multiblock.routingMap.size() + 1), 0.5f)
                        .pos(0, 4)
                        .size(70, 10))
                .child(
                    actionButton(
                        syncManager,
                        "boxCheckRouting",
                        GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_ITEM,
                        "tile.boxplusplus.boxUI.08",
                        () -> {
                            mirrorBindRoutingContext();
                            return true;
                        }).pos(92, 0)));
        return list;
    }

    private final class RoutingListWidget extends ListWidget<IWidget, RoutingListWidget> {

        private int previousRoutingCount = multiblock.getRoutingCountForGui();

        @Override
        public void postResize() {
            super.postResize();
            int currentRoutingCount = multiblock.getRoutingCountForGui();
            if (currentRoutingCount > previousRoutingCount && getScrollArea().getScrollY() != null) {
                getScrollArea().getScrollY()
                    .scrollTo(getScrollArea(), Integer.MAX_VALUE);
            }
            previousRoutingCount = currentRoutingCount;
        }
    }

    private IWidget createRoutingRow(PanelSyncManager syncManager, IPanelHandler routeDetail, int route) {
        ParentWidget<?> row = new ParentWidget<>().size(110, 20);
        row.child(
            itemSlot(() -> getRouting(route) == null ? null : getRouting(route).RoutingMachine).pos(0, 0)
                .size(16, 16));
        row.child(
            raw(() -> Util.i18n("tile.boxplusplus.boxUI.07") + route, 0.5f).pos(20, 4)
                .size(42, 10));
        row.child(
            new ButtonWidget<>().size(16, 16)
                .pos(70, 0)
                .overlay(GTGuiTextures.OVERLAY_BUTTON_ALLOW_INPUT)
                .syncHandler(syncManager.findSyncHandler("boxSelectRoute" + route, InteractionSyncHandler.class))
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0) {
                        selectedRoute = route;
                        routeDetail.openPanel();
                    }
                    return false;
                })
                .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.09"))
                .tooltipShowUpTimer(TOOLTIP_DELAY));
        row.child(
            actionButton(
                syncManager,
                "boxRemoveRoute" + route,
                GTGuiTextures.OVERLAY_BUTTON_BLOCK_INPUT,
                "tile.boxplusplus.boxUI.26",
                () -> true).pos(92, 0));
        return row.setEnabledIf(widget -> getRouting(route) != null);
    }

    private ModularPanel createRouteDetailPanel(PanelSyncManager syncManager) {
        IntSyncValue parallel = syncManager.findSyncHandler(SYNC_ROUTE_PARALLEL, IntSyncValue.class);
        BooleanSyncValue locked = syncManager.findSyncHandler(SYNC_LOCKED, BooleanSyncValue.class);
        return GTGuis.createPopUpPanel(PANEL_ROUTE_DETAIL)
            .size(220, 200)
            .child(
                GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_FLUID.asWidget()
                    .pos(5, 5)
                    .size(16, 16))
            .child(
                raw(() -> Util.i18n("tile.boxplusplus.boxUI.10") + selectedRoute, 0.55f).pos(25, 8)
                    .size(170, 12))
            .child(
                createRouteIngredientList().pos(20, 24)
                    .size(180, 110))
            .child(
                itemSlot(() -> getRouting(selectedRoute) == null ? null : getRouting(selectedRoute).RoutingMachine)
                    .pos(21, 146)
                    .size(16, 16))
            .child(
                raw(this::routeMachineLine, 0.5f).pos(45, 140)
                    .size(155, 10))
            .child(
                raw(this::routeVoltageLine, 0.5f).pos(45, 150)
                    .size(155, 10))
            .child(
                raw(this::routeTimeLine, 0.5f).pos(45, 160)
                    .size(155, 10))
            .child(
                raw(() -> Util.i18n("tile.boxplusplus.boxUI.23"), 0.55f).alignment(Alignment.CENTER)
                    .pos(21, 170)
                    .size(30, 16))
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .numbersInt(raw -> MathHelper.clamp_int(raw, 1, Math.max(1, multiblock.getMaxParallelForGui())))
                    .value(parallel)
                    .setTextAlignment(Alignment.CENTER)
                    .scrollValues(1, 64, 4, 16)
                    .shadow(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                    .background(IDrawable.EMPTY)
                    .hoverBackground(IDrawable.EMPTY)
                    .pos(50, 171)
                    .size(60, 12)
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.24"))
                    .setEnabledIf(widget -> !locked.getBoolValue()))
            .child(
                raw(() -> String.valueOf(multiblock.getRoutingParallelForGui(selectedRoute)), 0.55f)
                    .alignment(Alignment.CENTER)
                    .pos(50, 171)
                    .size(60, 12)
                    .setEnabledIf(widget -> locked.getBoolValue()));
    }

    private ModularPanel createFinalRecipePanel(PanelSyncManager syncManager) {
        ModularPanel panel = GTGuis.createPopUpPanel(PANEL_FINAL_RECIPE)
            .size(360, 220)
            .child(
                GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_FLUID.asWidget()
                    .pos(5, 5)
                    .size(16, 16))
            .child(
                text("tile.boxplusplus.boxUI.22", 0.55f).pos(25, 8)
                    .size(310, 12))
            .child(
                createFinalInputList().pos(20, 24)
                    .size(150, 130))
            .child(
                createFinalOutputList().pos(180, 24)
                    .size(150, 130))
            .child(
                small(IKey.str("------------------------------------------------------------------------------"), 0.45f)
                    .pos(20, 153)
                    .size(320, 10))
            .child(
                raw(() -> Util.i18n("tile.boxplusplus.boxUI.16") + multiblock.recipe.FinalVoteage + " eu/t", 0.5f)
                    .pos(20, 160)
                    .size(160, 10))
            .child(
                raw(
                    () -> Util.i18n("tile.boxplusplus.boxUI.17") + multiblock.recipe.FinalTime / 20.00
                        + "s ("
                        + multiblock.recipe.FinalTime
                        + "tick)",
                    0.5f).pos(20, 172)
                        .size(180, 10))
            .child(
                raw(() -> Util.i18n("tile.boxplusplus.boxUI.29") + multiblock.recipe.parallel, 0.5f).pos(20, 184)
                    .size(180, 10))
            .child(
                raw(
                    () -> multiblock.recipe.parallel > multiblock.getMaxParallelForGui()
                        ? Util.i18n("tile.boxplusplus.boxUI.32")
                            .replace("%max", String.valueOf(multiblock.getMaxParallelForGui()))
                        : "",
                    0.5f).pos(25, 196)
                        .size(180, 10))
            .child(
                raw(multiblock::getBoxRequiredModuleLine, 0.45f).alignment(Alignment.TopLeft)
                    .pos(200, 160)
                    .size(130, 45));
        panel.child(
            actionButton(
                syncManager,
                "boxLockRecipe",
                GTGuiTextures.OVERLAY_BUTTON_CHECKMARK,
                "tile.boxplusplus.boxUI.25",
                () -> {
                    multiblock.lockRecipeForGui();
                    panel.closeIfOpen();
                    return true;
                }).size(20, 20)
                    .pos(170, 160)
                    .setEnabledIf(
                        widget -> !multiblock.recipe.islocked
                            && multiblock.recipe.parallel <= multiblock.getMaxParallelForGui()));
        return panel;
    }

    private ModularPanel createImportPanel(PanelSyncManager syncManager) {
        StringSyncValue importSync = syncManager.findSyncHandler(SYNC_IMPORT_TEXT, StringSyncValue.class);
        return GTGuis.createPopUpPanel(PANEL_IMPORT)
            .size(300, 48)
            .child(
                text("tile.boxplusplus.boxUI.46", 0.5f).pos(5, 5)
                    .size(285, 10))
            .child(
                new TextFieldWidget().value(importSync)
                    .setTextAlignment(Alignment.CenterLeft)
                    .shadow(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                    .background(IDrawable.EMPTY)
                    .hoverBackground(IDrawable.EMPTY)
                    .pos(5, 16)
                    .size(250, 16))
            .child(
                actionButton(
                    syncManager,
                    "boxImportRouting",
                    GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_ITEM,
                    "tile.boxplusplus.boxUI.30",
                    () -> true).pos(270, 16));
    }

    private ModularPanel createExportPatternPanel(PanelSyncManager syncManager) {
        StringSyncValue itemRange = syncManager.findSyncHandler(SYNC_EXPORT_ITEM_RANGE, StringSyncValue.class);
        StringSyncValue fluidRange = syncManager.findSyncHandler(SYNC_EXPORT_FLUID_RANGE, StringSyncValue.class);
        return GTGuis.createPopUpPanel(PANEL_EXPORT_PATTERN)
            .size(168, 125)
            .child(
                new TextFieldWidget().value(itemRange)
                    .setTextAlignment(Alignment.CenterLeft)
                    .shadow(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                    .background(IDrawable.EMPTY)
                    .hoverBackground(IDrawable.EMPTY)
                    .pos(12, 10)
                    .size(60, 12)
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.13")))
            .child(
                new TextFieldWidget().value(fluidRange)
                    .setTextAlignment(Alignment.CenterLeft)
                    .shadow(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                    .background(IDrawable.EMPTY)
                    .hoverBackground(IDrawable.EMPTY)
                    .pos(96, 10)
                    .size(60, 12)
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.14")))
            .child(
                text("tile.boxplusplus.boxUI.48", 0.5f).pos(5, 45)
                    .size(158, 72))
            .child(
                actionButton(
                    syncManager,
                    "boxExportPattern",
                    GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_ITEM,
                    "tile.boxplusplus.boxUI.36",
                    () -> true).pos(76, 25));
    }

    private ModularPanel createClearPanel(PanelSyncManager syncManager) {
        multiblock.getClearPromptForGui();
        boolean finalConfirm = multiblock.randomSN.size() == 1;
        ModularPanel panel = GTGuis.createPopUpPanel(PANEL_CLEAR)
            .size(146, 60 + (finalConfirm ? 15 : 0))
            .child(
                BoxGuiTextures.CLEAR.asWidget()
                    .pos(5, 5)
                    .size(16, 16))
            .child(
                raw(multiblock::getClearPromptForGui, 0.48f).pos(25, 5)
                    .size(116, 35));
        for (int i = 1; i <= 5; i++) {
            panel.child(
                actionButton(
                    syncManager,
                    "boxClearNumber" + i,
                    BoxGuiTextures.number(i),
                    "tile.boxplusplus.boxUI.42",
                    () -> true).size(24, 24)
                        .pos(28 * (i - 1) + 5, 30 + (finalConfirm ? 15 : 0)));
        }
        return panel;
    }

    private ModularPanel createWikiPanel() {
        ModularPanel panel = GTGuis.createPopUpPanel(PANEL_WIKI)
            .size(300, 210)
            .child(
                BoxGuiTextures.NEI.asWidget()
                    .pos(5, 5)
                    .size(16, 16))
            .child(
                text("tile.boxplusplus.boxwiki.1", 0.55f).pos(25, 8)
                    .size(250, 12))
            .child(
                text("tile.boxplusplus.boxwiki.2", 0.55f).pos(25, 30)
                    .size(250, 12))
            .child(
                wikiButton(3, GTGuiTextures.OVERLAY_BUTTON_INVERT_REDSTONE, "tile.boxplusplus.boxwiki.3").pos(30, 45))
            .child(wikiButton(4, GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON, "tile.boxplusplus.boxwiki.4").pos(80, 45))
            .child(
                wikiButton(5, GTGuiTextures.OVERLAY_BUTTON_POWER_SWITCH_ON, "tile.boxplusplus.boxwiki.5").pos(130, 45))
            .child(wikiButton(6, GTGuiTextures.OVERLAY_BUTTON_PROGRESS, "tile.boxplusplus.boxwiki.6").pos(180, 45))
            .child(wikiButton(7, GTGuiTextures.OVERLAY_BUTTON_WHITELIST, "tile.boxplusplus.boxwiki.7").pos(230, 45));
        addWikiPages(panel);
        return panel;
    }

    private void addWikiPages(ModularPanel panel) {
        panel.child(
            text("tile.boxplusplus.boxwiki.8", 0.5f).pos(25, 85)
                .size(260, 110)
                .setEnabledIf(widget -> wikiPage == 3 || wikiPage == 1));
        panel.child(
            text("tile.boxplusplus.boxwiki.9", 0.5f).pos(25, 85)
                .size(260, 20)
                .setEnabledIf(widget -> wikiPage == 4));
        panel.child(
            text("tile.boxplusplus.boxwiki.10", 0.5f).pos(25, 105)
                .size(260, 80)
                .setEnabledIf(widget -> wikiPage == 4));
        panel.child(
            text("tile.boxplusplus.boxwiki.11", 0.5f).pos(25, 85)
                .size(260, 30)
                .setEnabledIf(widget -> wikiPage == 5));
        panel.child(
            text("tile.boxplusplus.boxwiki.12", 0.5f).pos(25, 115)
                .size(260, 55)
                .setEnabledIf(widget -> wikiPage == 5));
        panel.child(
            wikiButton(50, BoxGuiTextures.ARROW_GREEN_DOWN, "tile.boxplusplus.boxwiki.0").pos(135, 175)
                .setEnabledIf(widget -> wikiPage == 5));
        panel.child(
            text("tile.boxplusplus.boxwiki.15", 0.5f).pos(25, 85)
                .size(260, 110)
                .setEnabledIf(widget -> wikiPage == 50));
        panel.child(
            text("tile.boxplusplus.boxwiki.16", 0.5f).pos(25, 85)
                .size(260, 20)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            text("tile.boxplusplus.boxwiki.17", 0.5f).pos(25, 105)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            text("tile.boxplusplus.boxwiki.18", 0.5f).pos(25, 115)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            text("tile.boxplusplus.boxwiki.19", 0.5f).pos(25, 125)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            text("tile.boxplusplus.boxwiki.20", 0.5f).pos(25, 135)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            text("tile.boxplusplus.boxwiki.21", 0.5f).pos(25, 145)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            wikiButton(51, BoxGuiTextures.ARROW_GREEN_DOWN, "tile.boxplusplus.boxwiki.0").pos(135, 175)
                .setEnabledIf(widget -> wikiPage == 6));
        panel.child(
            text("tile.boxplusplus.boxwiki.22", 0.5f).pos(25, 85)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 51));
        panel.child(
            text("tile.boxplusplus.boxwiki.23", 0.5f).pos(180, 85)
                .size(100, 10)
                .setEnabledIf(widget -> wikiPage == 51));
        panel.child(
            BoxGuiTextures.TIME.asWidget()
                .pos(20, 105)
                .size(130, 42)
                .setEnabledIf(widget -> wikiPage == 51));
        panel.child(
            BoxGuiTextures.VOLTAGE.asWidget()
                .pos(190, 105)
                .size(62, 40)
                .setEnabledIf(widget -> wikiPage == 51));
        panel.child(
            text("tile.boxplusplus.boxwiki.52", 0.5f).pos(25, 165)
                .size(260, 30)
                .setEnabledIf(widget -> wikiPage == 51));
        panel.child(
            text("tile.boxplusplus.boxwiki.24", 0.5f).pos(25, 85)
                .size(260, 10)
                .setEnabledIf(widget -> wikiPage == 7));
        for (int i = 1; i < 15; i++) {
            int module = i;
            panel.child(
                new ButtonWidget<>().size(16, 16)
                    .pos(10 + 18 * i, 100)
                    .overlay(GTGuiTextures.OVERLAY_BUTTON_EMIT_REDSTONE)
                    .onMousePressed(mouseButton -> {
                        if (mouseButton == 0) {
                            wikiModule = module;
                            return true;
                        }
                        return false;
                    })
                    .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.module." + i))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .setEnabledIf(widget -> wikiPage == 7));
        }
        panel.child(
            raw(
                () -> Util.i18n("tile.boxplusplus.boxwiki.25")
                    + Util.i18n("tile.boxplusplus.boxUI.module." + wikiModule),
                0.5f).pos(25, 120)
                    .size(260, 10)
                    .setEnabledIf(widget -> wikiPage == 7));
        panel.child(
            raw(
                () -> Util.i18n("tile.boxplusplus.boxwiki.26")
                    + Util.i18n("tile.boxplusplus.boxUI.module.context." + wikiModule + "b"),
                0.5f).pos(25, 130)
                    .size(260, 20)
                    .setEnabledIf(widget -> wikiPage == 7));
        panel.child(
            raw(
                () -> Util.i18n("tile.boxplusplus.boxwiki.27")
                    + Util.i18n("tile.boxplusplus.boxUI.module.context." + wikiModule + "c"),
                0.5f).pos(25, 150)
                    .size(260, 10)
                    .setEnabledIf(widget -> wikiPage == 7));
        panel.child(
            raw(
                () -> Util.i18n("tile.boxplusplus.boxwiki.28")
                    + Util.i18n("tile.boxplusplus.boxUI.module.context." + wikiModule + "d"),
                0.5f).pos(25, 160)
                    .size(260, 10)
                    .setEnabledIf(widget -> wikiPage == 7));
        panel.child(
            raw(
                () -> Util.i18n("tile.boxplusplus.boxwiki.29")
                    + Util.i18n("tile.boxplusplus.boxUI.module.context." + wikiModule + "e"),
                0.5f).pos(25, 170)
                    .size(260, 10)
                    .setEnabledIf(widget -> wikiPage == 7));
    }

    private ListWidget<IWidget, ?> createRouteIngredientList() {
        Flow column = Flow.column()
            .width(180);
        for (int row = 0; row < MAX_ROUTE_INGREDIENT_ROWS; row++) {
            column.child(createRouteIngredientRow(row));
        }
        return new ListWidget<>().size(180, 110)
            .child(column);
    }

    private IWidget createRouteIngredientRow(int rowIndex) {
        ParentWidget<?> row = new ParentWidget<>().size(180, 16)
            .setEnabledIf(widget -> rowIndex < routeIngredientCount());
        row.child(
            itemDisplay(() -> routeItemAt(rowIndex)).pos(0, 0)
                .size(16, 16)
                .setEnabledIf(widget -> routeItemAt(rowIndex) != null));
        row.child(
            new FluidDisplayWidget().background(IDrawable.EMPTY)
                .value(new ObjectValue.Dynamic<>(FluidStack.class, () -> routeFluidAt(rowIndex), value -> {}))
                .displayAmount(true)
                .pos(0, 0)
                .size(16, 16)
                .setEnabledIf(widget -> routeFluidAt(rowIndex) != null));
        row.child(
            raw(() -> routeIngredientLine(rowIndex), 0.48f).pos(20, 4)
                .size(150, 10));
        return row;
    }

    private int routeIngredientCount() {
        BoxRoutings route = getRouting(selectedRoute);
        return route == null ? 0
            : route.InputItem.size() + route.InputFluid.size() + route.OutputItem.size() + route.OutputFluid.size();
    }

    private ItemStack routeItemAt(int rowIndex) {
        BoxRoutings route = getRouting(selectedRoute);
        if (route == null || rowIndex < 0) return null;
        if (rowIndex < route.InputItem.size()) return route.InputItem.get(rowIndex);
        rowIndex -= route.InputItem.size() + route.InputFluid.size();
        return rowIndex >= 0 && rowIndex < route.OutputItem.size() ? route.OutputItem.get(rowIndex) : null;
    }

    private FluidStack routeFluidAt(int rowIndex) {
        BoxRoutings route = getRouting(selectedRoute);
        if (route == null || rowIndex < 0) return null;
        rowIndex -= route.InputItem.size();
        if (rowIndex >= 0 && rowIndex < route.InputFluid.size()) return route.InputFluid.get(rowIndex);
        rowIndex -= route.InputFluid.size() + route.OutputItem.size();
        return rowIndex >= 0 && rowIndex < route.OutputFluid.size() ? route.OutputFluid.get(rowIndex) : null;
    }

    private String routeIngredientLine(int rowIndex) {
        BoxRoutings route = getRouting(selectedRoute);
        if (route == null || rowIndex < 0) return "";
        if (rowIndex < route.InputItem.size()) {
            return routeItemLine("tile.boxplusplus.boxUI.11", route.InputItem.get(rowIndex), rowIndex, null);
        }
        rowIndex -= route.InputItem.size();
        if (rowIndex < route.InputFluid.size()) {
            return routeFluidLine("tile.boxplusplus.boxUI.12", route.InputFluid.get(rowIndex), rowIndex);
        }
        rowIndex -= route.InputFluid.size();
        if (rowIndex < route.OutputItem.size()) {
            Integer chance = rowIndex < route.OutputChance.size() ? route.OutputChance.get(rowIndex) : null;
            return routeItemLine("tile.boxplusplus.boxUI.13", route.OutputItem.get(rowIndex), rowIndex, chance);
        }
        rowIndex -= route.OutputItem.size();
        return rowIndex < route.OutputFluid.size()
            ? routeFluidLine("tile.boxplusplus.boxUI.14", route.OutputFluid.get(rowIndex), rowIndex)
            : "";
    }

    private String routeItemLine(String key, ItemStack item, int index, Integer chance) {
        if (item == null) return "";
        String line = Util.i18n(key) + (index + 1) + ": " + item.getDisplayName() + " x" + item.stackSize;
        return chance == null ? line : line + "(" + chance / 10000.0 + ")";
    }

    private String routeFluidLine(String key, FluidStack fluid, int index) {
        return fluid == null ? ""
            : Util.i18n(key) + (index + 1) + ": " + fluid.getLocalizedName() + " " + fluid.amount + " L";
    }

    private ListWidget<IWidget, ?> createFinalInputList() {
        Flow column = Flow.column()
            .width(150);
        for (int row = 0; row < MAX_FINAL_INGREDIENT_ROWS; row++) {
            column.child(createFinalIngredientRow(true, row));
        }
        return new ListWidget<>().size(150, 130)
            .child(column);
    }

    private ListWidget<IWidget, ?> createFinalOutputList() {
        Flow column = Flow.column()
            .width(150);
        for (int row = 0; row < MAX_FINAL_INGREDIENT_ROWS; row++) {
            column.child(createFinalIngredientRow(false, row));
        }
        return new ListWidget<>().size(150, 130)
            .child(column);
    }

    private IWidget createFinalIngredientRow(boolean input, int rowIndex) {
        ParentWidget<?> row = new ParentWidget<>().size(150, 16)
            .setEnabledIf(widget -> rowIndex < finalIngredientCount(input));
        row.child(
            itemDisplay(() -> finalItemAt(input, rowIndex)).pos(0, 0)
                .size(16, 16)
                .setEnabledIf(widget -> rowIndex < finalItems(input).size()));
        row.child(
            new FluidDisplayWidget().background(IDrawable.EMPTY)
                .value(new ObjectValue.Dynamic<>(FluidStack.class, () -> finalFluidAt(input, rowIndex), value -> {}))
                .displayAmount(true)
                .pos(0, 0)
                .size(16, 16)
                .setEnabledIf(widget -> rowIndex >= finalItems(input).size()));
        row.child(
            raw(() -> finalIngredientLine(input, rowIndex), 0.48f).pos(20, 4)
                .size(125, 10));
        return row;
    }

    private List<ItemStack> finalItems(boolean input) {
        return input ? multiblock.recipe.FinalItemInput : multiblock.recipe.FinalItemOutput;
    }

    private List<FluidStack> finalFluids(boolean input) {
        return input ? multiblock.recipe.FinalFluidInput : multiblock.recipe.FinalFluidOutput;
    }

    private int finalIngredientCount(boolean input) {
        return finalItems(input).size() + finalFluids(input).size();
    }

    private ItemStack finalItemAt(boolean input, int rowIndex) {
        List<ItemStack> items = finalItems(input);
        return rowIndex >= 0 && rowIndex < items.size() ? items.get(rowIndex) : null;
    }

    private FluidStack finalFluidAt(boolean input, int rowIndex) {
        int fluidIndex = rowIndex - finalItems(input).size();
        List<FluidStack> fluids = finalFluids(input);
        return fluidIndex >= 0 && fluidIndex < fluids.size() ? fluids.get(fluidIndex) : null;
    }

    private String finalIngredientLine(boolean input, int rowIndex) {
        List<ItemStack> items = finalItems(input);
        if (rowIndex >= 0 && rowIndex < items.size() && items.get(rowIndex) != null) {
            ItemStack item = items.get(rowIndex);
            return Util.i18n(input ? "tile.boxplusplus.boxUI.11" : "tile.boxplusplus.boxUI.13") + (rowIndex + 1)
                + ": "
                + item.getDisplayName()
                + " x"
                + item.stackSize;
        }
        int fluidIndex = rowIndex - items.size();
        List<FluidStack> fluids = finalFluids(input);
        if (fluidIndex >= 0 && fluidIndex < fluids.size() && fluids.get(fluidIndex) != null) {
            FluidStack fluid = fluids.get(fluidIndex);
            return Util.i18n(input ? "tile.boxplusplus.boxUI.12" : "tile.boxplusplus.boxUI.14") + (fluidIndex + 1)
                + ": "
                + fluid.getLocalizedName()
                + " "
                + fluid.amount
                + " L";
        }
        return "";
    }

    private ButtonWidget<?> panelButton(IDrawable overlay, String tooltipKey, IPanelHandler panel) {
        return applyModernButton(
            new ButtonWidget<>().size(16, 16)
                .overlay(overlay)
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0) return false;
                    if (panel.isPanelOpen()) panel.closePanel();
                    else panel.openPanel();
                    return true;
                })
                .addTooltipLine(Util.i18n(tooltipKey))
                .tooltipShowUpTimer(TOOLTIP_DELAY),
            () -> true);
    }

    private ButtonWidget<?> routingButton(PanelSyncManager syncManager, IPanelHandler panel) {
        return applyModernButton(
            new ButtonWidget<>().size(16, 16)
                .overlay(GTGuiTextures.OVERLAY_BUTTON_IMPORT)
                .syncHandler(syncManager.findSyncHandler("boxBindRouting", InteractionSyncHandler.class))
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0) return false;
                    mirrorBindRoutingContext();
                    if (panel.isPanelOpen()) panel.closePanel();
                    else panel.openPanel();
                    return false;
                })
                .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.03"))
                .tooltipShowUpTimer(TOOLTIP_DELAY),
            () -> true);
    }

    private ButtonWidget<?> actionButton(PanelSyncManager syncManager, String action, IDrawable overlay,
        String tooltipKey, java.util.function.BooleanSupplier clientMirror) {
        return applyModernButton(
            new ButtonWidget<>().size(16, 16)
                .overlay(overlay)
                .syncHandler(syncManager.findSyncHandler(action, InteractionSyncHandler.class))
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0) return false;
                    clientMirror.getAsBoolean();
                    return false;
                })
                .addTooltipLine(Util.i18n(tooltipKey))
                .tooltipShowUpTimer(TOOLTIP_DELAY),
            () -> true);
    }

    private ButtonWidget<?> actionOpenButton(PanelSyncManager syncManager, String action, IDrawable overlay,
        String tooltipKey, IPanelHandler panel, java.util.function.BooleanSupplier clientMirror) {
        return applyModernButton(
            new ButtonWidget<>().size(16, 16)
                .overlay(overlay)
                .syncHandler(syncManager.findSyncHandler(action, InteractionSyncHandler.class))
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0) return false;
                    if (clientMirror.getAsBoolean()) {
                        panel.openPanel();
                    }
                    return false;
                })
                .addTooltipLine(Util.i18n(tooltipKey))
                .tooltipShowUpTimer(TOOLTIP_DELAY),
            () -> true);
    }

    private ButtonWidget<?> ringButton(PanelSyncManager syncManager, String action, String tooltipKey,
        java.util.function.BooleanSupplier clientMirror) {
        return actionButton(
            syncManager,
            action,
            GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_FLUID,
            tooltipKey,
            clientMirror);
    }

    private ButtonWidget<?> exportRoutingButton(PanelSyncManager syncManager) {
        return applyModernButton(
            new ButtonWidget<>().size(16, 16)
                .overlay(GTGuiTextures.OVERLAY_BUTTON_AUTOOUTPUT_ITEM)
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0) return false;
                    GuiScreen.setClipboardString(multiblock.getRoutingConfigForGui());
                    closeClientWithMessage("tile.boxplusplus.chatmessage.2");
                    return true;
                })
                .addTooltipLine(Util.i18n("tile.boxplusplus.boxUI.31"))
                .tooltipShowUpTimer(TOOLTIP_DELAY),
            () -> true);
    }

    private ButtonWidget<?> wikiButton(int page, IDrawable overlay, String tooltipKey) {
        return applyModernStateButton(
            new ButtonWidget<>().size(16, 16)
                .overlay(overlay)
                .onMousePressed(mouseButton -> {
                    if (mouseButton != 0) return false;
                    wikiPage = page;
                    return true;
                })
                .addTooltipLine(Util.i18n(tooltipKey))
                .tooltipShowUpTimer(TOOLTIP_DELAY),
            () -> wikiPage == page,
            () -> true);
    }

    private SlotLikeButtonWidget itemSlot(java.util.function.Supplier<ItemStack> stack) {
        return new SlotLikeButtonWidget(stack).tooltipDynamic(tooltip -> {
            ItemStack item = stack.get();
            if (item != null) tooltip.addFromItem(item);
        })
            .tooltipAutoUpdate(true);
    }

    private ItemDisplayWidget itemDisplay(java.util.function.Supplier<ItemStack> stack) {
        return new ItemDisplayWidget().item(new ObjectValue.Dynamic<>(ItemStack.class, stack, value -> {}))
            .displayAmount(true)
            .tooltipDynamic(tooltip -> {
                ItemStack item = stack.get();
                if (item != null) tooltip.addFromItem(item);
            })
            .tooltipAutoUpdate(true);
    }

    private TextWidget<?> text(String key, float scale) {
        return small(IKey.str(Util.i18n(key)), scale);
    }

    private TextWidget<?> raw(java.util.function.Supplier<String> supplier, float scale) {
        return small(IKey.dynamic(supplier), scale);
    }

    private TextWidget<?> small(IKey key, float scale) {
        return new TextWidget<>(key).scale(scale);
    }

    private void serverAction(PanelSyncManager syncManager, String key, Runnable action) {
        syncManager.syncValue(
            key,
            new InteractionSyncHandler()
                .setOnMousePressed(mouse -> { if (!mouse.isClient() && mouse.mouseButton == 0) action.run(); }));
    }

    private void sendFailure(PanelSyncManager syncManager) {
        EntityPlayer player = syncManager.getPlayer();
        if (player != null) player.addChatMessage(new ChatComponentText(Util.i18n("tile.boxplusplus.boxUI.47")));
    }

    private boolean isModuleAvailable(int index, int rings) {
        if (index < 4) return true;
        if (index < 8) return rings > 1;
        return rings == 3;
    }

    private String moduleStateLine(int index) {
        return Util.i18n("tile.boxplusplus.boxUI.module.24")
            + Util.i18n("tile.boxplusplus.boxUI.module.16" + (multiblock.isModuleEnabledForGui(index) ? "" : "a"))
            + " "
            + moduleTierLabel(index);
    }

    private String moduleTierLabel(int index) {
        String row = multiblock.getBoxModuleRow(index);
        int marker = row.indexOf(" T");
        if (marker < 0 || marker + 3 > row.length()) return "";
        int end = row.indexOf(' ', marker + 1);
        return end < 0 ? row.substring(marker + 1) : row.substring(marker + 1, end);
    }

    private String routeMachineLine() {
        BoxRoutings route = getRouting(selectedRoute);
        if (route == null || route.RoutingMachine == null) return "";
        return Util.i18n("tile.boxplusplus.boxUI.15") + route.RoutingMachine.getDisplayName();
    }

    private String routeVoltageLine() {
        BoxRoutings route = getRouting(selectedRoute);
        return route == null ? "" : Util.i18n("tile.boxplusplus.boxUI.16") + route.voltage + "eu/t";
    }

    private String routeTimeLine() {
        BoxRoutings route = getRouting(selectedRoute);
        return route == null ? ""
            : Util.i18n("tile.boxplusplus.boxUI.17") + route.time / 20.00 + "s (" + route.time + "tick)";
    }

    private BoxRoutings getRouting(int oneBasedIndex) {
        int index = oneBasedIndex - 1;
        if (index < 0 || index >= multiblock.routingMap.size()) return null;
        return multiblock.routingMap.get(index);
    }

    private boolean mirrorToggleRingRender() {
        multiblock.toggleRingRender();
        return true;
    }

    private void mirrorBindRoutingContext() {
        try {
            Class.forName("com.silvermoon.boxplusplus.client.BoxClientRoutingContext")
                .getMethod("bind", GTMachineBox.class)
                .invoke(null, multiblock);
        } catch (ReflectiveOperationException ignored) {
            // Not on client, or helper not loaded.
        }
    }

    private void closeClientWithMessage(String key) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.thePlayer == null) return;
        minecraft.thePlayer.addChatMessage(new ChatComponentText(Util.i18n(key)));
        minecraft.thePlayer.closeScreen();
    }
}
