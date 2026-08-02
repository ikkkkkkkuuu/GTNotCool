package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.FluidDisplayWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamGodforgeMilestones;
import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamGodforgeUpgradeCosts;
import com.xyp.gtnc.Common.material.GTNCMaterials;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;
import gregtech.common.gui.modularui.multiblock.godforge.data.Milestones;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Modules;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Panels;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncActions;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncHypervisor;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncValues;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;
import tectech.thing.metaTileEntity.multi.godforge.util.GodforgeMath;

/** Steam-localized replacements for the original Godforge detail panels. */
public final class SteamGodforgePanels {

    private SteamGodforgePanels() {}

    public static ModularPanel openUpgrade(SyncHypervisor hypervisor) {
        ModularPanel panel = hypervisor.getModularPanel(Panels.INDIVIDUAL_UPGRADE);
        SyncValues.AVAILABLE_GRAVITON_SHARDS.registerFor(Panels.INDIVIDUAL_UPGRADE, hypervisor);
        SyncActions.COMPLETE_UPGRADE.registerFor(Panels.INDIVIDUAL_UPGRADE, hypervisor);
        SyncActions.RESPEC_UPGRADE.registerFor(Panels.INDIVIDUAL_UPGRADE, hypervisor);

        EnumSyncValue<ForgeOfGodsUpgrade, ?> upgradeSyncer = SyncValues.UPGRADE_CLICKED
            .lookupFrom(Panels.UPGRADE_TREE, hypervisor);
        /*
         * Only the server refreshes this dynamic widget. The tree panel first sends the selected upgrade
         * ordinal through SELECT_UPGRADE_ACTION, then sends the normal REFRESH_DYNAMIC action.
         * Do not attach a change listener here: changing the client-side enum would otherwise make the
         * client rebuild and send a dynamic widget before the server has selected the same upgrade.
         */
        DynamicSyncHandler handler = new DynamicSyncHandler().widgetProvider(($, $$) -> {
            ForgeOfGodsUpgrade upgrade = upgradeSyncer.getValue();
            panel.size(upgrade.getPanelSize())
                .background(upgrade.getBackground())
                .disableHoverBackground();
            return buildUpgrade(upgrade, hypervisor);
        });
        return panel.child(
            new DynamicSyncedWidget<>().coverChildren()
                .syncHandler(handler));
    }

    private static ParentWidget<?> buildUpgrade(ForgeOfGodsUpgrade upgrade, SyncHypervisor hypervisor) {
        int size = upgrade.getPanelSize();
        ParentWidget<?> parent = new ParentWidget<>().size(size)
            .child(ForgeOfGodsGuiUtil.panelCloseButton())
            .child(
                upgrade.getSymbol()
                    .asWidget()
                    .size((int) (size / 2.0f * upgrade.getSymbolWidthRatio()), size / 2)
                    .center())
            .child(
                upgrade.getOverlay()
                    .asWidget()
                    .size(size / 2)
                    .center());

        Flow column = Flow.column()
            .size(size - 14, size - 18)
            .marginTop(10)
            .horizontalCenter()
            .childPadding(3)
            .child(
                IKey.str(TextLocalization.STEAM_GODFORGE_UPGRADE_NAMES[upgrade.ordinal()])
                    .style(EnumChatFormatting.GOLD)
                    .alignment(Alignment.CENTER)
                    .asWidget())
            .child(
                IKey.str(TextLocalization.STEAM_GODFORGE_UPGRADE_BODIES[upgrade.ordinal()])
                    .alignment(Alignment.CENTER)
                    .scale(0.72f)
                    .asWidget()
                    .height(48))
            .child(
                IKey.str(TextLocalization.STEAM_GODFORGE_UPGRADE_LORE)
                    .style(EnumChatFormatting.ITALIC)
                    .color(0xFFBBBDBD)
                    .alignment(Alignment.CENTER)
                    .scale(0.65f)
                    .asWidget()
                    .height(18));

        List<ItemStack> costs = SteamGodforgeUpgradeCosts.get(upgrade);
        column.child(
            IKey.str(TextLocalization.STEAM_GODFORGE_UPGRADE_MATERIALS)
                .style(EnumChatFormatting.AQUA)
                .alignment(Alignment.CENTER)
                .scale(0.68f)
                .asWidget());
        if (costs.isEmpty()) {
            column.child(
                IKey.str("-")
                    .alignment(Alignment.CENTER)
                    .asWidget());
        } else {
            Flow costColumn = Flow.column()
                .coverChildren()
                .childPadding(1);
            for (ItemStack cost : costs) {
                costColumn.child(
                    IKey.str(cost.stackSize + " x " + cost.getDisplayName())
                        .alignment(Alignment.CENTER)
                        .scale(0.62f)
                        .asWidget());
            }
            column.child(costColumn);
        }

        ParentWidget<?> bottom = new ParentWidget<>().fullWidth()
            .height(24)
            .bottomRel(0);
        bottom.child(
            IKey.dynamic(
                () -> TextLocalization.STEAM_GODFORGE_INSIGHT_COST + ": "
                    + EnumChatFormatting.BLUE
                    + upgrade.getShardCost())
                .scale(0.62f)
                .asWidget()
                .size(72, 12)
                .leftRel(0));
        bottom.child(
            IKey.dynamic(
                () -> TextLocalization.STEAM_GODFORGE_INSIGHT_AVAILABLE + ": "
                    + EnumChatFormatting.GREEN
                    + hypervisor.getData()
                        .getGravitonShardsAvailable())
                .alignment(Alignment.CENTER)
                .scale(0.62f)
                .asWidget()
                .size(72, 12)
                .rightRel(0));
        bottom.child(
            new ButtonWidget<>().size(52, 14)
                .horizontalCenter()
                .bottomRel(0)
                .background(
                    new DynamicDrawable(
                        () -> hypervisor.getData()
                            .isUpgradeActive(upgrade) ? GTGuiTextures.BUTTON_OUTLINE_HOLLOW_PRESSED
                                : GTGuiTextures.BUTTON_OUTLINE_HOLLOW))
                .overlay(
                    new DynamicDrawable(
                        () -> IKey.lang(
                            hypervisor.getData()
                                .isUpgradeActive(upgrade) ? "fog.upgrade.respec" : "fog.upgrade.confirm")
                            .alignment(Alignment.CENTER)
                            .scale(0.7f)))
                .onMousePressed(button -> {
                    if (hypervisor.getData()
                        .isUpgradeActive(upgrade)) {
                        SyncActions.RESPEC_UPGRADE.callFrom(Panels.INDIVIDUAL_UPGRADE, hypervisor, upgrade);
                    } else {
                        SyncActions.COMPLETE_UPGRADE.callFrom(Panels.INDIVIDUAL_UPGRADE, hypervisor, upgrade);
                    }
                    return true;
                })
                .tooltip(t -> { for (ItemStack cost : costs) t.addFromItem(cost); })
                .tooltipShowUpTimer(TOOLTIP_DELAY)
                .clickSound(ForgeOfGodsGuiUtil.getButtonSound()));
        column.child(bottom);
        return parent.child(column);
    }

