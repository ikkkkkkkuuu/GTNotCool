# GT-Not-Cool Multiblock Machine Playbook

Use this playbook for every new multiblock, port, inheritance change, structural rewrite, or behavior change to an existing multiblock. Read-only source inspection may happen first; implementation must wait for the user's base-class confirmation.

## Contents

- Mandatory base-class gate
- Source inspection workflow
- Base-family map
- Machine design contract
- StructureLib implementation
- Processing, state, GUI, registration, and resources
- Validation and shared-base safeguards

## Mandatory base-class gate

### Ask before every multiblock mutation

Before editing Java, structure files, machine registration, recipes, localization, textures, or GUI resources for a multiblock, ask the user which inheritance source to use:

1. **Official/upstream base** — a base from the exact resolved GTNH dependency source, such as GT5U/GT++, TecTech, KubaTech, or another owning mod.
2. **GT-Not-Cool custom base** — one of the project bases under `Common/machines/multiblock/multiMachineBase/`.

Do not infer the answer from the nearest machine, feature list, package, recipe map, or apparent convenience. Do not silently prefer the custom base because this is a private addon, and do not silently prefer an upstream base because it is more canonical.

Use a question in this shape:

> 这次多方块机器要继承哪一类基类：GTNH/GT 官方基类，还是 GT-Not-Cool 自定义基类？我已根据源码找到以下具体候选：……。请确认具体基类后我再开始修改。

If the user chooses only a family, present the concrete candidates and wait for the specific base. If the request already names a base, explicitly reconfirm it before mutation. For an existing multiblock bug fix, state its current base and ask whether inheritance must remain unchanged.

### Allowed work before confirmation

Before the answer, limit work to read-only investigation needed to make the choice concrete:

- inspect the current machine and its inheritance chain;
- inspect GT-Not-Cool base classes and representative subclasses;
- inspect the exact resolved upstream source/tag;
- compare inherited processing, structure, GUI, NBT, hatch, wireless, upgrade, and maintenance behavior;
- report tradeoffs and recommend candidates.

Do not create or modify implementation files until the answer is received.

### Record the decision

At implementation start, record in the task commentary:

- selected family: official/upstream or GT-Not-Cool custom;
- exact base class;
- exact upstream version/tag when applicable;
- local and upstream representative machines inspected;
- inherited features intentionally accepted and intentionally excluded.

Changing the selected base later requires a new confirmation because it changes machine behavior and compatibility boundaries.

## Source inspection workflow

Never design a multiblock base contract from memory. Use sources in this order:

1. The target GT-Not-Cool machine, if it already exists.
2. The selected local custom base and all parents up to the owning upstream base.
3. At least one local sibling using the same selected base and similar processing/structure behavior.
4. The exact resolved upstream source for the selected base.
5. One upstream concrete machine at the same tag that demonstrates the relevant API.
6. StructureLib and ModularUI2 exact sources when their contracts are involved.

Use `docs/codex/UPSTREAMS.md` to identify the owning repository and tag. Prefer local source JARs already resolved by Gradle:

```powershell
Get-ChildItem -LiteralPath "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1" `
  -Recurse -Filter "*-sources.jar"
