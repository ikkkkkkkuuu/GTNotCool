package com.xyp.gtnc.Common.recipe.gtnc;

import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_HV;
import static gregtech.api.enums.TierEU.RECIPE_IV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_MV;

import net.minecraftforge.fluids.FluidRegistry;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class DrillingRigRecipes {

    public static void loadRecipes() {

        RecipeMap<?> DRR = GTNCRecipeMaps.DrillingRigRecipes;

        // 钻井
        // 岩浆
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.Lava.getFluid(24000))
            .eut(RECIPE_MV)
            .duration(20 * 5)
            .circuit(1)
            .specialValue(1)
            .addTo(DRR);

        // 天然气
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.NaturalGas.getGas(18000))
            .eut(RECIPE_MV)
            .duration(70)
            .circuit(2)
            .specialValue(1)
            .addTo(DRR);
        // 原油
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.OilMedium.getFluid(18000))
            .eut(RECIPE_HV)
            .duration(20 * 5)
            .circuit(3)
            .specialValue(1)
            .addTo(DRR);
        // 轻油
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.OilLight.getFluid(22000))
            .eut(RECIPE_HV)
            .duration(20 * 3)
            .circuit(4)
            .specialValue(1)
            .addTo(DRR);
        // 重油
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.OilHeavy.getFluid(14000))
            .eut(RECIPE_HV)
            .duration(20 * 7)
            .circuit(5)
            .specialValue(1)
            .addTo(DRR);
        // 极重油
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.OilExtraHeavy.getFluid(24000))
            .eut(RECIPE_IV)
            .duration(20 * 8)
            .circuit(6)
            .specialValue(1)
            .addTo(DRR);
        // 盐水
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.SaltWater.getFluid(16000))
            .eut(RECIPE_EV)
            .duration(20 * 4)
            .circuit(7)
            .specialValue(2)
            .addTo(DRR);
        // 蒸馏水
        GTValues.RA.stdBuilder()
            .fluidOutputs(GTModHandler.getDistilledWater(24000))
            .eut(RECIPE_HV)
            .duration(20 * 4)
            .circuit(8)
            .specialValue(2)
            .addTo(DRR);
        // 氯苯
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.Chlorobenzene.getFluid(20000))
            .eut(RECIPE_EV)
            .duration(20 * 8)
            .circuit(9)
            .specialValue(3)
            .addTo(DRR);
        // 硫酸
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.SulfuricAcid.getFluid(18000))
            .eut(RECIPE_EV)
            .duration(20 * 10)
            .circuit(10)
            .specialValue(3)
            .addTo(DRR);
        // 不明液体
        GTValues.RA.stdBuilder()
            .fluidOutputs(FluidRegistry.getFluidStack("unknowwater", 12000))
            .eut(RECIPE_LuV)
            .duration(20 * 10)
            .circuit(11)
            .specialValue(5)
            .addTo(DRR);
        // 熔融铁
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.Iron.getMolten(12000))
            .eut(RECIPE_LuV)
            .duration(20 * 16)
            .circuit(12)
            .specialValue(4)
            .addTo(DRR);
        // 熔融铅
        GTValues.RA.stdBuilder()
            .fluidOutputs(Materials.Lead.getMolten(14000))
            .eut(RECIPE_LuV)
            .duration(20 * 16)
            .circuit(11)
            .specialValue(4)
            .addTo(DRR);
        // 末影粘浆
        GTValues.RA.stdBuilder()
            .fluidOutputs(FluidRegistry.getFluidStack("endergoo", 12000))
            .eut(RECIPE_EV)
            .duration(20 * 8)
            .circuit(14)
            .specialValue(3)
            .addTo(DRR);

    }
}
