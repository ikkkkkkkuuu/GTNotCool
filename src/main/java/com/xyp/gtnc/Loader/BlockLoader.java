package com.xyp.gtnc.Loader;

import net.minecraft.util.StatCollector;

import com.xyp.gtnc.Common.blocks.casings.base.ItemBlockBase;
import com.xyp.gtnc.Common.blocks.casings.base.MetaBlockBase;
import com.xyp.gtnc.Common.blocks.casings.casing.MetaCasing;
import com.xyp.gtnc.Common.blocks.casings.casing.MetaItemBlockCasing;
import com.xyp.gtnc.Common.blocks.casings.column.ItemBlockColumn;
import com.xyp.gtnc.Common.blocks.casings.column.MetaBlockColumn;
import com.xyp.gtnc.Common.blocks.casings.glass.ItemBlockGlass;
import com.xyp.gtnc.Common.blocks.casings.glass.MetaBlockGlass;
import com.xyp.gtnc.Common.blocks.casings.glow.ItemBlockGlow;
import com.xyp.gtnc.Common.blocks.casings.glow.MetaBlockGlow;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 方块注册中心 —— 所有自定义方块的创建和注册入口。
 *
 * <h2>注册流程</h2>
 * 采用 GT-Not-Leisure 的 MetaItemBlockCasing 模式：
 * <ol>
 * <li>创建 {@link MetaCasing} 实例（构造函数内自动调用 {@code GregTechAPI.registerMachineBlock}）</li>
 * <li>通过 {@code GameRegistry.registerBlock} 注册到 Forge</li>
 * <li>通过 {@link MetaItemBlockCasing#initMetaBlockCasing} 初始化每个 meta 变体
 * （内部调用 {@code Textures.BlockIcons.setCasingTextureForId} 绑定 GT5 纹理）</li>
 * </ol>
 *
 * <h2>添加新方块类型的步骤</h2>
 *
 * <pre>
 * // 1. 创建新 MetaCasing 实例
 * public static MetaCasing newCasings;
 *
 * // 2. 在 registerBlocks() 中注册
 * newCasings = new MetaCasing("blockNewCasings", (byte) 0);
 * GameRegistry.registerBlock(newCasings, MetaItemBlockCasing.class, newCasings.getUnlocalizedName());
 *
 * // 3. 在 registerBlockContainers() 中初始化 meta 变体
 * GTNCItemList.SomeBlock.set(MetaItemBlockCasing.initMetaBlockCasing(0, newCasings));
 *
 * // 4. 放入纹理文件到
 * // assets/sciencenotcool/textures/blocks/blockNewCasings/{meta}.png
 * </pre>
 *
 * <h2>当前注册的方块</h2>
 * <table>
 * <tr>
 * <th>变量名</th>
 * <th>类型</th>
 * <th>Meta 范围</th>
 * <th>用途</th>
 * </tr>
 * <tr>
 * <td>metaCasing02</td>
 * <td>MetaCasing</td>
 * <td>4~19</td>
 * <td>装配矩阵 + 量子计算机外壳</td>
 * </tr>
 * <tr>
 * <td>metaBlock</td>
 * <td>MetaBlockBase</td>
 * <td>—</td>
 * <td>通用Meta方块（预留）</td>
 * </tr>
 * <tr>
 * <td>metaBlockGlow</td>
 * <td>MetaBlockGlow</td>
 * <td>—</td>
 * <td>发光方块（预留）</td>
 * </tr>
 * <tr>
 * <td>metaBlockGlass</td>
 * <td>MetaBlockGlass</td>
 * <td>—</td>
 * <td>玻璃方块（预留）</td>
 * </tr>
 * <tr>
 * <td>metaBlockColumn</td>
 * <td>MetaBlockColumn</td>
 * <td>—</td>
 * <td>柱形方块（预留）</td>
 * </tr>
 * </table>
 */
public class BlockLoader {

    public static MetaBlockBase metaBlock = new MetaBlockBase("MetaBlock");
    public static MetaBlockGlow metaBlockGlow = new MetaBlockGlow("MetaBlockGlow");
    public static MetaBlockGlass metaBlockGlass = new MetaBlockGlass("MetaBlockGlass");
    public static MetaBlockColumn metaBlockColumn = new MetaBlockColumn("MetaBlockColumn");

    /** 装配矩阵 & 量子计算机 外壳方块 (meta 4-19), textureIdOffsite=32 对应 GT5 纹理偏移 */
    public static MetaCasing metaCasing02 = new MetaCasing("MetaCasing02", (byte) 32);

    /**
     * 注册所有方块到 Forge GameRegistry。
     */
    public static void registerBlocks() {
        // ---- 通用 Meta 方块（预留） ----
        GameRegistry.registerBlock(metaBlock, ItemBlockBase.class, metaBlock.getUnlocalizedName());
        GameRegistry.registerBlock(metaBlockGlow, ItemBlockGlow.class, metaBlockGlow.getUnlocalizedName());
        GameRegistry.registerBlock(metaBlockGlass, ItemBlockGlass.class, metaBlockGlass.getUnlocalizedName());
        GameRegistry.registerBlock(metaBlockColumn, ItemBlockColumn.class, metaBlockColumn.getUnlocalizedName());

        // ---- GT5 外壳方块（在构造函数中已调用 GregTechAPI.registerMachineBlock） ----
        GameRegistry.registerBlock(metaCasing02, MetaItemBlockCasing.class, metaCasing02.getUnlocalizedName());
    }

    /**
     * 初始化 meta 变体容器 —— 为每个 meta 值创建 ItemStack、绑定 GT5 纹理、添加 tooltip。
     * 仿照 GT-Not-Leisure 的 {@code BlockLoader.registryBlockContainers()}。
     */
    public static void registerBlockContainers() {

        // #tr tile.MetaCasing02.4.name
        // # Mineral processing framework
        // # zh_CN 矿物处理外壳
        GTNCItemList.MineralprocessingFrame.set(MetaItemBlockCasing.initMetaBlockCasing(4, metaCasing02));

        // #tr tile.MetaCasing02.5.name
        // # Assembler Matrix Wall
        // # zh_CN 装配矩阵墙壁
        GTNCItemList.AssemblerMatrixWall.set(MetaItemBlockCasing.initMetaBlockCasing(5, metaCasing02));
        // #tr tile.MetaCasing02.6.name
        // # Assembler Matrix Pattern Core
        // # zh_CN 装配矩阵样板核心
        // #tr Tooltip_AssemblerMatrixPatternCore_00
        // # Each provides 72 pattern slot processes
        // # zh_CN 每个提供72样板槽
        GTNCItemList.AssemblerMatrixPatternCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                6,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixPatternCore_00") }));
        // #tr tile.MetaCasing02.7.name
        // # Assembler Matrix Crafter Core
        // # zh_CN 装配矩阵合成核心
        // #tr Tooltip_AssemblerMatrixCrafterCore_00
        // # Each provides 2,0480 parallel processes
        // # zh_CN 每个提供2,0480并行
        GTNCItemList.AssemblerMatrixCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                7,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixCrafterCore_00") }));
        // #tr tile.MetaCasing02.8.name
        // # Assembler Matrix Singularity Crafter Core
        // # zh_CN 装配矩阵奇点合成核心
        // #tr Tooltip_AssemblerMatrixSingularityCrafterCore_00
        // # Each provides 2,147,483,647 parallel processes
        // # zh_CN 每个提供2,147,483,647并行
        GTNCItemList.AssemblerMatrixSingularityCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                8,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixSingularityCrafterCore_00") }));
        // #tr tile.MetaCasing02.9.name
        // # Assembler Matrix Speed Core
        // # zh_CN 装配矩阵速度核心
        // #tr Tooltip_AssemblerMatrixSpeedCore_00
        // # Each installed core halves the operation time
        // # zh_CN 每安装一个时间减半
        GTNCItemList.AssemblerMatrixSpeedCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                9,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixSpeedCore_00") }));

        // ══════════════════════════════════════════════════════════════
        // MetaCasing02: 量子计算机 (QuantumComputer) meta 10~19
        // ══════════════════════════════════════════════════════════════

        // #tr tile.MetaCasing02.10.name
        // # Quantum Computer Casing
        // # zh_CN 量子计算机外壳
        // #tr Tooltip_QuantumComputerCasing_00
        // # Used in the outside layer of the Quantum Computer Multiblock. Maximum multiblock size is %sx%sx%s.
        // # zh_CN 用于搭建量子计算机的最外层。最大尺寸为 %sx%sx%s。
        GTNCItemList.QuantumComputerCasing.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                10,
                metaCasing02,
                new String[] { StatCollector.translateToLocalFormatted("Tooltip_QuantumComputerCasing_00", 7, 7, 7) }));
        // #tr tile.MetaCasing02.11.name
        // # Quantum Crafting Unit
        // # zh_CN 量子合成单元
        GTNCItemList.QuantumComputerUnit.set(MetaItemBlockCasing.initMetaBlockCasing(11, metaCasing02));
        // #tr tile.MetaCasing02.12.name
        // # Quantum Computer Crafting Storage 128M
        // # zh_CN 量子计算机 128M 合成存储
        GTNCItemList.QuantumComputerCraftingStorage128M.set(MetaItemBlockCasing.initMetaBlockCasing(12, metaCasing02));
        // #tr tile.MetaCasing02.13.name
        // # Quantum Computer Crafting Storage 256M
        // # zh_CN 量子计算机 256M 合成存储
        GTNCItemList.QuantumComputerCraftingStorage256M.set(MetaItemBlockCasing.initMetaBlockCasing(13, metaCasing02));
        // #tr tile.MetaCasing02.14.name
        // # Quantum Data Entangler
        // # zh_CN 量子数据纠缠器
        // #tr Tooltip_QuantumComputerDataEntangler_00
        // # Multiplies the total storage in the Quantum Computer Multiblock by 4. Limited to %d per multiblock
        // # zh_CN 量子计算机的合成存储容量变为 4 倍。每个量子计算机中最多放置 %s 个。
        GTNCItemList.QuantumComputerDataEntangler.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                14,
                metaCasing02,
                new String[] {
                    StatCollector.translateToLocalFormatted("Tooltip_QuantumComputerDataEntangler_00", 1) }));
        // #tr tile.MetaCasing02.15.name
        // # Quantum Computer Accelerator
        // # zh_CN 量子计算机加速器
        // #tr Tooltip_QuantumComputerAccelerator_00
        // # Provides 1638400 co-processing threads per block.
        // # zh_CN 每个加速器提供 1638400 个并行处理线程。
        GTNCItemList.QuantumComputerAccelerator.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                15,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputerAccelerator_00") }));
        // #tr tile.MetaCasing02.16.name
        // # Quantum Computer Multi-Threader
        // # zh_CN 量子计算机多线程处理器
        // #tr Tooltip_QuantumComputerMultiThreader_00
        // # Multiplies the amount of co-processors in the Quantum Computer Multiblock by 4. Limited to %s per
        // # zh_CN 量子计算机的并行线程数变为 4 倍。每个量子计算机中最多放置 %s 个。
        GTNCItemList.QuantumComputerMultiThreader.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                16,
                metaCasing02,
                new String[] {
                    StatCollector.translateToLocalFormatted("Tooltip_QuantumComputerMultiThreader_00", 1) }));
        // #tr tile.MetaCasing02.17.name
        // # Quantum Computer Core
        // # zh_CN 量子计算机核心
        // #tr Tooltip_QuantumComputerCore_00
        // # Provides 2.56G crafting storage and 1638400 co-processing threads.
        // # zh_CN 提供 2.56G 合成存储空间和 1638400 并行处理线程。
        GTNCItemList.QuantumComputerCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                17,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputerCore_00") }));
        // #tr tile.MetaCasing02.18.name
        // # Assembler Matrix Debug Crafter Core
        // # zh_CN 装配矩阵Debug合成核心
        // #tr Tooltip_AssemblerMatrixDebugCrafterCore_00
        // # Each provides 9,223,372,036,854,775,807 parallel processes
        // # zh_CN 每个提供9,223,372,036,854,775,807并行
        GTNCItemList.AssemblerMatrixDebugCrafterCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                18,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_AssemblerMatrixDebugCrafterCore_00") }));
        // #tr tile.MetaCasing02.19.name
        // # Quantum Computer Singularity Core
        // # zh_CN 量子计算机奇点核心
        // #tr Tooltip_QuantumComputerSingularityCore_00
        // # Locks the total storage of the Quantum Computer Multiblock at 9.22E
        // # zh_CN 将量子计算机的合成存储容量锁定为9.22E
        GTNCItemList.QuantumComputerSingularityCore.set(
            MetaItemBlockCasing.initMetaBlockCasing(
                19,
                metaCasing02,
                new String[] { StatCollector.translateToLocal("Tooltip_QuantumComputerSingularityCore_00") }));

        // #tr tile.MetaBlockGlow.31.name
        // # Super Space Elevator Glow Block
        // # zh_CN 超级太空电梯发光方块
        GTNCItemList.WhiteLamp.set(ItemBlockGlow.initMetaBlockGlow(31));
    }

    /**
     * 对外暴露的注册入口，由 {@code CommonProxy.preInit()} 调用。
     */
    public static void registry() {
        registerBlocks();
        registerBlockContainers();
    }
}
