package com.xyp.gtnc.Common.mebridge;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.xyp.gtnc.Loader.BlockLoader;

/**
 * 发起端网桥方块。
 * <p>
 * 玩家在 GUI 里写一个频道名(可中文),该方块把自己注册到 {@link MEBridgeChannelManager}。
 * 接收端据频道名匹配到它,取其 {@code IGridNode} 建立跨维度连接,两端合并为同一 AE 网络。
 */
public class TileMEBridgeSender extends TileMEBridgeBase implements IGuiHolder<PosGuiData> {

    /** 玩家写入的频道名(空 = 未广播)。 */
    private String channelName = "";

    @Override
    protected ItemStack getVisualRepresentation() {
        return new ItemStack(BlockLoader.blockMEBridgeSender);
    }

    public String getChannelName() {
        return channelName;
    }

    /** 设置频道名并刷新注册表(仅服务端)。返回 false 表示频道名被别的发起端占用。 */
    public boolean setChannelName(String name) {
        if (worldObj == null || worldObj.isRemote) return false;
        String trimmed = name == null ? "" : name.trim();

        // 先注销旧频道
        if (!channelName.isEmpty()) {
            MEBridgeChannelManager.unregister(channelName, xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
        }

        this.channelName = trimmed;
        markDirty();

        if (trimmed.isEmpty()) return true;
        return registerSelf();
    }

    private boolean registerSelf() {
        if (channelName.isEmpty() || worldObj == null || worldObj.isRemote) return false;
        MEBridgeChannelInfo info = new MEBridgeChannelInfo(
            channelName,
            xCoord,
            yCoord,
            zCoord,
            worldObj.provider.dimensionId,
            null);
        return MEBridgeChannelManager.register(info, this);
    }

    @Override
    protected void onProxyReady() {
        // 区块重新加载后重新登记,刷新弱引用
        if (!channelName.isEmpty()) {
            registerSelf();
        }
    }

    @Override
    protected void onBridgeInvalidate() {
        if (worldObj != null && !worldObj.isRemote && !channelName.isEmpty()) {
            MEBridgeChannelManager.unregister(channelName, xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
        }
    }

    /** 当前连入本频道的接收端数量(遍历注册表统计,GUI 显示用)。 */
    public int getConnectedReceiverCount() {
        return TileMEBridgeReceiver.countReceiversOnChannel(channelName);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        channelName = nbt.getString("mebridge_channel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("mebridge_channel", channelName == null ? "" : channelName);
    }

    // region MUI2 GUI

    @Override
    @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public com.cleanroommc.modularui.screen.ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new com.cleanroommc.modularui.screen.ModularScreen(com.xyp.gtnc.ScienceNotCool.MODID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        // 频道名 C2S 同步：客户端输入 → 服务端 setChannelName（注册/注销频道）
        StringSyncValue channelSync = new StringSyncValue(this::getChannelName, this::setChannelName);
        channelSync.allowC2S();
        syncManager.syncValue("mebridge_channel", channelSync);

        // 接收端计数（只读，服务端 → 客户端）
        StringSyncValue countSync = new StringSyncValue(() -> String.valueOf(getConnectedReceiverCount()), v -> {});
        syncManager.syncValue("mebridge_count", countSync);

        return ModularPanel.defaultPanel("mebridge_sender", 176, 90)
            // #tr gui.mebridge.sender.title
            // # ME Bridge Sender
            // # zh_CN ME 网桥 - 发起端
            .child(
                IKey.lang("gui.mebridge.sender.title")
                    .asWidget()
                    .pos(8, 6))
            // #tr gui.mebridge.sender.channel
            // # Channel Name:
            // # zh_CN 频道名:
            .child(
                IKey.lang("gui.mebridge.sender.channel")
                    .asWidget()
                    .pos(8, 24))
            .child(
                new TextFieldWidget().value(channelSync)
                    .autoUpdateOnChange(false)
                    .setMaxLength(64)
                    .pos(8, 36)
                    .size(160, 14))
            // #tr gui.mebridge.sender.receivers
            // # Connected receivers: %s
            // # zh_CN 已连入接收端: %s
            .child(
                IKey.dynamic(
                    () -> net.minecraft.util.StatCollector
                        .translateToLocalFormatted("gui.mebridge.sender.receivers", countSync.getValue()))
                    .asWidget()
                    .pos(8, 60));
    }

    // endregion
}
