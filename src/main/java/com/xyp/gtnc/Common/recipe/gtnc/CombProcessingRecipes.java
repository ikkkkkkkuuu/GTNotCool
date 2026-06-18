
package com.xyp.gtnc.Common.recipe.gtnc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import gregtech.api.util.GTUtility;
import gregtech.common.items.ItemComb;

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
        List<IRecipeMap> mapsToScan = new ArrayList<>();
        collectRecipeMaps(RecipeMaps.class, mapsToScan);
        try {
            collectRecipeMaps(Class.forName("gtPlusPlus.api.recipe.GTPPRecipeMaps"), mapsToScan);
        } catch (ClassNotFoundException ignored) {}
        try {
            collectRecipeMaps(Class.forName("gregtech.api.recipe.GTRecipeConstants"), mapsToScan);
        } catch (ClassNotFoundException ignored) {}
        ScienceNotCool.LOG.info("Scanning {} recipe maps for comb recipes", mapsToScan.size());

        // 用反射获取 getAllRecipes，兼容非 RecipeMap 的 IRecipeMap 实现（如 UniversalChemical）
        Method getAllRecipesMethod = null;
        try {
            getAllRecipesMethod = IRecipeMap.class.getMethod("getAllRecipes");
        } catch (NoSuchMethodException e) {
            ScienceNotCool.LOG.warn("IRecipeMap.getAllRecipes() not found, falling back to instanceof RecipeMap");
        }

        // 第一遍：按输入分组收集候选配方，重复时保留最先遇到的
        // 同时拦截铂/超能/硅岩蜂窝配方（直接删除，由专用配方处理）
        Map<String, GTRecipe> candidates = new LinkedHashMap<>();
        int totalCandidates = 0;
        int specialCount = 0;
        List<ItemStack> platinumCombs = new ArrayList<>();
        List<ItemStack> naquadriaCombs = new ArrayList<>();
        List<ItemStack> naquadahCombs = new ArrayList<>();
        List<ItemStack> iridiumCombs = new ArrayList<>();
        List<ItemStack> osmiumCombs = new ArrayList<>();
        for (IRecipeMap nativeMap : mapsToScan) {
            if (nativeMap == null) continue;
            Collection<GTRecipe> recipes = null;
            if (getAllRecipesMethod != null) {
                try {
                    Object result = getAllRecipesMethod.invoke(nativeMap);
                    if (result instanceof Collection) {
                        @SuppressWarnings("unchecked")
                        Collection<GTRecipe> casted = (Collection<GTRecipe>) result;
                        recipes = casted;
                    }
                } catch (Exception ignored) {}
            }
            // 反射失败则回退到 instanceof RecipeMap
            if (recipes == null && nativeMap instanceof RecipeMap) {
                recipes = ((RecipeMap<?>) nativeMap).getAllRecipes();
            }
            if (recipes == null) continue;

            int mapFound = 0;
            for (GTRecipe recipe : recipes) {
                if (recipe.mInputs == null || recipe.mInputs.length == 0) continue;
                if (!hasCombInput(recipe.mInputs)) continue;
                totalCandidates++;
                // 铂/超能/硅岩蜂窝配方直接删除，不进入 candidates
                if (isPlatinumCombRecipe(recipe)) {
                    collectCombItems(recipe, platinumCombs, "Platinum", "platinum", "铂");
                    specialCount++;
                    continue;
                }
                if (isNaquadriaCombRecipe(recipe)) {
                    collectCombItems(recipe, naquadriaCombs, "Naquadria", "超能");
                    specialCount++;
                    continue;
                }
                if (isNaquadahCombRecipe(recipe)) {
                    collectCombItems(recipe, naquadahCombs, "Naquadah", "硅岩");
                    specialCount++;
                    continue;
                }
                if (isIridiumCombRecipe(recipe)) {
                    collectCombItems(recipe, iridiumCombs, "Iridium", "iridium", "铱");
                    specialCount++;
                    continue;
                }
                if (isOsmiumCombRecipe(recipe)) {
                    collectCombItems(recipe, osmiumCombs, "Osmium", "osmium", "锇");
                    specialCount++;
                    continue;
                }
                String key = recipeKey(recipe);
                GTRecipe existing = candidates.get(key);
                if (existing == null) {
                    candidates.put(key, recipe);
                    mapFound++;
                }
                // 重复配方直接跳过，保留先遇到的
            }
            if (mapFound > 0) {
                ScienceNotCool.LOG.info("  {} -> {} candidates", nativeMap.toString(), mapFound);
            }
        }

        // 第二遍：写入目标 RecipeMap
        for (Map.Entry<String, GTRecipe> entry : candidates.entrySet()) {
            copyRecipe(entry.getValue());
        }
        // 添加特殊配方（每种蜂窝类型合并为一条配方）
        if (!platinumCombs.isEmpty()) {
            addPlatinumRecipe(platinumCombs);
        }
        if (!naquadriaCombs.isEmpty()) {
            addNaquadriaRecipe(naquadriaCombs);
        }
        if (!naquadahCombs.isEmpty()) {
            addNaquadahRecipe(naquadahCombs);
        }
        if (!iridiumCombs.isEmpty()) {
            addIridiumRecipe(iridiumCombs);
        }
        if (!osmiumCombs.isEmpty()) {
            addOsmiumRecipe(osmiumCombs);
        }
        ScienceNotCool.LOG.info(
            "Imported {} comb recipes ({} total candidates, {} special) into SteamCombProcessingRecipes",
            candidates.size(),
            totalCandidates,
            specialCount);
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

    /** 多路检测：GT ItemComb + Forestry IItemBeeComb + 类名特征 */
    private static boolean hasCombInput(ItemStack[] inputs) {
        for (ItemStack is : inputs) {
            if (is == null || is.getItem() == null) continue;
            // 1) GT 蜂窝
            if (is.getItem() instanceof ItemComb) return true;
            // 2) Forestry 体系（ExtraBees/MagicBees/Gendustry）
            if (isForestryComb(is.getItem())) return true;
        }
        return false;
    }

    private static Boolean cachedForestryCombInterface = null;

    private static boolean isForestryComb(Item item) {
        // 尝试 Forestry IItemBeeComb 接口
        if (cachedForestryCombInterface == null) {
            try {
                Class<?> iface = Class.forName("forestry.api.apiculture.IItemBeeComb");
                cachedForestryCombInterface = true;
            } catch (ClassNotFoundException e) {
                cachedForestryCombInterface = false;
            }
        }
        if (cachedForestryCombInterface) {
            try {
                Class<?> iface = Class.forName("forestry.api.apiculture.IItemBeeComb");
                if (iface.isInstance(item)) return true;
            } catch (ClassNotFoundException ignored) {}
        }
        // 类名包含 Comb 的兜底（Gendustry 等）
        for (Class<?> c = item.getClass(); c != null; c = c.getSuperclass()) {
            String name = c.getSimpleName();
            if (name.contains("Comb") && !name.equals("ItemComb")) return true;
        }
        return false;
    }

    /** 检测配方输入中是否含铂蜂窝 */
    private static boolean isPlatinumCombRecipe(GTRecipe recipe) {
        for (ItemStack is : recipe.mInputs) {
            if (is == null || is.getItem() == null) continue;
            if (!isCombItem(is.getItem())) continue;
            String dn = is.getDisplayName();
            if (dn != null && (dn.contains("Platinum") || dn.contains("platinum") || dn.contains("铂"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCombItem(Item item) {
        if (item instanceof ItemComb) return true;
        return isForestryComb(item);
    }

    /** Naquadria 超能硅岩蜂窝检测（必须在 Naquadah 之前，因为 "Naquadria" 包含 "Naquadah" 子串） */
    private static boolean isNaquadriaCombRecipe(GTRecipe recipe) {
        for (ItemStack is : recipe.mInputs) {
            if (is == null || is.getItem() == null) continue;
            if (!isCombItem(is.getItem())) continue;
            String dn = is.getDisplayName();
            if (dn != null && (dn.contains("Naquadria") || dn.contains("超能"))) {
                return true;
            }
        }
        return false;
    }

    /** Naquadah 硅岩蜂窝检测（排除 Naquadria） */
    private static boolean isNaquadahCombRecipe(GTRecipe recipe) {
        for (ItemStack is : recipe.mInputs) {
            if (is == null || is.getItem() == null) continue;
            if (!isCombItem(is.getItem())) continue;
            String dn = is.getDisplayName();
            if (dn != null && ((dn.contains("Naquadah") && !dn.contains("Naquadria")) || dn.contains("硅岩"))) {
                return true;
            }
        }
        return false;
    }

    /** 检测配方输入中是否含铱蜂窝 */
    private static boolean isIridiumCombRecipe(GTRecipe recipe) {
        for (ItemStack is : recipe.mInputs) {
            if (is == null || is.getItem() == null) continue;
            if (!isCombItem(is.getItem())) continue;
            String dn = is.getDisplayName();
            if (dn != null && (dn.contains("Iridium") || dn.contains("iridium") || dn.contains("铱"))) {
                return true;
            }
        }
        return false;
    }

    /** 检测配方输入中是否含锇蜂窝 */
    private static boolean isOsmiumCombRecipe(GTRecipe recipe) {
        for (ItemStack is : recipe.mInputs) {
            if (is == null || is.getItem() == null) continue;
            if (!isCombItem(is.getItem())) continue;
            String dn = is.getDisplayName();
            if (dn != null && (dn.contains("Osmium") || dn.contains("osmium") || dn.contains("锇"))) {
                return true;
            }
        }
        return false;
    }

    /** 从配方中收集指定名称的蜂窝物品 */
    private static void collectCombItems(GTRecipe recipe, List<ItemStack> out, String... keywords) {
        for (ItemStack is : recipe.mInputs) {
            if (is == null || is.getItem() == null) continue;
            if (!isCombItem(is.getItem())) continue;
            String dn = is.getDisplayName();
            if (dn != null) {
                for (String kw : keywords) {
                    if (dn.contains(kw)) {
                        if (out.stream()
                            .noneMatch(s -> GTUtility.areStacksEqual(s, is))) {
                            out.add(is.copy());
                        }
                        break;
                    }
                }
            }
        }
    }

    /** 铂蜂窝特殊配方：所有铂蜂窝 -> 4铂粉 */
    private static void addPlatinumRecipe(List<ItemStack> combs) {
        GTRecipeBuilder.builder()
            .itemInputs(combs.toArray(new ItemStack[0]))
            .itemOutputs(d(Materials.Platinum, 4))
            .eut(30)
            .duration(100)
            .addTo(RM);
        ScienceNotCool.LOG.info("  Platinum comb special: {} comb types -> 4x Platinum dust", combs.size());
    }

    /** 超能硅岩配方 */
    private static void addNaquadriaRecipe(List<ItemStack> combs) {
        GTRecipeBuilder.builder()
            .itemInputs(combs.toArray(new ItemStack[0]))
            .fluidOutputs(Materials.Naquadria.getMolten(576))
            .eut(30)
            .duration(100)
            .addTo(RM);
        ScienceNotCool.LOG.info("  Naquadria comb special: {} comb types -> 576L molten naquadria", combs.size());
    }

    /** 普通硅岩配方 */
    private static void addNaquadahRecipe(List<ItemStack> combs) {
        GTRecipeBuilder.builder()
            .itemInputs(combs.toArray(new ItemStack[0]))
            .fluidOutputs(Materials.Naquadah.getMolten(576))
            .eut(30)
            .duration(100)
            .addTo(RM);
        ScienceNotCool.LOG.info("  Naquadah comb special: {} comb types -> 576L molten naquadah", combs.size());
    }

    /** 铱蜂窝特殊配方：所有铱蜂窝 -> 4铱粉 */
    private static void addIridiumRecipe(List<ItemStack> combs) {
        GTRecipeBuilder.builder()
            .itemInputs(combs.toArray(new ItemStack[0]))
            .itemOutputs(d(Materials.Iridium, 4))
            .eut(30)
            .duration(100)
            .addTo(RM);
        ScienceNotCool.LOG.info("  Iridium comb special: {} comb types -> 4x Iridium dust", combs.size());
    }

    /** 锇蜂窝特殊配方：所有锇蜂窝 -> 4锇粉 */
    private static void addOsmiumRecipe(List<ItemStack> combs) {
        GTRecipeBuilder.builder()
            .itemInputs(combs.toArray(new ItemStack[0]))
            .itemOutputs(d(Materials.Osmium, 4))
            .eut(30)
            .duration(100)
            .addTo(RM);
        ScienceNotCool.LOG.info("  Osmium comb special: {} comb types -> 4x Osmium dust", combs.size());
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
            .eut(30)
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
