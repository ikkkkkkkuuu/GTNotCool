package com.silvermoon.boxplusplus.common.tileentities;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.silvermoon.boxplusplus.boxplusplus.LOG;
import static com.silvermoon.boxplusplus.common.BoxModule.getModuleByIndex;
import static com.silvermoon.boxplusplus.common.BoxModule.transMachinesToModule;
import static com.silvermoon.boxplusplus.util.Util.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.silvermoon.boxplusplus.common.gui.BoxMachineGui;
import com.silvermoon.boxplusplus.common.loader.BlockRegister;
import com.silvermoon.boxplusplus.util.BoxRecipe;
import com.silvermoon.boxplusplus.util.BoxRoutings;
import com.silvermoon.boxplusplus.util.FluidContainer;
import com.silvermoon.boxplusplus.util.ItemContainer;
import com.silvermoon.boxplusplus.util.ResultModuleRequirement;
import com.silvermoon.boxplusplus.util.Util;
import com.xyp.gtnc.utils.structure.GTNCStructureErrors;

import appeng.api.AEApi;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.modularui2.ProxiedMteGui;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.client.iconContainers.blocks.GTBlockIconContainer;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class GTMachineBox extends MTEExtendedPowerMultiBlockBase<GTMachineBox> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MainFrames = "Mainframes";
    private static final String STRUCTURE_PIECE_FirstRing = "FirstRing";
    private static final String STRUCTURE_PIECE_SecondRing = "SecondRing";
    private static final String STRUCTURE_PIECE_Final = "Final";
    private static final IIconContainer boxActive = GTBlockIconContainer.create("EM_COLLIDER_ACTIVE");
    private static final IIconContainer boxInactive = GTBlockIconContainer.create("EM_COLLIDER");
    // #tr tile.boxplusplus.boxUI.clear_current_page
    // # Are you sure you want to clear the recipe and processes on the current page?
    // # zh_CN 确定要清除当前页的配方和工序吗？
    private static final String CLEAR_CURRENT_PAGE_KEY = "tile.boxplusplus.boxUI.clear_current_page";
    // #tr tile.boxplusplus.boxUI.clear_current_page_final
    // # Are you absolutely sure? Only the current recipe page will be cleared.
    // # zh_CN 再次确认：只会清除当前配方页，其他页面不会受到影响。
    private static final String CLEAR_CURRENT_PAGE_FINAL_KEY = "tile.boxplusplus.boxUI.clear_current_page_final";
    // #tr tile.boxplusplus.boxUI.42
    // # Are you sure you want to clear all recipes and processes?
    // # zh_CN 你确定要删除全部配方和工序吗？
    private static final String CLEAR_CONFIRM_KEY = "tile.boxplusplus.boxUI.42";
    // #tr tile.boxplusplus.boxUI.43
    // # Are you absolutely sure? This is the final confirmation.
    // # zh_CN 你真的确定要删除全部配方和工序吗？请注意，这是最后一次提示！
    private static final String CLEAR_FINAL_CONFIRM_KEY = "tile.boxplusplus.boxUI.43";
    // #tr tile.boxplusplus.boxUI.44
    // # If confirmed, press
    // # zh_CN 如果确认，请按
    private static final String CLEAR_NUMBER_PROMPT_KEY = "tile.boxplusplus.boxUI.44";
    // #tr tile.boxplusplus.boxUI.mui.module.enabled
    // # enabled
    // # zh_CN 已启用
    private static final String BOX_MUI_MODULE_ENABLED_KEY = "tile.boxplusplus.boxUI.mui.module.enabled";
    // #tr tile.boxplusplus.boxUI.mui.module.active
    // # active
    // # zh_CN 已检测
    private static final String BOX_MUI_MODULE_ACTIVE_KEY = "tile.boxplusplus.boxUI.mui.module.active";
    // #tr tile.boxplusplus.boxUI.mui.none
    // # none
    // # zh_CN 无
    private static final String BOX_MUI_NONE_KEY = "tile.boxplusplus.boxUI.mui.none";
    // #tr tile.boxplusplus.boxUI.mui.pattern
    // # Pattern:
    // # zh_CN 样板：
    private static final String BOX_MUI_PATTERN_KEY = "tile.boxplusplus.boxUI.mui.pattern";
    // #tr tile.boxplusplus.boxUI.mui.pattern.normal
    // # AE2 normal encoded pattern
    // # zh_CN AE2 普通编码样板
    private static final String BOX_MUI_PATTERN_NORMAL_KEY = "tile.boxplusplus.boxUI.mui.pattern.normal";
    // #tr tile.boxplusplus.boxUI.mui.pattern.ultimate
    // # GTNH ultimate encoded pattern
    // # zh_CN GTNH 终极编码样板
    private static final String BOX_MUI_PATTERN_ULTIMATE_KEY = "tile.boxplusplus.boxUI.mui.pattern.ultimate";
    private int extendCasing = 0;
    private final boolean[] moduleSwitch = new boolean[15];
    private boolean[] moduleActive = new boolean[15];
    private final int[] moduleTier = new int[15];
    public final ArrayList<BoxRoutings> routingMap = new ArrayList<>();
    public int moduleSN = 1;
    public List<Integer> randomSN = new ArrayList<>(Arrays.asList(1));
    public int ringCount = 1;
    public int wikiPageCode = 1;
    public int routingPageCode = 1;
    public int ringCountSet = 1;
    public int routingStatus = 0;
    private int routingGuiRevision = 0;
    private int parallelUsageRevision = Integer.MIN_VALUE;
    private long cachedConfiguredParallel = 0;
    private int[] machineError = new int[2];
    private int maxParallel = 160;
    private int maxRouting = 16;
    // What's that?
    private static final char[] coreElement = { 'Z', 'Y', 'X', 'W', 'V', 'U', 'T', 'S', 'R', 'Q', 'P', 'O', 'N', 'M' };
    public BoxRecipe recipe = new BoxRecipe();
    private final List<RecipePageState> recipePages = new ArrayList<>();
    protected TEBoxRing teBoxRing;
    public UUID userUUID;
    public boolean debug = false;
    public static IStructureDefinition<GTMachineBox> STRUCTURE_DEFINITION;

    private static final class RecipePageState {

        private final ArrayList<BoxRoutings> routings = new ArrayList<>();
        private BoxRecipe recipe = new BoxRecipe();
        private int status;
    }

    // The spotless made my structure a mess. Shit.

    // spotless off
    static {
        StructureDefinition.Builder<GTMachineBox> A = IStructureDefinition.<GTMachineBox>builder()
            .addShape(
                STRUCTURE_PIECE_MainFrames,
                transpose(
                    new String[][] {
                        { "       ", "   C   ", "  CCC  ", " CCCCC ", "  CCC  ", "   C   ", "       ", "       ",
                            "       ", "       ", "       " },
                        { "   C   ", " CCCCC ", " CC CC ", "CC   CC", " CC CC ", " CCCCC ", "   C   ", "       ",
                            "       ", "       ", "       " },
                        { "  CCC  ", " CC CC ", "CC   CC", "C     C", "CC   CC", " CC CC ", "  CCC  ", "       ",
                            "       ", "       ", "       " },
                        { " CC~CC ", "CC   CC", "C     C", "C  D  C", "C     C", "CC   CC", " CCCCC ", "   C   ",
                            "   C   ", "   C   ", "   C   " },
                        { "  CCC  ", " CC CC ", "CC   CC", "C     C", "CC   CC", " CC CC ", "  CCC  ", "       ",
                            "       ", "       ", "       " },
                        { "   C   ", " CCCCC ", " CC CC ", "CC   CC", " CC CC ", " CCCCC ", "   C   ", "       ",
                            "       ", "       ", "       " },
                        { "       ", "   C   ", "  CCC  ", " CCCCC ", "  CCC  ", "   C   ", "       ", "       ",
                            "       ", "       ", "       " } }))
            .addShape(
                STRUCTURE_PIECE_FirstRing,
                transpose(
                    new String[][] {
                        { "           E           ", "                       ", "           E           ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "E E                 E E",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "           E           ",
                            "                       ", "           E           " },
                        { "          EEE          ", "                       ", "          EEE          ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "E E                 E E", "E E                 E E",
                            "E E                 E E", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "          EEE          ",
                            "                       ", "          EEE          " },
                        { "         EEEEE         ", "      EEEEEEEEEEE      ", "     EEE EEEEE EEE     ",
                            "    EE     E     EE    ", "   EE      E      EE   ", "  EE               EE  ",
                            " EE                 EE ", " EE                 EE ", " E                   E ",
                            "EEE                 EEE", "EEE                 EEE", "EEEEE             EEEEE",
                            "EEE                 EEE", "EEE                 EEE", " E                   E ",
                            " EE                 EE ", " EE                 EE ", "  EE               EE  ",
                            "   EE      E      EE   ", "    EE     E     EE    ", "     EEE EEEEE EEE     ",
                            "      EEEEEEEEEEE      ", "         EEEEE         " },
                        { "        EEE EEE        ", "          E E          ", "        EEE EEE        ",
                            "          E E          ", "          E E          ", "                       ",
                            "                       ", "                       ", "E E                 E E",
                            "E E                 E E", "EEEEE             EEEEE", "                       ",
                            "EEEEE             EEEEE", "E E                 E E", "E E                 E E",
                            "                       ", "                       ", "                       ",
                            "          E E          ", "          E E          ", "        EEE EEE        ",
                            "          E E          ", "        EEE EEE        " },
                        { "         EEEEE         ", "      EEEEEEEEEEE      ", "     EEE EEEEE EEE     ",
                            "    EE     E     EE    ", "   EE      E      EE   ", "  EE               EE  ",
                            " EE                 EE ", " EE                 EE ", " E                   E ",
                            "EEE                 EEE", "EEE                 EEE", "EEEEE             EEEEE",
                            "EEE                 EEE", "EEE                 EEE", " E                   E ",
                            " EE                 EE ", " EE                 EE ", "  EE               EE  ",
                            "   EE      E      EE   ", "    EE     E     EE    ", "     EEE EEEEE EEE     ",
                            "      EEEEEEEEEEE      ", "         EEEEE         " },
                        { "          EEE          ", "                       ", "          EEE          ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "E E                 E E", "E E                 E E",
                            "E E                 E E", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "          EEE          ",
                            "                       ", "          EEE          " },
                        { "           E           ", "                       ", "           E           ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "E E                 E E",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "                       ",
                            "                       ", "                       ", "           E           ",
                            "                       ", "           E           " } }))
            .addShape(
                STRUCTURE_PIECE_SecondRing,
                transpose(
                    new String[][] {
                        { "                 F                 ", "                                   ",
                            "                 F                 ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "F F                             F F",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                 F                 ", "                                   ",
                            "                 F                 " },
                        { "                FFF                ", "                                   ",
                            "                FFF                ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "F F                             F F", "F F                             F F",
                            "F F                             F F", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                FFF                ", "                                   ",
                            "                FFF                " },
                        { "                F F                ", "                                   ",
                            "                F F                ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "F F                             F F", "                                   ",
                            "F F                             F F", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                F F                ", "                                   ",
                            "                F F                " },
                        { "              FFFFFFF              ", "           FFFFFFFFFFFFF           ",
                            "         FFFF FFFFFFF FFFF         ", "        FF       F       FF        ",
                            "      FFF        F        FFF      ", "     FF          F          FF     ",
                            "    FF                       FF    ", "    F                         F    ",
                            "   FF                         FF   ", "  FF                           FF  ",
                            "  F                             F  ", " FF                             FF ",
                            " FF                             FF ", " F                               F ",
                            "FFF                             FFF", "FFF                             FFF",
                            "FFF                             FFF", "FFFFFF                       FFFFFF",
                            "FFF                             FFF", "FFF                             FFF",
                            "FFF                             FFF", " F                               F ",
                            " FF                             FF ", " FF                             FF ",
                            "  F                             F  ", "  FF                           FF  ",
                            "   FF                         FF   ", "    F                         F    ",
                            "    FF                       FF    ", "     FF          F          FF     ",
                            "      FFF        F        FFF      ", "        FF       F       FF        ",
                            "         FFFF FFFFFFF FFFF         ", "           FFFFFFFFFFFFF           ",
                            "              FFFFFFF              " },
                        { "             FF F F FF             ", "                F F                ",
                            "             FF F F FF             ", "                F F                ",
                            "                F F                ", "                F F                ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "F F                             F F",
                            "F F                             F F", "                                   ",
                            "FFFFFF                       FFFFFF", "                                   ",
                            "FFFFFF                       FFFFFF", "                                   ",
                            "F F                             F F", "F F                             F F",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                F F                ",
                            "                F F                ", "                F F                ",
                            "             FF F F FF             ", "                F F                ",
                            "             FF F F FF             " },
                        { "              FFFFFFF              ", "           FFFFFFFFFFFFF           ",
                            "         FFFF FFFFFFF FFFF         ", "        FF       F       FF        ",
                            "      FFF        F        FFF      ", "     FF          F          FF     ",
                            "    FF                       FF    ", "    F                         F    ",
                            "   FF                         FF   ", "  FF                           FF  ",
                            "  F                             F  ", " FF                             FF ",
                            " FF                             FF ", " F                               F ",
                            "FFF                             FFF", "FFF                             FFF",
                            "FFF                             FFF", "FFFFFF                       FFFFFF",
                            "FFF                             FFF", "FFF                             FFF",
                            "FFF                             FFF", " F                               F ",
                            " FF                             FF ", " FF                             FF ",
                            "  F                             F  ", "  FF                           FF  ",
                            "   FF                         FF   ", "    F                         F    ",
                            "    FF                       FF    ", "     FF          F          FF     ",
                            "      FFF        F        FFF      ", "        FF       F       FF        ",
                            "         FFFF FFFFFFF FFFF         ", "           FFFFFFFFFFFFF           ",
                            "              FFFFFFF              " },
                        { "                F F                ", "                                   ",
                            "                F F                ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "F F                             F F", "                                   ",
                            "F F                             F F", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                F F                ", "                                   ",
                            "                F F                " },
                        { "                FFF                ", "                                   ",
                            "                FFF                ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "F F                             F F", "F F                             F F",
                            "F F                             F F", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                FFF                ", "                                   ",
                            "                FFF                " },
                        { "                 F                 ", "                                   ",
                            "                 F                 ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "F F                             F F",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                                   ", "                                   ",
                            "                 F                 ", "                                   ",
                            "                 F                 " } }))
            .addShape(
                STRUCTURE_PIECE_Final,
                transpose(
                    new String[][] {
                        { "                       G                       ",
                            "                                               ",
                            "                       G                       ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                       G                       ",
                            "                                               ",
                            "                       G                       " },
                        { "                      GGG                      ",
                            "                                               ",
                            "                      GGG                      ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                      GGG                      ",
                            "                                               ",
                            "                      GGG                      " },
                        { "                      G G                      ",
                            "                                               ",
                            "                      G G                      ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "                                               ",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                      G G                      ",
                            "                                               ",
                            "                      G G                      " },
                        { "                     GGGGG                     ",
                            "                GGGGGGGGGGGGGGG                ",
                            "              GGGG   GG GG   GGGG              ",
                            "            GGG                 GGG            ",
                            "          GGG                     GGG          ",
                            "         GG                         GG         ",
                            "        GG                           GG        ",
                            "       GG                             GG       ",
                            "      GG                               GG      ",
                            "     GG                                 GG     ",
                            "    GG                                   GG    ",
                            "    G                                     G    ",
                            "   GG                                     GG   ",
                            "   G                                       G   ",
                            "  GG                                       GG  ",
                            "  G                                         G  ",
                            " GG                                         GG ",
                            " GG                                         GG ",
                            " G                                           G ",
                            " G                                           G ",
                            " G                                           G ",
                            "GGG                                         GGG",
                            "GGG                                         GGG",
                            "GG                                           GG",
                            "GGG                                         GGG",
                            "GGG                                         GGG",
                            " G                                           G ",
                            " G                                           G ",
                            " G                                           G ",
                            " GG                                         GG ",
                            " GG                                         GG ",
                            "  G                                         G  ",
                            "  GG                                       GG  ",
                            "   G                                       G   ",
                            "   GG                                     GG   ",
                            "    G                                     G    ",
                            "    GG                                   GG    ",
                            "     GG                                 GG     ",
                            "      GG                               GG      ",
                            "       GG                             GG       ",
                            "        GG                           GG        ",
                            "         GG                         GG         ",
                            "          GGG                     GGG          ",
                            "            GGG                 GGG            ",
                            "              GGGG   GG GG   GGGG              ",
                            "                GGGGGGGGGGGGGGG                ",
                            "                     GGGGG                     " },
                        { "                   GGG G GGG                   ",
                            "                       G                       ",
                            "                   GGG G GGG                   ",
                            "                       G                       ",
                            "                       G                       ",
                            "                       G                       ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "GGGGGG                                   GGGGGG",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                       G                       ",
                            "                       G                       ",
                            "                       G                       ",
                            "                   GGG G GGG                   ",
                            "                       G                       ",
                            "                   GGG G GGG                   " },
                        { "                  GG  G G  GG                  ",
                            "                      G G                      ",
                            "                  GG  G G  GG                  ",
                            "                      G G                      ",
                            "                      G G                      ",
                            "                      G G                      ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "GGGGGG                                   GGGGGG",
                            "                                               ",
                            "GGGGGG                                   GGGGGG",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                      G G                      ",
                            "                      G G                      ",
                            "                      G G                      ",
                            "                  GG  G G  GG                  ",
                            "                      G G                      ",
                            "                  GG  G G  GG                  " },
                        { "                   GGG G GGG                   ",
                            "                       G                       ",
                            "                   GGG G GGG                   ",
                            "                       G                       ",
                            "                       G                       ",
                            "                       G                       ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "GGGGGG                                   GGGGGG",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                       G                       ",
                            "                       G                       ",
                            "                       G                       ",
                            "                   GGG G GGG                   ",
                            "                       G                       ",
                            "                   GGG G GGG                   " },
                        { "                     GGGGG                     ",
                            "                GGGGGGGGGGGGGGG                ",
                            "              GGGG   GG GG   GGGG              ",
                            "            GGG                 GGG            ",
                            "          GGG                     GGG          ",
                            "         GG                         GG         ",
                            "        GG                           GG        ",
                            "       GG                             GG       ",
                            "      GG                               GG      ",
                            "     GG                                 GG     ",
                            "    GG                                   GG    ",
                            "    G                                     G    ",
                            "   GG                                     GG   ",
                            "   G                                       G   ",
                            "  GG                                       GG  ",
                            "  G                                         G  ",
                            " GG                                         GG ",
                            " GG                                         GG ",
                            " G                                           G ",
                            " G                                           G ",
                            " G                                           G ",
                            "GGG                                         GGG",
                            "GGG                                         GGG",
                            "GG                                           GG",
                            "GGG                                         GGG",
                            "GGG                                         GGG",
                            " G                                           G ",
                            " G                                           G ",
                            " G                                           G ",
                            " GG                                         GG ",
                            " GG                                         GG ",
                            "  G                                         G  ",
                            "  GG                                       GG  ",
                            "   G                                       G   ",
                            "   GG                                     GG   ",
                            "    G                                     G    ",
                            "    GG                                   GG    ",
                            "     GG                                 GG     ",
                            "      GG                               GG      ",
                            "       GG                             GG       ",
                            "        GG                           GG        ",
                            "         GG                         GG         ",
                            "          GGG                     GGG          ",
                            "            GGG                 GGG            ",
                            "              GGGG   GG GG   GGGG              ",
                            "                GGGGGGGGGGGGGGG                ",
                            "                     GGGGG                     " },
                        { "                      G G                      ",
                            "                                               ",
                            "                      G G                      ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "                                               ",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                      G G                      ",
                            "                                               ",
                            "                      G G                      " },
                        { "                      GGG                      ",
                            "                                               ",
                            "                      GGG                      ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "G G                                         G G",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                      GGG                      ",
                            "                                               ",
                            "                      GGG                      " },
                        { "                       G                       ",
                            "                                               ",
                            "                       G                       ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "G G                                         G G",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                                               ",
                            "                       G                       ",
                            "                                               ",
                            "                       G                       " } }));
        for (int i = 0; i < 14; i++) {
            A.addShape(getModuleByIndex(i).name, transpose(getModuleByIndex(i).moduleStructure));
        }
        A.addElement(
            'C',
            buildHatchAdder(GTMachineBox.class)
                .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy, ExoticEnergy, Maintenance)
                .casingIndex(114 << 7)
                .hint(1)
                .buildAndChain(
                    onElementPass((GTMachineBox i) -> ++i.extendCasing, ofBlock(BlockRegister.SpaceExtend, 0))))
            .addElement('D', Util.RingTileAdder((GTMachineBox v, TEBoxRing t) -> {
                if ((t.getBlockType()
                    .isAssociatedBlock(BlockRegister.BoxRing) && v.ringCountSet != 1)) return false;
                if ((t.getBlockType()
                    .isAssociatedBlock(BlockRegister.BoxRing2) && v.ringCountSet != 2)) return false;
                if ((t.getBlockType()
                    .isAssociatedBlock(BlockRegister.BoxRing3) && v.ringCountSet != 3)) return false;
                v.teBoxRing = t;
                return true;
            },
                TEBoxRing.class,
                BlockRegister.BoxRing,
                0,
                (GTMachineBox v) -> v.ringCountSet == 1 ? BlockRegister.BoxRing
                    : (v.ringCountSet == 2 ? BlockRegister.BoxRing2 : BlockRegister.BoxRing3)))
            .addElement('E', ofBlock(BlockRegister.SpaceCompress, 0))
            .addElement('F', ofBlock(BlockRegister.SpaceConstraint, 0))
            .addElement('G', ofBlock(BlockRegister.SpaceWall, 0));
        for (int i = 0; i < 14; i++) {
            int finalI = i;
            A.addElement(coreElement[i], ofChain(ofBlockAdder((t, b, m) -> {
                if (b.isAssociatedBlock(BlockRegister.BoxModule) && m == finalI) {
                    t.moduleTier[finalI] = 0;
                    if (finalI == 13) t.maxParallel = 1280000;
                    return true;
                }
                return false;
            }, BlockRegister.BoxModule, i), ofBlockAdder((t, b, m) -> {
                if (m == 14 && finalI == 13 && b.isAssociatedBlock(BlockRegister.BoxModuleUpgrad)) {
                    t.debug = true;
                    t.maxParallel = Integer.MAX_VALUE;
                    t.maxRouting = Integer.MAX_VALUE;
                    return true;
                }
                if (b.isAssociatedBlock(BlockRegister.BoxModuleUpgrad) && m == finalI) {
                    t.moduleTier[finalI] = 1;
                    if (m == 13) {
                        t.maxParallel = 99900000;
                        t.maxRouting = 999;
                    }
                    return true;
                }
                t.moduleTier[finalI] = 0;
                return false;
            }, BlockRegister.BoxModuleUpgrad, i)));
        }
        STRUCTURE_DEFINITION = A.build();
    }

    // spotless on

    public GTMachineBox(String name) {
        super(name);
        initializeRecipePages();
    }

    public GTMachineBox(int ID, String Name, String NameRegional) {
        super(ID, Name, NameRegional);
        initializeRecipePages();
    }

    private void initializeRecipePages() {
        if (!recipePages.isEmpty()) return;
        RecipePageState first = new RecipePageState();
        first.routings.addAll(routingMap);
        first.recipe = recipe;
        first.status = routingStatus;
        recipePages.add(first);
        routingPageCode = 1;
    }

    private void storeActiveRecipePage() {
        initializeRecipePages();
        int index = Math.max(0, Math.min(recipePages.size() - 1, routingPageCode - 1));
        RecipePageState page = recipePages.get(index);
        page.routings.clear();
        page.routings.addAll(routingMap);
        page.recipe = recipe;
        page.status = routingStatus;
    }

    private void loadRecipePage(int zeroBasedIndex) {
        initializeRecipePages();
        int index = Math.max(0, Math.min(recipePages.size() - 1, zeroBasedIndex));
        RecipePageState page = recipePages.get(index);
        routingPageCode = index + 1;
        routingMap.clear();
        routingMap.addAll(page.routings);
        recipe = page.recipe;
        routingStatus = page.status;
    }

    public int getRecipePageForGui() {
        return routingPageCode;
    }

    public int getRecipePageCountForGui() {
        initializeRecipePages();
        return recipePages.size();
    }

    public int getMaxRecipePagesForGui() {
        if (ringCount < 3) return Math.max(1, ringCount) * 3;
        if (moduleActive[13]) return moduleTier[13] >= 1 ? 99 : 27;
        return 9;
    }

    public int getRecipePageNavigationLimitForGui() {
        initializeRecipePages();
        return Math.max(recipePages.size(), getMaxRecipePagesForGui());
    }

    public boolean switchRecipePageForGui(int oneBasedPage) {
        initializeRecipePages();
        if (oneBasedPage < 1 || oneBasedPage > 99) return false;
        if (oneBasedPage > recipePages.size() && oneBasedPage > getMaxRecipePagesForGui()) return false;
        storeActiveRecipePage();
        while (recipePages.size() < oneBasedPage) recipePages.add(new RecipePageState());
        loadRecipePage(oneBasedPage - 1);
        markRoutingGuiChanged();
        return true;
    }

    public void applyRecipePageCountMirrorForGui(int pageCount) {
        int safeCount = Math.max(1, Math.min(99, pageCount));
        initializeRecipePages();
        while (recipePages.size() < safeCount) recipePages.add(new RecipePageState());
        while (recipePages.size() > safeCount) recipePages.remove(recipePages.size() - 1);
        if (routingPageCode > safeCount) loadRecipePage(safeCount - 1);
    }

    public void applyRecipePageMirrorForGui(int oneBasedPage) {
        applyRecipePageCountMirrorForGui(oneBasedPage);
        storeActiveRecipePage();
        loadRecipePage(oneBasedPage - 1);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GTMachineBox(super.mName);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new BoxMachineGui(this);
    }

    @Override
    public IStructureDefinition<GTMachineBox> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(i18n("tile.boxplusplus.boxtype"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.02"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.03"))
            .addSeparator()
            .addInfo(EnumChatFormatting.DARK_GREEN + i18n("tile.boxplusplus.boxinfo.04"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.05"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.06"))
            .addInfo(EnumChatFormatting.GOLD + i18n("tile.boxplusplus.boxinfo.07"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.08"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.09"))
            .addSeparator()
            .addInfo(i18n("tile.boxplusplus.boxinfo.10"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.11"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.12"))
            .addSeparator()
            .addInfo(i18n("tile.boxplusplus.boxinfo.13"))
            .addInfo(i18n("tile.boxplusplus.boxinfo.14"))
            .addInfo(EnumChatFormatting.AQUA + i18n("tile.boxplusplus.boxinfo.15"))
            .addPollutionAmount(0)
            .addSeparator()
            .beginStructureBlock(47, 11, 47, false)
            .addStructureInfo(i18n("tile.boxplusplus.boxStructure.01"))
            .addController(i18n("tile.boxplusplus.boxStructure.02"))
            .addCasingInfoMin(i18n("tile.boxplusplus_SpaceExtend.name"), 130, false)
            .addCasingInfoExactly(i18n("tile.boxplusplus_SpaceCompress.name"), 408, false)
            .addCasingInfoExactly(i18n("tile.boxplusplus_SpaceConstraint.name"), 584, false)
            .addCasingInfoExactly(i18n("tile.boxplusplus_SpaceWall.name"), 760, false)
            .addStructureInfo(i18n("tile.boxplusplus.boxStructure.03"))
            .addEnergyHatch(i18n("tile.boxplusplus.boxStructure.04"))
            .addStructureInfo(EnumChatFormatting.YELLOW + i18n("tile.boxplusplus.boxStructure.05"))
            .addSeparator()
            .toolTipFinisher("BoxPlusPlus");
        return tt;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        switch (ringCountSet) {
            case 1 -> {
                switch (stackSize.stackSize) {
                    case 1 -> {
                        buildPiece(STRUCTURE_PIECE_MainFrames, stackSize, hintsOnly, 3, 3, 0);
                        buildPiece(STRUCTURE_PIECE_FirstRing, stackSize, hintsOnly, 11, 3, 8);
                    }
                    case 2 -> {
                        buildPiece(STRUCTURE_PIECE_MainFrames, stackSize, hintsOnly, 3, 3, 0);
                        buildPiece(STRUCTURE_PIECE_FirstRing, stackSize, hintsOnly, 11, 3, 8);
                        buildPiece(STRUCTURE_PIECE_SecondRing, stackSize, hintsOnly, 17, 5, 14);
                    }
                    default -> {
                        buildPiece(STRUCTURE_PIECE_MainFrames, stackSize, hintsOnly, 3, 3, 0);
                        buildPiece(STRUCTURE_PIECE_FirstRing, stackSize, hintsOnly, 11, 3, 8);
                        buildPiece(STRUCTURE_PIECE_SecondRing, stackSize, hintsOnly, 17, 5, 14);
                        buildPiece(STRUCTURE_PIECE_Final, stackSize, hintsOnly, 23, 5, 20);
                    }
                }
            }
            case 2 -> {
                buildPiece(STRUCTURE_PIECE_MainFrames, stackSize, hintsOnly, 3, 3, 0);
                buildPiece(STRUCTURE_PIECE_FirstRing, stackSize, hintsOnly, 11, 3, 8);
                buildPiece(STRUCTURE_PIECE_SecondRing, stackSize, hintsOnly, 17, 5, 14);
            }
            case 3 -> {
                buildPiece(STRUCTURE_PIECE_MainFrames, stackSize, hintsOnly, 3, 3, 0);
                buildPiece(STRUCTURE_PIECE_FirstRing, stackSize, hintsOnly, 11, 3, 8);
                buildPiece(STRUCTURE_PIECE_SecondRing, stackSize, hintsOnly, 17, 5, 14);
                buildPiece(STRUCTURE_PIECE_Final, stackSize, hintsOnly, 23, 5, 20);
            }
        }
        for (int i = 0; i < 14; i++) {
            if (moduleSwitch[i] || stackSize.stackSize - 4 >= i) {
                buildPiece(
                    getModuleByIndex(i).name,
                    stackSize,
                    hintsOnly,
                    getModuleByIndex(i).horizontalOffset,
                    getModuleByIndex(i).verticalOffset,
                    getModuleByIndex(i).depthOffset);
            }
        }
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        elementBudget = Math.min(200, elementBudget * 4);
        int count = 0;
        switch (ringCountSet) {
            case 1 -> {
                switch (stack.stackSize) {
                    case 1 -> {
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_MainFrames,
                            stack,
                            3,
                            3,
                            0,
                            elementBudget,
                            env,
                            false,
                            true);
                        if (count > -1) {
                            break;
                        }
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_FirstRing,
                            stack,
                            11,
                            3,
                            8,
                            elementBudget,
                            env,
                            false,
                            true);

                    }
                    case 2 -> {
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_MainFrames,
                            stack,
                            3,
                            3,
                            0,
                            elementBudget,
                            env,
                            false,
                            true);
                        if (count > -1) {
                            break;
                        }
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_FirstRing,
                            stack,
                            11,
                            3,
                            8,
                            elementBudget,
                            env,
                            false,
                            true);
                        if (count > -1) {
                            break;
                        }
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_SecondRing,
                            stack,
                            17,
                            5,
                            14,
                            elementBudget,
                            env,
                            false,
                            true);
                    }
                    default -> {
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_MainFrames,
                            stack,
                            3,
                            3,
                            0,
                            elementBudget,
                            env,
                            false,
                            true);
                        if (count > -1) {
                            break;
                        }
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_FirstRing,
                            stack,
                            11,
                            3,
                            8,
                            elementBudget,
                            env,
                            false,
                            true);
                        if (count > -1) {
                            break;
                        }
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_SecondRing,
                            stack,
                            17,
                            5,
                            14,
                            elementBudget,
                            env,
                            false,
                            true);
                        if (count > -1) {
                            break;
                        }
                        count = survivalBuildPiece(
                            STRUCTURE_PIECE_Final,
                            stack,
                            23,
                            5,
                            20,
                            elementBudget,
                            env,
                            false,
                            true);
                    }
                }
            }
            case 2 -> {
                count = survivalBuildPiece(STRUCTURE_PIECE_MainFrames, stack, 3, 3, 0, elementBudget, env, false, true);
                if (count > -1) {
                    break;
                }
                count = survivalBuildPiece(STRUCTURE_PIECE_FirstRing, stack, 11, 3, 8, elementBudget, env, false, true);
                if (count > -1) {
                    break;
                }
                count = survivalBuildPiece(
                    STRUCTURE_PIECE_SecondRing,
                    stack,
                    17,
                    5,
                    14,
                    elementBudget,
                    env,
                    false,
                    true);
            }
            case 3 -> {
                count = survivalBuildPiece(STRUCTURE_PIECE_MainFrames, stack, 3, 3, 0, elementBudget, env, false, true);
                if (count > -1) {
                    break;
                }
                count = survivalBuildPiece(STRUCTURE_PIECE_FirstRing, stack, 11, 3, 8, elementBudget, env, false, true);
                if (count > -1) {
                    break;
                }
                count = survivalBuildPiece(
                    STRUCTURE_PIECE_SecondRing,
                    stack,
                    17,
                    5,
                    14,
                    elementBudget,
                    env,
                    false,
                    true);
                if (count > -1) {
                    break;
                }
                count = survivalBuildPiece(STRUCTURE_PIECE_Final, stack, 23, 5, 20, elementBudget, env, false, true);
            }
        }
        return count;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        ringCount = 1;
        maxParallel = 160;
        maxRouting = 16;
        debug = false;
        moduleActive = new boolean[moduleActive.length];
        machineError = new int[2];
        switch (ringCountSet) {
            case 1 -> {
                if (checkPiece(STRUCTURE_PIECE_MainFrames, 3, 3, 0, errors)
                    && checkPiece(STRUCTURE_PIECE_FirstRing, 11, 3, 8, errors)) {
                    ringCount = 1;
                    break;
                }
                disableRingRenderAfterFailedCheck();
                machineError[0] = 1;
                return;
            }
            case 2 -> {
                if (checkPiece(STRUCTURE_PIECE_MainFrames, 3, 3, 0, errors)
                    && checkPiece(STRUCTURE_PIECE_FirstRing, 11, 3, 8, errors)
                    && checkPiece(STRUCTURE_PIECE_SecondRing, 17, 5, 14, errors)) {
                    ringCount = 2;
                    maxParallel = 6400;
                    maxRouting = 64;
                    break;
                }
                disableRingRenderAfterFailedCheck();
                machineError[0] = 2;
                return;
            }
            case 3 -> {
                if (checkPiece(STRUCTURE_PIECE_MainFrames, 3, 3, 0, errors)
                    && checkPiece(STRUCTURE_PIECE_FirstRing, 11, 3, 8, errors)
                    && checkPiece(STRUCTURE_PIECE_SecondRing, 17, 5, 14, errors)
                    && checkPiece(STRUCTURE_PIECE_Final, 23, 5, 20, errors)) {
                    ringCount = 3;
                    maxParallel = 128000;
                    maxRouting = 128;
                    break;
                }
                disableRingRenderAfterFailedCheck();
                machineError[0] = 3;
                return;
            }
            default -> {
                errors.add(GTNCStructureErrors.unknownLegacyCheckFailure());
                return;
            }
        }
        for (int i = 0; i < 15; i++) {
            if (moduleSwitch[i]) {
                if (checkPiece(
                    getModuleByIndex(i).name,
                    getModuleByIndex(i).horizontalOffset,
                    getModuleByIndex(i).verticalOffset,
                    getModuleByIndex(i).depthOffset,
                    errors)) {
                    moduleActive[i] = true;
                    continue;
                }
                if (teBoxRing != null) teBoxRing.renderStatus = false;
                machineError[0] = 4;
                machineError[1] = i + 1;
                return;
            }
        }
        // If you want it, then you'll have to take it.
        for (MTEHatch hatch : getExoticEnergyHatches()) {
            if (hatch instanceof MTEHatchEnergyMulti && ringCount == 1) {
                machineError[0] = 5;
                errors.add(GTNCStructureErrors.energyInputAmperageTooHigh());
                return;
            }
            if (hatch instanceof MTEHatchEnergyTunnel && !moduleActive[12]) {
                machineError[0] = 6;
                errors.add(GTNCStructureErrors.laserEnergyTunnelDisabled());
                return;
            }
        }
        if (teBoxRing != null) {
            teBoxRing.renderStatus = true;
            teBoxRing.count = ringCount;
        }
    }

    private void disableRingRenderAfterFailedCheck() {
        if (teBoxRing == null) return;
        teBoxRing.renderStatus = false;
        teBoxRing = null;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mDualInputHatches.clear();
    }

    /**
     * Check if the recipe is the final one.
     *
     * @return CheckRecipeResultRegistry
     */
    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        lEUt = 0;
        mMaxProgresstime = 0;
        mOutputItems = null;
        mOutputFluids = null;
        storeActiveRecipePage();
        List<ItemStack> inputItem = getStoredInputs();
        List<FluidStack> inputFluid = getStoredFluids();
        for (IDualInputHatch hatch : mDualInputHatches) {
            Iterator<? extends IDualInputInventory> meHatchIter = hatch.inventories();
            while (meHatchIter.hasNext()) {
                IDualInputInventory inv = meHatchIter.next();
                inputItem.addAll(Arrays.asList(inv.getItemInputs()));
                inputFluid.addAll(Arrays.asList(inv.getFluidInputs()));
            }
        }
        ItemContainer Icontainer = new ItemContainer();
        FluidContainer Fcontainer = new FluidContainer();
        List<ItemStack> totalInputItem = Icontainer.addItemStackList(inputItem, 1)
            .getItemStack();
        List<FluidStack> totalInputFluid = Fcontainer.addFluidStackList(inputFluid, 1)
            .getFluidStack();
        inputItem.removeAll(Collections.singleton(null));
        inputFluid.removeAll(Collections.singleton(null));
        CheckRecipeResult firstFailure = null;
        for (RecipePageState page : recipePages) {
            BoxRecipe candidate = page.recipe;
            if (candidate == null || !candidate.islocked) continue;
            if (!matchesRecipeInputs(candidate, totalInputItem, totalInputFluid)) continue;
            CheckRecipeResult moduleFailure = checkRecipeModules(candidate);
            if (moduleFailure != null) {
                if (firstFailure == null) firstFailure = moduleFailure;
                continue;
            }
            CheckRecipeResult result = runBox(inputItem, inputFluid, candidate);
            if (result.wasSuccessful()) return result;
            if (firstFailure == null) firstFailure = result;
        }
        return firstFailure == null ? CheckRecipeResultRegistry.NO_RECIPE : firstFailure;
    }

    private boolean matchesRecipeInputs(BoxRecipe candidate, List<ItemStack> totalInputItem,
        List<FluidStack> totalInputFluid) {
        if ((totalInputItem.isEmpty() && !candidate.FinalItemInput.isEmpty())
            || (totalInputFluid.isEmpty() && !candidate.FinalFluidInput.isEmpty())) return false;
        List<ItemStack> availableItem = deepCopyItemList(totalInputItem);
        List<FluidStack> availableFluid = deepCopyFluidList(totalInputFluid);
        List<ItemStack> requireItem = deepCopyItemList(candidate.FinalItemInput);
        List<FluidStack> requireFluid = deepCopyFluidList(candidate.FinalFluidInput);
        BoxRecipe.ItemOnBox(availableItem, requireItem);
        BoxRecipe.FluidOnBox(availableFluid, requireFluid);
        return requireItem.isEmpty() && requireFluid.isEmpty();
    }

    private CheckRecipeResult checkRecipeModules(BoxRecipe candidate) {
        for (int k : candidate.requireModules.keySet()) {
            if (k == 13 && candidate.requireModules.get(k) == 2 && !debug)
                return SimpleCheckRecipeResult.ofFailure("box_debugmode");
            if (!moduleActive[k] || candidate.requireModules.get(k) == 1 && moduleTier[k] != 1)
                return new ResultModuleRequirement(k, candidate.requireModules.get(k) == 1);
        }
        return null;
    }

    /**
     * Run the box system.
     *
     * @param inputItem  All itemstack that input
     * @param inputFluid All fluidstack that input
     * @return true if the box starts
     */
    public CheckRecipeResult runBox(List<ItemStack> inputItem, List<FluidStack> inputFluid, BoxRecipe selectedRecipe) {
        lEUt = 0;
        mMaxProgresstime = 0;
        if (selectedRecipe.FinalTime >= Integer.MAX_VALUE - 1) return CheckRecipeResultRegistry.DURATION_OVERFLOW;
        if (!moduleActive[12] || moduleTier[12] == 0) {
            if (getMaxInputEu() < selectedRecipe.FinalVoteage)
                return CheckRecipeResultRegistry.insufficientPower(selectedRecipe.FinalVoteage);
            lEUt = -selectedRecipe.FinalVoteage;
        }
        if (moduleActive[12] && moduleTier[12] == 1
            && !WirelessNetworkManager
                .addEUToGlobalEnergyMap(userUUID, -selectedRecipe.FinalVoteage * selectedRecipe.FinalTime)) {
            return SimpleCheckRecipeResult.ofFailure("no_wireless_power");
        }
        calTime(selectedRecipe);
        if (this.lEUt >= Long.MAX_VALUE - 1) return CheckRecipeResultRegistry.POWER_OVERFLOW;
        mEfficiencyIncrease = 10000;
        mEfficiency = 10000 - (this.getIdealStatus() - this.getRepairStatus()) * 1000;
        List<ItemStack> requireItem = deepCopyItemList(selectedRecipe.FinalItemInput);
        List<FluidStack> requireFluid = deepCopyFluidList(selectedRecipe.FinalFluidInput);
        BoxRecipe.ItemOnBox(requireItem, inputItem);
        BoxRecipe.FluidOnBox(requireFluid, inputFluid);
        mOutputItems = deepCopyItemList(selectedRecipe.FinalItemOutput).toArray(new ItemStack[0]);
        mOutputFluids = deepCopyFluidList(selectedRecipe.FinalFluidOutput).toArray(new FluidStack[0]);
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    private void calTime(BoxRecipe selectedRecipe) {
        if (lEUt == 0) {
            mMaxProgresstime = Math.max((int) Math.pow(selectedRecipe.FinalTime, 0.2), 10);
            return;
        }
        OverclockCalculator cal = new OverclockCalculator().setRecipeEUt(selectedRecipe.FinalVoteage)
            .setDuration((int) selectedRecipe.FinalTime)
            .setEUt(getMaxInputEu());
        switch (ringCount) {
            case 1:
                this.mMaxProgresstime = (int) selectedRecipe.FinalTime;
                return;
            case 2:
                break;
            case 3:
                cal.enablePerfectOC();
        }
        cal.calculate();
        this.lEUt = cal.getConsumption();
        this.mMaxProgresstime = cal.getDuration();
        if (this.lEUt > 0) {
            this.lEUt *= -1;
        }
    }

    @Override
    public int getMaxEfficiency(ItemStack stack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    /**
     * I'd rather use CuttingFactory's texture,but you will kill me definitely.
     */
    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[114][0], TextureFactory.builder()
                .addIcon(boxActive)
                .extFacing()
                .build() };
            return new ITexture[] { casingTexturePages[114][0], TextureFactory.builder()
                .addIcon(boxInactive)
                .extFacing()
                .build() };
        }
        return new ITexture[] { casingTexturePages[114][0] };
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPreTick(aBaseMetaTileEntity, aTick);
        if (aTick == 1) {
            userUUID = getBaseMetaTileEntity().getOwnerUuid();
            WirelessNetworkManager.strongCheckOrAddUser(userUUID);
        }
    }

    /**
     * Make a pattern. Mostly from AE2 & AE2FC-GTNH.
     *
     * @param player Who is making it
     */
    private void makeAE2Pattern(EntityPlayer player, String item, String fluid) {
        if (item.equals("0") && fluid.equals("0")) {
            player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.chatmessage.6")));
            return;
        }
        if (mProgresstime == 0) {
            for (ItemStack pattern : getStoredInputs()) {
                if (Util.isPattern(pattern)) {
                    ItemStack outputPattern = pattern.copy();
                    if (recipe.FinalFluidOutput.isEmpty() && recipe.FinalFluidInput.isEmpty()) {
                        for (final ItemStack encodedPatternStack : AEApi.instance()
                            .definitions()
                            .items()
                            .encodedPattern()
                            .maybeStack(1)
                            .asSet()) {
                            outputPattern = encodedPatternStack;
                            NBTTagCompound encodedValue = recipe.RecipeToAE2ItemPattern(item);
                            outputPattern.setTagCompound(encodedValue);
                        }
                    } else {
                        outputPattern = AEApi.instance()
                            .definitions()
                            .items()
                            .encodedUltimatePattern()
                            .maybeStack(1)
                            .orNull();
                        if (outputPattern == null) {
                            player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.chatmessage.8")));
                            return;
                        }
                        outputPattern.setTagCompound(recipe.RecipeToAE2UltimatePattern(item, fluid));
                    }
                    outputPattern.stackTagCompound
                        .setString("author", player.getDisplayName() + i18n("tile.boxplusplus.boxinfo.16"));
                    pattern.stackSize -= 1;
                    mOutputItems = new ItemStack[] { outputPattern };
                    mMaxProgresstime = 100;
                    updateSlots();
                    return;
                }
            }
        }
        player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.chatmessage.8")));
    }

    public void exportAE2Pattern(EntityPlayer player, String item, String fluid) {
        makeAE2Pattern(player, item, fluid);
        if (player != null) player.closeScreen();
    }

    public void bindRoutingContext(EntityPlayer player) {
        Util.boxMap.put(player, this);
    }

    public void toggleRingRender() {
        if (teBoxRing != null) {
            teBoxRing.teRingSwitch = !teBoxRing.teRingSwitch;
        }
    }

    public int getRingCountForGui() {
        return ringCountSet;
    }

    public void setRingCountForGui(int ring) {
        ringCountSet = Math.max(1, Math.min(3, ring));
        int firstUnavailableModule = ringCountSet == 1 ? 4 : ringCountSet == 2 ? 8 : 14;
        for (int i = firstUnavailableModule; i < 14; i++) {
            moduleSwitch[i] = false;
            moduleActive[i] = false;
        }
        onMachineBlockUpdate();
    }

    public boolean tryDoubleRecipe() {
        if (recipe.islocked || routingMap.isEmpty()) return false;
        boolean result = doubleRecipe();
        routingStatus = result ? 0 : 10;
        markRoutingGuiChanged();
        return result;
    }

    public boolean tryHalveRecipe() {
        if (recipe.islocked || routingMap.isEmpty()) return false;
        boolean result = halveRecipe();
        routingStatus = result ? 0 : 9;
        markRoutingGuiChanged();
        return result;
    }

    public void clearBoxRecipe() {
        routingMap.clear();
        recipe = new BoxRecipe();
        routingStatus = 0;
        storeActiveRecipePage();
        randomSN.clear();
        markRoutingGuiChanged();
    }

    private void reopenMainGui(EntityPlayer player) {
        if (player == null || getBaseMetaTileEntity() == null || getBaseMetaTileEntity().isDead()) return;
        if (!(player instanceof EntityPlayerMP playerMP)) return;
        player.closeScreen();
        ProxiedMteGui.open(this, playerMP);
    }

    public String getClearPromptForGui() {
        if (randomSN.isEmpty()) {
            for (int i = 0; i < 5; i++) randomSN.add(new Random().nextInt(5) + 1);
        }
        return i18n(randomSN.size() == 1 ? CLEAR_CURRENT_PAGE_FINAL_KEY : CLEAR_CURRENT_PAGE_KEY)
            + i18n(CLEAR_NUMBER_PROMPT_KEY)
            + EnumChatFormatting.RED
            + randomSN.get(randomSN.size() - 1);
    }

    public void submitClearNumberForGui(int number) {
        if (randomSN.isEmpty()) getClearPromptForGui();
        if (number != randomSN.get(randomSN.size() - 1)) {
            randomSN.clear();
            markRoutingGuiChanged();
            return;
        }
        randomSN.remove(randomSN.size() - 1);
        if (randomSN.isEmpty()) clearBoxRecipe();
        else markRoutingGuiChanged();
    }

    public void submitClearNumberForGui(int number, EntityPlayer player) {
        if (randomSN.isEmpty()) getClearPromptForGui();
        if (number != randomSN.get(randomSN.size() - 1)) {
            randomSN.clear();
            markRoutingGuiChanged();
            if (player != null) {
                player.closeScreen();
                player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.boxUI.47")));
            }
            return;
        }
        randomSN.remove(randomSN.size() - 1);
        if (randomSN.isEmpty()) {
            clearBoxRecipe();
            if (player != null) {
                player.closeScreen();
                player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.boxUI.45")));
            }
        } else {
            markRoutingGuiChanged();
        }
    }

    public String[] getBoxGuiSummary() {
        return new String[] {
            i18n("tile.boxplusplus.boxUI.05") + routingMap.size() + i18n("tile.boxplusplus.boxUI.06") + maxRouting,
            i18n("tile.boxplusplus.boxUI.40") + maxParallel,
            i18n("tile.boxplusplus.boxUI.16") + recipe.FinalVoteage + " eu/t",
            i18n("tile.boxplusplus.boxUI.17") + recipe.FinalTime / 20.00 + "s (" + recipe.FinalTime + "tick)",
            i18n("tile.boxplusplus.boxUI.29") + recipe.parallel,
            i18n("tile.boxplusplus.boxUI.ErrorCode." + routingStatus) };
    }

    public boolean isBoxRecipeLockedForGui() {
        return recipe.islocked;
    }

    public int getRoutingCountForGui() {
        return routingMap.size();
    }

    public int getRoutingStatusForGui() {
        return routingStatus;
    }

    public int getRoutingGuiRevisionForGui() {
        return routingGuiRevision;
    }

    private void markRoutingGuiChanged() {
        routingGuiRevision++;
        onMachineBlockUpdate();
    }

    public int getMaxParallelForGui() {
        return maxParallel;
    }

    public int getMaxRoutingForGui() {
        return maxRouting;
    }

    public int getAvailableRoutingForGui() {
        return Math.max(0, maxRouting - routingMap.size());
    }

    public int getAvailableParallelForGui() {
        long available = Math.max(0L, (long) maxParallel - getTotalConfiguredParallel());
        return (int) Math.min(Integer.MAX_VALUE, available);
    }

    public boolean rebuildFinalRecipeForGui() {
        if (recipe.islocked || routingMap.isEmpty()) return false;
        try {
            buildRecipe();
            routingStatus = 0;
            markRoutingGuiChanged();
            return true;
        } catch (RuntimeException e) {
            LOG.warn("Failed to build Box++ final recipe from {} routings.", routingMap.size(), e);
            return false;
        }
    }

    public void lockRecipeForGui() {
        storeActiveRecipePage();
        if (!recipe.islocked && getTotalConfiguredParallel() <= maxParallel) {
            recipe.islocked = true;
            storeActiveRecipePage();
            markRoutingGuiChanged();
        }
    }

    public void lockRecipeForGui(EntityPlayer player) {
        lockRecipeForGui();
        reopenMainGui(player);
    }

    @Override
    public boolean supportsBatchMode() {
        return false;
    }

    @Override
    public boolean supportsInputSeparation() {
        return false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
    }

    public String getBoxGuiDashboard() {
        return String.join("\n", getBoxGuiSummary()) + "\n"
            + getBoxPatternTypeLine()
            + "\n"
            + getBoxRequiredModuleLine();
    }

    public String getBoxSettlementSummary() {
        return String.join("\n", getBoxGuiSummary()) + "\n"
            + getBoxPatternTypeLine()
            + "\n"
            + i18n("tile.boxplusplus.boxUI.11")
            + ": "
            + recipe.FinalItemInput.size()
            + " / "
            + i18n("tile.boxplusplus.boxUI.13")
            + ": "
            + recipe.FinalItemOutput.size()
            + "\n"
            + i18n("tile.boxplusplus.boxUI.12")
            + ": "
            + recipe.FinalFluidInput.size()
            + " / "
            + i18n("tile.boxplusplus.boxUI.14")
            + ": "
            + recipe.FinalFluidOutput.size()
            + "\n"
            + getBoxRequiredModuleLine();
    }

    public String getBoxModuleSummary() {
        int enabled = 0;
        int active = 0;
        for (int i = 0; i < 14; i++) {
            if (moduleSwitch[i]) enabled++;
            if (moduleActive[i]) active++;
        }
        return enabled + "/14 "
            + i18n(BOX_MUI_MODULE_ENABLED_KEY)
            + "  "
            + active
            + "/14 "
            + i18n(BOX_MUI_MODULE_ACTIVE_KEY)
            + "  "
            + getBoxRequiredModuleLine();
    }

    public String getBoxModuleRow(int index) {
        if (index < 0 || index >= 14) return "";
        String manual = moduleSwitch[index] ? "§a■§r" : "§8□§r";
        String detected = moduleActive[index] ? "§b◆§r" : "§8◇§r";
        String required = "";
        if (recipe.requireModules.containsKey(index)) {
            required = " §e★T" + (recipe.requireModules.get(index) + 1) + "§r";
        }
        return String.format(
            "%02d %s%s T%d %s%s",
            index + 1,
            manual,
            detected,
            moduleTier[index] + 1,
            i18n("tile.boxplusplus.boxUI.module." + (index + 1)),
            required);
    }

    public String getBoxRequiredModuleLine() {
        if (recipe.requireModules.isEmpty()) {
            return i18n("tile.boxplusplus.boxUI.27") + i18n(BOX_MUI_NONE_KEY);
        }
        StringBuilder modules = new StringBuilder(i18n("tile.boxplusplus.boxUI.27"));
        for (int i = 0; i < 15; i++) {
            if (!recipe.requireModules.containsKey(i)) continue;
            modules.append(moduleActive[i] ? "" : "§4")
                .append(i18n("tile.boxplusplus.boxUI.module." + (i + 1)))
                .append(" T")
                .append(recipe.requireModules.get(i) + 1)
                .append(" ")
                .append(" §r| ");
        }
        return modules.toString();
    }

    public String getBoxPatternTypeLine() {
        boolean hasFluid = !recipe.FinalFluidInput.isEmpty() || !recipe.FinalFluidOutput.isEmpty();
        return i18n(BOX_MUI_PATTERN_KEY)
            + (hasFluid ? i18n(BOX_MUI_PATTERN_ULTIMATE_KEY) : i18n(BOX_MUI_PATTERN_NORMAL_KEY));
    }

    public void toggleModuleForGui(int index) {
        if (index < 0 || index >= 14) return;
        setModuleEnabledForGui(index, !moduleSwitch[index]);
    }

    public boolean isModuleEnabledForGui(int index) {
        return index >= 0 && index < 14 && moduleSwitch[index];
    }

    public void setModuleEnabledForGui(int index, boolean enabled) {
        if (index < 0 || index >= 14) return;
        moduleSwitch[index] = enabled;
        if (!enabled) moduleActive[index] = false;
        onMachineBlockUpdate();
    }

    public void enableRequiredModulesForGui(boolean onlyRequired) {
        if (!recipe.islocked && !routingMap.isEmpty()) buildRecipe();
        if (onlyRequired) {
            for (int i = 0; i < 14; i++) {
                moduleSwitch[i] = false;
                moduleActive[i] = false;
            }
        }
        for (Integer module : recipe.requireModules.keySet()) {
            if (module != null && module >= 0 && module < 14) {
                moduleSwitch[module] = true;
            }
        }
        onMachineBlockUpdate();
    }

    public void enableBuiltModulesForGui() {
        for (int i = 0; i < 14; i++) {
            if (getModuleByIndex(i) != null && checkPiece(
                getModuleByIndex(i).name,
                getModuleByIndex(i).horizontalOffset,
                getModuleByIndex(i).verticalOffset,
                getModuleByIndex(i).depthOffset,
                null)) {
                moduleSwitch[i] = true;
            }
        }
        onMachineBlockUpdate();
    }

    public void disableAllModulesForGui() {
        for (int i = 0; i < 14; i++) {
            moduleSwitch[i] = false;
            moduleActive[i] = false;
        }
        onMachineBlockUpdate();
    }

    public String getRoutingListForGui() {
        if (routingMap.isEmpty()) return i18n("tile.boxplusplus.boxUI.47");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < routingMap.size(); i++) {
            BoxRoutings routing = routingMap.get(i);
            String machine = routing.RoutingMachine == null ? "?" : routing.RoutingMachine.getDisplayName();
            if (i > 0) result.append('\n');
            result.append(i + 1)
                .append(". ")
                .append(machine)
                .append("  x")
                .append(routing.Parallel);
        }
        return result.toString();
    }

    public int getRoutingParallelForGui(int oneBasedIndex) {
        int index = oneBasedIndex - 1;
        if (index < 0 || index >= routingMap.size()) return 1;
        return routingMap.get(index).Parallel;
    }

    public void checkNextRoutingForGui() {
        if (recipe.islocked) return;
        BoxRoutings.checkRouting(this);
        markRoutingGuiChanged();
    }

    public void checkNextRoutingForGui(EntityPlayer player) {
        checkNextRoutingForGui();
        reopenMainGui(player);
    }

    public boolean updateRoutingForGui(int oneBasedIndex, int parallel) {
        int index = oneBasedIndex - 1;
        if (recipe.islocked || index < 0 || index >= routingMap.size() || parallel < 1) return false;
        long totalAfterUpdate = getTotalConfiguredParallel() - routingMap.get(index).Parallel + parallel;
        if (totalAfterUpdate > maxParallel) return false;
        routingMap.get(index).Parallel = parallel;
        recipe = new BoxRecipe();
        routingStatus = 0;
        markRoutingGuiChanged();
        return true;
    }

    public void updateRoutingParallelDirectForGui(int oneBasedIndex, int parallel) {
        updateRoutingForGui(oneBasedIndex, parallel);
    }

    public boolean removeRoutingForGui(int oneBasedIndex) {
        int index = oneBasedIndex - 1;
        if (recipe.islocked || index < 0 || index >= routingMap.size()) return false;
        routingMap.remove(index);
        recipe = new BoxRecipe();
        routingStatus = 0;
        markRoutingGuiChanged();
        return true;
    }

    public void addRoutingForGui(BoxRoutings routing) {
        if (recipe.islocked || routing == null) return;
        if (getTotalConfiguredParallel() + Math.max(1, routing.Parallel) > maxParallel) return;
        routingMap.add(routing);
        recipe = new BoxRecipe();
        routingStatus = 0;
        markRoutingGuiChanged();
    }

    public String getRoutingConfigForGui() {
        return serialize(getRoutingSnapshotForGui());
    }

    public NBTTagCompound getRoutingSnapshotForGui() {
        NBTTagCompound routing = new NBTTagCompound();
        routing.setInteger("TotalRouting", routingMap.size());
        for (int i = 0; i < routingMap.size(); i++) {
            routing.setTag(
                "Routing" + (i + 1),
                routingMap.get(i)
                    .routingToUNbt());
        }
        return routing;
    }

    /** Applies the server routing snapshot to the client-side MTE mirror used by the MUI2 widget tree. */
    public void applyRoutingMirrorForGui(String serialized) {
        applyRoutingMirrorForGui(deserialize(serialized));
    }

    /** Applies an authoritative packet snapshot after returning from the client-only NEI recipe screen. */
    public void applyRoutingMirrorForGui(NBTTagCompound routing) {
        if (routing == null) return;
        int total = routing.getInteger("TotalRouting");
        if (total < 0 || total > 999) return;
        List<BoxRoutings> mirrored = new ArrayList<>(total);
        try {
            for (int i = 0; i < total; i++) {
                mirrored.add(new BoxRoutings(routing.getCompoundTag("Routing" + (i + 1)), true));
            }
        } catch (RuntimeException ignored) {
            return;
        }
        routingMap.clear();
        routingMap.addAll(mirrored);
    }

    public String getFinalRecipeConfigForGui() {
        return serialize(recipe.RecipeToNBT());
    }

    /** Applies the authoritative final recipe to the client mirror so item and fluid widgets receive real stacks. */
    public void applyFinalRecipeMirrorForGui(String serialized) {
        applyFinalRecipeMirrorForGui(deserialize(serialized));
    }

    public void applyFinalRecipeMirrorForGui(NBTTagCompound finalRecipe) {
        if (finalRecipe == null) return;
        try {
            recipe = new BoxRecipe(finalRecipe);
        } catch (RuntimeException ignored) {
            // Keep the last valid client mirror if a partial sync packet cannot be decoded.
        }
    }

    public boolean importRoutingConfigForGui(String serialized) {
        NBTTagCompound routing = deserialize(serialized);
        if (routing == null) return false;
        int total = routing.getInteger("TotalRouting");
        if (total < 0 || total > 999 || maxRouting > 0 && total > maxRouting) return false;
        List<BoxRoutings> imported = new ArrayList<>(total);
        try {
            for (int i = 0; i < total; i++) {
                imported.add(new BoxRoutings(routing.getCompoundTag("Routing" + (i + 1)), true));
            }
        } catch (RuntimeException ignored) {
            return false;
        }
        routingMap.clear();
        routingMap.addAll(imported);
        recipe = new BoxRecipe();
        routingStatus = 0;
        markRoutingGuiChanged();
        return true;
    }

    public boolean importRoutingConfigForGui(String serialized, EntityPlayer player) {
        NBTTagCompound routing = deserialize(serialized);
        try {
            if (routing == null) {
                if (player != null)
                    player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.chatmessage.6")));
                return false;
            }
            int total = routing.getInteger("TotalRouting");
            if (total < 0 || total > 999 || maxRouting > 0 && total > maxRouting) {
                routingStatus = 8;
                markRoutingGuiChanged();
                if (player != null)
                    player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.chatmessage.4")));
                return false;
            }
            List<BoxRoutings> imported = new ArrayList<>(total);
            for (int i = 0; i < total; i++) {
                imported.add(new BoxRoutings(routing.getCompoundTag("Routing" + (i + 1)), true));
            }
            routingMap.clear();
            routingMap.addAll(imported);
            recipe = new BoxRecipe();
            routingStatus = 0;
            markRoutingGuiChanged();
            if (player != null) {
                player.addChatMessage(
                    new ChatComponentText(
                        i18n("tile.boxplusplus.chatmessage.5").replaceFirst("%count", String.valueOf(total))));
            }
            return true;
        } catch (RuntimeException ignored) {
            if (player != null) player.addChatMessage(new ChatComponentText(i18n("tile.boxplusplus.chatmessage.6")));
            return false;
        } finally {
            if (player != null) reopenMainGui(player);
        }
    }

    public String getBoxRecipeDetailsForGui() {
        StringBuilder result = new StringBuilder(getBoxSettlementSummary());
        appendItemsForGui(result, i18n("tile.boxplusplus.boxUI.11"), recipe.FinalItemInput);
        appendFluidsForGui(result, i18n("tile.boxplusplus.boxUI.12"), recipe.FinalFluidInput);
        appendItemsForGui(result, i18n("tile.boxplusplus.boxUI.13"), recipe.FinalItemOutput);
        appendFluidsForGui(result, i18n("tile.boxplusplus.boxUI.14"), recipe.FinalFluidOutput);
        return result.toString();
    }

    private void appendItemsForGui(StringBuilder result, String heading, List<ItemStack> stacks) {
        result.append('\n')
            .append(heading)
            .append(": ");
        if (stacks.isEmpty()) {
            result.append('-');
            return;
        }
        int shown = 0;
        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            if (shown++ > 0) result.append(", ");
            result.append(stack.getDisplayName())
                .append(" x")
                .append(stack.stackSize);
            if (shown == 4 && stacks.size() > shown) {
                result.append(" ...");
                break;
            }
        }
    }

    private void appendFluidsForGui(StringBuilder result, String heading, List<FluidStack> stacks) {
        result.append('\n')
            .append(heading)
            .append(": ");
        if (stacks.isEmpty()) {
            result.append('-');
            return;
        }
        int shown = 0;
        for (FluidStack stack : stacks) {
            if (stack == null) continue;
            if (shown++ > 0) result.append(", ");
            result.append(stack.getLocalizedName())
                .append(" ")
                .append(stack.amount)
                .append("L");
            if (shown == 4 && stacks.size() > shown) {
                result.append(" ...");
                break;
            }
        }
    }

    /**
     * Build final recipe, but not write it.
     */
    public void buildRecipe() {
        ItemContainer inputItemContainer = new ItemContainer();
        ItemContainer outputItemContainer = new ItemContainer();
        FluidContainer inputFluidContainer = new FluidContainer();
        FluidContainer OutputFluidContainer = new FluidContainer();
        recipe = new BoxRecipe();
        routingMap.forEach(boxRoutings -> {
            if (boxRoutings == null) return;
            inputItemContainer.addItemStackList(boxRoutings.InputItem, boxRoutings.Parallel);
            outputItemContainer
                .addItemStackList(boxRoutings.OutputItem, boxRoutings.OutputChance, boxRoutings.Parallel);
            inputFluidContainer.addFluidStackList(boxRoutings.InputFluid, boxRoutings.Parallel);
            OutputFluidContainer.addFluidStackList(boxRoutings.OutputFluid, boxRoutings.Parallel);
            recipe.FinalTime += boxRoutings.time * 5000L / (1 + Math.exp(-(boxRoutings.Parallel - 2000) / 320.0));
            recipe.FinalVoteage += boxRoutings.voltage == null ? 0L : boxRoutings.voltage;
            recipe.parallel += boxRoutings.Parallel;
            int[] machine = transMachinesToModule(boxRoutings);
            if (!recipe.requireModules.containsKey(machine[0]) || recipe.requireModules.get(machine[0]) < machine[1])
                recipe.requireModules.put(machine[0], machine[1]);
        });
        recipe.FinalItemInput = inputItemContainer.getItemStack();
        recipe.FinalItemOutput = outputItemContainer.getItemStack();
        recipe.FinalFluidInput = inputFluidContainer.getFluidStack();
        recipe.FinalFluidOutput = OutputFluidContainer.getFluidStack();
        BoxRecipe.ItemOnBox(recipe.FinalItemInput, recipe.FinalItemOutput);
        BoxRecipe.FluidOnBox(recipe.FinalFluidInput, recipe.FinalFluidOutput);
        if (recipe.parallel > 99900000) recipe.requireModules.put(13, 2);
        else if (recipe.parallel > 1280000) recipe.requireModules.put(13, 1);
        else if (recipe.parallel > 128000) recipe.requireModules.put(13, 0);
    }

    private boolean doubleRecipe() {
        long currentPage = 0;
        for (BoxRoutings r : routingMap) currentPage += r.Parallel;
        if (getTotalConfiguredParallel() + currentPage > maxParallel) return false;
        for (BoxRoutings r : routingMap) {
            r.Parallel *= 2;
        }
        return true;
    }

    private long getTotalConfiguredParallel() {
        if (parallelUsageRevision == routingGuiRevision) return cachedConfiguredParallel;
        storeActiveRecipePage();
        long total = 0;
        for (RecipePageState page : recipePages) {
            for (BoxRoutings routing : page.routings) {
                if (routing != null) total += Math.max(0, routing.Parallel);
            }
        }
        cachedConfiguredParallel = total;
        parallelUsageRevision = routingGuiRevision;
        return cachedConfiguredParallel;
    }

    private boolean halveRecipe() {
        for (BoxRoutings r : routingMap) {
            if ((r.Parallel & 1) == 1) return false;
        }
        for (BoxRoutings r : routingMap) {
            r.Parallel /= 2;
        }
        return true;
    }

    @Override
    public void onRemoval() {
        if (boxMap.containsValue(this)) boxMap.entrySet()
            .removeIf(
                t -> t.getValue()
                    .equals(this));
    }

    /**
     * We have many things need to store...
     */
    @Override
    public void saveNBTData(NBTTagCompound NBT) {
        super.saveNBTData(NBT);
        storeActiveRecipePage();
        NBT.setBoolean("Debug", debug);
        NBTTagCompound Routing = writeSavedRouting(routingMap);
        NBTTagCompound savedPages = new NBTTagCompound();
        savedPages.setInteger("PageCount", recipePages.size());
        savedPages.setInteger("ActivePage", routingPageCode);
        for (int i = 0; i < recipePages.size(); i++) {
            RecipePageState page = recipePages.get(i);
            NBTTagCompound savedPage = new NBTTagCompound();
            savedPage.setInteger("Status", page.status);
            savedPage.setTag("Routing", writeSavedRouting(page.routings));
            savedPage.setTag("BoxRecipe", page.recipe.RecipeToNBT());
            savedPages.setTag("Page" + (i + 1), savedPage);
        }
        NBTTagCompound nbtModuleSwitch = new NBTTagCompound();
        for (int i = 0; i < 14; i++) {
            nbtModuleSwitch.setBoolean(String.valueOf(i), moduleSwitch[i]);
        }
        NBTTagCompound nbtModuleActive = new NBTTagCompound();
        for (int i = 0; i < 14; i++) {
            nbtModuleActive.setBoolean(String.valueOf(i), moduleActive[i]);
        }
        NBT.setInteger("RingCountSet", ringCountSet);
        NBT.setInteger("RingCount", ringCount);
        NBT.setLong("maxParallel", maxParallel);
        NBT.setLong("maxRouting", maxRouting);
        NBT.setTag("ModuleSwitch", nbtModuleSwitch);
        NBT.setTag("ModuleActive", nbtModuleActive);
        NBT.setInteger("Status", routingStatus);
        NBT.setTag("Routing", Routing);
        NBT.setTag("BoxRecipe", recipe.RecipeToNBT());
        NBT.setTag("RecipePages", savedPages);
    }

    private NBTTagCompound writeSavedRouting(List<BoxRoutings> routings) {
        NBTTagCompound saved = new NBTTagCompound();
        saved.setInteger("ActiveRouting", routings.size());
        for (int i = 0; i < routings.size(); i++) {
            saved.setTag(
                "Routing" + (i + 1),
                routings.get(i)
                    .routingToNbt());
        }
        return saved;
    }

    private RecipePageState readSavedPage(NBTTagCompound savedPage) {
        RecipePageState page = new RecipePageState();
        NBTTagCompound savedRouting = savedPage.getCompoundTag("Routing");
        int count = Math.max(0, Math.min(999, savedRouting.getInteger("ActiveRouting")));
        for (int i = 0; i < count; i++) {
            page.routings.add(new BoxRoutings(savedRouting.getCompoundTag("Routing" + (i + 1))));
        }
        page.status = savedPage.getInteger("Status");
        page.recipe = new BoxRecipe(savedPage.getCompoundTag("BoxRecipe"));
        return page;
    }

    /**
     * We have many things need to read...
     */
    @Override
    public void loadNBTData(final NBTTagCompound NBT) {
        super.loadNBTData(NBT);
        debug = NBT.getBoolean("Debug");
        for (int i = 0; i < 14; i++) {
            moduleSwitch[i] = NBT.getCompoundTag("ModuleSwitch")
                .getBoolean(String.valueOf(i));
        }
        for (int i = 0; i < 14; i++) {
            moduleActive[i] = NBT.getCompoundTag("ModuleActive")
                .getBoolean(String.valueOf(i));
        }
        ringCount = NBT.getInteger("RingCount");
        ringCountSet = NBT.getInteger("RingCountSet");
        maxParallel = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, NBT.getLong("maxParallel")));
        maxRouting = (int) Math.min(Integer.MAX_VALUE, Math.max(0L, NBT.getLong("maxRouting")));

        recipePages.clear();
        NBTTagCompound savedPages = NBT.getCompoundTag("RecipePages");
        int pageCount = Math.max(0, Math.min(99, savedPages.getInteger("PageCount")));
        if (NBT.hasKey("RecipePages") && pageCount > 0) {
            for (int i = 0; i < pageCount; i++) {
                recipePages.add(readSavedPage(savedPages.getCompoundTag("Page" + (i + 1))));
            }
            routingPageCode = Math.max(1, Math.min(pageCount, savedPages.getInteger("ActivePage")));
        } else {
            NBTTagCompound legacyPage = new NBTTagCompound();
            legacyPage.setInteger("Status", NBT.getInteger("Status"));
            legacyPage.setTag("Routing", NBT.getCompoundTag("Routing"));
            legacyPage.setTag("BoxRecipe", NBT.getCompoundTag("BoxRecipe"));
            recipePages.add(readSavedPage(legacyPage));
            routingPageCode = 1;
        }
        routingMap.clear();
        recipe = new BoxRecipe();
        routingStatus = 0;
        loadRecipePage(routingPageCode - 1);
    }

    @Override
    protected boolean useMui2() {
        return true;
    }
}
