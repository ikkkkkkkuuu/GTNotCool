package com.xyp.gtnc.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class LateMixinsLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.sciencenotcool.late.json";
    }

    // region 目标 mod 的 modid(与各 mod mcmod.info 完全一致，注意大小写)
    private static final String MOD_ENDER_IO = "EnderIO";
    private static final String MOD_AE2 = "appliedenergistics2";
    private static final String MOD_GREGTECH = "gregtech";
    private static final String MOD_CROPSNH = "cropsnh";
    private static final String MOD_FORESTRY = "Forestry";
    private static final String MOD_THAUMCRAFT = "Thaumcraft";
    // endregion

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        // 每组 Mixin 仅在其目标 mod 被加载时才注册，避免目标 mod 缺失时注入失败/崩溃。
        List<String> list = new ArrayList<>();

        if (loadedMods.contains(MOD_ENDER_IO)) {
            addAll(list, "EnderIO.MixinNetworkedInventory", "EnderIO.MixinItemConduit");
        }

        if (loadedMods.contains(MOD_AE2)) {
            addAll(
                list,
                "AppliedEnergistics.DualityInterfaceMixin",
                "AppliedEnergistics.ItemEncodedPatternMixin",
                "AppliedEnergistics.MTEHatchCraftingInputMEMixin",
                "AppliedEnergistics.PatternMultiplierHelperMixin",
                "AppliedEnergistics.SuperMTEHatchCraftingInputMEMixin",
                "AppliedEnergistics.AccessorTaskProgress",
                "AppliedEnergistics.MixinCraftingCPUCluster",
                "AppliedEnergistics.MixinFinalOutput",
                "AppliedEnergistics.MixinGridNodeChannels",
                "AppliedEnergistics.MixinPathGridCacheChannels",
                "AppliedEnergistics.quantumComputer.MixinCraftingCPUCluster",
                "AppliedEnergistics.quantumComputer.MixinCraftingGridCache");
        }

        if (loadedMods.contains(MOD_GREGTECH)) {
            addAll(
                list,
                "Gregtech.GTMetaTools",
                "Gregtech.MixinElectricBlastFurnace",
                "Gregtech.HatchOutputBusMEMixin",
                "Gregtech.HatchOutputMEMixin",
                "Gregtech.MEOutputHatchCapacityMixin",
                "Gregtech.MixinMTEBasicMachineFacing",
                "Gregtech.ModifySomeConfigs",
                "Gregtech.MixinMTEAssemblyLine",
                "Gregtech.MixinMTEAdvAssLine");
        }

        if (loadedMods.contains(MOD_CROPSNH)) {
            addAll(list, "CropsNH.MixinTileEntityCropSticks", "CropsNH.MixinSeedStats");
        }

        if (loadedMods.contains(MOD_FORESTRY)) {
            addAll(list, "Forestry.MixinBee", "Forestry.MixinBeeHomozygous", "Forestry.MixinBeeGenomeEnvironment");
        }

        if (loadedMods.contains(MOD_THAUMCRAFT)) {
            addAll(
                list,
                "Thaumcraft.MixinWarpEvents",
                "Thaumcraft.MixinResearchManager",
                "Thaumcraft.MixinPlayerKnowledge",
                "Thaumcraft.MixinTileCrucible",
                "Thaumcraft.MixinTileInfusionMatrix",
                "Thaumcraft.MixinVisNetHandler");
        }

        return list;
    }

    private static void addAll(List<String> list, String... mixinNames) {
        for (String name : mixinNames) {
            if (name != null && !name.isEmpty()) {
                list.add(name);
            }
        }
    }
}
