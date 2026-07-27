package com.xyp.gtnc.Common.recipe.gtnc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.cropsnh.api.ICropMutation;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.farming.registries.MutationRegistry;
import com.gtnewhorizon.cropsnh.reference.Names;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;

import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;

public final class SteamCropBreederRecipes {

    private SteamCropBreederRecipes() {}

    public static void loadRecipes() {
        for (ICropMutation mutation : MutationRegistry.instance.getDeterministicMutations()) {
            if (mutation.getOutput()
                .hideFromNEI()) {
                continue;
            }

            List<Object> inputs = new ArrayList<>();
            for (var parent : mutation.getParents()) {
                ItemStack seed = parent.getSeedItem(SeedStats.DEFAULT_ANALYZED);
                seed.stackSize = 1;
                seed.getTagCompound()
                    .removeTag(Names.NBT.gain);
                seed.getTagCompound()
                    .removeTag(Names.NBT.growth);
                seed.getTagCompound()
                    .removeTag(Names.NBT.resistance);
                inputs.add(seed);
            }

            List<List<ItemStack>> catalysts = mutation.getBreedingMachineCatalystsForNEI(true);
            if (catalysts != null) {
                for (List<ItemStack> alternatives : catalysts) {
                    if (alternatives != null && !alternatives.isEmpty()) {
                        inputs.add(alternatives.toArray(new ItemStack[0]));
                    }
                }
            }

            GTRecipeBuilder builder = GTRecipeBuilder.builder()
                .itemInputs(inputs.toArray())
                .itemOutputs(
                    mutation.getOutput()
                        .getSeedItem(SeedStats.DEFAULT_ANALYZED))
                .duration(Math.max(1, mutation.getBreedingMachineRecipeDuration()))
                .eut(Math.max(1, mutation.getBreedingMachineRecipeEUt()))
                .nbtSensitive();

            if (catalysts == null || catalysts.isEmpty()) {
                builder.addTo(GTNCRecipeMaps.LargeSteamCropBreederRecipes);
                continue;
            }

            builder.forceOreDictInput()
                .buildWithAlt()
                .map(GTRecipe.GTRecipe_WithAlt.class::cast)
                .ifPresent(GTNCRecipeMaps.LargeSteamCropBreederRecipes::add);
        }
    }
}
