package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.activeCoils;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.StructureUtils;
import com.xyp.gtnc.utils.lang.TextLocalization;
import com.xyp.gtnc.utils.recipes.metadata.FuelRefiningMetadata;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TAE;
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
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;
import gtnhlanth.common.register.LanthItemList;

@IMetaTileEntity.SkipGenerateDescription
public class FuelRefiningComplex extends GTNCMultiBlockBase<FuelRefiningComplex> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_FILE = RESOURCE_ROOT_ID + ":multiblock/fuel_refining_complex";
    private static final String[][] SHAPE = StructureUtils.readStructureFromFile(STRUCTURE_FILE);
    private static final int HORIZONTAL_OFFSET = 8;
    private static final int VERTICAL_OFFSET = 12;
    private static final int DEPTH_OFFSET = 0;
    private static final int BASE_PARALLEL = 8;

    private int casingCount;
    private HeatingCoilLevel coilLevel = HeatingCoilLevel.None;
    private int heatingCapacity;

    public FuelRefiningComplex(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public FuelRefiningComplex(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new FuelRefiningComplex(mName);
    }

    @Override
    public IStructureDefinition<FuelRefiningComplex> getStructureDefinition() {
        return StructureDefinition.<FuelRefiningComplex>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(SHAPE))
            .addElement('A', ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass")))
            .addElement('B', ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('D', ofBlock(GregTechAPI.sBlockCasings4, 0))
            .addElement('E', ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement(
                'F',
                GTStructureChannels.HEATING_COIL
                    .use(activeCoils(ofCoil(FuelRefiningComplex::setCoilLevel, FuelRefiningComplex::getCoilLevel))))
            .addElement('G', ofBlock(GregTechAPI.sBlockCasings6, 6))
            .addElement('H', ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('I', ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('J', ofFrame(Materials.TungstenSteel))
            .addElement('K', ofBlock(ModBlocks.blockCasings2Misc, 4))
            .addElement(
                'L',
                buildHatchAdder(FuelRefiningComplex.class).casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(Maintenance, InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.casingCount, ofBlock(ModBlocks.blockCasings3Misc, 1))))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity tileEntity, ItemStack stack, List<StructureError> errors) {
        casingCount = 0;
        heatingCapacity = 0;
        coilLevel = HeatingCoilLevel.None;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) return;
        heatingCapacity = (int) coilLevel.getHeat();
        checkCasingMin(errors, casingCount, 245);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int requiredTier = recipe.getMetadataOrDefault(FuelRefiningMetadata.INSTANCE, 0);
                if (requiredTier > 0) return CheckRecipeResultRegistry.insufficientMachineTier(requiredTier);
                return recipe.mSpecialValue <= heatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(heatingCapacity)
                    .setDurationModifier(getUpgradeSpeedBonus());
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return BASE_PARALLEL + getUpgradeParallelBonus();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.FuelRefiningComplexRecipes;
    }

    public void setCoilLevel(HeatingCoilLevel level) {
        coilLevel = level;
    }

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    public int getCasingTextureID() {
        return TAE.GTPP_INDEX(33);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(
                        active ? Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE
                            : Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(TextLocalization.FuelRefiningComplexRecipeType)
            .addInfo(TextLocalization.Tooltip_FuelRefiningComplex_00)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_00)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_01)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_02)
            .addMultiAmpHatchInfo()
            .beginStructureBlock(17, 14, 16, true)
            .addInputHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addOutputHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addInputBus(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_FuelRefiningComplex_Casing)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
    }
}
