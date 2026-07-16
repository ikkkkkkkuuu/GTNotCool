package com.xyp.gtnc.Common.building.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.xyp.gtnc.Common.building.TileBuildingGenerator;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 当建筑生成器 GUI 打开时，在世界里实时画一个半透明线框包围盒，显示放大建筑将占据的范围（随 XYZ 偏移 / 旋转 /
 * 机器实时移动、变形）。关闭 GUI 即消失。仿 {@code BlockPosHighlighter} 用 {@link RenderWorldLastEvent} +
 * {@link Tessellator} 画线。
 * <p>
 * 结构尺寸由 {@link StructureExtractor} 提取（缓存，机器物品变才重算）。放大后包围盒 = 结构尺寸 × {@link BuildingGeneratorClient#SCALE}，
 * 旋转 90/270° 时 X/Z 尺寸互换。原点 = 生成器 + 偏移。
 * <p>
 * <b>仅客户端</b>。
 */
@SideOnly(Side.CLIENT)
public class BuildingBoundsRenderer {

    /** 客户端已加载的生成器 tile（validate/invalidate/onChunkUnload 维护）。用弱引用避免泄漏。 */
    private static final java.util.Set<TileBuildingGenerator> CLIENT_TILES = java.util.Collections
        .newSetFromMap(new java.util.WeakHashMap<>());

    /** 每台机器物品 → 结构尺寸缓存（避免每帧重新提取结构）。key 用 item+meta。 */
    private static final java.util.Map<String, int[]> SIZE_CACHE = new java.util.HashMap<>();

    public static void addClientTile(TileBuildingGenerator tile) {
        CLIENT_TILES.add(tile);
    }

    public static void removeClientTile(TileBuildingGenerator tile) {
        CLIENT_TILES.remove(tile);
    }

    private static String machineKey(ItemStack s) {
        return s.getItem() == null ? ""
            : (net.minecraft.item.Item.getIdFromItem(s.getItem()) + ":" + s.getItemDamage());
    }

    /** 取机器结构信息 {sizeX,sizeY,sizeZ, ctrlLx,ctrlLy,ctrlLz}，缓存；失败返回 null。 */
    private static int[] getInfo(ItemStack machine) {
        String key = machineKey(machine);
        int[] cached = SIZE_CACHE.get(key);
        if (cached != null) return cached[0] < 0 ? null : cached;
        StructureExtractor.Result res = StructureExtractor.extract(machine);
        if (res == null) {
            SIZE_CACHE.put(key, new int[] { -1 }); // 记失败，避免每帧重试
            return null;
        }
        int[] info = { res.sizeX, res.sizeY, res.sizeZ, res.ctrlLx, res.ctrlLy, res.ctrlLz };
        SIZE_CACHE.put(key, info);
        return info;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (CLIENT_TILES.isEmpty()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) return;

        EntityPlayerSP p = mc.thePlayer;
        double camX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks;
        double camY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks;
        double camZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glLineWidth(2.5f);
        GL11.glTranslated(-camX, -camY, -camZ);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (TileBuildingGenerator tile : CLIENT_TILES) {
            if (tile == null || tile.isInvalid()) continue;
            if (tile.isGenerated()) continue; // 已生成的不画线框
            ItemStack machine = tile.getMachineStack();
            if (machine == null || machine.getItem() == null) continue;
            int[] info = getInfo(machine);
            if (info == null) continue;

            int S = BuildingGeneratorClient.SCALE;
            int rot = ((tile.getRotation() % 4) + 4) % 4;
            int sizeX = info[0], sizeY = info[1], sizeZ = info[2];
            int ctrlLx = info[3], ctrlLy = info[4], ctrlLz = info[5];

            int spanX = ((rot & 1) == 0 ? sizeX : sizeZ) * S;
            int spanY = sizeY * S;
            int spanZ = ((rot & 1) == 0 ? sizeZ : sizeX) * S;

            double bx = tile.xCoord + tile.getOffsetX();
            double by = tile.yCoord + tile.getOffsetY();
            double bz = tile.zCoord + tile.getOffsetZ();

            // 整体包围盒（青色）。
            drawBox(bx, by, bz, bx + spanX, by + spanY, bz + spanZ, 0.2f, 0.8f, 1.0f, 0.9f);

            // 控制器正面标记（黄色方框）。伪世界里控制器 facing=SOUTH(+Z)，旋转 rot 后正面方向随之转。
            // 控制器格局部 (ctrlLx,ctrlLz) 旋转后 → (rcx,rcz)；正面法向 (0,+1) 旋转后 → (nx,nz)。
            int w = sizeX, d = sizeZ;
            int rcx, rcz;
            int nx, nz; // 正面朝向单位向量（格）
            switch (rot) {
                case 1: // 90°
                    rcx = d - 1 - ctrlLz;
                    rcz = ctrlLx;
                    nx = -1;
                    nz = 0;
                    break;
                case 2: // 180°
                    rcx = w - 1 - ctrlLx;
                    rcz = d - 1 - ctrlLz;
                    nx = 0;
                    nz = -1;
                    break;
                case 3: // 270°
                    rcx = ctrlLz;
                    rcz = w - 1 - ctrlLx;
                    nx = 1;
                    nz = 0;
                    break;
                default: // 0°：正面 +Z
                    rcx = ctrlLx;
                    rcz = ctrlLz;
                    nx = 0;
                    nz = 1;
                    break;
            }
            // 控制器那一格放大区域的角。
            double cx0 = bx + rcx * S, cy0 = by + ctrlLy * S, cz0 = bz + rcz * S;
            drawControllerFront(cx0, cy0, cz0, S, nx, nz);
        }

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /** 在控制器那一格放大区域的「正面」画一个黄色方框 + 对角线，标出朝向。nx/nz 是正面法向（±1 之一）。 */
    private static void drawControllerFront(double cx0, double cy0, double cz0, int S, int nx, int nz) {
        // 正面所在平面的四角。正面法向为 +Z：z = cz0+S；-Z：z=cz0；+X：x=cx0+S；-X：x=cx0。
        double x0 = cx0, x1 = cx0 + S, y0 = cy0, y1 = cy0 + S, z0 = cz0, z1 = cz0 + S;
        double ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz;
        if (nz != 0) {
            double z = nz > 0 ? z1 : z0;
            ax = x0;
            ay = y0;
            az = z;
            bx = x1;
            by = y0;
            bz = z;
            cx = x1;
            cy = y1;
            cz = z;
            dx = x0;
            dy = y1;
            dz = z;
        } else {
            double x = nx > 0 ? x1 : x0;
            ax = x;
            ay = y0;
            az = z0;
            bx = x;
            by = y0;
            bz = z1;
            cx = x;
            cy = y1;
            cz = z1;
            dx = x;
            dy = y1;
            dz = z0;
        }
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_LINES);
        t.setColorRGBA_F(1.0f, 0.9f, 0.1f, 1.0f); // 黄色
        // 方框 4 边
        edge(t, ax, ay, az, bx, by, bz);
        edge(t, bx, by, bz, cx, cy, cz);
        edge(t, cx, cy, cz, dx, dy, dz);
        edge(t, dx, dy, dz, ax, ay, az);
        // 对角线（X 叉，突出「这是正面」）
        edge(t, ax, ay, az, cx, cy, cz);
        edge(t, bx, by, bz, dx, dy, dz);
        t.draw();
    }

    /** 画一个线框盒子（12 条棱）。 */
    private static void drawBox(double x0, double y0, double z0, double x1, double y1, double z1, float r, float g,
        float b, float a) {
        Tessellator t = Tessellator.instance;
        t.startDrawing(GL11.GL_LINES);
        t.setColorRGBA_F(r, g, b, a);
        // 底面 4 棱
        edge(t, x0, y0, z0, x1, y0, z0);
        edge(t, x1, y0, z0, x1, y0, z1);
        edge(t, x1, y0, z1, x0, y0, z1);
        edge(t, x0, y0, z1, x0, y0, z0);
        // 顶面 4 棱
        edge(t, x0, y1, z0, x1, y1, z0);
        edge(t, x1, y1, z0, x1, y1, z1);
        edge(t, x1, y1, z1, x0, y1, z1);
        edge(t, x0, y1, z1, x0, y1, z0);
        // 竖直 4 棱
        edge(t, x0, y0, z0, x0, y1, z0);
        edge(t, x1, y0, z0, x1, y1, z0);
        edge(t, x1, y0, z1, x1, y1, z1);
        edge(t, x0, y0, z1, x0, y1, z1);
        t.draw();
    }

    private static void edge(Tessellator t, double ax, double ay, double az, double bx, double by, double bz) {
        t.addVertex(ax, ay, az);
        t.addVertex(bx, by, bz);
    }
}
