package com.xyp.gtnc.Common.vending;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.cubefury.vendingmachine.trade.TradeCategory;
import com.cubefury.vendingmachine.trade.TradeDatabase;
import com.cubefury.vendingmachine.util.NBTConverter;

import cpw.mods.fml.common.Loader;

/**
 * 把本 mod 定义的「无条件交易」动态注入自动贩卖机(VendingMachine)。
 *
 * <p>
 * <b>注入入口</b>：VendingMachine 0.4.87 唯一的注册路径是
 * {@code TradeDatabase.INSTANCE.readFromNBT(nbt, merge, isFileLoad)}。这里用：
 * <ul>
 * <li>{@code merge=true} —— 不清空 VM 已有交易（与整合包自带交易共存）；</li>
 * <li>{@code isFileLoad=false} —— 不触发 {@code MarkDirtyDbEvent}，因此<b>不回写 tradeDatabase.json、
 * 不污染存档、不重复堆积</b>（纯内存注入，每次开服重新注入一次）。</li>
 * </ul>
 *
 * <p>
 * <b>无条件</b>：每个 TradeGroup 的 {@code requirements} 写空列表 → VM 的 {@code hasNoConditions()} 为 true
 * → 进 {@code noConditionTrades} → {@code getAvailableTradeGroups} 无条件对所有玩家可见（无需任务解锁）。
 *
 * <p>
 * <b>分标签页</b>：TradeGroup 的 {@code category} 写 {@link TradeCategory#getKey()}，落到对应原版标签页。
 *
 * <p>
 * <b>稳定 UUID</b>：用 {@link UUID#nameUUIDFromBytes} 基于组的 key 生成确定性 ID，避免每次开服 merge 时
 * 因 ID 变化而重复堆积同一组交易。
 *
 * <p>
 * <b>客户端显示</b>：玩家登录时 VM 走 {@code NetBulkSync.sendReset → sendDatabase} 把整个
 * {@code TradeDatabase.INSTANCE} 全量同步给客户端，故注入后自动可见。
 *
 * <p>
 * <b>mod 缺失保护</b>：所有触碰 VM 类的操作都在 {@code Loader.isModLoaded("vendingmachine")} 之后，
 * 未装 VM 时 {@link #injectAll()} 直接返回，不会 NoClassDefFound。
 */
public final class VMTradeRegistry {

    public static final String VM_MODID = "vendingmachine";

    private VMTradeRegistry() {}

    /** 一个待注入的交易组：分类 + 冷却 + 次数上限 + 若干笔交易。 */
    private static final class PendingGroup {

        final String key;
        final TradeCategory category;
        final int cooldown;
        final int maxTrades;
        final List<VMTradeBuilder> trades;

        PendingGroup(String key, TradeCategory category, int cooldown, int maxTrades, List<VMTradeBuilder> trades) {
            this.key = key;
            this.category = category;
            this.cooldown = cooldown;
            this.maxTrades = maxTrades;
            this.trades = trades;
        }
    }

    private static final List<PendingGroup> PENDING = new ArrayList<>();

    /**
     * 登记一个交易组（不立即注入，攒到 {@link #injectAll()} 一起注入）。
     *
     * @param key       本组的稳定标识（用于生成确定性 UUID，全局唯一即可，如 "bees_starter"）
     * @param category  落到哪个标签页
     * @param cooldown  冷却秒数，-1 = 无冷却
     * @param maxTrades 可交易次数上限，-1 = 无限
     * @param trades    本组内的若干笔交易
     */
    public static void group(String key, TradeCategory category, int cooldown, int maxTrades,
        VMTradeBuilder... trades) {
        List<VMTradeBuilder> list = new ArrayList<>();
        for (VMTradeBuilder t : trades) {
            if (t != null) list.add(t);
        }
        if (list.isEmpty()) return;
        PENDING.add(new PendingGroup(key, category, cooldown, maxTrades, list));
    }

    /**
     * 把登记的所有交易组注入 VM。在 {@code FMLServerStartedEvent}（晚于 VM 在 serverStarting 里的 loadDatabase）时调用。
     * VM 未加载则直接返回。
     */
    public static void injectAll() {
        if (!Loader.isModLoaded(VM_MODID)) {
            return;
        }
        if (PENDING.isEmpty()) {
            GTNCVendingTrades.register();
        }
        if (PENDING.isEmpty()) {
            return;
        }
        inject();
    }

    /** 真正触碰 VM 类的部分，单独成方法，确保 VM 缺失时上面的 gate 先拦住不加载本方法引用的类。 */
    private static void inject() {
        NBTTagList groupList = new NBTTagList();
        for (PendingGroup g : PENDING) {
            groupList.appendTag(buildGroupNBT(g));
        }
        NBTTagCompound root = new NBTTagCompound();
        // version=-1：沿用 VM 空库的默认版本号，merge 时不影响已有交易的版本。
        root.setInteger("version", -1);
        root.setTag("tradeGroups", groupList);

        TradeDatabase.INSTANCE.readFromNBT(root, true, false);
    }

    /** 按 VM 的 TradeGroup.readFromNBT 期望结构拼一个交易组 NBT。 */
    private static NBTTagCompound buildGroupNBT(PendingGroup g) {
        NBTTagCompound nbt = new NBTTagCompound();
        UUID id = UUID
            .nameUUIDFromBytes(("gtnc:tradegroup:" + g.key).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        nbt.setTag("id", NBTConverter.UuidValueType.TRADEGROUP.writeId(id));
        nbt.setInteger("cooldown", g.cooldown);
        nbt.setInteger("maxTrades", g.maxTrades);
        nbt.setString("category", g.category.getKey());

        NBTTagList tradeList = new NBTTagList();
        for (VMTradeBuilder t : g.trades) {
            tradeList.appendTag(t.buildNBT());
        }
        nbt.setTag("trades", tradeList);

        // 空 requirements → hasNoConditions() = true → 无条件交易。
        nbt.setTag("requirements", new NBTTagList());
        return nbt;
    }
}
