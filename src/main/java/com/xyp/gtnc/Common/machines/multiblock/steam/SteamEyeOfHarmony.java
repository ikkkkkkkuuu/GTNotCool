package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.SteamEyeOfHarmonyGui;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.world.steam.SteamWirelessNetworkManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.TecTech;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.recipe.TecTechRecipeMaps;
import tectech.thing.block.TileEntityEyeOfHarmony;
import tectech.thing.casing.TTCasingsContainer;
import tectech.util.FluidStackLong;
import tectech.util.ItemStackLong;

// #tr NameSteamEyeOfHarmony
// # Steam Eye of Harmony
// # zh_CN 蒸汽鸿蒙之眼

// #tr SteamEyeOfHarmonyRecipeType
// # Steam EOH
// # zh_CN 蒸汽鸿蒙之眼

// #tr Tooltip_SteamEyeOfHarmony_00
// # A steam-powered Eye of Harmony that extracts ores from planetary blocks
// # zh_CN 从星球方块中提取矿石资源的蒸汽鸿蒙之眼

// #tr Tooltip_SteamEyeOfHarmony_01
// # Insert a planet block into the controller slot to begin
// # zh_CN 将星球方块放入控制器槽位以开始处理

// #tr Tooltip_SteamEyeOfHarmony_02
// # Uses wireless steam network. EU cost = Steam cost (1 EU = 1 L steam)
// # zh_CN 只能使用无线蒸汽网络。EU消耗 = 蒸汽消耗 (1 EU = 1 L蒸汽)

// #tr Tooltip_SteamEyeOfHarmony_03
// # Does not require hydrogen or helium
// # zh_CN 无需氢或氦

// #tr Tooltip_SteamEyeOfHarmony_04
// # Overclock with programmed circuits. Output is 1/500 of the original recipe
// # zh_CN 使用编程电路超频。产出为原配方的 1/500

// #tr Tooltip_SteamEyeOfHarmony_05
// # 100% success rate, no failure chance
// # zh_CN 100% 成功率，无失败几率

// #tr Tooltip_SteamEyeOfHarmony_06
// # Upgrade through the Celestial Path to gain parallel processing and steam discount
// # zh_CN 通过天途升级获得并行处理和蒸汽消耗减免

// #tr Tooltip_SteamEyeOfHarmony_07
// # Tier N gives N×2 parallel and N×2% steam discount (cumulative)
// # zh_CN 第N级提供 N×2 并行和 N×2% 蒸汽减免（累积叠加）

// #tr Tooltip_SteamEyeOfHarmony_Casing
// # Machine casing
// # zh_CN 机器外壳

