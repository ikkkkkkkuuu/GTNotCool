package com.xyp.gtnc.Common.recipe.gtnc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.ScienceNotCool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.objects.ItemData;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;

public class CombProcessingRecipes {

    private static final RecipeMap<?> RM = GTNCRecipeMaps.SteamCombProcessingRecipes;

    public static void loadRecipes() {
        ScienceNotCool.LOG.info("Loading Steam Comb Processing Recipes...");
        importFromNativeMaps();
        ScienceNotCool.LOG.info("Loaded Steam Comb Processing Recipes");
    }

    // ==================== helpers ====================

    private static ItemStack d(Materials m, long n) {
        return GTOreDictUnificator.get(OrePrefixes.dust, m, n);
    }

    // ==================== import from native recipe maps ====================
    // 通过反射扫描 RecipeMaps 和 GTPPRecipeMaps 中所有 RecipeMap 字段，
    // 确保 GT5U、GT++、gtnhmod 等所有 mod 注册的蜂窝配方都能被同步到我们的 SteamCombProcessingRecipes。

    private static void importFromNativeMaps() {
        Set<Item> combItems = buildCombItemSet();
        if (combItems.isEmpty()) return;

        List<IRecipeMap> mapsToScan = new ArrayList<>();
        collectRecipeMaps(RecipeMaps.class, mapsToScan);
        try {
            collectRecipeMaps(Class.forName("gtPlusPlus.api.recipe.GTPPRecipeMaps"), mapsToScan);
        } catch (ClassNotFoundException ignored) {}
        ScienceNotCool.LOG.info("Scanning {} recipe maps for comb recipes", mapsToScan.size());

        // 去重：用输入物品的唯一标识做 key
        Set<String> added = new HashSet<>();
        int imported = 0;
        for (IRecipeMap nativeMap : mapsToScan) {
            if (nativeMap == null || !(nativeMap instanceof RecipeMap)) continue;
            for (GTRecipe recipe : ((RecipeMap<?>) nativeMap).getAllRecipes()) {
                if (recipe.mInputs == null || recipe.mInputs.length == 0) continue;
                if (!hasCombInput(recipe.mInputs, combItems)) continue;
                if (!added.add(recipeKey(recipe))) continue;
                copyRecipe(recipe);
                imported++;
            }
        }
        ScienceNotCool.LOG.info("Imported {} native comb recipes into SteamCombProcessingRecipes", imported);
    }

    private static void collectRecipeMaps(Class<?> clazz, List<IRecipeMap> out) {
        for (Field f : clazz.getDeclaredFields()) {
            if (!IRecipeMap.class.isAssignableFrom(f.getType())) continue;
            try {
                IRecipeMap map = (IRecipeMap) f.get(null);
                if (map != null) out.add(map);
            } catch (Exception ignored) {}
        }
    }

    private static String recipeKey(GTRecipe recipe) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack is : recipe.mInputs) {
            if (is != null && is.getItem() != null) {
                sb.append(Item.getIdFromItem(is.getItem()))
                    .append(':')
                    .append(is.getItemDamage())
                    .append(';');
            }
        }
        return sb.toString();
    }

    private static Set<Item> buildCombItemSet() {
        Set<Item> items = new HashSet<>();
        for (CombType t : CombType.values()) {
            ItemStack stack = GTBees.combs.getStackForType(t);
            if (stack != null && stack.getItem() != null) {
                items.add(stack.getItem());
            }
        }
        return items;
    }

    private static boolean hasCombInput(ItemStack[] inputs, Set<Item> combItems) {
        for (ItemStack is : inputs) {
            if (is != null && is.getItem() != null && combItems.contains(is.getItem())) {
                return true;
            }
        }
        return false;
    }

    private static void copyRecipe(GTRecipe recipe) {
        // 去掉原图的流体输入（酸液）
        // 输出中的 crushedPurified 转成最终 dust
        ItemStack[] outputs = recipe.mOutputs;
        if (outputs != null) {
            ItemStack[] converted = new ItemStack[outputs.length];
            for (int i = 0; i < outputs.length; i++) {
                converted[i] = convertCrushedPurifiedToDust(outputs[i]);
            }
            outputs = converted;
        }
        GTRecipeBuilder b = GTRecipeBuilder.builder()
            .itemInputs(recipe.mInputs)
            .itemOutputs(outputs)
            .fluidOutputs(recipe.mFluidOutputs)
            .eut(recipe.mEUt)
            .duration(recipe.mDuration)
            .special(recipe.mSpecialValue);
        if (recipe.mOutputChances != null) b.outputChances(recipe.mOutputChances);
        b.addTo(RM);
    }

    private static ItemStack convertCrushedPurifiedToDust(ItemStack is) {
        if (is == null) return null;
        ItemData assoc = GTOreDictUnificator.getAssociation(is);
        if (assoc != null && assoc.mPrefix == OrePrefixes.crushedPurified
            && assoc.mMaterial != null
            && assoc.mMaterial.mMaterial != null) {
            return d(assoc.mMaterial.mMaterial, is.stackSize);
        }
        return is;
    }

}
