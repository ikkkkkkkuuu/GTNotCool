package com.xyp.gtnc.Common.building;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.xyp.gtnc.Common.packet.building.MessagePlacePixels;
import com.xyp.gtnc.Common.packet.building.MessageSyncPixelColors;
import com.xyp.gtnc.Loader.BlockLoader;
import com.xyp.gtnc.ScienceNotCool;

/**
 * 建筑生成器的<b>服务端逻辑</b>：放置像素外壳、记录撤销数据、把颜色同步回客户端。
 * <p>
 * 客户端算好外壳（结构 + 材质像素）后分片发来（{@link MessagePlacePixels}）。这里逐格放 {@link BlockPixel}、
 * 把 (坐标→颜色) 记进 {@link PixelBuildingData}（撤销权威源），并把颜色回传给玩家客户端渲染（{@link MessageSyncPixelColors}）。
 * <p>
 * 全部在服务端主线程执行（网络包 handler 已 {@code addScheduledTask} 调度）。
 */
public final class PixelBuildingManager {

    private PixelBuildingManager() {}

    /** 每个网络分片的最大像素数（放置 + 回传同步都按此分片）。 */
    public static final int CHUNK_SIZE = 2000;

    /**
     * <b>按时间预算自适应限速</b>：每 tick 处理放置/撤销直到用掉 {@link #TICK_BUDGET_NS}，到点即停、下 tick 续。
     * 这样无论 setBlock+光照多重、机器多慢，单 tick 永不超时 → 绝不 "Can't keep up"；机器快则放得快、慢则自动放慢。
     * 每处理 {@link #TIME_CHECK_BATCH} 个检查一次时间（避免频繁 System.nanoTime 开销）。
     */
    private static final long TICK_BUDGET_NS = 20_000_000L; // 20ms（50ms/tick 里留 30ms 给游戏其余部分）
    private static final int TIME_CHECK_BATCH = 2000;
    /** 每 tick 绝对硬上限（防极端情况下时间预算失灵）。 */
    private static final int MAX_OPS_PER_TICK = 200_000;

    /** 一个待处理的放置/撤销作业（限速队列元素）。 */
    private static final class Job {

        final int dim;
        final EntityPlayerMP player; // 放置的目标玩家（撤销广播时可能为 null）
        final int genX, genY, genZ;
        final boolean place; // true=放置, false=撤销
        final boolean markGeneratedOnFinish;
        // 放置用：绝对坐标 + 颜色；撤销用：绝对坐标（颜色忽略）。
        final int[] xs, ys, zs, col;
        int cursor = 0;
        PixelBuildingData.Building building; // 放置时的建筑记录

        Job(int dim, EntityPlayerMP player, int gx, int gy, int gz, boolean place, boolean mark, int[] xs, int[] ys,
            int[] zs, int[] col) {
            this.dim = dim;
            this.player = player;
            this.genX = gx;
            this.genY = gy;
            this.genZ = gz;
            this.place = place;
            this.markGeneratedOnFinish = mark;
            this.xs = xs;
            this.ys = ys;
            this.zs = zs;
            this.col = col;
        }
    }

    /** 全局限速队列（服务端主线程访问，无需同步）。 */
    private static final java.util.ArrayDeque<Job> JOBS = new java.util.ArrayDeque<>();

