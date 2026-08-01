package com.xyp.gtnc.Common.machines.multiblock;

import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static com.xyp.gtnc.utils.lang.TextLocalization.PETRO_00;
import static com.xyp.gtnc.utils.lang.TextLocalization.PETRO_01;
import static com.xyp.gtnc.utils.lang.TextLocalization.PETRO_02;
import static com.xyp.gtnc.utils.lang.TextLocalization.PETRO_CASING;
import static com.xyp.gtnc.utils.lang.TextLocalization.PETRO_MUFFLER;
import static com.xyp.gtnc.utils.lang.TextLocalization.PETRO_RECIPE_TYPE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.StructureUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;
import kekztech.common.Blocks;

@IMetaTileEntity.SkipGenerateDescription
public class PetrochemicalPlant extends GTNCMultiBlockBase<PetrochemicalPlant> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/petrochemical_plant";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PP_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 22;
    private static final int VERTICAL_OFF_SET = 56;
    private static final int DEPTH_OFF_SET = 0;

    private int casingCount;
    private gregtech.api.enums.HeatingCoilLevel coilLevel = gregtech.api.enums.HeatingCoilLevel.None;

    public PetrochemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PetrochemicalPlant(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PetrochemicalPlant(this.mName);
    }

    @Override
    public IStructureDefinition<PetrochemicalPlant> getStructureDefinition() {
        return StructureDefinition.<PetrochemicalPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlockAnyMeta(Blocks.yszUnit))
            .addElement('B', HatchElement.Muffler.newAny(getCasingTextureID(), 8))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 12))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 13))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 14))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 2))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 9))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 10))
            .addElement('K', StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 2))
            .addElement(
                'L',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(PetrochemicalPlant::setCoilLevel, PetrochemicalPlant::getCoilLevel))))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('N', StructureUtility.ofBlock(ModBlocks.blockCasingsTieredGTPP, 4))
            .addElement(
                'O',
                GTStructureUtility.buildHatchAdder(PetrochemicalPlant.class)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.casingCount,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))))
            .addElement('P', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 4))
            .addElement('Q', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 14))
            .addElement('R', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 0))
            .addElement('S', GTStructureUtility.ofFrame(Materials.NiobiumTitanium))
            .addElement('T', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement('U', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('V', GTStructureUtility.ofFrame(Materials.RedstoneAlloy))
            .addElement('W', GTStructureUtility.ofFrame(Materials.Vanadium))
            .addElement('X', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 4))
            .addElement('Y', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 11))
            .addElement('Z', StructureUtility.ofBlock(ModBlocks.blockCustomMachineCasings, 1))
            .addElement(
                '0',
                StructureUtility.ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass")))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
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
        casingCount = 0;
        coilLevel = gregtech.api.enums.HeatingCoilLevel.None;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        checkCasingMin(errors, casingCount, 5);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Override
            protected OverclockCalculator createOverclockCalculator(GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setDurationModifier(getUpgradeSpeedBonus())
                    .enablePerfectOC();
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.PetrochemicalPlantRecipes;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        ItemStack[] outputItems = processingLogic.getOutputItems();
        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    long scaled = (long) itemStack.stackSize * (getCoilLevel().getTier() + 1)
                        * GTUtility.getTier(this.getMaxInputVoltage())
                        * 6L;
                    itemStack.stackSize = (int) Math.min(Integer.MAX_VALUE, scaled);
                }
            }
        }
        mOutputItems = outputItems;

        FluidStack[] outputFluids = processingLogic.getOutputFluids();

        mOutputFluids = outputFluids;
        if (outputFluids != null) {
            List<FluidStack> expandedFluids = new ArrayList<>();
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    long totalAmount = (long) fluidStack.amount * (getCoilLevel().getTier() + 1)
                        * GTUtility.getTier(this.getMaxInputVoltage())
                        * 6;

                    while (totalAmount > 0) {
                        int stackSize = (int) Math.min(totalAmount, Integer.MAX_VALUE);
                        expandedFluids.add(new FluidStack(fluidStack.getFluid(), stackSize));
                        totalAmount -= stackSize;
                    }
                }
            }
            mOutputFluids = expandedFluids.toArray(new FluidStack[0]);
        }

        return result;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getCoilLevel().getTier() * 40 + getUpgradeParallelBonus();
    }

    public void setCoilLevel(gregtech.api.enums.HeatingCoilLevel level) {
        coilLevel = level;
    }

    public gregtech.api.enums.HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 3);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(PETRO_RECIPE_TYPE)
            .addInfo(PETRO_00)
            .addInfo(PETRO_01)
            .addInfo(PETRO_02)
            .addPerfectOCInfo()
            .addTecTechHatchInfo()
            .beginStructureBlock(28, 60, 65, true)
            .addInputHatch(PETRO_CASING)
            .addOutputHatch(PETRO_CASING)
            .addInputBus(PETRO_CASING)
            .addOutputBus(PETRO_CASING)
            .addEnergyHatch(PETRO_CASING)
            .addMaintenanceHatch(PETRO_CASING)
            .addMufflerHatch(PETRO_MUFFLER, 8)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    public boolean getPerfectOC() {
        return true;
    }
}
