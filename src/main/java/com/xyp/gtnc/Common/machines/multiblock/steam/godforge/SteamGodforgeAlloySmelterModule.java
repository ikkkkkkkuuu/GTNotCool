package com.xyp.gtnc.Common.machines.multiblock.steam.godforge;

import java.math.BigInteger;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.xyp.gtnc.Common.gui.modularui.multiblock.steam.SteamGodforgeSmeltingModeModuleGui;
import com.xyp.gtnc.utils.lang.TextLocalization;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
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

public class SteamGodforgeAlloySmelterModule extends MTESmeltingModule implements SteamGodforgePower.ControllerAware {

    private SteamForgeOfGods steamController;
    private long currentEUt;
    private int currentParallel;

    public SteamGodforgeAlloySmelterModule(int id, String name, String regionalName) {
        super(id, name, regionalName);
    }

    public SteamGodforgeAlloySmelterModule(String name) {
        super(name);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamGodforgeAlloySmelterModule(mName);
    }

    @Override
    public IStructureDefinition<? extends TTMultiblockBase> getStructure_EM() {
        return SteamGodforgeStructures.module();
    }

    @Override
    public boolean drainEnergyInput(long euPerTick, long amperes) {
        return SteamGodforgePower.drainEnergyInput(steamController, userUUID, euPerTick, amperes);
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
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamGodforgeSmeltingModeModuleGui<>(this);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return false;
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

                /*
                 * 不再检查 WirelessNetworkManager。
                 * 实际能源将在运行期间从蒸汽神锻控制器逐 tick 扣除。
                 */
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
        logic.enablePerfectOverclock();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.alloySmelterRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return SteamGodforgeTooltips.module(
            TextLocalization.SteamGodforgeAlloySmelterModuleMachineType,
            TextLocalization.Tooltip_SteamGodforgeAlloySmelterModule_00,
            TextLocalization.Tooltip_SteamGodforgeAlloySmelterModule_01,
            TextLocalization.Tooltip_SteamGodforgeAlloySmelterModule_02,
            TextLocalization.Tooltip_SteamGodforgeAlloySmelterModule_03,
            TextLocalization.Tooltip_SteamGodforgeAlloySmelterModule_04,
            TextLocalization.Tooltip_SteamGodforgeAlloySmelterModule_05,
            TextLocalization.Tooltip_SteamGodforge_perfect);
    }
}
