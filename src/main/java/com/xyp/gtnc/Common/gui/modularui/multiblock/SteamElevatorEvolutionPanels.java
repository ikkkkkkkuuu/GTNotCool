package com.xyp.gtnc.Common.gui.modularui.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Arrays;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget.Direction;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.machines.multiblock.steam.elevator.SteamElevator;
import com.xyp.gtnc.Common.machines.multiblock.steam.elevator.SteamElevatorEvolutionData;
import com.xyp.gtnc.utils.Utils;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.data.UpgradeColor;
import gregtech.common.gui.modularui.widget.RotatedDrawable;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;

/** ModularUI2 panels for the Steam Elevator's controller-local milestones and evolution tree. */
public final class SteamElevatorEvolutionPanels {

    public static final String POINTS_AVAILABLE_SYNC_KEY = "steamElevatorEvolutionPointsAvailable";
    public static final String POINTS_EARNED_SYNC_KEY = "steamElevatorEvolutionPointsEarned";

    private static final int TREE_SIZE = 300;
    private static final int NODE_WIDTH = 40;
    private static final int NODE_HEIGHT = 15;

    private SteamElevatorEvolutionPanels() {}

    public static String milestoneValueKey(int milestone) {
        return "steamElevatorMilestoneValue" + milestone;
    }

    public static String milestoneLevelKey(int milestone) {
        return "steamElevatorMilestoneLevel" + milestone;
    }

    public static String milestoneNextKey(int milestone) {
        return "steamElevatorMilestoneNext" + milestone;
    }

    public static String upgradeActiveKey(ForgeOfGodsUpgrade upgrade) {
        return "steamElevatorEvolutionActive" + upgrade.ordinal();
    }

    public static String upgradeActionKey(ForgeOfGodsUpgrade upgrade) {
        return "steamElevatorEvolutionAction" + upgrade.ordinal();
    }

    public static ModularPanel createMilestonePanel(PanelSyncManager syncManager, ModularPanel parent,
        SteamElevator steamElevator) {
        registerMilestoneSyncValues(syncManager, steamElevator);
        IntSyncValue earned = syncManager.findSyncHandler(POINTS_EARNED_SYNC_KEY, IntSyncValue.class);
        IntSyncValue available = syncManager.findSyncHandler(POINTS_AVAILABLE_SYNC_KEY, IntSyncValue.class);
        ModularPanel panel = new ModularPanel("steamElevatorMilestonePanel").relative(parent)
            .leftRelOffset(0, 4)
            .topRelOffset(0, 3)
            .size(260, 150)
            .background(GTNCGuiTextures.MODERN_VAULT_PANEL_BORDER)
            .disableHoverBackground()
            .child(ButtonWidget.panelCloseButton());

        panel.child(
            IKey.str(TextLocalization.GUI_STEAM_ELEVATOR_MILESTONE_TITLE)
                .style(EnumChatFormatting.GOLD)
                .alignment(Alignment.CENTER)
                .asWidget()
                .horizontalCenter()
                .marginTop(7));
        panel.child(
            IKey.dynamic(
                () -> String.format(
                    TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_POINTS,
                    available.getIntValue(),
                    earned.getIntValue()))
                .style(EnumChatFormatting.AQUA)
                .alignment(Alignment.CENTER)
                .asWidget()
                .horizontalCenter()
                .marginTop(20));

        for (int milestone = 0; milestone < SteamElevatorEvolutionData.MILESTONE_COUNT; milestone++) {
            panel.child(createMilestoneRow(syncManager, milestone));
        }
        return panel;
    }