```

List or stream files from a source JAR without extracting it into the repository:

```powershell
tar -tf "C:\path\dependency-version-sources.jar"
tar -xOf "C:\path\dependency-version-sources.jar" "path/inside/archive/BaseClass.java"
```

If the source JAR is unavailable, inspect the exact official tag online or use a temporary shallow clone outside the workspace. Do not vendor source trees, decompiled classes, Gradle caches, or temporary clones into this repository.

### Evidence to extract from source

For every candidate base, determine:

- direct parent and generic self type;
- required constructors and `newMetaEntity` pattern;
- structure interfaces, alignment behavior, and required abstract methods;
- processing path and power setup;
- supported hatch lists and lifecycle cleanup;
- overclocking, parallelism, efficiency, maintenance, void/input separation, and batch behavior;
- GUI framework and server/client synchronization;
- saved NBT and item-NBT behavior;
- Waila/tooltip behavior;
- wireless energy/steam, ownership, and upgrade-tree behavior;
- assumptions that would be inherited even if the new machine does not need them.

Prefer adapting a small verified pattern. Check the license before copying substantial source or assets.

## Base-family map

This map describes the audited source tree. Re-read the actual classes before each task.

### GT-Not-Cool custom bases

| Base | Direct parent | Inherited project behavior to review |
| --- | --- | --- |
| `GTNCMultiBlockBase<T>` | `MTEExtendedPowerMultiBlockBase<T>` | Chip upgrade tree, persistent upgrade state, custom ModularUI2 base GUI, Waila upgrade display |
| `GTNCWirelessEnergyMultiMachineBase<T>` | `GTNCMultiBlockBase<T>` | All GTNC electric-base behavior plus owner-bound wireless EU processing and multi-cycle output aggregation |
| `GTNCSteamMultiBlockBase<T>` | `MTESteamMultiBlockBase<T>` | Clean steam and ME input-output behavior, wireless steam, owner/state persistence, modern base GUI |
| `GTNCAdvancedSteamMultiBlockBase<T>` | `GTNCSteamMultiBlockBase<T>` | Existing large-steam family behavior: tier helpers, chip upgrades, cross-recipe processing, advanced GUI |
| `GTNCWirelessBase<T>` | `MTEExtendedPowerMultiBlockBase<T>` | Specialized Miracle Door-style wireless processing, maintenance immunity and mode handling, without the GTNC chip-upgrade base |

Selecting a custom base opts into its inherited behavior. Disable a feature only through an established override when the base explicitly supports it; do not inherit a broad base and then fight its contract with scattered overrides.

### Official/upstream base families present in this project

The exact owner and version must come from the resolved dependencies.

| Example family | Typical role to verify from exact source |
| --- | --- |
| `MTEMultiBlockBase` / `MTETooltipMultiBlockBase` | Core GregTech processing and tooltip contracts |
| `MTEEnhancedMultiBlockBase<T>` | Declarative StructureLib structure checking and alignment |
| `MTEExtendedPowerMultiBlockBase<T>` | Enhanced structure behavior plus long-EU/tick power handling |
| `MTESteamMultiBlockBase<T>` | GT++/GT5U steam multiblock power, hatches, GUI, and overclock behavior |
| `TTMultiblockBase` and TecTech specialized bases | TecTech-specific mechanics and UI/energy assumptions |
| `KubaTechGTMultiBlockBase<T>` | KubaTech-specific multiblock behavior |
| Specialized machine bases such as Forge of Gods modules/controllers | Narrow inherited mechanics; use only when the user explicitly chooses that lineage |

“Official base” is not a single class. After the user selects the official family, present exact candidates based on source evidence and obtain the concrete class choice.

## Define the machine contract

After base confirmation and before implementation, establish:

| Decision | Required answer |
| --- | --- |
| Purpose | Processing machine, generator, network controller, module, utility, or special system |
| Progression | Intended GT tier, prerequisites, controller recipe, and private-content deviation |
| Recipe behavior | Existing/new recipe map, inputs/outputs, catalysts, special values, locking/batch/separation |
| Power | EU, steam, wireless EU, wireless steam, generator output, amps, exotic hatches |
| Performance | Overclock type, speed modifier, EU modifier, parallel limit, cycle/batch limits |
| Structure | Dimensions, controller position, offsets, facing/alignment, casing count, tiered elements |
| Hatches | Required, optional, forbidden, tier/quantity limits, ME/dual-input support |
| State | NBT, owner, mode, upgrades, progress, cached topology, save compatibility |
| UI | Base GUI, extra panels, synchronized values, server-side actions |
| Integration | Optional mods, NEI, Waila, quests, wireless/global networks |

Do not invent a progression tier or balance value when the request leaves it material to the design. Use official quest/source evidence, then ask the user for the private-content decision.

## StructureLib implementation

### Structure source

Follow the selected sibling/base pattern:

- external `.mbs` through `StructureUtils.readStructureFromFile` for large reusable shapes; or
- an inline string shape when the structure is small and the local convention supports it.

Keep the resource under `assets/sciencenotcool` and use `ScienceNotCool.RESOURCE_ROOT_ID` rather than a duplicated namespace string.

### Coordinate invariants

- Determine the controller marker and calculate horizontal, vertical, and depth offsets from the actual shape.
- Use the same transposition convention in `getStructureDefinition`, `checkPiece`, `buildPiece`, and `survivalBuildPiece` calls.
- Verify front, back, left, right, top, and bottom in-world; an apparently symmetric shape can still have asymmetric hatch or texture rules.
- Respect the alignment capabilities inherited from the selected base. Do not enable rotation/flip merely because StructureLib can express it.

### Structure definition

Define and review:

- shape piece names and lazy/static definition lifetime;
- casing elements and texture indices;
- tiered blocks/channels and setters/reset behavior;
- hatch adders, dot/casing fallback, quantity limits, and required hatch checks;
- glass/coil/field-generator/material tiers;
- minimum casing counts and mutually exclusive elements;
- construction hints and survival construction budget behavior.

Reset all structure-derived counters and tiers before every check. A failed or partial check must not retain values from the previous valid structure.

## Processing and power

- Trace whether the selected base uses `checkProcessing`, `ProcessingLogic`, a specialized processing method, or generator logic.
- Set available voltage, amperage, amperage overclock, perfect/imperfect overclock, speed, EU modifier, and parallel suppliers in the established hook.
- Use `long` or `BigInteger` where the selected base and wireless/global network require it; check overflow before narrowing.
- Consume inputs exactly once and publish outputs only after a successful authoritative server-side result.
- Bound wireless/multi-cycle loops and output aggregation.
- Preserve shutdown reasons, efficiency, maintenance, pollution, void protection, recipe locking, input separation, and batch-mode contracts from the chosen base.
- Avoid world, recipe-map, inventory, or network-wide scans on every tick.

Test invalid power/hatch combinations, insufficient input, full outputs, power loss, disabled work, and structure invalidation—not only the successful recipe path.

## State, GUI, and synchronization

- Keep gameplay state server-authoritative.
- Persist only durable state; rebuild caches after load.
- Preserve existing NBT names/types when modifying a machine. Add migration logic for schema changes.
- Mark dirty after authoritative persistent changes.
- Clear/rebuild hatch and structure-derived collections through the selected base lifecycle.
- Use the GUI framework inherited by the selected base. Do not mix legacy ModularUI and ModularUI2 patterns accidentally.
- Synchronize display state and execute button mutations server-side. Do not call `PanelSyncManager.getPlayer()` while a panel is constructed.
- Verify close/reopen, reconnect, chunk reload, and dedicated-server classloading.

## Registration, recipes, localization, and assets

### Registration

- Choose an unused stable machine ID without renumbering existing entries.
- Bind the controller to the correct `GTNCItemList` entry.
- Register it in the established `MachineLoader` phase and confirm construction order dependencies.
- Keep internal/unlocalized names, registry names, controller stacks, and recipes consistent.

### Recipes and NEI

- Reuse an existing recipe map when semantics match; create a new map only for genuinely distinct behavior/UI.
- Register controller, casing, hatch, and machine recipes in the correct lifecycle phase.
- Verify recipe catalysts, frontend/backend, NEI visibility, dynamic client/server recipe views, and optional ingredients.

### Localization and assets

- Add source `// #tr` comments beside Java key usage; do not hand-edit generated language files.
- Declare multiblock machine-type and tooltip text as `public static final String` constants in
  `utils/lang/TextLocalization.java`, initialized through `TextEnums.tr(...)`. Put the complete
  `// #tr`, English `// #`, and Chinese `// # zh_CN` marker block directly above each constant.
  `createTooltip()` must reference these constants instead of calling `StatCollector.translateToLocal(...)` inline.
