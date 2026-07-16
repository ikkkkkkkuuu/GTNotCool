package com.xyp.gtnc.Common.building.client;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Area;

import blockrenderer6343.client.renderer.ImmediateWorldSceneRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * GUI 内的多方块结构 3D 预览窗（MUI2 自定义 widget）。
 * <p>
 * 复用 BlockRenderer6343 的 {@link ImmediateWorldSceneRenderer}：{@link StructureExtractor#buildRenderer} 把当前机器
 * 建进离屏伪世界，本 widget 在 {@link #draw} 里调 {@code renderer.render(屏幕矩形, 鼠标)} 画出来。轨道相机：
 * 左键拖拽转视角（yaw/pitch）、滚轮缩放（zoom）。机器物品变化时（由 supplier 提供）才重建 renderer（缓存，避免每帧重建）。
 * <p>
 * <b>仅客户端</b>。
 */
@SideOnly(Side.CLIENT)
public class StructurePreviewWidget extends Widget<StructurePreviewWidget> implements Interactable {

    private final Supplier<ItemStack> machineSupplier;

    private ImmediateWorldSceneRenderer renderer;
    private ItemStack lastMachine; // 上次用于建 renderer 的机器（判断是否需重建）
    private final Vector3f center = new Vector3f();

    private float rotationYaw = 20f;
    private float rotationPitch = 50f;
    private float zoom = 16f;
    private boolean cameraInited = false;

    private int lastDragX, lastDragY;
    private boolean dragging = false;

    public StructurePreviewWidget(Supplier<ItemStack> machineSupplier) {
        this.machineSupplier = machineSupplier;
        background(GuiTextures.DISPLAY);
    }

    /** 机器物品变化时重建离屏结构 renderer。 */
    private void ensureRenderer() {
        ItemStack machine = machineSupplier == null ? null : machineSupplier.get();
        if (!sameItem(machine, lastMachine)) {
            lastMachine = machine == null ? null : machine.copy();
            renderer = machine == null ? null : StructureExtractor.buildRenderer(machine);
            cameraInited = false;
        }
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        if (a == null || b == null) return a == b;
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
    }

    private void initCameraIfNeeded() {
        if (cameraInited || renderer == null) return;
        Vector3f size = renderer.world.getSize();
        Vector3f minPos = renderer.world.getMinPos();
        center.set(minPos.x + size.x / 2f, minPos.y + size.y / 2f, minPos.z + size.z / 2f);
        float max = Math.max(Math.max(size.x, size.y), size.z);
        zoom = Math.max(6f, max * 1.6f);
        rotationYaw = 20f;
        rotationPitch = 50f;
        cameraInited = true;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        ensureRenderer();
        if (renderer == null) return;
        initCameraIfNeeded();

        Area area = getArea();
        renderer.setCameraLookAt(center, zoom, Math.toRadians(rotationPitch), Math.toRadians(rotationYaw));
        try {
            // render 用屏幕绝对坐标。area 的 x/y 是屏幕像素左上角。
            renderer.render(area.x, area.y, area.width, area.height, context.getAbsMouseX(), context.getAbsMouseY());
        } catch (Throwable t) {
            // 3D 渲染失败不应弄崩整个 GUI。
        }
    }

    @NotNull
    @Override
    public Result onMousePressed(int mouseButton) {
        dragging = true;
        lastDragX = getContext().getAbsMouseX();
        lastDragY = getContext().getAbsMouseY();
        return Result.SUCCESS;
    }

    @Override
    public boolean onMouseRelease(int mouseButton) {
        dragging = false;
        return true;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        if (!dragging) return;
        int mx = getContext().getAbsMouseX();
        int my = getContext().getAbsMouseY();
        rotationYaw += (mx - lastDragX);
        rotationPitch = clamp(rotationPitch + (my - lastDragY), -89.9f, 89.9f);
        lastDragX = mx;
        lastDragY = my;
    }

    @Override
    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
        // 向上滚拉近（减小 zoom 半径），向下推远。modifier: UP=+1, DOWN=-1。
        zoom = clamp(zoom - scrollDirection.modifier * 2f, 3f, 999f);
        return true;
    }

    private static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
}
