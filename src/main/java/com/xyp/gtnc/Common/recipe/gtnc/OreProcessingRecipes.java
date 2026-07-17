package com.xyp.gtnc.Common.recipe.gtnc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Sets;
import com.xyp.gtnc.Loader.GTNCRecipeMaps;
import com.xyp.gtnc.ScienceNotCool;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import ic2.core.Ic2Items;

public class OreProcessingRecipes {

    public static final RecipeMap<?> OreProcessingRecipes = GTNCRecipeMaps.OreProcessingRecipes;

    // Recipe constants - no energy consumption
    private static final int EUT = 0;
    private static final int DURATION_TICKS = 20;

    // Ore stone types
    public static final Set<OrePrefixes> BASIC_STONE_TYPES = Sets.newHashSet(
        OrePrefixes.ore,
        OrePrefixes.oreBasalt,
        OrePrefixes.oreBlackgranite,
        OrePrefixes.oreRedgranite,
        OrePrefixes.oreMarble,
        OrePrefixes.oreNetherrack,
        OrePrefixes.oreEndstone);

    public static final Set<OrePrefixes> BASIC_STONE_TYPES_EXCEPT_NORMAL = Sets.newHashSet(
        OrePrefixes.oreBasalt,
        OrePrefixes.oreBlackgranite,
        OrePrefixes.oreRedgranite,
        OrePrefixes.oreMarble,
        OrePrefixes.oreNetherrack,
        OrePrefixes.oreEndstone);

    // Special processing line materials (PGM processing)
    private static final Map<Materials, ItemStack> PROCESSING_LINE_MATERIALS = new HashMap<>();

    // Special materials that need custom recipes
    private static final Set<Materials> SPECIAL_MATERIALS = Sets.newHashSet(
        Materials.Samarium,
        Materials.Cerium,
        Materials.Naquadah,
        Materials.NaquadahEnriched,
        Materials.Naquadria);

    /**
     * Main entry point - loads all ore processing recipes
     */
    public static void loadOreProcessingRecipes() {
        initProcessingLineMaterials();

        processGTMaterials();
        processBartworksOreRecipes();
        processGTPPOreRecipes();
        processSpecialOreRecipes();
        processIntermediateProducts();

        ScienceNotCool.LOG.info("Loaded ore processing recipes");
    }

