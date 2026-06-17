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
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialMixer;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialMixerActive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

// #tr NameLargeSteamMixer
// # Large Steam Mixer
// # zh_CN 大型蒸汽搅拌机

// #tr LargeSteamMixerRecipeType
// # Mixer
// # zh_CN 搅拌机

// #tr Tooltip_LargeSteamMixer_00
// # A large steam-powered mixer
// # zh_CN 大型蒸汽搅拌机

// #tr Tooltip_LargeSteamMixer_01
// # Bronze machine recipe tier: MV, Steel machine recipe tier: HV
// # zh_CN 青铜机器配方等级:MV 钢机器配方等级:HV

// #tr Tooltip_LargeSteamMixer_02
// # Insert Stainless Steel gear into controller for recipe tier +1
// # zh_CN 在主机里插入不锈钢齿轮配方等级+1

// #tr Tooltip_LargeSteamMixer_Casing
// # Machine casing
// # zh_CN 机器外壳

public class LargeSteamMixer extends GTNCSteamMultiBlockBase<LargeSteamMixer> implements ISurvivalConstructable {

    public LargeSteamMixer(String aName) {
        super(aName);
    }

    public LargeSteamMixer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamMixer(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamMixerRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;
    private int tierMachine = 1;
    private int tierMachineCasing = -1;
    private int tierGearCasing = -1;
    private int tierPipeCasing = -1;
    private int tierFrame = -1;
    private boolean enableHigherRecipe = false;

    private IStructureDefinition<LargeSteamMixer> STRUCTURE_DEFINITION = null;

    // 7 wide (x), 7 tall (y), 7 deep (z)
    // 'A' = tiered casing + hatches, 'B' = gear, 'C' = pipe, 'D' = frame, 'E' = glass
    // GT-NL 'D' was firebox casing → mapped to frame, 'F' industrial casing → mapped to 'A'
    private final String[][] shape = new String[][] {
        { "  DDD  ", " DAAAD ", "DAAAAAD", "DAAAAAD", "DAAAAAD", " DAAAD ", "  DDD  " },
        { "  EAE  ", " ABABA ", "EA   AE", "AB C BA", "EA   AE", " ABABA ", "  EAE  " },
        { "  EAE  ", " ABABA ", "EACCCAE", "ABCCCBA", "EACCCAE", " ABABA ", "  EAE  " },
        { "  E~E  ", " ABABA ", "EAB BAE", "AB B BA", "EAB BAE", " ABABA ", "  EAE  " },
        { "  EAE  ", " ABABA ", "EACCCAE", "ABCCCBA", "EACCCAE", " ABABA ", "  EAE  " },
        { "  EAE  ", " ABABA ", "EA C AE", "ABCCCBA", "EA C AE", " ABABA ", "  EAE  " },
        { "  DDD  ", " DAAAD ", "DAAAAAD", "DAAAAAD", "DAAAAAD", " DAAAD ", "  DDD  " } };

    @Override
    protected boolean isHighPressure() {
        return tierMachineCasing == 2 || tierGearCasing == 2 || tierPipeCasing == 2 || tierFrame == 2;
    }

    @Override
    public void onValueUpdate(byte aValue) {
        tierMachineCasing = aValue;
    }

    @Override
    public byte getUpdateData() {
        return (byte) tierMachineCasing;
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return oMCDIndustrialMixer;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return oMCDIndustrialMixerActive;
    }

    @Override
    public IStructureDefinition<LargeSteamMixer> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamMixer>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'A',
                    ofChain(
                        buildSteamInput(LargeSteamMixer.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamMixer.class)
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
                .addElement(
                    'B',
                    ofBlocksTiered(
                        LargeSteamMixer::getTierGearCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 2), Pair.of(sBlockCasings2, 3)),
                        -1,
                        (t, m) -> t.tierGearCasing = m,
                        t -> t.tierGearCasing))
                .addElement(
                    'C',
                    ofBlocksTiered(
                        LargeSteamMixer::getTierPipeCasing,
                        ImmutableList.of(Pair.of(sBlockCasings2, 12), Pair.of(sBlockCasings2, 13)),
                        -1,
                        (t, m) -> t.tierPipeCasing = m,
                        t -> t.tierPipeCasing))
                .addElement(
                    'D',
                    ofBlocksTiered(
                        LargeSteamMixer::getTierFrame,
                        ImmutableList.of(
                            Pair.of(sBlockFrames, Materials.Bronze.mMetaItemSubID),
                            Pair.of(sBlockFrames, Materials.Steel.mMetaItemSubID)),
                        -1,
                        (t, m) -> t.tierFrame = m,
                        t -> t.tierFrame))
                .addElement('E', chainAllGlasses())
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

