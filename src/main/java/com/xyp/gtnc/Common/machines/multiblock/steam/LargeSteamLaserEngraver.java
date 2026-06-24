package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachine;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachineActive;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachineActiveGlow;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachineGlow;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

// #tr NameLargeSteamLaserEngraver
// # Large Steam Laser Engraver
// # zh_CN 大型蒸汽激光雕刻机

// #tr LargeSteamLaserEngraverRecipeType
// # Laser Engraver
// # zh_CN 激光雕刻机

// #tr Tooltip_LargeSteamLaserEngraver_00
// # A large steam-powered laser engraver
// # zh_CN 大型蒸汽激光雕刻机

// #tr Tooltip_LargeSteamLaserEngraver_Casing
// # Machine casing
// # zh_CN 机器外壳

public class LargeSteamLaserEngraver extends GTNCSteamMultiBlockBase<LargeSteamLaserEngraver>
    implements ISurvivalConstructable {

    public LargeSteamLaserEngraver(String aName) {
        super(aName);
    }

    public LargeSteamLaserEngraver(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamLaserEngraver(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamLaserEngraverRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;
    private int tierMachineCasing = -1;
    private int tierPipeCasing = -1;

    private IStructureDefinition<LargeSteamLaserEngraver> STRUCTURE_DEFINITION = null;

    // 5 wide (x), 4 tall (y), 5 deep (z)
    // 'A' = glass, 'B' = tiered casing + hatches, 'C' = pipe casing
    private final String[][] shape = new String[][] { { "BBBBB", "BBBBB", "BBBBB", "BBBBB", "BBBBB" },
        { "BAAAB", "BCCCB", "BCCCB", "BCCCB", "BAAAB" }, { "BAAAB", "BCCCB", "BCCCB", "BCCCB", "BAAAB" },
        { "BB~BB", "BBBBB", "BBBBB", "BBBBB", "BBBBB" } };

    @Override
    protected IIconContainer getInactiveOverlay() {
        return oMCDIndustrialCuttingMachine;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return oMCDIndustrialCuttingMachineActive;
    }

    @Nullable
    @Override
    protected IIconContainer getActiveGlowOverlay() {
        return oMCDIndustrialCuttingMachineActiveGlow;
    }

    @Nullable
    @Override
    protected IIconContainer getInactiveGlowOverlay() {
        return oMCDIndustrialCuttingMachineGlow;
    }

    @Override
    public IStructureDefinition<LargeSteamLaserEngraver> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamLaserEngraver>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'B',
                    ofChain(
                        buildSteamInput(LargeSteamLaserEngraver.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamLaserEngraver.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                InputHatch,
                                OutputBus)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(),
                        ofBlocksTiered(
                            this::getTierMachineCasing,
                            ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                            -1,
                            (t, m) -> t.tierMachineCasing = m,
                            t -> t.tierMachineCasing)))
                .addElement('A', chainAllGlasses())
                .addElement(
                    'C',
                    ofBlocksTiered(
                        LargeSteamLaserEngraver::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Nullable
    public Integer getTierMachineCasing(Block block, int meta) {
        if (block == sBlockCasings1 && 10 == meta) {
            mCountCasing++;
            return 1;
        }
        if (block == sBlockCasings2 && 0 == meta) {
            mCountCasing++;
            return 2;
        }
        return null;
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
        mCountCasing = 0;
        tierMachineCasing = -1;
        tierPipeCasing = -1;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        if (tierMachineCasing >= 1 && tierMachineCasing == tierPipeCasing) {
            tierMachine = tierMachineCasing;
            syncTierValue = tierMachineCasing;
            updateHatchTexture();
        } else {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }
        checkCasingMin(errors, mCountCasing, 3);
        enableHigherRecipe = getUpgradeTier(getControllerSlot());
    }

    @Override
    public int getMaxParallelRecipes() {
        return enableHigherRecipe ? 16 : 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.laserEngraverRecipes;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("tierMachineCasing", tierMachineCasing);
        aNBT.setInteger("tierPipeCasing", tierPipeCasing);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachineCasing = aNBT.getInteger("tierMachineCasing");
        tierPipeCasing = aNBT.getInteger("tierPipeCasing");
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamLaserEngraverRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamLaserEngraver_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamTierInfo"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamGearInfo"))
            .addSteamBulkMachineInfo(8, 1.25f, 0.625f)
            .addInfo(HIGH_PRESSURE_TOOLTIP_NOTICE)
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .beginStructureBlock(5, 4, 5, false)
            .addController("Front center")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamLaserEngraver_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamLaserEngraver_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamLaserEngraver_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamLaserEngraver_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamLaserEngraver_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_INDUCTION_LOOP;
    }
}
