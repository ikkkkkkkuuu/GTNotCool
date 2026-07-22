package com.xyp.gtnc.Common.vending;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.cubefury.vendingmachine.trade.CurrencyType;
import com.cubefury.vendingmachine.trade.TradeCategory;
import com.xyp.gtnc.utils.enums.GTNCItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

/**
 * 本 mod 向自动贩卖机(VendingMachine)注册的「无条件交易」具体内容。
 *
 * <p>
 * 由 {@link VMTradeRegistry#injectAll()} 在开服时调用 {@link #register()}，把下面登记的交易组注入 VM。
 * 全部是<b>无条件交易</b>（无需任务解锁，所有玩家永久可见），按 {@link TradeCategory} 落到原版各标签页。
 *
 * <p>
 * <b>怎么加你自己的交易</b>：照下面示例往对应标签页加 {@code VMTradeRegistry.group(...)} 即可。
 * <ul>
 * <li>物品支付：{@code VMTradeBuilder.of(展示物).costItems(消耗物...).give(产出物...)}</li>
 * <li>硬币支付：{@code VMTradeBuilder.of(展示物).costCoin(CurrencyType.XXX, 面值).give(产出物...)}</li>
 * <li>两者混合：{@code .costItems(...).costCoin(...)} 都写上</li>
 * </ul>
 * 硬币来自 dreamcraft（{@code dreamcraft:CoinXXX}），仅在 dreamcraft 加载时可用（本地精简环境无 dreamcraft，
 * 硬币交易只能在完整整合包里验证）。
 *
 * <p>
 * 物品/方块/蜜蜂都在 {@link #register()}（开服 serverStarted）时动态取，此时所有 mod 已注册完毕，
 * 取不到（对应 mod 缺失）时跳过该笔交易，不影响其余交易与开服。
 */
public final class GTNCVendingTrades {

    private static boolean registered = false;

    private GTNCVendingTrades() {}

    /** 冷却：30 分钟 = 1800 秒（VM 的 cooldown 单位是秒）。 */
    private static final int COOLDOWN_30MIN = 1800;

    /** 冷却：15 分钟 = 900 秒。 */
    private static final int COOLDOWN_15MIN = 900;

    /** 冷却：10 分钟 = 600 秒。 */
    private static final int COOLDOWN_10MIN = 600;

    /** 冷却：5 分钟 = 300 秒。 */
    private static final int COOLDOWN_5MIN = 300;

