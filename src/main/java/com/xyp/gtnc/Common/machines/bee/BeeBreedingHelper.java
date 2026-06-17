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
     * 获取蜜蜂的品种标识名（使用未本地化的内部名称，保证客户端/服务端一致）
     */
    public static String getBeeSpecies(ItemStack stack) {
        if (!isBee(stack)) return null;
        IBee bee = getBeeRoot().getMember(stack);
        if (bee == null || bee.getGenome() == null) return null;
        IAlleleBeeSpecies species = bee.getGenome()
            .getPrimary();
        return species != null ? species.getUnlocalizedName() : null;
    }

    /**
     * 从未本地化名称中提取可读的品种名。
     * 优先取点分隔的最后一段；无点时尝试通过 getName() 获取英文名（仅 ASCII）。
     * 例如 "for.bees.species.Nickel" → "Nickel"、"SpeciesTENickel" → "Nickel"（若 getName() 为英文）
     */
    public static String getSpeciesDisplayName(String unlocalizedName) {
        if (unlocalizedName == null || unlocalizedName.isEmpty()) return "";
        int lastDot = unlocalizedName.lastIndexOf('.');
        if (lastDot >= 0) {
            String name = unlocalizedName.substring(lastDot + 1);
            if (!name.isEmpty()) {
                return Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }
        }
        // 无点分隔：尝试从物种注册表获取英文显示名
        IAlleleBeeSpecies species = findSpeciesByUnlocalizedName(unlocalizedName);
        if (species != null) {
            String name = species.getName();
            if (name != null && !name.isEmpty() && isAscii(name)) {
                return name;
            }
        }
        return Character.toUpperCase(unlocalizedName.charAt(0)) + unlocalizedName.substring(1);
    }

    /**
     * 从 unlocalizedName 中提取可读部分（不去首字母大写）。
     * 仅做点分隔提取，用于 matchSpeciesName 的快速匹配。
     */
    private static String extractReadableName(String uln) {
        int lastDot = uln.lastIndexOf('.');
        return lastDot >= 0 ? uln.substring(lastDot + 1) : uln;
    }

    /**
     * 仅通过 unlocalizedName（大小写不敏感）查找物种，不触发 matchSpeciesName 避免循环。
     */
    private static IAlleleBeeSpecies findSpeciesByUnlocalizedName(String uln) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;
        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies s = (IAlleleBeeSpecies) template[0];
                    if (uln.equalsIgnoreCase(s.getUnlocalizedName())) return s;
                }
            }
        }
        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies s = (IAlleleBeeSpecies) template[0];
                if (uln.equalsIgnoreCase(s.getUnlocalizedName())) return s;
            }
        }
        return null;
    }

    private static boolean isAscii(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 127) return false;
        }
        return true;
    }

    /**
     * 直接从 IAlleleBeeSpecies 获取显示名（无额外查找），供 matchSpeciesName 使用。
     */
    private static String getDisplayNameForSpecies(IAlleleBeeSpecies species) {
        String uln = species.getUnlocalizedName();
        if (uln == null || uln.isEmpty()) return "";
        int lastDot = uln.lastIndexOf('.');
        if (lastDot >= 0) {
            String name = uln.substring(lastDot + 1);
            if (!name.isEmpty()) {
                return Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }
        }
        String name = species.getName();
        if (name != null && !name.isEmpty() && isAscii(name)) {
            return name;
        }
        return Character.toUpperCase(uln.charAt(0)) + uln.substring(1);
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
     * 根据品种名称获取品种等位基因（大小写不敏感，同时匹配 getName() 和 getUnlocalizedName()）
     */
    public static IAlleleBeeSpecies getSpeciesByName(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                    if (matchSpeciesName(species, speciesName)) {
                        return species;
                    }
                }
            }
        }

        for (IAllele allele : root.getDefaultTemplate()) {
            if (allele instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) allele;
                if (matchSpeciesName(species, speciesName)) {
                    return species;
                }
            }
        }

        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                if (matchSpeciesName(species, speciesName)) {
                    return species;
                }
            }
        }
        return null;
    }

    /**
     * 匹配品种名称（大小写不敏感，同时匹配本地化名、未本地化名、提取的可读名、以及最终显示名）
     */
    private static boolean matchSpeciesName(IAlleleBeeSpecies species, String speciesName) {
        if (species == null || speciesName == null) return false;
        // 匹配本地化名（用户手动输入的英文名）
        if (species.getName() != null && species.getName()
            .equalsIgnoreCase(speciesName)) {
            return true;
        }
        // 匹配未本地化的内部名（NEI拖放获取的标识名）
        if (species.getUnlocalizedName() != null) {
            if (species.getUnlocalizedName()
                .equalsIgnoreCase(speciesName)) {
                return true;
            }
            // 匹配点分隔后的可读名（如 "for.bees.species.Nickel" → "Nickel"）
            String readable = extractReadableName(species.getUnlocalizedName());
            if (readable.equalsIgnoreCase(speciesName)) {
                return true;
            }
            // 匹配最终显示名（无点分隔时 getSpeciesDisplayName 可能通过 getName() 解析出英文名）
            String display = getDisplayNameForSpecies(species);
            if (display.equalsIgnoreCase(speciesName)) {
                return true;
            }
        }
        return false;
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
     * 获取指定品种的所有杂交配方（作为结果）（大小写不敏感匹配）
     */
    public static List<MutationData> getMutationsForSpecies(String speciesName) {
        List<MutationData> mutations = new ArrayList<>();
        IBeeRoot root = getBeeRoot();
        if (root == null) return mutations;

        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] resultTemplate = mutation.getTemplate();
            if (resultTemplate.length > 0 && resultTemplate[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies resultSpecies = (IAlleleBeeSpecies) resultTemplate[0];
                if (matchSpeciesName(resultSpecies, speciesName)) {
                    String parent1 = mutation.getAllele0()
                        .getUnlocalizedName();
                    String parent2 = mutation.getAllele1()
                        .getUnlocalizedName();
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
     * 键为 target 品种的内部标识名（unlocalizedName），值为优先选择的亲本对
     */
    private static final Map<String, String[]> BREEDING_PREFERENCES = new HashMap<>();

    private static void putPref(String target, String parent1, String parent2) {
        BREEDING_PREFERENCES.put(target, new String[] { parent1, parent2 });
    }

    static {
        // 初始化时用 English 名填入，运行时由 resolvePreferences() 转为 unlocalizedName
        putPref("Common", "Forest", "Meadows");
        putPref("Cultivated", "Common", "Forest");
        putPref("Sinister", "Cultivated", "Modest");
        putPref("Fiendish", "Sinister", "Cultivated");
        putPref("Frugal", "Modest", "Sinister");
        putPref("Arid", "Meadows", "Frugal");
        putPref("Diamond", "Certus", "Coal");
        putPref("Ruby", "Redstone", "Diamond");
        putPref("Sapphire", "Certus", "Lapis");
        putPref("Emerald", "Olivine", "Diamond");
        putPref("Rusty", "Meadows", "Resilient");
        putPref("Corroded", "Wintry", "Resilient");
        putPref("Leaden", "Meadows", "Resilient");
        putPref("Tarnished", "Marshy", "Resilient");
        putPref("Lustered", "Forest", "Resilient");
        putPref("Galvanized", "Wintry", "Resilient");
        putPref("Shining", "Majestic", "Galvanized");
        putPref("Glittering", "Majestic", "Rusty");
        putPref("Oily", "Ocean", "Primeval");
        putPref("Fossilised", "Primeval", "Growing");
        putPref("Fungal", "Boggy", "Miry");
        putPref("Scummy", "Agrarian", "Exotic");
        putPref("Spirit", "Ethereal", "Aware");
        putPref("Nuclear", "Unstable", "Rusty");
        putPref("Vengeful", "Demonic", "Vindictive");
        putPref("Eldritch", "Mystical", "Cultivated");
        putPref("Indium", "Lead", "Osmium");
    }

    /**
     * 将 BREEDING_PREFERENCES 中的 English 名称转为 unlocalizedName（在所有蜂种注册后调用）
     */
    private static boolean preferencesResolved = false;

    private static void resolvePreferences() {
        if (preferencesResolved) return;
        preferencesResolved = true;

        Map<String, String[]> resolved = new HashMap<>();
        for (Map.Entry<String, String[]> entry : BREEDING_PREFERENCES.entrySet()) {
            String targetEnglish = entry.getKey();
            String[] parentsEnglish = entry.getValue();

            // 解析 target 品种名
            IAlleleBeeSpecies targetSpecies = findSpeciesByEnglishName(targetEnglish);
            String targetKey = targetSpecies != null ? targetSpecies.getUnlocalizedName() : targetEnglish;

            // 解析 parent 品种名
            IAlleleBeeSpecies p1Species = findSpeciesByEnglishName(parentsEnglish[0]);
            IAlleleBeeSpecies p2Species = findSpeciesByEnglishName(parentsEnglish[1]);
            String p1Key = p1Species != null ? p1Species.getUnlocalizedName() : parentsEnglish[0];
            String p2Key = p2Species != null ? p2Species.getUnlocalizedName() : parentsEnglish[1];

            resolved.put(targetKey, new String[] { p1Key, p2Key });
        }

        BREEDING_PREFERENCES.clear();
        BREEDING_PREFERENCES.putAll(resolved);
    }

    /**
     * 通过 English 名（getName() 返回值）查找 IAlleleBeeSpecies
     */
    private static IAlleleBeeSpecies findSpeciesByEnglishName(String englishName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        // 遍历 genome templates
        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                    if (englishName.equalsIgnoreCase(species.getName())) {
                        return species;
                    }
                }
            }
        }

        // 遍历 mutations
        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                if (englishName.equalsIgnoreCase(species.getName())) {
                    return species;
                }
            }
        }
        return null;
    }

    /**
     * 选择最佳杂交配方
     */
    public static MutationData selectBestMutation(String beeName, List<MutationData> mutations) {
        if (mutations.isEmpty()) return null;
        if (mutations.size() == 1) return mutations.get(0);

        resolvePreferences();
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
