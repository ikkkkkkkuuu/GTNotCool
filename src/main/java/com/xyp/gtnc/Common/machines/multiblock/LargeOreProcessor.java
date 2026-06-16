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

    private static final int MAX_PARALLEL = Integer.MAX_VALUE;
    private static final int DURATION_TICKS = 20;

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeOreProcessor(this.mName);
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LOP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_ore_processor";
    private static final String[][] shape = StructureUtils.readStructureFromFile(LOP_STRUCTURE_FILE_PATH);

    public LargeOreProcessor(String aName) {
        super(aName);
    }

    public LargeOreProcessor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;

    private int mCountCasing = 0;

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

    @Override
    public IStructureDefinition<LargeOreProcessor> getStructureDefinition() {
        return StructureDefinition.<LargeOreProcessor>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
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
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) {
            return;
        }
        checkCasingMin(errors, mCountCasing, 1);
    }

    @Override
    public int getMaxParallelRecipes() {
        return MAX_PARALLEL;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.OreProcessingRecipes;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        // 直接从所有输入总线获取物品
        List<ItemStack> tInput = getStoredInputs();
        if (tInput.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        RecipeMap<?> recipeMap = getRecipeMap();
        if (recipeMap == null) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        List<ItemStack> outputs = new ArrayList<>();
        int totalParallel = 0;

        // 检查每个输入物品
        for (ItemStack input : tInput) {
            if (input == null || input.stackSize <= 0) continue;

            // 遍历所有配方查找匹配的
            for (GTRecipe recipe : recipeMap.getAllRecipes()) {
                if (recipe.mInputs == null || recipe.mInputs.length < 1) continue;

                ItemStack recipeInput = recipe.mInputs[0];
                // 使用更宽松的匹配：只检查物品 ID 和 damage，不检查 NBT
                if (GTUtility.areStacksEqual(recipeInput, input, false)) {
                    // 找到配方！计算可以处理的并行数
                    int parallel = Math.min(input.stackSize, MAX_PARALLEL - totalParallel);

                    if (parallel <= 0) break;

                    // 消耗输入
                    input.stackSize -= parallel;

                    // 添加产出
                    for (ItemStack output : recipe.mOutputs) {
                        if (output != null) {
                            outputs.add(GTUtility.copyAmountUnsafe(output.stackSize * parallel, output));
                        }
                    }

                    totalParallel += parallel;

                    // 如果这个物品还有剩余，继续尝试匹配其他配方
                    if (input.stackSize > 0 && totalParallel < MAX_PARALLEL) {
                        continue;
                    } else {
                        break; // 这个物品处理完了或者达到最大并行
                    }
                }
            }

            // 达到最大并行限制，停止处理
            if (totalParallel >= MAX_PARALLEL) break;
        }

        // 将无法被配方处理的剩余物品转到输出总线，避免堵塞输入
        for (ItemStack input : tInput) {
            if (input != null && input.stackSize > 0) {
                outputs.add(input.copy());
                input.stackSize = 0;
            }
        }

        // 如果没有任何产出（配方产出 + 未处理物品）
        if (outputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        // 设置输出和运行参数 - 完全不消耗能源
        this.mOutputItems = outputs.toArray(new ItemStack[0]);
        this.mEUt = 0; // 不消耗任何能源
        this.mMaxProgresstime = DURATION_TICKS;

        // 更新槽位
        updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public int getCasingTextureID() {
        return GTUtility.getTextureId((byte) 116, (byte) 36);
    }

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
