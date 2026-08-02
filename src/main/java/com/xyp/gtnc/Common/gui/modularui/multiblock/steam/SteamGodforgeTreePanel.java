package com.xyp.gtnc.Common.gui.modularui.multiblock.steam;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade.*;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.xyp.gtnc.Common.machines.multiblock.steam.godforge.SteamGodforgeUpgradeCosts;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;
import gregtech.common.gui.modularui.multiblock.godforge.data.UpgradeColor;
import gregtech.common.gui.modularui.multiblock.godforge.sync.Panels;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncActions;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncHypervisor;
import gregtech.common.gui.modularui.multiblock.godforge.sync.SyncValues;
import gregtech.common.gui.modularui.widget.RotatedDrawable;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

/** The original 31-node layout with steam-localized nodes and controller-bus material payment. */
public final class SteamGodforgeTreePanel {

    private static final int SIZE = 300;
    private static final int BUTTON_W = 40;
    private static final int BUTTON_H = 15;

    /**
     * Sends the selected upgrade through an explicit synced action before the detail panel is opened/refreshed.
     * This avoids relying on the separate UPGRADE_CLICKED value packet in a dedicated client/server setup.
     */
    private static final String SELECT_UPGRADE_ACTION = "gtnc.steam_godforge.select_upgrade";

    private SteamGodforgeTreePanel() {}

    public static ModularPanel openPanel(SyncHypervisor hypervisor) {
        ModularPanel panel = hypervisor.getModularPanel(Panels.UPGRADE_TREE);
        SyncValues.UPGRADE_CLICKED.registerFor(Panels.UPGRADE_TREE, hypervisor);
        SyncValues.SECRET_UPGRADE.registerFor(Panels.UPGRADE_TREE, hypervisor);
        SyncActions.RESPEC_UPGRADE.registerFor(Panels.UPGRADE_TREE, hypervisor);
        SyncActions.COMPLETE_UPGRADE.registerFor(Panels.UPGRADE_TREE, hypervisor);
        SyncActions.REFRESH_DYNAMIC.registerFor(Panels.UPGRADE_TREE, hypervisor, hypervisor);

        PanelSyncManager treeSyncManager = hypervisor.getSyncManager(Panels.UPGRADE_TREE);
        EnumSyncValue<ForgeOfGodsUpgrade, ?> selected = SyncValues.UPGRADE_CLICKED
            .lookupFrom(Panels.UPGRADE_TREE, hypervisor);
        IPanelHandler detail = Panels.INDIVIDUAL_UPGRADE.getFrom(Panels.UPGRADE_TREE, hypervisor);

        /*
         * Do not depend on UPGRADE_CLICKED's own C2S packet here.
         * On a dedicated client/server setup, the panel-open and refresh packets can reach a server whose
         * selected enum is still the default START value. The server then builds START while the client builds
         * the actually clicked node, producing a DynamicSyncHandler widget-tree mismatch and a black panel.
         * This explicit action carries the selected ordinal and updates the server before the panel-open and
         * REFRESH_DYNAMIC packets are sent.
         */
        treeSyncManager.registerSyncedAction(SELECT_UPGRADE_ACTION, buffer -> {
            if (treeSyncManager.isClient()) {
                return;
            }

            int ordinal = buffer.readUnsignedByte();
            if (ordinal >= ForgeOfGodsUpgrade.VALUES.length) {
                return;
            }

            ForgeOfGodsUpgrade upgrade = ForgeOfGodsUpgrade.VALUES[ordinal];
            selected.setValue(upgrade, true, false);
        });

        panel.size(SIZE)
            .padding(4, 0, 4, 0)
            .background(GTGuiTextures.BACKGROUND_STAR)
            .disableHoverBackground()
            .child(ForgeOfGodsGuiUtil.panelCloseButton());
        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.setScrollSize(957);
        ScrollWidget<?> tree = new ScrollWidget<>(scrollData).size(292);

        tree.child(line(UpgradeColor.BLUE, START, IGCC, hypervisor))
            .child(line(UpgradeColor.BLUE, IGCC, STEM, hypervisor))
            .child(line(UpgradeColor.BLUE, IGCC, CFCE, hypervisor))
            .child(line(UpgradeColor.BLUE, STEM, GISS, hypervisor))
            .child(line(UpgradeColor.BLUE, STEM, FDIM, hypervisor))
            .child(line(UpgradeColor.BLUE, CFCE, FDIM, hypervisor))
            .child(line(UpgradeColor.BLUE, CFCE, SA, hypervisor))
            .child(line(UpgradeColor.BLUE, FDIM, GPCI, hypervisor))
            .child(line(UpgradeColor.BLUE, GPCI, GEM, hypervisor))
            .child(line(UpgradeColor.RED, GISS, REC, hypervisor))
            .child(line(UpgradeColor.RED, GPCI, REC, hypervisor))
            .child(line(UpgradeColor.RED, SA, CTCDD, hypervisor))
            .child(line(UpgradeColor.RED, GPCI, CTCDD, hypervisor))
            .child(line(UpgradeColor.BLUE, REC, QGPIU, hypervisor))
            .child(line(UpgradeColor.BLUE, CTCDD, QGPIU, hypervisor))
            .child(line(UpgradeColor.ORANGE, QGPIU, TCT, hypervisor))
            .child(line(UpgradeColor.ORANGE, TCT, EPEC, hypervisor))
            .child(line(UpgradeColor.ORANGE, EPEC, POS, hypervisor))
            .child(line(UpgradeColor.ORANGE, POS, NGMS, hypervisor))
            .child(line(UpgradeColor.PURPLE, QGPIU, SEFCP, hypervisor))
            .child(line(UpgradeColor.PURPLE, SEFCP, CNTI, hypervisor))
            .child(line(UpgradeColor.PURPLE, CNTI, NDPE, hypervisor))
            .child(line(UpgradeColor.PURPLE, NDPE, NGMS, hypervisor))
            .child(line(UpgradeColor.PURPLE, CNTI, DOP, hypervisor))
            .child(line(UpgradeColor.GREEN, QGPIU, GGEBE, hypervisor))
            .child(line(UpgradeColor.GREEN, GGEBE, IMKG, hypervisor))
            .child(line(UpgradeColor.GREEN, IMKG, DOR, hypervisor))
            .child(line(UpgradeColor.GREEN, DOR, NGMS, hypervisor))
            .child(line(UpgradeColor.GREEN, GGEBE, TPTP, hypervisor))
            .child(line(UpgradeColor.BLUE, NGMS, SEDS, hypervisor))
            .child(line(UpgradeColor.BLUE, SEDS, PA, hypervisor))
            .child(line(UpgradeColor.BLUE, PA, CD, hypervisor))
            .child(line(UpgradeColor.BLUE, CD, TSE, hypervisor))
            .child(line(UpgradeColor.BLUE, TSE, TBF, hypervisor))
            .child(line(UpgradeColor.BLUE, TBF, EE, hypervisor))
            .child(line(UpgradeColor.BLUE, EE, END, hypervisor));
        Arrays.stream(ForgeOfGodsUpgrade.VALUES)
            .map(upgrade -> button(upgrade, hypervisor, selected, detail, treeSyncManager))
            .forEach(tree::child);
        return panel.child(tree);
    }

