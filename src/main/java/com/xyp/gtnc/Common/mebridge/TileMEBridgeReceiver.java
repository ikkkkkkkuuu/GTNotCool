package com.xyp.gtnc.Common.mebridge;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.xyp.gtnc.Loader.BlockLoader;
import com.xyp.gtnc.ScienceNotCool;

import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;

/**
 * 跨维度 ME 网桥 - 接收端。
 * <p>
 * 选定一个频道后,取该频道发起端的 {@code IGridNode},用 {@code createGridConnection} 把接收端节点与发起端节点连成
 * 一张网(方案 A:两端视为同一 AE 网络,样板/合成/存储全共享)。一个发起端可被任意多个接收端连入(1 对多)。
 * <p>
 * 连接采用懒重连 + 频率限制:仅在节点就绪 / 频道变更 / 对端上线时尝试,断开后不每 tick 重试,避免连接风暴触发
 * AE 全网重算导致卡顿。前提是两端方块所在区块常驻加载(认领区块),连接建立一次即稳定。
 */
public class TileMEBridgeReceiver extends TileMEBridgeBase implements IGuiHolder<PosGuiData> {

    /** 当前选定的频道名(可为中文);null / 空表示未选。 */
    private String channelName = "";

    /** 当前持有的跨网连接;null 表示未连接。 */
    private IGridConnection connection;

    /** 懒重连节流:每隔若干 tick 才尝试一次。 */
    private static final int RECONNECT_INTERVAL = 40;
    private int reconnectCooldown = 0;

    /** 统计某频道当前的接收端连接数(供发起端 GUI 显示"N 个接收端连入")。 */
    public static int countReceiversOnChannel(String name) {
        // 无全局接收端表;发起端 GUI 通过遍历不便,这里返回 -1 表示"未统计"。
        // 真正的计数在发起端侧用一个静态计数器维护(见 register/unregister 时机)。
        return MEBridgeReceiverRegistry.count(name);
    }

    public String getChannelName() {
        return channelName == null ? "" : channelName;
    }

    /** 设置频道(GUI 选择时调用)。会先断开旧连接,再在下次 tick 尝试连新频道。 */
    public void setChannelName(String name) {
        String newName = name == null ? "" : name;
        if (newName.equals(this.channelName)) return;
        disconnect();
        this.channelName = newName;
        this.reconnectCooldown = 0; // 立即尝试
        markDirty();
    }

    public boolean isConnected() {
        return connection != null;
    }

    @Override
    protected net.minecraft.item.ItemStack getVisualRepresentation() {
        return BlockLoader.blockMEBridgeReceiver != null
            ? new net.minecraft.item.ItemStack(BlockLoader.blockMEBridgeReceiver)
            : null;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj.isRemote) return;

