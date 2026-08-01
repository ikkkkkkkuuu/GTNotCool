package com.xyp.gtnc.Common.machines.multiblock;

import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_00;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_01;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_02;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_03;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_04;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_05;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_CASING;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_MUFFLER;
import static com.xyp.gtnc.utils.lang.TextLocalization.PBT_RECIPE_TYPE;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.StructureUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;

@IMetaTileEntity.SkipGenerateDescription
public class PlatinumBasedTreatment extends GTNCMultiBlockBase<PlatinumBasedTreatment>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PBT_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/platinum_based_treatment";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PBT_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 15;
    private static final int DEPTH_OFF_SET = 0;

    private int casingCount;
    private int glassTier = -1;
    private gregtech.api.enums.HeatingCoilLevel coilLevel = gregtech.api.enums.HeatingCoilLevel.None;

    public PlatinumBasedTreatment(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PlatinumBasedTreatment(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new PlatinumBasedTreatment(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(PBT_RECIPE_TYPE)
            .addInfo(PBT_00)
            .addInfo(PBT_01)
            .addInfo(PBT_02)
            .addInfo(PBT_03)
            .addInfo(PBT_04)
            .addInfo(PBT_05)
            .addPerfectOCInfo()
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 17, 18, true)
            .addInputHatch(PBT_CASING)
            .addOutputHatch(PBT_CASING)
            .addInputBus(PBT_CASING)
            .addOutputBus(PBT_CASING)
            .addEnergyHatch(PBT_CASING)
            .addMaintenanceHatch(PBT_CASING)
            .addMufflerHatch(PBT_MUFFLER)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<PlatinumBasedTreatment> getStructureDefinition() {
        return StructureDefinition.<PlatinumBasedTreatment>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', GTStructureUtility.chainAllGlasses(-1, (te, t) -> te.glassTier = t, te -> te.glassTier))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 11))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sSolenoidCoilCasings, 3))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 13))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 14))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 0))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement(
                'H',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility
                            .ofCoil(PlatinumBasedTreatment::setCoilLevel, PlatinumBasedTreatment::getCoilLevel))))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 1))
            .addElement('K', GTStructureUtility.ofFrame(Materials.BlackSteel))
            .addElement('L', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 5))
            .addElement('M', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 6))
            .addElement('N', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 11))
            .addElement(
                'O',
                GTStructureUtility.buildHatchAdder(PlatinumBasedTreatment.class)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputHatch,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.casingCount,
                            StructureUtility.ofBlock(ModBlocks.blockCasings3Misc, 2))))
            .addElement('P', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 0))
            .addElement('Q', StructureUtility.ofBlock(ModBlocks.blockCasingsMisc, 5))
            .addElement(
                'R',
                HatchElement.Muffler.newAny(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 11), 6))
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
    public void checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack, List<StructureError> errors) {
        casingCount = 0;
        glassTier = -1;
        coilLevel = gregtech.api.enums.HeatingCoilLevel.None;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        validateEnergyHatchesAgainstGlass(errors);
        checkCasingMin(errors, casingCount, 30);
    }

    private void validateEnergyHatchesAgainstGlass(List<StructureError> errors) {
        if (glassTier >= VoltageIndex.UHV) {
            return;
        }
        for (MTEHatch hatch : mExoticEnergyHatches) {
            if (hatch.getConnectionType() == MTEHatch.ConnectionType.LASER) {
                errors.add(StructureErrors.glassTierNotEnough(VoltageIndex.UHV));
                return;
            }
            if (hatch.mTier > glassTier) {
                errors.add(StructureErrorRegistry.ENERGY_TIER_EXCEED_GLASS);
                return;
            }
        }
        for (MTEHatchEnergy hatch : mEnergyHatches) {
            if (hatch.mTier > glassTier) {
                errors.add(StructureErrorRegistry.ENERGY_TIER_EXCEED_GLASS);
                return;
            }
        }
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @Override
            protected OverclockCalculator createOverclockCalculator(GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier() * getUpgradeSpeedBonus())
                    .enablePerfectOC();
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.PlatinumBasedTreatmentRecipes;
    }

    public double getEUtDiscount() {
        return 1.0 - getCoilLevel().getTier() * 0.05;
    }

    public double getDurationModifier() {
        return 1.0 - getCoilLevel().getTier() * 0.05;
    }

    @Override
    public int getMaxParallelRecipes() {
        return GTUtility.getTier(this.getMaxInputVoltage()) * 4 + 8 + getUpgradeParallelBonus();
    }

    public void setCoilLevel(gregtech.api.enums.HeatingCoilLevel level) {
        coilLevel = level;
    }

    public gregtech.api.enums.HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(0)) };
    }

    public int getCasingTextureID() {
        return TAE.getIndexFromPage(2, 2);
    }

    public boolean getPerfectOC() {
        return true;
    }

    protected int getGlassEnergyTierLimit() {
        return VoltageIndex.UHV;
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_MEGA_BLAST_FURNACE_LOOP;
    }
}
