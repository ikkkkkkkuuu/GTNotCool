package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import goodgenerator.items.GGMaterial;
import gtPlusPlus.core.fluids.GTPPFluids;

public class DrillingRigRecipes {

    /** Helper: add fuel→output recipe */
    private static void addDR(RecipeMap<?> map, FluidStack fuel, FluidStack output, long eut, int duration,
        int tier, int circuit) {
        if (fuel == null || output == null) return;
        GTValues.RA.stdBuilder()
            .fluidInputs(fuel)
            .fluidOutputs(output)
            .eut(eut)
            .duration(duration)
            .circuit(circuit)
            .specialValue(tier)
            .addTo(map);
    }

    public static void loadRecipes() {

        RecipeMap<?> DRR = GTNCRecipeMaps.DrillingRigRecipes;

        // ============ Tier 1 fuels: 优质汽油 / RP1火箭燃料 ============
        FluidStack fuel1a = Materials.GasolinePremium.getFluid(10000);
        FluidStack fuel1b = new FluidStack(GTPPFluids.RP1RocketFuel, 6000);

        FluidStack[] tier1Out = {
            Materials.Hydrogen.getGas(50000000), Materials.Helium.getGas(50000000),
            Materials.Nitrogen.getGas(50000000), Materials.Methane.getGas(5000000),
            Materials.SulfurDioxide.getGas(5000000), Materials.CarbonDioxide.getGas(5000000),
            Materials.NitrogenDioxide.getGas(5000000), Materials.Ammonia.getGas(5000000),
            Materials.Chlorine.getGas(50000000), Materials.Fluorine.getGas(50000000),
            Materials.CarbonMonoxide.getGas(50000000), Materials.Oxygen.getGas(50000000), };

        for (int i = 0; i < tier1Out.length; i++) {
            addDR(DRR, fuel1a, tier1Out[i], 512, 600, 1, i);
            addDR(DRR, fuel1b, tier1Out[i], 512, 300, 1, i);
        }

        // ============ Tier 2 fuels: 高密度肼燃料 / CN3H7O3火箭燃料 ============
        FluidStack fuel2a = new FluidStack(GTPPFluids.DenseHydrazineFuelMixture, 10000);
        FluidStack fuel2b = new FluidStack(GTPPFluids.CN3H7O3RocketFuel, 6000);

        FluidStack[] tier2Out = {
            GTModHandler.getDistilledWater(500000), Materials.Argon.getGas(500000),
            Materials.Radon.getGas(500000), Materials.Helium3.getGas(50000000), };

        for (int i = 0; i < tier2Out.length; i++) {
            addDR(DRR, fuel2a, tier2Out[i], 2048, 300, 2, i);
            addDR(DRR, fuel2b, tier2Out[i], 2048, 150, 2, i);
        }

        // ============ Tier 3 fuels: H8N4C2O4火箭燃料 / 钍基液态燃料 ============
        FluidStack fuel3a = new FluidStack(GTPPFluids.H8N4C2O4RocketFuel, 10000);
        // thoriumBasedLiquidFuel from GoodGenerator, fallback to HeavyFuel
        FluidStack fuel3b = GGMaterial.thoriumBasedLiquidFuel.getFluidOrGas(6000);

        FluidStack[] tier3Out = {
            Materials.Deuterium.getGas(5000000), Materials.Tritium.getGas(5000000),
            Materials.HeavyFuel.getFluid(5000000), Materials.LightFuel.getFluid(5000000),
            Materials.Naphtha.getFluid(5000000), Materials.Gas.getGas(50000000),
            Materials.Oil.getFluid(5000000), Materials.OilHeavy.getFluid(500000),
            Materials.Lava.getFluid(50000000), Materials.SaltWater.getFluid(50000000), };

        for (int i = 0; i < tier3Out.length; i++) {
            addDR(DRR, fuel3a, tier3Out[i], 8192, 150, 3, i);
            addDR(DRR, fuel3b, tier3Out[i], 8192, 75, 3, i);
        }

        // ============ Tier 4 fuels: H8N4C2O4 + 钍基替代 ============
        FluidStack[] tier4Out = { Materials.HydrochloricAcid.getFluid(5000000),
            Materials.SulfuricAcid.getFluid(5000000), Materials.NitricAcid.getFluid(5000000),
            Materials.HydrofluoricAcid.getFluid(5000000), Materials.PhosphoricAcid.getFluid(5000000),
            Materials.PhthalicAcid.getFluid(5000000), };

        for (int i = 0; i < tier4Out.length; i++) {
            addDR(DRR, fuel3a, tier4Out[i], 8192, 150, 4, i);
            addDR(DRR, fuel3b, tier4Out[i], 8192, 75, 4, i);
        }

        // ============ Tier 5 ============
        FluidStack[] tier5Out = { Materials.Iron.getMolten(12000), Materials.Lead.getMolten(14000),
            Materials.Copper.getMolten(10000), Materials.Gold.getMolten(8000), };

        for (int i = 0; i < tier5Out.length; i++) {
            addDR(DRR, fuel3a, tier5Out[i], 32768, 150, 5, i);
            addDR(DRR, fuel3b, tier5Out[i], 32768, 75, 5, i);
        }

        // ============ Tier 6: 五级硅岩基燃料 → 矮星物质 ============
        FluidStack fuel6 = Materials.HeavyFuel.getFluid(20000);
        addDR(DRR, fuel6, Materials.WhiteDwarfMatter.getMolten(50000), 131072, 750, 6, 0);
        addDR(DRR, fuel6, Materials.BlackDwarfMatter.getMolten(50000), 131072, 750, 6, 1);
    }
}