    private static void initProcessingLineMaterials() {
        PROCESSING_LINE_MATERIALS.put(Materials.Platinum, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.dust, 1));
        PROCESSING_LINE_MATERIALS.put(Materials.Palladium, WerkstoffLoader.PDMetallicPowder.get(OrePrefixes.dust, 1));
        PROCESSING_LINE_MATERIALS.put(Materials.Iridium, WerkstoffLoader.IrLeachResidue.get(OrePrefixes.dust, 1));
        PROCESSING_LINE_MATERIALS.put(Materials.Osmium, WerkstoffLoader.IrOsLeachResidue.get(OrePrefixes.dust, 1));
        PROCESSING_LINE_MATERIALS
            .put(Materials.Samarium, WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 1));
        PROCESSING_LINE_MATERIALS
            .put(Materials.Cerium, WerkstoffMaterialPool.CeriumOreConcentrate.get(OrePrefixes.dust, 1));
    }

    // ==================== GT Materials Processing ====================

    private static void processGTMaterials() {
        Set<Materials> specialProcesses = new HashSet<>(SPECIAL_MATERIALS);

        for (int i = 0; i < GregTechAPI.sGeneratedMaterials.length; i++) {
            if (GregTechAPI.sGeneratedMaterials[i] == null) continue;

            Materials material = GregTechAPI.sGeneratedMaterials[i];

            // Skip special materials
            if (specialProcesses.remove(material)) {
                continue;
            }

            processGTMaterialOre(material, i);
        }
    }

    private static void processGTMaterialOre(Materials material, int materialID) {
        if (GTOreDictUnificator.get(OrePrefixes.ore, material, 1) == null) return;

        ItemStack[] normalOutputs = getOutputs(material, false);
        ItemStack[] richOutputs = getOutputs(material, true);

        // Normal stone ore. Must use the unificator's canonical ore stack, NOT
        // getModItem("gregtech","gt.blockores",...): in the GT5U 594 ore rework
        // that name resolves to the dead BlockOresLegacy block (no OreDict entry),
        // while real/void-mined Stone ore is now the new GTBlockOre. Registering
        // against the legacy item keyed the recipe to an item that never appears,
        // so Stone ores fell through unprocessed.
        addRecipe(GTOreDictUnificator.get(OrePrefixes.ore, material, 1), normalOutputs);

        // Raw ore
        addRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, material, 1), normalOutputs);

        // Other stone types
        for (OrePrefixes prefix : BASIC_STONE_TYPES_EXCEPT_NORMAL) {
            ItemStack ore = GTOreDictUnificator.get(prefix, material, 1);
            if (ore == null) {
                ScienceNotCool.LOG.info("Failed to get ore: material=" + material + ", prefix=" + prefix);
                continue;
            }
            addRecipe(ore, isRichOre(prefix) ? richOutputs : normalOutputs);
        }
    }

    private static ItemStack[] getOutputs(Materials material, boolean isRich) {
        List<ItemStack> outputs = new ArrayList<>();

        // Main output + byproducts
        if (material.mOreByProducts != null && !material.mOreByProducts.isEmpty()) {
            outputs.add(getDustStack(material, 4));

            if (material.mOreByProducts.size() == 1) {
                for (Materials byproduct : material.mOreByProducts) {
                    if (byproduct != null) {
                        outputs.add(getDustStack(byproduct, 3));
                    }
                }
            } else {
                for (Materials byproduct : material.mOreByProducts) {
                    if (byproduct == null || byproduct == Materials.Netherrack
                        || byproduct == Materials.Endstone
                        || byproduct == Materials.Stone) continue;
                    outputs.add(getDustStack(byproduct, 2));
                }
            }
        } else {
            outputs.add(getDustStack(material, 8));
        }

        // Gem outputs
        addGemOutputs(outputs, material);

        // Rich ore multiplier
        if (isRich) {
            outputs.forEach(stack -> { if (stack != null) stack.stackSize *= 2; });
        }

        return outputs.toArray(new ItemStack[0]);
    }

    private static void addGemOutputs(List<ItemStack> outputs, Materials material) {
        ItemStack gem = GTOreDictUnificator.get(OrePrefixes.gem, material, 1);
        if (gem == null) return;

        ItemStack gemExquisite = GTOreDictUnificator.get(OrePrefixes.gemExquisite, material, 1);
        if (gemExquisite != null) {
            outputs.add(gemExquisite);
            outputs.add(GTOreDictUnificator.get(OrePrefixes.gemFlawless, material, 2));
        }
        outputs.add(gem);
    }

    private static ItemStack getDustStack(Materials material, int amount) {
        ItemStack special = PROCESSING_LINE_MATERIALS.get(material);
        if (special != null) {
            return GTUtility.copyAmountUnsafe(amount * 3, special);
        }
        return GTUtility.copyAmountUnsafe(amount, GTOreDictUnificator.get(OrePrefixes.dust, material, 1));
    }

    // ==================== Intermediate Products Processing ====================

    private static void processIntermediateProducts() {
        for (Materials material : GregTechAPI.sGeneratedMaterials) {
            if (material == null) continue;

            // Crushed ore → same outputs as normal ore
            processIntermediateForm(material, OrePrefixes.crushed, m -> getOutputs(m, false));

            // Purified crushed ore (washed) → same full ore outputs
            processIntermediateForm(material, OrePrefixes.crushedPurified, m -> getOutputs(m, false));

            // Centrifuged crushed ore → same full ore outputs
            processIntermediateForm(material, OrePrefixes.crushedCentrifuged, m -> getOutputs(m, false));

            // Impure dust → main dust * 6
            processIntermediateForm(material, OrePrefixes.dustImpure, m -> new ItemStack[] { getDustStack(m, 6) });

            // Pure dust → main dust * 7
            processIntermediateForm(material, OrePrefixes.dustPure, m -> new ItemStack[] { getDustStack(m, 7) });
        }
    }

    private static void processIntermediateForm(Materials material, OrePrefixes prefix,
        java.util.function.Function<Materials, ItemStack[]> outputProvider) {

        ItemStack input = GTOreDictUnificator.get(prefix, material, 1);
        if (input != null) {
            addRecipe(input, outputProvider.apply(material));
        }
    }

    // ==================== Special Materials Processing ====================

    private static void processSpecialOreRecipes() {
        // Cerium
        processSpecialMaterial(
            Materials.Cerium,
            WerkstoffMaterialPool.CeriumOreConcentrate.get(OrePrefixes.dust, 11),
            WerkstoffMaterialPool.CeriumOreConcentrate.get(OrePrefixes.dust, 22));

        // Samarium
        processSpecialMaterial(
            Materials.Samarium,
            WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 11),
            WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 22));

        // Naquadah
        processSpecialMaterial(
            Materials.Naquadah,
            new ItemStack[] { GGMaterial.naquadahEarth.get(OrePrefixes.dust, 8),
                GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 3) },
            new ItemStack[] { GGMaterial.naquadahEarth.get(OrePrefixes.dust, 16),
                GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 8) });

        // Enriched Naquadah
        processSpecialMaterial(
            Materials.NaquadahEnriched,
            new ItemStack[] { GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 8),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 3) },
            new ItemStack[] { GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 16),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 6) });

        // Naquadria
        processSpecialMaterial(
            Materials.Naquadria,
            new ItemStack[] { GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 8),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 3) },
            new ItemStack[] { GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 16),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 6) });

        // Other mod ores
        processOtherModOre(GTModHandler.getModItem("TConstruct", "SearedBrick", 1, 1), Materials.Cobalt, true);
        processOtherModOre(GTModHandler.getModItem("TConstruct", "SearedBrick", 1, 2), Materials.Ardite, true);
        processOtherModOre(GTUtility.copyAmountUnsafe(1, Ic2Items.uraniumOre), Materials.Uranium, false);
        processOtherModOre(new ItemStack(Blocks.iron_ore), Materials.Iron, false);
    }

    private static void processSpecialMaterial(Materials material, ItemStack normalOutput, ItemStack richOutput) {
        processSpecialMaterial(material, new ItemStack[] { normalOutput }, new ItemStack[] { richOutput });
    }

    private static void processSpecialMaterial(Materials material, ItemStack[] normalOutputs, ItemStack[] richOutputs) {
        // Raw ore
        addRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, material, 1), normalOutputs);

        // All stone types
        for (OrePrefixes prefix : BASIC_STONE_TYPES) {
            ItemStack ore = GTOreDictUnificator.get(prefix, material, 1);
            if (ore != null) {
                addRecipe(ore, isRichOre(prefix) ? richOutputs : normalOutputs);
            }
        }
    }

    private static void processOtherModOre(ItemStack ore, Materials material, boolean isRich) {
        if (ore != null) {
            addRecipe(ore, getOutputs(material, isRich));
        }
    }

    // ==================== Bartworks Processing ====================

    public static void processBartworksOreRecipes() {
        try {
            for (Werkstoff werkstoff : Werkstoff.werkstoffHashSet) {
                if (!werkstoff.hasItemType(OrePrefixes.ore)) continue;

                ItemStack[] outputs = getBartworksOutputs(werkstoff);

                // Ore forms
                addRecipe(werkstoff.get(OrePrefixes.ore, 1), outputs);
                addRecipeIfNotNull(werkstoff.get(OrePrefixes.rawOre, 1), outputs);

                // Intermediate products
                processWerkstoffIntermediate(werkstoff, OrePrefixes.crushed, outputs);
                processWerkstoffIntermediate(werkstoff, OrePrefixes.crushedPurified, outputs);
                processWerkstoffIntermediate(werkstoff, OrePrefixes.crushedCentrifuged, outputs);
                processWerkstoffIntermediate(werkstoff, OrePrefixes.dustImpure, werkstoff.get(OrePrefixes.dust, 6));
                processWerkstoffIntermediate(werkstoff, OrePrefixes.dustPure, werkstoff.get(OrePrefixes.dust, 7));
            }
        } catch (Throwable t) {
            ScienceNotCool.LOG.info("Failed to process Bartworks ores: " + t.getMessage());
        }
    }

    private static ItemStack[] getBartworksOutputs(Werkstoff werkstoff) {
        ArrayList<ItemStack> outputs = new ArrayList<>();

        // Main dust
        outputs.add(werkstoff.get(OrePrefixes.dust, 4));

        // Gems
        if (werkstoff.hasItemType(OrePrefixes.gem)) {
            if (werkstoff.hasItemType(OrePrefixes.gemExquisite)) {
                outputs.add(werkstoff.get(OrePrefixes.gemExquisite, 1));
                outputs.add(werkstoff.get(OrePrefixes.gemFlawless, 2));
                outputs.add(werkstoff.get(OrePrefixes.gem, 2));
            } else {
                outputs.add(werkstoff.get(OrePrefixes.gem, 4));
            }
        }

        // Byproducts
        int byproductCount = werkstoff.getNoOfByProducts();
        if (byproductCount == 1) {
            outputs.add(GTUtility.copyAmountUnsafe(3, werkstoff.getOreByProduct(0, OrePrefixes.dust)));
        } else if (byproductCount > 1) {
            for (int i = 0; i < byproductCount; i++) {
                outputs.add(GTUtility.copyAmountUnsafe(2, werkstoff.getOreByProduct(i, OrePrefixes.dust)));
            }
        } else {
            outputs.add(werkstoff.get(OrePrefixes.dust, 3));
        }

        return outputs.toArray(new ItemStack[0]);
    }

    private static void processWerkstoffIntermediate(Werkstoff werkstoff, OrePrefixes prefix, ItemStack... outputs) {
        if (werkstoff.hasItemType(prefix)) {
            addRecipeIfNotNull(werkstoff.get(prefix, 1), outputs);
        }
    }

    // ==================== GT++ Processing ====================

    public static void processGTPPOreRecipes() {
        try {
            Class<?> materialClass = Class.forName("gtPlusPlus.core.material.Material");
            Set<Object> gtppMaterials = collectGTPPMaterials(materialClass);

            Method getOre = materialClass.getMethod("getOre", int.class);
            Method getDust = materialClass.getMethod("getDust", int.class);
            Method getRawOre = materialClass.getMethod("getRawOre", int.class);

            // Intermediate product methods (may not exist)
            Method getCrushed = getMethodSafely(materialClass, "getCrushed");
            Method getDustImpure = getMethodSafely(materialClass, "getDustImpure");
            Method getDustPure = getMethodSafely(materialClass, "getDustPure");

            for (Object material : gtppMaterials) {
                try {
                    ItemStack mainOutput = (ItemStack) getDust.invoke(material, 12);
                    if (mainOutput == null) continue;

                    // Ore forms
                    addRecipeIfNotNull((ItemStack) getOre.invoke(material, 1), mainOutput);
                    addRecipeIfNotNull((ItemStack) getRawOre.invoke(material, 1), mainOutput);

                    // Intermediate products
                    processGTPPIntermediate(material, getCrushed, getDust, 12);
                    processGTPPIntermediate(material, getDustImpure, getDust, 6);
                    processGTPPIntermediate(material, getDustPure, getDust, 7);

                } catch (Throwable e) {
                    // Ignore per-material failures
                }
            }
        } catch (ClassNotFoundException e) {
            // GT++ not present, skip
        } catch (Throwable t) {
            ScienceNotCool.LOG.info("Failed to process GT++ ores: " + t.getMessage());
        }
    }

    private static Set<Object> collectGTPPMaterials(Class<?> materialClass) throws Exception {
        Set<Object> materials = new HashSet<>();

        // Main materials
        Class<?> oresClass = Class.forName("gtPlusPlus.core.material.MaterialsOres");
        for (Field field : oresClass.getFields()) {
            if (materialClass.isAssignableFrom(field.getType())) {
                try {
                    Object obj = field.get(null);
                    if (obj != null) materials.add(obj);
                } catch (IllegalAccessException ignored) {}
            }
        }

        // Special materials
        addGTPPSpecialMaterials(
            materials,
            "gtPlusPlus.core.material.MaterialMisc",
            "RARE_EARTH_LOW",
            "RARE_EARTH_MID",
            "RARE_EARTH_HIGH");
        addGTPPSpecialMaterials(materials, "gtPlusPlus.core.material.MaterialsAlloy", "KOBOLDITE");
        addGTPPSpecialMaterials(materials, "gtPlusPlus.core.material.MaterialsElements$STANDALONE", "RUNITE");

        return materials;
    }

    private static void addGTPPSpecialMaterials(Set<Object> materials, String className, String... fieldNames) {
        try {
            Class<?> clazz = Class.forName(className);
            for (String fieldName : fieldNames) {
                try {
                    materials.add(
                        clazz.getField(fieldName)
                            .get(null));
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    private static void processGTPPIntermediate(Object material, Method getInput, Method getDust, int dustAmount)
        throws Exception {
        if (getInput != null) {
            ItemStack input = (ItemStack) getInput.invoke(material, 1);
            ItemStack output = (ItemStack) getDust.invoke(material, dustAmount);
            addRecipeIfNotNull(input, output);
        }
    }

    private static Method getMethodSafely(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName, int.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    // ==================== Helper Methods ====================

    private static void addRecipe(ItemStack input, ItemStack... outputs) {
        if (input == null) return;
        GTRecipeBuilder.builder()
            .itemInputs(input)
            .itemOutputs(outputs)
            .eut(EUT)
            .duration(DURATION_TICKS)
            .addTo(OreProcessingRecipes);
    }

    private static void addRecipeIfNotNull(ItemStack input, ItemStack... outputs) {
        if (input != null && outputs != null && outputs.length > 0 && outputs[0] != null) {
            addRecipe(input, outputs);
        }
    }

    private static boolean isRichOre(OrePrefixes prefix) {
        return prefix == OrePrefixes.oreNetherrack || prefix == OrePrefixes.oreEndstone;
    }
}
