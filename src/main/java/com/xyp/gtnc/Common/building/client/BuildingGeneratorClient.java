package com.xyp.gtnc.Common.building.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.xyp.gtnc.Common.building.PixelBuildingManager;
import com.xyp.gtnc.Common.packet.building.MessagePlacePixels;
import com.xyp.gtnc.Common.packet.building.MessageUndoBuilding;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 客户端驱动：把玩家指定的多方块机器提取成结构，算出<b>等比放大的空壳</b>（外表面铺像素方块），分片发往服务端放置。
 * <p>
 * <b>M1（当前）</b>：放大倍率固定 {@link #SCALE}；每个原方块放大成 {@code SCALE³} 区域，只在<b>朝外的面</b>
 * （该方向邻居在结构里为空）铺一层像素方块，内部中空。颜色用该面材质的<b>平均色</b>（整格朝外面单色），先验证
 * 「选机器 → 生成 → 撤销」闭环。逐像素贴图色是 M2。
 * <p>
 * 六个面方向（与 {@link net.minecraft.block.Block#getIcon} 一致）：0=下(-Y) 1=上(+Y) 2=北(-Z) 3=南(+Z) 4=西(-X) 5=东(+X)。
 */
@SideOnly(Side.CLIENT)
public final class BuildingGeneratorClient {

    private BuildingGeneratorClient() {}

    /** M1 固定放大倍率（16x 材质 = 16）。后续做成可配 / GUI 可调。 */
    public static final int SCALE = 16;

    /**
     * 单结构像素方块数上限，超出则拒绝（防误炸 / 防网络包与内存爆炸）。
     * 服务端按时间预算自适应限速放置（绝不卡 tick），量级承受力较高，故放宽到 200 万
     * （约 15×15×20 的结构放大后规模）。仍保留上限防止极端误操作把存档撑爆。
     */
    private static final int MAX_PIXELS = 2_000_000;

    private static final int[] DX = { 0, 0, 0, 0, -1, 1 };
    private static final int[] DY = { -1, 1, 0, 0, 0, 0 };
    private static final int[] DZ = { 0, 0, -1, 1, 0, 0 };

    /** 兼容旧签名（无旋转/偏移，默认落生成器上方一格）。 */
    public static void requestGenerate(World world, int genX, int genY, int genZ, ItemStack machineStack) {
        requestGenerate(world, genX, genY, genZ, machineStack, 0, 0, 1, 0);
    }

    /**
     * @param rotation       0..3（0/90/180/270° 绕 Y 轴，G4 生效）
     * @param offX/offY/offZ 相对生成器的偏移（建筑局部原点落在 gen + off）
     */
    public static void requestGenerate(World world, int genX, int genY, int genZ, ItemStack machineStack, int rotation,
        int offX, int offY, int offZ) {
        StructureExtractor.Result res = StructureExtractor.extract(machineStack);
        if (res == null || res.cells.isEmpty()) {
            chat("gtnc.building_generator.hint.not_multiblock");
            return;
        }

        // 快速估算方块量（外表面格数 × SCALE²，粗略），超限直接拒绝。
        long estimate = (long) res.cells.size() * SCALE * SCALE;
        if (estimate > MAX_PIXELS) {
            chat("gtnc.building_generator.hint.too_large");
            return;
        }

        // 结构占用集合。
        Set<Long> occupied = new HashSet<>();
        for (StructureExtractor.Cell c : res.cells) {
            occupied.add(CoordinatePacker.pack(c.lx, c.ly, c.lz));
        }

        // 只留「最外层外壳」：从包围盒外部对空气做 3D flood fill，标记所有「从外部可达的空气格」。
        // 一个面朝外 = 该方向邻居是外部可达空气；内部空腔（被墙包住的空气）灌不到，故内壁不铺。
        Set<Long> exterior = computeExteriorAir(occupied, res.sizeX, res.sizeY, res.sizeZ);

        // 生成的建筑放在生成器正上方：局部原点 (0,0,0) 映射到 (genX, genY+1, genZ)。
        // 相对生成器的偏移用 short 传输，故要求在 ±32767 内（SCALE=16 时可容纳 2048 格边长的结构，足够）。
        List<int[]> pending = new ArrayList<>(); // 每项 {dx, dy, dz, rgb}
        for (StructureExtractor.Cell c : res.cells) {
            Block block = c.block;
            if (block == null || block == Blocks.air) continue;
            for (int f = 0; f < 6; f++) {
                long neighbor = CoordinatePacker.pack(c.lx + DX[f], c.ly + DY[f], c.lz + DZ[f]);
                // 只铺朝向「外部可达空气」的面：邻居被占（朝内）或邻居是内部空腔（非外部可达）都跳过。
                if (occupied.contains(neighbor)) continue;
                if (!exterior.contains(neighbor)) continue;
                PixelSampler.Sprite sprite = null;
                // MTE（控制器/仓）：优先取真实多层材质（含正面 overlay）；失败回退 block.getIcon。
                if (c.gtTile != null) {
                    sprite = MTETextureExtractor.sample(c.gtTile, f);
                }
                if (sprite == null) {
                    sprite = PixelSampler.sample(block, c.meta, f);
                }
                addFacePixels(pending, c, f, sprite);
            }
        }

        if (pending.isEmpty()) {
            chat("gtnc.building_generator.hint.not_multiblock");
            return;
        }
        if (pending.size() > MAX_PIXELS) {
            chat("gtnc.building_generator.hint.too_large");
            return;
        }

        // 先绕 Y 轴旋转（90°×rotation），再施加 XYZ 偏移。旋转后 footprint 仍从 (0,0) 起，故偏移语义不变。
        int w = res.sizeX * SCALE; // 未旋转 X 跨度
        int d = res.sizeZ * SCALE; // 未旋转 Z 跨度
        int r = ((rotation % 4) + 4) % 4;
        for (int[] p : pending) {
            int x = p[0], z = p[2];
            int rx, rz;
            switch (r) {
                case 1: // 90°
                    rx = d - 1 - z;
                    rz = x;
                    break;
                case 2: // 180°
                    rx = w - 1 - x;
                    rz = d - 1 - z;
                    break;
                case 3: // 270°
                    rx = z;
                    rz = w - 1 - x;
                    break;
                default:
                    rx = x;
                    rz = z;
                    break;
            }
            p[0] = rx + offX;
            p[1] = p[1] + offY;
            p[2] = rz + offZ;
        }

        sendInChunks(genX, genY, genZ, pending);
        chat("gtnc.building_generator.hint.generated");
    }

    /**
     * 从包围盒外部对空气做 3D flood fill，返回所有「从外部可达的空气格」的打包坐标集合。
     * <p>
     * 灌水范围是把结构包围盒各向外扩 1 格 [-1, size]，从角上 (-1,-1,-1) 起 6 邻接扩散：非占用格且在范围内即可蔓延。
     * 结构内部被墙完全包住的空腔灌不到 → 不在返回集合里 → 其内壁不会被判「朝外」。
     */
    private static Set<Long> computeExteriorAir(Set<Long> occupied, int sizeX, int sizeY, int sizeZ) {
        Set<Long> exterior = new HashSet<>();
        java.util.ArrayDeque<long[]> queue = new java.util.ArrayDeque<>();
        long startKey = CoordinatePacker.pack(-1, -1, -1);
        exterior.add(startKey);
        queue.add(new long[] { -1, -1, -1 });
        while (!queue.isEmpty()) {
            long[] p = queue.poll();
            int x = (int) p[0], y = (int) p[1], z = (int) p[2];
            for (int f = 0; f < 6; f++) {
                int nx = x + DX[f], ny = y + DY[f], nz = z + DZ[f];
                // 限制在外扩 1 格的范围内 [-1, size]。
                if (nx < -1 || ny < -1 || nz < -1 || nx > sizeX || ny > sizeY || nz > sizeZ) continue;
                long key = CoordinatePacker.pack(nx, ny, nz);
                if (exterior.contains(key)) continue;
                if (occupied.contains(key)) continue; // 撞到方块，停下（该面成为外壳面）
                exterior.add(key);
                queue.add(new long[] { nx, ny, nz });
            }
        }
        return exterior;
    }

    /**
     * 把一格的某个朝外面按<b>逐像素</b>铺一层像素方块（M2）：材质 16×16 的每个像素 → 放大区域对应平面上的一个方块。
     * SCALE=16 恰好等于材质宽，一像素一方块。
     * <p>
     * 局部坐标 (lx,ly,lz) → 放大后区域 x∈[bx, bx+S)、y∈[by, by+S)、z∈[bz, bz+S)（相对生成器，y 从上方一格起）。
     * 材质像素索引 (tu, tv)：tu 为列(左→右)，tv 为行(上→下，v=0 是顶行)。各面按 Minecraft 贴图习惯映射朝向，
     * 使放大后的贴图与原方块该面观感一致。透明像素（rgb<0）跳过，外壳该处留孔。
     * <p>
     * sprite 为 null（采样失败）时整面用中灰兜底，避免破洞。
     */
    private static void addFacePixels(List<int[]> pending, StructureExtractor.Cell c, int face,
        PixelSampler.Sprite sprite) {
        int bx = c.lx * SCALE;
        int by = c.ly * SCALE; // 基点，偏移在 sendInChunks 里统一施加
        int bz = c.lz * SCALE;
        int S = SCALE;
        int sw = sprite != null ? sprite.width : S;

        for (int a = 0; a < S; a++) {
            for (int b = 0; b < S; b++) {
                // (a,b) 是放大区域该面网格的两个轴；(tu,tv) 是材质采样坐标。
                int tu, tv, dx, dy, dz;
                switch (face) {
                    case 0: // 下 -Y：y=by 平面。俯视，u=x, v=z
                        dx = bx + a;
                        dy = by;
                        dz = bz + b;
                        tu = a;
                        tv = b;
                        break;
                    case 1: // 上 +Y：y=by+S-1 平面
                        dx = bx + a;
                        dy = by + S - 1;
                        dz = bz + b;
                        tu = a;
                        tv = b;
                        break;
                    case 2: // 北 -Z：z=bz 平面。正视此面时 u 沿 -x(东看向西)，v 沿 -y(上→下用 tv)
                        dx = bx + a;
                        dy = by + (S - 1 - b);
                        dz = bz;
                        tu = S - 1 - a;
                        tv = b;
                        break;
                    case 3: // 南 +Z：z=bz+S-1 平面
                        dx = bx + a;
                        dy = by + (S - 1 - b);
                        dz = bz + S - 1;
                        tu = a;
                        tv = b;
                        break;
                    case 4: // 西 -X：x=bx 平面
                        dx = bx;
                        dy = by + (S - 1 - b);
                        dz = bz + a;
                        tu = a;
                        tv = b;
                        break;
                    case 5: // 东 +X：x=bx+S-1 平面
                        dx = bx + S - 1;
                        dy = by + (S - 1 - b);
                        dz = bz + a;
                        tu = S - 1 - a;
                        tv = b;
                        break;
                    default:
                        continue;
                }
                int rgb;
                if (sprite == null) {
                    rgb = 0x808080; // 采样失败兜底中灰
                } else {
                    // 材质分辨率可能非 16（如 32x），按比例缩放采样坐标。
                    int su = sw == S ? tu : tu * sw / S;
                    int sv = sw == S ? tv : tv * sprite.height / S;
                    int idx = sv * sprite.width + su;
                    int c2 = (idx >= 0 && idx < sprite.rgb.length) ? sprite.rgb[idx] : -1;
                    if (c2 < 0) continue; // 透明像素：留孔
                    rgb = c2;
                }
                pending.add(new int[] { dx, dy, dz, rgb });
            }
        }
    }

    private static void sendInChunks(int genX, int genY, int genZ, List<int[]> pending) {
        int total = pending.size();
        int chunk = PixelBuildingManager.CHUNK_SIZE;
        for (int start = 0; start < total; start += chunk) {
            int end = Math.min(start + chunk, total);
            int m = end - start;
            int[] dx = new int[m], dy = new int[m], dz = new int[m], rgb = new int[m];
            for (int i = 0; i < m; i++) {
                int[] p = pending.get(start + i);
                dx[i] = p[0];
                dy[i] = p[1];
                dz[i] = p[2];
                rgb[i] = p[3];
            }
            boolean first = start == 0;
            boolean last = end == total;
            ScienceNotCool.channel.sendToServer(new MessagePlacePixels(genX, genY, genZ, first, last, dx, dy, dz, rgb));
        }
    }

    public static void requestUndo(int genX, int genY, int genZ) {
        ScienceNotCool.channel.sendToServer(new MessageUndoBuilding(genX, genY, genZ));
        chat("gtnc.building_generator.hint.undone");
    }

    // 生成器提示信息（chat 提示用）。翻译注释集中放此，供 lang 自动生成。
    // #tr gtnc.building_generator.hint.not_multiblock
    // # §cThat item is not a previewable multiblock machine.
    // # zh_CN §c该物品不是可预览的多方块机器。
    // #tr gtnc.building_generator.hint.too_large
    // # §cStructure too large, exceeds the generation limit.
    // # zh_CN §c结构过大，超出生成上限。
    // #tr gtnc.building_generator.hint.generated
    // # §aScaled building generated.
    // # zh_CN §a已生成放大建筑。
    // #tr gtnc.building_generator.hint.undone
    // # §aBuilding removed for this generator.
    // # zh_CN §a已撤销本生成器的建筑。
    private static void chat(String langKey) {
        EntityPlayer p = net.minecraft.client.Minecraft.getMinecraft().thePlayer;
        if (p != null) p.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(langKey)));
    }
}
