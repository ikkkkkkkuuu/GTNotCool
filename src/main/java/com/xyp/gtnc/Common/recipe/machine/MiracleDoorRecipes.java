package com.xyp.gtnc.Common.recipe.machine;

import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

/**
 * Crafting recipes for the Miracle Door multiblock (controller) and its two consumable items (Critical Photon fuel and
 * White Dwarf Mold). Everything is EV-tier so the machine is reachable in the early-late game, matching the requested
 * tech level.
 */
public class MiracleDoorRecipes {

    public static void loadRecipes() {

        // 奇迹之门 (控制器) —— EV 阶段装配机配方
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Hull_EV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.TungstenSteel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.TungstenSteel, 8),
                GTOreDictUnificator.get(OrePrefixes.cableGt04, Materials.Aluminium, 8))
            .fluidInputs(Materials.Titanium.getMolten(1152))
            .itemOutputs(GTNCItemList.MiracleDoor.get(1))
            .eut(RECIPE_EV)
            .duration(60 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 临界光子 (燃料) —— 每次运行消耗，装配机批量产出
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 1))
            .circuit(1)
            .fluidInputs(Materials.Radon.getGas(1000))
            .itemOutputs(GTNCItemList.MiracleDoorPhoton.get(16))
            .eut(RECIPE_EV)
            .duration(10 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);

        // 白矮星模具 (催化触发器，不消耗) —— 装配机产出
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2))
            .fluidInputs(Materials.Iridium.getMolten(576))
            .itemOutputs(GTNCItemList.MiracleDoorMold.get(1))
            .eut(RECIPE_EV)
            .duration(30 * SECONDS)
            .addTo(RecipeMaps.assemblerRecipes);
    }
}
