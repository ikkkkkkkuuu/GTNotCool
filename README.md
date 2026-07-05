# GT-Not-Cool

[![Build Status](https://github.com/ikkkkkkkuuu/GTNotCool/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/ikkkkkkkuuu/GTNotCool/actions/workflows/build-and-test.yml)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.7.10-brightgreen)](https://www.minecraft.net)
[![GTNH](https://img.shields.io/badge/GT_New_Horizons-addon-blue)](https://www.gtnewhorizons.com/)

**GTNotCool**（`sciencenotcool`）是 [GT New Horizons](https://www.gtnewhorizons.com/) 的综合附属模组，面向 Minecraft 1.7.10。它围绕 **大规模蒸汽工业**、**无线能源 / 蒸汽网络**、**工业化蜜蜂养殖与蜂窝处理**、**样板通配与 ME 网络扩展**、**无线 ME 综合终端** 以及一系列 **玩家工具与时间加速** 系统，为 GTNH 中后期到末期的自动化流程提供整套增强内容。

> 面向 GTNH 环境构建，依赖其自带的 GregTech 5U、AE2-Unofficial、AE2FluidCraft-Rework、BartWorks、GTNH-Intergalactic 等模组。

---

## 目录

- [🏭 多方块机器](#-多方块机器)
- [🐝 蜜蜂与蜂窝系统](#-蜜蜂与蜂窝系统)
- [⚡ 能源系统](#-能源系统)
- [♨️ 无线蒸汽网络](#️-无线蒸汽网络)
- [🔧 基础机器](#-基础机器)
- [📋 样板与 ME 网络](#-样板与-me-网络)
- [📡 无线 ME 综合终端](#-无线-me-综合终端)
- [🎒 工具腰带与玩家工具](#-工具腰带与玩家工具)
- [⏳ 时间加速系统](#-时间加速系统)
- [📐 自定义配方表](#-自定义配方表)
- [⚙️ 配置项](#️-配置项)
- [📦 依赖](#-依赖)
- [🚀 构建与发布](#-构建与发布)
- [🔗 参考与致谢](#-参考与致谢)

---

Mixin
**禁用cropsnh的杂草生成
加速cropsnh的作物生长为64倍**







## 🏭 多方块机器

### 大型蒸汽机器（18 台单机模拟）

以下大型蒸汽多方块各自并行模拟对应的 GT 单方块机器，支持 **青铜 / 钢** 两档外壳等级：

| 类型 | 对应单方块 | 类型 | 对应单方块 |
|------|-----------|------|-----------|
| Large Steam Assembler | 组装机 | Large Steam Extruder | 挤压机 |
| Large Steam Alloy Smelter | 合金炉 | Large Steam Fluid Extractor | 流体提取机 |
| Large Steam Bending Machine | 卷板机 | Large Steam Fluid Solidifier | 流体固化机 |
| Large Steam Centrifuge | 离心机 | Large Steam Forge Hammer | 锻造锤 |
| Large Steam Chemical Reactor | 化学反应釜 | Large Steam Forming Press | 冲压机床 |
| Large Steam Circuit Assembler | 电路组装机 | Large Steam Laser Engraver | 激光蚀刻机 |
| Large Steam Compressor | 压缩机 | Large Steam Mixer | 搅拌机 |
| Large Steam Cutting Machine | 切割机 | Large Steam Wire Mill | 线材轧机 |
| Large Steam Distillation Tower | 蒸馏塔 | Large Steam Electrolyzer | 电解机 |

### 蒸汽动力与锅炉

**Large Steam Turbine** — 四档外壳，输出蒸馏水，支持无线蒸汽输入与无线 EU 输出：

| 外壳 | 输出 | 效率 | 最佳蒸汽流量 |
|------|------|------|--------------|
| 青铜 | 400 EU/t | 85% | 4,000 L/s |
| 钢 | 800 EU/t | 90% | 8,000 L/s |
| 钛 | 1,600 EU/t | 95% | 16,000 L/s |
| 钨钢 | 3,200 EU/t | 100% | 32,000 L/s |

配套 **Large Boiler**（青铜 / 钢 / 钛 / 钨钢 四档）作为产汽端。

### 其它蒸汽多方块

| 机器 | 说明 |
|------|------|
| **Large Steam Crucible** | 大型蒸汽坩埚，五档外壳（钢 / 殷钢 / 不锈钢 / 钛 / 钨钢），使用专用 `SteamCrucibleRecipes` 配方（按坩埚等级分级） |
| **Large Steam Void Miner** | 大型蒸汽虚空采矿机，带维度 / 矿物过滤 GUI |
| **Steam Eye of Harmony** | 蒸汽版和谐之眼 |

### 处理与产业多方块

| 机器 | 说明 |
|------|------|
| **Large Ore Processor** | 大型矿石处理器 — 一站式矿石处理，超大并行，覆盖 GT / GT++ / BartWorks 矿石及特殊材料 |
| **Large Comb Processor** | 大型蜂窝处理机 — 并行处理最多 **2,560** 蜂窝，无损超频；自动识别铂 / 铱 / 锇 / 超能硅岩（Naquadria）/ 硅岩（Naquadah）蜂窝并替换为专用配方 |
| **Mega Industrial Apiary** | 巨型工业蜂箱 — 见 [蜜蜂系统](#-蜜蜂与蜂窝系统) |
| **General Chemical Factory** | 综合化工厂 |
| **Drilling Rig / Mining Rig** | 钻井平台 / 采矿平台 |
| **Singularity Data Hub** | 奇点数据枢纽 |
| **Super Space Elevator** | 超级太空电梯（基于 TecTech 多方块基类），配套发光外壳 |

> 另附 Assembler Matrix / Quantum Computer 系列外壳方块（合成核心、加速器、多线程器、奇点核心、128M/256M 合成存储等），用于上述高级多方块结构。

---

## 🐝 蜜蜂与蜂窝系统

### Mega Industrial Apiary（巨型工业蜂箱）

- **128× 蜜蜂加速**，**32 个产量升级槽**
- 最低需要 **HV（512 EU/t）** 供电才有蜂位
- 蜂位数量 = 输入总功率 ÷ 512 EU/t（例如 ZPM 131,072 EU/t → **256 蜂位**）
- 支持工业蜂后（SWARMER 模式占 1 蜂位）
- 每次运行能耗 = `512 × 蜂位数 × 0.99` EU/t

### Large Steam Bee Breeder（大型蒸汽蜂育器）

- 蒸汽驱动的全自动蜜蜂杂交繁育
- GUI 内可视化查看完整繁育链

### 辅助组件

- **Drone Pool** — 雄蜂池，集中存储雄蜂
- **Bee Breeding Helper** — 繁育逻辑辅助
- **蜂窝统一处理** — 从 GT5U / GT++ / gtnhmod 等多源配方表反射同步；对铂 / 铱 / 锇 / 超能硅岩 / 硅岩蜂窝拦截并替换为专用产出配方（见 Large Comb Processor）

---

## ⚡ 能源系统

### 有线 / 无线能源仓

覆盖 **LV → MAX** 全电压，低压档提供 `4A / 16A / 64A` 多档电流，IV 及以上提供 `256A` 起、直至百万安级的大电流阶梯。

- **Wireless Energy Hatch** — 无线能源仓，能量存入全局网络
- **Laser Energy Hatch** — 激光能源仓，**IV → MAX**，`256A` 起步的超大规模激光能量传输
- **Wireless Multi Energy Cover** — 无线能源覆盖板，**LV → MAX**（14 档），4A，直接贴机器即可从全局能源网络取电

---

## ♨️ 无线蒸汽网络

基于 GT 的 `SpaceProjectManager` 团队系统，蒸汽全局共享。

- **全局蒸汽存储** — 每位玩家 / 团队独立的无线蒸汽账户，按 UUID 记账，跨维度共享
- **螺丝刀切换** — 蒸汽多方块机器用螺丝刀右键控制器正面切换无线模式
- **实时显示** — `getInfoData` 面板与 WAILA 提示显示无线开关状态、网络蒸汽余额、本次消耗
- **指令管理** — `/steam_network <add|set|join|display> [player] [amount]`（`add` / `set` 需 OP）
- **自动持久化** — WorldSavedData 存储，重启 / 切换维度不丢失

流向：产汽端（锅炉无线模式）→ 存入网络 → 耗汽端（蒸汽机器无线模式）。

---

## 🔧 基础机器

| 机器 | 等级 |
|------|------|
| **Diesel Generator** | LV / MV / HV / EV |
| **Electric Steam Turbine** | LV / MV / HV / EV / IV / LuV |
| **Time Accelerator** | LV → UHV（9 档，见 [时间加速系统](#-时间加速系统)） |

---

## 📋 样板与 ME 网络

### 通配样板（Wildcard Pattern）

- 支持按可配置的 **材料轴模型** 对物品与流体进行通配匹配
- 采用 MUI2 手持物品 GUI（`PlayerInventoryGuiFactory`），非传统 FML GUI Handler
- 内置过滤（filter）与输入 / 输出（io）多轴模型

### ME 超级样板输入

| 组件 | 说明 |
|------|------|
| **Super Pattern Input Bus / Hatch (ME)** | 超级样板输入总线 / 总成（同一实现，标志位区分），作为 AE2 接口终端注册 |
| **Super Pattern Input Mirror (ME)** | 超级样板输入镜像 / 代理 |
| **Vault Port Hatch** | 仓库端口仓 |

> ME 输出仓 / 输出总线可在配置中启用无限容量的流体 / 物品输出（默认开启）。

---

## 📡 无线 ME 综合终端

一体化的 **无线 ME 综合终端**，将 **接口终端 + 样板编码 + 合成终端** 集于一件物品：

- 无线访问 ME 网络，支持无限扩展卡（范围）与能量卡升级
- 接口终端列表支持 **Alt+点击隐藏** 与 **Shift+点击重命名** 单个接口
- **结构预览「?」按钮** — 从终端打开时，可将多方块结构方块打包进样板（纸张输出）
- 完整流体支持（AE2FC 流体样板 / 数据包），NEI 配方一键导入终端
- **Shift+滚轮** 快速替换样板配料，自动合成请求追踪
- 快捷键：打开综合终端、将手持物品送入网络；中键快速补货 / 快速下单
- 附带 **Pattern Modifier**（样板修改器）物品

---

## 🎒 工具腰带与玩家工具

### 工具腰带（Tool Belt）

- 数据挂在玩家身上，**无需实体腰带物品**，固定 **10 槽位**
- 默认 **R 键** 打开 **径向菜单**，另有两个默认未绑定的左右循环键
- 支持存 / 取工具，可配置松开即切换、圆形 / 方形裁剪、死区偏移等

### 采矿工具

- **Vein Mining Pickaxe** — 连锁采矿镐，最大连锁数量与范围可配置（默认 327,670 / 32）

### 计算物品

- **High Computing Power Chips Tier I–VII** — 高算力芯片
- **Bioware SMD Inductor** — 生物贴片电感

---

## ⏳ 时间加速系统

- **Time Accelerator**（LV → UHV，9 档）— 加速半径内的随机刻与 TileEntity
- **Time Vial（时间瓶）** — 可配置的加速道具，支持方块模式加速、GT 机器加速、加速折扣、初始速率与最大加速上限等
- 全部行为通过 `Time_Vial` 配置类别调节（详见下方配置项）

---

## 📐 自定义配方表

| Recipe Map | 用途 |
|------------|------|
| `OreProcessingRecipes` | 大型矿石处理器专用，覆盖所有矿石类型及 GT++ / BartWorks 材料 |
| `CombProcessingRecipes` | 蒸汽蜂窝处理，反射同步多源配方表，特殊蜂窝统一替换 |
| `SteamCrucibleRecipes` | 大型蒸汽坩埚配方，按坩埚等级分级 |

额外提供 Assembler / Bender / Furnace / Laser Engraver / Crafting Table 等配方注册。

---

## ⚙️ 配置项

配置文件：`config/sciencenotcool/sciencenotcool.cfg`

| 类别 | 主要内容 |
|------|----------|
| **General** | GT 工具合成耐久（10000）、显示配方所有者 / WAILA 平均耗时 / NEI 原始电压 |
| **Time_Vial** | 启用开关、方块模式与间隔、单瓶限制、折扣、加速器倍率（256× / 128×）、GT 机器加速、初始速率（32）、最大加速（1024）、基础持续时间（18000 tick） |
| **Vein_Miner_Pickaxe** | 最大连锁数量（327670）、最大范围（32） |
| **Tool_Belt** | 松开即切换、圆形裁剪、越界点击、显示空槽、死区偏移（8.0） |
| **ME_Output_Hatch** | ME 输出仓 / 输出总线无限容量开关（默认开启） |

---

## 📦 依赖

### 必需（required-after）

Minecraft 1.7.10 + Forge 10.13.4.1614、GregTech 5 Unofficial、AE2-Unofficial（rv3-beta-977-GTNH）、AE2FluidCraft-Rework（1.5.88）、BartWorks、GTNH-Intergalactic、GalacticGreg、IC2、ModularUI、StructureLib、Avaritia、Botania、Thaumcraft。

### 可选 / 软依赖（after）

GT++（miscutils）、Blood Magic / Blood Arsenal、Eternal Singularity、Et Futurum、Galacticraft、DreamCraft、NEI Custom Diagram 等。

---

## 🚀 构建与发布

```bash
./gradlew build          # 构建 jar
./gradlew runClient      # 启动开发客户端
./gradlew runServer      # 启动开发服务端
```

推送 `master` 分支后 CI 自动：

1. 构建验证（`Build and test`）
2. 递增版本号
3. 创建 Git Tag + GitHub Release
4. 上传 `GTNotCool-{version}.jar`

---

## 🔗 参考与致谢

本项目部分功能在实现时参考 / 借鉴了以下开源项目，特此致谢：

- [GT-Not-Leisure](https://github.com/ABKQPO/GT-Not-Leisure) — 大型蒸汽多方块机器参考
- [WildcardPatternforGTNH](https://github.com/clfpwp/WildcardPatternforGTNH-1.7.10) — 通配样板功能参考
- [NH-Utilities](https://github.com/Keriils/NH-Utilities) — GTNH 实用工具参考
- [AE2Things](https://github.com/asdflj/AE2Things) — 无线 ME 综合终端相关代码参考

---

## 📄 许可

基于 GTNH 社区许可发布。详见 [LICENSE-template](./LICENSE-template)。
