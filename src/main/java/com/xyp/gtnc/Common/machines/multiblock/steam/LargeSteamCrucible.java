package com.xyp.gtnc.Common.machines.multiblock.steam;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.lazy;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Common.machines.multiblock.multiMachineBase.GTNCSteamMultiBlockBase;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

// #tr NameLargeSteamCrucibleSteel
// # Large Steam Steel Crucible
// # zh_CN 大型蒸汽钢坩埚

// #tr NameLargeSteamCrucibleInvar
// # Large Steam Invar Crucible
// # zh_CN 大型蒸汽殷钢坩埚

// #tr NameLargeSteamCrucibleStainless
// # Large Steam Stainless Steel Crucible
// # zh_CN 大型蒸汽不锈钢坩埚

// #tr NameLargeSteamCrucibleTitanium
// # Large Steam Titanium Crucible
// # zh_CN 大型蒸汽钛坩埚

// #tr NameLargeSteamCrucibleTungstenSteel
// # Large Steam Tungstensteel Crucible
// # zh_CN 大型蒸汽钨钢坩埚

// #tr LargeSteamCrucibleRecipeType
// # Crucible
// # zh_CN 坩埚

// #tr Tooltip_LargeSteamCrucible_00
// # Uses steam to process high-temperature recipes
// # zh_CN 使用蒸汽处理高温配方

// #tr Tooltip_LargeSteamCrucible_01
// # Higher tier crucibles can handle more advanced recipes
// # zh_CN 更高等级的坩埚可以处理更高级的配方

// #tr Tooltip_LargeSteamCrucible_Casing
// # Any Casing Block
// # zh_CN 任意机械方块

// #tr Tooltip_LargeSteamCrucible_Speed_00
// # §aSpeed Bonus: %sx
// # zh_CN §a速度加成：%s倍