    private static Widget<?> createMilestoneRow(PanelSyncManager syncManager, int milestone) {
        LongSyncValue value = syncManager.findSyncHandler(milestoneValueKey(milestone), LongSyncValue.class);
        LongSyncValue next = syncManager.findSyncHandler(milestoneNextKey(milestone), LongSyncValue.class);
        IntSyncValue level = syncManager.findSyncHandler(milestoneLevelKey(milestone), IntSyncValue.class);
        int y = 39 + milestone * 25;

        ParentWidget<?> row = new ParentWidget<>().pos(10, y)
            .size(240, 22);
        row.child(
            IKey.dynamic(
                () -> TextLocalization.GUI_STEAM_ELEVATOR_MILESTONE_NAMES[milestone] + "  Lv."
                    + level.getIntValue()
                    + "/"
                    + SteamElevatorEvolutionData.MAX_MILESTONE_LEVEL)
                .style(EnumChatFormatting.GRAY)
                .asWidget()
                .pos(0, 0)
                .size(120, 10));
        row.child(
            IKey.dynamic(
                () -> level.getIntValue() >= SteamElevatorEvolutionData.MAX_MILESTONE_LEVEL
                    ? TextLocalization.GUI_STEAM_ELEVATOR_MILESTONE_COMPLETE
                    : Utils.formatNumbers(value.getLongValue()) + " / " + Utils.formatNumbers(next.getLongValue()))
                .style(EnumChatFormatting.DARK_GRAY)
                .alignment(Alignment.CenterRight)
                .asWidget()
                .pos(120, 0)
                .size(120, 10));
        row.child(
            new ProgressWidget().value(
                new DoubleSyncValue(
                    () -> milestoneProgress(value.getLongValue(), next.getLongValue(), level.getIntValue(), milestone)))
                .texture(GTNCGuiTextures.MODERN_BUTTON_DISABLED, GTNCGuiTextures.MODERN_BUTTON_PRESSED, -1)
                .direction(Direction.RIGHT)
                .pos(0, 13)
                .size(240, 7));
        return row;
    }

    private static double milestoneProgress(long value, long next, int level, int milestone) {
        if (level >= SteamElevatorEvolutionData.MAX_MILESTONE_LEVEL) return 1;
        long previous = level == 0 ? 0 : thresholdFor(milestone, level);
        if (next <= previous) return 1;
        return Math.max(0, Math.min(1, (value - previous) / (double) (next - previous)));
    }

    private static long thresholdFor(int milestone, int level) {
        return SteamElevatorEvolutionData.getMilestoneThreshold(milestone, level);
    }

    public static ModularPanel createEvolutionTreePanel(PanelSyncManager syncManager, ModularPanel parent,
        SteamElevator steamElevator) {
        registerEvolutionSyncValues(syncManager, steamElevator);
        IntSyncValue available = syncManager.findSyncHandler(POINTS_AVAILABLE_SYNC_KEY, IntSyncValue.class);
        ModularPanel panel = new ModularPanel("steamElevatorEvolutionPanel").relative(parent)
            .leftRelOffset(0, 4)
            .topRelOffset(0, 3)
            .size(TREE_SIZE)
            .padding(4, 0, 4, 0)
            .background(GTGuiTextures.BACKGROUND_STAR)
            .disableHoverBackground()
            .child(ButtonWidget.panelCloseButton())
            .child(
                IKey.dynamic(
                    () -> String
                        .format(TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_AVAILABLE, available.getIntValue()))
                    .style(EnumChatFormatting.AQUA)
                    .asWidget()
                    .pos(7, 5)
                    .size(180, 10));

        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.setScrollSize(957);
        ScrollWidget<?> tree = new ScrollWidget<>(scrollData).pos(0, 16)
            .size(292, 280);

        addConnectors(tree, syncManager);
        Arrays.stream(ForgeOfGodsUpgrade.VALUES)
            .map(upgrade -> createUpgradeButton(syncManager, upgrade))
            .forEach(tree::child);
        panel.child(tree);
        return panel;
    }

    private static void registerMilestoneSyncValues(PanelSyncManager syncManager, SteamElevator steamElevator) {
        SteamElevatorEvolutionData evolution = steamElevator.getEvolutionData();
        syncManager.syncValue(POINTS_EARNED_SYNC_KEY, new IntSyncValue(evolution::getEvolutionPointsEarned));
        syncManager.syncValue(POINTS_AVAILABLE_SYNC_KEY, new IntSyncValue(evolution::getEvolutionPointsAvailable));
        for (int milestone = 0; milestone < SteamElevatorEvolutionData.MILESTONE_COUNT; milestone++) {
            int index = milestone;
            syncManager
                .syncValue(milestoneValueKey(index), new LongSyncValue(() -> evolution.getMilestoneValue(index)));
            syncManager.syncValue(milestoneLevelKey(index), new IntSyncValue(() -> evolution.getMilestoneLevel(index)));
            syncManager.syncValue(
                milestoneNextKey(index),
                new LongSyncValue(
                    () -> SteamElevatorEvolutionData.getMilestoneThreshold(
                        index,
                        Math.min(
                            SteamElevatorEvolutionData.MAX_MILESTONE_LEVEL,
                            evolution.getMilestoneLevel(index) + 1))));
        }
    }

