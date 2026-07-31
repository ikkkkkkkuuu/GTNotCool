package com.xyp.gtnc.Loader;

import java.util.HashMap;
import java.util.Map;

import com.xyp.gtnc.Common.material.GTNCMaterials;
import com.xyp.gtnc.mixins.early.gregtech.AccessorGTLanguageManager;
import com.xyp.gtnc.utils.lang.TextLocalization;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.WerkstoffLoader;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTLanguageManager;

/**
 * Werkstoff 材料中文名注册。
 * 参照 GTNL LanguageLoader + Utils.storeTranslation 模式：
 * 1. 写入 GregTech 语言配置文件
 * 2. 注入 GT 和原版运行时语言表，无需重启即可生效
 */
public class GTNCLanguageLoader {

    private static boolean fluidPipePrefixLocalized;
    private static boolean itemPipePrefixLocalized;

    private GTNCLanguageLoader() {}

    public static void registry() {
        String currentLanguage = FMLCommonHandler.instance()
            .getCurrentLanguage();
        if (currentLanguage == null || currentLanguage.isEmpty()) {
            currentLanguage = GTLanguageManager.LanguageCode;
        }
        if (!"zh_CN".equals(currentLanguage)) return;

        // ==== MTO催化剂 ====
        addWerkstoffLocalization(GTNCMaterials.SAPO34, TextLocalization.Material_SAPO34, false);

        // ==== 火箭燃料化工 ====
        addWerkstoffLocalization(
            GTNCMaterials.AmmoniumPerchlorate,
            TextLocalization.Material_AmmoniumPerchlorate,
            false);
        addWerkstoffLocalization(
            GTNCMaterials.AmmoniumDinitramide,
            TextLocalization.Material_AmmoniumDinitramide,
            false);
        addWerkstoffLocalization(GTNCMaterials.NaquadahOxidizer, TextLocalization.Material_NaquadahOxidizer, false);

        // ==== 压缩蒸汽 ====
        addWerkstoffLocalization(GTNCMaterials.CompressedSteam, TextLocalization.Material_CompressedSteam, true);

        // ==== ThinkTech 移植材料 ====
        addWerkstoffLocalization(GTNCMaterials.AlkaneWaterMixture, TextLocalization.Material_AlkaneWaterMixture, false);
        addWerkstoffLocalization(GTNCMaterials.Trinitrotoluene, TextLocalization.Material_Trinitrotoluene, false);
        addWerkstoffLocalization(GTNCMaterials.LeadAzide, TextLocalization.Material_LeadAzide, false);
        addWerkstoffLocalization(GTNCMaterials.PETN, TextLocalization.Material_PETN, false);
        addWerkstoffLocalization(GTNCMaterials.HMX, TextLocalization.Material_HMX, false);
        addWerkstoffLocalization(GTNCMaterials.HNIW, TextLocalization.Material_HNIW, false);
        addWerkstoffLocalization(GTNCMaterials.HMT, TextLocalization.Material_HMT, false);
        addWerkstoffLocalization(GTNCMaterials.Ethanedial, TextLocalization.Material_Ethanedial, false);
        addWerkstoffLocalization(GTNCMaterials.Phenylmethanamine, TextLocalization.Material_Phenylmethanamine, false);
        addWerkstoffLocalization(GTNCMaterials.Benzaldehyd, TextLocalization.Material_Benzaldehyd, false);
        addWerkstoffLocalization(GTNCMaterials.Pentachloride, TextLocalization.Material_Pentachloride, false);
        addWerkstoffLocalization(GTNCMaterials.SodiumAzide, TextLocalization.Material_SodiumAzide, false);
        addWerkstoffLocalization(
            GTNCMaterials.PreculturedBacterialSolution,
            TextLocalization.Material_PreculturedBacterialSolution,
            false);
        addWerkstoffLocalization(
            GTNCMaterials.FreezedPreculturedBacterialSolution,
            TextLocalization.Material_FreezedPreculturedBacterialSolution,
            false);
        addWerkstoffLocalization(GTNCMaterials.RawBioSludge, TextLocalization.Material_RawBioSludge, false);
        addWerkstoffLocalization(GTNCMaterials.SeleniumDioxide, TextLocalization.Material_SeleniumDioxide, false);
        addWerkstoffLocalization(GTNCMaterials.NutrientSolution, TextLocalization.Material_NutrientSolution, false);
        addWerkstoffLocalization(GTNCMaterials.Photoresist, TextLocalization.Material_Photoresist, false);
        addWerkstoffLocalization(GTNCMaterials.Sxcqyry, TextLocalization.Material_Sxcqyry, false);
        addWerkstoffLocalization(GTNCMaterials.Dndcq, TextLocalization.Material_Dndcq, false);

        // ==== GTNL 独立化工材料 ====
        addWerkstoffLocalization(GTNCMaterials.BariumChloride, TextLocalization.Material_BariumChloride, false);
        addWerkstoffLocalization(GTNCMaterials.MaleicAnhydride, TextLocalization.Material_MaleicAnhydride, false);
        addWerkstoffLocalization(GTNCMaterials.Durene, TextLocalization.Material_Durene, false);
        addWerkstoffLocalization(
            GTNCMaterials.AntimonyTrifluoride,
            TextLocalization.Material_AntimonyTrifluoride,
            false);
        addWerkstoffLocalization(GTNCMaterials.AmmoniumBisulfate, TextLocalization.Material_AmmoniumBisulfate, false);
        addWerkstoffLocalization(GTNCMaterials.PMDA, TextLocalization.Material_PMDA, false);
        addWerkstoffLocalization(GTNCMaterials.SuccinicAcid, TextLocalization.Material_SuccinicAcid, false);
        addWerkstoffLocalization(GTNCMaterials.SmallBaka, TextLocalization.Material_SmallBaka, false);

        // ==== GTNL 电路用材料 ====
        addWerkstoffLocalization(GTNCMaterials.Polyimide, TextLocalization.Material_Polyimide, true);
        addWerkstoffLocalization(
            GTNCMaterials.Germaniumtungstennitride,
            TextLocalization.Material_Germaniumtungstennitride,
            true);
        addWerkstoffLocalization(
            GTNCMaterials.MolybdenumDisilicide,
            TextLocalization.Material_MolybdenumDisilicide,
            true);
        addWerkstoffLocalization(GTNCMaterials.HSLASteel, TextLocalization.Material_HSLASteel, true);
    }

