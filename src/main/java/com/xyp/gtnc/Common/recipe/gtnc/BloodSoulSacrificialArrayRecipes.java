package com.xyp.gtnc.Common.recipe.gtnc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import WayofTime.alchemicalWizardry.ModItems;
import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipe;
import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipeRegistry;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipe;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import WayofTime.alchemicalWizardry.api.bindingRegistry.BindingRecipe;
import WayofTime.alchemicalWizardry.api.bindingRegistry.BindingRegistry;
import WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorComponent;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public final class BloodSoulSacrificialArrayRecipes {

    private static boolean loaded;

    private BloodSoulSacrificialArrayRecipes() {}

    // #tr NEI.BloodSoulSacrificialArray.specialValue
    // # Base LP Cost: %s LP
    // # zh_CN 基础 LP 消耗：%s LP
    public static void loadRecipes() {
        if (loaded || !Mods.BloodMagic.isModLoaded()) return;
        loaded = true;
        loadBloodDemonInjectionRecipes();
        loadAlchemicChemistryRecipes();
        loadFallingTowerRecipes();
    }

    private static void loadBloodDemonInjectionRecipes() {
        for (AltarRecipe recipe : AltarRecipeRegistry.altarRecipes) {
            if (recipe.requiredItem == null || recipe.result == null) continue;
            GTValues.RA.stdBuilder()
                .itemInputs(recipe.requiredItem.copy())
                .itemOutputs(recipe.result.copy())
                .eut(0)
                .specialValue(Math.max(0, recipe.liquidRequired))
                .duration(128)
                .addTo(GTNCRecipeMaps.BloodDemonInjectionRecipes);
        }

        for (BindingRecipe recipe : BindingRegistry.bindingRecipes) {
            if (recipe.requiredItem == null || recipe.outputItem == null) continue;
            GTValues.RA.stdBuilder()
                .itemInputs(
                    recipe.requiredItem.copy(),
                    new ItemStack(ModItems.weakBloodShard, 1),
                    GTUtility.getIntegratedCircuit(11))
                .itemOutputs(recipe.outputItem.copy())
                .eut(0)
                .specialValue(30000)
                .duration(128)
                .addTo(GTNCRecipeMaps.BloodDemonInjectionRecipes);
        }
    }

    private static void loadAlchemicChemistryRecipes() {
        for (AlchemyRecipe recipe : AlchemyRecipeRegistry.recipes) {
            ItemStack[] inputs = recipe.getRecipe();
            ItemStack output = recipe.getResult();
            if (inputs == null || inputs.length == 0 || output == null) continue;

            ItemStack[] copiedInputs = new ItemStack[inputs.length];
            boolean valid = true;
            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i] == null) {
                    valid = false;
                    break;
                }
                copiedInputs[i] = inputs[i].copy();
            }
            if (!valid) continue;

            GTValues.RA.stdBuilder()
                .itemInputs(copiedInputs)
                .itemOutputs(output.copy())
                .specialValue(Math.max(0, recipe.getAmountNeeded() * 2))
                .eut(0)
                .duration(128)
                .addTo(GTNCRecipeMaps.AlchemicChemistrySetRecipes);
        }
    }

    private static void loadFallingTowerRecipes() {
        ItemStack industrialTnt = GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockNuke", 1);
        ItemStack bloodTnt = GTModHandler.getModItem(Mods.BloodArsenal.ID, "blood_tnt", 1);

        for (Meteor meteor : MeteorRegistry.meteorList) {
            MeteorData data = new MeteorData(meteor);
            if (data.input != null && (isSameItem(data.input, industrialTnt) || isSameItem(data.input, bloodTnt)))
                continue;
            if (data.outputs.isEmpty()) continue;

            GTRecipeBuilder builder = GTValues.RA.stdBuilder();
            if (data.input != null) builder.itemInputs(data.input);
            builder.itemOutputs(data.outputs.toArray(new ItemStack[0]))
                .outputChances(
                    data.chances.stream()
                        .mapToInt(Integer::intValue)
                        .toArray())
                .specialValue(Math.max(0, data.cost))
                .eut(0)
                .duration(Math.max(1, data.totalExpectedAmount / 2))
                .addTo(GTNCRecipeMaps.FallingTowerRecipes);
        }
    }

    private static boolean isSameItem(ItemStack first, ItemStack second) {
        return first != null && second != null && first.isItemEqual(second);
    }

    private static final class MeteorData {

        private final ItemStack input;
        private final List<ItemStack> outputs = new ArrayList<>();
        private final List<Integer> chances = new ArrayList<>();
        private final int cost;
        private int totalExpectedAmount;

        private MeteorData(Meteor meteor) {
            input = meteor.focusItem == null ? null : meteor.focusItem.copy();
            cost = meteor.cost;
            float fillerRatio = meteor.fillerChance / 100.0f;
            processComponents(meteor.ores, 1.0f - fillerRatio, meteor.radius);
            if (meteor.fillerChance > 0) processComponents(meteor.filler, fillerRatio, meteor.radius);
        }

        private void processComponents(List<MeteorComponent> source, float ratio, int radius) {
            if (source == null || source.isEmpty() || ratio <= 0) return;
            List<MeteorComponent> components = new ArrayList<>(source);
            components.sort(
                Comparator.comparingInt(MeteorComponent::getWeight)
                    .reversed());
            int totalWeight = components.stream()
                .mapToInt(MeteorComponent::getWeight)
                .sum();
            if (totalWeight <= 0) return;

            for (MeteorComponent component : components) {
                float chance = component.getWeight() / (float) totalWeight * ratio;
                ItemStack output = component.getBlock();
                if (output == null || chance <= 0) continue;
                output = output.copy();
                output.stackSize = Math
                    .max(1, (int) Math.ceil(4.0 / 3.0 * Math.PI * Math.pow(radius + 0.5, 3) * chance));
                outputs.add(output);
                chances.add(Math.max(1, (int) (chance * 20000)));
                totalExpectedAmount += output.stackSize;
            }
        }
    }
}
