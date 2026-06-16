package com.xyp.gtnc.Common.items.wildcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;

/**
 * 通配样板符配方生成器
 * 根据配置的条目生成实际的AE2加工样板
 */
public final class WildcardPatternGenerator {

    private static final String KEY_WILDCARD = "WildcardPattern";
    private static final String KEY_SELECTED_MATERIAL = "WildcardSelectedMaterial";
    public static final String KEY_GENERATED_PATTERN_ID = "WildcardGeneratedPatternId";
    private static final int MAX_RULES = 9;

    private WildcardPatternGenerator() {}

    /**
     * 生成所有展开的配方
     * 
     * @return 生成的配方列表
     */
    public static List<GeneratedPattern> generatePatterns(ItemStack wildcardStack) {
        List<WildcardPatternEntry> inputs = WildcardPatternState.getInputEntries(wildcardStack);
        List<WildcardPatternEntry> outputs = WildcardPatternState.getOutputEntries(wildcardStack);

        if (inputs.isEmpty() || outputs.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有候选材料
        Set<String> candidateMaterials = getCandidateMaterials(inputs, outputs);

        if (candidateMaterials.isEmpty()) {
            return new ArrayList<>();
        }

        // 为每个材料生成配方
        List<GeneratedPattern> patterns = new ArrayList<>();
        for (String materialName : candidateMaterials) {
            GeneratedPattern pattern = createPatternForMaterial(materialName, inputs, outputs, wildcardStack);
            if (pattern != null) {
                patterns.add(pattern);
            }
        }

        // 更新展开数量
        WildcardPatternState.setExpandedPatternCount(wildcardStack, patterns.size());

        return patterns;
    }

    /**
     * 获取所有候选材料名
     */
    public static Set<String> getCandidateMaterials(List<WildcardPatternEntry> inputs,
        List<WildcardPatternEntry> outputs) {
        Set<String> result = new HashSet<>();

        // 从输入条目收集
        for (WildcardPatternEntry entry : inputs) {
            if (!entry.isEmpty()) {
                result.addAll(entry.getCandidateMaterials());
            }
        }

        // 从输出条目收集
        for (WildcardPatternEntry entry : outputs) {
            if (!entry.isEmpty()) {
                result.addAll(entry.getCandidateMaterials());
            }
        }

        return result;
    }

    /**
     * 为特定材料创建配方
     */
    public static GeneratedPattern createPatternForMaterial(String materialName, List<WildcardPatternEntry> inputs,
        List<WildcardPatternEntry> outputs, ItemStack configStack) {
        ItemStack inputStack = null;
        ItemStack outputStack = null;

        // 构建输入堆(取第一个非空条目)
        for (WildcardPatternEntry entry : inputs) {
            if (!entry.isEmpty()) {
                inputStack = entry.createStack(materialName, configStack);
                if (inputStack != null && inputStack.getItem() != null) {
                    break;
                }
            }
        }

        // 构建输出堆(取第一个非空条目)
        for (WildcardPatternEntry entry : outputs) {
            if (!entry.isEmpty()) {
                outputStack = entry.createStack(materialName, configStack);
                if (outputStack != null && outputStack.getItem() != null) {
                    break;
                }
            }
        }

        if (inputStack == null && outputStack == null) {
            return null;
        }

        return new GeneratedPattern(materialName, inputStack, outputStack);
    }

    /**
     * 检查是否为通配样板符
     */
    public static boolean isWildcardPattern(ItemStack stack) {
        return stack != null && (stack.getItem() == com.xyp.gtnc.Loader.ItemsLoader.wildcardPattern
            || stack.hasTagCompound() && stack.getTagCompound()
                .getBoolean(KEY_WILDCARD));
    }

    /**
     * 标记样板为通配样板
     */
    public static void markAsWildcard(ItemStack stack) {
        if (stack == null) return;
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setBoolean(KEY_WILDCARD, true);
        WildcardPatternState.ensureInitialized(stack);
    }

    /**
     * 获取AE2识别的配方详情 - 关键方法!
     */
    public static ICraftingPatternDetails getDetailsForItem(ItemStack stack, World world) {
        if (!isWildcardPattern(stack)) {
            return null;
        }
        markAsWildcard(stack);
        if (isGeneratedPattern(stack)) {
            return createDetailForCurrentStack(stack, world);
        }
        return getDisplayDetails(stack, world);
    }

    /**
     * 生成所有展开的配方详情 - AE2接口调用此方法获取多个配方
     */
    public static List<ICraftingPatternDetails> generateAllDetails(ItemStack stack, World world) {
        if (!isWildcardPattern(stack)) {
            return Collections.emptyList();
        }

        List<ICraftingPatternDetails> result = new ArrayList<>();
        for (int ruleIndex = 0; ruleIndex < MAX_RULES; ruleIndex++) {
            result.addAll(generateRuleDetails(stack, world, ruleIndex));
        }
        result.sort(Comparator.comparing(details -> getPatternIdentity(details == null ? null : details.getPattern())));
        return result;
    }

    /**
     * 为指定规则生成所有配方详情
     */
    public static List<ICraftingPatternDetails> generateRuleDetails(ItemStack stack, World world, int ruleIndex) {
        List<GeneratedPattern> patterns = generateRulePreviewPatterns(stack, ruleIndex);
        if (patterns.isEmpty()) {
            return Collections.emptyList();
        }

        List<ICraftingPatternDetails> result = new ArrayList<>();
        for (GeneratedPattern pattern : patterns) {
            ItemStack generated = createPatternStack(
                stack,
                ruleIndex,
                pattern.materialName,
                pattern.inputStack,
                pattern.outputStack);
            if (generated == null) {
                continue;
            }
            ICraftingPatternDetails detail = createDetailForCurrentStack(generated, world);
            if (detail != null) {
                result.add(detail);
            }
        }
        return result;
    }

    /**
     * 获取用于显示的预览详情
     */
    private static ICraftingPatternDetails getDisplayDetails(ItemStack stack, World world) {
        if (!isWildcardPattern(stack)) {
            return null;
        }
        return new WildcardPreviewPatternDetails(stack, getRepresentativeInput(stack), getRepresentativeOutput(stack));
    }

    /**
     * 为当前物品堆创建配方详情
     */
    public static ICraftingPatternDetails createDetailForCurrentStack(ItemStack stack, World world) {
        try {
            return new WildcardPatternDetails(stack, world);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    /**
     * 检查是否为已生成的具体配方(而非通配模板)
     */
    public static boolean isGeneratedPattern(ItemStack stack) {
        return !getGeneratedPatternId(stack).isEmpty();
    }

    /**
     * 获取已生成配方的ID
     */
    public static String getGeneratedPatternId(ItemStack stack) {
        NBTTagCompound tag = stack == null ? null : stack.getTagCompound();
        return tag == null || !tag.hasKey(KEY_GENERATED_PATTERN_ID) ? "" : tag.getString(KEY_GENERATED_PATTERN_ID);
    }

    /**
     * 获取配方的唯一标识
     */
    public static String getPatternIdentity(ItemStack stack) {
        String generatedId = getGeneratedPatternId(stack);
        if (!generatedId.isEmpty()) {
            return generatedId;
        }
        return getStackFingerprint(stack);
    }

    /**
     * 获取物品堆的唯一指纹
     */
    private static String getStackFingerprint(ItemStack stack) {
        if (stack == null) {
            return "empty";
        }
        String itemName = String.valueOf(net.minecraft.item.Item.itemRegistry.getNameForObject(stack.getItem()));
        if (itemName == null || itemName.isEmpty() || "null".equals(itemName)) {
            itemName = stack.getItem() == null ? "null"
                : stack.getItem()
                    .getClass()
                    .getName();
        }
        NBTTagCompound tag = stack.getTagCompound();
        return itemName + "@"
            + stack.getItemDamage()
            + "x"
            + Math.max(1, stack.stackSize)
            + "#"
            + (tag == null ? "" : Integer.toHexString(tag.hashCode()));
    }

    /**
     * 获取或创建NBT标签
     */
    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    /**
     * 获取代表性输入
     */
    public static ItemStack getRepresentativeInput(ItemStack stack) {
        return getRepresentativeEntryStack(WildcardPatternState.getInputEntries(stack), stack);
    }

    /**
     * 获取代表性输出
     */
    public static ItemStack getRepresentativeOutput(ItemStack stack) {
        return getRepresentativeEntryStack(WildcardPatternState.getOutputEntries(stack), stack);
    }

    /**
     * 获取物品的输出 - AE2调用此方法获取配方输出
     */
    public static ItemStack getOutputForItem(ItemStack stack, World world) {
        if (!isWildcardPattern(stack)) {
            return null;
        }
        markAsWildcard(stack);
        if (isGeneratedPattern(stack)) {
            ItemStack generatedOutput = getFirstCondensedOutput(createDetailForCurrentStack(stack, world));
            if (generatedOutput != null) {
                return generatedOutput;
            }
        }
        return getRepresentativeOutput(stack);
    }

    /**
     * 从配方详情中获取第一个压缩输出
     */
    private static ItemStack getFirstCondensedOutput(ICraftingPatternDetails details) {
        IAEItemStack[] outputs = details == null ? null : details.getCondensedOutputs();
        if (outputs == null || outputs.length == 0 || outputs[0] == null) {
            return null;
        }
        ItemStack output = outputs[0].getItemStack();
        return output == null ? null : output.copy();
    }

    /**
     * 从条目列表获取第一个非空显示堆
     */
    private static ItemStack getRepresentativeEntryStack(List<WildcardPatternEntry> entries, ItemStack fallbackStack) {
        if (entries != null) {
            for (WildcardPatternEntry entry : entries) {
                if (entry == null || entry.isEmpty()) {
                    continue;
                }
                ItemStack display = entry.getDisplayStack();
                if (display != null) {
                    return display;
                }
            }
        }
        if (fallbackStack == null) {
            return null;
        }
        ItemStack fallback = fallbackStack.copy();
        fallback.stackSize = 1;
        return fallback;
    }

    /**
     * 创建具体的配方物品堆(带NBT数据)
     */
    private static ItemStack createPatternStack(ItemStack template, int ruleIndex, String materialName,
        ItemStack inputStack, ItemStack outputStack) {
        if (inputStack == null && outputStack == null) {
            return null;
        }

        NBTTagList inputList = buildPatternList(inputStack);
        NBTTagList outputList = buildPatternList(outputStack);
        if (inputList == null || outputList == null) {
            return null;
        }

        ItemStack result = template.copy();
        NBTTagCompound resultTag = getOrCreateTag(result);
        resultTag.setTag("in", inputList);
        resultTag.setTag("out", outputList);
        resultTag.setString(KEY_SELECTED_MATERIAL, materialName == null ? "" : materialName);
        resultTag.setString(
            KEY_GENERATED_PATTERN_ID,
            buildGeneratedPatternId(ruleIndex, materialName, inputStack, outputStack));
        resultTag.setBoolean("crafting", false);
        resultTag.removeTag("InvalidPattern");
        return result;
    }

    /**
     * 构建配方列表NBT
     */
    private static NBTTagList buildPatternList(ItemStack stack) {
        NBTTagList rewritten = new NBTTagList();
        if (stack == null) {
            rewritten.appendTag(new NBTTagCompound());
            return rewritten;
        }
        NBTTagCompound rewrittenTag = new NBTTagCompound();
        stack.writeToNBT(rewrittenTag);
        int count = Math.max(1, stack.stackSize);
        rewrittenTag.setInteger("Count", count);
        rewrittenTag.setLong("Cnt", count);
        rewritten.appendTag(rewrittenTag);
        return rewritten;
    }

    /**
     * 构建已生成配方的唯一ID
     */
    private static String buildGeneratedPatternId(int ruleIndex, String materialName, ItemStack inputStack,
        ItemStack outputStack) {
        return ruleIndex + "|"
            + sanitizeIdentityPart(materialName)
            + "|"
            + getStackFingerprint(inputStack)
            + "->"
            + getStackFingerprint(outputStack);
    }

    /**
     * 清理身份标识中的特殊字符
     */
    private static String sanitizeIdentityPart(String value) {
        return value == null ? ""
            : value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("->", "-\\>");
    }

    /**
     * 生成规则预览配方(用于显示)
     */
    public static List<GeneratedPattern> generateRulePreviewPatterns(ItemStack stack, int ruleIndex) {
        if (!isWildcardPattern(stack) || ruleIndex < 0 || ruleIndex >= MAX_RULES) {
            return Collections.emptyList();
        }

        List<WildcardPatternEntry> inputs = WildcardPatternState.getInputEntries(stack);
        List<WildcardPatternEntry> outputs = WildcardPatternState.getOutputEntries(stack);
        if (ruleIndex >= inputs.size() || ruleIndex >= outputs.size()) {
            return Collections.emptyList();
        }

        WildcardPatternEntry input = inputs.get(ruleIndex);
        WildcardPatternEntry output = outputs.get(ruleIndex);
        if ((input == null || input.isEmpty()) && (output == null || output.isEmpty())) {
            return Collections.emptyList();
        }

        // 收集排除规则(支持通配符)
        List<String> excludePatterns = new ArrayList<>();
        String globalExclude = WildcardPatternConfig.getGlobalExcludeMaterials(stack);
        if (globalExclude != null && !globalExclude.trim()
            .isEmpty()) {
            for (String token : globalExclude.split("[,;\\s]+")) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty()) {
                    excludePatterns.add(trimmed);
                }
            }
        }
        List<String> ruleExcludes = WildcardPatternConfig.getRuleExcludeList(stack, MAX_RULES);
        if (ruleIndex < ruleExcludes.size()) {
            String ruleExclude = ruleExcludes.get(ruleIndex);
            if (ruleExclude != null && !ruleExclude.trim()
                .isEmpty()) {
                for (String token : ruleExclude.split("[,;\\s]+")) {
                    String trimmed = token.trim();
                    if (!trimmed.isEmpty()) {
                        excludePatterns.add(trimmed);
                    }
                }
            }
        }

        List<String> candidates = new ArrayList<>();
        for (String candidate : collectRuleCandidatePool(input, output)) {
            if (candidate != null && !isExcluded(candidate, excludePatterns)) {
                candidates.add(candidate);
            }
        }
        candidates.sort(String.CASE_INSENSITIVE_ORDER);

        List<GeneratedPattern> result = new ArrayList<>();
        for (String candidate : candidates) {
            if (candidate == null || candidate.trim()
                .isEmpty()) {
                continue;
            }
            ItemStack inputStack = input == null || input.isEmpty() ? null : input.createStack(candidate, stack);
            ItemStack outputStack = output == null || output.isEmpty() ? null : output.createStack(candidate, stack);
            if ((input != null && !input.isEmpty() && inputStack == null)
                || (output != null && !output.isEmpty() && outputStack == null)) {
                continue;
            }

            // 额外检查:如果输入或输出的显示名称也匹配排除规则,则排除
            String inputDisplay = inputStack != null ? inputStack.getDisplayName() : null;
            String outputDisplay = outputStack != null ? outputStack.getDisplayName() : null;
            if (isExcludedWithDisplay(candidate, inputDisplay, excludePatterns)
                || isExcludedWithDisplay(candidate, outputDisplay, excludePatterns)) {
                continue;
            }

            result.add(new GeneratedPattern(candidate, inputStack, outputStack));
        }
        return result;
    }

    /**
     * 检查材料是否被排除规则匹配(支持黑白名单)
     */
    private static boolean isExcluded(String materialName, List<String> excludePatterns) {
        if (excludePatterns.isEmpty()) {
            return false;
        }

        List<String> blacklist = new ArrayList<>(); // 黑名单(排除)
        List<String> whitelist = new ArrayList<>(); // 白名单(仅选)

        // 分离黑白名单
        for (String pattern : excludePatterns) {
            if (pattern.startsWith("!")) {
                whitelist.add(pattern.substring(1)); // 移除!前缀
            } else {
                blacklist.add(pattern);
            }
        }

        String lowerName = materialName.toLowerCase();

        // 如果有白名单,只保留匹配白名单的材料
        if (!whitelist.isEmpty()) {
            boolean inWhitelist = false;
            for (String pattern : whitelist) {
                if (matchesExcludePattern(lowerName, pattern.toLowerCase())) {
                    inWhitelist = true;
                    break;
                }
            }
            if (!inWhitelist) {
                return true; // 不在白名单中,排除
            }
        }

        // 检查是否在黑名单中
        for (String pattern : blacklist) {
            if (matchesExcludePattern(lowerName, pattern.toLowerCase())) {
                return true; // 在黑名单中,排除
            }
        }

        return false; // 通过所有检查,保留
    }

    /**
     * 检查材料或显示名称是否被排除规则匹配
     */
    private static boolean isExcludedWithDisplay(String materialName, String displayName,
        List<String> excludePatterns) {
        if (excludePatterns.isEmpty()) {
            return false;
        }
        // 检查材料名
        if (isExcluded(materialName, excludePatterns)) {
            return true;
        }
        // 检查显示名称(如果有的话)
        if (displayName != null && !displayName.isEmpty()) {
            String lowerDisplay = displayName.toLowerCase();
            for (String pattern : excludePatterns) {
                if (matchesExcludePattern(lowerDisplay, pattern.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断材料名是否匹配排除模式(支持通配符*)
     */
    private static boolean matchesExcludePattern(String materialName, String pattern) {
        if (pattern.isEmpty()) {
            return false;
        }
        // 如果没有通配符,进行精确匹配
        if (!pattern.contains("*")) {
            return materialName.equals(pattern);
        }
        // 将通配符模式转换为正则表达式
        String regex = pattern.replace(".", "\\.")
            .replace("+", "\\+")
            .replace("?", "\\?")
            .replace("*", ".*");
        try {
            return materialName.matches(regex);
        } catch (Exception e) {
            // 如果正则表达式无效,回退到简单的包含匹配
            String simplePattern = pattern.replace("*", "");
            return materialName.contains(simplePattern);
        }
    }

    /**
     * 收集规则的候选材料池(合并输入和输出的候选材料)
     */
    private static Iterable<String> collectRuleCandidatePool(WildcardPatternEntry input, WildcardPatternEntry output) {
        Set<String> candidates = null;
        boolean narrowed = false;

        Set<String> inputCandidates = input == null || input.isEmpty() ? Collections.<String>emptySet()
            : input.getCandidateMaterials();
        if (inputCandidates != null && !inputCandidates.isEmpty()) {
            candidates = mergeCandidatePool(candidates, inputCandidates);
            narrowed = true;
        }

        Set<String> outputCandidates = output == null || output.isEmpty() ? Collections.<String>emptySet()
            : output.getCandidateMaterials();
        if (outputCandidates != null && !outputCandidates.isEmpty()) {
            candidates = mergeCandidatePool(candidates, outputCandidates);
            narrowed = true;
        }

        if (narrowed) {
            return candidates == null ? Collections.<String>emptySet() : candidates;
        }

        // 如果都没有矿辞候选材料,返回所有已知材料名
        return WildcardPatternEntry.getAllKnownMaterialNames();
    }

    /**
     * 合并候选材料池(取交集)
     */
    private static Set<String> mergeCandidatePool(Set<String> current, Set<String> narrowed) {
        if (current == null) {
            return new LinkedHashSet<>(narrowed);
        }
        current.retainAll(narrowed);
        return current;
    }

    /**
     * 统计预览配方数量
     */
    public static int countPreviewPatterns(ItemStack stack) {
        List<WildcardPatternEntry> entries = WildcardPatternState.getInputEntries(stack);
        int count = 0;
        for (WildcardPatternEntry entry : entries) {
            if (!entry.isEmpty()) {
                count += entry.getCandidateMaterials()
                    .size();
            }
        }
        return count;
    }

    /**
     * 统计考虑排除规则后的实际配方数量
     */
    public static int countActualPatternsAfterExclude(ItemStack stack) {
        if (!isWildcardPattern(stack)) {
            return 0;
        }

        List<WildcardPatternEntry> inputs = WildcardPatternState.getInputEntries(stack);
        List<WildcardPatternEntry> outputs = WildcardPatternState.getOutputEntries(stack);

        // 收集全局排除规则
        List<String> globalExcludePatterns = new ArrayList<>();
        String globalExclude = WildcardPatternConfig.getGlobalExcludeMaterials(stack);
        if (globalExclude != null && !globalExclude.trim()
            .isEmpty()) {
            for (String token : globalExclude.split("[,;\\s]+")) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty()) {
                    globalExcludePatterns.add(trimmed);
                }
            }
        }

        int totalCount = 0;
        for (int ruleIndex = 0; ruleIndex < MAX_RULES; ruleIndex++) {
            if (ruleIndex >= inputs.size() || ruleIndex >= outputs.size()) {
                break;
            }

            WildcardPatternEntry input = inputs.get(ruleIndex);
            WildcardPatternEntry output = outputs.get(ruleIndex);
            if ((input == null || input.isEmpty()) && (output == null || output.isEmpty())) {
                continue;
            }

            // 合并全局和规则级别的排除(每个规则独立)
            List<String> excludePatterns = new ArrayList<>(globalExcludePatterns);
            List<String> ruleExcludes = WildcardPatternConfig.getRuleExcludeList(stack, MAX_RULES);
            if (ruleIndex < ruleExcludes.size()) {
                String ruleExclude = ruleExcludes.get(ruleIndex);
                if (ruleExclude != null && !ruleExclude.trim()
                    .isEmpty()) {
                    for (String token : ruleExclude.split("[,;\\s]+")) {
                        String trimmed = token.trim();
                        if (!trimmed.isEmpty()) {
                            excludePatterns.add(trimmed);
                        }
                    }
                }
            }

            // 统计该规则的候选材料(应用排除)
            for (String candidate : collectRuleCandidatePool(input, output)) {
                if (candidate != null && !isExcluded(candidate, excludePatterns)) {
                    totalCount++;
                }
            }
        }

        return totalCount;
    }

    /**
     * 获取指定规则的候选材料(用于去重功能)
     */
    public static Set<String> getCandidateMaterials(ItemStack preview, int ruleIndex) {
        if (preview == null || preview.stackTagCompound == null) {
            return new HashSet<>();
        }
        // 从NBT中读取对应规则的条目
        List<WildcardPatternEntry> inputs = WildcardPatternState.getInputEntries(preview);
        if (ruleIndex >= 0 && ruleIndex < inputs.size()) {
            WildcardPatternEntry entry = inputs.get(ruleIndex);
            if (!entry.isEmpty()) {
                return entry.getCandidateMaterials();
            }
        }
        return new HashSet<>();
    }

    /**
     * 生成的配方数据类
     */
    public static class GeneratedPattern {

        public final String materialName;
        public final ItemStack inputStack;
        public final ItemStack outputStack;

        public GeneratedPattern(String materialName, ItemStack inputStack, ItemStack outputStack) {
            this.materialName = materialName == null ? "" : materialName;
            this.inputStack = inputStack == null ? null : inputStack.copy();
            this.outputStack = outputStack == null ? null : outputStack.copy();
        }

        public String getMaterialName() {
            return materialName;
        }
    }
}