    public static ModularPanel openMilestoneDetail(SyncHypervisor hypervisor) {
        ModularPanel panel = hypervisor.getModularPanel(Panels.INDIVIDUAL_MILESTONE);
        registerMilestoneSync(hypervisor);
        panel.size(150)
            .background(GTGuiTextures.BACKGROUND_GLOW_WHITE)
            .disableHoverBackground()
            .child(ForgeOfGodsGuiUtil.panelCloseButton());

        EnumSyncValue<Milestones, ?> selected = SyncValues.MILESTONE_CLICKED.lookupFrom(Panels.MILESTONE, hypervisor);
        for (Milestones milestone : Milestones.VALUES) panel.child(milestoneBackground(milestone, selected));

        BooleanSyncValue inversion = SyncValues.INVERSION
            .lookupFrom(Modules.CORE, Panels.INDIVIDUAL_MILESTONE, hypervisor);
        Flow column = Flow.column()
            .width(144)
            .coverChildrenHeight()
            .horizontalCenter()
            .marginTop(14)
            .childPadding(5)
            .child(
                IKey.dynamic(
                    () -> TextLocalization.STEAM_GODFORGE_MILESTONE_NAMES[selected.getValue()
                        .ordinal()])
                    .style(EnumChatFormatting.GOLD)
                    .alignment(Alignment.CENTER)
                    .asWidget())
            .child(
                IKey.dynamic(
                    () -> TextLocalization.STEAM_GODFORGE_MILESTONE_DESCRIPTIONS[selected.getValue()
                        .ordinal()])
                    .style(EnumChatFormatting.ITALIC)
                    .color(0xFFBBBDBD)
                    .alignment(Alignment.CENTER)
                    .scale(0.65f)
                    .asWidget()
                    .size(140, 24))
            .child(info(() -> milestoneTotal(selected.getValue(), hypervisor)))
            .child(info(() -> milestoneLevel(selected.getValue(), inversion.getBoolValue(), hypervisor)))
            .child(info(() -> milestoneNext(selected.getValue(), hypervisor)))
            .child(info(() -> milestoneInsight(selected.getValue(), inversion.getBoolValue(), hypervisor)));
        return panel.child(column);
    }

