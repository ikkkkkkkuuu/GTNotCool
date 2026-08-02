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
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.lang.TextLocalization;
import com.xyp.gtnc.utils.recipes.metadata.SolorMuonCatalystMetadata;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.godforge.MTESmeltingModule;
import tectech.thing.metaTileEntity.multi.godforge.upgrade.ForgeOfGodsUpgrade;
import tectech.thing.metaTileEntity.multi.godforge.util.ForgeOfGodsData;

/**
 * GTNL Solar Muon Catalyst module adapted for the Steam Forge of Gods.
 *
 * <p>
 * This version:
 * <ul>
 * <li>uses the Steam Forge controller and team wireless-steam network;</li>
 * <li>draws power continuously through {@link #drainEnergyInput(long, long)};</li>
 * <li>uses the common Steam Godforge module structure and GUI;</li>
 * <li>keeps GTNL's Solar Muon recipe map and full-upgrade metadata restriction.</li>
 * </ul>
 */

public class SteamGodforgeSolarMuonCatalystModule extends MTESmeltingModule
    implements SteamGodforgePower.ControllerAware {

    private long currentEUt;
    private int currentParallel;

    private SteamForgeOfGods steamController;

    public SteamGodforgeSolarMuonCatalystModule(int id, String name, String regionalName) {
        super(id, name, regionalName);
        useLongPower = true;
    }

    public SteamGodforgeSolarMuonCatalystModule(String name) {
        super(name);
        useLongPower = true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new SteamGodforgeSolarMuonCatalystModule(mName);
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
     * Converts the module's normal EU/t request into a draw from the Steam Forge's
     * team wireless-steam network.
     */
    @Override
    public boolean drainEnergyInput(long euPerTick, long amperes) {
        boolean success = SteamGodforgePower.drainEnergyInput(steamController, userUUID, euPerTick, amperes);

        if (success) {
            addToPowerTally(
                BigInteger.valueOf(euPerTick)
                    .abs()
                    .multiply(
                        BigInteger.valueOf(amperes)
                            .abs()));
        }

        return success;
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
                 * GTNL marks selected Solar Muon recipes with this Boolean metadata.
                 * Those recipes require all Godforge upgrades and at least three rings.
                 */
                if (recipe.getMetadataOrDefault(SolorMuonCatalystMetadata.INSTANCE, false)
                    && !hasAllRequiredUpgrades()) {
                    return SimpleCheckRecipeResult.ofFailure("not_enough_upgrade");
                }

                /*
                 * Do not check or deduct WirelessNetworkManager EU here.
                 * This Steam version consumes energy continuously through drainEnergyInput().
                 */
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                /*
                 * mSpecialValue is not used as heat by this recipe map.
                 * Perfect overclocking is enabled in setProcessingLogicPower().
                 */
                return super.createOverclockCalculator(recipe).setEUt(getSafeProcessingVoltage())
                    .setHeatDiscountMultiplier(getHeatEnergyDiscount());
            }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                currentEUt = calculatedEut;
                currentParallel = calculatedParallels;

                /*
                 * Do not call overwriteCalculatedEut(0).
                 * Keeping calculatedEut makes the module request steam energy every running tick.
                 */
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    /**
     * Replaces the original module's MTEForgeOfGods master field with this
     * project's SteamForgeOfGods controller reference.
     */
    public boolean hasAllRequiredUpgrades() {
        if (steamController == null) {
            return false;
        }

        ForgeOfGodsData data = steamController.getData();

        if (data.getRingAmount() < 3) {
            return false;
        }

        for (ForgeOfGodsUpgrade upgrade : ForgeOfGodsUpgrade.VALUES) {
            if (!data.isUpgradeActive(upgrade)) {
                return false;
            }
        }

        return true;
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
         * Use the same perfect-overclock path as the other Steam Godforge modules.
         * Do not also set durationDecreasePerOC in createOverclockCalculator(),
         * because that would override the perfect-overclock duration factor.
         */
        logic.enablePerfectOverclock();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNCRecipeMaps.SolarMuonCatalystRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTNCRecipeMaps.SolarMuonCatalystRecipes);
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -10;
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
        return new SteamGodforgeSmeltingModeModuleGui<>(this);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        return SteamGodforgeTooltips.module(
            TextLocalization.SteamGodforgeSolarMuonCatalystModuleMachineType,
            TextLocalization.Tooltip_SteamGodforgeSolarMuonCatalystModule_00,
            TextLocalization.Tooltip_SteamGodforgeSolarMuonCatalystModule_01,
            TextLocalization.Tooltip_SteamGodforgeSolarMuonCatalystModule_02,
            TextLocalization.Tooltip_SteamGodforgeSolarMuonCatalystModule_03,
            TextLocalization.Tooltip_SteamGodforgeSolarMuonCatalystModule_04,
            TextLocalization.Tooltip_SteamGodforgeSolarMuonCatalystModule_05,
            TextLocalization.Tooltip_SteamGodforge_perfect);
    }
}
