# AE2Thing 二合一接口终端移植 — 维护清单

> 本文档记录「ME 无线二合一接口终端」从 AE2Things mod 移植并入本 mod（ScienceNotCool）后，
> 对 **Applied Energistics 2 (Unofficial)** 和 **AE2FluidCraft-Rework** 的依赖关系。
> 当 GTNH 升级 AE2 / AE2FC 版本时，用本文档快速判断「要不要改、改哪里」。

生成时间: 2026-07-02（基于 GTNH 2.8.x，AE2 rv3-beta-977-GTNH，AE2FC 1.5.88-gtnh）

---

## 0. TL;DR — 拿到新版 diff 后怎么做

给定新的 `gtnh-upstream-tracker/diffs/<旧>-to-<新>/` 目录：

1. 看 `VERSION-DIFF.md`，确认 `Applied-Energistics-2-Unofficial` 和 `AE2FluidCraft-Rework` 版本变了没。没变 → 大概率无需改动。
2. 在这两个 patch 里 grep 第 2、3、4 节列出的**符号清单**。命中 = 潜在影响点。
3. 对命中的：
   - **签名/字段名变了** → 编译期会报错，按第 6 节流程改。
   - **只是内部逻辑变了** → 编译不报错，靠第 5 节的运行时验证抓。
4. `./gradlew compileJava` → `build` → `runClient` 实机验证（第 5 节）。

代码位置：`src/main/java/com/xyp/gtnc/ae2thing/`（220 个文件）。
访问转换器：`src/main/resources/META-INF/ae2thing_at.cfg`（在 `gradle.properties` 的 `accessTransformersFile` 注册）。
Mixin：由 `coremod/AE2LatePlugin.java`（`@LateMixin`，被 GTNHMixins 自动发现）加载 `mixins.ae2thing.late.json`，具体清单在 `coremod/Mixins.java`。

---

## 1. 依赖的外部 mod（只有这两个会影响本移植）

| Mod | 用途 | patch 路径 |
|---|---|---|
| Applied-Energistics-2-Unofficial | 终端 GUI/容器基类、ME 存储 API、样板、无线终端 | `mods/Applied-Energistics-2-Unofficial/full-java.patch` |
| AE2FluidCraft-Rework (glodblock) | 流体液滴(ItemFluidDrop)、流体样板、双接口显示 | `mods/AE2FluidCraft-Rework/full-java.patch` |

其它 mod（Thaumcraft/Botania/GT5/NEI 等）的变动**不影响**本移植 —— 相关集成代码在移植时已删除，只留了被 `isModLoaded()` 守卫的空壳。

---

## 2. 【最脆】Mixin 注入点 —— 运行时崩溃风险

这些 mixin 用 `@Inject`/`@Shadow`/`@Redirect` 钩 AE2 具体方法。**如果 AE2 重命名/删除/重构了被钩的方法，编译不报错，但运行时崩**。升级后必须 grep patch 确认这些方法还在、签名没变。

Mixin 配置：`coremod/Mixins.java`（enum，只启用了 AE_CLIENT / AE_SERVER / NEI 三组）。

### AE 客户端/通用 mixin（`coremod/mixin/ae/`）

| Mixin | 目标类 (appeng) | 钩的方法/字段 | 说明 |
|---|---|---|---|
| **MixinAEBaseGui** ⚠️最关键 | `client.gui.AEBaseGui` | `@Inject handleMouseClick`, `onGuiClosed`, `handleMouseInput`；`@Shadow getScrollBar` | 终端 GUI 的鼠标点击/滚动/关闭。**没它终端 GUI 交互失灵**。 |
| AccessorGuiScrollbar | `client.gui.widgets.GuiScrollbar` | `@Accessor isLatestClickOnScrollbar` | 滚动条状态 |
| MixinItemRepo | `client.me.ItemRepo` | `@Inject updateView`, `addEntriesToView`；`@Shadow view/list` | ME 物品列表视图过滤 |
| MixinContainerCraftAmount | `container.implementations.ContainerCraftAmount` | `onUpdate` | |
| MixinContainerCraftConfirm | `container.implementations.ContainerCraftConfirm` | `setItemToCraft`, `startJob()V`；多个 `@Shadow` | 合成确认 |
| MixinGuiCraftAmount | `client.gui.implementations.GuiCraftAmount` | (无 @Inject，仅结构) | |
| MixinGuiCraftConfirm | `client.gui.implementations.GuiCraftConfirm` | `actionPerformed`, `initGui`, `drawFG`；多个 `@Shadow` | |
| MixinCraftingCPUCluster | `me.cluster.implementations.CraftingCPUCluster` | `submitJob`, `handleCraftBranchFailure`, `completeJob` | 合成状态预览 |
| MixinTileIOPort | `tile.storage.TileIOPort` | `transferContents` | |

