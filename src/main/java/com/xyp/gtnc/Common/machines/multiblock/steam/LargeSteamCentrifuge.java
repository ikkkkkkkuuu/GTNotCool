package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockFrames;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialThermalCentrifuge;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialThermalCentrifugeActive;

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
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

// #tr NameLargeSteamCentrifuge
// # Large Steam Centrifuge
// # zh_CN 大型蒸汽离心机

// #tr LargeSteamCentrifugeRecipeType
// # Centrifuge
// # zh_CN 离心机

// #tr Tooltip_LargeSteamCentrifuge_00
// # A large steam-powered centrifuge
// # zh_CN 大型蒸汽离心机

// #tr Tooltip_LargeSteamCentrifuge_Casing
// # Machine casing
// # zh_CN 机器外壳

public class LargeSteamCentrifuge extends GTNCSteamMultiBlockBase<LargeSteamCentrifuge>
    implements ISurvivalConstructable {

    public LargeSteamCentrifuge(String aName) {
        super(aName);
    }

    public LargeSteamCentrifuge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamCentrifuge(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCentrifugeRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;
    private int tierMachineCasing = -1;
    private int tierGearCasing = -1;
    private int tierPipeCasing = -1;
    private int tierFrame = -1;

    private IStructureDefinition<LargeSteamCentrifuge> STRUCTURE_DEFINITION = null;

    // 7 wide (x), 10 tall (y), 7 deep (z)
    // 'A' = glass, 'B' = tiered casing + hatches, 'C' = pipe casing, 'D' = frame, 'G' = gear casing
    private static final String[][] shape = new String[][] {
        { "       ", "  BBB  ", " BBBBB ", " BBGBB ", " BBBBB ", "  BBB  ", "       " },
        { "  BBB  ", " BBBBB ", "BB   BB", "B     B", "BB   BB", " BBBBB ", "  BBB  " },
        { "  BAB  ", " DC CD ", "BC   CB", "A     A", "BC   CB", " DC CD ", "  BAB  " },
        { "  BAB  ", " DG GD ", "BG   GB", "A     A", "BG   GB", " DG GD ", "  BAB  " },
        { "  BAB  ", " DC CD ", "BC   CB", "A     A", "BC   CB", " DC CD ", "  BAB  " },
        { "  BAB  ", " DG GD ", "BG   GB", "A     A", "BG   GB", " DG GD ", "  BAB  " },
        { "  BAB  ", " DC CD ", "BC   CB", "A     A", "BC   CB", " DC CD ", "  BAB  " },
        { "  BAB  ", " DG GD ", "BG   GB", "A     A", "BG   GB", " DG GD ", "  BAB  " },
        { "  BAB  ", " DC CD ", "BC   CB", "A     A", "BC   CB", " DC CD ", "  BAB  " },
        { "  B~B  ", " BBBBB ", "BBBBBBB", "BBBBBBB", "BBBBBBB", " BBBBB ", "  BBB  " } };

    @Override
    protected IIconContainer getInactiveOverlay() {
        return oMCDIndustrialThermalCentrifuge;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return oMCDIndustrialThermalCentrifugeActive;
    }

    @Override
    public IStructureDefinition<LargeSteamCentrifuge> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamCentrifuge>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'B',
                    ofChain(
                        buildSteamInput(LargeSteamCentrifuge.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamCentrifuge.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch,
                                OutputHatch)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(),
                        onElementPass(
                            x -> ++x.mCountCasing,
                            ofBlocksTiered(
                                this::getTierMachineCasing,
                                ImmutableList.of(Pair.of(sBlockCasings1, 10), Pair.of(sBlockCasings2, 0)),
                                -1,
                                (t, m) -> t.tierMachineCasing = m,
                                t -> t.tierMachineCasing))))
                .addElement('A', chainAllGlasses())
                .addElement(
                    'C',
                    ofBlocksTiered(
                        LargeSteamCentrifuge::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing))
                .addElement(
                    'D',
                    ofBlocksTiered(
                        LargeSteamCentrifuge::getTierFrame,
                        ImmutableList.of(
                            Pair.of(sBlockFrames, Materials.Bronze.mMetaItemSubID),
                            Pair.of(sBlockFrames, Materials.Steel.mMetaItemSubID)),
                        -1,
                        (t, m) -> t.tierFrame = m,
                        t -> t.tierFrame))
                .addElement(
                    'G',
                    ofBlocksTiered(
                        LargeSteamCentrifuge::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing))
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
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (super.addToMachineList(aTileEntity, aBaseCasingIndex)) return true;
        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof MTEHatchInputBus inputBus) {
            return addToMachineListInternal(mInputBusses, inputBus, aBaseCasingIndex);
        }
        if (aMetaTileEntity instanceof IDualInputHatch dualHatch) {
            dualHatch.updateTexture(aBaseCasingIndex);
            dualHatch.updateCraftingIcon(this.getMachineCraftingIcon());
            return mDualInputHatches.add(dualHatch);
        }
        if (aMetaTileEntity instanceof MTEHatchOutput outputHatch)
            return addToMachineListInternal(mOutputHatches, outputHatch, aBaseCasingIndex);
        if (aMetaTileEntity instanceof MTEHatchOutputBus outputBus)
            return addToMachineListInternal(mOutputBusses, outputBus, aBaseCasingIndex);
        return false;
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
        tierGearCasing = -1;
        tierPipeCasing = -1;
        tierFrame = -1;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        if (tierMachineCasing >= 1 && tierMachineCasing == tierGearCasing
            && tierMachineCasing == tierPipeCasing
            && tierMachineCasing == tierFrame) {
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
        if (tierMachine == 2) return enableHigherRecipe ? 64 : 32;
        return enableHigherRecipe ? 32 : 16;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.centrifugeNonCellRecipes;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("tierMachineCasing", tierMachineCasing);
        aNBT.setInteger("tierGearCasing", tierGearCasing);
        aNBT.setInteger("tierPipeCasing", tierPipeCasing);
        aNBT.setInteger("tierFrame", tierFrame);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachineCasing = aNBT.getInteger("tierMachineCasing");
        tierGearCasing = aNBT.getInteger("tierGearCasing");
        tierPipeCasing = aNBT.getInteger("tierPipeCasing");
        tierFrame = aNBT.getInteger("tierFrame");
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamCentrifugeRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamTierInfo"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamGearInfo"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamGearInfo_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamWirelessMode"))
            .addSteamBulkMachineInfo(16, 2f, 0.35f)
            .addInfo(HIGH_PRESSURE_TOOLTIP_NOTICE)
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .beginStructureBlock(7, 10, 7, false)
            .addController("Front center")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCentrifuge_Casing"), 1)
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
