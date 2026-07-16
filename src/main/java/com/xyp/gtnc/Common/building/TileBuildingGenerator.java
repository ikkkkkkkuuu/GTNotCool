package com.xyp.gtnc.Common.building;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 建筑生成器的 TileEntity + MUI2 GUI 宿主。
 * <p>
 * 持有：放入的多方块机器物品、旋转（0..3 = 0/90/180/270 绕 Y 轴）、XYZ 偏移（相对生成器）。玩家在 GUI 里调这些参数、
 * 看 3D 预览，点保存才真正生成放大建筑（走已有 {@link PixelBuildingManager} 放置 + 撤销 + 持久化管线）。
 * <p>
 * <b>G1 阶段</b>：先把方块变成 TE + 可开的空 GUI，验证 NBT 存取与开关面板。控件/预览/保存在后续里程碑加。
 */
public class TileBuildingGenerator extends TileEntity implements IGuiHolder<PosGuiData> {

    /** 放入的多方块机器物品（预览/生成的源）——1 格库存，可插取。 */
    private final ItemStackHandler machineInv = new ItemStackHandler(1);
    /** 旋转档位 0..3（0/90/180/270°，绕 Y 轴）。 */
    private int rotation = 0;
    /** 相对生成器的偏移。 */
    private int offsetX = 0, offsetY = 1, offsetZ = 0;
    /** 是否已生成建筑（已生成则不画预览线框）。服务端放置/撤销时更新并同步客户端。 */
    private boolean generated = false;

