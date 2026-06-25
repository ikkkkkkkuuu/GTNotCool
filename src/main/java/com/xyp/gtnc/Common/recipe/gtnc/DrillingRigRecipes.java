package com.xyp.gtnc.Common.recipe.gtnc;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class DrillingRigRecipes {

    public static void loadRecipes() {

        RecipeMap<?> DRR = GTNCRecipeMaps.DrillingRigRecipes;

        // ==================== 管道等级 1 (基础管道) ====================

        // 氢气
        addDR(DRR, Materials.Hydrogen.getGas(50000000), 128, 300, 1, 1);
        // 氦气
        addDR(DRR, Materials.Helium.getGas(50000000), 128, 300, 1, 2);
        // 氮气
        addDR(DRR, Materials.Nitrogen.getGas(50000000), 128, 300, 1, 3);
        // 甲烷
        addDR(DRR, Materials.Methane.getGas(5000000), 128, 300, 1, 4);
        // 二氧化硫
        addDR(DRR, Materials.SulfurDioxide.getGas(5000000), 128, 300, 1, 5);
        // 二氧化碳
        addDR(DRR, Materials.CarbonDioxide.getGas(5000000), 128, 300, 1, 6);
        // 二氧化氮
        addDR(DRR, Materials.NitrogenDioxide.getGas(5000000), 128, 300, 1, 7);
        // 氨
        addDR(DRR, Materials.Ammonia.getGas(5000000), 128, 300, 1, 8);
        // 氯气
        addDR(DRR, Materials.Chlorine.getGas(50000000), 128, 300, 1, 9);
        // 氟气
        addDR(DRR, Materials.Fluorine.getGas(50000000), 128, 300, 1, 10);
        // 一氧化碳
        addDR(DRR, Materials.CarbonMonoxide.getGas(50000000), 128, 300, 1, 11);
        // 氧气
        addDR(DRR, Materials.Oxygen.getGas(50000000), 128, 300, 1, 12);

        // ==================== 管道等级 2 (钢管道) ====================

        // 不明液体
        addDR(DRR, FluidRegistry.getFluidStack("unknowwater", 500000), 512, 400, 2, 1);
        // 氖气
        addDR(DRR, FluidRegistry.getFluidStack("Neon", 500000), 512, 400, 2, 2);
        // 氩气
        addDR(DRR, Materials.Argon.getGas(500000), 512, 400, 2, 3);
        // 氪气
        addDR(DRR, FluidRegistry.getFluidStack("Krypton", 500000), 512, 400, 2, 4);
        // 氙气
        addDR(DRR, FluidRegistry.getFluidStack("Xenon", 500000), 512, 400, 2, 5);
        // 氡气
        addDR(DRR, Materials.Radon.getGas(500000), 512, 400, 2, 6);
        // 氦3
        addDR(DRR, Materials.Helium3.getGas(50000000), 512, 400, 2, 7);

        // ==================== 管道等级 3 (含锇管道) ====================

        // 氘
        addDR(DRR, Materials.Deuterium.getGas(5000000), 2048, 500, 3, 1);
        // 氚
        addDR(DRR, Materials.Tritium.getGas(5000000), 2048, 500, 3, 2);
        // 重油燃料
        addDR(DRR, Materials.HeavyFuel.getFluid(5000000), 2048, 500, 3, 3);
        // 轻油燃料
        addDR(DRR, Materials.LightFuel.getFluid(5000000), 2048, 500, 3, 4);
        // 石脑油
        addDR(DRR, Materials.Naphtha.getFluid(5000000), 2048, 500, 3, 5);
        // 天然气
        addDR(DRR, Materials.Gas.getGas(50000000), 2048, 500, 3, 6);
        // 煤气
        addDR(DRR, FluidRegistry.getFluidStack("CoalGas", 5000000), 2048, 500, 3, 7);
        // 溴
        addDR(DRR, FluidRegistry.getFluidStack("Bromine", 5000000), 2048, 500, 3, 8);
        // 原油
        addDR(DRR, Materials.Oil.getFluid(5000000), 2048, 500, 3, 9);
        // 重油
        addDR(DRR, Materials.OilHeavy.getFluid(500000), 2048, 500, 3, 10);
        // 岩浆
        addDR(DRR, Materials.Lava.getFluid(50000000), 2048, 500, 3, 11);
        // 盐水
        addDR(DRR, Materials.SaltWater.getFluid(50000000), 2048, 500, 3, 12);
        // 蒸馏水
        addDR(DRR, GTModHandler.getDistilledWater(50000000), 2048, 500, 3, 13);
        // 烈焰之炽焱
        addDR(DRR, FluidRegistry.getFluidStack("Pyrotheum", 500000), 2048, 500, 3, 14);
        // 极寒之凛冰
        addDR(DRR, FluidRegistry.getFluidStack("Cryotheum", 500000), 2048, 500, 3, 15);

        // ==================== 管道等级 4 (联苯管道) ====================

        // 液态DNA
        addDR(DRR, GTModHandler.getLiquidDNA(500000), 8192, 600, 4, 1);
        // 盐酸
        addDR(DRR, Materials.HydrochloricAcid.getFluid(5000000), 8192, 600, 4, 2);
        // 硫酸
        addDR(DRR, Materials.SulfuricAcid.getFluid(5000000), 8192, 600, 4, 3);
        // 硝酸
        addDR(DRR, Materials.NitricAcid.getFluid(5000000), 8192, 600, 4, 4);
        // 氢氟酸
        addDR(DRR, Materials.HydrofluoricAcid.getFluid(5000000), 8192, 600, 4, 5);
        // 磷酸
        addDR(DRR, Materials.PhosphoricAcid.getFluid(5000000), 8192, 600, 4, 6);
        // 邻苯二甲酸
        addDR(DRR, Materials.PhthalicAcid.getFluid(5000000), 8192, 600, 4, 7);

        // ==================== 管道等级 5 (铌钛管道) ====================

        // 熔融铁
        addDR(DRR, Materials.Iron.getMolten(12000), 32768, 800, 5, 1);
        // 熔融铅
        addDR(DRR, Materials.Lead.getMolten(14000), 32768, 800, 5, 2);
        // 熔融铜
        addDR(DRR, Materials.Copper.getMolten(10000), 32768, 800, 5, 3);
        // 熔融金
        addDR(DRR, Materials.Gold.getMolten(8000), 32768, 800, 5, 4);

        // ==================== 管道等级 6 (量子管道) ====================

        // 白矮星物质
        addDR(DRR, Materials.WhiteDwarfMatter.getMolten(50000), 131072, 1200, 6, 1);
        // 黑矮星物质
        addDR(DRR, Materials.BlackDwarfMatter.getMolten(50000), 131072, 1200, 6, 2);
    }

    private static void addDR(RecipeMap<?> map, FluidStack output, long eut, int duration, int tier, int circuit) {
        if (output == null) return;
        GTValues.RA.stdBuilder()
            .fluidOutputs(output)
            .eut(eut)
            .duration(duration)
            .circuit(circuit)
            .specialValue(tier)
            .addTo(map);
    }
}
