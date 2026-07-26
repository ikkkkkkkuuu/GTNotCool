package com.xyp.gtnc.Common.mebridge;

import java.util.List;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DynamicLinkedSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

final class MEBridgeSenderGui {

    private static final String CHANNEL_SYNC_KEY = "mebridge_channel";
    private static final String RECEIVER_COUNT_SYNC_KEY = "mebridge_count";
    private static final String CONNECTION_SYNC_KEY = "mebridge_connection";
    private static final String TOPOLOGY_SYNC_KEY = "mebridge_topology";
    private static final String TOPOLOGY_WIDGET_SYNC_KEY = "mebridge_topology_widget";

    private MEBridgeSenderGui() {}

    static ModularPanel build(TileMEBridgeSender sender, PanelSyncManager syncManager) {
        StringSyncValue channelSync = MEBridgeGuiSync.editableChannel(sender::getChannelName, sender::setChannelName);
        syncManager.syncValue(CHANNEL_SYNC_KEY, channelSync);

        StringSyncValue receiverCountSync = MEBridgeGuiSync
            .readOnly(() -> String.valueOf(sender.getConnectedReceiverCount()));
        syncManager.syncValue(RECEIVER_COUNT_SYNC_KEY, receiverCountSync);
        StringSyncValue connectionSync = MEBridgeGuiSync
            .readOnly(() -> sender.getConnectedReceiverCount() > 0 ? "1" : "0");
        syncManager.syncValue(CONNECTION_SYNC_KEY, connectionSync);
        StringSyncValue topologySync = MEBridgeGuiSync.readOnly(sender::getReceiverTopologySnapshot);
        syncManager.syncValue(TOPOLOGY_SYNC_KEY, topologySync);
        DynamicLinkedSyncHandler<StringSyncValue> topologyWidgetSync = new DynamicLinkedSyncHandler<>(topologySync)
            .widgetProvider((manager, value) -> createTopology(value.getValue()));
        syncManager.syncValue(TOPOLOGY_WIDGET_SYNC_KEY, topologyWidgetSync);

        ParentWidget<?> content = new ParentWidget<>().size(412, 202);
        // #tr gui.mebridge.sender.title
        // # ME Bridge Sender
        // # zh_CN ME 网桥 - 发起端
        content.child(
            MEBridgeGuiTheme
                .text(IKey.lang("gui.mebridge.sender.title"), 8, 6, 210, MEBridgeGuiTheme.TEXT, Alignment.CenterLeft));
        // #tr gui.mebridge.sender.online
        // # Online
        // # zh_CN 在线
        // #tr gui.mebridge.sender.offline
        // # Offline
        // # zh_CN 离线
        content.child(
            MEBridgeGuiTheme.dynamic(
                () -> StatCollector.translateToLocal(
                    "1".equals(connectionSync.getValue()) ? "gui.mebridge.sender.online"
                        : "gui.mebridge.sender.offline"),
                306,
                6,
                98,
                "1".equals(connectionSync.getValue()) ? MEBridgeGuiTheme.SUCCESS : MEBridgeGuiTheme.MUTED,
                Alignment.CenterRight));

        ParentWidget<?> channelSection = MEBridgeGuiTheme.section(0, 24, 212, 158);
        // #tr gui.mebridge.sender.channel
        // # Channel Name
        // # zh_CN 频道名称
        channelSection.child(
            MEBridgeGuiTheme.text(
                IKey.lang("gui.mebridge.sender.channel"),
                8,
                8,
                196,
                MEBridgeGuiTheme.ACCENT,
                Alignment.CenterLeft));
        channelSection.child(
            new TextFieldWidget().value(channelSync)
                .autoUpdateOnChange(false)
                .setMaxLength(MEBridgeChannelName.MAX_LENGTH)
                .pos(8, 24)
                .size(196, 16));
        // #tr gui.mebridge.sender.hint
        // # Press Enter or close this screen to save changes.
        // # zh_CN 按回车或关闭界面保存修改。
        channelSection.child(
            MEBridgeGuiTheme
                .text(IKey.lang("gui.mebridge.sender.hint"), 8, 46, 196, MEBridgeGuiTheme.MUTED, Alignment.CenterLeft));
        // #tr gui.mebridge.sender.location
        // # Location
        // # zh_CN 发起端位置
        channelSection.child(
            MEBridgeGuiTheme.dynamic(
                () -> StatCollector.translateToLocal("gui.mebridge.sender.location") + ": "
                    + MEBridgeGuiTheme.dimensionLabel(sender.getDimensionId())
                    + "  "
                    + sender.getCoordinates(),
                8,
                78,
                196,
                MEBridgeGuiTheme.TEXT,
                Alignment.CenterLeft));
        content.child(channelSection);

        ParentWidget<?> monitor = MEBridgeGuiTheme.section(218, 24, 194, 158);
        // #tr gui.mebridge.sender.monitor
        // # Receiver Monitor
        // # zh_CN 接收端监控
        monitor.child(
            MEBridgeGuiTheme
                .text(IKey.lang("gui.mebridge.sender.monitor"), 8, 8, 178, MEBridgeGuiTheme.TEXT, Alignment.Center));
        // #tr gui.mebridge.sender.receivers
        // # Active Receivers
        // # zh_CN 活跃接收端
        monitor.child(
            MEBridgeGuiTheme.dynamic(
                () -> StatCollector.translateToLocal("gui.mebridge.sender.receivers") + ": "
                    + receiverCountSync.getValue(),
                8,
                28,
                178,
                MEBridgeGuiTheme.SUCCESS,
                Alignment.Center));
        monitor.child(
            new DynamicSyncedWidget<>().syncHandler(topologyWidgetSync)
                .initialChild(createTopology(""))
                .pos(8, 52)
                .size(178, 96));
        content.child(monitor);

        return ModularPanel.defaultPanel("mebridge_sender", 420, 210)
            .background(MEBridgeGuiTheme.background())
            .child(content.pos(4, 4));
    }

