package com.xyp.gtnc.Common.gui.modularui.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.math.BigInteger;
import java.util.regex.Pattern;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.IntValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.xyp.gtnc.Common.gui.modularui.GTNCGuiTextures;
import com.xyp.gtnc.Common.machines.multiblock.steam.elevator.SteamElevatorModuleBase;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;

/** Shared GUI layer for Steam Elevator modules, including IGCC-gated per-module voltage configuration. */
public class SteamElevatorModuleGui extends GTNCSteamMultiBlockBaseGui {

    private static final String VOLTAGE_UNLOCKED_SYNC_KEY = "steamElevatorModuleVoltageUnlocked";
    private static final String CONNECTION_STATUS_SYNC_KEY = "steamElevatorModuleConnected";
    private static final String PROCESSING_VOLTAGE_SYNC_KEY = "steamElevatorModuleProcessingVoltage";
    private static final String CALCULATED_MAX_PARALLEL_SYNC_KEY = "steamElevatorModuleCalculatedMaxParallel";
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("[0-9]*");
    private static final BigInteger MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    private static final String SET_MAX_PARALLEL_SYNC_KEY = "steamElevatorModuleSetMaxParallel";
    private static final String ALWAYS_MAX_PARALLEL_SYNC_KEY = "steamElevatorModuleAlwaysMaxParallel";

    private final SteamElevatorModuleBase module;

    public SteamElevatorModuleGui(SteamElevatorModuleBase module) {
        super(module);
        this.module = module;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(VOLTAGE_UNLOCKED_SYNC_KEY, new BooleanSyncValue(module::isVoltageConfigUnlocked));
        syncManager.syncValue(CONNECTION_STATUS_SYNC_KEY, new BooleanSyncValue(module::isConnected));
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
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createLeftPanelGapRow(parent, syncManager).child(createConnectionStatus(syncManager));
    }

    private IWidget createConnectionStatus(PanelSyncManager syncManager) {
        BooleanSyncValue connected = syncManager.findSyncHandler(CONNECTION_STATUS_SYNC_KEY, BooleanSyncValue.class);
        return IKey
            .dynamic(
                () -> TextLocalization.GUI_STEAM_ELEVATOR_MODULE_CONNECTION_STATUS + " "
                    + (connected.getBoolValue() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED)
                    + (connected.getBoolValue() ? TextLocalization.GUI_STEAM_ELEVATOR_MODULE_CONNECTED
                        : TextLocalization.GUI_STEAM_ELEVATOR_MODULE_DISCONNECTED))
            .style(EnumChatFormatting.BLACK)
            .alignment(Alignment.CENTER)
            .asWidget()
            .size(100, 16);
    }

