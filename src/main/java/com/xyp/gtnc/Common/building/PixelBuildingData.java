package com.xyp.gtnc.Common.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.xyp.gtnc.ScienceNotCool;

/**
 * 建筑生成器放置记录的<b>服务端持久化</b>（撤销的权威数据源）。
 * <p>
 * 每个「建筑」以其<b>生成器方块坐标</b>为 key，记录该次生成放置的<b>所有像素方块坐标</b>及各自颜色。
 * 撤销时按 key 取回全部坐标 → 逐格设回空气 + 清颜色 + 通知客户端，实现零残留一键还原。
 * <p>
 * 仿照 {@code MEBridgeWorldSavedData}：静态单例 + {@code sLoaded} 防重入 + 逐条 NBTTagList 写。
 * <p>
 * <b>仅服务端</b>：客户端渲染用的颜色在 {@link PixelColorStore}。本表只在服务端读写。
 */
public class PixelBuildingData extends WorldSavedData {

    public static PixelBuildingData INSTANCE;
    private static boolean sLoaded = false;

    public static final String DATA_NAME = "GTNC_PixelBuildingData";
    private static final String KEY_LIST = "buildings";

    /** 一次生成的记录：生成器坐标 + 维度 + 放置的像素（坐标打包 → 0xRRGGBB）。 */
    public static final class Building {

        public final int dim;
        public final int ctrlX, ctrlY, ctrlZ;
        /** 打包坐标 → 0xRRGGBB。 */
        public final Map<Long, Integer> pixels = new HashMap<>();

        public Building(int dim, int ctrlX, int ctrlY, int ctrlZ) {
            this.dim = dim;
            this.ctrlX = ctrlX;
            this.ctrlY = ctrlY;
            this.ctrlZ = ctrlZ;
        }
    }

    /** key = 打包的生成器坐标（同维度内唯一；跨维度由 dim 字段区分，key 冲突概率忽略）。 */
    private final Map<Long, Building> buildings = new HashMap<>();

    /**
     * 按区块的颜色索引（供 {@code ChunkWatchEvent} 懒同步用）：{@code (dim, chunkX, chunkZ)} → 该区块内所有像素
     * {@code (x,y,z,rgb)}。玩家开始观察某区块时，服务端据此把该区块颜色补发给客户端（重进存档/新玩家不丢色）。
     * 与 {@link #buildings} 冗余，但按区块查颜色是热路径，需 O(区块内像素) 而非遍历全部建筑。
     */
    private final Map<Long, List<int[]>> chunkIndex = new HashMap<>();

    /** (dim, chunkX, chunkZ) 压成一个 long key。dim 用低 8 位偏移足够（维度 id 范围小）。 */
    private static long chunkKey(int dim, int cx, int cz) {
        // cx/cz 各 24 位（±800 万区块，远超边界），dim 16 位。
        return ((long) (dim & 0xFFFF) << 48) | ((long) (cx & 0xFFFFFF) << 24) | (cz & 0xFFFFFF);
    }

    private void indexAdd(int dim, int x, int y, int z, int rgb) {
        chunkIndex.computeIfAbsent(chunkKey(dim, x >> 4, z >> 4), k -> new ArrayList<>())
            .add(new int[] { x, y, z, rgb });
    }

    private void reindexAll() {
        chunkIndex.clear();
        for (Building b : buildings.values()) {
            for (Map.Entry<Long, Integer> e : b.pixels.entrySet()) {
                long p = e.getKey();
                indexAdd(
                    b.dim,
                    CoordinatePacker.unpackX(p),
                    CoordinatePacker.unpackY(p),
                    CoordinatePacker.unpackZ(p),
                    e.getValue());
            }
        }
    }

    /** 取某区块内所有像素颜色（{x,y,z,rgb}），无则空列表。供 ChunkWatchEvent 补发。 */
    public List<int[]> getChunkPixels(int dim, int chunkX, int chunkZ) {
        List<int[]> l = chunkIndex.get(chunkKey(dim, chunkX, chunkZ));
        return l == null ? java.util.Collections.emptyList() : l;
    }

