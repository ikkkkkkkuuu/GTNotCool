# GT-Not-Cool

[![Build Status](https://github.com/ikkkkkkkuuu/GTNotCool/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/ikkkkkkkuuu/GTNotCool/actions/workflows/build-and-test.yml)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.7.10-brightgreen)](https://www.minecraft.net)
[![GTNH](https://img.shields.io/badge/GT_New_Horizons-addon-blue)](https://www.gtnewhorizons.com/)

**GTNotCool** (`sciencenotcool`) 是 [GT New Horizons](https://www.gtnewhorizons.com/) 的综合附属模组，为 Minecraft 1.7.10 生态提供大规模蒸汽工业设备、无线能源网络、高级蜜蜂繁育系统、蜂窝自动化处理、样板通配与 ME 网络扩展、大型矿石处理流水线等一系列工业增强内容。

---

## 🏭 多方块机器

### 大型蒸汽机器（18 台）

所有大型蒸汽机器支持 **青铜 / 钢** 两种外壳等级，对应 MV / HV 配方等级。在控制器槽中插入**不锈钢齿轮**可额外提升一级。

| 类型 | 对应单方块机器 |
|------|----------------|
| Large Steam Assembler | 组装机 |
| Large Steam Alloy Smelter | 合金炉 |
| Large Steam Bending Machine | 卷板机 |
| Large Steam Centrifuge | 离心机 |
| Large Steam Chemical Reactor | 化学反应釜 |
| Large Steam Circuit Assembler | 电路组装机 |
| Large Steam Compressor | 压缩机 |
| Large Steam Cutting Machine | 切割机 |
| Large Steam Distillation Tower | 蒸馏塔 |
| Large Steam Electrolyzer | 电解机 |
| Large Steam Extruder | 压模机 |
| Large Steam Fluid Extractor | 流体提取机 |
| Large Steam Fluid Solidifier | 流体固化机 |
| Large Steam Forge Hammer | 锻造锤 |
| Large Steam Forming Press | 冲压机床 |
| Large Steam Laser Engraver | 激光蚀刻机 |
| Large Steam Mixer | 搅拌机 |
| Large Steam Wire Mill | 线材轧机 |

此外还有 **Large Steam Pyrolyse Oven**（热解炉，加热线圈等级提供速度加成）。

### 蒸汽涡轮 & 锅炉

| 机器 | 外壳 | 蒸汽消耗 | 输出 | 效率 |
|------|------|----------|------|------|
| Large Steam Turbine | 青铜 | 800 L/s | 400 EU/t | 85% |
| Large Steam Turbine | 钢 | 1600 L/s | 800 EU/t | 90% |
| Large Steam Turbine | 钛 | 3200 L/s | 1600 EU/t | 95% |
| Large Steam Turbine | 钨钢 | 6400 L/s | 3200 EU/t | 100% |

所有涡轮副产蒸馏水，支持螺丝刀切换无线输出模式。对应的 **Large Boiler**（青铜 / 钢 / 钛 / 钨钢）一并提供。

### 核心多方块

| 机器 | 说明 |
|------|------|
| **Large Ore Processor** | 大型矿石处理器 — 一站式矿石处理，支持 GT / GT++ / BartWorks 矿石及特殊材料（铈、钐、硅岩等） |
| **Large Comb Processor** | 大型蜂窝处理机 — 并行处理 256 蜂窝，无损超频，自动识别铂/铱/锇/超能硅岩/普通硅岩并替换为专用配方 |
| **Mega Industrial Apiary** | 巨型工业蜂箱 — HV 级供电、128× 速度、32 升级槽、工业蜂后支持，槽位随能源仓等级动态扩展 |
| **Large Steam Bee Breeder** | 大型蒸汽蜂育器 — 自动蜜蜂杂交繁育，GUI 可视化繁育链 |
| **Singularity Data Hub** | 奇点数据枢纽 |
| **Vault Port Hatch** | 仓库端口仓 |

---

## 🐝 蜜蜂系统

- **Large Steam Bee Breeder** — 全自动蜜蜂杂交繁育，GUI 内可视化查看完整繁育链
- **Drone Pool** — 雄蜂池，集中存储雄蜂
- **Bee Breeding Helper** — 繁育辅助工具类
- **Mega Industrial Apiary** — 工业化量产蜂箱：HV 输入、128 倍速、32 升级槽，能源仓等级决定蜜蜂槽位数（ZPM → 512 槽）
- 蜂窝处理统一化管理：自动从 GT5U / GT++ / gtnhmod 等多源配方表同步，对铂/铱/锇/超能硅岩/普通硅岩蜂窝自动拦截并替换为专用产出配方

---

## 🔌 能源系统

### 有线 / 无线能源仓

覆盖 **LV → MAX** 全电压等级，提供 `4A / 16A / 64A` 多档电流。

- **Energy Hatch** — 传统有线能源仓
- **Wireless Energy Hatch** — 无线能源仓，能量存入全局网络，无视电压限制
- **Wireless Dynamo Hatch** — 无线动力仓，从全局网络获取能量

### 激光能源仓

**IV → MAX**，`256A` 起步超大规模激光能量传输。

### 无线能源覆盖板

**LV → MAX** 全电压，4A 电流，直接贴机器上即可从全局网络获取能量。

### 无线蒸汽网络

基于 `SpaceProjectManager` 团队系统，蒸汽全局共享。

- **全局蒸汽存储** — 每位玩家/团队独立的无线蒸汽账户，跨纬度共享
- **螺丝刀切换** — 所有蒸汽多方块机器用螺丝刀右键控制器正面切换无线模式
- **实时显示** — `getInfoData` 面板和 WAILA 提示显示：无线开关状态、网络蒸汽余额、本次蒸汽消耗
- **指令管理** — `/steam_network add|set|join|display` 管理蒸汽网络
- **自动持久化** — WorldSavedData 存储，重启/切换维度不丢失

产汽端（锅炉无线模式）→ 存网 → 耗汽端（蒸汽机器无线模式）。

---

## 🔧 基础机器

| 机器 | 等级 |
|------|------|
| Diesel Generator | LV / MV / HV / EV |
| Electric Steam Turbine | LV / MV / HV / EV / IV / LuV |

---

## 📋 样板与 ME 网络

| 物品 | 说明 |
|------|------|
| **Wildcard Pattern** | 通配样板符 — 支持物品与流体通配匹配 |
| **Super Pattern Input Bus (ME)** | 超级样板输入总线 |
| **Super Pattern Input Hatch (ME)** | 超级样板输入总成 |
| **Super Pattern Input Mirror (ME)** | 超级样板输入镜像 |

---

## 🛠️ 物品

| 物品 | 说明 |
|------|------|
| **Bioware SMD Inductor** | 生物贴片电感 |

---

## 🎒 工具腰带

- 按 **R 键**（可配置）打开
- 固定 **10 槽位**，仅可存取不可堆叠物品
- 操作方式：手持目标物品按 R → 存入 / 取出

---

## 📐 自定义配方表

| Recipe Map | 用途 |
|------------|------|
| `OreProcessingRecipes` | 大型矿石处理器专用配方，覆盖所有矿石类型及 GT++ / BartWorks 材料 |
| `SteamCombProcessingRecipes` | 蒸汽蜂窝处理配方，自动反射同步多源配方表，特殊蜂窝统一替换 |

额外提供 Assembler / Bender / Furnace / Laser Engraver / Crafting Table 等配方注册。

---

## 🚀 自动发布

推送 `master` 分支后 CI 自动：
1. 构建验证 (`Build and test`)
2. 递增版本号
3. 创建 Git Tag + GitHub Release
4. 上传 `GTNotCool-{version}.jar`

---

## 📦 依赖

| 依赖 | 必要性 |
|------|--------|
| Minecraft 1.7.10 + Forge 10.13.4.1614 | required |
| GregTech 5 Unofficial | required |
| GTNHIntergalactic | required |
| StructureLib | required |
| ModularUI | required |
| IC2 | required |
| BartWorks | required |
| Avaritia | required |
| Botania | required |
| Thaumcraft | required |
| GalacticGreg | required |
| GT++ (miscutils) | optional |

---

## 🔗 参考

- [GT-Not-Leisure](https://github.com/ABKQPO/GT-Not-Leisure) — 大型蒸汽多方块机器参考
- [WildcardPatternforGTNH](https://github.com/clfpwp/WildcardPatternforGTNH-1.7.10) — 通配样板符功能参考
- [NH-Utilities](https://github.com/Keriils/NH-Utilities) — GTNH 实用工具参考

---

## 📄 许可

基于 GTNH 社区许可发布。详见 [LICENSE-template](./LICENSE-template)。
