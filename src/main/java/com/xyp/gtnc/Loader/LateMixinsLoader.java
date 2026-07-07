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

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        // 只需在这里添加新的 Mixin 类名即可(相对于 com.xyp.gtnc.mixins.late 的路径)
        return mixins(
            // EnderIO
            "EnderIO.MixinNetworkedInventory",
            "EnderIO.MixinItemConduit",
            // Applied Energistics 2
            "AppliedEnergistics.DualityInterfaceMixin",
            "AppliedEnergistics.ItemEncodedPatternMixin",
            "AppliedEnergistics.MTEHatchCraftingInputMEMixin",
            "AppliedEnergistics.PatternMultiplierHelperMixin",
            "AppliedEnergistics.SuperMTEHatchCraftingInputMEMixin",
            "AppliedEnergistics.AccessorTaskProgress",
            "AppliedEnergistics.MixinCraftingCPUCluster",
            "AppliedEnergistics.MixinFinalOutput",
            "AppliedEnergistics.quantumComputer.MixinCraftingCPUCluster",
            "AppliedEnergistics.quantumComputer.MixinCraftingGridCache",
            // Gregtech
            "Gregtech.GTMetaTools",
            "Gregtech.MixinElectricBlastFurnace",
            "Gregtech.HatchOutputBusMEMixin",
            "Gregtech.HatchOutputMEMixin",
            "Gregtech.MEOutputHatchCapacityMixin",
            "Gregtech.MixinMTEBasicMachineFacing",
            "Gregtech.ModifySomeConfigs",
            "Gregtech.MixinMTEAssemblyLine",
            "Gregtech.MixinMTEAdvAssLine",
            // CropsNH
            "CropsNH.MixinTileEntityCropSticks",
            "CropsNH.MixinSeedStats");

    }

    private static List<String> mixins(String... mixinNames) {
        List<String> list = new ArrayList<>();
        for (String name : mixinNames) {
            if (name != null && !name.isEmpty()) {
                list.add(name);
            }
        }
        return list;
    }
}
