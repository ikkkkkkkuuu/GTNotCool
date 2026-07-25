package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings10;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockCasings9;
import static gregtech.api.GregTechAPI.sBlockGlass1;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.ScienceNotCool;
import com.xyp.gtnc.utils.StructureUtils;
import com.xyp.gtnc.utils.item.ItemUtils;
import com.xyp.gtnc.utils.lang.TextLocalization;
import com.xyp.gtnc.utils.structure.GTNCStructureErrors;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class IndustrialArcaneAssembler extends MTEExtendedPowerMultiBlockBase<IndustrialArcaneAssembler>
    implements ISurvivalConstructable {

    public static final int ShapedArcaneCrafting = 0;
    public static final int InfusionCrafting = 1;
    public int mCountCasing = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LAA_STRUCTURE_FILE_PATH = ScienceNotCool.RESOURCE_ROOT_ID + ":"
        + "multiblock/industrial_arcane_assembler";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LAA_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 9;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 3;

    public IndustrialArcaneAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public IndustrialArcaneAssembler(String aName) {
        super(aName);
    }

    @Override
    public IStructureDefinition<IndustrialArcaneAssembler> getStructureDefinition() {
        return StructureDefinition.<IndustrialArcaneAssembler>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', ofBlock(sBlockCasings10, 7))
            .addElement('B', ofBlock(sBlockCasings2, 0))
            .addElement('C', ofBlock(sBlockCasings1, 13))
            .addElement('D', ofBlock(sBlockCasings8, 7))
            .addElement('E', ofBlock(sBlockCasings10, 6))
            .addElement('F', ofBlock(sBlockCasings10, 8))
            .addElement(
                'G',
                buildHatchAdder(IndustrialArcaneAssembler.class).casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(Maintenance, InputBus, OutputBus, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings10, 3))))
            .addElement('H', ofBlock(sBlockCasings2, 4))
            .addElement('I', ofBlock(sBlockCasings9, 12))
            .addElement('J', ofFrame(Materials.Neutronium))
            .addElement('K', ofFrame(Materials.DarkIron))
            .addElement('L', ofBlock(sBlockGlass1, 0))
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
        mCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 25);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == ShapedArcaneCrafting ? GTNCRecipeMaps.IndustrialShapedArcaneCraftingRecipes
            : GTNCRecipeMaps.IndustrialInfusionCraftingRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            GTNCRecipeMaps.IndustrialShapedArcaneCraftingRecipes,
            GTNCRecipeMaps.IndustrialInfusionCraftingRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    public void checkHatch(List<StructureError> errors) {
        if (!GTUtility.areStacksEqual(
            getControllerSlot(),
            ItemUtils.getItemStack(
                Mods.Thaumcraft.ID,
                "WandCasting",
                1,
                9000,
                "{cap:\"matrix\",rod:\"infinity\",aer:999999900,aqua:999999900,ignis:999999900,ordo:999999900,perditio:999999900,terra:999999900}",
                null))) {
            errors.add(GTNCStructureErrors.missingCrystalWand());
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings10, 3);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.IndustrialArcaneAssemblerRecipeType)
            .addInfo(TextLocalization.Tooltip_IndustrialArcaneAssembler_00)
            .addInfo(TextLocalization.Tooltip_IndustrialArcaneAssembler_01)
            .addInfo(TextLocalization.Tooltip_IndustrialArcaneAssembler_02)
            .beginStructureBlock(19, 19, 19, true)
            .addInputBus(TextLocalization.Tooltip_EnergeticIndustrialArcaneAssembler_Casing)
            .addOutputBus(TextLocalization.Tooltip_EnergeticIndustrialArcaneAssembler_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_EnergeticIndustrialArcaneAssembler_Casing)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new MTEMultiBlockBaseGui<>(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public String getMachineModeName() {
        return machineMode == ShapedArcaneCrafting ? TextLocalization.IndustrialArcaneAssembler_Mode_0
            : TextLocalization.IndustrialArcaneAssembler_Mode_1;
    }

    @Override
    @Deprecated
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new IndustrialArcaneAssembler(mName);
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
}
