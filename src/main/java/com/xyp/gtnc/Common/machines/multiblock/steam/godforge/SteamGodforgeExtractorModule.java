package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

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
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.godforge.MTESmeltingModule;

public class SteamGodforgeExtractorModule extends MTESmeltingModule implements SteamGodforgePower.ControllerAware {

    private static final int MODE_EXTRACTOR = 0;
    private static final int MODE_FLUID_EXTRACTOR = 1;

    private long currentEUt;
    private int currentParallel;

    private SteamForgeOfGods steamController;

    public SteamGodforgeExtractorModule(int id, String name, String regionalName) {
        super(id, name, regionalName);

    }

    public SteamGodforgeExtractorModule(String name) {
        super(name);

    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamGodforgeExtractorModule(mName);
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

    @Override
    public boolean drainEnergyInput(long euPerTick, long amperes) {
        return SteamGodforgePower.drainEnergyInput(steamController, userUUID, euPerTick, amperes);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mEUt > getProcessingVoltage()) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUt(getSafeProcessingVoltage())
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setHeatOC(true)
                    .setHeatDiscount(true)
                    .setMachineHeat(Math.max(recipe.mSpecialValue, getHeatForOC()))
                    .setHeatDiscountMultiplier(getHeatEnergyDiscount());
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

                long steamCost = totalSteam.longValue();

                if (!SteamGodforgePower.drainEnergyInput(steamController, userUUID, steamCost, 1)) {

                    return CheckRecipeResultRegistry.insufficientPower(steamCost);
                }

                currentEUt = calculatedEut;
                currentParallel = calculatedParallels;

                addToPowerTally(totalSteam);
                setCurrentRecipeHeat(recipe.mSpecialValue);

                // 已提前支付全部蒸汽，运行过程中不再扣费。
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

        /*
         * 无损超频：
         * 每次超频功率乘 4、处理时间除以 4，总耗能保持不变。
         * createOverclockCalculator() 中不要再调用
         * setDurationDecreasePerOC(getOverclockTimeFactor())，
         * 否则会覆盖这里的无损超频时间倍率。
         */
        logic.enablePerfectOverclock();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return machineMode == MODE_FLUID_EXTRACTOR ? RecipeMaps.fluidExtractionRecipes : RecipeMaps.extractorRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.extractorRecipes, RecipeMaps.fluidExtractionRecipes);
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
        return machineMode == MODE_FLUID_EXTRACTOR ? MODE_EXTRACTOR : MODE_FLUID_EXTRACTOR;
    }

    @Override
    public void setMachineMode(int mode) {
        super.setMachineMode(mode == MODE_FLUID_EXTRACTOR ? MODE_FLUID_EXTRACTOR : MODE_EXTRACTOR);
    }

    @Override
    public String getMachineModeKey() {
        return machineMode == MODE_FLUID_EXTRACTOR ? TextLocalization.SteamGodforgeExtractorModuleModeFluidExtractorKey
            : TextLocalization.SteamGodforgeExtractorModuleModeExtractorKey;
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal(getMachineModeKey());
    }

    @Override
    public void outputAfterRecipe_EM() {
        super.outputAfterRecipe_EM();

        if (currentParallel > 0) {
            addToRecipeTally(currentParallel);
        }

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

        return info.toArray(new String[0]);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamGodforgeSmeltingModeModuleGui<>(
            this,
            GTGuiTextures.TT_OVERLAY_BUTTON_FURNACE_MODE_OFF,
            GTGuiTextures.TT_OVERLAY_BUTTON_FURNACE_MODE);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return SteamGodforgeTooltips.module(
            TextLocalization.SteamGodforgeExtractorModuleMachineType,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_00,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_01,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_02,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_03,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_04,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_05,
            TextLocalization.Tooltip_SteamGodforgeExtractorModule_06,
            TextLocalization.Tooltip_SteamGodforge_perfect);

    }

    @Override
    public double getSpeedBonus() {
        return processingSpeedBonus / 2;
    }

    @Override
    public double getEnergyDiscount() {
        return energyDiscount / 2;
    }
}