    /** 放置一个分片：不再立即放，而是入队，由 tick 限速处理。 */
    public static void placeChunk(EntityPlayerMP player, MessagePlacePixels msg) {
        World world = player.worldObj;
        if (world == null || world.isRemote) return;
        PixelBuildingData.loadInstance(world);
        PixelBuildingData data = PixelBuildingData.INSTANCE;
        int dim = world.provider.dimensionId;

        // 首个分片：若该生成器已有旧建筑，先撤销（避免叠加残留），再新建记录。
        PixelBuildingData.Building building;
        if (msg.first) {
            undo(player, msg.genX, msg.genY, msg.genZ);
            building = new PixelBuildingData.Building(dim, msg.genX, msg.genY, msg.genZ);
            data.put(building);
        } else {
            building = data.get(msg.genX, msg.genY, msg.genZ);
            if (building == null) {
                building = new PixelBuildingData.Building(dim, msg.genX, msg.genY, msg.genZ);
                data.put(building);
            }
        }

        int n = msg.dx == null ? 0 : msg.dx.length;
        int[] xs = new int[n], ys = new int[n], zs = new int[n], col = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = msg.genX + msg.dx[i];
            ys[i] = msg.genY + msg.dy[i];
            zs[i] = msg.genZ + msg.dz[i];
            col[i] = msg.rgb[i] & 0xFFFFFF;
        }
        Job job = new Job(dim, player, msg.genX, msg.genY, msg.genZ, true, msg.last, xs, ys, zs, col);
        job.building = building;
        JOBS.add(job);
    }

    /** 服务端每 tick 处理限速队列。由 {@link PixelBuildingEventHandler} 在 ServerTickEvent 调用。 */
    public static void onServerTick() {
        if (JOBS.isEmpty()) return;
        long deadline = System.nanoTime() + TICK_BUDGET_NS;
        int opsThisTick = 0;
        while (!JOBS.isEmpty() && opsThisTick < MAX_OPS_PER_TICK) {
            Job job = JOBS.peek();
            int did = processJob(job, TIME_CHECK_BATCH);
            opsThisTick += did;
            if (job.cursor >= job.xs.length) {
                JOBS.poll(); // 本作业完成
                onJobFinished(job);
            }
            // 每小批检查一次时间预算，用尽即停（下 tick 续）。
            if (System.nanoTime() >= deadline) break;
        }
    }

    /** 处理一个作业的一部分（最多 budget 个），返回实际处理数。 */
    private static int processJob(Job job, int budget) {
        WorldServer world = worldForDim(job.dim);
        if (world == null) {
            job.cursor = job.xs.length; // 世界没了，丢弃
            return 0;
        }
        PixelBuildingData.loadInstance(world);
        PixelBuildingData data = PixelBuildingData.INSTANCE;

        int start = job.cursor;
        int end = Math.min(start + budget, job.xs.length);
        int done = end - start;

        // 收集本批实际改动的坐标用于颜色同步。
        int[] sx = new int[done], sy = new int[done], sz = new int[done], sc = new int[done];
        int k = 0;
        for (int i = start; i < end; i++) {
            int x = job.xs[i], y = job.ys[i], z = job.zs[i];
            if (y < 0 || y > 255) continue;
            if (job.place) {
                if (!world.isAirBlock(x, y, z)) continue; // 只在空气处放
                world.setBlock(x, y, z, BlockLoader.blockPixel, 0, 2);
                data.addPixel(job.building, x, y, z, job.col[i]);
                sc[k] = job.col[i];
            } else {
                if (world.getBlock(x, y, z) != BlockLoader.blockPixel) continue; // 只清仍是像素方块的
                world.setBlock(x, y, z, net.minecraft.init.Blocks.air, 0, 2);
            }
            sx[k] = x;
            sy[k] = y;
            sz[k] = z;
            k++;
        }
        job.cursor = end;
        data.markDirty();

        // 同步这批颜色（放置=写色，撤销=清色）。
        if (k > 0) {
            MessageSyncPixelColors m = new MessageSyncPixelColors(
                job.dim,
                !job.place,
                trim(sx, k),
                trim(sy, k),
                trim(sz, k),
                job.place ? trim(sc, k) : null);
            if (job.player != null) {
                ScienceNotCool.channel.sendTo(m, job.player);
            } else {
                ScienceNotCool.channel.sendToDimension(m, job.dim);
            }
        }
        return done;
    }

    private static void onJobFinished(Job job) {
        WorldServer world = worldForDim(job.dim);
        if (world == null) return;
        if (job.markGeneratedOnFinish) {
            net.minecraft.tileentity.TileEntity te = world.getTileEntity(job.genX, job.genY, job.genZ);
            if (te instanceof TileBuildingGenerator gen) {
                gen.setGenerated(job.place);
            }
        }
    }

    private static WorldServer worldForDim(int dim) {
        net.minecraft.server.MinecraftServer srv = net.minecraft.server.MinecraftServer.getServer();
        if (srv == null) return null;
        for (WorldServer w : srv.worldServers) {
            if (w.provider.dimensionId == dim) return w;
        }
        return null;
    }

    /** 撤销：还原某生成器记录的全部像素（GUI 撤销按钮触发，给操作玩家同步）。 */
    public static void undo(EntityPlayerMP player, int genX, int genY, int genZ) {
        if (player == null || player.worldObj == null) return;
        undoInternal(player.worldObj, genX, genY, genZ, player);
    }

    /** 撤销：无玩家上下文（如破坏生成器方块时），清除颜色广播给该维度所有玩家。 */
    public static void undoAt(World world, int genX, int genY, int genZ) {
        undoInternal(world, genX, genY, genZ, null);
    }

    private static void undoInternal(World world, int genX, int genY, int genZ, EntityPlayerMP player) {
        if (world == null || world.isRemote) return;
        PixelBuildingData.loadInstance(world);
        PixelBuildingData data = PixelBuildingData.INSTANCE;
        PixelBuildingData.Building b = data.remove(genX, genY, genZ);
        // 立即清「已生成」标记（不入队，让预览线框马上恢复）。
        net.minecraft.tileentity.TileEntity te = world.getTileEntity(genX, genY, genZ);
        if (te instanceof TileBuildingGenerator gen) {
            gen.setGenerated(false);
        }
        if (b == null || b.pixels.isEmpty()) return;
        int dim = world.provider.dimensionId;

        // 撤销也入限速队列（大建筑逐 tick 清，避免卡顿）。把该建筑所有坐标打成一个撤销作业。
        int n = b.pixels.size();
        int[] xs = new int[n], ys = new int[n], zs = new int[n];
        int i = 0;
        for (Map.Entry<Long, Integer> e : b.pixels.entrySet()) {
            long p = e.getKey();
            xs[i] = CoordinatePacker.unpackX(p);
            ys[i] = CoordinatePacker.unpackY(p);
            zs[i] = CoordinatePacker.unpackZ(p);
            i++;
        }
        JOBS.add(new Job(dim, player, genX, genY, genZ, false, false, xs, ys, zs, null));
    }

    private static int[] trim(int[] a, int n) {
        if (n == a.length) return a;
        int[] r = new int[n];
        System.arraycopy(a, 0, r, 0, n);
        return r;
    }

    /** 供世界卸载复位（防单机切档残留）。 */
    public static void onWorldUnload(World world) {
        if (!world.isRemote && world.provider.dimensionId == 0) {
            JOBS.clear(); // 清空限速队列，防残留作业跨存档执行
            PixelBuildingData.reset();
        }
    }

    /** 供世界加载时初始化持久化。 */
    public static void onWorldLoad(World world) {
        if (!world.isRemote) {
            try {
                PixelBuildingData.loadInstance(world);
            } catch (Exception e) {
                ScienceNotCool.LOG.error("[PixelBuilding] Failed to load WorldSavedData", e);
            }
        }
    }

    /**
     * 玩家开始观察某区块：把该区块内所有像素方块颜色补发给他，供客户端渲染（重进存档/重连/新玩家不丢色）。
     * 客户端 {@link PixelColorStore} 是内存态、进档时空，靠此按区块懒同步补回。
     */
    public static void onChunkWatch(EntityPlayerMP player, int chunkX, int chunkZ) {
        World world = player.worldObj;
        if (world == null || world.isRemote) return;
        if (PixelBuildingData.INSTANCE == null) return;
        int dim = world.provider.dimensionId;
        List<int[]> pixels = PixelBuildingData.INSTANCE.getChunkPixels(dim, chunkX, chunkZ);
        if (pixels.isEmpty()) return;

        // 按 CHUNK_SIZE 分片发送。
        int total = pixels.size();
        for (int start = 0; start < total; start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, total);
            int m = end - start;
            int[] xs = new int[m], ys = new int[m], zs = new int[m], col = new int[m];
            for (int i = 0; i < m; i++) {
                int[] p = pixels.get(start + i);
                xs[i] = p[0];
                ys[i] = p[1];
                zs[i] = p[2];
                col[i] = p[3];
            }
            ScienceNotCool.channel.sendTo(new MessageSyncPixelColors(dim, false, xs, ys, zs, col), player);
        }
    }
}