    /** 登记所有交易组。幂等：只跑一次。由 {@link VMTradeRegistry#injectAll()} 调用。 */
    public static void register() {
        if (registered) return;
        registered = true;

        // ── 零部件页（COMPONENTS）──────────────────────────────
        // 10 技术员币 → 1 脱氧钢机械方块（gregtech:gt.blockcasings2 meta 0），无冷却
        ItemStack deoxidizedCasing = gtBlock("gt.blockcasings2", 0);
        if (deoxidizedCasing != null) {
            VMTradeRegistry.group(
                "components_deoxidized_casing",
                TradeCategory.COMPONENTS,
                -1,
                -1,
                VMTradeBuilder.of(deoxidizedCasing.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 10)
                    .give(deoxidizedCasing.copy()));
        }

        // 10 技术员币 → 1 ae2fc me二合一接口 无冷却
        ItemStack dualInterface = GTModHandler.getModItem("ae2fc", "fluid_interface", 1);
        if (dualInterface != null) {
            VMTradeRegistry.group(
                "components_ae2fc_fluid_interface",
                TradeCategory.COMPONENTS,
                -1,
                -1,
                VMTradeBuilder.of(dualInterface.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 10)
                    .give(dualInterface.copy()));
        }

        // 10 技术员币 → 1 me终端 无冷却
        ItemStack meTerminal = GTModHandler.getModItem("appliedenergistics2", "item.ItemMultiPart", 1, 380);
        if (meTerminal != null) {
            VMTradeRegistry.group(
                "components_ae_me_terminal",
                TradeCategory.COMPONENTS,
                -1,
                -1,
                VMTradeBuilder.of(meTerminal.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 10)
                    .give(meTerminal.copy()));
        }

        // 64 技术员币 → 64 空白样板（AE2 blankPattern），冷却 15 分钟
        ItemStack blankPattern = aeBlankPattern(64);
        if (blankPattern != null) {
            VMTradeRegistry.group(
                "components_ae_blank_pattern",
                TradeCategory.COMPONENTS,
                COOLDOWN_15MIN,
                -1,
                VMTradeBuilder.of(blankPattern.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 64)
                    .give(blankPattern.copy()));
        }

        // 30 技术员币 → 1 ME 网桥发送端（本 mod 方块），冷却 10 分钟
        ItemStack bridgeSender = new ItemStack(com.xyp.gtnc.Loader.BlockLoader.blockMEBridgeSender, 1);
        if (bridgeSender != null) {
            VMTradeRegistry.group(
                "components_me_bridge_sender",
                TradeCategory.COMPONENTS,
                COOLDOWN_10MIN,
                -1,
                VMTradeBuilder.of(bridgeSender.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 30)
                    .give(bridgeSender.copy()));
        }

        // 30 技术员币 → 1 ME 网桥接收端（本 mod 方块），冷却 10 分钟
        ItemStack bridgeReceiver = new ItemStack(com.xyp.gtnc.Loader.BlockLoader.blockMEBridgeReceiver, 1);
        if (bridgeReceiver != null) {
            VMTradeRegistry.group(
                "components_me_bridge_receiver",
                TradeCategory.COMPONENTS,
                COOLDOWN_10MIN,
                -1,
                VMTradeBuilder.of(bridgeReceiver.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 30)
                    .give(bridgeReceiver.copy()));
        }

        // 100 技术员币 → 1 大型矿物处理机主控制器（本 mod 机器），maxTrades=1 只能换一次
        ItemStack oreProcessor = GTNCItemList.LargeOreProcessor.get(1);
        if (oreProcessor != null) {
            VMTradeRegistry.group(
                "components_large_ore_processor",
                TradeCategory.COMPONENTS,
                -1,
                1,
                VMTradeBuilder.of(oreProcessor.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 100)
                    .give(oreProcessor.copy()));
        }

        // 10 技术员币 → 2 大型矿物处理机外壳方块（MineralprocessingFrame = metaCasing02 meta 4），冷却 10 分钟
        ItemStack oreProcessorFrame = GTNCItemList.MineralprocessingFrame.get(2);
        if (oreProcessorFrame != null) {
            VMTradeRegistry.group(
                "components_ore_processor_frame",
                TradeCategory.COMPONENTS,
                COOLDOWN_10MIN,
                -1,
                VMTradeBuilder.of(oreProcessorFrame.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 10)
                    .give(oreProcessorFrame.copy()));
        }

        // 50 技术员币 → 1 保险库主控制器（SingularityDataHub），限 10 次
        ItemStack vault = GTNCItemList.SingularityDataHub.get(1);
        if (vault != null) {
            VMTradeRegistry.group(
                "components_vault_controller",
                TradeCategory.COMPONENTS,
                -1,
                10,
                VMTradeBuilder.of(vault.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 50)
                    .give(vault.copy()));
        }

        // 30 技术员币 → 1 保险库接口（VaultPortHatch），限 10 次
        ItemStack vaultPort = GTNCItemList.VaultPortHatch.get(1);
        if (vaultPort != null) {
            VMTradeRegistry.group(
                "components_vault_port",
                TradeCategory.COMPONENTS,
                -1,
                10,
                VMTradeBuilder.of(vaultPort.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 30)
                    .give(vaultPort.copy()));
        }

        // 20 技术员币 → 1 超级样板输入总成（本 mod GT 仓室 MTE），无冷却
        ItemStack superCraftingInput = GTNCItemList.SuperMTEHatchCraftingInputME.get(1);
        if (superCraftingInput != null) {
            VMTradeRegistry.group(
                "components_super_crafting_input",
                TradeCategory.COMPONENTS,
                -1,
                -1,
                VMTradeBuilder.of(superCraftingInput.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 20)
                    .give(superCraftingInput.copy()));
        }

        // 20 技术员币 → 1 超级样板输入镜像，无冷却
        ItemStack superCraftingInputMirror = GTNCItemList.SuperMTEHatchCraftingInputSlave.get(1);
        if (superCraftingInputMirror != null) {
            VMTradeRegistry.group(
                "components_super_crafting_input_mirror",
                TradeCategory.COMPONENTS,
                -1,
                -1,
                VMTradeBuilder.of(superCraftingInputMirror.copy())
                    .costCoin(CurrencyType.TECHNICIAN, 20)
                    .give(superCraftingInputMirror.copy()));
        }

        // ── 蜜蜂页（BEES）──────────────────────────────────
        // 30 养蜂员币 → 1 勇者雄蜂，冷却 30 分钟
        ItemStack valiantDrone = vanillaDrone("forestry.speciesValiant");
        if (valiantDrone != null) {
            VMTradeRegistry.group(
                "bees_valiant_drone",
                TradeCategory.BEES,
                COOLDOWN_30MIN,
                -1,
                VMTradeBuilder.of(valiantDrone.copy())
                    .costCoin(CurrencyType.BEES, 30)
                    .give(valiantDrone.copy()));
        }

        // 30 养蜂员币 → 1 坚定雄蜂，冷却 30 分钟
        ItemStack steadfastDrone = vanillaDrone("forestry.speciesSteadfast");
        if (steadfastDrone != null) {
            VMTradeRegistry.group(
                "bees_steadfast_drone",
                TradeCategory.BEES,
                COOLDOWN_30MIN,
                -1,
                VMTradeBuilder.of(steadfastDrone.copy())
                    .costCoin(CurrencyType.BEES, 30)
                    .give(steadfastDrone.copy()));
        }

        // 30 养蜂员币 → 1 僧侣雄蜂（forestry.speciesMonastic），冷却 30 分钟
        ItemStack monasticDrone = vanillaDrone("forestry.speciesMonastic");
        if (monasticDrone != null) {
            VMTradeRegistry.group(
                "bees_monastic_drone",
                TradeCategory.BEES,
                COOLDOWN_30MIN,
                -1,
                VMTradeBuilder.of(monasticDrone.copy())
                    .costCoin(CurrencyType.BEES, 30)
                    .give(monasticDrone.copy()));
        }

        // 30 养蜂员币 → 1 末影雄蜂（Forestry END 分支 ENDED，UID 用 enum 名 → forestry.speciesEnded），冷却 30 分钟
        ItemStack endedDrone = vanillaDrone("forestry.speciesEnded");
        if (endedDrone != null) {
            VMTradeRegistry.group(
                "bees_ended_drone",
                TradeCategory.BEES,
                COOLDOWN_30MIN,
                -1,
                VMTradeBuilder.of(endedDrone.copy())
                    .costCoin(CurrencyType.BEES, 30)
                    .give(endedDrone.copy()));
        }

        // ── 化工页（CHEMISTRY）──────────────────────────────
        // 10 化工币 → 64 橡胶条（GT ingot + Rubber），冷却 5 分钟
        ItemStack rubberIngots = gtMaterial(OrePrefixes.ingot, Materials.Rubber, 64);
        if (rubberIngots != null) {
            VMTradeRegistry.group(
                "chemistry_rubber_ingot",
                TradeCategory.CHEMISTRY,
                COOLDOWN_5MIN,
                -1,
                VMTradeBuilder.of(rubberIngots.copy())
                    .costCoin(CurrencyType.CHEMIST, 10)
                    .give(rubberIngots.copy()));
        }
    }

