package com.xyp.gtnc.utils.lang;

/**
 * 翻译注释汇总类（不含任何逻辑）。
 * <p>
 * 本项目 lang 由 gradle 任务 preprocessLangInJavaFiles 扫描源码 // #tr / // # / // # zh_CN 三行注释自动生成。
 * 历史上一批 key 只手写进 lang、没有注释，会被该任务重生成时误删。这里集中补齐这些 key 的翻译注释，使 lang 可无损重生成。
 * 这些 key 多经 IKey.lang / translateToLocal 动态引用，无精确字面量可挂注释，故统一集中于此。
 */
public final class LangEntries {

    private LangEntries() {}

    // #tr GT5U.gui.button.vm.refresh
    // # Refresh Ore List
    // # zh_CN 刷新矿石列表
    // #tr GTNC.gui.steam_type.normal
    // # Normal
    // # zh_CN 普通蒸汽
    // #tr GTNC.gui.steam_type.superheated
    // # Superheated
    // # zh_CN 过热蒸汽
    // #tr GTNC.gui.text.structure_error.legacy_check_failed
    // # Structure validation failed
    // # zh_CN 结构验证失败
    // #tr GTNC.gui.void_borer.dimension
    // # §eDimension:
    // # zh_CN §e维度：
    // #tr GTNC.gui.void_borer.mining
    // # §eLast Ore:
    // # zh_CN §e最后开采：
    // #tr GTNC.gui.void_borer.no_dimension
    // # §cNo Dimension
    // # zh_CN §c未选择维度
    // #tr GTNC.gui.void_borer.no_ores
    // # §cNo Ores Available
    // # zh_CN §c无可用矿石
    // #tr GTNC.gui.void_borer.no_steam
    // # §eNo Steam
    // # zh_CN §e缺少蒸汽
    // #tr GTNC.gui.void_borer.status
    // # §eStatus:
    // # zh_CN §e状态：
    // #tr GTNC.gui.void_borer.steam_cost
    // # §eSteam:
    // # zh_CN §e蒸汽：
    // #tr GTNC.gui.void_borer.steam_type
    // # §eSteam Type:
    // # zh_CN §e蒸汽类型：
    // #tr GTNC.gui.void_borer.work_cycle
    // # §eCycle:
    // # zh_CN §e周期：
    // #tr Info_QuantumComputer_00
    // # Multiblock size: %sx%sx%s
    // # zh_CN 结构大小：%sx%sx%s
    // #tr Info_QuantumComputer_01
    // # Co-processors: %s / Used: %s (%s)
    // # zh_CN 并行线程：%s / 已使用：%s (%s)
    // #tr Info_QuantumComputer_02
    // # Storage: %s / Used: %s (%s)
    // # zh_CN 可存储：%s / 已使用：%s (%s)
    // #tr Info_QuantumComputer_03
    // # Set Quantum Computer custom name
    // # zh_CN 设置量子计算机自定义名称
    // #tr NameVoidCrustSteamBorer
    // # Void Crust Steam Borer
    // # zh_CN 虚空地壳蒸汽钻探器
    // #tr Tooltip_GTNC_SteamInputHatch
    // # Steam Input Hatch
    // # zh_CN 蒸汽输入仓
    // #tr Tooltip_LargeSteamVoidMiner_Casing
    // # Machine casing
    // # zh_CN 机器外壳
    // #tr Tooltip_VoidCrustSteamBorer_00
    // # Uses Steam to mine ores from any dimension
    // # zh_CN 使用蒸汽从任意维度开采矿石
    // #tr Tooltip_VoidCrustSteamBorer_01
    // # Place a Dimension Display item in the controller slot to target a specific dimension
    // # zh_CN 在控制器槽位中放入维度显示物品以指定目标维度
    // #tr Tooltip_VoidCrustSteamBorer_02
    // # §cSteam Cost: §f%s L/s
    // # zh_CN §c蒸汽消耗：§f%s L/s
    // #tr Tooltip_VoidCrustSteamBorer_03
    // # §aSuperheated steam quadruples mining speed
    // # zh_CN §a过热蒸汽可四倍开采速度
    // #tr Tooltip_VoidCrustSteamBorer_Casing
    // # Any Casing
    // # zh_CN 任意机械方块
    // #tr gtsr.gui.building
    // # §eBuilding
    // # zh_CN §e搭建中
    // #tr gtsr.gui.none
    // # None
    // # zh_CN 无
    // #tr gtsr.gui.status.idle
    // # §7Idle
    // # zh_CN §7空闲
    // #tr gtsr.gui.status.running
    // # §bRunning
    // # zh_CN §b运行中
    // #tr gtsr.gui.tier
    // # Tier:
    // # zh_CN 等级：
    // #tr gtsr.gui.tier.steel
    // # Steel
    // # zh_CN 钢
    // #tr gui.wildcardpattern.actions
    // # Actions
    // # zh_CN 操作
    // #tr gui.wildcardpattern.add_material
    // # +Material
    // # zh_CN +材料
    // #tr gui.wildcardpattern.back
    // # Back
    // # zh_CN 返回
    // #tr gui.wildcardpattern.clear
    // # Clear All
    // # zh_CN 清空全部
    // #tr gui.wildcardpattern.clear_short
    // # Clear
    // # zh_CN 清空
    // #tr gui.wildcardpattern.col_input
    // # Input
    // # zh_CN 输入
    // #tr gui.wildcardpattern.col_mode
    // # Mode
    // # zh_CN 模式
    // #tr gui.wildcardpattern.col_output
    // # Output
    // # zh_CN 输出
    // #tr gui.wildcardpattern.dedupe
    // # Deduplicate
    // # zh_CN 去重
    // #tr gui.wildcardpattern.dedupe_hint
    // # Choose which mod's items to keep
    // # zh_CN 选择保留哪个模组的物品
    // #tr gui.wildcardpattern.dedupe_input
    // # Cycle Input
    // # zh_CN 切换输入
    // #tr gui.wildcardpattern.dedupe_output
    // # Cycle Output
    // # zh_CN 切换输出
    // #tr gui.wildcardpattern.dedupe_page
    // # Deduplication Selection
    // # zh_CN 去重选择
    // #tr gui.wildcardpattern.drag_hint
    // # Drag items from NEI or enter manually
    // # zh_CN 从NEI拖入物品或手动输入
    // #tr gui.wildcardpattern.exclude_current
    // # Current Exclusions:
    // # zh_CN 当前排除项:
    // #tr gui.wildcardpattern.exclude_empty
    // # (empty)
    // # zh_CN (空)
    // #tr gui.wildcardpattern.exclude_hint
    // # Edit exclusion list
    // # zh_CN 编辑排除列表
    // #tr gui.wildcardpattern.exclude_page
    // # Exclude Editor
    // # zh_CN 排除编辑器
    // #tr gui.wildcardpattern.exclude_short
    // # Exclude
    // # zh_CN 排除
    // #tr gui.wildcardpattern.exclude_tip1
    // # One exclusion per line, supports wildcard *
    // # zh_CN 每行一个排除项，支持*通配符
    // #tr gui.wildcardpattern.exclude_tip2
    // # Example: *Dust* excludes all dusts
    // # zh_CN 例如：*Dust* 排除所有粉
    // #tr gui.wildcardpattern.filter_short
    // # Filter
    // # zh_CN 筛选
    // #tr gui.wildcardpattern.global_exclude
    // # Global Exclude
    // # zh_CN 全局排除
    // #tr gui.wildcardpattern.include_short
    // # Include:
    // # zh_CN 包含:
    // #tr gui.wildcardpattern.mode_name
    // # Name
    // # zh_CN 名称
    // #tr gui.wildcardpattern.mode_oredict
    // # OreDict
    // # zh_CN 矿辞
    // #tr gui.wildcardpattern.multiply_short
    // # x2/x½
    // # zh_CN 翻倍/减半
    // #tr gui.wildcardpattern.page
    // # Page %d / %d
    // # zh_CN 第 %d 页 / 共 %d 页
    // #tr gui.wildcardpattern.preview_all
    // # Preview All
    // # zh_CN 全部预览
    // #tr gui.wildcardpattern.preview_empty
    // # (empty)
    // # zh_CN (空)
    // #tr gui.wildcardpattern.preview_hint
    // # Preview recipes that will be expanded
    // # zh_CN 预览将要展开的配方
    // #tr gui.wildcardpattern.preview_page
    // # Recipe Preview
    // # zh_CN 配方预览
    // #tr gui.wildcardpattern.preview_rule
    // # Rule %s Preview
    // # zh_CN 第 %s 行规则预览
    // #tr gui.wildcardpattern.preview_short
    // # Preview
    // # zh_CN 预览
    // #tr gui.wildcardpattern.rule_exclude
    // # Rule %d Exclude
    // # zh_CN 规则 %d 排除项
    // #tr gui.wildcardpattern.search
    // # Search:
    // # zh_CN 搜索:
    // #tr gui.wildcardpattern.tab_filter
    // # Filter
    // # zh_CN 过滤
    // #tr gui.wildcardpattern.tab_input
    // # Input
    // # zh_CN 输入
    // #tr gui.wildcardpattern.tab_main
    // # Preview
    // # zh_CN 预览
    // #tr gui.wildcardpattern.tab_output
    // # Output
    // # zh_CN 输出
    // #tr sciencenotcool.mebridge.receiver.title
    // # ME Bridge Receiver
    // # zh_CN ME 网桥 - 接收端
    // #tr sciencenotcool.mebridge.sender.channel
    // # Channel Name:
    // # zh_CN 频道名：
    // #tr sciencenotcool.mebridge.sender.save
    // # Broadcast
    // # zh_CN 广播频道
    // #tr sciencenotcool.mebridge.sender.title
    // # ME Bridge Sender
    // # zh_CN ME 网桥 - 发起端
    // #tr sciencenotcool.tooltip.key_unbound
    // # §8Unbound
    // # zh_CN §8未绑定
    // #tr tile.mebridge.receiver.tooltip.0
    // # §7Connects to a channel broadcast by a Sender.
    // # zh_CN §7连接到发起端广播的频道。
    // #tr tile.mebridge.receiver.tooltip.1
    // # §7Becomes part of the Sender's ME network across dimensions.
    // # zh_CN §7跨维度并入发起端所在的 ME 网络。
    // #tr tile.mebridge.receiver.tooltip.2
    // # §7Right-click to type or pick a channel.
    // # zh_CN §7右键输入或从列表选择频道。
    // #tr tile.mebridge.receiver.tooltip.3
    // # §eKeep both ends' chunks loaded for a stable link.
    // # zh_CN §e两端区块需常驻加载以保持连接稳定。
}
