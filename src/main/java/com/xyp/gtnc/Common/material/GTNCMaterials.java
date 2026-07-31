package com.xyp.gtnc.Common.material;

import static bartworks.util.BWUtil.subscriptNumbers;

import org.apache.commons.lang3.tuple.Pair;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TextureSet;

/**
 * GT-Not-Cool 自定义材料注册。参照 ThinkTech 模式，static final 直接初始化，run() 空体。
 *
 * <p>
 * ID 偏移量：29_000，避免与 GTNH 内置材料、GT++、GTNL、ThinkTech 等冲突。
 * </p>
 */
public class GTNCMaterials implements Runnable {

    protected static final int OFFSET_ID = 29_000;

    // ==== MTO催化剂 ====
    /** SAPO-34 分子筛 — MTO（甲醇制烯烃）催化剂，不消耗 */
    public static final Werkstoff SAPO34 = new Werkstoff(
        new short[] { 83, 146, 163 },
        "SAPO-34",
        subscriptNumbers("(SiO2)x(AlO2)y(PO2)z"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 1,
        TextureSet.SET_SHINY);

    // ==== 火箭燃料化工 ====
    /** 高氯酸铵 (NH₄ClO₄) — T2固体火箭氧化剂，消耗粉 */
    public static final Werkstoff AmmoniumPerchlorate = new Werkstoff(
        new short[] { 230, 225, 215 },
        "AmmoniumPerchlorate",
        subscriptNumbers("NH4ClO4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 2,
        TextureSet.SET_DULL);

    /** 二硝酰胺铵 ADN (NH₄N₃O₄) — T3高能固体火箭氧化剂，消耗粉 */
    public static final Werkstoff AmmoniumDinitramide = new Werkstoff(
        new short[] { 180, 200, 255 },
        "AmmoniumDinitramide",
        subscriptNumbers("NH4N3O4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 3,
        TextureSet.SET_DULL);

    /** 硅岩氧化剂 — T6高能火箭氧化剂，消耗粉 */
    public static final Werkstoff NaquadahOxidizer = new Werkstoff(
        new short[] { 80, 180, 80 },
        "NaquadahOxidizer",
        subscriptNumbers("Naq-O-F"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 4,
        TextureSet.SET_DULL);

    // ==== 压缩蒸汽 ====
    /** 压缩蒸汽 — 高温高压金属态材料 */
    public static final Werkstoff CompressedSteam = new Werkstoff(
        new short[] { 211, 211, 211 },
        "Compressed Steam",
        subscriptNumbers("H2O"),
        new Werkstoff.Stats().setMass(50)
            .setProtons(8000)
            .setQualityOverride((byte) 10)
            .setSpeedOverride(50)
            .setDurOverride(600000),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()
            .addDoubleAndDensePlates()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        OFFSET_ID + 5,
        TextureSet.SET_SHINY);

    // ==== ThinkTech 移植材料 ====

    // 烷烃水混合物
    public static final Werkstoff AlkaneWaterMixture = new Werkstoff(
        new short[] { 178, 176, 176 },
        "AlkaneWaterMixture",
        subscriptNumbers("CH4·xH2O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 6,
        TextureSet.SET_FLUID);

    // TNT
    public static final Werkstoff Trinitrotoluene = new Werkstoff(
        new short[] { 191, 104, 50 },
        "Trinitrotoluene",
        subscriptNumbers("C7H5N3O6"),
        new Werkstoff.Stats().setMeltingPoint(354),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 7,
        TextureSet.SET_RUBY);

    // 叠氮化铅
    public static final Werkstoff LeadAzide = new Werkstoff(
        new short[] { 233, 238, 232 },
        "LeadAzide",
        subscriptNumbers("Pb(N3)2"),
        new Werkstoff.Stats().setMeltingPoint(350),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 8,
        TextureSet.SET_DULL);

    // PETN
    public static final Werkstoff PETN = new Werkstoff(
        new short[] { 221, 221, 221 },
        "PETN",
        subscriptNumbers("C5H8N4O12"),
        new Werkstoff.Stats().setMeltingPoint(139),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 9,
        TextureSet.SET_RUBY);

    // HMX
    public static final Werkstoff HMX = new Werkstoff(
        new short[] { 226, 226, 228 },
        "HMX",
        subscriptNumbers("C4H8N8O8"),
        new Werkstoff.Stats().setMeltingPoint(275),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 10,
        TextureSet.SET_RUBY);

    // HNIW / CL-20
    public static final Werkstoff HNIW = new Werkstoff(
        new short[] { 230, 230, 230 },
        "HNIW",
        subscriptNumbers("C6N12H6O12"),
        new Werkstoff.Stats().setMeltingPoint(275),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 11,
        TextureSet.SET_RUBY);

    // HMT (乌洛托品)
    public static final Werkstoff HMT = new Werkstoff(
        new short[] { 230, 230, 230 },
        "HMT",
        subscriptNumbers("C6H12N4"),
        new Werkstoff.Stats().setMeltingPoint(280),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 12,
        TextureSet.SET_RUBY);

    // 乙二醛
    public static final Werkstoff Ethanedial = new Werkstoff(
        new short[] { 182, 186, 44 },
        "Ethanedial",
        subscriptNumbers("C2H2O2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 13,
        TextureSet.SET_FLUID);

    // 苄胺
    public static final Werkstoff Phenylmethanamine = new Werkstoff(
        new short[] { 178, 176, 176 },
        "Phenylmethanamine",
        subscriptNumbers("C6H5CH2NH2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 14,
        TextureSet.SET_FLUID);

    // 苯甲醛
    public static final Werkstoff Benzaldehyd = new Werkstoff(
        new short[] { 178, 176, 176 },
        "Benzaldehyd",
        subscriptNumbers("C7H6O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 15,
        TextureSet.SET_FLUID);

    // 五氯化磷
    public static final Werkstoff Pentachloride = new Werkstoff(
        new short[] { 190, 196, 70 },
        "Pentachloride",
        subscriptNumbers("PCl5"),
        new Werkstoff.Stats().setMeltingPoint(440),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 16,
        TextureSet.SET_RUBY);

    // 叠氮化钠
    public static final Werkstoff SodiumAzide = new Werkstoff(
        new short[] { 221, 221, 221 },
        "SodiumAzide",
        subscriptNumbers("NaN3"),
        new Werkstoff.Stats().setMeltingPoint(548),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 17,
        TextureSet.SET_DULL);

    // 预培养维生细菌液
    public static final Werkstoff PreculturedBacterialSolution = new Werkstoff(
        new short[] { 254, 243, 97 },
        "PreculturedBacterialSolution",
        subscriptNumbers("???"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 18,
        TextureSet.SET_FLUID);

    // 预培养维生冷藏细菌液
    public static final Werkstoff FreezedPreculturedBacterialSolution = new Werkstoff(
        new short[] { 232, 228, 186 },
        "FreezedPreculturedBacterialSolution",
        subscriptNumbers("???"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 19,
        TextureSet.SET_FLUID);

    // 待处理浓缩菌泥
    public static final Werkstoff RawBioSludge = new Werkstoff(
        new short[] { 8, 57, 1 },
        "RawBioSludge",
        subscriptNumbers("???"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 20,
        TextureSet.SET_FLUID);

    // 二氧化硒
    public static final Werkstoff SeleniumDioxide = new Werkstoff(
        new short[] { 211, 204, 204 },
        "SeleniumDioxide",
        subscriptNumbers("SeO2"),
        new Werkstoff.Stats().setMeltingPoint(456),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust()
            .addMolten(),
        OFFSET_ID + 21,
        TextureSet.SET_DULL);

    // 营养液
    public static final Werkstoff NutrientSolution = new Werkstoff(
        new short[] { 86, 115, 79 },
        "NutrientSolution",
        subscriptNumbers("???"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 22,
        TextureSet.SET_FLUID);

    // 光刻胶
    public static final Werkstoff Photoresist = new Werkstoff(
        new short[] { 156, 238, 220 },
        "Photoresist",
        subscriptNumbers("???"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 23,
        TextureSet.SET_FLUID);

    // 三相萃取铟溶液
    public static final Werkstoff Sxcqyry = new Werkstoff(
        new short[] { 12, 72, 82 },
        "Sxcqyry",
        subscriptNumbers("??In??"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 24,
        TextureSet.SET_FLUID);

    // 低浓度萃取液
    public static final Werkstoff Dndcq = new Werkstoff(
        new short[] { 180, 194, 197 },
        "Dndcq",
        subscriptNumbers("???"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 25,
        TextureSet.SET_FLUID);

    // ==== GTNL 独立化工材料 ====

    // 氯化钡
    public static final Werkstoff BariumChloride = new Werkstoff(
        new short[] { 207, 99, 84 },
        "Barium Chloride",
        subscriptNumbers("BaCl2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 26,
        TextureSet.SET_SHINY);

    // 马来酸酐
    public static final Werkstoff MaleicAnhydride = new Werkstoff(
        new short[] { 155, 239, 244 },
        "Maleic Anhydride",
        subscriptNumbers("C4H2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 27,
        TextureSet.SET_FLUID);

    // 均四甲苯
    public static final Werkstoff Durene = new Werkstoff(
        new short[] { 99, 114, 128 },
        "Durene",
        subscriptNumbers("C10H14"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 28,
        TextureSet.SET_SHINY);

    // 三氟化锑
    public static final Werkstoff AntimonyTrifluoride = new Werkstoff(
        new short[] { 199, 194, 180 },
        "Antimony Trifluoride",
        subscriptNumbers("SbF3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 29,
        TextureSet.SET_SHINY);

    // 硫酸氢铵
    public static final Werkstoff AmmoniumBisulfate = new Werkstoff(
        new short[] { 250, 245, 225 },
        "Ammonium Bisulfate",
        subscriptNumbers("(NH4)HSO4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.COMPOUND,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 30,
        TextureSet.SET_FLUID);

    // 均苯四甲酸二酐 PMDA
    public static final Werkstoff PMDA = new Werkstoff(
        new short[] { 99, 114, 128 },
        "Pyromellitic Dianhydride",
        subscriptNumbers("C10H2O6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 31,
        TextureSet.SET_SHINY);

    // 丁二酸
    public static final Werkstoff SuccinicAcid = new Werkstoff(
        new short[] { 80, 87, 86 },
        "Succinic Acid",
        subscriptNumbers("C4H6O4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        OFFSET_ID + 32,
        TextureSet.SET_SHINY);

    // 硝基苯类流体 (SmallBaka)
    public static final Werkstoff SmallBaka = new Werkstoff(
        new short[] { 65, 105, 225 },
        "Small Baka",
        subscriptNumbers("C6H5N3O2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        OFFSET_ID + 33,
        TextureSet.SET_FLUID);

    // ==== GTNL 电路用材料 ====

    // 聚酰亚胺
    public static final Werkstoff Polyimide = new Werkstoff(
        new short[] { 248, 100, 47 },
        "Polyimide",
        subscriptNumbers("C22H12N2O6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()
            .addDoubleAndDensePlates()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        OFFSET_ID + 34,
        TextureSet.SET_FLUID);

    // 锗钨氮化物
    public static final Werkstoff Germaniumtungstennitride = new Werkstoff(
        new short[] { 111, 11, 160 },
        "Germaniumtungstennitride",
        subscriptNumbers("Ge3W3N10"),
        new Werkstoff.Stats().setCentrifuge(true)
            .setBlastFurnace(true)
            .setMeltingPoint(8200)
            .setMeltingVoltage(30720),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()
            .addDoubleAndDensePlates()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        OFFSET_ID + 35,
        TextureSet.SET_SHINY);

    // 二硅化钼 MoSi₂
    public static final Werkstoff MolybdenumDisilicide = new Werkstoff(
        new short[] { 82, 74, 125 },
        "Molybdenum Disilicide",
        subscriptNumbers("MoSi2"),
        new Werkstoff.Stats().setCentrifuge(true)
            .setBlastFurnace(true)
            .setMeltingPoint(2301)
            .setMeltingVoltage(1920),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()
            .addDoubleAndDensePlates()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes()
            .addMixerRecipes((short) 2),
        OFFSET_ID + 36,
        TextureSet.SET_SHINY,
        Pair.of(Materials.Molybdenum, 1),
        Pair.of(Materials.Silicon, 2));

    // HSLA钢
    public static final Werkstoff HSLASteel = new Werkstoff(
        new short[] { 96, 98, 101 },
        "HSLA Steel",
        subscriptNumbers("(Fe2Ni)2VTiMo"),
        new Werkstoff.Stats().setCentrifuge(true)
            .setBlastFurnace(true)
            .setMeltingPoint(1711)
            .setMeltingVoltage(480)
            .setToxic(true),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()
            .addDoubleAndDensePlates()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes()
            .addMixerRecipes((short) 4),
        OFFSET_ID + 37,
        TextureSet.SET_SHINY,
        Pair.of(Materials.Invar, 2),
        Pair.of(Materials.Vanadium, 1),
        Pair.of(Materials.Titanium, 1),
        Pair.of(Materials.Molybdenum, 1));

    @Override
    public void run() {}
}