    @Nullable
    public static Integer getTierPipeCasing(Block block, int meta) {
        if (block == sBlockCasings2 && 12 == meta) return 1;
        if (block == sBlockCasings2 && 13 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierFrame(Block block, int meta) {
        if (block == sBlockFrames) {
            if (meta == Materials.Bronze.mMetaItemSubID) return 1;
            if (meta == Materials.Steel.mMetaItemSubID) return 2;
        }
        return null;
    }

    @Nullable
    public static Integer getTierGearCasing(Block block, int meta) {
        if (block == sBlockCasings2 && 2 == meta) return 1;
        if (block == sBlockCasings2 && 3 == meta) return 2;
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
            updateHatchTexture();
        } else {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }
        checkCasingMin(errors, mCountCasing, 30);
        checkHasInputBus(errors);
        checkHasInputHatch(errors);
        checkHasOutputBus(errors);
        checkHasOutputHatch(errors);
        enableHigherRecipe = getUpgradeTier(getControllerSlot());
    }

    public boolean getUpgradeTier(ItemStack inventory) {
        if (inventory == null) return false;
        return inventory.isItemEqual(GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 1L));
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        ItemStack controllerItem = getControllerSlot();
        enableHigherRecipe = getUpgradeTier(controllerItem);
        return super.checkProcessing();
    }

    @Override
    protected void updateHatchTexture() {
        super.updateHatchTexture();
        int id = getCasingTextureId();
        for (MTEHatch h : mInputBusses) h.updateTexture(id);
        for (MTEHatch h : mOutputBusses) h.updateTexture(id);
        for (IDualInputHatch h : mDualInputHatches) h.updateTexture(id);
    }

    @Override
    public int getMaxParallelRecipes() {
        if (tierMachine == 2) {
            return enableHigherRecipe ? 32 : 16;
        }
        return enableHigherRecipe ? 16 : 8;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTPPRecipeMaps.mixerNonCellRecipes;
    }

    @Override
    public int getTierRecipes() {
        return tierMachine + 1 + (enableHigherRecipe ? 1 : 0);
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(
            StatCollector.translateToLocalFormatted(
                "gtpp.infodata.multi.steam.tier",
                "" + EnumChatFormatting.YELLOW + tierMachine));
        info.add(
            StatCollector.translateToLocalFormatted(
                "gtpp.infodata.multi.steam.parallel",
                "" + EnumChatFormatting.YELLOW + getMaxParallelRecipes()));
        return info.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        currenttip.add(
            StatCollector.translateToLocal("GTPP.machines.tier") + ": "
                + EnumChatFormatting.YELLOW
                + getSteamTierTextForWaila(tag)
                + EnumChatFormatting.RESET);
        currenttip.add(
            StatCollector.translateToLocal("GT5U.multiblock.curparallelism") + ": "
                + EnumChatFormatting.BLUE
                + tag.getInteger("parallel")
                + EnumChatFormatting.RESET);
        currenttip.add(
            StatCollector.translateToLocal("GT5U.multiblock.maxtier") + ": "
                + EnumChatFormatting.YELLOW
                + GTValues.VN[tag.getInteger("tierMachine") + 1 + (tag.getBoolean("enableHigherRecipe") ? 1 : 0)]
                + EnumChatFormatting.RESET);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("tierMachine", tierMachine);
        tag.setInteger("parallel", getTrueParallel());
        tag.setBoolean("enableHigherRecipe", getUpgradeTier(getControllerSlot()));
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("tierMachine", tierMachine);
        aNBT.setInteger("tierMachineCasing", tierMachineCasing);
        aNBT.setInteger("tierGearCasing", tierGearCasing);
        aNBT.setInteger("tierPipeCasing", tierPipeCasing);
        aNBT.setInteger("tierFrame", tierFrame);
        aNBT.setBoolean("enableHigherRecipe", enableHigherRecipe);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachine = aNBT.getInteger("tierMachine");
        tierMachineCasing = aNBT.getInteger("tierMachineCasing");
        tierGearCasing = aNBT.getInteger("tierGearCasing");
        tierPipeCasing = aNBT.getInteger("tierPipeCasing");
        tierFrame = aNBT.getInteger("tierFrame");
        enableHigherRecipe = aNBT.getBoolean("enableHigherRecipe");
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamMixerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_02"))
            .addSteamBulkMachineInfo(8, 2f, 0.75f)
            .addInfo(HIGH_PRESSURE_TOOLTIP_NOTICE)
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .beginStructureBlock(7, 7, 7, false)
            .addController("Front center")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamMixer_Casing"), 1)
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
