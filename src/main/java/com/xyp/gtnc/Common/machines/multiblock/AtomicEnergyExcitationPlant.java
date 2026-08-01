package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
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
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.ITierConverter;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.xyp.gtnc.Client.render.AtomicEnergyExcitationPlantRenderer;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.StructureUtils;
import com.xyp.gtnc.utils.lang.TextLocalization;
import com.xyp.gtnc.utils.recipes.metadata.FuelRefiningMetadata;

import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.render.IMTERenderer;
import kubatech.loaders.BlockLoader;

@IMetaTileEntity.SkipGenerateDescription
public class AtomicEnergyExcitationPlant extends GTNCMultiBlockBase<AtomicEnergyExcitationPlant>
    implements ISurvivalConstructable, IMTERenderer {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_SPHERE = "sphere";
    private static final String STRUCTURE_PIECE_SPHERE_AIR = "sphere_air";
    private static final String MAIN_STRUCTURE_FILE = RESOURCE_ROOT_ID + ":multiblock/atomic_energy_excitation_plant";
    private static final String SPHERE_STRUCTURE_FILE = RESOURCE_ROOT_ID
        + ":multiblock/atomic_energy_excitation_plant_sphere";
    private static final String[][] MAIN_SHAPE = StructureUtils.readStructureFromFile(MAIN_STRUCTURE_FILE);
    private static final String[][] SPHERE_SHAPE = StructureUtils.readStructureFromFile(SPHERE_STRUCTURE_FILE);
    private static final String[][] SPHERE_AIR_SHAPE = StructureUtils.replaceLetters(SPHERE_SHAPE, "L");
    private static final int HORIZONTAL_OFFSET = 8;
    private static final int VERTICAL_OFFSET = 8;
    private static final int DEPTH_OFFSET = 3;
    private static final int SPHERE_HORIZONTAL_OFFSET = 4;
    private static final int SPHERE_VERTICAL_OFFSET = 4;
    private static final int SPHERE_DEPTH_OFFSET = -7;
    private static final int BASE_PARALLEL = 8;
    private static final Block[] FIELD_RESTRICTION_COILS = { Loaders.FRF_Coil_1, Loaders.FRF_Coil_2, Loaders.FRF_Coil_3,
        Loaders.FRF_Coil_4 };

    private int casingCount;
    private int machineTier = -1;
    private HeatingCoilLevel coilLevel = HeatingCoilLevel.None;
    private int heatingCapacity;
    private boolean enableRender = true;
    private boolean renderActive;
    public float rotation;

    public AtomicEnergyExcitationPlant(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public AtomicEnergyExcitationPlant(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new AtomicEnergyExcitationPlant(mName);
    }

    public void setMachineTier(int tier) {
        machineTier = tier;
    }

    public int getMachineTier() {
        return machineTier;
    }

    public void setCoilLevel(HeatingCoilLevel level) {
        coilLevel = level;
    }

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity tileEntity) {
        super.onFirstTick(tileEntity);
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderTESR(double x, double y, double z, float timeSinceLastTick) {
        if (renderActive && enableRender) AtomicEnergyExcitationPlantRenderer.renderTileEntity(this, x, y, z);
    }

    @Override
    public void onValueUpdate(byte value) {
        renderActive = (value & 0x01) != 0;
        enableRender = (value & 0x02) != 0;
    }

    @Override
    public byte getUpdateData() {
        byte data = 0;
        if (renderActive) data |= 0x01;
        if (enableRender) data |= 0x02;
        return data;
    }

    @Override
    public void onPostTick(IGregTechTileEntity tileEntity, long tick) {
        super.onPostTick(tileEntity, tick);
        if (tileEntity.isClientSide()) rotation = (rotation + 0.5F) % 360F;
    }

    @Override
    public boolean isFlipChangeAllowed() {
        return !mMachine && !renderActive && super.isFlipChangeAllowed();
    }

    @Override
    public boolean isRotationChangeAllowed() {
        return !mMachine && !renderActive && super.isRotationChangeAllowed();
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        if (renderActive) buildSphere();
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer player,
        float x, float y, float z, ItemStack tool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            enableRender = !enableRender;
            GTUtility.sendChatTrans(player, "Info_Render_" + (enableRender ? "Enabled" : "Disabled"));
            checkStructure(true, getBaseMetaTileEntity());
        }
        return true;
    }

    private void destroySphere() {
        buildPiece(
            STRUCTURE_PIECE_SPHERE_AIR,
            null,
            false,
            SPHERE_HORIZONTAL_OFFSET,
            SPHERE_VERTICAL_OFFSET,
            SPHERE_DEPTH_OFFSET);
        renderActive = true;
    }

    private void buildSphere() {
        buildPiece(
            STRUCTURE_PIECE_SPHERE,
            null,
            false,
            SPHERE_HORIZONTAL_OFFSET,
            SPHERE_VERTICAL_OFFSET,
            SPHERE_DEPTH_OFFSET);
        renderActive = false;
    }

    public ChunkCoordinates getRenderPos() {
        ForgeDirection back = getExtendedFacing().getRelativeBackInWorld();
        return new ChunkCoordinates(11 * back.offsetX, 11 * back.offsetY, 11 * back.offsetZ);
    }

    private static ITierConverter<Integer> fieldCoilTierConverter() {
        return (block, meta) -> {
            for (int i = 0; i < FIELD_RESTRICTION_COILS.length; i++) {
                if (block == FIELD_RESTRICTION_COILS[i]) return i + 1;
            }
            return null;
        };
    }

    private static List<Pair<Block, Integer>> getAllFieldCoilTiers() {
        List<Pair<Block, Integer>> tiers = new ArrayList<>();
        for (Block coil : FIELD_RESTRICTION_COILS) tiers.add(Pair.of(coil, 0));
        return tiers;
    }

    @Override
    public IStructureDefinition<AtomicEnergyExcitationPlant> getStructureDefinition() {
        return StructureDefinition.<AtomicEnergyExcitationPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(MAIN_SHAPE))
            .addShape(STRUCTURE_PIECE_SPHERE, transpose(SPHERE_SHAPE))
            .addShape(STRUCTURE_PIECE_SPHERE_AIR, transpose(SPHERE_AIR_SHAPE))
            .addElement('A', chainAllGlasses())
            .addElement(
                'B',
                GTStructureChannels.TIER_MACHINE_CASING.use(
                    StructureUtility.ofBlocksTiered(
                        fieldCoilTierConverter(),
                        getAllFieldCoilTiers(),
                        -1,
                        AtomicEnergyExcitationPlant::setMachineTier,
                        AtomicEnergyExcitationPlant::getMachineTier)))
            .addElement('C', ofBlock(BlockLoader.defcCasingBlock, 7))
            .addElement(
                'D',
                GTStructureChannels.HEATING_COIL.use(
                    activeCoils(
                        ofCoil(AtomicEnergyExcitationPlant::setCoilLevel, AtomicEnergyExcitationPlant::getCoilLevel))))
            .addElement('E', ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement(
                'F',
                buildHatchAdder(AtomicEnergyExcitationPlant.class).casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(Maintenance, InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.casingCount, ofBlock(GregTechAPI.sBlockCasings9, 11))))
            .addElement('G', ofFrame(Materials.Neutronium))
            .addElement('H', ofBlock(WerkstoffLoader.BWBlockCasingsAdvanced, 31_766 + 129))
            .addElement('I', ofBlock(WerkstoffLoader.BWBlockCasings, 31_766 + 129))
            .addElement('J', ofBlock(GregTechAPI.sBlockMetal4, 13))
            .addElement('K', ofBlock(GregTechAPI.sBlockMetal4, 14))
            .addElement('L', isAir())
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity tileEntity, ItemStack stack, List<StructureError> errors) {
        casingCount = 0;
        machineTier = -1;
        heatingCapacity = 0;
        coilLevel = HeatingCoilLevel.None;

        if (renderActive) {
            if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)
                || !checkPiece(
                    STRUCTURE_PIECE_SPHERE_AIR,
                    SPHERE_HORIZONTAL_OFFSET,
                    SPHERE_VERTICAL_OFFSET,
                    SPHERE_DEPTH_OFFSET,
                    errors)) {
                buildSphere();
                return;
            }
        } else if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)
            || !checkPiece(
                STRUCTURE_PIECE_SPHERE,
                SPHERE_HORIZONTAL_OFFSET,
                SPHERE_VERTICAL_OFFSET,
                SPHERE_DEPTH_OFFSET,
                errors)) {
                    return;
                }

        heatingCapacity = (int) coilLevel.getHeat();
        checkCasingMin(errors, casingCount, 350);

        if (!renderActive && enableRender && mTotalRunTime > 0) {
            destroySphere();
        } else if (renderActive && !enableRender) {
            buildSphere();
        }
        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(getMaxInputEu());
        logic.setAvailableAmperage(1);
        logic.setAmperageOC(true);
    }

    @Override
    public void construct(ItemStack stack, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stack, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
        buildPiece(
            STRUCTURE_PIECE_SPHERE,
            stack,
            hintsOnly,
            SPHERE_HORIZONTAL_OFFSET,
            SPHERE_VERTICAL_OFFSET,
            SPHERE_DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stack, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);
        int built = survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stack,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            realBudget,
            env,
            false,
            true);
        if (built >= 0) return built;
        return built + survivalBuildPiece(
            STRUCTURE_PIECE_SPHERE,
            stack,
            SPHERE_HORIZONTAL_OFFSET,
            SPHERE_VERTICAL_OFFSET,
            SPHERE_DEPTH_OFFSET,
            realBudget,
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
                if (requiredTier > machineTier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(requiredTier);
                }
                return recipe.mSpecialValue <= heatingCapacity ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(heatingCapacity)
                    .setHeatOC(true)
                    .enablePerfectOC()
                    .setHeatDiscount(true)
                    .setEUtDiscount(0.8 * Math.pow(0.95, coilLevel.getTier()))
                    .setDurationModifier((1.0 / 1.67) * getUpgradeSpeedBonus());
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

    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings9, 11);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()), TextureFactory.builder()
                    .addIcon(
                        active ? Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE
                            : Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(
                        active ? Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW
                            : Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return new MultiblockTooltipBuilder().addMachineType(TextLocalization.AtomicEnergyExcitationPlantRecipeType)
            .addInfo(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_00)
            .addInfo(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_01)
            .addInfo(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_02)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_00)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_01)
            .addInfo(TextLocalization.Tooltip_GTNC_Upgrade_02)
            .addPerfectOCInfo()
            .addTecTechHatchInfo()
            .beginStructureBlock(17, 29, 23, true)
            .addInputHatch(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_Casing)
            .addOutputHatch(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_Casing)
            .addInputBus(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_AtomicEnergyExcitationPlant_Casing)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .addSubChannelUsage(GTStructureChannels.TIER_MACHINE_CASING)
            .addSubChannelUsage(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean("isRenderActive", renderActive);
        nbt.setBoolean("enableRender", enableRender);
        nbt.setInteger("mTier", machineTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        machineTier = nbt.getInteger("mTier");
        renderActive = nbt.getBoolean("isRenderActive");
        if (nbt.hasKey("enableRender")) enableRender = nbt.getBoolean("enableRender");
    }
}