    public PixelBuildingData() {
        super(DATA_NAME);
    }

    public PixelBuildingData(String name) {
        super(name);
    }

    public static void loadInstance(World world) {
        if (sLoaded) return;
        MapStorage storage = world.mapStorage;
        INSTANCE = (PixelBuildingData) storage.loadData(PixelBuildingData.class, DATA_NAME);
        if (INSTANCE == null) {
            INSTANCE = new PixelBuildingData();
            storage.setData(DATA_NAME, INSTANCE);
        }
        sLoaded = true;
    }

    public static void reset() {
        INSTANCE = null;
        sLoaded = false;
    }

    private static long ctrlKey(int x, int y, int z) {
        return CoordinatePacker.pack(x, y, z);
    }

    public Building get(int x, int y, int z) {
        return buildings.get(ctrlKey(x, y, z));
    }

    public void put(Building b) {
        buildings.put(ctrlKey(b.ctrlX, b.ctrlY, b.ctrlZ), b);
        markDirty();
    }

    public Building remove(int x, int y, int z) {
        Building b = buildings.remove(ctrlKey(x, y, z));
        if (b != null) {
            markDirty();
            reindexAll(); // 该建筑的像素从区块索引里清掉（撤销）。
        }
        return b;
    }

    /** 记录一个像素（放置时调用）：同时更新建筑与区块索引。 */
    public void addPixel(Building b, int x, int y, int z, int rgb) {
        b.pixels.put(CoordinatePacker.pack(x, y, z), rgb & 0xFFFFFF);
        indexAdd(b.dim, x, y, z, rgb & 0xFFFFFF);
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        buildings.clear();
        NBTTagList list = nbt.getTagList(KEY_LIST, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            try {
                int dim = tag.getInteger("dim");
                int cx = tag.getInteger("cx");
                int cy = tag.getInteger("cy");
                int cz = tag.getInteger("cz");
                Building b = new Building(dim, cx, cy, cz);
                // 1.7.10 无 long 数组 NBT，坐标拆成 xs/ys/zs 三个 int[] 存。
                int[] xs = tag.getIntArray("xs");
                int[] ys = tag.getIntArray("ys");
                int[] zs = tag.getIntArray("zs");
                int[] col = tag.getIntArray("col");
                int n = Math.min(Math.min(xs.length, ys.length), Math.min(zs.length, col.length));
                for (int j = 0; j < n; j++) {
                    b.pixels.put(CoordinatePacker.pack(xs[j], ys[j], zs[j]), col[j]);
                }
                buildings.put(ctrlKey(cx, cy, cz), b);
            } catch (RuntimeException e) {
                ScienceNotCool.LOG.warn("[PixelBuilding] Skipping invalid building entry {}", i);
            }
        }
        reindexAll(); // 加载后重建区块索引。
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (Building b : buildings.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("dim", b.dim);
            tag.setInteger("cx", b.ctrlX);
            tag.setInteger("cy", b.ctrlY);
            tag.setInteger("cz", b.ctrlZ);
            // 1.7.10 无 long 数组 NBT，坐标拆成 xs/ys/zs 三个 int[] 存（与 readFromNBT 对应）。
            int size = b.pixels.size();
            int[] xs = new int[size];
            int[] ys = new int[size];
            int[] zs = new int[size];
            int[] col = new int[size];
            int j = 0;
            for (Map.Entry<Long, Integer> e : b.pixels.entrySet()) {
                long p = e.getKey();
                xs[j] = CoordinatePacker.unpackX(p);
                ys[j] = CoordinatePacker.unpackY(p);
                zs[j] = CoordinatePacker.unpackZ(p);
                col[j] = e.getValue();
                j++;
            }
            tag.setIntArray("xs", xs);
            tag.setIntArray("ys", ys);
            tag.setIntArray("zs", zs);
            tag.setIntArray("col", col);
            list.appendTag(tag);
        }
        nbt.setTag(KEY_LIST, list);
    }

    /** 便捷：取所有建筑（供整世界重建颜色同步用，M2/M3）。 */
    public List<Building> all() {
        return new ArrayList<>(buildings.values());
    }
}
