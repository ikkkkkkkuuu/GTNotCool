package com.xyp.gtnc.Common.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.xyp.gtnc.Client.utils.BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB;
import static com.xyp.gtnc.Client.utils.BlockIcons.OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE;
import static com.xyp.gtnc.ScienceNotCool.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.xyp.gtnc.Loader.BlockLoader;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.StructureUtils;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class LargeOreProcessor extends MTEEnhancedMultiBlockBase<LargeOreProcessor> implements ISurvivalConstructable {

    // Machine constants
    private static final int MAX_PARALLEL = Integer.MAX_VALUE;
    private static final int DURATION_TICKS = 20;
    private static final int EU_PER_TICK = 0; // No energy consumption

    // Structure constants
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LOP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_ore_processor";
    private static final String[][] STRUCTURE_SHAPE = StructureUtils.readStructureFromFile(LOP_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFFSET = 1;
    private static final int VERTICAL_OFFSET = 1;
    private static final int DEPTH_OFFSET = 0;

    // Instance state
    private int mCountCasing = 0;

    // Constructors
    public LargeOreProcessor(String aName) {
        super(aName);
    }

    public LargeOreProcessor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeOreProcessor(this.mName);
    }

    // ==================== Structure Definition ====================

    @Override
    public IStructureDefinition<LargeOreProcessor> getStructureDefinition() {
        return StructureDefinition.<LargeOreProcessor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(STRUCTURE_SHAPE))
            .addElement(
                'A',
                ofChain(
                    buildHatchAdder(LargeOreProcessor.class).casingIndex(getCasingTextureID())
                        .hint(1)
                        .atLeast(InputBus, OutputBus, Maintenance)
                        .build(),
                    onElementPass(x -> ++x.mCountCasing, ofBlock(BlockLoader.metaCasing02, 4))))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFFSET,
            VERTICAL_OFFSET,
            DEPTH_OFFSET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        mCountCasing = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) {
            return;
        }
        checkCasingMin(errors, mCountCasing, 1);
    }

    // ==================== Recipe Processing ====================

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.OreProcessingRecipes;
    }

    @Override
    public int getMaxParallelRecipes() {
        return MAX_PARALLEL;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        List<ItemStack> inputs = getStoredInputs();
        if (inputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        RecipeMap<?> recipeMap = getRecipeMap();
        if (recipeMap == null) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        ProcessingResult result = processInputs(inputs, recipeMap);

        if (result.outputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        applyProcessingResult(result);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    /**
     * Process all inputs and collect outputs
     */
    private ProcessingResult processInputs(List<ItemStack> inputs, RecipeMap<?> recipeMap) {
        List<ItemStack> outputs = new ArrayList<>();
        int totalParallel = 0;

        // Process each input with recipe lookup
        for (ItemStack input : inputs) {
            if (input == null || input.stackSize <= 0) continue;

            GTRecipe recipe = recipeMap.findRecipeQuery()
                .items(input)
                .find();

            if (recipe != null) {
                int parallel = Math.min(input.stackSize, MAX_PARALLEL - totalParallel);
                if (parallel <= 0) break;

                processRecipe(input, recipe, parallel, outputs);
                totalParallel += parallel;

                if (totalParallel >= MAX_PARALLEL) break;
            }
        }

        // Transfer unprocessed items to output
        transferUnprocessedItems(inputs, outputs);

        return new ProcessingResult(outputs, totalParallel);
    }

    /**
     * Process a single recipe with parallel multiplier
     */
    private void processRecipe(ItemStack input, GTRecipe recipe, int parallel, List<ItemStack> outputs) {
        input.stackSize -= parallel;

        for (ItemStack output : recipe.mOutputs) {
            if (output != null) {
                outputs.add(GTUtility.copyAmountUnsafe(output.stackSize * parallel, output));
            }
        }
    }

    /**
     * Transfer any unprocessed items to output to avoid input clogging
     */
    private void transferUnprocessedItems(List<ItemStack> inputs, List<ItemStack> outputs) {
        for (ItemStack input : inputs) {
            if (input != null && input.stackSize > 0) {
                outputs.add(input.copy());
                input.stackSize = 0;
            }
        }
    }

    /**
     * Apply processing result to machine state
     */
    private void applyProcessingResult(ProcessingResult result) {
        this.mOutputItems = result.outputs.toArray(new ItemStack[0]);
        this.mEUt = EU_PER_TICK;
        this.mMaxProgresstime = DURATION_TICKS;
        updateSlots();
    }

    /**
     * Helper class to hold processing results
     */
    private static class ProcessingResult {

        final List<ItemStack> outputs;
        final int totalParallel;

        ProcessingResult(List<ItemStack> outputs, int totalParallel) {
            this.outputs = outputs;
            this.totalParallel = totalParallel;
        }
    }

    // ==================== Rendering ====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        int id = getCasingTextureID();
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_SINGULARITY_DATA_HUB)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 36);
    }

    // ==================== Tooltip ====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeOreProcessorRecipeType"))
            // #tr Tooltip_LargeOreProcessor_00
            // # A large ore processing machine
            // # zh_CN 大型矿石处理机
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeOreProcessor_00"))
            // #tr Tooltip_LargeOreProcessor_01
            // # Processes all types of ores from input bus simultaneously
            // # zh_CN 同时处理输入总线中的所有种类矿石
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeOreProcessor_01"))
            // #tr Tooltip_LargeOreProcessor_02
            // # No energy or lubricant required!
            // # zh_CN 不需要任何能源或润滑油！
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeOreProcessor_02"))
            // #tr Tooltip_LargeOreProcessor_03
            // # Supports unlimited parallel recipes
            // # zh_CN 支持无限并行配方
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeOreProcessor_03"))
            // #tr Tooltip_LargeOreProcessor_04
            // # Unprocessable items auto-transfer to output bus
            // # zh_CN 无法处理的物品自动转至输出总线
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeOreProcessor_04"))
            .beginStructureBlock(3, 2, 2, false)
            .addInputBus("Any Input Bus", 1)
            .addOutputBus("Any Output Bus", 1)
            .addMaintenanceHatch("Any Maintenance Hatch", 1)
            .toolTipFinisher(RESOURCE_ROOT_ID);
        return tt;
    }
}
