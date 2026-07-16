package com.xyp.gtnc.Common.building;

import net.minecraft.client.Minecraft;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

/**
 * 客户端专用：像素方块的 (维度, 坐标) → RGB 颜色映射。
 * <p>
 * {@link BlockPixel#colorMultiplier} 在区块重建时逐格查询这里返回全彩颜色（结果被烤进静态顶点缓存，
 * 不是每帧调用，故几十万方块也很轻）。因此像素方块本体是一张纯白 16×16 贴图 + 这里的乘色 = 任意 RGB，
 * 无需 TileEntity、无需为每种颜色注册方块。
 * <p>
 * 坐标用 {@link CoordinatePacker} 打包成 long（与 {@code BRUtil} 一致），再按维度 id 分表，避免不同维度
 * 相同坐标的颜色互相覆盖。颜色为 0xRRGGBB。
 * <p>
 * <b>仅客户端</b>：服务端的权威颜色数据存在 {@link PixelBuildingData}（WorldSavedData）。M1 阶段生成时由
 * 客户端在本地直接填充本表（同会话即时全彩）；跨会话 / 多人的补发同步在后续里程碑补齐（{@code ChunkWatchEvent}）。
 */
public final class PixelColorStore {

    private PixelColorStore() {}

    private static final int NO_COLOR = 0xFFFFFF;

    /** dimId -> (打包坐标 -> 0xRRGGBB)。默认白色（未登记的像素方块渲染为原白贴图）。 */
    private static final Int2ObjectMap<Long2IntMap> COLORS = new Int2ObjectOpenHashMap<>();

    private static Long2IntMap dimMap(int dim, boolean create) {
        Long2IntMap m = COLORS.get(dim);
        if (m == null && create) {
            m = new Long2IntOpenHashMap();
            m.defaultReturnValue(NO_COLOR);
            COLORS.put(dim, m);
        }
        return m;
    }

    public static void put(int dim, int x, int y, int z, int rgb) {
        dimMap(dim, true).put(CoordinatePacker.pack(x, y, z), rgb & 0xFFFFFF);
    }

    public static int get(int dim, int x, int y, int z) {
        Long2IntMap m = dimMap(dim, false);
        return m == null ? NO_COLOR : m.get(CoordinatePacker.pack(x, y, z));
    }

    public static void remove(int dim, int x, int y, int z) {
        Long2IntMap m = dimMap(dim, false);
        if (m != null) m.remove(CoordinatePacker.pack(x, y, z));
    }

    public static void clearDim(int dim) {
        COLORS.remove(dim);
    }

    public static void clearAll() {
        COLORS.clear();
    }

    /**
     * 填充一批颜色后，请求这些坐标所在区块重建渲染（否则 colorMultiplier 不会被重新采样）。
     * 由放置流程在批量 put 之后按包围盒调用。
     */
    @SideOnly(Side.CLIENT)
    public static void requestRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null) {
            mc.theWorld.markBlockRangeForRenderUpdate(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
