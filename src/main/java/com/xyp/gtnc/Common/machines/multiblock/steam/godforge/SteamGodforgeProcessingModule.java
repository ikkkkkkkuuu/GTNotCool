package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.SteamGodforgeSmeltingModeModuleGui;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.godforge.MTESmeltingModule;

/**
 * 蒸汽诸神之锻炉加工模块。
 *
 * <p>
 * 包含九种加工模式：
 * <ol>
 * <li>激光蚀刻机</li>
 * <li>切割机</li>
 * <li>卷板机</li>
 * <li>线材轧机</li>
 * <li>GT++ 工业搅拌机</li>
 * <li>组装机</li>
 * <li>压模机</li>
 * <li>流体固化机</li>
 * <li>压缩机</li>
 * </ol>
 *
 * <p>
 * 该模块使用蒸汽神锻的团队无线蒸汽供能、升级并行、速度和能耗加成，
 * 并启用无损完美超频。
 */

public class SteamGodforgeProcessingModule extends MTESmeltingModule implements SteamGodforgePower.ControllerAware {

    public static final int MODE_LASER_ENGRAVER = 0;
    public static final int MODE_CUTTER = 1;
    public static final int MODE_BENDER = 2;
    public static final int MODE_WIREMILL = 3;
    public static final int MODE_GTPP_MIXER = 4;
    public static final int MODE_ASSEMBLER = 5;
    public static final int MODE_FORMING_PRESS = 6;
    public static final int MODE_FLUID_SOLIDIFIER = 7;
    public static final int MODE_COMPRESSOR = 8;

    private static final int MODE_COUNT = 9;

    private static final String[] MODE_KEYS = { TextLocalization.SteamGodforgeProcessingModeLaserEngraverKey,
        TextLocalization.SteamGodforgeProcessingModeCutterKey, TextLocalization.SteamGodforgeProcessingModeBenderKey,
        TextLocalization.SteamGodforgeProcessingModeWiremillKey,
        TextLocalization.SteamGodforgeProcessingModeGTPPMixerKey,
        TextLocalization.SteamGodforgeProcessingModeAssemblerKey,
        TextLocalization.SteamGodforgeProcessingModeFormingPressKey,
        TextLocalization.SteamGodforgeProcessingModeFluidSolidifierKey,
        TextLocalization.SteamGodforgeProcessingModeCompressorKey };

    private long currentEUt;
    private int currentParallel;

    private SteamForgeOfGods steamController;

    public SteamGodforgeProcessingModule(int id, String name, String regionalName) {
        super(id, name, regionalName);
        useLongPower = true;
    }

    public SteamGodforgeProcessingModule(String name) {
        super(name);
        useLongPower = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamGodforgeProcessingModule(mName);
    }

    @Override
    public IStructureDefinition<? extends TTMultiblockBase> getStructure_EM() {
        return SteamGodforgeStructures.module();
    }

    @Override
    public void setSteamController(SteamForgeOfGods controller) {
        steamController = controller;
    }

    @Override
    public SteamForgeOfGods getSteamController() {
        return steamController;
    }

    /**
     * 把模块正常请求的 EU/t 转换为蒸汽神锻团队无线蒸汽消耗。
     */
    @Override
    public boolean drainEnergyInput(long euPerTick, long amperes) {
        return SteamGodforgePower.drainEnergyInput(steamController, userUUID, euPerTick, amperes);
    }

