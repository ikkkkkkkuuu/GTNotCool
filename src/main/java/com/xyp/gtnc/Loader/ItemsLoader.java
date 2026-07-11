package com.xyp.gtnc.Loader;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.xyp.gtnc.Client.GTNCCreativeTabs;
import com.xyp.gtnc.Common.items.MetaItemAdder;
import com.xyp.gtnc.Common.items.tools.VeinMiningPickaxe;
import com.xyp.gtnc.Common.items.wildcard.WildcardPatternItem;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;

/**
 * 物品加载器，负责注册所有物品
 */
public class ItemsLoader {

    public static VeinMiningPickaxe veinMiningPickaxe;

    // 通配样板符
    public static Item wildcardPattern;

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

        // #tr item.MetaItem.30.name
        // # Bioware SMD Inductor
        // # zh_CN 生物贴片电感
        GTNCItemList.BiowareSMDInductor.set(MetaItemAdder.initItem(30));

        // #tr item.MetaItem.31.name
        // # Critical Photon
        // # zh_CN 临界光子
        GTNCItemList.MiracleDoorPhoton.set(
            // #tr Tooltip_MiracleDoorPhoton_00
            // # Fuel for the Miracle Door. Each run consumes Overclock Parameter of it.
            // # zh_CN §d奇迹之门的燃料. 每次运行消耗超频系数颗.
            MetaItemAdder
                .initItem(31, new String[] { StatCollector.translateToLocal("Tooltip_MiracleDoorPhoton_00") }));

        // #tr item.MetaItem.32.name
        // # White Dwarf Mold (Ingot)
        // # zh_CN 白矮星模具(锭)
        GTNCItemList.MiracleDoorMold.set(
            // #tr Tooltip_MiracleDoorMold_00
            // # Put into input bus of the Miracle Door to cast molten metal outputs into ingots.
            // # zh_CN §d放入奇迹之门的输入总线, 将熔融金属产出熔铸为锭.
            MetaItemAdder.initItem(32, new String[] { StatCollector.translateToLocal("Tooltip_MiracleDoorMold_00") }));

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
                // #tr Tooltip_WirelessEnergyCover4A_00
                // # Stores energy in the global network, up to 2^(2^31)EU.
                // # zh_CN §7将能量存储于全局网络中，上限为2^(2^31)EU.
                // #tr Tooltip_WirelessEnergyCover4A_01
                // # Without connecting wires, this cover can draw EU from the network.
                // # zh_CN §7不连接导线，此覆盖板可以从网络中抽取EU.
                // #tr Tooltip_WirelessEnergyCover4A_02
                // # Ignores voltage limits (won't cause explosions).
                // # zh_CN 无视电压限制 (不会造成爆炸).
                // #tr Tooltip_WirelessEnergyCover4A_03
                // # Amperage: §e4A§7
                // # zh_CN 电流：§e4A§7
                // #tr Tooltip_WirelessEnergyCover4A_04
                // # Input Voltage: §a%s (§7%s§r§a)
                // # zh_CN 输入电压：§a%s (§7%s§r§a)
                MetaItemAdder.initItem(
                    107 + i,
                    new String[] { StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_00"),
                        StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_01"),
                        StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_02"),
                        StatCollector.translateToLocal("Tooltip_WirelessEnergyCover4A_03"),
                        StatCollector.translateToLocalFormatted(
                            "Tooltip_WirelessEnergyCover4A_04",
                            NumberFormatUtil.formatNumber(GTValues.V[i + 1]),
                            GTValues.VN[i + 1]) }));
        }
    }

    public static void registry() {
        initItems();
        registryMetaItems();

    }

}
