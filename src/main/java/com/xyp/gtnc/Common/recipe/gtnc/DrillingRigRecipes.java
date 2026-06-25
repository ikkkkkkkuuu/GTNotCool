package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class DrillingRigRecipes {

    /** Helper: add recipe with fuel input, output, circuit, tier */
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

        // 钻井燃料: 石脑油(5000L) / 重燃油(3000L)
        FluidStack fuelT1a = Materials.Naphtha.getFluid(5000);
        FluidStack fuelT1b = Materials.HeavyFuel.getFluid(3000);

        // ==================== 管道等级 1 (基础管道) ====================
        Object[][] tier1 = {
            { Materials.Hydrogen.getGas(50000000), 128, 600, 1 }, { Materials.Helium.getGas(50000000), 128, 600, 2 },
            { Materials.Nitrogen.getGas(50000000), 128, 600, 3 }, { Materials.Methane.getGas(5000000), 128, 600, 4 },
            { Materials.SulfurDioxide.getGas(5000000), 128, 600, 5 },
            { Materials.CarbonDioxide.getGas(5000000), 128, 600, 6 },
            { Materials.NitrogenDioxide.getGas(5000000), 128, 600, 7 },
            { Materials.Ammonia.getGas(5000000), 128, 600, 8 }, { Materials.Chlorine.getGas(50000000), 128, 600, 9 },
            { Materials.Fluorine.getGas(50000000), 128, 600, 10 },
            { Materials.CarbonMonoxide.getGas(50000000), 128, 600, 11 },
            { Materials.Oxygen.getGas(50000000), 128, 600, 12 }, };

        for (Object[] r : tier1) {
            addDR(DRR, fuelT1a, (FluidStack) r[0], (int) r[1], (int) r[2], 1, (int) r[3]);
            addDR(DRR, fuelT1b, (FluidStack) r[0], (int) r[1], (int) (r[2]) / 2, 1, (int) r[3]);
        }

        // ==================== 管道等级 2 (钢管道) ====================
        // 燃料: 轻燃油(8000L) / 柴油(5000L)
        FluidStack fuelT2a = Materials.LightFuel.getFluid(8000);
        FluidStack fuelT2b = Materials.LightFuel.getFluid(4000);

        Object[][] tier2 = {
            { FluidRegistry.getFluidStack("unknowwater", 500000), 256, 400, 1 },
            { FluidRegistry.getFluidStack("Neon", 500000), 256, 400, 2 },
            { Materials.Argon.getGas(500000), 256, 400, 3 },
            { FluidRegistry.getFluidStack("Krypton", 500000), 256, 400, 4 },
            { FluidRegistry.getFluidStack("Xenon", 500000), 256, 400, 5 },
            { Materials.Radon.getGas(500000), 256, 400, 6 }, { Materials.Helium3.getGas(50000000), 256, 400, 7 }, };

        for (Object[] r : tier2) {
            FluidStack out = (FluidStack) r[0];
            if (out == null) continue;
            addDR(DRR, fuelT2a, out, (int) r[1], (int) r[2], 2, (int) r[3]);
            addDR(DRR, fuelT2b, out, (int) r[1], (int) (r[2]) / 2, 2, (int) r[3]);
        }

        // ==================== 管道等级 3 (含锇管道) ====================
        // 燃料: 硝基柴油(6000L)
        FluidStack fuelT3 = Materials.HeavyFuel.getFluid(8000);

        FluidStack[] tier3 = {
            Materials.Deuterium.getGas(5000000), Materials.Tritium.getGas(5000000),
            Materials.HeavyFuel.getFluid(5000000), Materials.LightFuel.getFluid(5000000),
            Materials.Naphtha.getFluid(5000000), Materials.Gas.getGas(50000000),
            FluidRegistry.getFluidStack("CoalGas", 5000000), FluidRegistry.getFluidStack("Bromine", 5000000),
            Materials.Oil.getFluid(5000000), Materials.OilHeavy.getFluid(500000),
            Materials.Lava.getFluid(50000000), Materials.SaltWater.getFluid(50000000),
            GTModHandler.getDistilledWater(50000000), FluidRegistry.getFluidStack("Pyrotheum", 500000),
            FluidRegistry.getFluidStack("Cryotheum", 500000), GTModHandler.getLiquidDNA(500000), };

        for (int i = 0; i < tier3.length; i++) {
            if (tier3[i] == null) continue;
            addDR(DRR, fuelT3, tier3[i], 2048, 200, 3, i + 1);
        }

        // ==================== 管道等级 4 (联苯管道) ====================
        FluidStack fuelT4 = Materials.HeavyFuel.getFluid(10000);
        FluidStack[] tier4 = { Materials.HydrochloricAcid.getFluid(5000000),
            Materials.SulfuricAcid.getFluid(5000000), Materials.NitricAcid.getFluid(5000000),
            Materials.HydrofluoricAcid.getFluid(5000000), Materials.PhosphoricAcid.getFluid(5000000),
            Materials.PhthalicAcid.getFluid(5000000), };

        for (int i = 0; i < tier4.length; i++) {
            addDR(DRR, fuelT4, tier4[i], 8192, 150, 4, i + 1);
        }

        // ==================== 管道等级 5 (铌钛管道) ====================
        FluidStack fuelT5 = Materials.HeavyFuel.getFluid(15000);
        FluidStack[] tier5 = { Materials.Iron.getMolten(12000), Materials.Lead.getMolten(14000),
            Materials.Copper.getMolten(10000), Materials.Gold.getMolten(8000), };

        for (int i = 0; i < tier5.length; i++) {
            addDR(DRR, fuelT5, tier5[i], 32768, 200, 5, i + 1);
        }

        // ==================== 管道等级 6 (量子管道) ====================
        FluidStack fuelT6 = Materials.HeavyFuel.getFluid(20000);
        addDR(DRR, fuelT6, Materials.WhiteDwarfMatter.getMolten(50000), 131072, 750, 6, 1);
        addDR(DRR, fuelT6, Materials.BlackDwarfMatter.getMolten(50000), 131072, 750, 6, 2);
    }
}
