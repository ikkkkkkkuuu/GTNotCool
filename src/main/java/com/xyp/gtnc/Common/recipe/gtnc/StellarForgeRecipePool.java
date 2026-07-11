package com.xyp.gtnc.Common.recipe.gtnc;

import static gregtech.api.enums.TierEU.RECIPE_MV;
import static gregtech.api.enums.TierEU.RECIPE_UV;
import static gregtech.api.recipe.RecipeMaps.fluidExtractionRecipes;
import static gregtech.api.recipe.RecipeMaps.fluidSolidifierRecipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Sets;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GTUtility.ItemId;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class StellarForgeRecipePool {

    public static final HashSet<String> IngotHotOreDictNames = new HashSet<>();
    public static final HashSet<String> IngotOreDictNames = new HashSet<>();
    public static final HashSet<ItemId> IngotHots = new HashSet<>();
    public static final HashSet<ItemId> Ingots = new HashSet<>();
    public static final HashMap<ItemId, ItemStack> IngotHotToIngot = new HashMap<>();
    public static final HashSet<ItemId> SpecialRecipeOutputs = new HashSet<>();
    public static final HashMap<Fluid, ItemStack> MoltenToIngot = new HashMap<>();

    /**
     * Moved from Utils.
     */
    private static boolean itemStackArrayEqualFuzzy(ItemStack[] isa1, ItemStack[] isa2) {
        if (isa1.length != isa2.length) return false;
        for (ItemStack itemStack1 : isa1) {
            boolean flag = false;
            for (ItemStack itemStack2 : isa2) {
                if (GTUtility.areStacksEqual(itemStack1, itemStack2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    /**
     * Moved from Utils.
     */
    private static boolean fluidStackEqualFuzzy(FluidStack[] fsa1, FluidStack[] fsa2) {
        if (fsa1.length != fsa2.length) return false;
        for (FluidStack fluidStack1 : fsa1) {
            boolean flag = false;
            for (FluidStack fluidStack2 : fsa2) {
                if (GTUtility.areFluidsEqual(fluidStack1, fluidStack2)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return false;
        }
        return true;
    }

    public static void initData() {

        for (String name : OreDictionary.getOreNames()) {
            if (name.startsWith("ingotHot")) {
                IngotHotOreDictNames.add(name);
                for (ItemStack items : OreDictionary.getOres(name)) {
                    IngotHots.add(ItemId.createWithoutNBT(items));
                }
            } else if (name.startsWith("ingot")) {
                IngotOreDictNames.add(name);
                for (ItemStack items : OreDictionary.getOres(name)) {
                    Ingots.add(ItemId.createWithoutNBT(items));
                }
            }
        }

        // iterate Vacuum Freezer recipes
        for (GTRecipe recipeFreezer : RecipeMaps.vacuumFreezerRecipes.getAllRecipes()) {
            if (recipeFreezer.mInputs == null || recipeFreezer.mInputs.length < 1
                || recipeFreezer.mOutputs == null
                || recipeFreezer.mOutputs.length < 1) continue;
            ItemStack input = recipeFreezer.mInputs[0].copy();
            // check input is ingotHot
            // if (!IngotHots.contains(copyAmount(1, input))) continue;
            ItemStack outputIngot = recipeFreezer.mOutputs[0].copy();

            // add ingotHot - ingot to map
            IngotHotToIngot.put(ItemId.createWithoutNBT(input), outputIngot);

        }

        // add SpecialRecipeOutputs
        SpecialRecipeOutputs.add(ItemId.create(WerkstoffLoader.CubicZirconia.get(OrePrefixes.gemFlawed, 1)));
        SpecialRecipeOutputs.add(ItemId.create(Materials.MeteoricIron.getIngots(1)));
        SpecialRecipeOutputs.add(ItemId.create(Materials.MeteoricSteel.getIngots(1)));
        SpecialRecipeOutputs.add(ItemId.create(ItemList.Harmonic_Compound.get(1)));
        SpecialRecipeOutputs.add(ItemId.create(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 12129)));

        // generate molten fluid to ingot map
        for (GTRecipe recipeSolidifier : fluidSolidifierRecipes.getAllRecipes()) {
            if (recipeSolidifier.mInputs == null || recipeSolidifier.mInputs.length < 1
                || recipeSolidifier.mOutputs == null
                || recipeSolidifier.mOutputs.length < 1) continue;
            if (GTUtility.areStacksEqual(
                GTModHandler.getModItem(Mods.GregTech.ID, "gt.metaitem.01", 1, 32306),
                recipeSolidifier.mInputs[0])) {
                if (!GTUtility.areFluidsEqual(recipeSolidifier.mFluidInputs[0], Materials.AnnealedCopper.getMolten(1)))
                    MoltenToIngot.put(recipeSolidifier.mFluidInputs[0].getFluid(), recipeSolidifier.mOutputs[0]);
            }
            MoltenToIngot.put(
                Materials.AnnealedCopper.getMolten(1)
                    .getFluid(),
                Materials.AnnealedCopper.getIngots(1));
        }
    }

    private static void prepareEBFRecipes() {
        Set<Fluid> protectionGas = Sets.newHashSet(
            Materials.Hydrogen.mGas,
            Materials.Oxygen.mGas,
            Materials.Nitrogen.mGas,
            WerkstoffLoader.Xenon.getFluidOrGas(1)
                .getFluid(),
            WerkstoffLoader.Oganesson.getFluidOrGas(1)
                .getFluid(),
            WerkstoffLoader.Krypton.getFluidOrGas(1)
                .getFluid(),
            WerkstoffLoader.Neon.getFluidOrGas(1)
                .getFluid(),
            Materials.Radon.mGas,
            Materials.Argon.mGas,
            Materials.Helium.mGas);

        ItemStack nullItem = new ItemStack(Blocks.fire, 1).setStackDisplayName("Null Item");

        for (GTRecipe recipe : RecipeMaps.blastFurnaceRecipes.getAllRecipes()) {
            if (recipe.mOutputs.length == 1 && SpecialRecipeOutputs.contains(ItemId.create(recipe.mOutputs[0])))
                continue;

            ArrayList<ItemStack> inputItems = new ArrayList<>();
            ArrayList<FluidStack> inputFluids = new ArrayList<>();
            ArrayList<ItemStack> outputItems = new ArrayList<>();
            ArrayList<FluidStack> outputFluids = new ArrayList<>();

            // process Item input
            byte integrateNum = 0;
            for (ItemStack inputs : recipe.mInputs) {

                if (null == inputs) {
                    inputItems.add(nullItem);
                    continue;
                }

                if (GTUtility.areStacksEqual(inputs, GTUtility.getIntegratedCircuit(1))) {
                    integrateNum = 1;
                    continue;
                }
                if (GTUtility.areStacksEqual(inputs, GTUtility.getIntegratedCircuit(11))) {
                    integrateNum = 11;
                    continue;
                }

                inputItems.add(inputs.copy());

            }

            // process Item output
            for (ItemStack outputs : recipe.mOutputs) {

                if (null == outputs) {
                    outputItems.add(nullItem);
                    continue;
                }

                ItemId outputItemID = ItemId.createWithoutNBT(outputs);
                boolean isRecipeAdded = false;
                if (IngotHots.contains(outputItemID)) {
                    // if this output item is Hot Ingot
                    ItemStack normalIngot = IngotHotToIngot.get(outputItemID);

                    FluidStack fluidStack = getMoltenFluids(normalIngot, outputs.stackSize);
                    if (fluidStack != null) {
                        outputFluids.add(fluidStack);
                        isRecipeAdded = true;
                    }

                } else if (Ingots.contains(outputItemID)) {
                    // if this output item is normal Ingot
                    FluidStack fluidStack = getMoltenFluids(GTUtility.copyAmountUnsafe(1, outputs), outputs.stackSize);
                    if (fluidStack != null) {
                        outputFluids.add(fluidStack);
                        isRecipeAdded = true;
                    }
                }
                if (!isRecipeAdded) {
                    // if this output item is not Ingot
                    outputItems.add(outputs.copy());
                }
            }

            // process Fluid input
            for (FluidStack fluids : recipe.mFluidInputs) {
                if (!protectionGas.contains(fluids.getFluid())) {
                    inputFluids.add(fluids.copy());
                }
            }

            // process Fluid output
            for (FluidStack fluids : recipe.mFluidOutputs) {
                outputFluids.add(fluids.copy());
            }

            // New Alloy Recipe in Blast Furnace conflicts with some single item recipes
            if (integrateNum != 0 || inputItems.size() < 2) {
                inputItems.add(GTUtility.getIntegratedCircuit(1));
            }

            ItemStack[] inputItemsArray = inputItems.toArray(new ItemStack[0]);
            FluidStack[] outputFluidsArray = outputFluids.toArray(new FluidStack[0]);
            boolean canAddNewRecipe = true;

            int duration = Math.max(1, recipe.mDuration / 3);
            if (integrateNum != 0) {
                for (GTRecipe recipeCheck : GTNCRecipeMaps.StellarForgeRecipes.getAllRecipes()) {
                    if (!itemStackArrayEqualFuzzy(recipeCheck.mInputs, inputItemsArray)) continue;
                    if (!fluidStackEqualFuzzy(recipeCheck.mFluidOutputs, outputFluidsArray)) continue;
                    canAddNewRecipe = false;
                    recipeCheck.mDuration = Math.min(recipeCheck.mDuration, duration);
                    break;
                }
            }

            // add to recipe map
            if (canAddNewRecipe) {

                // Move the Integrated Circuit to the end of the recipe
                int InputItemLength = inputItemsArray.length;
                for (int i = 0; i < InputItemLength; i++) {
                    if (inputItemsArray[i].getItem() == ItemList.Circuit_Integrated.getItem()
                        && i != InputItemLength - 1) {
                        ItemStack IntegratedCircuit = inputItemsArray[i];
                        for (int j = i; j < InputItemLength - 1; j++) {
                            inputItemsArray[j] = inputItemsArray[j + 1];
                        }
                        inputItemsArray[InputItemLength - 1] = IntegratedCircuit;
                        break;
                    }
                }

                addToMiracleDoorRecipes(
                    inputItemsArray,
                    inputFluids.toArray(new FluidStack[0]),
                    outputItems.toArray(new ItemStack[0]),
                    outputFluidsArray,
                    recipe.mEUt,
                    Math.max(1, recipe.mDuration / 3),
                    GTNCRecipeMaps.StellarForgeRecipes);
            }

        }
    }

    private static void prepareABSRecipes() {

        for (GTRecipe recipe : GTPPRecipeMaps.alloyBlastSmelterRecipes.getAllRecipes()) {
            int minOutputFluidAmount = 144;
            // if there is more than one output fluid, find the fewest
            for (FluidStack aOutputFluid : recipe.mFluidOutputs) {
                // if (aOutputFluid.amount % 144 == 0) continue;
                minOutputFluidAmount = Math.min(minOutputFluidAmount, aOutputFluid.amount);
            }
            ArrayList<ItemStack> inputItemList = new ArrayList<>();
            ArrayList<ItemStack> outputItemList = new ArrayList<>();
            ArrayList<FluidStack> inputFluidList = new ArrayList<>();
            ArrayList<FluidStack> outputFluidList = new ArrayList<>();
            ArrayList<FluidStack> outputFluidListTemp = new ArrayList<>();

            int RecipeMultiplier = 1;
            if (minOutputFluidAmount < 144) {
                int CorrectFluidAmount = minOutputFluidAmount;
                while (CorrectFluidAmount % 144 != 0) {
                    CorrectFluidAmount += minOutputFluidAmount;
                }
                RecipeMultiplier = CorrectFluidAmount / minOutputFluidAmount;
            }
            // Multiply the recipe

            if (recipe.mInputs != null) for (ItemStack aItemStack : recipe.mInputs) {
                ItemStack aStackCopy = aItemStack.copy();
                aStackCopy.stackSize *= RecipeMultiplier;
                inputItemList.add(aStackCopy);
            }

            if (recipe.mFluidInputs != null) for (FluidStack aFluidStack : recipe.mFluidInputs) {
                FluidStack aFluidCopy = aFluidStack.copy();
                aFluidCopy.amount *= RecipeMultiplier;
                inputFluidList.add(aFluidCopy);
            }

            if (recipe.mOutputs != null) for (ItemStack aItemStack : recipe.mOutputs) {
                if (getMoltenFluids(aItemStack, 1) != null) {
                    outputFluidListTemp.add(getMoltenFluids(aItemStack, aItemStack.stackSize));
                } else {
                    ItemStack aStackCopy = aItemStack.copy();
                    aStackCopy.stackSize *= RecipeMultiplier;
                    outputItemList.add(aStackCopy);
                }

            }

            if (recipe.mOutputs != null) for (FluidStack aFluidStack : recipe.mFluidOutputs) {
                FluidStack aFluidCopy = aFluidStack.copy();
                aFluidCopy.amount *= RecipeMultiplier;
                outputFluidListTemp.add(aFluidCopy);
            }

            for (FluidStack aFluidTemp : outputFluidListTemp) {
                boolean isFluidRepetitious = false;
                for (FluidStack aFluidOut : outputFluidList) {
                    if (GTUtility.areFluidsEqual(aFluidTemp, aFluidOut)) {
                        aFluidOut.amount += aFluidTemp.amount;
                        isFluidRepetitious = true;
                    }
                }

                if (!isFluidRepetitious) {
                    outputFluidList.add(aFluidTemp);
                }
            }

            // Move the Integrated Circuit to the start of the recipe
            for (int i = 0; i < inputItemList.size(); i++) {
                if (inputItemList.get(i)
                    .getItem() == ItemList.Circuit_Integrated.getItem() && i != 0) {
                    ItemStack integratedCircuit = inputItemList.remove(i);
                    inputItemList.add(0, integratedCircuit);
                    break;
                }
            }

            addToMiracleDoorRecipes(
                inputItemList.toArray(new ItemStack[0]),
                inputFluidList.toArray(new FluidStack[0]),
                outputItemList.toArray(new ItemStack[0]),
                outputFluidList.toArray(new FluidStack[0]),
                recipe.mEUt,
                recipe.mDuration,
                GTNCRecipeMaps.StellarForgeAlloySmelterRecipes);
        }

    }

    private static void addToMiracleDoorRecipes(ItemStack[] inputItems, FluidStack[] inputFluids,
        ItemStack[] outputItems, FluidStack[] outputFluids, int eut, int duration, IRecipeMap aRecipeMap) {

        // 1) Base recipe: molten-fluid output, no mold. No special marker so the terminal autofill
        // never counts a phantom mold as an output.
        GTValues.RA.stdBuilder()
            .itemInputs(inputItems != null ? inputItems : new ItemStack[0])
            .fluidInputs(inputFluids != null ? inputFluids : new FluidStack[0])
            .itemOutputs(outputItems != null ? outputItems : new ItemStack[0])
            .fluidOutputs(outputFluids != null ? outputFluids : new FluidStack[0])
            .eut(eut)
            .duration(duration)
            .addTo(aRecipeMap);

        // 2) Ingot variant: same recipe but with the White Dwarf Mold as a size-0 (non-consumed) input,
        // and every convertible molten fluid output turned into solid ingots. Only registered when at
        // least one output fluid actually maps to an ingot, so the mold is a real NEI input (not a marker).
        addIngotVariant(inputItems, inputFluids, outputItems, outputFluids, eut, duration, aRecipeMap);
    }

    /**
     * Register the ingot-output variant of a recipe: prepend the (non-consumed) White Dwarf Mold to the item
     * inputs and convert molten fluid outputs into ingots via {@link #MoltenToIngot}. Does nothing if the mold
     * item is not registered or no output fluid can be converted.
     */
    private static void addIngotVariant(ItemStack[] inputItems, FluidStack[] inputFluids, ItemStack[] outputItems,
        FluidStack[] outputFluids, int eut, int duration, IRecipeMap aRecipeMap) {
        if (!GTNCItemList.MiracleDoorMold.hasBeenSet()) return;
        if (outputFluids == null || outputFluids.length == 0) return;

        ArrayList<FluidStack> remainingFluids = new ArrayList<>();
        ArrayList<ItemStack> ingotOutputs = new ArrayList<>();
        boolean converted = false;
        for (FluidStack fluidStack : outputFluids) {
            if (fluidStack == null) continue;
            ItemStack ingot = MoltenToIngot.get(fluidStack.getFluid());
            if (ingot == null || fluidStack.amount < 144) {
                remainingFluids.add(fluidStack.copy());
            } else {
                int ingotAmount = fluidStack.amount / 144;
                ingotOutputs.add(GTUtility.copyAmountUnsafe(ingotAmount, ingot));
                int remainder = fluidStack.amount - 144 * ingotAmount;
                if (remainder > 0) remainingFluids.add(new FluidStack(fluidStack.getFluid(), remainder));
                converted = true;
            }
        }
        if (!converted) return;

        // original inputs first, then the mold (size 0, non-consumed) at the end. The mold must NOT be the
        // first size-0 input, otherwise the terminal's getRecipeName picks the mold's damage (32) instead of
        // the programmed circuit's number (e.g. 2) when auto-filling the interface search name.
        ArrayList<ItemStack> ingotInputs = new ArrayList<>();
        if (inputItems != null) {
            for (ItemStack s : inputItems) {
                if (s != null) ingotInputs.add(s.copy());
            }
        }
        ingotInputs.add(GTNCItemList.MiracleDoorMold.get(0));

        ArrayList<ItemStack> allItemOutputs = new ArrayList<>();
        if (outputItems != null) {
            for (ItemStack s : outputItems) {
                if (s != null) allItemOutputs.add(s.copy());
            }
        }
        allItemOutputs.addAll(ingotOutputs);

        GTValues.RA.stdBuilder()
            .itemInputs(ingotInputs.toArray(new ItemStack[0]))
            .fluidInputs(inputFluids != null ? inputFluids : new FluidStack[0])
            .itemOutputs(allItemOutputs.toArray(new ItemStack[0]))
            .fluidOutputs(remainingFluids.toArray(new FluidStack[0]))
            .eut(eut)
            .duration(duration)
            .addTo(aRecipeMap);
    }

    private static FluidStack getMoltenFluids(ItemStack ingot, int ingotAmount) {
        FluidStack out = null;
        for (GTRecipe recipeMolten : fluidExtractionRecipes.getAllRecipes()) {
            if (GTUtility.areStacksEqual(ingot, recipeMolten.mInputs[0])) {
                if (recipeMolten.mFluidOutputs[0] != null) {
                    out = recipeMolten.mFluidOutputs[0].copy();
                    out.amount = 144 * ingotAmount;
                }
                break;
            }
        }
        return out;
    }

    private static void loadManualRecipes() {

        // Meteoric Iron
        addToMiracleDoorRecipes(
            new ItemStack[] { Materials.MeteoricIron.getDust(1), GTUtility.getIntegratedCircuit(1) },
            null,
            null,
            new FluidStack[] { Materials.MeteoricIron.getMolten(144) },
            (int) RECIPE_MV,
            20 * 25,
            GTNCRecipeMaps.StellarForgeRecipes);

        // Meteoric Steel
        addToMiracleDoorRecipes(
            new ItemStack[] { Materials.MeteoricIron.getDust(1), GTUtility.getIntegratedCircuit(2) },
            null,
            null,
            new FluidStack[] { Materials.MeteoricSteel.getMolten(144) },
            (int) RECIPE_MV,
            20 * 10,
            GTNCRecipeMaps.StellarForgeRecipes);

        // Neutronium
        addToMiracleDoorRecipes(
            new ItemStack[] { Materials.Neutronium.getDust(1), GTUtility.getIntegratedCircuit(1) },
            null,
            null,
            new FluidStack[] { Materials.Neutronium.getMolten(144) },
            (int) RECIPE_UV,
            20 * 112,
            GTNCRecipeMaps.StellarForgeRecipes);

    }

    public static Collection<GTRecipe> stellarForgeRecipeListCache;

    private void cacheRecipeList() {
        stellarForgeRecipeListCache = new HashSet<>(GTNCRecipeMaps.StellarForgeRecipes.getAllRecipes());
    }

    private void loadRecipeListCache() {
        for (GTRecipe recipe : stellarForgeRecipeListCache) {
            GTNCRecipeMaps.StellarForgeRecipes.addRecipe(recipe);
        }
    }

    public static void loadRecipes() {
        initData();
    }

    public static void loadOnServerStarted() {
        prepareEBFRecipes();
        prepareABSRecipes();
        loadManualRecipes();
    }
}