    // region accessors

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean g) {
        if (this.generated == g) return;
        this.generated = g;
        markDirty();
        syncToClient();
    }

    public ItemStackHandler getMachineInv() {
        return machineInv;
    }

    public ItemStack getMachineStack() {
        return machineInv.getStackInSlot(0);
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int r) {
        this.rotation = ((r % 4) + 4) % 4;
        markDirty();
        syncToClient();
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    public void setOffset(int x, int y, int z) {
        this.offsetX = clampOffset(x);
        this.offsetY = clampOffset(y);
        this.offsetZ = clampOffset(z);
        markDirty();
        syncToClient();
    }

    private static int clampOffset(int v) {
        return v < -256 ? -256 : (v > 256 ? 256 : v);
    }

    // endregion

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("rotation", rotation);
        nbt.setInteger("offX", offsetX);
        nbt.setInteger("offY", offsetY);
        nbt.setInteger("offZ", offsetZ);
        nbt.setBoolean("generated", generated);
        ItemStack machine = machineInv.getStackInSlot(0);
        if (machine != null) {
            NBTTagCompound item = new NBTTagCompound();
            machine.writeToNBT(item);
            nbt.setTag("machine", item);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        rotation = ((nbt.getInteger("rotation") % 4) + 4) % 4;
        offsetX = clampOffset(nbt.getInteger("offX"));
        offsetY = nbt.hasKey("offY") ? clampOffset(nbt.getInteger("offY")) : 1;
        offsetZ = clampOffset(nbt.getInteger("offZ"));
        generated = nbt.getBoolean("generated");
        machineInv.setStackInSlot(
            0,
            nbt.hasKey("machine") ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("machine")) : null);
    }

    // region 客户端同步（让世界内线框预览常驻可读：机器/旋转/偏移随 tile 同步到客户端）

    @Override
    public net.minecraft.network.Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new net.minecraft.network.play.server.S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net,
        net.minecraft.network.play.server.S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    /** 服务端改了参数后，标记方块更新以重发描述包（同步到客户端供线框渲染）。 */
    private void syncToClient() {
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    // 客户端加载的生成器 tile 集合（供 BuildingBoundsRenderer 遍历画线框）。
    @Override
    public void validate() {
        super.validate();
        if (worldObj != null && worldObj.isRemote) {
            com.xyp.gtnc.Common.building.client.BuildingBoundsRenderer.addClientTile(this);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (worldObj != null && worldObj.isRemote) {
            com.xyp.gtnc.Common.building.client.BuildingBoundsRenderer.removeClientTile(this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (worldObj != null && worldObj.isRemote) {
            com.xyp.gtnc.Common.building.client.BuildingBoundsRenderer.removeClientTile(this);
        }
    }

    // endregion

    // region MUI2 GUI

    // createScreen 返回纯客户端类 ModularScreen，重写必须带 @SideOnly(CLIENT)（照 MEBridge 做法，避免服务端解析崩）。
    @Override
    @SideOnly(Side.CLIENT)
    public com.cleanroommc.modularui.screen.ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new com.cleanroommc.modularui.screen.ModularScreen(com.xyp.gtnc.ScienceNotCool.MODID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        // 机器物品槽（可插取），同步键让两端一致。
        syncManager.registerSlotGroup("machine_inv", 1);

        // 旋转 / 偏移：IntSyncValue 双向同步（客户端调 → 服务端存）。
        IntSyncValue rotSync = new IntSyncValue(this::getRotation, this::setRotation);
        rotSync.allowC2S();
        syncManager.syncValue("rotation", rotSync);
        IntSyncValue offXSync = new IntSyncValue(this::getOffsetX, v -> setOffset(v, offsetY, offsetZ));
        offXSync.allowC2S();
        syncManager.syncValue("offX", offXSync);
        IntSyncValue offYSync = new IntSyncValue(this::getOffsetY, v -> setOffset(offsetX, v, offsetZ));
        offYSync.allowC2S();
        syncManager.syncValue("offY", offYSync);
        IntSyncValue offZSync = new IntSyncValue(this::getOffsetZ, v -> setOffset(offsetX, offsetY, v));
        offZSync.allowC2S();
        syncManager.syncValue("offZ", offZSync);

        ModularPanel panel = ModularPanel.defaultPanel("building_generator", 280, 240);

        // 标题
        // #tr gui.gtnc.building_generator.title
        // # Building Generator
        // # zh_CN 建筑生成器
        panel.child(
            IKey.lang("gui.gtnc.building_generator.title")
                .asWidget()
                .pos(8, 6));

        // 左侧：3D 预览窗（仅客户端创建，服务端跳过——避免加载 @SideOnly(CLIENT) 类）。
        if (data.isClient()) {
            panel.child(
                createPreviewWidget().pos(10, 20)
                    .size(110, 110));
        }

        // 机器物品槽（预览窗下方）。物品变化时同步到客户端，让世界线框跟着更新。
        panel.child(
            new ItemSlot().slot(
                new ModularSlot(machineInv, 0).slotGroup("machine_inv")
                    .changeListener((stack, onlyAmount, client, init) -> { if (!client) syncToClient(); }))
                .pos(10, 134));

        // 右侧控件区（x 从 132 起）
        int rx = 132;
        // 旋转：显示 + 循环按钮
        // #tr gui.gtnc.building_generator.rotation
        // # Rotation: %s°
        // # zh_CN 旋转: %s°
        panel.child(
            IKey.dynamic(
                () -> net.minecraft.util.StatCollector
                    .translateToLocalFormatted("gui.gtnc.building_generator.rotation", rotSync.getValue() * 90))
                .asWidget()
                .pos(rx, 24)
                .size(110, 12));
        panel.child(
            new ButtonWidget<>().overlay(IKey.str("↻"))
                .size(16, 16)
                .pos(rx + 110, 20)
                .onMousePressed(btn -> {
                    rotSync.setValue((rotSync.getValue() + 1) & 3, true, true);
                    return true;
                }));

        // XYZ 偏移行：各 [-] 值 [+]
        panel.child(offsetRow(syncManager, "X", offXSync, rx, 42));
        panel.child(offsetRow(syncManager, "Y", offYSync, rx, 62));
        panel.child(offsetRow(syncManager, "Z", offZSync, rx, 82));

        // 保存 / 撤销按钮
        // #tr gui.gtnc.building_generator.save
        // # Generate
        // # zh_CN 保存生成
        panel.child(
            new ButtonWidget<>().overlay(IKey.lang("gui.gtnc.building_generator.save"))
                .size(126, 18)
                .pos(rx, 108)
                .onMousePressed(btn -> {
                    onSavePressed();
                    return true;
                }));
        // #tr gui.gtnc.building_generator.undo
        // # Undo
        // # zh_CN 撤销
        panel.child(
            new ButtonWidget<>().overlay(IKey.lang("gui.gtnc.building_generator.undo"))
                .size(126, 18)
                .pos(rx, 130)
                .onMousePressed(btn -> {
                    com.xyp.gtnc.Common.building.client.BuildingGeneratorClient.requestUndo(xCoord, yCoord, zCoord);
                    return true;
                }));

        // 玩家背包（否则物品无法拖入机器槽）。
        panel.child(
            com.cleanroommc.modularui.widgets.SlotGroupWidget.playerInventory(false)
                .align(com.cleanroommc.modularui.utils.Alignment.BottomCenter)
                .marginBottom(6));

        return panel;
    }

    /** 客户端专用：创建 3D 预览 widget（隔离在独立方法，服务端不调用故不加载该类）。 */
    @SideOnly(Side.CLIENT)
    private com.cleanroommc.modularui.widget.Widget<?> createPreviewWidget() {
        return new com.xyp.gtnc.Common.building.client.StructurePreviewWidget(this::getMachineStack);
    }

    /** 一行偏移控件：标签 [-] 值 [+]。 */
    private com.cleanroommc.modularui.widget.Widget<?> offsetRow(PanelSyncManager sm, String axis, IntSyncValue sync,
        int x, int y) {
        com.cleanroommc.modularui.widgets.layout.Row row = new com.cleanroommc.modularui.widgets.layout.Row();
        row.pos(x, y)
            .size(126, 16)
            .child(
                IKey.str(axis)
                    .asWidget()
                    .size(12, 16))
            .child(
                new ButtonWidget<>().overlay(IKey.str("-"))
                    .size(16, 16)
                    .onMousePressed(b -> {
                        sync.setValue(sync.getValue() - 1, true, true);
                        return true;
                    }))
            .child(
                IKey.dynamic(() -> String.valueOf(sync.getValue()))
                    .asWidget()
                    .size(40, 16))
            .child(
                new ButtonWidget<>().overlay(IKey.str("+"))
                    .size(16, 16)
                    .onMousePressed(b -> {
                        sync.setValue(sync.getValue() + 1, true, true);
                        return true;
                    }));
        return row;
    }

    /** 保存按钮（客户端）：读当前机器物品 + 旋转 + 偏移，触发放大生成（走已有客户端提取 + 网络管线）。 */
    @SideOnly(Side.CLIENT)
    private void onSavePressed() {
        ItemStack machine = getMachineStack();
        if (machine == null) return;
        com.xyp.gtnc.Common.building.client.BuildingGeneratorClient
            .requestGenerate(worldObj, xCoord, yCoord, zCoord, machine, rotation, offsetX, offsetY, offsetZ);
    }

    // endregion
}