    private static void registerEvolutionSyncValues(PanelSyncManager syncManager, SteamElevator steamElevator) {
        SteamElevatorEvolutionData evolution = steamElevator.getEvolutionData();
        syncManager.syncValue(POINTS_AVAILABLE_SYNC_KEY, new IntSyncValue(evolution::getEvolutionPointsAvailable));
        for (ForgeOfGodsUpgrade upgrade : ForgeOfGodsUpgrade.VALUES) {
            syncManager
                .syncValue(upgradeActiveKey(upgrade), new BooleanSyncValue(() -> evolution.isUpgradeActive(upgrade)));
            syncManager.syncValue(upgradeActionKey(upgrade), new InteractionSyncHandler().setOnMousePressed(data -> {
                if (data.isClient()) return;
                if (data.mouseButton == 0) steamElevator.tryUnlockEvolutionUpgrade(upgrade);
                if (data.mouseButton == 1) steamElevator.tryRespecEvolutionUpgrade(upgrade);
            }));
        }
    }

    private static ButtonWidget<?> createUpgradeButton(PanelSyncManager syncManager, ForgeOfGodsUpgrade upgrade) {
        BooleanSyncValue active = syncManager.findSyncHandler(upgradeActiveKey(upgrade), BooleanSyncValue.class);
        InteractionSyncHandler action = syncManager
            .findSyncHandler(upgradeActionKey(upgrade), InteractionSyncHandler.class);
        return new ButtonWidget<>().size(NODE_WIDTH, NODE_HEIGHT)
            .pos(upgrade.getTreeX(), upgrade.getTreeY())
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .overlay(
                new DynamicDrawable(
                    () -> active.getBoolValue() ? GTGuiTextures.BUTTON_SPACE_PRESSED_32x16
                        : GTGuiTextures.BUTTON_SPACE_32x16),
                IKey.str(upgrade.name())
                    .style(EnumChatFormatting.GOLD)
                    .scale(0.8f)
                    .alignment(Alignment.CENTER))
            .syncHandler(action)
            .tooltipDynamic(tooltip -> {
                tooltip.addLine(String.format(TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_NODE, upgrade.name()));
                if (upgrade == ForgeOfGodsUpgrade.IGCC) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_IGCC_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.STEM) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_STEM_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.CFCE) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_CFCE_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.GISS) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_GISS_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.FDIM) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_FDIM_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.SA) {
                    tooltip.addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_SA_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.GPCI) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_GPCI_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.REC) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_REC_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.QGPIU) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_QGPIU_EFFECT);
                }
                if (upgrade == ForgeOfGodsUpgrade.TCT) {
                    tooltip
                        .addLine(EnumChatFormatting.GREEN + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_TCT_EFFECT);
                }
                tooltip.addLine(
                    EnumChatFormatting.AQUA
                        + String.format(TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_COST, upgrade.getShardCost()));
                tooltip.addLine(EnumChatFormatting.GRAY + TextLocalization.GUI_STEAM_ELEVATOR_EVOLUTION_INTERACTION);
            })
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private static void addConnectors(ScrollWidget<?> tree, PanelSyncManager syncManager) {
        tree.child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.START, ForgeOfGodsUpgrade.IGCC, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.IGCC, ForgeOfGodsUpgrade.STEM, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.IGCC, ForgeOfGodsUpgrade.CFCE, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.STEM, ForgeOfGodsUpgrade.GISS, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.STEM, ForgeOfGodsUpgrade.FDIM, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.CFCE, ForgeOfGodsUpgrade.FDIM, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.CFCE, ForgeOfGodsUpgrade.SA, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.FDIM, ForgeOfGodsUpgrade.GPCI, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.GPCI, ForgeOfGodsUpgrade.GEM, syncManager))
            .child(line(UpgradeColor.RED, ForgeOfGodsUpgrade.GISS, ForgeOfGodsUpgrade.REC, syncManager))
            .child(line(UpgradeColor.RED, ForgeOfGodsUpgrade.GPCI, ForgeOfGodsUpgrade.REC, syncManager))
            .child(line(UpgradeColor.RED, ForgeOfGodsUpgrade.SA, ForgeOfGodsUpgrade.CTCDD, syncManager))
            .child(line(UpgradeColor.RED, ForgeOfGodsUpgrade.GPCI, ForgeOfGodsUpgrade.CTCDD, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.REC, ForgeOfGodsUpgrade.QGPIU, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.CTCDD, ForgeOfGodsUpgrade.QGPIU, syncManager))
            .child(line(UpgradeColor.ORANGE, ForgeOfGodsUpgrade.QGPIU, ForgeOfGodsUpgrade.TCT, syncManager))
            .child(line(UpgradeColor.ORANGE, ForgeOfGodsUpgrade.TCT, ForgeOfGodsUpgrade.EPEC, syncManager))
            .child(line(UpgradeColor.ORANGE, ForgeOfGodsUpgrade.EPEC, ForgeOfGodsUpgrade.POS, syncManager))
            .child(line(UpgradeColor.ORANGE, ForgeOfGodsUpgrade.POS, ForgeOfGodsUpgrade.NGMS, syncManager))
            .child(line(UpgradeColor.PURPLE, ForgeOfGodsUpgrade.QGPIU, ForgeOfGodsUpgrade.SEFCP, syncManager))
            .child(line(UpgradeColor.PURPLE, ForgeOfGodsUpgrade.SEFCP, ForgeOfGodsUpgrade.CNTI, syncManager))
            .child(line(UpgradeColor.PURPLE, ForgeOfGodsUpgrade.CNTI, ForgeOfGodsUpgrade.NDPE, syncManager))
            .child(line(UpgradeColor.PURPLE, ForgeOfGodsUpgrade.NDPE, ForgeOfGodsUpgrade.NGMS, syncManager))
            .child(line(UpgradeColor.PURPLE, ForgeOfGodsUpgrade.CNTI, ForgeOfGodsUpgrade.DOP, syncManager))
            .child(line(UpgradeColor.GREEN, ForgeOfGodsUpgrade.QGPIU, ForgeOfGodsUpgrade.GGEBE, syncManager))
            .child(line(UpgradeColor.GREEN, ForgeOfGodsUpgrade.GGEBE, ForgeOfGodsUpgrade.IMKG, syncManager))
            .child(line(UpgradeColor.GREEN, ForgeOfGodsUpgrade.IMKG, ForgeOfGodsUpgrade.DOR, syncManager))
            .child(line(UpgradeColor.GREEN, ForgeOfGodsUpgrade.DOR, ForgeOfGodsUpgrade.NGMS, syncManager))
            .child(line(UpgradeColor.GREEN, ForgeOfGodsUpgrade.GGEBE, ForgeOfGodsUpgrade.TPTP, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.NGMS, ForgeOfGodsUpgrade.SEDS, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.SEDS, ForgeOfGodsUpgrade.PA, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.PA, ForgeOfGodsUpgrade.CD, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.CD, ForgeOfGodsUpgrade.TSE, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.TSE, ForgeOfGodsUpgrade.TBF, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.TBF, ForgeOfGodsUpgrade.EE, syncManager))
            .child(line(UpgradeColor.BLUE, ForgeOfGodsUpgrade.EE, ForgeOfGodsUpgrade.END, syncManager));
    }

    private static Widget<?> line(UpgradeColor color, ForgeOfGodsUpgrade from, ForgeOfGodsUpgrade to,
        PanelSyncManager syncManager) {
        BooleanSyncValue fromActive = syncManager.findSyncHandler(upgradeActiveKey(from), BooleanSyncValue.class);
        BooleanSyncValue toActive = syncManager.findSyncHandler(upgradeActiveKey(to), BooleanSyncValue.class);
        int fromX = from.getTreeX() + NODE_WIDTH / 2;
        int fromY = from.getTreeY() + NODE_HEIGHT / 2;
        int toX = to.getTreeX() + NODE_WIDTH / 2;
        int toY = to.getTreeY() + NODE_HEIGHT / 2;
        int width = 6;
        int height = (int) Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2));
        float rotation = (float) (Math.atan2(toY - fromY, toX - fromX) - Math.PI / 2);
        int x = (fromX + toX) / 2 - width / 2;
        int y = (fromY + toY) / 2 - height / 2;
        return new DynamicDrawable(() -> {
            UITexture texture = fromActive.getBoolValue() && toActive.getBoolValue() ? color.getOpaqueConnector()
                : color.getConnector();
            return new RotatedDrawable(texture).rotationRadian(rotation);
        }).asWidget()
            .pos(x, y)
            .size(width, height);
    }
}