    private static void addWerkstoffLocalization(Werkstoff werkstoff, String zhName, boolean isItemPipe) {
        if (werkstoff == null) return;

        String unlocalizedName = werkstoff.getDefaultName()
            .toLowerCase();
        String mName = unlocalizedName.replace(" ", "");

        storeTranslation("Material." + mName, zhName);
        storeTranslation("bw.werkstoff." + werkstoff.getmID() + ".name", zhName);

        if (werkstoff.hasItemType(OrePrefixes.cellMolten)) {
            storeTranslation("fluid.molten." + unlocalizedName, "熔融" + zhName);
        }
        if (werkstoff.hasItemType(OrePrefixes.cell)) {
            storeTranslation("fluid." + unlocalizedName, zhName);
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt01, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.wire." + unlocalizedName + ".01.name", "1x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt02, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.wire." + unlocalizedName + ".02.name", "2x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt04, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.wire." + unlocalizedName + ".04.name", "4x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt08, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.wire." + unlocalizedName + ".08.name", "8x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt12, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.wire." + unlocalizedName + ".12.name", "12x%s导线");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.wireGt16, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.wire." + unlocalizedName + ".16.name", "16x%s导线");
        }

        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt01, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.cable." + unlocalizedName + ".01.name", "1x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt02, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.cable." + unlocalizedName + ".02.name", "2x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt04, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.cable." + unlocalizedName + ".04.name", "4x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt08, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.cable." + unlocalizedName + ".08.name", "8x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt12, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.cable." + unlocalizedName + ".12.name", "12x%s线缆");
        }
        if (WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.cableGt16, werkstoff, 1) != null) {
            storeTranslation("gt.blockmachines.cable." + unlocalizedName + ".16.name", "16x%s线缆");
        }

        if (hasAnyPipePrefix(werkstoff)) {
            if (isItemPipe) {
                registerItemPipePrefixLocalization();
            } else {
                registerFluidPipePrefixLocalization();
            }
        }
    }

    /** 参照 GTNL Utils.storeTranslation：写入配置文件 + 注入运行时语言表 */
    private static synchronized void storeTranslation(String key, String text) {
        // 1. 写 GregTech 配置文件
        GTLanguageManager.addStringLocalization(key, text);

        // 2. 注入 GT 运行时语言表
        Map<String, String> langMap = AccessorGTLanguageManager.getLangMap();
        if (langMap != null) langMap.put(key, text);

        // 3. 注入 GT fallback 语言表
        Map<String, String> fallbackMap = AccessorGTLanguageManager.getStringTranslateLanguageListFallBack();
        if (fallbackMap != null) fallbackMap.put(key, text);

        // 4. 通过 LanguageRegistry 注入客户端
        HashMap<String, String> tempMap = AccessorGTLanguageManager.getTempMap();
        tempMap.put(key, text);
        LanguageRegistry.instance()
            .injectLanguage("en_US", tempMap);
        tempMap.clear();
    }

    private static boolean hasAnyPipePrefix(Werkstoff werkstoff) {
        return WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeTiny, werkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeSmall, werkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeMedium, werkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeLarge, werkstoff, 1) != null
            || WerkstoffLoader.getCorrespondingItemStackUnsafe(OrePrefixes.pipeHuge, werkstoff, 1) != null;
    }

    private static void registerFluidPipePrefixLocalization() {
        if (fluidPipePrefixLocalized) return;
        storeTranslation("gt.oreprefix.tiny_material_fluid_pipe", "微型%s流体管道");
        storeTranslation("gt.oreprefix.small_material_fluid_pipe", "小型%s流体管道");
        storeTranslation("gt.oreprefix.material_fluid_pipe", "%s流体管道");
        storeTranslation("gt.oreprefix.large_material_fluid_pipe", "大型%s流体管道");
        storeTranslation("gt.oreprefix.huge_material_fluid_pipe", "巨型%s流体管道");
        storeTranslation("gt.oreprefix.quadruple_material_fluid_pipe", "四联%s流体管道");
        storeTranslation("gt.oreprefix.nonuple_material_fluid_pipe", "九联%s流体管道");
        fluidPipePrefixLocalized = true;
    }

    private static void registerItemPipePrefixLocalization() {
        if (itemPipePrefixLocalized) return;
        storeTranslation("gt.oreprefix.tiny_material_item_pipe", "微型%s物品管道");
        storeTranslation("gt.oreprefix.small_material_item_pipe", "小型%s物品管道");
        storeTranslation("gt.oreprefix.material_item_pipe", "%s物品管道");
        storeTranslation("gt.oreprefix.large_material_item_pipe", "大型%s物品管道");
        storeTranslation("gt.oreprefix.huge_material_item_pipe", "巨型%s物品管道");
        storeTranslation("gt.oreprefix.tiny_restrictive_material_item_pipe", "微型限流%s物品管道");
        storeTranslation("gt.oreprefix.small_restrictive_material_item_pipe", "小型限流%s物品管道");
        storeTranslation("gt.oreprefix.restrictive_material_item_pipe", "限流%s物品管道");
        storeTranslation("gt.oreprefix.large_restrictive_material_item_pipe", "大型限流%s物品管道");
        storeTranslation("gt.oreprefix.huge_restrictive_material_item_pipe", "巨型限流%s物品管道");
        itemPipePrefixLocalized = true;
    }
}
