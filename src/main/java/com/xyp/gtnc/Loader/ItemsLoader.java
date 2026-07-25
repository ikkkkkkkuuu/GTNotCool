package com.xyp.gtnc.Loader;

import net.minecraft.item.Item;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Common.items.MetaItemAdder;
import com.xyp.gtnc.Common.items.bee.EndlessFrameItem;
import com.xyp.gtnc.Common.items.bee.MutagenicFrameItem;
import com.xyp.gtnc.Common.items.tools.VeinMiningPickaxe;
import com.xyp.gtnc.Common.items.wildcard.WildcardPatternItem;
import com.xyp.gtnc.utils.enums.GTNCItemList;
import com.xyp.gtnc.utils.lang.TextLocalization;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.util.GTOreDictUnificator;

/**
 * 物品加载器，负责注册所有物品
 */
public class ItemsLoader {

    public static VeinMiningPickaxe veinMiningPickaxe;

    // 通配样板符
    public static Item wildcardPattern;

    // 诱变框架（杂交成功率倍增）
    public static Item mutagenicFrame;

    // 无尽框架（寿命无限 + 高产量，宇宙彩虹材质）
    public static Item endlessFrame;

    // MetaItem 系统
    public static Item metaItem;

    public static void initItems() {
        // 注册 MetaItem
        metaItem = new MetaItemAdder("MetaItem", GTNCCreativeTabs.GTNCItem);

        // 注册矿脉挖掘镐
        veinMiningPickaxe = new VeinMiningPickaxe();

        // 注册通配样板符
        wildcardPattern = new WildcardPatternItem();
        GameRegistry.registerItem(wildcardPattern, WildcardPatternItem.ITEM_NAME);

        // 注册诱变框架（+80% 杂交成功率、不衰变、永不磨损）
        mutagenicFrame = new MutagenicFrameItem();
        GameRegistry.registerItem(mutagenicFrame, MutagenicFrameItem.ITEM_NAME);

        // 注册无尽框架（寿命无限 + 产量×30、不杂交、不衰变、永不磨损，宇宙彩虹材质）
        endlessFrame = new EndlessFrameItem();
        GameRegistry.registerItem(endlessFrame, EndlessFrameItem.ITEM_NAME);
    }