public class SteamEyeOfHarmony extends GTNCSteamMultiBlockBase<SteamEyeOfHarmony> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 16;
    private static final int VERTICAL_OFF_SET = 16;
    private static final int DEPTH_OFF_SET = 0;

    /** Parallel per paid upgrade tier */
    private static final int PARALLEL_PER_TIER = 2;
    /** Steam discount per paid upgrade tier (0.02 = 2%) */
    private static final double STEAM_DISCOUNT_PER_TIER = 0.02;

    private IStructureDefinition<SteamEyeOfHarmony> STRUCTURE_DEFINITION = null;

    // Recipe processing fields
    private EyeOfHarmonyRecipe currentRecipe;
    private long currentCircuitMultiplier = 0;
    private long steamCost = 0;
    /** Full-precision steam consumed String for WAILA, avoids long overflow */
    private String steamConsumedStr = "0";
    private ArrayList<ItemStackLong> outputItems = new ArrayList<>();
    private ArrayList<FluidStackLong> outputFluids = new ArrayList<>();
    private FluidStackLong starMatter;
    private FluidStackLong stellarPlasma;

    /** Recipe check cooldown to avoid checking every tick */
    private long lagPreventer = 0;
    private static final long RECIPE_CHECK_INTERVAL = 3 * 20;

    public SteamEyeOfHarmony(String aName) {
        super(aName);
        // Always wireless mode
        this.wirelessMode = true;
    }

    public SteamEyeOfHarmony(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        this.wirelessMode = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamEyeOfHarmony(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("SteamEyeOfHarmonyRecipeType");
    }

    // ==================== Upgrade Tree (Cumulative) ====================

    /**
     * Cumulatively counts all paid upgrade tiers and applies bonuses.
     * Each tier: +2 parallel, +2% steam discount.
     * Overrides base class "take highest" logic with cumulative.
     */
    @Override
    public void onUpgradeComplete() {
        int cumulativeTier = 0;
        List<ItemStack> costs = getUpgradeCosts();
        for (int idx : paidUpgradeCostIndices) {
            if (idx >= costs.size()) continue;
            for (int i = 7; i >= 1; i--) {
                GTNCItemList chip = GTNCItemList.valueOf("ChipTier" + i);
                if (chip.hasBeenSet() && GTUtility.areStacksEqual(costs.get(idx), chip.get(1))) {
                    cumulativeTier += i;
                    break;
                }
            }
        }
        if (cumulativeTier > mUpgradeTier) {
            mUpgradeTier = cumulativeTier;
            mUpgraded = true;
            this.enableHigherRecipe = true;
        }
    }

    /** Number of paid upgrade tiers (distinct indices). */
    public int getPaidTierCount() {
        return paidUpgradeCostIndices.size();
    }

    /** Total parallel = 1 (base) + sum(tier * PARALLEL_PER_TIER) for each paid tier */
    public int getAstralParallel() {
        return 1 + mUpgradeTier * PARALLEL_PER_TIER;
    }

    /** Steam discount ratio: sum(tier) * STEAM_DISCOUNT_PER_TIER (clamped to [0, 1)) */
    public double getSteamDiscount() {
        return Math.min(mUpgradeTier * STEAM_DISCOUNT_PER_TIER, 0.99);
    }

    // ==================== Textures ====================

    private static IIconContainer ScreenOFF;
    private static IIconContainer ScreenON;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenOFF = Textures.BlockIcons.custom("iconsets/EM_BHG");
        ScreenON = Textures.BlockIcons.custom("iconsets/EM_BHG_ACTIVE");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return ScreenOFF;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return ScreenON;
    }

    // ==================== Structure ====================

    // 33x33x33 structure, same shape as original Eye of Harmony
    // Character mapping (all point to bronze steam casing for now):
    // C = Spatial Casing, D = Temporal Casing, A = Compression Field
    // E = Dilation Field, S = Stabilisation Field, H = Hatches, ~ = Controller
    // Currently all use the same bronze block; change element mappings to customize
    private static final String[][] RAW_SHAPE = new String[][] { { "                                 ",
        "                                 ", "                                 ", "                                 ",
        "                                 ", "                                 ", "                                 ",
        "                                 ", "                                 ", "                                 ",
        "                                 ", "                                 ", "               D D               ",
        "               D D               ", "               D D               ", "            DDDDDDDDD            ",
        "               D D               ", "            DDDDDDDDD            ", "               D D               ",
        "               D D               ", "               D D               ", "                                 ",
        "                                 ", "                                 ", "                                 ",
        "                                 ", "                                 ", "                                 ",
        "                                 ", "                                 ", "                                 ",
        "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "               D D               ", "               D D               ",
            "               D D               ", "               D D               ",
            "              FFFFF              ", "             FFDFDFF             ",
            "         DDDDFDDFDDFDDDD         ", "             FFFFFFF             ",
            "         DDDDFDDFDDFDDDD         ", "             FFDFDFF             ",
            "              FFFFF              ", "               D D               ",
            "               D D               ", "               D D               ",
            "               D D               ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "               D D               ", "               D D               ",
            "               D D               ", "                F                ",
            "                F                ", "             FFFFFFF             ",
            "            FF     FF            ", "            F  BBB  F            ",
            "       DDD  F BCCCB F  DDD       ", "          FFF BCCCB FFF          ",
            "       DDD  F BCCCB F  DDD       ", "            F  BBB  F            ",
            "            FF     FF            ", "             FFFFFFF             ",
            "                F                ", "                F                ",
            "               D D               ", "               D D               ",
            "               D D               ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "               D D               ",
            "               D D               ", "                F                ",
            "                F                ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "      DD                 DD      ", "        FF             FF        ",
            "      DD                 DD      ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                F                ", "                F                ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "               D D               ", "              DDDDD              ",
            "                F                ", "                C                ",
            "                C                ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "      D                   D      ",
            "     DD                   DD     ", "      DFCC             CCFD      ",
            "     DD                   DD     ", "      D                   D      ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                C                ", "                C                ",
            "                F                ", "              DDDDD              ",
            "               D D               ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "               D D               ",
            "               D D               ", "                F                ",
            "             EBBCBBE             ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "       E                 E       ", "       B                 B       ",
            "    DD B                 B DD    ", "      FC                 CF      ",
            "    DD B                 B DD    ", "       B                 B       ",
            "       E                 E       ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "             EBBCBBE             ", "                F                ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "               D D               ", "              DDDDD              ",
            "                F                ", "                C                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "    D                       D    ",
            "   DD                       DD   ", "    DFC                   CFD    ",
            "   DD                       DD   ", "    D                       D    ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                C                ",
            "                F                ", "              DDDDD              ",
            "               D D               ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "               D D               ",
            "               D D               ", "                F                ",
            "             EBBCBBE             ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "     E                     E     ", "     B                     B     ",
            "  DD B                     B DD  ", "    FC                     CF    ",
            "  DD B                     B DD  ", "     B                     B     ",
            "     E                     E     ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "             EBBCBBE             ", "                F                ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "               D D               ",
            "                F                ", "                C                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "  D                           D  ", "   FC                       CF   ",
            "  D                           D  ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                C                ",
            "                F                ", "               D D               ",
            "                                 ", "                                 " },
        { "                                 ", "               D D               ", "               D D               ",
            "                F                ", "                C                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            " DD                           DD ", "   FC                       CF   ",
            " DD                           DD ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                C                ",
            "                F                ", "               D D               ",
            "               D D               ", "                                 " },
        { "                                 ", "               D D               ", "                F                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            " D                             D ", "  F                           F  ",
            " D                             D ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                F                ",
            "               D D               ", "                                 " },
        { "                                 ", "               D D               ", "                F                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            " D                             D ", "  F                           F  ",
            " D                             D ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                F                ",
            "               D D               ", "                                 " },
        { "             DDDDDDD             ", "               D D               ", "             FFFFFFF             ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "  F                           F  ", "  F                           F  ",
            "DDF                           FDD", "  F                           F  ",
            "DDF                           FDD", "  F                           F  ",
            "  F                           F  ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "             FFFFFFF             ",
            "               D D               ", "               D D               " },
        { "            DDAAAAADD            ", "              FFFFF              ", "            FF     FF            ",
            "                                 ", "                                 ",
            "       E                 E       ", "                                 ",
            "     E                     E     ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "  F                           F  ",
            "  F                           F  ", " F                             F ",
            "DF                             FD", " F                             F ",
            "DF                             FD", " F                             F ",
            "  F                           F  ", "  F                           F  ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "     E                     E     ", "                                 ",
            "       E                 E       ", "                                 ",
            "                                 ", "            FF     FF            ",
            "              FFFFF              ", "               D D               " },
        { "            DAAAAAAAD            ", "             FFDFDFF             ", "            F  BBB  F            ",
            "                                 ", "      D                   D      ",
            "       B                 B       ", "    D                       D    ",
            "     B                     B     ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "  F                           F  ",
            " F                             F ", " F                             F ",
            "DDB                           BDD", " FB                           BF ",
            "DDB                           BDD", " F                             F ",
            " F                             F ", "  F                           F  ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "     B                     B     ", "    D                       D    ",
            "       B                 B       ", "      D                   D      ",
            "                                 ", "            F  BBB  F            ",
            "             FFDFDFF             ", "               D D               " },
        { "            DAADDDAAD            ", "         DDDDFDDFDDFDDDD         ", "       DDD  F BCCCB F  DDD       ",
            "      DD                 DD      ", "     DD                   DD     ",
            "    DD B                 B DD    ", "   DD                       DD   ",
            "  DD B                     B DD  ", "  D                           D  ",
            " DD                           DD ", " D                             D ",
            " D                             D ", "DDF                           FDD",
            "DF                             FD", "DDB                           BDD",
            "DDC                           CDD", "DFC                           CFD",
            "DDC                           CDD", "DDB                           BDD",
            "DF                             FD", "DDF                           FDD",
            " D                             D ", " D                             D ",
            " DD                           DD ", "  D                           D  ",
            "  DD B                     B DD  ", "   DD                       DD   ",
            "    DD B                 B DD    ", "     DD                   DD     ",
            "      DD                 DD      ", "       DDD  F BCCCB F  DDD       ",
            "         DDDDFDDFDDFDDDD         ", "            DDDDDDDDD            " },
        { "            DAAD~DAAD            ", "             FFFFFFF             ", "          FFF BCCCB FFF          ",
            "        FF             FF        ", "      DFCC             CCFD      ",
            "      FC                 CF      ", "    DFC                   CFD    ",
            "    FC                     CF    ", "   FC                       CF   ",
            "   FC                       CF   ", "  F                           F  ",
            "  F                           F  ", "  F                           F  ",
            " F                             F ", " FB                           BF ",
            "DFC                           CFD", " FC                           CF ",
            "DFC                           CFD", " FB                           BF ",
            " F                             F ", "  F                           F  ",
            "  F                           F  ", "  F                           F  ",
            "   FC                       CF   ", "   FC                       CF   ",
            "    FC                     CF    ", "    DFC                   CFD    ",
            "      FC                 CF      ", "      DFCC             CCFD      ",
            "        FF             FF        ", "          FFF BCCCB FFF          ",
            "             FFFFFFF             ", "               D D               " },
        { "            DAADDDAAD            ", "         DDDDFDDFDDFDDDD         ", "       DDD  F BCCCB F  DDD       ",
            "      DD                 DD      ", "     DD                   DD     ",
            "    DD B                 B DD    ", "   DD                       DD   ",
            "  DD B                     B DD  ", "  D                           D  ",
            " DD                           DD ", " D                             D ",
            " D                             D ", "DDF                           FDD",
            "DF                             FD", "DDB                           BDD",
            "DDC                           CDD", "DFC                           CFD",
            "DDC                           CDD", "DDB                           BDD",
            "DF                             FD", "DDF                           FDD",
            " D                             D ", " D                             D ",
            " DD                           DD ", "  D                           D  ",
            "  DD B                     B DD  ", "   DD                       DD   ",
            "    DD B                 B DD    ", "     DD                   DD     ",
            "      DD                 DD      ", "       DDD  F BCCCB F  DDD       ",
            "         DDDDFDDFDDFDDDD         ", "            DDDDDDDDD            " },
        { "            DAAAAAAAD            ", "             FFDFDFF             ", "            F  BBB  F            ",
            "                                 ", "      D                   D      ",
            "       B                 B       ", "    D                       D    ",
            "     B                     B     ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "  F                           F  ",
            " F                             F ", " F                             F ",
            "DDB                           BDD", " FB                           BF ",
            "DDB                           BDD", " F                             F ",
            " F                             F ", "  F                           F  ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "     B                     B     ", "    D                       D    ",
            "       B                 B       ", "      D                   D      ",
            "                                 ", "            F  BBB  F            ",
            "             FFDFDFF             ", "               D D               " },
        { "            DDAAAAADD            ", "              FFFFF              ", "            FF     FF            ",
            "                                 ", "                                 ",
            "       E                 E       ", "                                 ",
            "     E                     E     ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "  F                           F  ",
            "  F                           F  ", " F                             F ",
            "DF                             FD", " F                             F ",
            "DF                             FD", " F                             F ",
            "  F                           F  ", "  F                           F  ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "     E                     E     ", "                                 ",
            "       E                 E       ", "                                 ",
            "                                 ", "            FF     FF            ",
            "              FFFFF              ", "               D D               " },
        { "             DDDDDDD             ", "               D D               ", "             FFFFFFF             ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "  F                           F  ", "  F                           F  ",
            "DDF                           FDD", "  F                           F  ",
            "DDF                           FDD", "  F                           F  ",
            "  F                           F  ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "             FFFFFFF             ",
            "               D D               ", "               D D               " },
        { "                                 ", "               D D               ", "                F                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            " D                             D ", "  F                           F  ",
            " D                             D ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                F                ",
            "               D D               ", "                                 " },
        { "                                 ", "               D D               ", "                F                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            " D                             D ", "  F                           F  ",
            " D                             D ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                F                ",
            "               D D               ", "                                 " },
        { "                                 ", "               D D               ", "               D D               ",
            "                F                ", "                C                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            " DD                           DD ", "   FC                       CF   ",
            " DD                           DD ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                C                ",
            "                F                ", "               D D               ",
            "               D D               ", "                                 " },
        { "                                 ", "                                 ", "               D D               ",
            "                F                ", "                C                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "  D                           D  ", "   FC                       CF   ",
            "  D                           D  ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                C                ",
            "                F                ", "               D D               ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "               D D               ",
            "               D D               ", "                F                ",
            "             EBBCBBE             ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "     E                     E     ", "     B                     B     ",
            "  DD B                     B DD  ", "    FC                     CF    ",
            "  DD B                     B DD  ", "     B                     B     ",
            "     E                     E     ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "             EBBCBBE             ", "                F                ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "               D D               ", "              DDDDD              ",
            "                F                ", "                C                ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "    D                       D    ",
            "   DD                       DD   ", "    DFC                   CFD    ",
            "   DD                       DD   ", "    D                       D    ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                C                ",
            "                F                ", "              DDDDD              ",
            "               D D               ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "               D D               ",
            "               D D               ", "                F                ",
            "             EBBCBBE             ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "       E                 E       ", "       B                 B       ",
            "    DD B                 B DD    ", "      FC                 CF      ",
            "    DD B                 B DD    ", "       B                 B       ",
            "       E                 E       ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "             EBBCBBE             ", "                F                ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "               D D               ", "              DDDDD              ",
            "                F                ", "                C                ",
            "                C                ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "      D                   D      ",
            "     DD                   DD     ", "      DFCC             CCFD      ",
            "     DD                   DD     ", "      D                   D      ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                C                ", "                C                ",
            "                F                ", "              DDDDD              ",
            "               D D               ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "               D D               ",
            "               D D               ", "                F                ",
            "                F                ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "      DD                 DD      ", "        FF             FF        ",
            "      DD                 DD      ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                F                ", "                F                ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "               D D               ", "               D D               ",
            "               D D               ", "                F                ",
            "                F                ", "             FFFFFFF             ",
            "            FF     FF            ", "            F  BBB  F            ",
            "       DDD  F BCCCB F  DDD       ", "          FFF BCCCB FFF          ",
            "       DDD  F BCCCB F  DDD       ", "            F  BBB  F            ",
            "            FF     FF            ", "             FFFFFFF             ",
            "                F                ", "                F                ",
            "               D D               ", "               D D               ",
            "               D D               ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "               D D               ", "               D D               ",
            "               D D               ", "               D D               ",
            "              FFFFF              ", "             FFDFDFF             ",
            "         DDDDFDDFDDFDDDD         ", "             FFFFFFF             ",
            "         DDDDFDDFDDFDDDD         ", "             FFDFDFF             ",
            "              FFFFF              ", "               D D               ",
            "               D D               ", "               D D               ",
            "               D D               ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " },
        { "                                 ", "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "               D D               ",
            "               D D               ", "               D D               ",
            "            DDDDDDDDD            ", "               D D               ",
            "            DDDDDDDDD            ", "               D D               ",
            "               D D               ", "               D D               ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 ",
            "                                 ", "                                 " } };

    @Override
    public IStructureDefinition<SteamEyeOfHarmony> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<SteamEyeOfHarmony>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(RAW_SHAPE))
                .addElement(
                    'A',
                    ofChain(
                        buildSteamInput(SteamEyeOfHarmony.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(SteamEyeOfHarmony.class).atLeast(InputBus, OutputBus, OutputHatch)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(),
                        ofBlock(sBlockCasings1, 10)))
                .addElement('B', ofBlock(Blocks.lapis_block, 0))
                .addElement('C', ofBlock(Blocks.bookshelf, 0))
                .addElement('D', ofBlock(Blocks.brick_block, 0))
                .addElement('E', ofBlock(Blocks.stonebrick, 3))
                .addElement('F', ofBlockAnyMeta(Blocks.planks))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
    }

    // ==================== Always wireless ====================

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        // Ensure wireless mode is always on
        this.wirelessMode = true;
    }

    // ==================== Recipe Processing ====================

    @Override
    public RecipeMap<?> getRecipeMap() {
        return TecTechRecipeMaps.eyeOfHarmonyRecipes;
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        // Ensure wireless mode
        this.wirelessMode = true;

        ItemStack controllerStack = getControllerSlot();
        if (controllerStack == null) {
            // #tr GT5U.gui.text.recipe_result.no_planet_block
            // # Insert a Planet Block into the controller slot
            // # zh_CN 请将星球方块放入控制器槽位
            return SimpleCheckRecipeResult.ofFailure("no_planet_block");
        }

        lagPreventer++;
        if (lagPreventer < RECIPE_CHECK_INTERVAL) {
            lagPreventer = 0;

            currentRecipe = TecTech.eyeOfHarmonyRecipeStorage.recipeLookUp(controllerStack);
            if (currentRecipe == null) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }
            return processRecipe(currentRecipe);
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    private CheckRecipeResult processRecipe(EyeOfHarmonyRecipe recipeObject) {
        // Read circuit for overclocking
        currentCircuitMultiplier = 0;
        for (MTEHatchInputBus inputBus : GTUtility.validMTEList(mInputBusses)) {
            for (ItemStack itemStack : inputBus.getRealInventory()) {
                if (GTUtility.isAnyIntegratedCircuit(itemStack)) {
                    currentCircuitMultiplier = MathHelper.clamp_int(itemStack.getItemDamage(), 0, 24);
                    break;
                }
            }
            if (currentCircuitMultiplier > 0) break;
        }

        // Steam cost = EU start cost * 4^circuit (same as original EU formula)
        // 1 EU = 1 L steam
        long startEU = recipeObject.getEUStartCost();
        BigInteger steamNeeded = BigInteger.valueOf(startEU)
            .multiply(BigInteger.valueOf((long) Math.pow(4, currentCircuitMultiplier)));

        // Base steam cost is 1% of the recipe cost (round up so it never drops to 0)
        steamNeeded = steamNeeded.add(BigInteger.valueOf(99))
            .divide(BigInteger.valueOf(100));

        // Apply astral array steam discount (cumulative from upgrade tree)
        double discount = getSteamDiscount();
        if (discount > 0) {
            // Use pure BigInteger arithmetic to avoid double/long overflow
            long discountBasis = (long) (discount * 10000);
            steamNeeded = steamNeeded.multiply(BigInteger.valueOf(10000 - discountBasis))
                .divide(BigInteger.valueOf(10000));
        }

        // Check and consume steam from wireless network
        if (ownerUUID == null) {
            // #tr GT5U.gui.text.recipe_result.no_owner
            // # No owner assigned
            // # zh_CN 无所有者
            return SimpleCheckRecipeResult.ofFailure("no_owner");
        }

        BigInteger networkSteam = SteamWirelessNetworkManager.getUserSteam(ownerUUID);
        // #tr GT5U.gui.text.recipe_result.insufficient_steam
        // # Insufficient steam in wireless network
        // # zh_CN 无线蒸汽网络蒸汽不足
        if (networkSteam.compareTo(steamNeeded) < 0) {
            return SimpleCheckRecipeResult.ofFailure("insufficient_steam");
        }

        if (!SteamWirelessNetworkManager.addSteamToGlobalSteamMap(ownerUUID, steamNeeded.negate())) {
            return SimpleCheckRecipeResult.ofFailure("insufficient_steam");
        }

        // Store full steam cost string for WAILA display
        this.steamConsumedStr = steamNeeded.toString();
        this.steamCost = steamNeeded.longValue();
        this.totalSteamConsumed = steamCost;

        // Time calculation: recipeTimeInTicks / 2^circuit (same overclock as original)
        long recipeTime = recipeObject.getRecipeTimeInTicks();
        this.mMaxProgresstime = (int) Math.max(1, recipeTime / Math.max(1, 1L << currentCircuitMultiplier));

        // Get recipe outputs (deep copies)
        outputFluids = recipeObject.getOutputFluids();
        outputItems = recipeObject.getOutputItems();

        // Apply 1/500 output reduction (base steam penalty)
        long outputDivisor = 500;
        for (ItemStackLong itemStackLong : outputItems) {
            itemStackLong.stackSize = Math.max(1, itemStackLong.stackSize / outputDivisor);
        }
        for (FluidStackLong fluidStackLong : outputFluids) {
            fluidStackLong.amount = Math.max(1, fluidStackLong.amount / outputDivisor);
        }

        // Apply astral array parallel multiplier (cumulative from upgrade tree)
        int parallel = getAstralParallel();
        if (parallel > 1) {
            for (ItemStackLong itemStackLong : outputItems) {
                itemStackLong.stackSize *= parallel;
            }
            for (FluidStackLong fluidStackLong : outputFluids) {
                fluidStackLong.amount *= parallel;
            }
        }

        // Star matter is always the last element, stellar plasma second last
        if (outputFluids.size() >= 2) {
            starMatter = new FluidStackLong(outputFluids.get(outputFluids.size() - 1));
            stellarPlasma = new FluidStackLong(outputFluids.get(outputFluids.size() - 2));
        }

        this.lEUt = 0; // No EU to output, all steam consumed upfront
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;

        updateSlots();
        createRenderBlock(currentRecipe);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    protected void outputAfterRecipe() {
        destroyRenderBlock();
        // Output items via ME output buses
        outputItemToAENetwork(outputItems);

        // Output fluids via output hatches
        outputFluidToAENetwork(outputFluids);

        // Clear output lists
        outputItems = new ArrayList<>();
        outputFluids = new ArrayList<>();
        starMatter = null;
        stellarPlasma = null;
        currentRecipe = null;

        super.outputAfterRecipe();
    }

    @Override
    public void stopMachine(@javax.annotation.Nonnull ShutDownReason reason) {
        destroyRenderBlock();
        super.stopMachine(reason);
        currentRecipe = null;
        outputItems = new ArrayList<>();
        outputFluids = new ArrayList<>();
    }

    // ==================== Render Block ====================

    private void createRenderBlock(final EyeOfHarmonyRecipe currentRecipe) {
        IGregTechTileEntity gtTE = this.getBaseMetaTileEntity();
        int x = gtTE.getXCoord();
        int y = gtTE.getYCoord();
        int z = gtTE.getZCoord();
        ForgeDirection back = gtTE.getFrontFacing()
            .getOpposite();
        int xOffset = 16 * back.offsetX;
        int yOffset = 16 * back.offsetY;
        int zOffset = 16 * back.offsetZ;
        gtTE.getWorld()
            .setBlock(x + xOffset, y + yOffset, z + zOffset, Blocks.air);
        gtTE.getWorld()
            .setBlock(x + xOffset, y + yOffset, z + zOffset, TTCasingsContainer.eyeOfHarmonyRenderBlock);
        TileEntityEyeOfHarmony renderTile = (TileEntityEyeOfHarmony) gtTE.getWorld()
            .getTileEntity(x + xOffset, y + yOffset, z + zOffset);
        if (renderTile != null) {
            renderTile.setTier(currentRecipe.getRocketTier());
            renderTile.setStarSize(0.4 + currentRecipe.getSpacetimeCasingTierRequired() / 8.0);
        }
    }

    private void destroyRenderBlock() {
        IGregTechTileEntity gtTE = this.getBaseMetaTileEntity();
        int x = gtTE.getXCoord();
        int y = gtTE.getYCoord();
        int z = gtTE.getZCoord();
        ForgeDirection back = gtTE.getFrontFacing()
            .getOpposite();
        int xOffset = 16 * back.offsetX;
        int yOffset = 16 * back.offsetY;
        int zOffset = 16 * back.offsetZ;
        gtTE.getWorld()
            .setBlock(x + xOffset, y + yOffset, z + zOffset, Blocks.air);
    }

    // ==================== Item/Fluid Output Helpers ====================

    private void outputItemToAENetwork(List<ItemStackLong> items) {
        if (items == null || items.isEmpty()) return;

        gregtech.api.util.ItemEjectionHelper ejectionHelper = new gregtech.api.util.ItemEjectionHelper(this);

        for (ItemStackLong itemStackLong : items) {
            if (itemStackLong.itemStack == null || itemStackLong.stackSize <= 0) continue;

            for (long i = 0; i < itemStackLong.stackSize; i += Integer.MAX_VALUE) {
                int xfer = (int) Math.min(itemStackLong.stackSize - i, Integer.MAX_VALUE);
                ejectionHelper.ejectStack(GTUtility.copyAmountUnsafe(xfer, itemStackLong.itemStack));
            }
        }
        ejectionHelper.commit();
    }

    private void outputFluidToAENetwork(List<FluidStackLong> fluids) {
        if (fluids == null || fluids.isEmpty()) return;

        for (FluidStackLong fluidStackLong : fluids) {
            if (fluidStackLong.fluidStack == null || fluidStackLong.amount <= 0) continue;

            long amount = fluidStackLong.amount;
            while (amount >= Integer.MAX_VALUE) {
                net.minecraftforge.fluids.FluidStack tmpFluid = fluidStackLong.fluidStack.copy();
                tmpFluid.amount = Integer.MAX_VALUE;
                dumpFluid(mOutputHatches, tmpFluid, false);
                amount -= Integer.MAX_VALUE;
            }
            net.minecraftforge.fluids.FluidStack tmpFluid = fluidStackLong.fluidStack.copy();
            tmpFluid.amount = (int) amount;
            dumpFluid(mOutputHatches, tmpFluid, false);
        }
    }

    // ==================== NBT Persistence ====================

    private static final String EOH_STEAM = "steamEyeOfHarmony";
    private static final String STEAM_COST_NBT = EOH_STEAM + "steamCost";
    private static final String CIRCUIT_MULTIPLIER_NBT = EOH_STEAM + "circuitMultiplier";

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong(STEAM_COST_NBT, steamCost);
        aNBT.setLong(CIRCUIT_MULTIPLIER_NBT, currentCircuitMultiplier);
        aNBT.setString("steamConsumedStr", steamConsumedStr);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        steamCost = aNBT.getLong(STEAM_COST_NBT);
        currentCircuitMultiplier = aNBT.getLong(CIRCUIT_MULTIPLIER_NBT);
        if (aNBT.hasKey("steamConsumedStr")) {
            steamConsumedStr = aNBT.getString("steamConsumedStr");
        }
    }

    // ==================== Tooltip ====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamEyeOfHarmonyRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_07"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .beginStructureBlock(33, 33, 33, false)
            .addController("Front center")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_SteamEyeOfHarmony_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ==================== Info Data ====================

    @Override
    public String[] getInfoData() {
        String[] superInfo = super.getInfoData();
        int paidCount = getPaidTierCount();
        int extraLines = paidCount > 0 ? 5 : 2;
        String[] info = new String[superInfo.length + extraLines];
        System.arraycopy(superInfo, 0, info, 0, superInfo.length);

        info[superInfo.length] = "Circuit OC: " + currentCircuitMultiplier;
        info[superInfo.length + 1] = "Steam used: " + steamConsumedStr + " L";
        if (paidCount > 0) {
            info[superInfo.length + 2] = "Astral Tier: " + paidCount
                + " (Parallel: "
                + getAstralParallel()
                + ", Steam: -"
                + String.format("%.0f", getSteamDiscount() * 100)
                + "%)";
            info[superInfo.length + 3] = "Effective output multiplier: x" + getAstralParallel();
            if (currentRecipe != null) {
                info[superInfo.length + 4] = "Recipe Tier: " + currentRecipe.getRocketTier();
            } else {
                info[superInfo.length + 4] = "Idle";
            }
        } else {
            if (currentRecipe != null) {
                info[superInfo.length + 2] = "Recipe Tier: " + currentRecipe.getRocketTier();
            } else {
                info[superInfo.length + 2] = "Idle";
            }
        }

        return info;
    }

    // ==================== WAILA ====================
    @Override
    protected boolean showWailaExtraInfo() {
        return false;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setBoolean("wirelessMode", true);
        if (ownerUUID != null) {
            tag.setString(
                "networkSteam",
                SteamWirelessNetworkManager.getUserSteam(ownerUUID)
                    .toString());
        }
        // Store full steam cost as String to avoid long overflow for EOH-scale values
        tag.setString("steamConsumedStr", steamConsumedStr);
        tag.setInteger("astralParallel", getAstralParallel());
        tag.setDouble("steamDiscount", getSteamDiscount());
    }

    // #tr WAILA_SteamEOH_Parallel
    // # Parallel
    // # zh_CN 并行

    // #tr WAILA_SteamEOH_SteamDiscount
    // # Steam Discount
    // # zh_CN 蒸汽减免

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        // Skip base class steam WAILA (uses tag.getLong("steamConsumed") which overflows for EOH)
        // Replicate wireless + network steam display, then add full-precision steam consumed + parallel + discount
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessMode")) {
            currenttip.add(
                StatCollector.translateToLocal("GT5U.turbine.wireless_mode") + ": "
                    + EnumChatFormatting.GREEN
                    + "ON"
                    + EnumChatFormatting.RESET);
            currenttip.add(
                StatCollector.translateToLocal("GTNC.info.wireless_steam") + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("networkSteam")
                    + EnumChatFormatting.RESET
                    + " L");
        }
        currenttip.add(
            StatCollector.translateToLocal("GTNC.info.steam_consumed") + ": "
                + EnumChatFormatting.AQUA
                + tag.getString("steamConsumedStr")
                + EnumChatFormatting.RESET
                + " L");
        int parallel = tag.getInteger("astralParallel");
        double discount = tag.getDouble("steamDiscount");
        currenttip.add(
            StatCollector.translateToLocal("WAILA_SteamEOH_Parallel") + ": "
                + EnumChatFormatting.BLUE
                + parallel
                + EnumChatFormatting.RESET);
        currenttip.add(
            StatCollector.translateToLocal("WAILA_SteamEOH_SteamDiscount") + ": "
                + EnumChatFormatting.GREEN
                + "-"
                + String.format("%.0f", discount * 100)
                + "%"
                + EnumChatFormatting.RESET);
    }

    // ==================== Sound ====================

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_INDUCTION_LOOP;
    }

    // ==================== Disable unnecessary features ====================

    @Override
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new SteamEyeOfHarmonyGui(this);
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getAstralParallel();
    }

}