    private static ButtonWidget<?> button(ForgeOfGodsUpgrade upgrade, SyncHypervisor hypervisor,
        EnumSyncValue<ForgeOfGodsUpgrade, ?> selected, IPanelHandler detail, PanelSyncManager treeSyncManager) {
        ButtonWidget<?> widget = new ButtonWidget<>();
        return widget.size(BUTTON_W, BUTTON_H)
            .pos(upgrade.getTreeX(), upgrade.getTreeY())
            .disableThemeBackground(true)
            .disableHoverThemeBackground(true)
            .overlay(
                new DynamicDrawable(
                    () -> hypervisor.getData()
                        .isUpgradeActive(upgrade) ? GTGuiTextures.BUTTON_SPACE_PRESSED_32x16
                            : GTGuiTextures.BUTTON_SPACE_32x16),
                IKey.str(upgrade.name())
                    .style(EnumChatFormatting.GOLD)
                    .scale(0.8f)
                    .alignment(Alignment.CENTER))
            .onMousePressed(mouse -> {
                if (mouse == 0 && Interactable.hasShiftDown()) {
                    SyncActions.COMPLETE_UPGRADE.callFrom(Panels.UPGRADE_TREE, hypervisor, upgrade);
                } else if (mouse == 0) {
                    /*
                     * Update the client-side selection immediately, but do not send UPGRADE_CLICKED as a
                     * separate packet. The custom action below is the authoritative C2S update.
                     */
                    selected.setValue(upgrade, true, false);

                    /*
                     * Send the authoritative server selection first. The following panel-open and refresh
                     * packets are then processed with the same selected upgrade on the server.
                     */
                    treeSyncManager
                        .callSyncedAction(SELECT_UPGRADE_ACTION, buffer -> buffer.writeByte(upgrade.ordinal()));

                    if (!detail.isPanelOpen()) {
                        detail.openPanel();
                    }

                    SyncActions.REFRESH_DYNAMIC.callFrom(Panels.UPGRADE_TREE, hypervisor, Panels.INDIVIDUAL_UPGRADE);
                } else if (mouse == 1) {
                    SyncActions.RESPEC_UPGRADE.callFrom(Panels.UPGRADE_TREE, hypervisor, upgrade);
                }
                return true;
            })
            .tooltipDynamic(t -> {
                t.addLine(TextLocalization.STEAM_GODFORGE_UPGRADE_NAMES[upgrade.ordinal()]);
                t.addLine(EnumChatFormatting.GRAY + TextLocalization.STEAM_GODFORGE_UPGRADE_BODIES[upgrade.ordinal()]);
                for (ItemStack cost : SteamGodforgeUpgradeCosts.get(upgrade)) t.addFromItem(cost);
            })
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }

    private static Widget<?> line(UpgradeColor color, ForgeOfGodsUpgrade from, ForgeOfGodsUpgrade to,
        SyncHypervisor hypervisor) {
        int fromX = from.getTreeX() + BUTTON_W / 2;
        int fromY = from.getTreeY() + BUTTON_H / 2;
        int toX = to.getTreeX() + BUTTON_W / 2;
        int toY = to.getTreeY() + BUTTON_H / 2;
        int width = 6;
        int height = (int) Math.sqrt(Math.pow(toX - fromX, 2) + Math.pow(toY - fromY, 2));
        float rotation = (float) (Math.atan2(toY - fromY, toX - fromX) - Math.PI / 2);
        int x = (fromX + toX) / 2 - width / 2;
        int y = (fromY + toY) / 2 - height / 2;
        return new DynamicDrawable(() -> {
            ForgeOfGodsData data = hypervisor.getData();
            UITexture texture = data.isUpgradeActive(from) && data.isUpgradeActive(to) ? color.getOpaqueConnector()
                : color.getConnector();
            return new RotatedDrawable(texture).rotationRadian(rotation);
        }).asWidget()
            .pos(x, y)
            .size(width, height);
    }
}
