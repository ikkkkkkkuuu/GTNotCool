package com.xyp.gtnc.Common.machines.bee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.IAllele;

/**
 * 蜜蜂繁育工具类
 * <p>
 * 提供杂交路径计算、蜜蜂物品判断等功能
 */
public class BeeBreedingHelper {

    private static final Random RANDOM = new Random();

    /**
     * 获取 Forestry 蜜蜂根接口
     */
    public static IBeeRoot getBeeRoot() {
        return BeeManager.beeRoot;
    }

    /**
     * 判断物品是否为蜜蜂
     */
    public static boolean isBee(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        IBeeRoot root = getBeeRoot();
        return root != null && root.isMember(stack);
    }

    /**
     * 判断物品是否为公主蜂
     */
    public static boolean isPrincess(ItemStack stack) {
        if (!isBee(stack)) return false;
        return getBeeRoot().getType(stack) == EnumBeeType.PRINCESS;
    }

    /**
     * 判断物品是否为雄蜂
     */
    public static boolean isDrone(ItemStack stack) {
        if (!isBee(stack)) return false;
        return getBeeRoot().getType(stack) == EnumBeeType.DRONE;
    }

    /**
     * 判断物品是否为蜂后
     */
    public static boolean isQueen(ItemStack stack) {
        if (!isBee(stack)) return false;
        return getBeeRoot().getType(stack) == EnumBeeType.QUEEN;
    }

    /**
     * 获取蜜蜂的品种名称（活性等位基因）
     */
    public static String getBeeSpecies(ItemStack stack) {
        if (!isBee(stack)) return null;
        IBee bee = getBeeRoot().getMember(stack);
        if (bee == null || bee.getGenome() == null) return null;
        IAlleleBeeSpecies species = bee.getGenome()
            .getPrimary();
        return species != null ? species.getName() : null;
    }

    /**
     * 模拟杂交尝试（使用实际杂交概率）
     *
     * @param chance 杂交成功概率（0-100，来自 Forestry 的 getBaseChance）
     * @return true 如果杂交成功
     */
    public static boolean tryMutation(double chance) {
        return RANDOM.nextDouble() * 100 < chance;
    }

