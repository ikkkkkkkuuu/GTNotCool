package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCWirelessEnergyMultiMachineBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
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
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

// #tr NameGTNCMiningRig
// # Mining Rig
// # zh_CN 矿机平台

public class GTNCMiningRig extends GTNCWirelessEnergyMultiMachineBase<GTNCMiningRig> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 4;
    private static final int VERTICAL_OFF_SET = 10;
    private static final int DEPTH_OFF_SET = 1;

    // Machine level determined by pipe blocks (1~5)
    private int mMachineLevel = 0;
    private double mSpeedBonus = 1.0;

    private static IStructureDefinition<GTNCMiningRig> STRUCTURE_DEFINITION = null;

    private static final ITexture SOLID_STEEL_MACHINE_CASING = Textures.BlockIcons
        .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0));

    // ==================== Structure Shape ====================
    // A -> Hatch placement + Solid Steel Machine Casing
    // B -> Chemically Inert Machine Casing (gt.blockcasings8, 0)
    // C -> Tiered Pipe Blocks (determines machine level)
    // D -> Steel Frame (fixed)
    // Offsets: 4 10 1
    private static final String[][] SHAPE = new String[][] { { // Layer 0
        "         ", "         ", "         ", "         ", "         ", "         ", "         ", "   AAA   ",
        "   AAA   ", "         ", "         ", "         " },
        { // Layer 1
            "         ", "         ", "         ", "         ", "         ", "         ", "         ", "  ABBBA  ",
            "  A   A  ", "   AAA   ", "   A~A   ", "   AAA   " },
        { // Layer 2
            "    A    ", "   AAA   ", "   D D   ", "   D D   ", "   D D   ", "   D D   ", " DDD DDD ", "DABBBBBAD",
            "DA  D  AD", "D A D A D", "D A D A D", "D AAAAA D" },
        { // Layer 3
            "   AAA   ", "   ACA   ", "    C    ", "    C    ", "    C    ", "    C    ", "    C    ", " ABBCBBA ",
            " A DCD A ", "  ADCDA  ", "  ADCDA  ", "  AAAAA  " },
        { // Layer 4
            "    A    ", "   AAA   ", "   D D   ", "   D D   ", "   D D   ", "   D D   ", " DDD DDD ", "DABBBBBAD",
            "DA  D  AD", "D A D A D", "D A D A D", "D AAAAA D" },
        { // Layer 5
            "         ", "         ", "         ", "         ", "         ", "         ", "         ", "  ABBBA  ",
            "  A   A  ", "   AAA   ", "   AAA   ", "   AAA   " },
        { // Layer 6
            "         ", "         ", "         ", "         ", "         ", "         ", "         ", "   AAA   ",
            "   AAA   ", "         ", "         ", "         " } };

    // ==================== Constructors ====================
    public GTNCMiningRig(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTNCMiningRig(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GTNCMiningRig(this.mName);
    }

    // ==================== Pipe Tier Detection ====================
    public static int getPipeTier(Block block, int meta) {
        if (block == GregTechAPI.sBlockCasings2 && meta == 12) return 1;
        if (block == GregTechAPI.sBlockCasings2 && meta == 13) return 2;
        if (block == GregTechAPI.sBlockCasings2 && meta == 14) return 3;
        if (block == GregTechAPI.sBlockCasings2 && meta == 15) return 4;
        if (block == GregTechAPI.sBlockCasings9 && meta == 0) return 5;
        if (block == GregTechAPI.sBlockCasings9 && meta == 14) return 6;
        return -1;
    }

    // ==================== Structure Definition ====================
    @Override
    public IStructureDefinition<GTNCMiningRig> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GTNCMiningRig>builder()
                .addShape(STRUCTURE_PIECE_MAIN, SHAPE)
                .addElement(
                    'A',
                    buildHatchAdder(GTNCMiningRig.class)
                        .atLeast(Energy.or(ExoticEnergy), InputHatch, InputBus, OutputHatch, OutputBus)
                        .casingIndex(Textures.BlockIcons.getTextureIndex(SOLID_STEEL_MACHINE_CASING))
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 0))
                // B -> Chemically Inert Machine Casing
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings8, 0))
                // C -> Tiered Pipe Blocks (determines machine level)
                .addElement(
                    'C',
                    withChannel(
                        "pipe",
                        ofBlocksTiered(
                            GTNCMiningRig::getPipeTier,
                            ImmutableList.of(
                                Pair.of(GregTechAPI.sBlockCasings2, 12),
                                Pair.of(GregTechAPI.sBlockCasings2, 13),
                                Pair.of(GregTechAPI.sBlockCasings2, 14),
                                Pair.of(GregTechAPI.sBlockCasings2, 15),
                                Pair.of(GregTechAPI.sBlockCasings9, 0),
                                Pair.of(GregTechAPI.sBlockCasings9, 14)),
                            -1,
                            (m, t) -> m.mMachineLevel = t,
                            m -> m.mMachineLevel)))
                // D -> Steel Frame (fixed)
                .addElement('D', ofFrame(Materials.Steel))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // ==================== Structure Check ====================
    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mMachineLevel = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
    }

    // ==================== Processing Logic ====================
    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(4, 4); // Perfect overclock
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                if (mMachineLevel < recipe.mSpecialValue) {
                    return CheckRecipeResultRegistry.NONE;
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 8 + getUpgradeParallelBonus();
    }

    /**
     * Speed bonus: each machine level gives 15% speed reduction,
     * multiplied by upgrade speed bonus from chip.
     * SpeedBonus = (1.0 - (mMachineLevel - 1) * 0.15) * upgradeSpeedBonus
     */
    protected float getSpeedBonus() {
        double sb = 1.0 - (mMachineLevel - 1) * 0.15;
        if (sb <= 0) sb = 0.01;
        mSpeedBonus = sb * getUpgradeSpeedBonus();
        return (float) Math.max(0.01f, mSpeedBonus);
    }

    // ==================== Recipe Map ====================
    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.MiningRigRecipes;
    }

    // ==================== GUI ====================
    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new MTEMultiBlockBaseGui<>(this);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_DEFAULT);
    }

    // ==================== Texture ====================
    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { SOLID_STEEL_MACHINE_CASING, TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { SOLID_STEEL_MACHINE_CASING, TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_OIL_CRACKER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { SOLID_STEEL_MACHINE_CASING };
    }

    // ==================== Energy ====================
    @Override
    public boolean addOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        boolean exotic = addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
        return addToMachineList(aTileEntity, aBaseCasingIndex) || exotic;
    }

    // ==================== Basic Methods ====================
    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
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
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    // ==================== Maintenance-Free ====================
    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }

    // ==================== NBT Persistence ====================
    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mMachineLevel", mMachineLevel);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMachineLevel = aNBT.getInteger("mMachineLevel");
    }

    // ==================== Tooltip ====================
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        // #tr GTNCMiningRigRecipeType
        // # Mining Rig
        // # zh_CN 矿机平台
        tt.addMachineType(StatCollector.translateToLocal("GTNCMiningRigRecipeType"))
            // #tr Tooltip_GTNCMiningRig_01
            // # §eExtracts rare resources from deep underground
            // # zh_CN §e从深层地下提取稀有资源
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCMiningRig_01"))
            // #tr Tooltip_GTNCMiningRig_02
            // # §bPipe blocks determine machine level (1~6)
            // # zh_CN §b管道方块决定机器等级(1~6)
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCMiningRig_02"))
            // #tr Tooltip_GTNCMiningRig_03
            // # §bEach level provides 15% speed bonus and allows higher tier recipes
            // # zh_CN §b每一级提供15%的速度加成并允许更高等级的配方
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCMiningRig_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SupportsTecTechMultiAmp"))
            .beginStructureBlock(9, 12, 7, false)
            .addController("见结构预览")
            .addCasingInfoExactly("脱氧钢机械方块", 84, false)
            .addCasingInfoExactly("化学惰性机械方块", 20, false)
            .addCasingInfoExactly("管道方块(决定等级)", 10, true)
            .addCasingInfoExactly("钢框架", 60, false)
            .addInputBus("任意脱氧钢机械方块")
            .addInputHatch("任意脱氧钢机械方块")
            .addOutputBus("任意脱氧钢机械方块")
            .addOutputHatch("任意脱氧钢机械方块")
            .addEnergyHatch("任意脱氧钢机械方块")
            .toolTipFinisher();
        return tt;
    }
}