### NEI mixin（`coremod/mixin/nei/`）—— 依赖 NotEnoughItems

MixinGuiContainerManager, MixinGuiOverlayButton, MixinIOverlayHandler, MixinPanelWidget, MixinRecipeItemInputHandler。
NEI 大版本变动时检查（NEI 在 2.8→2.9 从 1.7.14→1.7.30，patch 在 `mods/NotEnoughItems/`）。

> **升级检查命令示例：**
> ```
> grep -nE "handleMouseClick|onGuiClosed|handleMouseInput|getScrollBar" <AE2patch>
> grep -nE "updateView|addEntriesToView" <AE2patch>
> ```
> 若这些方法出现在 patch 的 `-`（删除）行，且没有等价 `+`（新增），mixin 需要改注入点。

---

## 3. 【脆】访问转换器 (AT) —— 编译期依赖

`src/main/resources/META-INF/ae2thing_at.cfg` 把原版私有成员放开为 public。若 GTNH/原版重构了这些成员（罕见，多为 MCP 映射稳定的 vanilla 字段），编译会报错。

| SRG 名 | 含义 | 属于 |
|---|---|---|
| `field_147006_u` | `theSlot` | GuiContainer（vanilla） |
| `func_146975_c` | `getSlotAtPosition(II)` | GuiContainer（vanilla） |
| `field_147003_i` / `field_147009_r` | `guiLeft` / `guiTop` | GuiContainer（vanilla） |
| `field_146284_a` | `actionPerformed` | GuiScreen（vanilla） |
| `func_146976_a` | `drawGuiContainerBackgroundLayer` | GuiContainer（vanilla） |
| `field_70465_c` / `field_70464_b` | InventoryCrafting eventHandler/width | vanilla |
| `field_110574_e` | `mapRegisteredSprites` | TextureMap（vanilla） |

> 这些都是 **vanilla MC 字段**（不是 AE2 的），MCP 映射极稳定，几乎不会因 GTNH 升级而变。低风险。

---

## 4. 【脆】反射访问的 AE2/AE2FC 私有成员 —— 运行时风险

`util/Ae2ReflectClient.java` 和 `util/Ae2Reflect.java` 用反射读 AE2 私有字段。字段**改名 → 运行时抛异常**（编译不报错）。升级后 grep patch 里这些字段名。

| 类 | 字段/方法 | 文件 |
|---|---|---|
| `AEBaseGui` | `draggedSlots`（或旧名 `drag_click`）, `getInventorySlots()` | Ae2ReflectClient |
| `client.me.ItemRepo` | `view`, `list` | Ae2ReflectClient |
| `GuiCraftingStatus` | `originalGuiBtn` | Ae2ReflectClient |
| `me.Grid` | `myStorage` | Ae2Reflect |
| `container.implementations.ContainerInterfaceTerminal` | `tracked` | Ae2Reflect |
| `container.implementations.ContainerCraftConfirm` | `result` | Ae2Reflect |
| `me.MEInventoryHandler` | `myPartitionList` | Ae2Reflect |
| `me.storage.MEPassThrough` | `internal` | Ae2Reflect |
| `crafting.v2.CraftingJobV2` | `callback` | Ae2Reflect |
| AE2FC `GuiFluidInterface` | `cont` | Ae2ReflectClient |

---

## 5. 【中】继承 / API 依赖 —— 编译期，最好抓

终端直接继承 AE2 基类。基类的方法签名/构造器变了，编译会报错（好事，能抓到）。

**GUI 继承链：**
`GuiWirelessDualInterfaceTerminal` → `GuiBaseInterfaceWireless` → `BaseMEGui` → `appeng.client.gui.AEBaseGui`

**容器继承链：**
`ContainerWirelessDualInterfaceTerminal` → `ContainerMonitor` → `appeng.container.implementations.ContainerMEMonitorable` → `AEBaseContainer`

**物品：** `ItemWirelessDualInterfaceTerminal` → `ItemBaseWirelessTerminal` → `appeng.items.tools.powered.ToolWirelessTerminal`

