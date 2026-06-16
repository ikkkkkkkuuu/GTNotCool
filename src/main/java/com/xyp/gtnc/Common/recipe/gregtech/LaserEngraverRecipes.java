package com.xyp.gtnc.Common.recipe.gregtech;

import net.minecraft.item.ItemStack;

import com.xyp.gtnc.utils.enums.GTNCItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import tectech.thing.CustomItemList;

public class LaserEngraverRecipes {

    public static void loadRecipes() {

        loadWirelessEnergyCoverRecipes();
    }

    public static void loadWirelessEnergyCoverRecipes() {

        for (int j = 0; j < 14; j++) { // 电压等级：LV(0) → MAX(13)
            for (int i = 0; i < 13; i++) { // 安培档位：1A(0) → 16777216A(12)

                // LV ~ HV 不做 256A 以上
                if (j < 4 && i >= 4) continue;

                // 能量检测器：低安用Cover_EnergyDetector，256A+用Multi_Transformer
                ItemStack energyDetector = i >= 4 ? CustomItemList.Machine_Multi_Transformer.get(1)
                    : ItemList.Cover_EnergyDetector.get(1);

                // 能源舱来源：1A~64A 用普通舱，256A+ 用激光舱
                GTNCItemList[][] energyHatch;
                int hatchIndex;
                if (j < 4 || i < 4) {
                    energyHatch = GTNCItemList.ENERGY_HATCH;
                    hatchIndex = i;
                } else {
                    energyHatch = GTNCItemList.LASER_ENERGY_HATCH;
                    hatchIndex = i - 4;
                }

                // 覆盖版：1A/4A 用1A版，16A+ 用4A版
                GTNCItemList[] energyCover = i >= 2 ? GTNCItemList.WIRELESS_ENERGY_COVER_4A
                    : GTNCItemList.WIRELESS_ENERGY_COVER;

                // 覆盖版数量：1A=1, 4A=1, 16A=2, 64A=4, 256A+=4(封顶)
                long coverAmount = Math.min(1L << (i >= 2 ? i - 2 : i), 4L);

                GTRecipeBuilder.builder()
                    .itemInputs(
                        energyHatch[j >= 4 && i >= 4 ? j - 4 : j][hatchIndex].get(1), // 普通/激光能源舱
                        energyCover[j].get(coverAmount), // 无线覆盖版
                        energyDetector) // 检测器
                    .itemOutputs(GTNCItemList.WIRELESS_ENERGY_HATCHES[j][i].get(1))
                    .circuit(1)
                    .duration(200)
                    .eut(GTValues.VP[j + 1])
                    .addTo(RecipeMaps.laserEngraverRecipes);
            }
        }

        // 无线动力舱 LV~HV: 1A/4A/16A/64A
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 4; i++) {

                GTRecipeBuilder.builder()
                    .itemInputs(
                        GTNCItemList.ENERGY_HATCH[j][i].get(1),
                        GTNCItemList.WIRELESS_ENERGY_COVER[j].get(Math.min(1L << (i >= 2 ? i - 2 : i), 4L)),
                        ItemList.Cover_EnergyDetector.get(1))
                    .itemOutputs(GTNCItemList.WIRELESS_DYNAMO_HATCHES[j][i].get(1))
                    .circuit(2)
                    .duration(200)
                    .eut(GTValues.VP[j + 1])
                    .addTo(RecipeMaps.laserEngraverRecipes);
            }
        }

        // 无线动力舱 IV~MAX: 1A/4A/16A/64A/256A~16777216A
        int[] energyTierMap = { 0, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 13 };
        for (int j = 3; j < 12; j++) {
            int e = energyTierMap[j];
            for (int i = 0; i < 13; i++) {

                ItemStack energyDetector = i >= 4 ? CustomItemList.Machine_Multi_Transformer.get(1)
                    : ItemList.Cover_EnergyDetector.get(1);

                GTNCItemList[][] energyHatch;
                int hatchIndex;
                if (i < 4) {
                    energyHatch = GTNCItemList.ENERGY_HATCH;
                    hatchIndex = i;
                } else {
                    energyHatch = GTNCItemList.LASER_ENERGY_HATCH;
                    hatchIndex = i - 4;
                }

                GTNCItemList[] energyCover = i >= 2 ? GTNCItemList.WIRELESS_ENERGY_COVER_4A
                    : GTNCItemList.WIRELESS_ENERGY_COVER;
                long coverAmount = Math.min(1L << (i >= 2 ? i - 2 : i), 4L);

                int row = i >= 4 ? e - 4 : e;

                GTRecipeBuilder.builder()
                    .itemInputs(energyHatch[row][hatchIndex].get(1), energyCover[e].get(coverAmount), energyDetector)
                    .itemOutputs(GTNCItemList.WIRELESS_DYNAMO_HATCHES[j][i].get(1))
                    .circuit(2)
                    .duration(200)
                    .eut(GTValues.VP[e + 1])
                    .addTo(RecipeMaps.laserEngraverRecipes);
            }
        }

    }

}