    public static void registryMetaItems() {

        // #tr item.MetaItem.1.name
        // # High Computing Power Chip Tier I
        // # zh_CN 高算力芯片 I
        GTNCItemList.ChipTier1.set(MetaItemAdder.initItem(1));

        // #tr item.MetaItem.2.name
        // # High Computing Power Chip Tier II
        // # zh_CN 高算力芯片 II
        GTNCItemList.ChipTier2.set(MetaItemAdder.initItem(2));

        // #tr item.MetaItem.3.name
        // # High Computing Power Chip Tier III
        // # zh_CN 高算力芯片 III
        GTNCItemList.ChipTier3.set(MetaItemAdder.initItem(3));

        // #tr item.MetaItem.4.name
        // # High Computing Power Chip Tier IV
        // # zh_CN 高算力芯片 IV
        GTNCItemList.ChipTier4.set(MetaItemAdder.initItem(4));

        // #tr item.MetaItem.5.name
        // # High Computing Power Chip Tier V
        // # zh_CN 高算力芯片 V
        GTNCItemList.ChipTier5.set(MetaItemAdder.initItem(5));

        // #tr item.MetaItem.6.name
        // # High Computing Power Chip Tier VI
        // # zh_CN 高算力芯片 VI
        GTNCItemList.ChipTier6.set(MetaItemAdder.initItem(6));

        // #tr item.MetaItem.7.name
        // # High Computing Power Chip Tier VII
        // # zh_CN 高算力芯片 VII
        GTNCItemList.ChipTier7.set(MetaItemAdder.initItem(7));

        // #tr item.MetaItem.9.name
        // # Primitive Resonatic Circuit
        // # zh_CN 原始磁共振电路
        GTNCItemList.CircuitResonaticULV.set(
            MetaItemAdder.initItem(
                9,
                new String[] { TextLocalization.Tooltip_CircuitResonaticULV_00,
                    TextLocalization.Tooltip_CircuitResonaticULV_01 }));
        // #tr item.MetaItem.10.name
        // # Basic Resonatic Circuit
        // # zh_CN 基础磁共振电路
        GTNCItemList.CircuitResonaticLV.set(
            MetaItemAdder.initItem(
                10,
                new String[] { TextLocalization.Tooltip_CircuitResonaticLV_00,
                    TextLocalization.Tooltip_CircuitResonaticLV_01 }));
        // #tr item.MetaItem.11.name
        // # Advanced Resonatic Circuit
        // # zh_CN 进阶磁共振电路
        GTNCItemList.CircuitResonaticMV.set(
            MetaItemAdder.initItem(
                11,
                new String[] { TextLocalization.Tooltip_CircuitResonaticMV_00,
                    TextLocalization.Tooltip_CircuitResonaticMV_01 }));
        // #tr item.MetaItem.12.name
        // # Progressive Resonatic Circuit
        // # zh_CN 先进磁共振电路
        GTNCItemList.CircuitResonaticHV.set(
            MetaItemAdder.initItem(
                12,
                new String[] { TextLocalization.Tooltip_CircuitResonaticHV_00,
                    TextLocalization.Tooltip_CircuitResonaticHV_01 }));
        // #tr item.MetaItem.13.name
        // # Data Resonatic Circuit
        // # zh_CN 数据磁共振电路
        GTNCItemList.CircuitResonaticEV.set(
            MetaItemAdder.initItem(
                13,
                new String[] { TextLocalization.Tooltip_CircuitResonaticEV_00,
                    TextLocalization.Tooltip_CircuitResonaticEV_01 }));
        // #tr item.MetaItem.14.name
        // # Elite Resonatic Circuit
        // # zh_CN 精英磁共振电路
        GTNCItemList.CircuitResonaticIV.set(
            MetaItemAdder.initItem(
                14,
                new String[] { TextLocalization.Tooltip_CircuitResonaticIV_00,
                    TextLocalization.Tooltip_CircuitResonaticIV_01 }));
        // #tr item.MetaItem.15.name
        // # Master Resonatic Circuit
        // # zh_CN 大师磁共振电路
        GTNCItemList.CircuitResonaticLuV.set(
            MetaItemAdder.initItem(
                15,
                new String[] { TextLocalization.Tooltip_CircuitResonaticLuV_00,
                    TextLocalization.Tooltip_CircuitResonaticLuV_01 }));
        // #tr item.MetaItem.16.name
        // # Top Resonatic Circuit
        // # zh_CN 顶级磁共振电路
        GTNCItemList.CircuitResonaticZPM.set(
            MetaItemAdder.initItem(
                16,
                new String[] { TextLocalization.Tooltip_CircuitResonaticZPM_00,
                    TextLocalization.Tooltip_CircuitResonaticZPM_01 }));
        // #tr item.MetaItem.17.name
        // # Superconducting Resonatic Circuit
        // # zh_CN 超导磁共振电路
        GTNCItemList.CircuitResonaticUV.set(
            MetaItemAdder.initItem(
                17,
                new String[] { TextLocalization.Tooltip_CircuitResonaticUV_00,
                    TextLocalization.Tooltip_CircuitResonaticUV_01 }));
        // #tr item.MetaItem.18.name
        // # Supreme Resonatic Circuit
        // # zh_CN 终极磁共振电路
        GTNCItemList.CircuitResonaticUHV.set(
            MetaItemAdder.initItem(
                18,
                new String[] { TextLocalization.Tooltip_CircuitResonaticUHV_00,
                    TextLocalization.Tooltip_CircuitResonaticUHV_01 }));
        // #tr item.MetaItem.19.name
        // # Bio Resonatic Circuit
        // # zh_CN 生物磁共振电路
        GTNCItemList.CircuitResonaticUEV.set(
            MetaItemAdder.initItem(
                19,
                new String[] { TextLocalization.Tooltip_CircuitResonaticUEV_00,
                    TextLocalization.Tooltip_CircuitResonaticUEV_01 }));
        // #tr item.MetaItem.20.name
        // # Optical Resonatic Circuit
        // # zh_CN 光学磁共振电路
        GTNCItemList.CircuitResonaticUIV.set(
            MetaItemAdder.initItem(
                20,
                new String[] { TextLocalization.Tooltip_CircuitResonaticUIV_00,
                    TextLocalization.Tooltip_CircuitResonaticUIV_01 }));

        // #tr item.MetaItem.21.name
        // # Very Simple Circuit
        // # zh_CN 极简控制电路
        GTNCItemList.VerySimpleCircuit.set(
            MetaItemAdder.initItem(
                21,
                new String[] { TextLocalization.Tooltip_VerySimpleCircuit_00,
                    TextLocalization.Tooltip_VerySimpleCircuit_01 }));

        // #tr item.MetaItem.22.name
        // # Simple Circuit
        // # zh_CN 简单控制电路
        GTNCItemList.SimpleCircuit.set(
            MetaItemAdder.initItem(
                22,
                new String[] { TextLocalization.Tooltip_SimpleCircuit_00, TextLocalization.Tooltip_SimpleCircuit_01 }));

        // #tr item.MetaItem.23.name
        // # Basic Circuit
        // # zh_CN 基础控制电路
        GTNCItemList.BasicCircuit.set(
            MetaItemAdder.initItem(
                23,
                new String[] { TextLocalization.Tooltip_BasicCircuit_00, TextLocalization.Tooltip_BasicCircuit_01 }));
        // #tr item.MetaItem.24.name
        // # Advanced Circuit
        // # zh_CN 高级控制电路
        GTNCItemList.AdvancedCircuit.set(
            MetaItemAdder.initItem(
                24,
                new String[] { TextLocalization.Tooltip_AdvancedCircuit_00,
                    TextLocalization.Tooltip_AdvancedCircuit_01 }));

        // #tr item.MetaItem.25.name
        // # Elite Circuit
        // # zh_CN 精英控制电路
        GTNCItemList.EliteCircuit.set(
            MetaItemAdder.initItem(
                25,
                new String[] { TextLocalization.Tooltip_EliteCircuit_00, TextLocalization.Tooltip_EliteCircuit_01 }));

        // #tr item.MetaItem.30.name
        // # Bioware SMD Inductor
        // # zh_CN 生物贴片电感
        GTNCItemList.BiowareSMDInductor.set(MetaItemAdder.initItem(30));

        // #tr item.MetaItem.31.name
        // # Critical Photon
        // # zh_CN 临界光子
        GTNCItemList.MiracleDoorPhoton
            .set(MetaItemAdder.initItem(31, new String[] { TextLocalization.Tooltip_MiracleDoorPhoton_00 }));

        // #tr item.MetaItem.32.name
        // # White Dwarf Mold (Ingot)
        // # zh_CN 白矮星模具(锭)
        GTNCItemList.MiracleDoorMold
            .set(MetaItemAdder.initItem(32, new String[] { TextLocalization.Tooltip_MiracleDoorMold_00 }));

        // #tr item.MetaItem.107.name
        // # 4A LV Wireless Energy Cover
        // # zh_CN 4安 LV无线能源覆盖板
        // #tr item.MetaItem.108.name
        // # 4A MV Wireless Energy Cover
        // # zh_CN 4安 MV无线能源覆盖板
        // #tr item.MetaItem.109.name
        // # 4A HV Wireless Energy Cover
        // # zh_CN 4安 HV无线能源覆盖板
        // #tr item.MetaItem.110.name
        // # 4A EV Wireless Energy Cover
        // # zh_CN 4安 EV无线能源覆盖板
        // #tr item.MetaItem.111.name
        // # 4A IV Wireless Energy Cover
        // # zh_CN 4安 IV无线能源覆盖板
        // #tr item.MetaItem.112.name
        // # 4A LuV Wireless Energy Cover
        // # zh_CN 4安 LuV无线能源覆盖板
        // #tr item.MetaItem.113.name
        // # 4A ZPM Wireless Energy Cover
        // # zh_CN 4安 ZPM无线能源覆盖板
        // #tr item.MetaItem.114.name
        // # 4A UV Wireless Energy Cover
        // # zh_CN 4安 UV无线能源覆盖板
        // #tr item.MetaItem.115.name
        // # 4A UHV Wireless Energy Cover
        // # zh_CN 4安 UHV无线能源覆盖板
        // #tr item.MetaItem.116.name
        // # 4A UEV Wireless Energy Cover
        // # zh_CN 4安 UEV无线能源覆盖板
        // #tr item.MetaItem.117.name
        // # 4A UIV Wireless Energy Cover
        // # zh_CN 4安 UIV无线能源覆盖板
        // #tr item.MetaItem.118.name
        // # 4A UMV Wireless Energy Cover
        // # zh_CN 4安 UMV无线能源覆盖板
        // #tr item.MetaItem.119.name
        // # 4A UXV Wireless Energy Cover
        // # zh_CN 4安 UXV无线能源覆盖板
        // #tr item.MetaItem.120.name
        // # 4A MAX Wireless Energy Cover
        // # zh_CN 4安 MAX无线能源覆盖板
        for (int i = 0; i < 14; i++) {
            GTNCItemList.WIRELESS_ENERGY_COVER[i].set(ItemList.WIRELESS_ENERGY_COVERS[i].get(1));

            GTNCItemList.WIRELESS_ENERGY_COVER_4A[i].set(
                MetaItemAdder.initItem(
                    107 + i,
                    new String[] { TextLocalization.Tooltip_WirelessEnergyCover4A_00,
                        TextLocalization.Tooltip_WirelessEnergyCover4A_01,
                        TextLocalization.Tooltip_WirelessEnergyCover4A_02,
                        TextLocalization.Tooltip_WirelessEnergyCover4A_03,
                        String.format(
                            TextLocalization.Tooltip_WirelessEnergyCover4A_04,
                            NumberFormatUtil.formatNumber(GTValues.V[i + 1]),
                            GTValues.VN[i + 1]) }));
        }
    }

    public static void registryOreBlackList() {
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.CircuitResonaticUIV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.BasicCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNCItemList.EliteCircuit.get(1));
    }

    public static void registry() {
        initItems();
        registryMetaItems();
        registryOreBlackList();
    }

}
