package com.xyp.gtnc.Common.building.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructableProvider;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import blockrenderer6343.api.utils.CreativeItemSource;
import blockrenderer6343.client.renderer.ImmediateWorldSceneRenderer;
import blockrenderer6343.client.utils.BRUtil;
import blockrenderer6343.client.world.TrackedDummyWorld;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.INEIPreviewModifier;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.ITurnable;
import gregtech.api.threads.RunnableMachineUpdate;

/**
 * 客户端：把一台多方块机器的结构<b>建进离屏伪世界</b>，产出每格的 {@code (相对坐标, Block, meta)}。
 * <p>
 * 复刻 BlockRenderer6343 的 {@code GTGuiMultiblockHandler#placeMultiblock}：用机器物品在 {@link TrackedDummyWorld}
 * 里 {@code onItemUse} 放下控制器 → 设朝向 → {@code survivalConstruct} 循环把结构补全 → 遍历伪世界方块。
 * 相对坐标以控制器所在格为原点（结构最小角归一化后再减控制器偏移，使控制器在 (?,?,?) 处；这里直接用伪世界
 * 绝对坐标减去最小角，得到 [0..size) 的局部坐标，控制器位置一并返回供放置时对齐）。
 * <p>
 * <b>仅客户端</b>：伪世界与材质图集都只在客户端存在。
 */
@SideOnly(Side.CLIENT)
public final class StructureExtractor {

    private StructureExtractor() {}

    /** 伪世界里放置控制器的固定位置（与 BR6343 的 MB_PLACE_POS 一致，避开边界）。 */
    private static final int PLACE_X = 0, PLACE_Y = 64, PLACE_Z = 0;
    private static final int MAX_PLACE_ROUNDS = 100;

    /** 提取结果里的一格。坐标是相对结构最小角的局部坐标（>=0）。 */
    public static final class Cell {

        public final int lx, ly, lz;
        public final Block block;
        public final int meta;
        /**
         * 若该格是 GT 机器（MTE：控制器/仓等），保留其 tile，用于取真实多层材质（含正面 overlay）；普通 casing 为 null。
         * 注意：这是伪世界里的 tile，采样在同一提取流程内、伪世界销毁前完成即可。
         */
        public final IGregTechTileEntity gtTile;

        Cell(int lx, int ly, int lz, Block block, int meta, IGregTechTileEntity gtTile) {
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.block = block;
            this.meta = meta;
            this.gtTile = gtTile;
        }
    }

    public static final class Result {

        public final List<Cell> cells;
        /** 结构包围盒尺寸（格）。 */
        public final int sizeX, sizeY, sizeZ;
        /** 控制器在局部坐标系里的位置。 */
        public final int ctrlLx, ctrlLy, ctrlLz;

        Result(List<Cell> cells, int sizeX, int sizeY, int sizeZ, int ctrlLx, int ctrlLy, int ctrlLz) {
            this.cells = cells;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
            this.ctrlLx = ctrlLx;
            this.ctrlLy = ctrlLy;
            this.ctrlLz = ctrlLz;
        }
    }

