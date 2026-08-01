package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;
import gregtech.common.gui.modularui.multiblock.godforge.data.Milestones;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Panels;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncHypervisor;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncValues;

/** Original four-quadrant milestone screen with steam-localized categories. */
public final class SteamGodforgeMilestonePanel {

    private SteamGodforgeMilestonePanel() {}

    public static ModularPanel openPanel(SyncHypervisor hypervisor) {
        ModularPanel panel = hypervisor.getModularPanel(Panels.MILESTONE);
        SyncValues.MILESTONE_CLICKED.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_CHARGE_PROGRESS.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_CHARGE_PROGRESS_INVERTED.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_CONVERSION_PROGRESS.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_CONVERSION_PROGRESS_INVERTED.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_CATALYST_PROGRESS.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_CATALYST_PROGRESS_INVERTED.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_COMPOSITION_PROGRESS.registerFor(Panels.MILESTONE, hypervisor);
        SyncValues.MILESTONE_COMPOSITION_PROGRESS_INVERTED.registerFor(Panels.MILESTONE, hypervisor);

        panel.size(400, 300)
            .background(GTGuiTextures.BACKGROUND_SPACE)
            .disableHoverBackground()
            .child(ForgeOfGodsGuiUtil.panelCloseButton());
        for (Milestones milestone : Milestones.VALUES) panel.child(createMilestone(milestone, hypervisor));
        return panel;
    }

    private static ParentWidget<?> createMilestone(Milestones milestone, SyncHypervisor hypervisor) {
        IPanelHandler detail = Panels.INDIVIDUAL_MILESTONE.getFrom(Panels.MILESTONE, hypervisor);
        EnumSyncValue<Milestones, ?> selected = SyncValues.MILESTONE_CLICKED.lookupFrom(Panels.MILESTONE, hypervisor);
        FloatSyncValue progress = milestone.getProgressSyncer()
            .lookupFrom(Panels.MILESTONE, hypervisor);
        FloatSyncValue inverted = milestone.getProgressInvertedSyncer()
            .lookupFrom(Panels.MILESTONE, hypervisor);
        ParentWidget<?> parent = new ParentWidget<>().size(130, 100)
            .margin(37, 24);
        if (milestone.getPosition() == Alignment.TopLeft) parent.topRel(0)
            .leftRel(0);
        if (milestone.getPosition() == Alignment.TopRight) parent.topRel(0)
            .rightRel(0);
        if (milestone.getPosition() == Alignment.BottomLeft) parent.bottomRel(0)
            .leftRel(0);
        if (milestone.getPosition() == Alignment.BottomRight) parent.bottomRel(0)
            .rightRel(0);

        parent.child(
            new ButtonWidget<>().horizontalCenter()
                .size(milestone.getMainWidth(), milestone.getMainHeight())
                .background(milestone.getMainBackground())
                .disableHoverBackground()
                .onMousePressed(button -> {
                    selected.setValue(milestone);
                    if (!detail.isPanelOpen()) detail.openPanel();
                    return true;
                })
                .tooltip(t -> t.addLine(TextLocalization.STEAM_GODFORGE_MILESTONE_NAMES[milestone.ordinal()]))
                .tooltipShowUpTimer(TOOLTIP_DELAY)
                .clickSound(ForgeOfGodsGuiUtil.getButtonSound()));
        parent.child(
            new ProgressWidget().value(new DoubleSyncValue(progress::getDoubleValue))
                .texture(
                    GTGuiTextures.PROGRESSBAR_GODFORGE_MILESTONE_BACKGROUND,
                    milestone.getProgressBarMainOverlay(),
                    -1)
                .direction(ProgressWidget.Direction.RIGHT)
                .verticalCenter()
                .widthRel(1.0f)
                .height(7));
        parent.child(
            new ProgressWidget().value(new DoubleSyncValue(inverted::getDoubleValue))
                .texture(GTGuiTextures.TRANSPARENT, milestone.getProgressBarInvertedOverlay(), -1)
                .direction(ProgressWidget.Direction.LEFT)
                .verticalCenter()
                .widthRel(1.0f)
                .height(7));
        return parent.child(
            IKey.str(TextLocalization.STEAM_GODFORGE_MILESTONE_NAMES[milestone.ordinal()])
                .style(EnumChatFormatting.GOLD)
                .asWidget()
                .topRel(0.35f)
                .horizontalCenter());
    }
}
