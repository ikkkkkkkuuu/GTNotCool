package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamForgeOfGods;

import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.common.gui.modularui.multiblock.godforge.MTEForgeOfGodsGui;

// #tr gui.steam_godforge.upgrades
// # Steam Engineering Upgrades
// # zh_CN 蒸汽工程升级
// #tr gui.steam_godforge.stage
// # Stage %d | Parallel %d | Speed penalty x%s
// # zh_CN 阶段 %d | 并行 %d | 速度惩罚 x%s
// #tr gui.steam_godforge.milestone_required
// # Required milestone
// # zh_CN 所需里程碑
// #tr gui.steam_godforge.free
// # No material cost
// # zh_CN 无材料消耗
// #tr gui.steam_godforge.wireless_steam
// # Team wireless steam: %s L
// # zh_CN 团队无线蒸汽：%s L
// #tr gui.steam_godforge.cold_start
// # Cold start: %s / %s Critical Photons
// # zh_CN 冷启动：%s / %s 临界光子
// #tr gui.steam_godforge.compressed_steam
// # Upkeep: %s L molten Compressed Steam / 5s
// # zh_CN 维护消耗：%s L 液态压缩蒸汽 / 5秒

/** Adds steam-network information while retaining the upstream Godforge panel framework. */
public class SteamForgeOfGodsGui extends MTEForgeOfGodsGui {

    private final SteamForgeOfGods steamForge;
    private String syncedWirelessSteam = "0";

    public SteamForgeOfGodsGui(SteamForgeOfGods multiblock) {
        super(multiblock);
        this.steamForge = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        // The steam upkeep is displayed on the main panel as well as the fuel panel. Upstream only registers this
        // value when the fuel panel is opened, leaving the main-panel copy at zero after reconnecting to a world.
        syncManager.syncValue(
            "steamGodforgeFuelConsumption",
            new LongSyncValue(steamForge.getData()::getFuelConsumption, steamForge.getData()::setFuelConsumption));
        syncManager.syncValue(
            "steamGodforgeWirelessSteam",
            new StringSyncValue(steamForge::getWirelessSteamForGui, value -> syncedWirelessSteam = value));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(
                () -> StatCollector.translateToLocalFormatted("gui.steam_godforge.wireless_steam", syncedWirelessSteam))
                .color(Color.WHITE.main)
                .alignment(Alignment.CENTER)
                .asWidget()
                .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                .marginTop(4)
                .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "gui.steam_godforge.cold_start",
                        steamForge.getData()
                            .getStellarFuelAmount(),
                        steamForge.getData()
                            .getNeededStartupFuel()))
                    .color(Color.WHITE.main)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                    .marginTop(2)
                    .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "gui.steam_godforge.compressed_steam",
                        steamForge.getData()
                            .getFuelConsumption()))
                    .color(Color.WHITE.main)
                    .alignment(Alignment.CENTER)
                    .asWidget()
                    .widgetTheme(GTWidgetThemes.DISPLAY_TEXT_WHITE)
                    .marginTop(2)
                    .fullWidth());
    }

}