    private static IWidget createTopology(String encoded) {
        List<MEBridgeReceiverTopologyCodec.Entry> entries = MEBridgeReceiverTopologyCodec.decode(encoded);
        if (entries.isEmpty()) return emptyTopology();

        Flow column = Flow.column()
            .coverChildrenHeight()
            .width(178);
        for (MEBridgeReceiverTopologyCodec.Entry entry : entries) {
            ParentWidget<?> row = new ParentWidget<>().width(178)
                .height(16)
                .background(new com.cleanroommc.modularui.drawable.Rectangle().color(MEBridgeGuiTheme.PANEL_ROW));
            row.child(
                MEBridgeGuiTheme.text(
                    IKey.str(MEBridgeGuiTheme.dimensionLabel(entry.dimension)),
                    6,
                    2,
                    124,
                    MEBridgeGuiTheme.ACCENT,
                    Alignment.CenterLeft));
            row.child(
                MEBridgeGuiTheme.text(
                    IKey.str(String.valueOf(entry.receiverCount)),
                    132,
                    2,
                    40,
                    MEBridgeGuiTheme.SUCCESS,
                    Alignment.CenterRight));
            column.child(row);
        }
        return new ListWidget<>().size(178, 96)
            .child(column);
    }

    private static IWidget emptyTopology() {
        ParentWidget<?> empty = new ParentWidget<>().size(178, 96);
        // #tr gui.mebridge.sender.no_receivers
        // # No active receivers
        // # zh_CN 当前没有活跃接收端
        empty.child(
            MEBridgeGuiTheme.text(
                IKey.lang("gui.mebridge.sender.no_receivers"),
                4,
                40,
                170,
                MEBridgeGuiTheme.MUTED,
                Alignment.Center));
        return empty;
    }
}