    /**
     * 把 {@code machineStack} 代表的多方块建进一个离屏伪世界，返回已建好结构的 renderer（供 GUI 3D 预览用，
     * 已调 {@code setRenderAllBlocks}）。失败返回 null。提取方块用 {@link #collect}。
     */
    public static ImmediateWorldSceneRenderer buildRenderer(ItemStack machineStack) {
        if (machineStack == null || machineStack.getItem() == null) return null;

        boolean threadWasEnabled = RunnableMachineUpdate.isCurrentThreadEnabled();
        if (threadWasEnabled) RunnableMachineUpdate.setCurrentThreadEnabled(false);

        try {
            ImmediateWorldSceneRenderer renderer = new ImmediateWorldSceneRenderer(new TrackedDummyWorld());
            TrackedDummyWorld world = (TrackedDummyWorld) renderer.world;
            BRUtil.FAKE_PLAYER.setWorld(world);

            ItemStack copy = machineStack.copy();
            copy.stackSize = Math.max(1, copy.stackSize);
            // 放下控制器
            copy.getItem()
                .onItemUse(copy, BRUtil.FAKE_PLAYER, world, PLACE_X, PLACE_Y, PLACE_Z, 0, PLACE_X, PLACE_Y, PLACE_Z);

            TileEntity tile = world.getTileEntity(PLACE_X, PLACE_Y, PLACE_Z);
            if (tile == null) return null;
            if (tile instanceof ITurnable turnable) {
                turnable.setFrontFacing(ForgeDirection.SOUTH);
            }

            IConstructable constructable = null;
            IMetaTileEntity mte = tile instanceof IGregTechTileEntity gt ? gt.getMetaTileEntity() : null;
            ItemStack trigger = new ItemStack(net.minecraft.init.Items.paper); // build-trigger 占位（tier 用默认）

            if (mte instanceof INEIPreviewModifier modifier) {
                modifier.onPreviewConstruct(trigger);
            }

            if (mte instanceof ISurvivalConstructable survival) {
                int iterations = 0;
                do {
                    survival.survivalConstruct(
                        trigger,
                        Integer.MAX_VALUE,
                        ISurvivalBuildEnvironment.create(CreativeItemSource.instance, BRUtil.FAKE_PLAYER));
                    iterations++;
                } while (world.hasChanged() && iterations < MAX_PLACE_ROUNDS);
            } else if (tile instanceof IConstructableProvider provider) {
                constructable = provider.getConstructable();
            } else if (tile instanceof IConstructable c) {
                constructable = c;
            }
            if (constructable != null) {
                constructable.construct(trigger, false);
            }
            if (mte instanceof INEIPreviewModifier modifier) {
                modifier.onPreviewStructureComplete(trigger);
            }

            renderer.setRenderAllBlocks();
            return renderer;
        } catch (Throwable t) {
            com.xyp.gtnc.ScienceNotCool.LOG.warn("[BuildingGenerator] structure build failed", t);
            return null;
        } finally {
            if (threadWasEnabled) RunnableMachineUpdate.setCurrentThreadEnabled(true);
        }
    }

    /**
     * 把 {@code machineStack} 代表的多方块建进伪世界并提取所有方块。失败（物品不是多方块 / 无法构建）返回 null。
     */
    public static Result extract(ItemStack machineStack) {
        ImmediateWorldSceneRenderer renderer = buildRenderer(machineStack);
        if (renderer == null) return null;
        return collect((TrackedDummyWorld) renderer.world);
    }

    /** 遍历伪世界方块，归一化到局部坐标。 */
    private static Result collect(TrackedDummyWorld world) {
        // 注意：不用 world.getMinPos()/getSize()。那两者由 setBlock 里的 Math.min/max 单调累积，
        // 方块被放下又清掉时不会回缩，会把包围盒撑大（→ 线框比实际大）。这里直接从最终 blockMap
        // 的真实方块算包围盒，保证「线框尺寸」与「实际放置的 cells」严格同源、绝不偏差。
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        // 先收集所有非空方块的绝对坐标（连同 block/meta/tile），同时求真实包围盒。
        List<int[]> raw = new ArrayList<>(); // {x,y,z}
        List<Block> blocks = new ArrayList<>();
        List<Integer> metas = new ArrayList<>();
        List<IGregTechTileEntity> tiles = new ArrayList<>();
        for (long packed : world.blockMap.keySet()) {
            int x = CoordinatePacker.unpackX(packed);
            int y = CoordinatePacker.unpackY(packed);
            int z = CoordinatePacker.unpackZ(packed);
            Block block = world.getBlock(x, y, z);
            if (block == null || block == Blocks.air) continue;
            int meta = world.getBlockMetadata(x, y, z);
            TileEntity te = world.getTileEntity(x, y, z);
            IGregTechTileEntity gt = te instanceof IGregTechTileEntity g ? g : null;
            raw.add(new int[] { x, y, z });
            blocks.add(block);
            metas.add(meta);
            tiles.add(gt);
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
        if (raw.isEmpty()) return null;

        int sx = maxX - minX + 1, sy = maxY - minY + 1, sz = maxZ - minZ + 1;
        List<Cell> cells = new ArrayList<>(raw.size());
        for (int i = 0; i < raw.size(); i++) {
            int[] p = raw.get(i);
            cells.add(new Cell(p[0] - minX, p[1] - minY, p[2] - minZ, blocks.get(i), metas.get(i), tiles.get(i)));
        }
        return new Result(cells, sx, sy, sz, PLACE_X - minX, PLACE_Y - minY, PLACE_Z - minZ);
    }
}