    /**
     * 根据品种名称获取品种等位基因
     */
    public static IAlleleBeeSpecies getSpeciesByName(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                    if (species.getName()
                        .equals(speciesName)) {
                        return species;
                    }
                }
            }
        }

        for (IAllele allele : root.getDefaultTemplate()) {
            if (allele instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) allele;
                if (species.getName()
                    .equals(speciesName)) {
                    return species;
                }
            }
        }

        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                if (species.getName()
                    .equals(speciesName)) {
                    return species;
                }
            }
        }
        return null;
    }

    /**
     * 获取所有已注册的蜜蜂品种名称
     */
    public static List<String> getAllBeeSpeciesNames() {
        List<String> names = new ArrayList<>();
        IBeeRoot root = getBeeRoot();
        if (root == null) return names;

        Set<String> addedNames = new HashSet<>();
        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                String name = ((IAlleleBeeSpecies) template[0]).getName();
                if (!addedNames.contains(name)) {
                    names.add(name);
                    addedNames.add(name);
                }
            }
        }
        return names;
    }

    /**
     * 杂交配方数据
     */
    public static class MutationData {

        public final String parent1;
        public final String parent2;
        public final String result;
        public final double chance;

        public MutationData(String parent1, String parent2, String result, double chance) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.result = result;
            this.chance = chance;
        }
    }

    /**
     * 获取指定品种的所有杂交配方（作为结果）
     */
    public static List<MutationData> getMutationsForSpecies(String speciesName) {
        List<MutationData> mutations = new ArrayList<>();
        IBeeRoot root = getBeeRoot();
        if (root == null) return mutations;

        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] resultTemplate = mutation.getTemplate();
            if (resultTemplate.length > 0 && resultTemplate[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies resultSpecies = (IAlleleBeeSpecies) resultTemplate[0];
                if (resultSpecies.getName()
                    .equals(speciesName)) {
                    String parent1 = mutation.getAllele0()
                        .getName();
                    String parent2 = mutation.getAllele1()
                        .getName();
                    mutations.add(new MutationData(parent1, parent2, speciesName, mutation.getBaseChance()));
                }
            }
        }
        return mutations;
    }

    /**
     * 杂交路径中的一步
     */
    public static class BreedingStep {

        public final String parent1;
        public final String parent2;
        public final String result;
        public final double chance;

        public BreedingStep(String parent1, String parent2, String result, double chance) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.result = result;
            this.chance = chance;
        }

        @Override
        public String toString() {
            return parent1 + " + " + parent2 + " -> " + result;
        }
    }

    /**
     * 蜜蜂杂交路径优先配置（当有多条路径时优先选择）
     */
    private static final Map<String, String[]> BREEDING_PREFERENCES = new HashMap<>();
    static {
        BREEDING_PREFERENCES.put("Common", new String[] { "Forest", "Meadows" });
        BREEDING_PREFERENCES.put("Cultivated", new String[] { "Common", "Forest" });
        BREEDING_PREFERENCES.put("Sinister", new String[] { "Cultivated", "Modest" });
        BREEDING_PREFERENCES.put("Fiendish", new String[] { "Sinister", "Cultivated" });
        BREEDING_PREFERENCES.put("Frugal", new String[] { "Modest", "Sinister" });
        BREEDING_PREFERENCES.put("Arid", new String[] { "Meadows", "Frugal" });
        BREEDING_PREFERENCES.put("Diamond", new String[] { "Certus", "Coal" });
        BREEDING_PREFERENCES.put("Ruby", new String[] { "Redstone", "Diamond" });
        BREEDING_PREFERENCES.put("Sapphire", new String[] { "Certus", "Lapis" });
        BREEDING_PREFERENCES.put("Emerald", new String[] { "Olivine", "Diamond" });
        BREEDING_PREFERENCES.put("Rusty", new String[] { "Meadows", "Resilient" });
        BREEDING_PREFERENCES.put("Corroded", new String[] { "Wintry", "Resilient" });
        BREEDING_PREFERENCES.put("Leaden", new String[] { "Meadows", "Resilient" });
        BREEDING_PREFERENCES.put("Tarnished", new String[] { "Marshy", "Resilient" });
        BREEDING_PREFERENCES.put("Lustered", new String[] { "Forest", "Resilient" });
        BREEDING_PREFERENCES.put("Galvanized", new String[] { "Wintry", "Resilient" });
        BREEDING_PREFERENCES.put("Shining", new String[] { "Majestic", "Galvanized" });
        BREEDING_PREFERENCES.put("Glittering", new String[] { "Majestic", "Rusty" });
        BREEDING_PREFERENCES.put("Oily", new String[] { "Ocean", "Primeval" });
        BREEDING_PREFERENCES.put("Fossilised", new String[] { "Primeval", "Growing" });
        BREEDING_PREFERENCES.put("Fungal", new String[] { "Boggy", "Miry" });
        BREEDING_PREFERENCES.put("Scummy", new String[] { "Agrarian", "Exotic" });
        BREEDING_PREFERENCES.put("Spirit", new String[] { "Ethereal", "Aware" });
        BREEDING_PREFERENCES.put("Nuclear", new String[] { "Unstable", "Rusty" });
        BREEDING_PREFERENCES.put("Vengeful", new String[] { "Demonic", "Vindictive" });
        BREEDING_PREFERENCES.put("Eldritch", new String[] { "Mystical", "Cultivated" });
        BREEDING_PREFERENCES.put("Indium", new String[] { "Lead", "Osmium" });
    }

    /**
     * 选择最佳杂交配方
     */
    private static MutationData selectBestMutation(String beeName, List<MutationData> mutations) {
        if (mutations.isEmpty()) return null;
        if (mutations.size() == 1) return mutations.get(0);

        String[] preference = BREEDING_PREFERENCES.get(beeName);
        if (preference != null) {
            for (MutationData mutation : mutations) {
                if ((mutation.parent1.equals(preference[0]) && mutation.parent2.equals(preference[1]))
                    || (mutation.parent1.equals(preference[1]) && mutation.parent2.equals(preference[0]))) {
                    return mutation;
                }
            }
        }
        return mutations.get(0);
    }

    /**
     * 创建从现有蜜蜂到目标蜜蜂的杂交路径（BFS）
     *
     * @param targetBee    目标蜜蜂品种名
     * @param existingBees 现有蜜蜂品种名集合
     * @return 杂交步骤列表（从基础蜜蜂到目标蜜蜂的顺序），每步包含实际杂交概率
     */
    public static List<BreedingStep> createBreedingChain(String targetBee, Set<String> existingBees) {
        if (existingBees.contains(targetBee)) {
            return new ArrayList<>();
        }

        Map<String, BreedingStep> breedingChain = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>(existingBees);
        visited.add(targetBee);

        List<MutationData> targetMutations = getMutationsForSpecies(targetBee);
        targetMutations.removeIf(m -> m.parent1.equals(targetBee) || m.parent2.equals(targetBee));
        MutationData targetMutation = selectBestMutation(targetBee, targetMutations);
        if (targetMutation == null) {
            return new ArrayList<>();
        }

        breedingChain.put(
            targetBee,
            new BreedingStep(targetMutation.parent1, targetMutation.parent2, targetBee, targetMutation.chance));
        queue.add(targetBee);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            BreedingStep step = breedingChain.get(current);
            if (step == null) continue;

            for (String parent : new String[] { step.parent1, step.parent2 }) {
                if (visited.contains(parent)) continue;
                visited.add(parent);

                List<MutationData> parentMutations = getMutationsForSpecies(parent);
                parentMutations.removeIf(m -> m.parent1.equals(targetBee) || m.parent2.equals(targetBee));
                MutationData parentMutation = selectBestMutation(parent, parentMutations);
                if (parentMutation != null) {
                    breedingChain.put(
                        parent,
                        new BreedingStep(
                            parentMutation.parent1,
                            parentMutation.parent2,
                            parent,
                            parentMutation.chance));
                    queue.add(parent);
                }
            }
        }

        List<BreedingStep> orderedSteps = new ArrayList<>();
        Set<String> canBreed = new HashSet<>(existingBees);

        boolean changed = true;
        while (changed && !breedingChain.isEmpty()) {
            changed = false;
            List<String> toRemove = new ArrayList<>();

            for (Map.Entry<String, BreedingStep> entry : breedingChain.entrySet()) {
                BreedingStep step = entry.getValue();
                if (canBreed.contains(step.parent1) && canBreed.contains(step.parent2)) {
                    orderedSteps.add(step);
                    canBreed.add(step.result);
                    toRemove.add(entry.getKey());
                    changed = true;
                }
            }

            for (String key : toRemove) {
                breedingChain.remove(key);
            }
        }

        return orderedSteps;
    }

    /**
     * 创建指定品种的雄蜂 ItemStack
     */
    public static ItemStack createDrone(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        IAlleleBeeSpecies species = getSpeciesByName(speciesName);
        if (species == null) return null;

        IAllele[] template = root.getTemplate(species.getUID());
        if (template == null) return null;

        IBeeGenome genome = root.templateAsGenome(template);
        IBee bee = root.getBee(null, genome);
        if (bee == null) return null;
        return root.getMemberStack(bee, EnumBeeType.DRONE.ordinal());
    }

    /**
     * 创建指定品种的公主蜂 ItemStack
     */
    public static ItemStack createPrincess(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        IAlleleBeeSpecies species = getSpeciesByName(speciesName);
        if (species == null) return null;

        IAllele[] template = root.getTemplate(species.getUID());
        if (template == null) return null;

        IBeeGenome genome = root.templateAsGenome(template);
        IBee bee = root.getBee(null, genome);
        if (bee == null) return null;
        return root.getMemberStack(bee, EnumBeeType.PRINCESS.ordinal());
    }
}