    private static void registerMilestoneSync(SyncHypervisor hypervisor) {
        SyncValues.INVERSION.registerFor(Modules.CORE, Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.TOTAL_RECIPES_PROCESSED.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.TOTAL_POWER_CONSUMED.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.TOTAL_FUEL_CONSUMED.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.MILESTONE_CHARGE_LEVEL.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.MILESTONE_CONVERSION_LEVEL.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.MILESTONE_CATALYST_LEVEL.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
        SyncValues.MILESTONE_COMPOSITION_LEVEL.registerFor(Panels.INDIVIDUAL_MILESTONE, hypervisor);
    }

    private static Widget<?> milestoneBackground(Milestones milestone, EnumSyncValue<Milestones, ?> selected) {
        return milestone.getSymbolBackground()
            .asWidget()
            .size(milestone.getSymbolWidth(), milestone.getSymbolHeight())
            .center()
            .setEnabledIf($ -> selected.getValue() == milestone);
    }

    private static Widget<?> info(java.util.function.Supplier<String> text) {
        return IKey.dynamic(text)
            .alignment(Alignment.CENTER)
            .scale(0.7f)
            .asWidget()
            .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
            .width(140);
    }

    private static String milestoneTotal(Milestones milestone, SyncHypervisor hypervisor) {
        Number value = milestone.getTotalSyncer()
            .lookupFrom(Panels.INDIVIDUAL_MILESTONE, hypervisor)
            .getValue();
        return TextLocalization.STEAM_GODFORGE_MILESTONE_TOTAL + ": "
            + EnumChatFormatting.GRAY
            + hypervisor.getData()
                .getFormatter()
                .format(value)
            + " "
            + TextLocalization.STEAM_GODFORGE_MILESTONE_UNITS[milestone.ordinal()];
    }

    private static String milestoneLevel(Milestones milestone, boolean inversion, SyncHypervisor hypervisor) {
        int raw = milestone.getLevelSyncer()
            .lookupFrom(Panels.INDIVIDUAL_MILESTONE, hypervisor)
            .getValue();
        return TextLocalization.STEAM_GODFORGE_MILESTONE_LEVEL + ": "
            + EnumChatFormatting.GRAY
            + (inversion ? raw : Math.min(raw, 7));
    }

    private static String milestoneNext(Milestones milestone, SyncHypervisor hypervisor) {
        int level = milestone.getLevelSyncer()
            .lookupFrom(Panels.INDIVIDUAL_MILESTONE, hypervisor)
            .getValue();
        Number target = SteamGodforgeMilestones.getNextTarget(milestone.ordinal(), level);
        return TextLocalization.STEAM_GODFORGE_MILESTONE_NEXT + ": "
            + EnumChatFormatting.GRAY
            + hypervisor.getData()
                .getFormatter()
                .format(target)
            + " "
            + TextLocalization.STEAM_GODFORGE_MILESTONE_UNITS[milestone.ordinal()];
    }

    private static String milestoneInsight(Milestones milestone, boolean inversion, SyncHypervisor hypervisor) {
        int raw = milestone.getLevelSyncer()
            .lookupFrom(Panels.INDIVIDUAL_MILESTONE, hypervisor)
            .getValue();
        int level = inversion ? raw : Math.min(raw, 7);
        return TextLocalization.STEAM_GODFORGE_MILESTONE_INSIGHT + ": "
            + EnumChatFormatting.GRAY
            + level * (level + 1) / 2;
    }

    public static ModularPanel openFuel(SyncHypervisor hypervisor) {
        ModularPanel panel = hypervisor.getModularPanel(Panels.FUEL_CONFIG);
        ForgeOfGodsData data = hypervisor.getData();
        SyncValues.FUEL_CONSUMPTION.registerFor(Panels.FUEL_CONFIG, hypervisor);
        panel.relative(hypervisor.getModularPanel(Panels.MAIN))
            .size(92, 118)
            .topRel(0)
            .leftRelOffset(1, -3);
        Flow column = Flow.column()
            .size(92, 118)
            .child(
                IKey.str(TextLocalization.STEAM_GODFORGE_FUEL_TITLE)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .width(88)
                    .marginTop(5))
            .child(
                IKey.str(TextLocalization.STEAM_GODFORGE_FUEL_FACTOR)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .width(88)
                    .marginTop(6))
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .numbersInt(raw -> MathHelper.clamp_int(raw, 1, GodforgeMath.calculateMaxFuelFactor(data)))
                    .setTextAlignment(Alignment.CENTER)
                    .value(SyncValues.FUEL_FACTOR.create(hypervisor))
                    .scrollValues(1, 64, 4, 16)
                    .size(70, 18)
                    .horizontalCenter()
                    .marginTop(3))
            .child(
                new FluidDisplayWidget().background(IDrawable.EMPTY)
                    .value(GTNCMaterials.CompressedSteam.getMolten(1))
                    .displayAmount(false)
                    .size(20)
                    .horizontalCenter()
                    .marginTop(7))
            .child(
                IKey.str(TextLocalization.STEAM_GODFORGE_FUEL_USAGE)
                    .alignment(Alignment.CENTER)
                    .scale(0.72f)
                    .asWidget()
                    .width(88)
                    .marginTop(5))
            .child(
                IKey.dynamic(
                    () -> data.getFormatter()
                        .format(data.getFuelConsumption()) + " L/5s")
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_GRAY)
                    .width(88)
                    .marginTop(3));
        return panel.child(column);
    }
}