    /** 按 GT 前缀+材质取物品的 ItemStack；取不到返回 null（跳过该交易）。 */
    private static ItemStack gtMaterial(OrePrefixes prefix, Materials material, int amount) {
        return GTOreDictUnificator.get(prefix, material, amount);
    }

    /** 取 AE2 空白样板的 ItemStack；AE2 缺失或取不到返回 null（跳过该交易）。 */
    private static ItemStack aeBlankPattern(int amount) {
        com.google.common.base.Optional<ItemStack> stack = appeng.api.AEApi.instance()
            .definitions()
            .materials()
            .blankPattern()
            .maybeStack(amount);
        return stack.isPresent() ? stack.get() : null;
    }

    /**
     * 创建「原版模板雄蜂」ItemStack：物种基因取自 Forestry 原版模板，<b>不经过</b>本 mod 杂交机的
     * {@code applyMaxGenome}（速度无尽/寿命不死/生育固定那套满基因）。因此卖出的是普通雄蜂——
     * 其 NBT 是原版基因，只会被「读时拉满环境」的 mixin（{@code MixinBeeGenomeEnvironment} 等）影响，
     * 与杂交机产出的满基因 NBT 蜂不同。root/物种/模板缺失时返回 null（跳过该交易）。
     */
    private static ItemStack vanillaDrone(String speciesUID) {
        forestry.api.apiculture.IBeeRoot root = forestry.api.apiculture.BeeManager.beeRoot;
        if (root == null) return null;
        forestry.api.genetics.IAllele[] template = root.getTemplate(speciesUID);
        if (template == null) return null;
        forestry.api.apiculture.IBeeGenome genome = root.templateAsGenome(template);
        forestry.api.apiculture.IBee bee = root.getBee(null, genome);
        if (bee == null) return null;
        return root.getMemberStack(bee, forestry.api.apiculture.EnumBeeType.DRONE.ordinal());
    }

    /** 按注册名取 GT 方块的 ItemStack；方块缺失返回 null（跳过该交易）。 */
    private static ItemStack gtBlock(String name, int meta) {
        Block block = GameRegistry.findBlock("gregtech", name);
        if (block == null) return null;
        return new ItemStack(block, 6, meta);
    }
}
