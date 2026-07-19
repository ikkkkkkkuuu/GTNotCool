package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofHatchAdder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
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
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.tileentities.machines.IDualInputHatch;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

// #tr NameLargeSteamDistillationTower
// # Large Steam Distillation Tower
// # zh_CN 大型蒸汽蒸馏塔

// #tr LargeSteamDistillationTowerRecipeType
// # Distillation Tower
// # zh_CN 蒸馏塔

// #tr Tooltip_LargeSteamDistillationTower_00
// # A large steam-powered distillation tower
// # zh_CN 大型蒸汽蒸馏塔

// #tr Tooltip_LargeSteamDistillationTower_02
// # Fluids are only put out at the correct height
// # zh_CN 流体仅在对应高度输出

// #tr Tooltip_LargeSteamDistillationTower_03
// # The correct height equals the slot number in the NEI recipe
// # zh_CN 正确高度对应NEI配方中的槽位编号

// #tr Tooltip_LargeSteamDistillationTower_Casing
// # Machine casing
// # zh_CN 机器外壳

public class LargeSteamDistillationTower extends GTNCSteamMultiBlockBase<LargeSteamDistillationTower>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_BASE = "base";
    private static final String STRUCTURE_PIECE_LAYER = "layer";
    private static final String STRUCTURE_PIECE_LAYER_HINT = "layerHint";
    private static final String STRUCTURE_PIECE_TOP_HINT = "topHint";

    private int mCountCasing = 0;

    private int mHeight;
    private boolean mTopLayerFound;
    protected final List<List<MTEHatchOutput>> mOutputHatchesByLayer = new ArrayList<>();

    private IStructureDefinition<LargeSteamDistillationTower> STRUCTURE_DEFINITION = null;

    public LargeSteamDistillationTower(String aName) {
        super(aName);
    }

    public LargeSteamDistillationTower(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamDistillationTower(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamDistillationTowerRecipeType");
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE;
    }

    @Override
    public void onValueUpdate(byte aValue) {}

    @Override
    public byte getUpdateData() {
        return 1;
    }

    @Override
    public IStructureDefinition<LargeSteamDistillationTower> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamDistillationTower>builder()
                .addShape(STRUCTURE_PIECE_BASE, transpose(new String[][] { { "b~b", "bbb", "bbb" } }))
                .addShape(STRUCTURE_PIECE_LAYER, transpose(new String[][] { { "lll", "lcl", "lll" } }))
                .addShape(STRUCTURE_PIECE_LAYER_HINT, transpose(new String[][] { { "lll", "l-l", "lll" } }))
                .addShape(STRUCTURE_PIECE_TOP_HINT, transpose(new String[][] { { "LLL", "LcL", "LLL" } }))
                .addElement(
                    'b',
                    ofChain(
                        buildSteamInput(LargeSteamDistillationTower.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamDistillationTower.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch,
                                OutputHatch)
                            .casingIndex(10)
                            .hint(1)
                            .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 10)))))
                .addElement(
                    'l',
                    ofChain(
                        buildHatchAdder(LargeSteamDistillationTower.class)
                            .atLeast(
                                OutputHatch.withCount(LargeSteamDistillationTower::getCurrentLayerOutputHatchCount)
                                    .withAdder(LargeSteamDistillationTower::addLayerOutputHatch))
                            .casingIndex(10)
                            .hint(2)
                            .disallowOnly(ForgeDirection.UP, ForgeDirection.DOWN)
                            .build(),
                        onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 10))))
                .addElement(
                    'L',
                    buildHatchAdder(LargeSteamDistillationTower.class)
                        .atLeast(
                            OutputHatch.withCount(LargeSteamDistillationTower::getCurrentLayerOutputHatchCount)
                                .withAdder(LargeSteamDistillationTower::addLayerOutputHatch))
                        .casingIndex(10)
                        .hint(2)
                        .disallowOnly(ForgeDirection.UP)
                        .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 10))))
                .addElement(
                    'c',
                    ofChain(
                        onElementPass(
                            t -> t.onTopLayerFound(false),
                            ofHatchAdder(LargeSteamDistillationTower::addOutputToMachineList, 10, 3)),
                        onElementPass(t -> t.onTopLayerFound(true), ofBlock(sBlockCasings1, 10)),
                        isAir()))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    private void onCasingFound() {
        mCountCasing++;
    }

    private void onTopLayerFound(boolean aIsCasing) {
        mTopLayerFound = true;
        if (aIsCasing) onCasingFound();
    }

    private boolean addLayerOutputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null || aTileEntity.isDead()
            || !(aTileEntity.getMetaTileEntity() instanceof MTEHatchOutput tHatch)) return false;
        while (mOutputHatchesByLayer.size() < mHeight) mOutputHatchesByLayer.add(new ArrayList<>());
        tHatch.updateTexture(aBaseCasingIndex);
        addToMachineListInternal(mOutputHatches, tHatch, aBaseCasingIndex);
        return mOutputHatchesByLayer.get(mHeight - 1)
            .add(tHatch);
    }

    private int getCurrentLayerOutputHatchCount() {
        return 0;
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
        buildPiece(STRUCTURE_PIECE_BASE, stackSize, hintsOnly, 1, 0, 0);
        int tTotalHeight = GTStructureChannels.STRUCTURE_HEIGHT.getValueClamped(stackSize, 3, 12);
        for (int i = 1; i < tTotalHeight - 1; i++) {
            buildPiece(STRUCTURE_PIECE_LAYER_HINT, stackSize, hintsOnly, 1, i, 0);
        }
        buildPiece(STRUCTURE_PIECE_TOP_HINT, stackSize, hintsOnly, 1, tTotalHeight - 1, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        mHeight = 0;
        int built = survivalBuildPiece(STRUCTURE_PIECE_BASE, stackSize, 1, 0, 0, elementBudget, env, false, true);
        if (built >= 0) return built;
        int tTotalHeight = GTStructureChannels.STRUCTURE_HEIGHT.getValueClamped(stackSize, 3, 12);
        for (int i = 1; i < tTotalHeight - 1; i++) {
            mHeight = i;
            built = survivalBuildPiece(STRUCTURE_PIECE_LAYER_HINT, stackSize, 1, i, 0, elementBudget, env, false, true);
            if (built >= 0) return built;
        }
        mHeight = tTotalHeight - 1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_TOP_HINT,
            stackSize,
            1,
            tTotalHeight - 1,
            0,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
        mTopLayerFound = false;
        mCountCasing = 0;

        if (!checkPiece(STRUCTURE_PIECE_BASE, 1, 0, 0, errors)) return;

        while (mHeight < 12) {
            if (!checkPiece(STRUCTURE_PIECE_LAYER, 1, mHeight, 0, errors)) return;
            if (mTopLayerFound) {
                break;
            }
            mHeight++;
        }

        if (mHeight + 1 < 3) {
            errors.add(StructureErrorRegistry.TOO_SHORT_HEIGHT);
            return;
        }
        if (!mTopLayerFound) {
            return;
        }

        tierMachine = 1;
        updateHatchTexture();

        checkCasingMin(errors, mCountCasing, 7 * (mHeight + 1) - 5);
        enableHigherRecipe = getUpgradeTier(getControllerSlot());
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        return super.checkProcessing();
    }

    @Override
    protected void updateHatchTexture() {
        super.updateHatchTexture();
        int id = getCasingTextureId();
        for (MTEHatch h : mInputBusses) h.updateTexture(id);
        for (MTEHatch h : mOutputBusses) h.updateTexture(id);
        for (IDualInputHatch h : mDualInputHatches) h.updateTexture(id);
        for (List<MTEHatchOutput> layer : mOutputHatchesByLayer) {
            for (MTEHatchOutput h : layer) h.updateTexture(id);
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return enableHigherRecipe ? 64 : 32;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.distillationTowerRecipes;
    }

    @Override
    public int getTierRecipes() {
        return 2 + (enableHigherRecipe ? 1 : 0);
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
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
            StatCollector.translateToLocal("GT5U.multiblock.curparallelism") + ": "
                + EnumChatFormatting.BLUE
                + tag.getInteger("parallel")
                + EnumChatFormatting.RESET);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("parallel", getTrueParallel());
        tag.setBoolean("enableHigherRecipe", getUpgradeTier(getControllerSlot()));
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> (d.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) == 0 && r.isNotRotated()
            && !f.isVerticallyFliped();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return false;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamDistillationTowerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamGearInfo"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamGearInfo_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SteamWirelessMode"))
            .addSteamBulkMachineInfo(4, 2f, 0.35f)
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .beginVariableStructureBlock(3, 3, 3, 12, 3, 3, false)
            .addController("Front bottom center")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamDistillationTower_Casing"), 1, 2, 3)
            .addSubChannelUsage(GTStructureChannels.STRUCTURE_HEIGHT)
            .toolTipFinisher();
        return tt;
    }

    /** Inlined replacement for the removed static dumpFluid from GT5U 5.09.52. */
    private static boolean dumpFluidLocal(List<gregtech.api.metatileentity.implementations.MTEHatchOutput> hatches,
        net.minecraftforge.fluids.FluidStack fluid, boolean restrictive) {
        for (gregtech.api.metatileentity.implementations.MTEHatchOutput hatch : gregtech.api.util.GTUtility
            .filterValidMTEs(hatches)) {
            if (!hatch.canStoreFluid(fluid)) continue;
            int filled = hatch.fill(fluid, false);
            if (filled >= fluid.amount) {
                hatch.fill(fluid, true);
                return true;
            } else if (filled > 0) {
                fluid.amount -= hatch.fill(fluid, true);
            }
        }
        return false;
    }

    @Override
    protected boolean addFluidOutputs(FluidStack[] outputFluids) {
        boolean dumped = false;
        for (int i = 0; i < outputFluids.length && i < mOutputHatchesByLayer.size(); i++) {
            final FluidStack fluidStack = outputFluids[i];
            if (fluidStack == null) continue;
            FluidStack tStack = fluidStack.copy();
            if (!dumpFluidLocal(mOutputHatchesByLayer.get(i), tStack, true))
                dumpFluidLocal(mOutputHatchesByLayer.get(i), tStack, false);
            dumped = true;
        }
        return dumped;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
        mTopLayerFound = false;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}