    private static boolean hasRealInput(GTRecipe recipe) {
        if (recipe.mInputs != null) {
            for (ItemStack input : recipe.mInputs) {
                if (input != null && input.getItem() != null && input.stackSize > 0) {
                    return true;
                }
            }
        }

        if (recipe.mFluidInputs != null) {
            for (FluidStack input : recipe.mFluidInputs) {
                if (input != null && input.getFluid() != null && input.amount > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean hasSafeItemOutputs(GTRecipe recipe) {
        if (recipe.mOutputs == null || recipe.mOutputs.length == 0) {
            return false;
        }

        for (ItemStack output : recipe.mOutputs) {
            /*
             * VoidProtectionHelper 会在过滤 null 之前调用 ItemId.create，
             * 因此数组里任何位置存在 null 都必须拒绝。
             */
            if (output == null || output.getItem() == null || output.stackSize <= 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (machineMode == MODE_ASSEMBLER) {
                    /*
                     * 拒绝 NEI 占位配方、禁用配方以及输入或输出不完整的异常配方。
                     */
                    if (recipe.mFakeRecipe || !recipe.mEnabled) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    if (!hasRealInput(recipe)) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }

                    if (!hasSafeItemOutputs(recipe)) {
                        return CheckRecipeResultRegistry.NO_RECIPE;
                    }
                }

                if (recipe.mEUt > getProcessingVoltage()) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUt(getSafeProcessingVoltage());
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                BigInteger totalSteam = BigInteger.valueOf(calculatedEut)
                    .abs()
                    .multiply(BigInteger.valueOf(duration));

                if (totalSteam.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                    return CheckRecipeResultRegistry.insufficientPower(Long.MAX_VALUE);
                }

                long totalSteamLong = totalSteam.longValue();

                /*
                 * SteamGodforgePower.drainEnergyInput() 每调用一次只扣一次。
                 * 把整条配方消耗作为第一个参数，倍率传 1。
                 */
                if (!SteamGodforgePower.drainEnergyInput(steamController, userUUID, totalSteamLong, 1L)) {

                    return CheckRecipeResultRegistry.insufficientPower(totalSteamLong);
                }

                currentEUt = calculatedEut;
                currentParallel = calculatedParallels;

                addToPowerTally(totalSteam);
                addToRecipeTally(calculatedParallels);

                /*
                 * 已经一次性支付整条配方能量。
                 * 清零以后，运行期间不会再调用正常的逐 tick 能源扣除。
                 */
                overwriteCalculatedEut(0);

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(Integer.MAX_VALUE);
        logic.setAmperageOC(false);
        logic.setUnlimitedTierSkips();
        logic.setMaxParallel(getActualParallel());
        logic.setSpeedBonus(getSpeedBonus());
        logic.setEuModifier(getEnergyDiscount());
        logic.enablePerfectOverclock();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        switch (machineMode) {
            case MODE_CUTTER:
                return RecipeMaps.cutterRecipes;
            case MODE_BENDER:
                return RecipeMaps.benderRecipes;
            case MODE_WIREMILL:
                return RecipeMaps.wiremillRecipes;
            case MODE_GTPP_MIXER:
                return GTPPRecipeMaps.mixerNonCellRecipes;
            case MODE_ASSEMBLER:
                return RecipeMaps.assemblerRecipes;
            case MODE_FORMING_PRESS:
                return RecipeMaps.formingPressRecipes;
            case MODE_FLUID_SOLIDIFIER:
                return RecipeMaps.fluidSolidifierRecipes;
            case MODE_COMPRESSOR:
                return RecipeMaps.compressorRecipes;
            case MODE_LASER_ENGRAVER:
            default:
                return RecipeMaps.laserEngraverRecipes;
        }
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            RecipeMaps.laserEngraverRecipes,
            RecipeMaps.cutterRecipes,
            RecipeMaps.benderRecipes,
            RecipeMaps.wiremillRecipes,
            GTPPRecipeMaps.mixerNonCellRecipes,
            RecipeMaps.assemblerRecipes,
            RecipeMaps.formingPressRecipes,
            RecipeMaps.fluidSolidifierRecipes,
            RecipeMaps.compressorRecipes);
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -10;
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        return (machineMode + 1) % MODE_COUNT;
    }

    @Override
    public void setMachineMode(int mode) {
        int validMode = mode;

        if (validMode < 0 || validMode >= MODE_COUNT) {
            validMode = MODE_LASER_ENGRAVER;
        }

        super.setMachineMode(validMode);
    }

    @Override
    public String getMachineModeKey() {
        int mode = machineMode;

        if (mode < 0 || mode >= MODE_KEYS.length) {
            mode = MODE_LASER_ENGRAVER;
        }

        return MODE_KEYS[mode];
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal(getMachineModeKey());
    }

    @Override
    public void outputAfterRecipe_EM() {
        super.outputAfterRecipe_EM();

        currentEUt = 0;
        currentParallel = 0;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>();

        info.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.progress",
                EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(mProgresstime / 20) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mMaxProgresstime / 20)
                    + EnumChatFormatting.RESET));

        info.add(
            StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.currently_using",
                EnumChatFormatting.RED
                    + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(currentEUt) : "0")
                    + EnumChatFormatting.RESET));

        info.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.max_parallel",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getActualParallel())));

        info.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                EnumChatFormatting.RESET
                    + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(currentParallel) : "0")));

        info.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.recipe_time",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getSpeedBonus())));

        info.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.energy",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getEnergyDiscount())));

        info.add(
            EnumChatFormatting.YELLOW
                + StatCollector.translateToLocal("gtnc.machine.steam_godforge_processing.info.mode")
                + ": "
                + EnumChatFormatting.RESET
                + getMachineModeName());

        return info.toArray(new String[0]);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamGodforgeSmeltingModeModuleGui<>(
            this,
            // 0 激光蚀刻机
            GTGuiTextures.OVERLAY_SLOT_LENS,
            // 1 切割机
            GTGuiTextures.OVERLAY_SLOT_CUTTER_SLICED,
            // 2 卷板机
            GTGuiTextures.OVERLAY_SLOT_BENDER,
            // 3 线材轧机
            GTGuiTextures.OVERLAY_SLOT_WIREMILL,
            // 4 搅拌机
            GTGuiTextures.OVERLAY_SLOT_BEAKER_1,
            // 5 组装机
            GTGuiTextures.OVERLAY_SLOT_CIRCUIT,
            // 6 压模机
            GTGuiTextures.OVERLAY_SLOT_PRESS_1,
            // 7 流体固化机
            GTGuiTextures.OVERLAY_SLOT_MOLD,
            // 8 压缩机
            GTGuiTextures.OVERLAY_SLOT_COMPRESSOR);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return SteamGodforgeTooltips.module(
            TextLocalization.SteamGodforgeProcessingModuleMachineType,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_00,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_01,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_02,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_03,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_04,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_05,
            TextLocalization.Tooltip_SteamGodforgeProcessingModule_06);
    }

}
