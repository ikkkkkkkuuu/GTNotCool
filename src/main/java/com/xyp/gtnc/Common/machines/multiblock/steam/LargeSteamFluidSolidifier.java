package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachine;
import static gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock.oMCDIndustrialCuttingMachineActive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBlockBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

// #tr NameLargeSteamFluidSolidifier
// # Large Steam Fluid Solidifier
// # zh_CN 大型蒸汽流体固化机

// #tr LargeSteamFluidSolidifierRecipeType
// # Fluid Solidifier
// # zh_CN 流体固化机

// #tr Tooltip_LargeSteamFluidSolidifier_00
// # A large steam-powered fluid solidifying machine
// # zh_CN 大型蒸汽流体固化机

// #tr Tooltip_LargeSteamFluidSolidifier_01
// # Bronze machine recipe tier: MV, Steel machine recipe tier: HV
// # zh_CN 青铜机器配方等级:MV 钢机器配方等级:HV

// #tr Tooltip_LargeSteamFluidSolidifier_02
// # Insert Stainless Steel gear into controller for recipe tier +1
// # zh_CN 在主机里插入不锈钢齿轮配方等级+1

// #tr Tooltip_LargeSteamFluidSolidifier_Casing
// # Machine casing
// # zh_CN 机器外壳

public class LargeSteamFluidSolidifier extends MTESteamMultiBlockBase<LargeSteamFluidSolidifier>
    implements ISurvivalConstructable {

    public LargeSteamFluidSolidifier(String aName) {
        super(aName);
    }

    public LargeSteamFluidSolidifier(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeSteamFluidSolidifier(this.mName);
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamFluidSolidifierRecipeType");
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;
    private int tierMachine = 1;
    private int tierMachineCasing = -1;
    private int tierPipeCasing = -1;
    private boolean enableHigherRecipe = false;

    private IStructureDefinition<LargeSteamFluidSolidifier> STRUCTURE_DEFINITION = null;

    // 5 wide (x), 4 tall (y), 5 deep (z)
    // 'B' = tiered casing + hatches, 'C' = pipe casing
    private final String[][] shape = new String[][] { { " BBB ", "BBBBB", "BBBBB", "BBBBB", " BBB " },
        { " BBB ", "BC CB", "B   B", "BC CB", " BBB " }, { " B~B ", "BC CB", "B   B", "BC CB", " BBB " },
        { " BBB ", "BBBBB", "BBBBB", "BBBBB", " BBB " } };

    @Override
    protected boolean isHighPressure() {
        return tierMachineCasing == 2 || tierPipeCasing == 2;
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
        return oMCDIndustrialCuttingMachine;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return oMCDIndustrialCuttingMachineActive;
    }

    @Override
    public IStructureDefinition<LargeSteamFluidSolidifier> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<LargeSteamFluidSolidifier>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement(
                    'B',
                    ofChain(
                        buildSteamInput(LargeSteamFluidSolidifier.class).casingIndex(10)
                            .hint(1)
                            .build(),
                        buildHatchAdder(LargeSteamFluidSolidifier.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                SteamHatchElement.OutputBus_Steam,
                                InputBus,
                                OutputBus,
                                InputHatch)
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
                    'C',
                    ofBlocksTiered(
                        LargeSteamFluidSolidifier::getTierPipeCasing,
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

    @Nullable
    public static Integer getTierPipeCasing(Block block, int meta) {
        if (block == sBlockCasings2 && 12 == meta) return 1;
        if (block == sBlockCasings2 && 13 == meta) return 2;
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
            updateHatchTexture();
        } else {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }
        checkCasingMin(errors, mCountCasing, 3);
        checkHasInputBus(errors);
        checkHasInputHatch(errors);
        checkHasOutputBus(errors);
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
    public ArrayList<FluidStack> getAllSteamStacks() {
        ArrayList<FluidStack> stacks = super.getAllSteamStacks();
        FluidStack steam = Materials.Steam.getGas(1);
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluid : meHatch.getStoredFluids()) {
                    if (fluid != null && fluid.isFluidEqual(steam)) {
                        stacks.add(fluid);
                    }
                }
            }
        }
        return stacks;
    }

    @Override
    public boolean depleteInput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            if (tHatch instanceof MTEHatchInputME meHatch) {
                meHatch.startRecipeProcessing();
                FluidStack drained = meHatch.drain(ForgeDirection.UNKNOWN, aLiquid, false);
                if (drained != null && drained.amount >= aLiquid.amount) {
                    meHatch.drain(ForgeDirection.UNKNOWN, aLiquid, true);
                    meHatch.endRecipeProcessing(this);
                    return true;
                }
                meHatch.endRecipeProcessing(this);
            } else {
                FluidStack tLiquid = tHatch.getFluid();
                if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                    tLiquid = tHatch.drain(aLiquid.amount, false);
                    if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                        tLiquid = tHatch.drain(aLiquid.amount, true);
                        return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int getTotalSteamCapacity() {
        int cap = super.getTotalSteamCapacity();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            cap += tHatch.getCapacity();
        }
        return cap;
    }

    @Override
    public List<IOutputBus> getOutputBusses() {
        List<IOutputBus> output = new ArrayList<>();
        for (MTEHatchOutputBus bus : mSteamOutputs) {
            if (bus.isValid()) output.add(bus);
        }
        for (MTEHatchOutputBus bus : mOutputBusses) {
            if (bus.isValid()) output.add(bus);
        }
        return output;
    }

    @Override
    public int getMaxParallelRecipes() {
        return enableHigherRecipe ? 512 : 256;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.fluidSolidifierRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (availableVoltage < recipe.mEUt) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Override
            @Nonnull
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                int effectiveTier = tierMachine + (enableHigherRecipe ? 1 : 0);
                return OverclockCalculator.ofNoOverclock(recipe)
                    .setEUtDiscount(effectiveTier)
                    .setDurationModifier(0.9 / effectiveTier);
            }
        }.setMaxParallelSupplier(this::getTrueParallel);
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
        aNBT.setInteger("tierPipeCasing", tierPipeCasing);
        aNBT.setBoolean("enableHigherRecipe", enableHigherRecipe);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachine = aNBT.getInteger("tierMachine");
        tierMachineCasing = aNBT.getInteger("tierMachineCasing");
        tierPipeCasing = aNBT.getInteger("tierPipeCasing");
        enableHigherRecipe = aNBT.getBoolean("enableHigherRecipe");
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeSteamFluidSolidifierRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_02"))
            .addSteamBulkMachineInfo(256, 2f, 0.45f)
            .addInfo(HIGH_PRESSURE_TOOLTIP_NOTICE)
            .beginStructureBlock(5, 4, 5, false)
            .addController("Front center")
            .addSteamInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_Casing"), 1)
            .addSteamOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_Casing"), 1)
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamFluidSolidifier_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.IC2_MACHINES_INDUCTION_LOOP;
    }
}
