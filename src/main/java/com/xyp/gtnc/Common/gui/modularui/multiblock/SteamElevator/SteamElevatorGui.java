package com.xyp.gtnc.Common.gui.modularui.multiblock.SteamElevator;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.gui.modularui.multiblock.BaseGui.GTNCSteamMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.multiblock.steam.elevator.SteamElevator;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;

public class SteamElevatorGui extends GTNCSteamMultiBlockBaseGui {

    private static final String MACHINE_SYNC_KEY = "steamElevatorMachine";
    private static final String ALLOWED_SYNC_KEY = "steamElevatorAllowed";
    private static final String MODULE_COUNT_SYNC_KEY = "steamElevatorModules";
    private static final String MODULE_REFRESH_SYNC_KEY = "steamElevatorModuleRefresh";
    private static final String TELEPORT_SYNC_KEY = "steamElevatorTeleport";
    private static final UITexture TELEPORT_OVERLAY = UITexture
        .fullImage("gtnhintergalactic", "gui/overlay_button/planet_teleport.png");

    private final SteamElevator steamElevator;

    public SteamElevatorGui(SteamElevator steamElevator) {
        super(steamElevator);
        this.steamElevator = steamElevator;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(MACHINE_SYNC_KEY, new BooleanSyncValue(steamElevator::isMachineForGui));
        syncManager.syncValue(ALLOWED_SYNC_KEY, new BooleanSyncValue(steamElevator::isAllowedToWorkForGui));
        syncManager.syncValue(MODULE_COUNT_SYNC_KEY, new IntSyncValue(steamElevator::getNumberOfModulesForGui));
        syncManager.syncValue(
            MODULE_REFRESH_SYNC_KEY,
            new InteractionSyncHandler().setOnMousePressed(
                data -> { if (!data.isClient() && data.mouseButton == 0) steamElevator.refreshModuleConnections(); }));
        syncManager.syncValue(TELEPORT_SYNC_KEY, new InteractionSyncHandler().setOnMousePressed(data -> {
            if (!data.isClient() && data.mouseButton == 0) {
                steamElevator.openCelestialSelection(syncManager.getPlayer());
            }
        }));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        BooleanSyncValue machine = syncManager.findSyncHandler(MACHINE_SYNC_KEY, BooleanSyncValue.class);
        BooleanSyncValue allowed = syncManager.findSyncHandler(ALLOWED_SYNC_KEY, BooleanSyncValue.class);
        IntSyncValue modules = syncManager.findSyncHandler(MODULE_COUNT_SYNC_KEY, IntSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.lang("gt.interact.desc.mb.incomplete")
                .color(Color.WHITE.main)
                .asWidget()
                .textAlign(Alignment.CenterLeft)
                .setEnabledIf(widget -> !machine.getBoolValue())
                .fullWidth())
            .child(
                IKey.lang("gt.blockmachines.multimachine.ig.elevator.gui.ready")
                    .color(Color.WHITE.main)
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .setEnabledIf(widget -> machine.getBoolValue())
                    .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocal("gt.blockmachines.multimachine.ig.elevator.gui.numOfModules")
                        + ": "
                        + modules.getIntValue())
                    .color(Color.WHITE.main)
                    .asWidget()
                    .textAlign(Alignment.CenterLeft)
                    .setEnabledIf(widget -> allowed.getBoolValue())
                    .fullWidth());
    }

    @Override
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .width(18)
            .leftRel(1, -3, 1)
            .childPadding(2)
            .mainAxisAlignment(Alignment.MainAxis.END)
            .reverseLayout(true)
            .child(createWirelessModeButton(syncManager));
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
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .mainAxisAlignment(Alignment.MainAxis.END)
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .reverseLayout(true)
            .verticalCenter()
            .rightRel(0)
            .coverChildrenWidth()
            .fullHeight()
            .child(createPowerSwitchButton())
            .child(createModuleRefreshButton(syncManager))
            .child(createMilestoneButton(parent, syncManager))
            .child(createEvolutionTreeButton(parent, syncManager))
            .child(createTeleportButton(syncManager));
    }

    private IWidget createModuleRefreshButton(PanelSyncManager syncManager) {
        InteractionSyncHandler refresh = syncManager
            .findSyncHandler(MODULE_REFRESH_SYNC_KEY, InteractionSyncHandler.class);
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .overlay(GTGuiTextures.TT_OVERLAY_CYCLIC_BLUE)
            .syncHandler(refresh)
            .tooltipBuilder(
                tooltip -> tooltip.addLine(IKey.str(TextLocalization.GUI_STEAM_ELEVATOR_MODULE_REFRESH_BUTTON)))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernButton(button, () -> true);
    }

    private IWidget createMilestoneButton(ModularPanel panel, PanelSyncManager syncManager) {
        IPanelHandler milestonePanel = syncManager.syncedPanel(
            "steamElevatorMilestonePanel",
            true,
            (manager, handler) -> SteamElevatorEvolutionPanels.createMilestonePanel(manager, panel, steamElevator));
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .overlay(GTNCGuiTextures.OVERLAY_BUTTON_FLAG)
            .onMousePressed(mouse -> {
                if (milestonePanel.isPanelOpen()) milestonePanel.closePanel();
                else milestonePanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.str(TextLocalization.GUI_STEAM_ELEVATOR_MILESTONE_BUTTON)))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernStateButton(button, milestonePanel::isPanelOpen, steamElevator::isMachineForGui);
    }

    private IWidget createEvolutionTreeButton(ModularPanel panel, PanelSyncManager syncManager) {
        IPanelHandler evolutionPanel = syncManager.syncedPanel(
            "steamElevatorEvolutionPanel",
            true,
            (manager, handler) -> SteamElevatorEvolutionPanels.createEvolutionTreePanel(manager, panel, steamElevator));
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .overlay(GTNCGuiTextures.OVERLAY_BUTTON_ARROW_BLUE_UP)
            .onMousePressed(mouse -> {
                if (evolutionPanel.isPanelOpen()) evolutionPanel.closePanel();
                else evolutionPanel.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.str(TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_BUTTON)))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernStateButton(button, evolutionPanel::isPanelOpen, steamElevator::isMachineForGui);
    }

    private IWidget createTeleportButton(PanelSyncManager syncManager) {
        InteractionSyncHandler teleport = syncManager.findSyncHandler(TELEPORT_SYNC_KEY, InteractionSyncHandler.class);
        ButtonWidget<?> button = new ButtonWidget<>().size(16)
            .playClickSound(false)
            .overlay(TELEPORT_OVERLAY)
            .syncHandler(teleport)
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("ig.button.travel")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernButton(button, steamElevator::isMachineForGui);
    }
}