- Add controller/casing/overlay textures at native pixel resolution and verify active/inactive/facing rendering.
- Keep StructureLib files, textures, and recipe/quest resources in the correct namespace and case.

## Validation gates

### Static gates

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat check --no-daemon
git diff --check
```

Run `processResources` when structures, localization, textures, or other resources change. Treat `test NO-SOURCE` as no gameplay coverage.

### Runtime gates

Verify all applicable scenarios:

1. Controller registration and item/recipe availability.
2. Construction hints and survival construction.
3. Valid structure for every permitted facing/alignment.
4. Rejection for a missing casing, wrong tier, missing/duplicate/forbidden hatch, and incorrect controller offset.
5. Successful recipe/generation behavior and expected overclock/parallel/power values.
6. Insufficient inputs/power, full outputs, shutdown, disabled work, and structure break during operation.
7. GUI synchronization, mode controls, tooltips/Waila, NEI, textures, and localization.
8. Chunk unload/reload, server restart, dismantle/replacement, and old-save compatibility when relevant.
9. Dedicated-server startup and multiplayer interaction.
10. Wireless/global state ownership, insufficient balance, reconnect, and world-switch behavior when applicable.

Record which cases were exercised and which remain unverified.

## Shared-base safeguards

Changing a GT-Not-Cool custom base is a cross-machine change, not a local machine implementation.

Before editing a shared base:

1. Ask the user whether the behavior belongs in the shared base or only the target machine.
2. Search every direct and indirect subclass.
3. Identify overrides and assumptions for processing, GUI, hatches, NBT, upgrades, wireless behavior, and maintenance.
4. Prefer a narrow opt-in hook or target-machine override when existing machines should not change.
5. Compile and test representative subclasses from each affected family.

Do not move behavior into or out of a base class as incidental cleanup.

## Completion record

Report:

- user-confirmed base family and exact class;
- local and exact-version upstream sources inspected;
- machine ID, item binding, recipe map, structure resource, and registration path;
- inherited features and intentional overrides;
- static and runtime validation completed;
- progression, multiplayer, save, integration, or visual uncertainty that remains.
