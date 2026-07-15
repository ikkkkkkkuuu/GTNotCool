package com.xyp.gtnc.Common.vending;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.cubefury.vendingmachine.trade.CurrencyItem;
import com.cubefury.vendingmachine.trade.CurrencyType;
import com.cubefury.vendingmachine.util.BigItemStack;

/**
 * 链式构造自动贩卖机(VendingMachine)单笔交易(Trade)的 NBT。
 *
 * <p>
 * 为什么直接拼 NBT：VendingMachine 0.4.87 的唯一注册入口是
 * {@code TradeDatabase.INSTANCE.readFromNBT(nbt, merge, isFileLoad)}，交易数据只能以 NBT 形式喂进去。
 * 这里复用 VM 自己的 {@link BigItemStack#writeToNBT}、{@link CurrencyItem#writeToNBT} 生成子片段，
 * 保证 NBT 结构与 VM 的 {@code Trade.readFromNBT} 期望完全一致（displayItem/fromCurrency/fromItems/toItems）。
 *
 * <p>
 * 一笔 Trade = 支付(fromCurrency 硬币 + fromItems 物品) → 产出(toItems)，外加一个展示图标(displayItem)。
 * displayItem 默认取第一个产出物。
 *
 * <p>
 * <b>类加载隔离</b>：本类引用了 VM 的类，只能在确认 vendingmachine 已加载后才触碰
 * （由 {@link VMTradeRegistry} 在 {@code Loader.isModLoaded} 之后调用）。
 */
public class VMTradeBuilder {

    private final BigItemStack display;
    private final List<NBTTagCompound> fromCurrency = new ArrayList<>();
    private final List<NBTTagCompound> fromItems = new ArrayList<>();
    private final List<NBTTagCompound> toItems = new ArrayList<>();

    private VMTradeBuilder(ItemStack displayStack) {
        this.display = new BigItemStack(displayStack);
    }

    /** 以展示图标(通常就是要卖的物品)开始构造一笔交易。 */
    public static VMTradeBuilder of(ItemStack displayStack) {
        return new VMTradeBuilder(displayStack);
    }

    /** 物品支付：购买时消耗这些物品。可多次调用累加。 */
    public VMTradeBuilder costItems(ItemStack... items) {
        for (ItemStack is : items) {
            if (is == null) continue;
            fromItems.add(new BigItemStack(is).writeToNBT(new NBTTagCompound()));
        }
        return this;
    }

    /** 硬币支付：如 {@code costCoin(CurrencyType.BEES, 500)}。value 为货币总面值。可多次调用叠加不同币种。 */
    public VMTradeBuilder costCoin(CurrencyType type, int value) {
        if (type != null && value > 0) {
            fromCurrency.add(new CurrencyItem(type, value).writeToNBT(new NBTTagCompound()));
        }
        return this;
    }

    /** 产出：购买后获得这些物品。可多次调用累加。 */
    public VMTradeBuilder give(ItemStack... items) {
        for (ItemStack is : items) {
            if (is == null) continue;
            toItems.add(new BigItemStack(is).writeToNBT(new NBTTagCompound()));
        }
        return this;
    }

    /** 组装成 VM 的 Trade NBT（供 TradeGroup 收集）。 */
    NBTTagCompound buildNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("displayItem", display.writeToNBT(new NBTTagCompound()));
        if (!fromCurrency.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (NBTTagCompound c : fromCurrency) list.appendTag(c);
            nbt.setTag("fromCurrency", list);
        }
        if (!fromItems.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (NBTTagCompound c : fromItems) list.appendTag(c);
            nbt.setTag("fromItems", list);
        }
        if (!toItems.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (NBTTagCompound c : toItems) list.appendTag(c);
            nbt.setTag("toItems", list);
        }
        return nbt;
    }
}
