# GT-Not-Cool

[![Build and test](https://github.com/ikkkkkkkuuu/GTNotCool/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/ikkkkkkkuuu/GTNotCool/actions/workflows/build-and-test.yml)

**GTNotCool** (modid: `sciencenotcool`) 是一个 [GT New Horizons](https://www.gtnewhorizons.com/) 的附属模组，为 Minecraft 1.7.10 添加了大量蒸汽多方块机器、无线能源系统、装配矩阵、量子计算机等高级工业设备。

---

## 🏭 多方块机器

### 核心机器

| 机器 | 说明 |
|------|------|
| **Singularity Data Hub** | 奇点数据枢纽 |
| **Vault Port Hatch** | 仓库端口仓 |
| **Large Ore Processor** | 大型矿石处理器 |
  

### 大型蒸汽机器

所有大型蒸汽机器支持青铜/钢两种外壳等级，配方等级分别为 MV/HV。在控制器槽中插入**不锈钢齿轮**可额外提升一级配方等级。

| 机器 | 类型 |
|------|------|
| Large Steam Assembler | 组装机 |
| Large Steam Centrifuge | 离心机 |
| Large Steam Electrolyzer | 电解机 |
| Large Steam Bending Machine | 卷板机 |
| Large Steam Fluid Extractor | 流体提取机 |
| Large Steam Fluid Solidifier | 流体固化机 |
| Large Steam Chemical Reactor | 化学反应釜 |
| Large Steam Wire Mill | 线材轧机 |
| Large Steam Mixer | 搅拌机 |
| Large Steam Alloy Smelter | 合金炉 |
| Large Steam Circuit Assembler | 电路组装机 |
| Large Steam Compressor | 压缩机 |
| Large Steam Cutting Machine | 切割机 |
| Large Steam Forming Press | 冲压机床 |
| Large Steam Forge Hammer | 锻造锤 |
| Large Steam Extruder | 压模机 |
| Large Steam Distillation Tower | 蒸馏塔 |
| Large Steam Pyrolyse Oven | 热解炉（加热线圈等级提供速度加成） |
| **Large Steam Bee Breeder** 🐝 | 大型蒸汽养蜂机 — 自动蜜蜂杂交繁育 |

### 蒸汽涡轮 & 锅炉

| 机器 | 材料 | 消耗 | 产出 |
|------|------|------|------|
| Large Bronze Steam Turbine | 青铜 | 800L/s | 400EU/t, 85% 效率 |
| Large Steel Steam Turbine | 钢 | 1600L/s | 800EU/t, 90% 效率 |
| Large Titanium Steam Turbine | 钛 | 3200L/s | 1600EU/t, 95% 效率 |
| Large Tungstensteel Steam Turbine | 钨钢 | 6400L/s | 3200EU/t, 100% 效率 |

所有蒸汽涡轮副产蒸馏水，可用螺丝刀切换无线输出模式。

对应的 **Large Boiler**（青铜/钢/钛/钨钢）也一并提供。

---

## 🔌 能源仓 & 动力仓

### 有线/无线能源仓

覆盖 **LV → MAX** 全电压等级，提供 `4A / 16A / 64A` 多档电流（更高等级舱室可输出更大电流，最高 `16777216A`）。

- **Energy Hatch** — 传统有线能源仓
- **Wireless Energy Hatch** — 无线能源仓，将能量存入全局网络，忽略电压限制不炸
- **Wireless Dynamo Hatch** — 无线动力仓，从全局网络获取能量输出

### 激光能源仓

覆盖 **IV → MAX** 电压等级，支持 `256A` 起步的超大电流激光能源传输。

### 无线能源覆盖板 (4A)

覆盖 **LV → MAX** 全电压等级，4A 电流无线能源覆盖板，直接贴在机器上即可从全局网络获取能量。

---

## 🔧 基础机器

| 机器 | 等级 |
|------|------|
| Diesel Generator | LV / MV / HV / EV |
| Electric Steam Turbine | LV / MV / HV / EV / IV / LuV |

---

## 🛠️ 其他物品

| 物品 | 说明 |
|------|------|
| **Bioware SMD Inductor** | 生物贴片电感 |
| **Wildcard Pattern (支持流体)** | 通配样板符 — 支持物品和流体通配匹配 |
| **Super Pattern Input Bus (ME)** | 超级样板输入总线 |
| **Super Pattern Input Hatch (ME)** | 超级样板输入总成 |
| **Super Pattern Input Mirror (ME)** | 超级样板输入镜像 |

---

## 🚀 自动发布

每次推送到 `master` 分支，CI 会自动：
1. 运行 `Build and test` 构建验证
2. 构建成功后自动递增版本号（如 `1.0.0` → `1.0.1`）
3. 创建 Git Tag 和 GitHub Release
4. 上传 `GTNotCool-{version}.jar` 到 Release

---

## 📦 依赖

- Minecraft 1.7.10 + Forge 10.13.4.1614
- GregTech 5 Unofficial
- GTNHIntergalactic
- GT++ (miscutils)
- StructureLib
- ModularUI
- IC2
- BartWorks
- Avaritia
- Botania
- Thaumcraft
- GalacticGreg

---

## 🔗 参考代码

本项目部分功能借鉴自以下开源项目：

- [GT-Not-Leisure](https://github.com/ABKQPO/GT-Not-Leisure) — 大型蒸汽多方块机器参考
- [WildcardPatternforGTNH](https://github.com/clfpwp/WildcardPatternforGTNH-1.7.10) — 通配样板符功能参考
- [NH-Utilities](https://github.com/Keriils/NH-Utilities) — GTNH 实用工具参考

---

## 📄 许可

本项目基于 GTNH 社区许可发布。详见 [LICENSE-template](./LICENSE-template)。
