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

    /**
     * Ore stone types enum
     */
    public static final Set<OrePrefixes> basicStoneTypes = Sets.newHashSet(
        OrePrefixes.ore,
        OrePrefixes.oreBasalt,
        OrePrefixes.oreBlackgranite,
        OrePrefixes.oreRedgranite,
        OrePrefixes.oreMarble,
        OrePrefixes.oreNetherrack,
        OrePrefixes.oreEndstone);

    public static final Set<OrePrefixes> basicStoneTypesExceptNormalStone = Sets.newHashSet(
        OrePrefixes.oreBasalt,
        OrePrefixes.oreBlackgranite,
        OrePrefixes.oreRedgranite,
        OrePrefixes.oreMarble,
        OrePrefixes.oreNetherrack,
        OrePrefixes.oreEndstone);

    public static final Map<Materials, ItemStack> processingLineMaterials = new HashMap<>();

    // Recipe constants - no energy consumption
    private static final int EUT = 0; // No energy consumption
    private static final int DURATION_TICKS = 20; // 1 second

    public static void initProcessingLineMaterials() {
        processingLineMaterials.put(Materials.Platinum, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.dust, 1));
        processingLineMaterials.put(Materials.Palladium, WerkstoffLoader.PDMetallicPowder.get(OrePrefixes.dust, 1));
        processingLineMaterials.put(Materials.Iridium, WerkstoffLoader.IrLeachResidue.get(OrePrefixes.dust, 1));
        processingLineMaterials.put(Materials.Osmium, WerkstoffLoader.IrOsLeachResidue.get(OrePrefixes.dust, 1));
        processingLineMaterials
            .put(Materials.Samarium, WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 1));
        processingLineMaterials
            .put(Materials.Cerium, WerkstoffMaterialPool.CeriumOreConcentrate.get(OrePrefixes.dust, 1));
    }

    public static ItemStack getDustStack(Materials material, int amount) {
        ItemStack t = processingLineMaterials.get(material);
        if (t != null) {
            return GTUtility.copyAmountUnsafe(amount * 3, t);
        }
        return GTUtility.copyAmountUnsafe(amount, GTOreDictUnificator.get(OrePrefixes.dust, material, 1));
    }

    /**
     * Generate all ore processing recipes
     */
    public static void loadOreProcessingRecipes() {
        initProcessingLineMaterials();

        Set<Materials> specialProcesses = Sets.newHashSet(
            Materials.Samarium,
            Materials.Cerium,
            Materials.Naquadah,
            Materials.NaquadahEnriched,
            Materials.Naquadria);

        // Generate normal GT materials' ore processing recipes
        for (int i = 0; i < GregTechAPI.sGeneratedMaterials.length; i++) {
            if (GregTechAPI.sGeneratedMaterials[i] == null) continue;

            Materials material = GregTechAPI.sGeneratedMaterials[i];

            // Rule out special materials
            if (!specialProcesses.isEmpty() && specialProcesses.contains(material)) {
                specialProcesses.remove(material);
                continue;
            }

            // Generate recipes
            processOreRecipe(material, i);
        }

        // Process GT++ ores (if present) and Bartworks ores so third-party ores get recipes too
        processGTPPOreRecipes();
        processBartworksOreRecipes();

        // Process special ores
        processSpecialOreRecipe();

        // Process intermediate products (crushed ores, impure dusts, clean dusts)
        processIntermediateProducts();

        ScienceNotCool.LOG.info("Loaded ore processing recipes");
    }

    /**
     * Generate special ores recipes
     */
    public static void processSpecialOreRecipe() {
        // Cerium ore
        {
            ItemStack[] outputs = new ItemStack[] {
                WerkstoffMaterialPool.CeriumOreConcentrate.get(OrePrefixes.dust, 11) };
            ItemStack[] outputsRich = new ItemStack[] {
                WerkstoffMaterialPool.CeriumOreConcentrate.get(OrePrefixes.dust, 22) };

            registryOreProcessRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Cerium, 1), outputs);
            for (OrePrefixes prefixes : basicStoneTypes) {
                if (GTOreDictUnificator.get(prefixes, Materials.Cerium, 1) == null) continue;
                registryOreProcessRecipe(
                    GTOreDictUnificator.get(prefixes, Materials.Cerium, 1),
                    isRich(prefixes) ? outputsRich : outputs);
            }
        }

        // Samarium Ore
        {
            ItemStack[] outputs = new ItemStack[] {
                WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 11) };
            ItemStack[] outputsRich = new ItemStack[] {
                WerkstoffMaterialPool.SamariumOreConcentrate.get(OrePrefixes.dust, 22) };

            registryOreProcessRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Samarium, 1), outputs);
            for (OrePrefixes prefixes : basicStoneTypes) {
                if (GTOreDictUnificator.get(prefixes, Materials.Samarium, 1) == null) continue;
                registryOreProcessRecipe(
                    GTOreDictUnificator.get(prefixes, Materials.Samarium, 1),
                    isRich(prefixes) ? outputsRich : outputs);
            }
        }

        // Naquadah Ore
        {
            ItemStack[] outputs = new ItemStack[] { GGMaterial.naquadahEarth.get(OrePrefixes.dust, 8),
                GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 3), };
            ItemStack[] outputsRich = new ItemStack[] { GGMaterial.naquadahEarth.get(OrePrefixes.dust, 16),
                GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 8), };

            registryOreProcessRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Naquadah, 1), outputs);
            for (OrePrefixes prefixes : basicStoneTypes) {
                if (GTOreDictUnificator.get(prefixes, Materials.Naquadah, 1) == null) continue;
                registryOreProcessRecipe(
                    GTOreDictUnificator.get(prefixes, Materials.Naquadah, 1),
                    isRich(prefixes) ? outputsRich : outputs);
            }
        }

        // Enriched Naquadah Ore
        {
            ItemStack[] outputs = new ItemStack[] { GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 8),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 3) };
            ItemStack[] outputsRich = new ItemStack[] { GGMaterial.enrichedNaquadahEarth.get(OrePrefixes.dust, 16),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 6) };

            registryOreProcessRecipe(
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.NaquadahEnriched, 1),
                outputs);
            for (OrePrefixes prefixes : basicStoneTypes) {
                if (GTOreDictUnificator.get(prefixes, Materials.NaquadahEnriched, 1) == null) continue;
                registryOreProcessRecipe(
                    GTOreDictUnificator.get(prefixes, Materials.NaquadahEnriched, 1),
                    isRich(prefixes) ? outputsRich : outputs);
            }
        }

        // Naquadria Ore
        {
            ItemStack[] outputs = new ItemStack[] { GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 8),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 3), };
            ItemStack[] outputsRich = new ItemStack[] { GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 16),
                GGMaterial.naquadriaEarth.get(OrePrefixes.dust, 6), };

            registryOreProcessRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Naquadria, 1), outputs);
            for (OrePrefixes prefixes : basicStoneTypes) {
                if (GTOreDictUnificator.get(prefixes, Materials.Naquadria, 1) == null) continue;
                registryOreProcessRecipe(
                    GTOreDictUnificator.get(prefixes, Materials.Naquadria, 1),
                    isRich(prefixes) ? outputsRich : outputs);
            }
        }

        // Tinker Construct - Cobalt ore
        processOreRecipe(GTModHandler.getModItem("TConstruct", "SearedBrick", 1, 1), Materials.Cobalt, true);

        // Tinker Construct - Ardite ore
        processOreRecipe(GTModHandler.getModItem("TConstruct", "SearedBrick", 1, 2), Materials.Ardite, true);

        // IC2 Uranium ore
        processOreRecipe(GTUtility.copyAmountUnsafe(1, Ic2Items.uraniumOre), Materials.Uranium, false);

        // Minecraft Iron ore
        processOreRecipe(new ItemStack(Blocks.iron_ore), Materials.Iron, false);
    }

    /**
     * Generate intermediate ore product recipes (crushed ores, impure dusts, clean dusts)
     */
    public static void processIntermediateProducts() {
        for (int i = 0; i < GregTechAPI.sGeneratedMaterials.length; i++) {
            if (GregTechAPI.sGeneratedMaterials[i] == null) continue;

            Materials material = GregTechAPI.sGeneratedMaterials[i];

            // Process crushed ore - use same outputs as normal ore but don't multiply
            ItemStack crushedOre = GTOreDictUnificator.get(OrePrefixes.crushed, material, 1);
            if (crushedOre != null) {
                ItemStack[] outputs = getOutputs(material, false);
                registryOreProcessRecipe(crushedOre, outputs);
            }

            // Process impure dust
            ItemStack impureDust = GTOreDictUnificator.get(OrePrefixes.dustImpure, material, 1);
            if (impureDust != null) {
                ItemStack[] outputs = new ItemStack[] { getDustStack(material, 6) };
                registryOreProcessRecipe(impureDust, outputs);
            }

            // Process clean dust
            ItemStack cleanDust = GTOreDictUnificator.get(OrePrefixes.dustPure, material, 1);
            if (cleanDust != null) {
                ItemStack[] outputs = new ItemStack[] { getDustStack(material, 7) };
                registryOreProcessRecipe(cleanDust, outputs);
            }
        }
    }

    /**
     * Generate normal ore recipes
     *
     * @param material The ore's Material.
     * @param ID       The material ID.
     */
    public static void processOreRecipe(Materials material, int ID) {
        if (GTOreDictUnificator.get(OrePrefixes.ore, material, 1) == null) return;
        ItemStack[] outputs = getOutputs(material, false);
        ItemStack[] outputsRich = getOutputs(material, true);

        // Registry normal stone ore
        registryOreProcessRecipe(GTModHandler.getModItem("gregtech", "gt.blockores", 1, ID), outputs);

        // Registry raw ore item style
        registryOreProcessRecipe(GTOreDictUnificator.get(OrePrefixes.rawOre, material, 1), outputs);

        // Registry gt stone ore
        for (OrePrefixes prefixes : basicStoneTypesExceptNormalStone) {
            if (GTOreDictUnificator.get(prefixes, material, 1) == null) {
                ScienceNotCool.LOG.info("Failed to get ore: material=" + material + " , prefixes=" + prefixes);
                continue;
            }
            registryOreProcessRecipe(
                GTOreDictUnificator.get(prefixes, material, 1),
                isRich(prefixes) ? outputsRich : outputs);
        }
    }

    /**
     * Process other mods' ore but normal style.
     *
     * @param inputOreItems Input ore item stack.
     * @param material      Input ore's material in GT design.
     * @param isRich        Is this ore a rich type.
     */
    public static void processOreRecipe(ItemStack inputOreItems, Materials material, boolean isRich) {
        registryOreProcessRecipe(inputOreItems, getOutputs(material, isRich));
    }

    public static ItemStack[] getOutputs(Materials material, boolean isRich) {
        List<ItemStack> outputs = new ArrayList<>();

        // Check byproduct - add null check
        if (material.mOreByProducts != null && !material.mOreByProducts.isEmpty()) {
            // The basic output the material
            ItemStack mainDust = getDustStack(material, 4);
            if (mainDust != null) outputs.add(mainDust);

            if (material.mOreByProducts.size() == 1) {
                for (Materials byproduct : material.mOreByProducts) {
                    if (byproduct == null) continue;
                    ItemStack byproductDust = getDustStack(byproduct, 3);
                    if (byproductDust != null) outputs.add(byproductDust);
                }
            } else {
                for (Materials byproduct : material.mOreByProducts) {
                    if (byproduct == null || byproduct == Materials.Netherrack
                        || byproduct == Materials.Endstone
                        || byproduct == Materials.Stone) continue;

                    ItemStack byproductDust = getDustStack(byproduct, 2);
                    if (byproductDust != null) outputs.add(byproductDust);
                }
            }

        } else {
            ItemStack mainDust = getDustStack(material, 8);
            if (mainDust != null) outputs.add(mainDust);
        }

        // Check gem style
        ItemStack gem = GTOreDictUnificator.get(OrePrefixes.gem, material, 1);
        if (gem != null) {
            ItemStack gemExquisite = GTOreDictUnificator.get(OrePrefixes.gemExquisite, material, 1);
            if (gemExquisite != null) {
                // Has gem style
                outputs.add(gemExquisite);
                outputs.add(GTOreDictUnificator.get(OrePrefixes.gemFlawless, material, 2));
                outputs.add(gem);

            } else {
                // Just normal gem
                outputs.add(gem);
            }
        }

        if (isRich) {
            for (ItemStack out : outputs) {
                if (out != null) {
                    out.stackSize *= 2;
                }
            }
        }

        return outputs.toArray(new ItemStack[0]);
    }

    /**
     * Process Bartworks ores (generate recipes for Werkstoff ores)
     */
    public static void processBartworksOreRecipes() {
        try {
            for (Werkstoff werkstoff : Werkstoff.werkstoffHashSet) {
                if (!werkstoff.hasItemType(OrePrefixes.ore)) continue;
                ArrayList<ItemStack> outputs = new ArrayList<>();

                // basic output
                outputs.add(werkstoff.get(OrePrefixes.dust, 4));

                // gem output
                if (werkstoff.hasItemType(OrePrefixes.gem)) {
                    if (werkstoff.hasItemType(OrePrefixes.gemExquisite)) {
                        outputs.add(werkstoff.get(OrePrefixes.gemExquisite, 1));
                        outputs.add(werkstoff.get(OrePrefixes.gemFlawless, 2));
                        outputs.add(werkstoff.get(OrePrefixes.gem, 2));
                    } else {
                        outputs.add(werkstoff.get(OrePrefixes.gem, 4));
                    }
                }

                // byproducts
                if (werkstoff.getNoOfByProducts() >= 1) {
                    if (werkstoff.getNoOfByProducts() == 1) {
                        outputs.add(GTUtility.copyAmountUnsafe(3, werkstoff.getOreByProduct(0, OrePrefixes.dust)));
                    } else {
                        for (int i = 0; i < werkstoff.getNoOfByProducts(); i++) {
                            outputs.add(GTUtility.copyAmountUnsafe(2, werkstoff.getOreByProduct(i, OrePrefixes.dust)));
                        }
                    }
                } else {
                    outputs.add(werkstoff.get(OrePrefixes.dust, 3));
                }

                // generate recipes - NO LUBRICANT REQUIRED
                GTRecipeBuilder.builder()
                    .itemInputs(werkstoff.get(OrePrefixes.ore, 1))
                    .itemOutputs(outputs.toArray(new ItemStack[] {}))
                    .eut(EUT)
                    .duration(DURATION_TICKS)
                    .addTo(OreProcessingRecipes);

                ItemStack r = werkstoff.get(OrePrefixes.rawOre, 1);
                if (r != null) {
                    GTRecipeBuilder.builder()
                        .itemInputs(r)
                        .itemOutputs(outputs.toArray(new ItemStack[] {}))
                        .eut(EUT)
                        .duration(DURATION_TICKS)
                        .addTo(OreProcessingRecipes);
                }
            }
        } catch (Throwable t) {
            ScienceNotCool.LOG.info("Failed to process Bartworks ores: " + t.getMessage());
        }
    }

    /**
     * Process GT++ ores via reflection so build won't fail when GT++ isn't present.
     */
    public static void processGTPPOreRecipes() {
        try {
            Class<?> materialsOresClass = Class.forName("gtPlusPlus.core.material.MaterialsOres");
            Class<?> materialClass = Class.forName("gtPlusPlus.core.material.Material");
            java.util.Set<Object> gtppOres = new HashSet<>();
            for (Field field : materialsOresClass.getFields()) {
                if (!materialClass.isAssignableFrom(field.getType())) continue;
                try {
                    Object object = field.get(null);
                    if (object == null) continue;
                    gtppOres.add(object);
                } catch (IllegalAccessException e) {
                    // ignore
                }
            }

            // try adding a few known specials if present
            try {
                Class<?> misc = Class.forName("gtPlusPlus.core.material.MaterialMisc");
                Field f1 = misc.getField("RARE_EARTH_LOW");
                gtppOres.add(f1.get(null));
                Field f2 = misc.getField("RARE_EARTH_MID");
                gtppOres.add(f2.get(null));
                Field f3 = misc.getField("RARE_EARTH_HIGH");
                gtppOres.add(f3.get(null));
            } catch (Throwable ignored) {}
            try {
                Class<?> alloy = Class.forName("gtPlusPlus.core.material.MaterialsAlloy");
                Field f = alloy.getField("KOBOLDITE");
                gtppOres.add(f.get(null));
            } catch (Throwable ignored) {}
            try {
                Class<?> elems = Class.forName("gtPlusPlus.core.material.MaterialsElements$STANDALONE");
                Field f = elems.getField("RUNITE");
                gtppOres.add(f.get(null));
            } catch (Throwable ignored) {}

            Method getOre = materialClass.getMethod("getOre", int.class);
            Method getDust = materialClass.getMethod("getDust", int.class);
            Method getRawOre = materialClass.getMethod("getRawOre", int.class);

            for (Object ore : gtppOres) {
                try {
                    ItemStack in = (ItemStack) getOre.invoke(ore, 1);
                    ItemStack out = (ItemStack) getDust.invoke(ore, 12);
                    if (in != null && out != null) {
                        GTRecipeBuilder.builder()
                            .itemInputs(in)
                            .itemOutputs(new ItemStack[] { out })
                            .eut(EUT)
                            .duration(DURATION_TICKS)
                            .addTo(OreProcessingRecipes);
                    }
                    ItemStack raw = (ItemStack) getRawOre.invoke(ore, 1);
                    if (raw != null && out != null) {
                        GTRecipeBuilder.builder()
                            .itemInputs(raw)
                            .itemOutputs(new ItemStack[] { out })
                            .eut(EUT)
                            .duration(DURATION_TICKS)
                            .addTo(OreProcessingRecipes);
                    }
                } catch (Throwable e) {
                    // ignore per-ore failures
                }
            }

        } catch (ClassNotFoundException e) {
            // GT++ not present, skip
        } catch (Throwable t) {
            ScienceNotCool.LOG.info("Failed to process GT++ ores: " + t.getMessage());
        }
    }

    public static void registryOreProcessRecipe(ItemStack input, ItemStack[] output) {
        if (input == null) return;

        GTRecipeBuilder.builder()
            .itemInputs(input)
            .itemOutputs(output)
            .eut(EUT)
            .duration(DURATION_TICKS)
            .addTo(OreProcessingRecipes);
    }

    /**
     * Check is this OrePrefix is rich ore style.
     *
     * @param prefixes The style to check.
     * @return True is rich ore.
     */
    public static boolean isRich(OrePrefixes prefixes) {
        return prefixes == OrePrefixes.oreNetherrack || prefixes == OrePrefixes.oreEndstone;
    }
}
