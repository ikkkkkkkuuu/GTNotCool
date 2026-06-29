package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockAnyMeta;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockUnlocalizedName;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.init.Blocks.piston;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.GTNCMultiBlockBaseGui;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCWirelessEnergyMultiMachineBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

// #tr NameGTNCGeneralChemicalFactory
// # General Chemical Factory
// # zh_CN 通用化工厂

public class GTNCGeneralChemicalFactory extends GTNCWirelessEnergyMultiMachineBase<GTNCGeneralChemicalFactory>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 11;
    private static final int DEPTH_OFF_SET = 2;

    private HeatingCoilLevel mCoilLevel = HeatingCoilLevel.None;

    private static final int MACHINEMODE_MTECR = 0;
    private static final int MACHINEMODE_GCF = 1;
    private static final int MACHINEMODE_GTPP = 2;

    private static final String[][] SHAPE = new String[][] {
        { "                    ", "                    ", "                    ", "   HHHHH            ",
            "   H   H            ", "   H   H            ", "   H   H            ", "   H   H            ",
            "   H   H            ", "   HHHHH            ", "   H   H            ", "   H   H            ",
            "   H   H            " },
        { "                    ", "                    ", "                    ", "   HBBBH            ",
            "    BBB             ", "    GGG             ", "    GGG             ", "    GGG             ",
            "    BBB             ", "   HBBBH    HHHHHHH ", "            H     H ", "            H     H ",
            "            H     H " },
        { "                    ", "    BBB             ", "    BBB             ", "  HB I BH           ",
            "   B   B            ", "   G   G            ", "   G C G            ", "   GDDDG            ",
            "   B   B     BBBBB  ", "  HB   BH   HB   BH ", "    BBB      B   B  ", "    B~B      B   B  ",
            "    BBB      BBBBB  " },
        { "                    ", "   B   B            ", "   BGGGB            ", "HHB  I  BHH         ",
            "H B     B H         ", "H G     G H         ", "H G  C  G H         ", "H GD   DG H  BBBBB  ",
            "H B FFF B H BBBBBBB ", "HHB     BHHHBGGGGGBH", "H  BFFFB  H BGAAAGB ", "H  B C B  H BGGGGGB ",
            "H  BEEEB  H BEEEEEB " },
        { "                    ", "  B     B           ", "  BGGGGGB           ", "HB   I   BH         ",
            " B   I   B          ", " G   I   G          ", " G   C   G          ", " GD  I  DG   BAAAB  ",
            " B F I F B  BBJJJBB ", "HB       BH  G   G H", "  BF I FB    G   G  ", "  B  C  B    GKKKG  ",
            "  BEEEEEB   BEEEEEB " },
        { "     CCCCCCC        ", "  B  C  B  C        ", "  BGGCGGB  C        ", "HBIIICIIIBHC        ",
            " B  ICI  B C        ", " G  ICI  G C        ", " GCCCCCCCG C        ", " GD ICI DG C BAAAB  ",
            " B FICIF B CBBJJJBB ", "HB   C   BHCCD   G H", "  BFICIFB    G   G  ", "  BCCCCCB    GKKKG  ",
            "  BEEEEEB   BEEEEEB " },
        { "                    ", "  B     B           ", "  BGGGGGB           ", "HB   I   BH         ",
            " B   I   B          ", " G   I   G          ", " G   C   G          ", " GD  I  DG   BBBBB  ",
            " B F I F B  BBBBBBB ", "HB       BH BGGGGGBH", "  BF I FB   BGGGGGB ", "  B  C  B   BGGGGGB ",
            "  BEEEEEB   BEEEEEB " },
        { "                    ", "   B   B            ", "   BGGGB            ", "HHB  I  BHH         ",
            "H B     B H         ", "H G     G H         ", "H G  C  G H         ", "H GD   DG H         ",
            "H B FFF B H  BBBBB  ", "HHB     BHHHHB   BH ", "H  BFFFB  H  B   B  ", "H  B C B  H  B   B  ",
            "H  BEEEB  H  BBBBB  " },
        { "                    ", "    BBB             ", "    BBB             ", "  HB I BH           ",
            "   B   B            ", "   G   G            ", "   G C G            ", "   GDDDG            ",
            "   B   B            ", "  HB   BH   HHHHHHH ", "    BBB     H     H ", "    BBB     H     H ",
            "    BBB     H     H " },
        { "                    ", "                    ", "                    ", "   HBBBH            ",
            "    BBB             ", "    GGG             ", "    GGG             ", "    GGG             ",
            "    BBB             ", "   HBBBH            ", "                    ", "                    ",
            "                    " },
        { "                    ", "                    ", "                    ", "   HHHHH            ",
            "   H   H            ", "   H   H            ", "   H   H            ", "   H   H            ",
            "   H   H            ", "   HHHHH            ", "   H   H            ", "   H   H            ",
            "   H   H            " } };

    public GTNCGeneralChemicalFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GTNCGeneralChemicalFactory(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GTNCGeneralChemicalFactory(this.mName);
    }

    // #tr mte.SAF.common.mode.0
    // # Chemical Reactor
    // # zh_CN 化学反应釜

    // #tr mte.SAF.common.mode.1
    // # General Chemical Factory
    // # zh_CN 通用化工厂

    // #tr mte.SAF.common.mode.2
    // # Mobil Chemical Factory
    // # zh_CN 美孚化工厂

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (machineMode == MACHINEMODE_MTECR) {
            return RecipeMaps.multiblockChemicalReactorRecipes;
        } else if (machineMode == MACHINEMODE_GTPP) {
            return GTPPRecipeMaps.chemicalPlantRecipes;
        }
        return GTNCRecipeMaps.GeneralChemicalFactoryRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTNCRecipeMaps.GeneralChemicalFactoryRecipes,
            RecipeMaps.multiblockChemicalReactorRecipes,
            GTPPRecipeMaps.chemicalPlantRecipes);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MACHINEMODE_MTECR) {
            return MACHINEMODE_GTPP;
        } else if (machineMode == MACHINEMODE_GTPP) {
            return MACHINEMODE_GCF;
        }
        return MACHINEMODE_MTECR;
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("mte.SAF.common.mode." + machineMode);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new GTNCMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.clear();
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_CHEMBATH);
    }

    @Override
    public boolean addOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        boolean exotic = addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
        return super.addToMachineList(aTileEntity, aBaseCasingIndex) || exotic;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (getCurrentRecipeMap() == GTPPRecipeMaps.chemicalPlantRecipes) {
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 8 * (mCoilLevel.getTier() + 1) + getUpgradeParallelBonus();
    }

    protected float getSpeedBonus() {
        int tier = mCoilLevel.getTier();
        float bonus = (tier <= 1) ? 1.0f : Math.max(0.01f, 1.0f - (tier - 1) * 0.1f);
        return Math.max(0.01f, bonus * getUpgradeSpeedBonus());
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
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
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
    }

    @Override
    public IStructureDefinition<GTNCGeneralChemicalFactory> getStructureDefinition() {
        return StructureDefinition.<GTNCGeneralChemicalFactory>builder()
            .addShape(STRUCTURE_PIECE_MAIN, SHAPE)
            .addElement('A', ofBlockUnlocalizedName("IC2", "blockAlloyGlass", 0, true))
            .addElement(
                'B',
                ofChain(
                    buildHatchAdder(GTNCGeneralChemicalFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputHatch, InputBus, OutputHatch, OutputBus)
                        .casingIndex(
                            Textures.BlockIcons.getTextureIndex(
                                Textures.BlockIcons.getCasingTextureForId(
                                    GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0))))
                        .hint(1)
                        .buildAndChain(GregTechAPI.sBlockCasings2, 0)))
            .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 13))
            .addElement('D', ofBlock(GregTechAPI.sBlockCasings3, 10))
            .addElement('E', ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement('F', ofCoil(GTNCGeneralChemicalFactory::setCoilLevel, GTNCGeneralChemicalFactory::getCoilLevel))
            .addElement('G', ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement('H', ofFrame(Materials.Steel))
            .addElement('I', ofFrame(Materials.StainlessSteel))
            .addElement('J', ofBlock(GregTechAPI.sBlockMetal6, 13))
            .addElement('K', ofBlockAnyMeta(piston))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
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

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        mCoilLevel = aCoilLevel;
    }

    public HeatingCoilLevel getCoilLevel() {
        return mCoilLevel;
    }

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

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        // #tr GTNCGeneralChemicalFactoryRecipeType
        // # Chemical Factory
        // # zh_CN 化工厂
        tt.addMachineType(StatCollector.translateToLocal("GTNCGeneralChemicalFactoryRecipeType"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_01
            // # §eF.Haber
            // # zh_CN §eF.Haber
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_01"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_02
            // # §ePerhaps people will not be free from hunger due to the birth of this machine, but the gluttons on
            // your assembly lines will love it.
            // # zh_CN §e也许人们不会因为这台机器的诞生而免受饥饿之苦，但你流水线上的饕餮们会很喜欢这个的
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_02"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_03
            // # §4§nRecombination, exchange, condensation......
            // # zh_CN §4§n重组，交换，冷凝......
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_03"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_04
            // # §eHope your blast furnace can withstand such a huge amount of steel consumption.
            // # zh_CN §e希望你的高炉能经得住如此庞大的钢铁消耗量
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_04"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_05
            // # §bIt has three modes: Chemical Reactor / General Chemical Factory / Mobil Chemical Factory
            // # zh_CN §b它有三种模式：化学反应釜/通用化工厂/美孚化工厂
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_05"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_06
            // # §bIn Mobil mode, it can run recipes ignoring machine tier requirements and without consuming catalyst
            // # zh_CN §b美孚化工厂模式下，它可以无视机器等级需求和不消耗催化剂运行配方
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_06"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_07
            // # §bIncrease machine efficiency by replacing heating coils.
            // # zh_CN §b通过替换更好的线圈来提高机器运行效率
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_07"))
            // #tr Tooltip_GTNCGeneralChemicalFactory_08
            // # §bEach coil tier provides 10% speed bonus and 8 recipe parallels!
            // # zh_CN §b每一级线圈提供10%的速度加成和8的配方并行！
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNCGeneralChemicalFactory_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Upgrade_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_Wireless_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_SupportsTecTechMultiAmp"))
            .beginStructureBlock(11, 13, 20, false)
            .addController("见结构预览")
            .addCasingInfoExactly("洁净不锈钢机械方块", 41, false)
            .addCasingInfoExactly("钢管道方块", 48, false)
            .addCasingInfoExactly("脱氧钢机械方块", 242, false)
            .addCasingInfoExactly("格栅机械方块", 17, false)
            .addCasingInfoExactly("任意线圈方块", 24, true)
            .addCasingInfoExactly("不锈钢框架", 32, false)
            .addCasingInfoExactly("化学惰性机械方块", 118, false)
            .addCasingInfoExactly("活塞", 6, false)
            .addCasingInfoExactly("钢块", 6, false)
            .addCasingInfoExactly("防爆玻璃", 9, false)
            .addCasingInfoExactly("钢框架", 164, false)
            .addInputBus("任意脱氧钢机械方块")
            .addInputHatch("任意脱氧钢机械方块")
            .addOutputBus("任意脱氧钢机械方块")
            .addOutputHatch("任意脱氧钢机械方块")
            .addEnergyHatch("任意脱氧钢机械方块")
            .toolTipFinisher();
        return tt;
    }
}
