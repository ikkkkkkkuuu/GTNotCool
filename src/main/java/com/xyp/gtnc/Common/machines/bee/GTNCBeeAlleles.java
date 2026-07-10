package com.xyp.gtnc.Common.machines.bee;

import com.xyp.gtnc.Config.Config;
import com.xyp.gtnc.ScienceNotCool;

import cpw.mods.fml.common.Loader;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;

/**
 * 本 mod 自注册的「满分」蜜蜂等位基因。
 * <p>
 * 与其依赖别的蜂 mod（MagicBees 致盲 2.0 / Avaritia 等）是否安装、数值多少，不如自己在
 * Forestry 的等位基因注册表里注册两个可控数值的基因：
 * <ul>
 * <li>速度「无尽」——{@link forestry.api.genetics.IAlleleFloat}，数值 {@link Config#customBeeSpeedValue}</li>
 * <li>寿命「不死」——{@link forestry.api.genetics.IAlleleInteger}，数值 {@link Config#customBeeLifespanValue}</li>
 * </ul>
 * 这两个基因注册在 {@link EnumBeeChromosome#SPEED} / {@link EnumBeeChromosome#LIFESPAN} 染色体上，
 * 因此 可直接写入，且能被 NEI / 分析仪正常识别显示。
 * <p>
 * UID 由 Forestry 规则生成：{@code modId + '.' + category + Capitalize(valueName)}，
 * 即 {@code sciencenotcool.beeSpeedEndless} / {@code sciencenotcool.beeLifespanUndying}。
 */
// Forestry 等位基因默认本地化 key 为 [modId].allele.[valueName]

// #tr sciencenotcool.allele.endless
// # Endless
// # zh_CN 无尽

// #tr sciencenotcool.allele.undying
// # Undying
// # zh_CN 不死

public final class GTNCBeeAlleles {

    private GTNCBeeAlleles() {}

    /** 自注册的速度「无尽」等位基因（未注册或关闭时为 null）。 */
    public static IAllele speedAllele;

    /** 自注册的寿命「不死」等位基因（未注册或关闭时为 null）。 */
    public static IAllele lifespanAllele;

    private static boolean registered = false;

    /**
     * 在 init 阶段调用（此时 Forestry 已在 preInit 注册完自己的等位基因与工厂）。
     * 幂等：重复调用只注册一次。
     */
    public static void register() {
        if (registered) return;
        registered = true;

        if (!Config.enableCustomBeeAlleles) return;
        if (!Loader.isModLoaded("Forestry")) return;
        if (AlleleManager.alleleFactory == null || AlleleManager.alleleRegistry == null) {
            ScienceNotCool.LOG.warn("[Bee] Forestry allele factory unavailable, skip custom bee allele registration");
            return;
        }

        try {
            // 速度「无尽」：float 基因，显性，注册到 SPEED 染色体
            speedAllele = AlleleManager.alleleFactory.createFloat(
                ScienceNotCool.MODID,
                "beeSpeed",
                "endless",
                Config.customBeeSpeedValue,
                true,
                new IChromosomeType[] { EnumBeeChromosome.SPEED });

            // 寿命「不死」：integer 基因，显性，注册到 LIFESPAN 染色体
            lifespanAllele = AlleleManager.alleleFactory.createInteger(
                ScienceNotCool.MODID,
                "beeLifespan",
                "undying",
                Config.customBeeLifespanValue,
                true,
                new IChromosomeType[] { EnumBeeChromosome.LIFESPAN });

            ScienceNotCool.LOG.info(
                "[Bee] Registered custom alleles: speed(无尽)={} lifespan(不死)={}",
                Config.customBeeSpeedValue,
                Config.customBeeLifespanValue);
        } catch (Throwable t) {
            ScienceNotCool.LOG.error("[Bee] Failed to register custom bee alleles", t);
            speedAllele = null;
            lifespanAllele = null;
        }
    }
}