        // 每 RECONNECT_INTERVAL tick 重新评估一次连接状态(仿量子网桥 updateStatus:
        // 校验现有连接是否仍有效 / 发起端离线则拆除 / 该连未连则建)。
        if (reconnectCooldown > 0) {
            reconnectCooldown--;
            return;
        }
        reconnectCooldown = RECONNECT_INTERVAL;
        updateConnectionStatus();
    }

    /**
     * 重新评估到发起端的连接。仿 QuantumCluster.updateStatus:
     * <ul>
     * <li>目标频道无效 / 发起端离线 / 节点未就绪 → 拆掉现有连接;</li>
     * <li>现有连接两端节点仍匹配当前发起端 → 保留;</li>
     * <li>否则重建。</li>
     * </ul>
     */
    private void updateConnectionStatus() {
        IGridNode myNode = getGridNode(ForgeDirection.UNKNOWN);

        // 解析目标发起端节点(频道有效 + 发起端在线 + 节点就绪)
        IGridNode senderNode = null;
        if (channelName != null && !channelName.isEmpty() && myNode != null) {
            MEBridgeChannelInfo info = MEBridgeChannelManager.get(channelName);
            if (info != null) {
                TileMEBridgeSender sender = info.getSenderTile();
                if (sender != null) {
                    IGridNode n = sender.getGridNode(ForgeDirection.UNKNOWN);
                    if (n != null && n != myNode) senderNode = n;
                }
            }
        }

        // 现有连接仍指向同一对节点 → 保留,不动
        if (connection != null) {
            IGridNode a = connection.a();
            IGridNode b = connection.b();
            boolean stillValid = senderNode != null
                && ((a == myNode || b == myNode) && (a == senderNode || b == senderNode));
            if (stillValid) return;
            // 目标变了 / 发起端离线 → 拆掉旧连接
            disconnect();
        }

        // 无可连目标(未选频道 / 发起端离线 / 节点未就绪)→ 保持断开
        if (senderNode == null) return;

        // 建立新连接。先让接收端节点认领发起端节点的 playerID——接收端是发起端网络的延伸,
        // 不该有独立身份。这样安保检查 checkPlayerPermissions 查的是主网所有者对自己主网的权限,必过,
        // 避免 "different security realms" 拒绝。
        try {
            myNode.setPlayerID(senderNode.getPlayerID());
            connection = appeng.api.AEApi.instance()
                .createGridConnection(myNode, senderNode);
            MEBridgeReceiverRegistry.add(channelName, this);
        } catch (appeng.api.exceptions.FailedConnection e) {
            // 通常是两端本地网各有不同 key 的安保终端(SecurityConnectionException),
            // 或已在同一网(ExistingConnectionException)。
            connection = null;
            ScienceNotCool.LOG.warn(
                "[MEBridge] receiver ({},{},{}) failed to connect channel '{}': {}",
                xCoord,
                yCoord,
                zCoord,
                channelName,
                e.getClass()
                    .getSimpleName() + " - "
                    + e.getMessage());
        }
    }

    /** 断开当前连接。 */
    private void disconnect() {
        if (connection != null) {
            try {
                connection.destroy();
            } catch (RuntimeException e) {
                ScienceNotCool.LOG.warn("[MEBridge] receiver connection destroy failed", e);
            }
            connection = null;
            MEBridgeReceiverRegistry.remove(channelName, this);
        }
    }

    @Override
    protected void onBridgeInvalidate() {
        disconnect();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.channelName = nbt.getString("mebridge_channel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (channelName != null) nbt.setString("mebridge_channel", channelName);
    }

    // region MUI2 GUI

    @Override
    public com.cleanroommc.modularui.screen.ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new com.cleanroommc.modularui.screen.ModularScreen(com.xyp.gtnc.ScienceNotCool.MODID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        // 当前选定频道名 + 连接状态,C2S 同步:客户端显示,服务端点击列表时写入。
        StringSyncValue channelSync = new StringSyncValue(this::getChannelName, this::setChannelName);
        channelSync.allowC2S();
        syncManager.syncValue("channel", channelSync);

        // 连接状态(只读,服务端 → 客户端)。connection 字段只在服务端有值,客户端靠此同步才能显示绿/灰。
        StringSyncValue connectedSync = new StringSyncValue(() -> isConnected() ? "1" : "0", v -> {});
        syncManager.syncValue("connected", connectedSync);

        // 频道列表面板(服务端构建,注册表在服务端有数据),点开时按当前注册表快照重建。
        IPanelHandler listPanel = syncManager.syncedPanel("channelList", true, this::createChannelListPanel);

        return ModularPanel.defaultPanel("mebridge_receiver", 180, 78)
            // #tr gui.mebridge.receiver.title
            // # ME Bridge Receiver
            // # zh_CN ME 网桥 - 接收端
            .child(
                IKey.lang("gui.mebridge.receiver.title")
                    .asWidget()
                    .pos(8, 6))
            // #tr gui.mebridge.receiver.channel
            // # Channel Name:
            // # zh_CN 频道名:
            .child(
                IKey.lang("gui.mebridge.receiver.channel")
                    .asWidget()
                    .pos(8, 22))
            // 直接写频道名(回车/关闭 GUI 时同步),或点右侧按钮从列表选。
            .child(
                new TextFieldWidget().value(channelSync)
                    .autoUpdateOnChange(false)
                    .setMaxLength(64)
                    .pos(8, 34)
                    .size(140, 14))
            // #tr gui.mebridge.receiver.select
            // # Browse channels
            // # zh_CN 浏览频道列表
            .child(
                new ButtonWidget<>().pos(152, 34)
                    .size(16, 14)
                    .overlay(IKey.str("..."))
                    .onMousePressed(btn -> {
                        if (listPanel.isPanelOpen()) listPanel.closePanel();
                        else listPanel.openPanel();
                        return true;
                    }))
            // #tr gui.mebridge.receiver.status
            // # Status: %s
            // # zh_CN 状态: %s
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "gui.mebridge.receiver.status",
                        "1".equals(connectedSync.getValue()) ? "§a●§r" : "§7○§r"))
                    .asWidget()
                    .pos(8, 58));
    }

    /** 频道列表弹出面板:列出全局所有频道,点击即选中并连接。服务端构建,快照当前注册表。 */
    public ModularPanel createChannelListPanel(PanelSyncManager syncManager, IPanelHandler panelHandler) {
        List<MEBridgeChannelInfo> channels = MEBridgeChannelManager.snapshot();

        Flow column = Flow.column()
            .coverChildrenHeight()
            .width(156);
        for (MEBridgeChannelInfo info : channels) {
            final String name = info.name;
            final boolean online = info.isOnline();
            String label = (online ? "§a●§r " : "§7○§r ") + name;
            column.child(
                new ButtonWidget<>().width(156)
                    .height(16)
                    .marginBottom(1)
                    .overlay(IKey.str(label))
                    .syncHandler(new InteractionSyncHandler().setOnMousePressed(md -> {
                        if (!md.isClient()) {
                            setChannelName(name);
                            markDirty();
                        }
                    }))
                    .tooltip(
                        t -> t.addLine(
                            IKey.str(
                                StatCollector.translateToLocal("gui.mebridge.channel.pos") + " "
                                    + info.x
                                    + ", "
                                    + info.y
                                    + ", "
                                    + info.z
                                    + " (dim "
                                    + info.dim
                                    + ")"))));
        }
        ListWidget<?, ?> list = new ListWidget<>().size(160, 100)
            .child(column);

        return new ModularPanel("mebridge:list").child(ButtonWidget.panelCloseButton())
            .child(
                Flow.column()
                    .child(
                        IKey.lang("gui.mebridge.receiver.select")
                            .asWidget()
                            .marginTop(10))
                    .child(list)
                    .childPadding(4)
                    .margin(8)
                    .coverChildren()
                    .crossAxisAlignment(Alignment.CrossAxis.START))
            .coverChildren();
    }

    // endregion
}
