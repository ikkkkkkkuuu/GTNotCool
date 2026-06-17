package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.xyp.gtnc.Common.gui.modularui.widget.BeeSpeciesDropTextField;
import com.xyp.gtnc.Common.machines.bee.BeeBreedingHelper;
import com.xyp.gtnc.Common.machines.multiblock.steam.LargeSteamBeeBreeder;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/**
 * GUI for Large Steam Bee Breeder.
 * <p>
 * Features:
 * <ul>
 * <li>Species name text input to set the breeding target</li>
 * <li>Terminal display showing breeding pool state, chain progress, pending outputs</li>
 * <li>Drone Pool button to view all available species in the pool</li>
 * </ul>
 */
public class LargeSteamBeeBreederGui extends MTEMultiBlockBaseGui<LargeSteamBeeBreeder> {

    private final LargeSteamBeeBreeder breeder;

    private static final int DISPLAY_ROW_HEIGHT = 12;

    public LargeSteamBeeBreederGui(LargeSteamBeeBreeder breeder) {
        super(breeder);
        this.breeder = breeder;
    }

    // ==================== Sync Values ====================

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            "targetSpecies",
            new StringSyncValue(
                () -> BeeBreedingHelper.getSpeciesDisplayName(breeder.getTargetBeeSpecies()),
                breeder::setTargetBeeSpecies).allowC2S());
        syncManager.syncValue("poolSize", new IntSyncValue(breeder::getSyncedPoolSize));
        syncManager.syncValue("chainTotal", new IntSyncValue(breeder::getChainTotalSteps));
        syncManager.syncValue("chainCompleted", new IntSyncValue(breeder::getChainCompletedSteps));
        syncManager.syncValue("pendingOutputs", new IntSyncValue(breeder::getPendingPrincessOutputs));
        syncManager.syncValue("allBlocked", new BooleanSyncValue(breeder::isAllTasksBlocked));
        syncManager.syncValue("missingSpecies", new StringSyncValue(breeder::getSyncedMissingInfo, val -> {}));
        syncManager.syncValue("poolSummary", new StringSyncValue(breeder::getSyncedPoolSummary, val -> {}));
        syncManager.syncValue("chainSummary", new StringSyncValue(breeder::getSyncedChainSummary, val -> {}));
    }

    // ==================== Main Column Layout ====================

    @Override
    public Flow createMainColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return Flow.column()
            .padding(4)
            .child(createTerminalRow(panel, syncManager))
            .child(createSpeciesInputRow(panel, syncManager))
            .childIf(multiblock.canBeMuffled(), this::createMuffleButton)
            .childIf(multiblock.supportsInventoryRow(), () -> createInventoryRow(panel, syncManager));
    }

    // ==================== Species Input Row ====================

    private IWidget createSpeciesInputRow(ModularPanel panel, PanelSyncManager syncManager) {
        StringSyncValue speciesSync = syncManager.findSyncHandler("targetSpecies", StringSyncValue.class);
        IntSyncValue chainTotalSync = syncManager.findSyncHandler("chainTotal", IntSyncValue.class);
        IntSyncValue chainCompletedSync = syncManager.findSyncHandler("chainCompleted", IntSyncValue.class);
        StringSyncValue chainSummarySync = syncManager.findSyncHandler("chainSummary", StringSyncValue.class);
        TextWidget label = new TextWidget(IKey.lang("Target: "));
        label.widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
        BeeSpeciesDropTextField input = new BeeSpeciesDropTextField();
        input.value(speciesSync);
        input.size(120, 14);
        // #tr gt.blockmachines.multimachine.bee.species.tooltip
        // # Input the target bee species name, or drag a bee from NEI
        // # zh_CN 输入目标蜜蜂品种名称，或从NEI拖入蜜蜂
        input.tooltipBuilder(t -> t.addLine(translateToLocal("gt.blockmachines.multimachine.bee.species.tooltip")));
        IPanelHandler chainPanel = syncManager
            .syncedPanel("chainPanel", false, (sm, sh) -> createChainPanel(syncManager, panel));
        ButtonWidget chainBtn = new ButtonWidget<>().size(18, 18)
            .overlay(IKey.dynamic(() -> {
                int total = chainTotalSync.getIntValue();
                int completed = chainCompletedSync.getIntValue();
                if (total == 0) return EnumChatFormatting.DARK_GRAY + "--";
                return EnumChatFormatting.YELLOW + "" + completed + "/" + total;
            }))
            .onMousePressed(d -> {
                if (!chainPanel.isPanelOpen()) {
                    chainPanel.openPanel();
                } else {
                    chainPanel.closePanel();
                }
                return true;
            })
            .tooltipBuilder(t -> t.addLine(IKey.lang("View Chain Status")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return Flow.row()
            .fullWidth()
            .height(20)
            .paddingLeft(5)
            .paddingRight(5)
            .marginTop(4)
            .marginBottom(2)
            .child(label)
            .child(input)
            .child(chainBtn);
    }

    // ==================== Button Control ====================

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
    protected Flow createButtonColumn(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createButtonColumn(panel, syncManager).child(createDronePoolButton(syncManager, panel));
    }

    private IWidget createDronePoolButton(PanelSyncManager syncManager, ModularPanel parent) {
        IPanelHandler poolPanel = syncManager
            .syncedPanel("dronePoolPanel", false, (sm, sh) -> createPoolPanel(syncManager, parent));
        return new ButtonWidget<>().size(18, 18)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_WHITELIST)
            .onMousePressed(d -> {
                if (!poolPanel.isPanelOpen()) {
                    poolPanel.openPanel();
                } else {
                    poolPanel.closePanel();
                }
                return true;
            })
            .tooltipBuilder(t -> t.addLine(IKey.lang("View Drone Pool")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    // ==================== Drone Pool Sub-Panel ====================

    private ModularPanel createPoolPanel(PanelSyncManager syncManager, ModularPanel parent) {
        StringSyncValue poolSummary = syncManager.findSyncHandler("poolSummary", StringSyncValue.class);
        IntSyncValue poolSize = syncManager.findSyncHandler("poolSize", IntSyncValue.class);

        ModularPanel panel = ModularPanel.defaultPanel("dronePoolPanel")
            .relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(140, 160)
            .widgetTheme(GTWidgetThemes.BACKGROUND_TERMINAL);

        ListWidget<IWidget, ?> list = new ListWidget<>();
        list.padding(6)
            .size(140, 160)
            .child(
                IKey.lang("Drone Pool")
                    .asWidget()
                    .height(12)
                    .marginBottom(2))
            .child(new TextWidget<>(IKey.dynamic(() -> {
                String summary = poolSummary.getStringValue();
                if (summary == null || summary.isEmpty()) {
                    return EnumChatFormatting.GRAY + "(empty)";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(EnumChatFormatting.GOLD)
                    .append("Count: ")
                    .append(poolSize.getIntValue())
                    .append("\n\n")
                    .append(EnumChatFormatting.AQUA);
                String[] species = summary.split("\\|");
                for (String s : species) {
                    if (!s.isEmpty()) {
                        sb.append("  \u2022 ")
                            .append(s)
                            .append("\n");
                    }
                }
                return sb.toString();
            })).widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE));

        return panel.child(list);
    }

    // ==================== Chain Status Sub-Panel ====================

    private ModularPanel createChainPanel(PanelSyncManager syncManager, ModularPanel parent) {
        StringSyncValue chainSummary = syncManager.findSyncHandler("chainSummary", StringSyncValue.class);
        IntSyncValue chainTotal = syncManager.findSyncHandler("chainTotal", IntSyncValue.class);
        IntSyncValue chainCompleted = syncManager.findSyncHandler("chainCompleted", IntSyncValue.class);

        ModularPanel panel = ModularPanel.defaultPanel("chainPanel")
            .relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(180, 160)
            .widgetTheme(GTWidgetThemes.BACKGROUND_TERMINAL);

        ListWidget<IWidget, ?> list = new ListWidget<>();
        list.padding(6)
            .size(180, 160)
            .child(
                IKey.lang("Breeding Chain")
                    .asWidget()
                    .height(12)
                    .marginBottom(2))
            .child(new TextWidget<>(IKey.dynamic(() -> {
                int total = chainTotal.getIntValue();
                int completed = chainCompleted.getIntValue();
                String summary = chainSummary.getStringValue();
                StringBuilder sb = new StringBuilder();
                sb.append(EnumChatFormatting.GOLD)
                    .append("Progress: ")
                    .append(completed)
                    .append("/")
                    .append(total)
                    .append("\n\n");
                if (summary == null || summary.isEmpty()) {
                    sb.append(EnumChatFormatting.GRAY)
                        .append("(empty)");
                } else {
                    String[] steps = summary.split("\\|");
                    for (int i = 0; i < steps.length; i++) {
                        String step = steps[i];
                        if (step.isEmpty()) continue;
                        String[] parts = step.split(",", 5);
                        if (parts.length < 5) continue;
                        String status = parts[0];
                        String p1 = parts[1];
                        String p2 = parts[2];
                        String result = parts[3];
                        String chance = parts[4];
                        String icon;
                        String color;
                        if ("D".equals(status)) {
                            icon = "\u2713";
                            color = EnumChatFormatting.GREEN.toString();
                        } else if ("R".equals(status)) {
                            icon = "\u2192";
                            color = EnumChatFormatting.YELLOW.toString();
                        } else {
                            icon = "\u2717";
                            color = EnumChatFormatting.RED.toString();
                        }
                        sb.append(color)
                            .append("[")
                            .append(icon)
                            .append("] ")
                            .append(EnumChatFormatting.WHITE)
                            .append(p1)
                            .append(" + ")
                            .append(p2)
                            .append(" \u2192 ")
                            .append(result)
                            .append(" (")
                            .append(EnumChatFormatting.GOLD)
                            .append(chance)
                            .append("%")
                            .append(EnumChatFormatting.WHITE)
                            .append(")");
                        if (i < steps.length - 1) {
                            sb.append("\n");
                        }
                    }
                }
                return sb.toString();
            })).widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE));

        return panel.child(list);
    }

    // ==================== Terminal Text Widget ====================

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return super.createTerminalTextWidget(syncManager, parent).child(createTargetSpeciesRow(syncManager))
            .child(createCurrentChainRow(syncManager))
            .child(createPendingOutputRow(syncManager))
            .child(createMissingRow(syncManager));
    }

    private IWidget createCurrentChainRow(PanelSyncManager syncManager) {
        StringSyncValue chainSummarySync = syncManager.findSyncHandler("chainSummary", StringSyncValue.class);
        return new TextWidget<>(IKey.dynamic(() -> {
            String summary = chainSummarySync.getStringValue();
            if (summary == null || summary.isEmpty()) return "";
            String[] steps = summary.split("\\|");
            for (String step : steps) {
                if (step.isEmpty()) continue;
                String[] parts = step.split(",", 5);
                if (parts.length < 5) continue;
                if (!"R".equals(parts[0])) continue;
                return EnumChatFormatting.YELLOW + "\u2192 "
                    + EnumChatFormatting.WHITE
                    + parts[1]
                    + " + "
                    + parts[2]
                    + " \u2192 "
                    + EnumChatFormatting.GOLD
                    + parts[3]
                    + EnumChatFormatting.WHITE
                    + " ("
                    + EnumChatFormatting.AQUA
                    + parts[4]
                    + "%"
                    + EnumChatFormatting.WHITE
                    + ")";
            }
            return "";
        })).height(DISPLAY_ROW_HEIGHT)
            .scale(0.75f)
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
    }

    private IWidget createTargetSpeciesRow(PanelSyncManager syncManager) {
        StringSyncValue targetSync = syncManager.findSyncHandler("targetSpecies", StringSyncValue.class);
        return Flow.row()
            .fullWidth()
            .height(DISPLAY_ROW_HEIGHT)
            .child(
                new TextWidget<>(IKey.lang("Target: ")).widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                    .scale(0.75f))
            .child(new TextWidget<>(IKey.dynamic(() -> {
                String val = targetSync.getStringValue();
                return (val == null || val.isEmpty())
                    // #tr gt.blockmachines.multimachine.notset
                    // # Not Set
                    // # zh_CN 未设置
                    ? EnumChatFormatting.GRAY + translateToLocal("gt.blockmachines.multimachine.notset")
                    : EnumChatFormatting.GOLD + val;
            })).widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                .scale(0.75f));
    }

    private IWidget createPendingOutputRow(PanelSyncManager syncManager) {
        IntSyncValue pendingSync = syncManager.findSyncHandler("pendingOutputs", IntSyncValue.class);
        return new TextWidget<>(IKey.dynamic(() -> {
            int pending = pendingSync.getIntValue();
            // #tr gt.blockmachines.multimachine.pending
            // # Pending
            // # zh_CN 待输出
            return EnumChatFormatting.AQUA + translateToLocal("gt.blockmachines.multimachine.pending")
                + ": "
                + EnumChatFormatting.GREEN
                + pending;
        })).height(DISPLAY_ROW_HEIGHT)
            .scale(0.75f)
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
    }

    private IWidget createMissingRow(PanelSyncManager syncManager) {
        BooleanSyncValue blockedSync = syncManager.findSyncHandler("allBlocked", BooleanSyncValue.class);
        StringSyncValue missingSync = syncManager.findSyncHandler("missingSpecies", StringSyncValue.class);
        return new TextWidget<>(IKey.dynamic(() -> {
            boolean blocked = blockedSync.getBoolValue();
            if (!blocked) return "";
            String missing = missingSync.getStringValue();
            // #tr gt.blockmachines.multimachine.missing
            // # Missing
            // # zh_CN 缺少
            return EnumChatFormatting.RED + translateToLocal("gt.blockmachines.multimachine.missing")
                + ": "
                + EnumChatFormatting.GOLD
                + (missing != null && !missing.isEmpty() ? missing : "?");
        })).height(DISPLAY_ROW_HEIGHT)
            .scale(0.75f)
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE);
    }

    // ==================== Muffler Position ====================

    @Override
    protected int getMufflerPosFromRightOutwards() {
        return 15;
    }
}