**高频/非原版 AE2 API 符号**（这些若在 AE2 patch 里改了签名，我们要跟着改）：
- `appeng.client.gui.slots.VirtualMEMonitorableSlot` / `VirtualMESlot`（虚拟 ME 槽）
- `appeng.api.parts.IInterfaceTerminal` + `ContainerInterfaceTerminal`（接口列表委托）
- `appeng.core.sync.packets.PacketInterfaceTerminalUpdate`（接口列表更新包）
- `appeng.util.MonitorableTypeFilter` + `appeng.api.storage.ITerminalTypeFilterProvider` + `IAEStackType`（类型过滤）
- `appeng.api.storage.data.{IAEItemStack,IAEFluidStack,IAEStack,IItemList}`
- `appeng.tile.inventory.AppEngInternalInventory`, `appeng.container.slot.{SlotFake,SlotFakeCraftingMatrix,SlotRestrictedInput}`

**运行时验证（无法靠读 diff 保证，必须跑）：**
1. `./gradlew runClient`，确认加载日志里 `Mixing ae.MixinAEBaseGui ... into appeng.client.gui.AEBaseGui` 等**全部注入成功**、无 mixin apply 报错。
2. 进游戏创造栏拿终端，右键开 GUI（验证 MixinAEBaseGui + AT）。
3. 测：ME 面板显示/搜索、接口列表渲染、样板编码、crafting/processing 切换、类型过滤、多页样板、NEI 配方拖拽入终端。

---

## 6. AE2FC (glodblock) 依赖符号

| 符号 | 用途 | 风险 |
|---|---|---|
| `common.item.ItemFluidDrop` | 流体以「液滴物品」形式进样板 | 高频(20处)，核心 |
| `util.Util`（glodblock 的） | 流体填充/提取工具 | 中 |
| `util.Ae2Reflect`（glodblock 的） | 反射工具（我们复用） | 中 |
| `common.item.ItemFluidPacket` / `ItemFluidEncodedPattern` | 流体包/编码样板 | 中 |
| `util.FluidPatternDetails` | 流体样板细节 | 中 |
| `loader.ItemAndBlockHolder` | 引用 AE2FC 的物品(WIRELESS_INTERFACE_TERM 等，做合成配方) | 低 |
| `client.gui.GuiFluidInterface` / `GuiFCImgButton` | 双接口显示微调（ASM，非必需） | 低 |

---

## 7. 已知的刻意取舍（不是 bug，别"修复"）

- **ASM 转换器未接线**：`coremod/AE2ThingCore`（IFMLLoadingPlugin）+ `coremod/transform/*`（GuiDualInterface/Platform/FluidConvertingInventoryAdaptor/CraftingJobV2 转换器）**没有注册**。它们改的是 AE2FC 自己的 GuiFluidInterface（装饰性显示名），不是本终端。本 mod 只有一个 `coreModClass` 槽位，留空。如果将来想要那个装饰效果，需把 AE2ThingCore 注册为独立 FMLCorePlugin。
- **被 `isModLoaded()` 守卫的空分支**：Thaumcraft/HBM/WCT/背包等集成代码大多已删或留空壳，因为对应 mod 变体没装或功能不需要。升级时这些不用管。
- **网络封包**：`network/` 下 22 个封包由 `loader/ChannelLoader` 按 classpath **自动扫描注册**。不要按「有没有被 import」判断死活 —— 它们靠消息系统反射用。删任何一个都可能导致封包 discriminator 错位、多人同步崩。

---

## 8. 维护时最常用的 grep 模板

```bash
DIR="路径/diffs/<旧>-to-<新>"
AE2="$DIR/mods/Applied-Energistics-2-Unofficial/full-java.patch"
FC="$DIR/mods/AE2FluidCraft-Rework/full-java.patch"

# 1. mixin 注入点是否被动（最优先）
grep -nE "handleMouseClick|onGuiClosed|handleMouseInput|getScrollBar|updateView|addEntriesToView|setItemToCraft|startJob|submitJob|completeJob" "$AE2"

# 2. 继承的基类是否被动
grep -nE "class (AEBaseGui|ContainerMEMonitorable|ToolWirelessTerminal)|VirtualMEMonitorableSlot|PacketInterfaceTerminalUpdate|MonitorableTypeFilter" "$AE2"

# 3. 反射字段是否改名
grep -nE "draggedSlots|myStorage|myPartitionList|\btracked\b|originalGuiBtn" "$AE2"

# 4. AE2FC 核心符号
grep -nE "ItemFluidDrop|FluidPatternDetails|ItemFluidPacket" "$FC"
```
命中后只需读那几个 hunk，不用通读 15 万行 patch。