public abstract class LargeSteamCrucible extends GTNCSteamMultiBlockBase<LargeSteamCrucible>
    implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;

    private static final String[][] SHAPE = new String[][] { { "AAAA", "AAA~", "AAAA" }, { "A  A", "A  A", "AAAA" },
        { "A  A", "A  A", "AAAA" }, { "AAAA", "AAAA", "AAAA" } };

    public LargeSteamCrucible(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeSteamCrucible(String aName) {
        super(aName);
    }

    public abstract Block getCasingBlock();

    public abstract byte getCasingMeta();

    public abstract byte getCasingTextureIndex();

    public abstract int getCrucibleTier();

    public String getSpeedMultiplierStr() {
        return String.format("%.2f", 1.0 + (getCrucibleTier() - 1) * 0.25);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.SteamCrucibleRecipes;
    }

    @Override
    public String getMachineType() {
        return StatCollector.translateToLocal("LargeSteamCrucibleRecipeType");
    }

    @Override
    protected IIconContainer getInactiveOverlay() {
        return Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER;
    }

    @Override
    protected IIconContainer getActiveOverlay() {
        return Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                int required = recipe.mSpecialValue;
                if (required <= 0) required = 1;
                if (getCrucibleTier() < required) {
                    // #tr GT5U.gui.text.recipe_result.crucible_insufficient_tier
                    // # Requires a higher tier crucible
                    // # zh_CN 需要更高级的坩埚
                    return SimpleCheckRecipeResult.ofFailure("crucible_insufficient_tier");
                }
                return super.validateRecipe(recipe);
            }
        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = super.checkProcessing();
        if (result.wasSuccessful() && mMaxProgresstime > 0) {
            // Each tier above 1 adds 25% speed: T1=1x, T2=1.25x, T3=1.5x, T4=1.75x, T5=2x
            double speedFactor = 1.0 + (getCrucibleTier() - 1) * 0.25;
            mMaxProgresstime = Math.max(1, (int) (mMaxProgresstime / speedFactor));
        }
        return result;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
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
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()) };
    }

    @Override
    public IStructureDefinition<LargeSteamCrucible> getStructureDefinition() {
        return StructureDefinition.<LargeSteamCrucible>builder()
            .addShape(STRUCTURE_PIECE_MAIN, SHAPE)
            .addElement(
                'A',
                buildHatchAdder(LargeSteamCrucible.class).atLeast(InputBus, OutputBus, InputHatch, OutputHatch)
                    .casingIndex(getCasingTextureIndex())
                    .hint(1)
                    .buildAndChain(onElementPass(x -> {}, lazy(t -> ofBlock(t.getCasingBlock(), t.getCasingMeta())))))
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

    // ==================== Steel Crucible (Tier 1) ====================

    public static class LargeSteamCrucibleSteel extends LargeSteamCrucible {

        public LargeSteamCrucibleSteel(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamCrucibleSteel(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamCrucibleSteel(this.mName);
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings2;
        }

        @Override
        public byte getCasingMeta() {
            return 0;
        }

        @Override
        public byte getCasingTextureIndex() {
            return (byte) GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0);
        }

        @Override
        public int getCrucibleTier() {
            return 1;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 4;
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamCrucibleRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_01"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))

                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_LargeSteamCrucible_Speed_00", getSpeedMultiplierStr()))
                .beginStructureBlock(4, 4, 3, false)
                .addController("Front bottom right")
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .toolTipFinisher();
            return tt;
        }
    }

    // ==================== Invar Crucible (Tier 2) ====================

    public static class LargeSteamCrucibleInvar extends LargeSteamCrucible {

        public LargeSteamCrucibleInvar(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamCrucibleInvar(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamCrucibleInvar(this.mName);
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings1;
        }

        @Override
        public byte getCasingMeta() {
            return 11;
        }

        @Override
        public byte getCasingTextureIndex() {
            return (byte) GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 11);
        }

        @Override
        public int getCrucibleTier() {
            return 2;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 8;
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamCrucibleRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_01"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))

                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_LargeSteamCrucible_Speed_00", getSpeedMultiplierStr()))
                .beginStructureBlock(4, 4, 3, false)
                .addController("Front bottom right")
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .toolTipFinisher();
            return tt;
        }
    }

    // ==================== Stainless Crucible (Tier 3) ====================

    public static class LargeSteamCrucibleStainless extends LargeSteamCrucible {

        public LargeSteamCrucibleStainless(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamCrucibleStainless(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamCrucibleStainless(this.mName);
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 1;
        }

        @Override
        public byte getCasingTextureIndex() {
            return (byte) GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings4, 1);
        }

        @Override
        public int getCrucibleTier() {
            return 3;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 16;
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamCrucibleRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_01"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))

                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_LargeSteamCrucible_Speed_00", getSpeedMultiplierStr()))
                .beginStructureBlock(4, 4, 3, false)
                .addController("Front bottom right")
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .toolTipFinisher();
            return tt;
        }
    }

    // ==================== Titanium Crucible (Tier 4) ====================

    public static class LargeSteamCrucibleTitanium extends LargeSteamCrucible {

        public LargeSteamCrucibleTitanium(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamCrucibleTitanium(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamCrucibleTitanium(this.mName);
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 2;
        }

        @Override
        public byte getCasingTextureIndex() {
            return (byte) GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings4, 2);
        }

        @Override
        public int getCrucibleTier() {
            return 4;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 32;
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamCrucibleRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_01"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))

                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_LargeSteamCrucible_Speed_00", getSpeedMultiplierStr()))
                .beginStructureBlock(4, 4, 3, false)
                .addController("Front bottom right")
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .toolTipFinisher();
            return tt;
        }
    }

    // ==================== TungstenSteel Crucible (Tier 5) ====================

    public static class LargeSteamCrucibleTungstenSteel extends LargeSteamCrucible {

        public LargeSteamCrucibleTungstenSteel(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional);
        }

        public LargeSteamCrucibleTungstenSteel(String aName) {
            super(aName);
        }

        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
            return new LargeSteamCrucibleTungstenSteel(this.mName);
        }

        @Override
        public Block getCasingBlock() {
            return GregTechAPI.sBlockCasings4;
        }

        @Override
        public byte getCasingMeta() {
            return 0;
        }

        @Override
        public byte getCasingTextureIndex() {
            return (byte) GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings4, 0);
        }

        @Override
        public int getCrucibleTier() {
            return 5;
        }

        @Override
        public int getMaxParallelRecipes() {
            return 64;
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(StatCollector.translateToLocal("LargeSteamCrucibleRecipeType"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_00"))
                .addInfo(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_01"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_PerfectOverclock"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeParallel"))
                .addInfo(StatCollector.translateToLocal("Tooltip_GTNC_CrossRecipeDuration"))

                .addInfo(
                    StatCollector
                        .translateToLocalFormatted("Tooltip_LargeSteamCrucible_Speed_00", getSpeedMultiplierStr()))
                .beginStructureBlock(4, 4, 3, false)
                .addController("Front bottom right")
                .addInputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addInputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeSteamCrucible_Casing"), 1)
                .toolTipFinisher();
            return tt;
        }
    }
}