    @Override
    protected ButtonWidget<?> createPowerPanelButton(PanelSyncManager syncManager, ModularPanel parent) {
        BooleanSyncValue unlocked = syncManager.findSyncHandler(VOLTAGE_UNLOCKED_SYNC_KEY, BooleanSyncValue.class);
        IPanelHandler powerPanel = syncManager
            .syncedPanel("powerPanel", true, (manager, handler) -> createModulePowerPanel(manager, parent));
        ButtonWidget<?> button = new ButtonWidget<>().marginLeft(4)
            .overlay(GTNCGuiTextures.OVERLAY_BUTTON_POWER_PANEL)
            .onMousePressed(mouse -> {
                if (powerPanel.isPanelOpen()) powerPanel.closePanel();
                else powerPanel.openPanel();
                return true;
            })
            .tooltipDynamic(tooltip -> {
                tooltip.addLine(IKey.lang("GT5U.gui.button.power_panel"));
                if (!unlocked.getBoolValue()) {
                    tooltip.addLine(EnumChatFormatting.RED + TextLocalization.GUI_STEAM_ELEVATOR_MODULE_VOLTAGE_LOCKED);
                }
            })
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY);
        return applyModernStateButton(button, powerPanel::isPanelOpen, () -> true);
    }

    private ModularPanel createModulePowerPanel(PanelSyncManager syncManager, ModularPanel parent) {
        syncManager.syncValue(VOLTAGE_UNLOCKED_SYNC_KEY, new BooleanSyncValue(module::isVoltageConfigUnlocked));
        syncManager.syncValue(
            PROCESSING_VOLTAGE_SYNC_KEY,
            new LongSyncValue(module::getConfiguredProcessingVoltage, module::setConfiguredProcessingVoltageFromGui)
                .allowC2S());
        syncManager.syncValue(CALCULATED_MAX_PARALLEL_SYNC_KEY, new IntSyncValue(module::getMaxParallelRecipes));
        syncManager.syncValue(
            SET_MAX_PARALLEL_SYNC_KEY,
            new IntSyncValue(module::getPowerPanelMaxParallel, module::setPowerPanelMaxParallelFromGui).allowC2S());
        syncManager.syncValue(
            ALWAYS_MAX_PARALLEL_SYNC_KEY,
            new BooleanSyncValue(module::isAlwaysMaxParallel, module::setAlwaysMaxParallelFromGui).allowC2S());

        BooleanSyncValue unlocked = syncManager.findSyncHandler(VOLTAGE_UNLOCKED_SYNC_KEY, BooleanSyncValue.class);
        LongSyncValue voltage = syncManager.findSyncHandler(PROCESSING_VOLTAGE_SYNC_KEY, LongSyncValue.class);
        IntSyncValue calculatedMax = syncManager.findSyncHandler(CALCULATED_MAX_PARALLEL_SYNC_KEY, IntSyncValue.class);
        IntSyncValue setMax = syncManager.findSyncHandler(SET_MAX_PARALLEL_SYNC_KEY, IntSyncValue.class);
        BooleanSyncValue alwaysMax = syncManager.findSyncHandler(ALWAYS_MAX_PARALLEL_SYNC_KEY, BooleanSyncValue.class);

        return new ModularPanel("powerPanel").relative(parent)
            .leftRel(1)
            .topRel(0)
            .size(138, 98)
            .background(GTNCGuiTextures.MODERN_VAULT_PANEL_BORDER)
            .disableHoverBackground()
            .child(
                Flow.column()
                    .full()
                    .padding(3)
                    .child(
                        IKey.lang("GT5U.gui.text.power_panel")
                            .style(EnumChatFormatting.UNDERLINE, EnumChatFormatting.BLACK)
                            .alignment(Alignment.CENTER)
                            .asWidget()
                            .marginTop(4)
                            .marginBottom(4))
                    .child(createMaxParallelGroup(calculatedMax, setMax, alwaysMax).marginBottom(4))
                    .child(createVoltageGroup(voltage, unlocked)));
    }

    private Flow createMaxParallelGroup(IntSyncValue calculatedMax, IntSyncValue setMax, BooleanSyncValue alwaysMax) {
        Flow column = Flow.column()
            .coverChildren();
        column.child(
            IKey.lang("GTPP.CC.parallel")
                .style(EnumChatFormatting.BLACK)
                .alignment(Alignment.CENTER)
                .asWidget()
                .height(14));
        column.child(
            Flow.row()
                .coverChildren()
                .childPadding(4)
                .child(
                    new TextFieldWidget().formatAsInteger(true)
                        .numbersInt(
                            () -> alwaysMax.getBoolValue() ? calculatedMax.getIntValue() : 1,
                            calculatedMax::getIntValue)
                        .value(new IntValue.Dynamic(setMax::getIntValue, setMax::setIntValue))
                        .scrollValues(1, 64, 4, 16)
                        .setTextAlignment(Alignment.CENTER)
                        .size(70, 18))
                .child(
                    new ButtonWidget<>().size(16)
                        .margin(1)
                        .overlay(
                            new DynamicDrawable(
                                () -> alwaysMax.getBoolValue() ? GTGuiTextures.OVERLAY_BUTTON_CHECKMARK
                                    : GTGuiTextures.OVERLAY_BUTTON_CROSS))
                        .onMousePressed(mouse -> {
                            alwaysMax.setBoolValue(!alwaysMax.getBoolValue());
                            setMax.setIntValue(calculatedMax.getIntValue());
                            return true;
                        })
                        .tooltip(tooltip -> tooltip.addLine(IKey.lang("GT5U.gui.button.max_parallel")))
                        .tooltipShowUpTimer(TOOLTIP_DELAY)));
        return column;
    }

    private Flow createVoltageGroup(LongSyncValue voltage, BooleanSyncValue unlocked) {
        return Flow.column()
            .coverChildren()
            .collapseDisabledChild()
            .child(
                IKey.str(TextLocalization.GUI_STEAM_ELEVATOR_MODULE_VOLTAGE_LABEL)
                    .style(EnumChatFormatting.BLACK)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .height(14))
            .child(
                new TextFieldWidget().formatAsInteger(true)
                    .setPattern(POSITIVE_INTEGER_PATTERN)
                    .setMaxLength(19)
                    .setValidator(SteamElevatorModuleGui::normalizeVoltageText)
                    .value(
                        new StringValue.Dynamic(
                            () -> Long.toString(voltage.getLongValue()),
                            value -> voltage.setLongValue(parseVoltageSaturated(value))))
                    .setTextAlignment(Alignment.CENTER)
                    .size(130, 18)
                    .setTooltipOverride(true)
                    .setEnabledIf(widget -> unlocked.getBoolValue()))
            .child(
                GTGuiTextures.OVERLAY_BUTTON_CROSS.asWidget()
                    .size(20)
                    .tooltip(tooltip -> tooltip.addLine(TextLocalization.GUI_STEAM_ELEVATOR_MODULE_VOLTAGE_LOCKED))
                    .tooltipShowUpTimer(TOOLTIP_DELAY)
                    .setEnabledIf(widget -> !unlocked.getBoolValue()));
    }

    private static String normalizeVoltageText(String value) {
        return Long.toString(parseVoltageSaturated(value));
    }

    private static long parseVoltageSaturated(String value) {
        if (value == null || value.isEmpty()) return 1L;
        try {
            BigInteger parsed = new BigInteger(value);
            if (parsed.signum() <= 0) return 1L;
            return parsed.compareTo(MAX_LONG_VALUE) >= 0 ? Long.MAX_VALUE : parsed.longValue();
        } catch (NumberFormatException ignored) {
            return 1L;
        }
    }
}
