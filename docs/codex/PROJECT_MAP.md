# GT-Not-Cool Project Map

This is the durable onboarding map for maintainers and coding agents. It records verified repository facts, not product aspirations. Last deep audit: 2026-08-14.

## Reading order and truth hierarchy

For a broad change, read in this order:

1. Root `AGENTS.md` for non-negotiable repository rules.
2. This map for architecture, ownership, lifecycle, risks, and validation routes.
3. `UPSTREAMS.md` for official repository ownership and exact-version source routing.
4. `UPGRADE_PLAYBOOK.md` only after an explicit user instruction to adopt a target GTNH/dependency version.
5. `MULTIBLOCK_PLAYBOOK.md` for every multiblock task, with base-class confirmation before mutation.
6. The nearest implementation and its callers.
7. `gradle.properties` and `dependencies.gradle` for the actual build and integration versions.
8. Narrow subsystem documentation, when present.

When sources disagree, current build files and executable code win. Update stale documentation in the same change when practical.

## Repository profile

| Property | Verified value |
| --- | --- |
| Mod | GTNotCool / `sciencenotcool` |
| Root package | `com.xyp.gtnc` |
| Platform | Minecraft 1.7.10, Forge 10.13.4.1614 |
| Build | GTNHGradle conventions; settings plugin 2.0.20 at audit time |
| Language | Modern Java syntax through Jabel, Java 8-compatible output |
| Main sources | `src/main/java` |
| Resource namespace | `src/main/resources/assets/sciencenotcool` |
| Mixins | enabled; early, normal, late, plus the embedded AE2Thing set |
| Tests | no `src/test`; Gradle currently reports `test NO-SOURCE` |

Actual integration pins at the audit baseline include:

- Applied Energistics 2: `rv3-beta-1000-GTNH`
- AE2FluidCraft-Rework: `1.5.95-gtnh`
- GT5-Unofficial: `5.09.54.20`
- GTNHLib: `0.11.24`
- NotEnoughItems: `2.8.111-GTNH`

These values are a snapshot. Always re-read `dependencies.gradle` before targeting dependency internals.

For source ownership, exact-tag links, transitive dependency resolution, and upgrade workflow, use `docs/codex/UPSTREAMS.md`.

## Scale snapshot

The 2026-08-14 audit found approximately:

- 643 Java files and 121,391 Java lines.
- 977 source/resource files, including 276 PNG files, 15 JSON files, and 12 StructureLib `.mbs` files.
- 1,771 source translation markers.
- 83 Mixin classes.
- 4 `WorldSavedData` implementations.
- 337 machine ID entries and 683 `GTNCItemList` entries.
- 19 recipe-map builders.

Counts are navigation aids, not invariants. Refresh them after large refactors.

## Lifecycle map

The entry point is `src/main/java/com/xyp/gtnc/ScienceNotCool.java`. It delegates to `CommonProxy`; the physical client uses `ClientProxy`.

| Phase | Main responsibilities |
| --- | --- |
| `preInit` | Configuration static initialization; main network channel and packets; materials; items; entities; blocks; GUI/event handlers; wireless steam; ME bridge and wireless links; pixel building; BoxPlusPlus; AE2Thing |
| `init` | Bee alleles; AE2Thing init; BoxPlusPlus init; material init invoked by `ScienceNotCool` |
| `postInit` | Machine registration; AE interface-terminal registrations; AE2Thing post-init |
| `serverStarting` | Server commands; dynamic Miracle Door/Stellar Forge recipes |
| `serverStarted` | Vending-machine injection; BetterQuesting import when present |
| `loadComplete` | Core recipes; AE2Thing load-complete; BoxPlusPlus load-complete |
| client `loadComplete` | Rebuild dynamic Stellar Forge recipe views so client-side NEI can display them |

Lifecycle changes are cross-cutting. Check single-player, dedicated-server classloading, and whether a client needs a mirrored view without becoming authoritative.

## Subsystem routes

| Area | Primary route | Responsibility and notes |
| --- | --- | --- |
| Entrypoint | `ScienceNotCool.java`, `CommonProxy.java`, `ClientProxy.java` | FML lifecycle and side separation |
| Registries | `Loader/` | Materials, items, blocks, machines, renderers, quests, recipes, recipe maps |
| Machines | `Common/machines/` | GT meta-tile entities, hatches, single blocks, multiblocks |
| Machine IDs | `Loader/MachineLoader.java` and related ID declarations | IDs are save/progression compatibility data; never renumber casually |
| Recipe maps | `Loader/GTNCRecipeMaps.java` | Stable map identity, frontend/backend wiring, NEI behavior |
| Recipe loading | `Loader/RecipeLoader.java`, recipe pool packages | Static and runtime-derived recipes; respect lifecycle timing |
| Multiblock bases | `Common/machines/multiblock/multiMachineBase/` | Shared processing, steam, wireless energy, structure, and UI behavior |
| Wireless steam | `GTNCSteamMultiBlockBase.java`, `utils/event/SteamNetworkEventHandler.java` | Clean shared steam/ME I/O and owner-bound wireless-steam foundation |
| Wireless energy | `GTNCWirelessEnergyMultiMachineBase.java`, `GTNCWirelessBase.java` | Shared wireless power/accounting contracts |
| ME bridge | `Common/mebridge/` | Cross-dimensional ME channel/link data, visualization, persistence, packets |
| Pixel building | `Common/building/`, `Common/packet/building/` | Queued structure placement, preview, undo, synchronization, saved state |
| Teleport | `Common/teleport/` | Destination storage, server actions, dimension/world safety |
| Wildcard patterns | `Common/items/wildcard/` | Pattern configuration and server-synchronized item state |
| Toolbelt | `Common/items/toolbelt/` | Inventory wrappers, GUI, packets, and item NBT |
| Client research | `Client/research/` | Thaumcraft research UI behavior; client-only unless explicitly bridged |
| AE2Thing port | `ae2thing/` | Embedded AE2 terminal/network functionality and its own coremod/network layer |
| Quick terminal | `ae2thing/quickterminal/` | Large, recently refactored GUI and terminal behavior |
| BoxPlusPlus port | `com/silvermoon/boxplusplus/` | Imported integration with its own lifecycle/network/UI conventions |
| Mixins | `mixins/`, `Loader/LateMixinsLoader.java`, Mixin JSON resources | Internal hooks into AE2, GregTech, Forestry, Thaumcraft, EnderIO, CropsNH, TecTech, and others |
| Assets | `src/main/resources/assets/sciencenotcool/` | Textures, models/structures, quests, localization output |

## Shared base-class decision guide

Before adding or changing a multiblock, determine which existing contract owns the behavior:

- First ask the user to choose official/upstream versus GT-Not-Cool custom inheritance and confirm the exact base. Do not choose by inference.
- Read `docs/codex/MULTIBLOCK_PLAYBOOK.md` and inspect local plus exact-version upstream source before implementation.

- Use `GTNCMultiBlockBase` for the general project multiblock contract.
- Use `GTNCSteamMultiBlockBase` for the clean steam/ME I/O and wireless-steam foundation.
- Use `GTNCAdvancedSteamMultiBlockBase` for the existing large-steam family that also needs tier helpers,
  chip upgrades, cross-recipe processing, and its advanced GUI.
- Use `GTNCWirelessEnergyMultiMachineBase` or `GTNCWirelessBase` when the machine participates in established wireless-energy accounting.
- Inspect at least one concrete sibling with the same recipe, structure, hatch, and UI behavior before changing a base class.

A base-class edit can affect many machines. Search overrides and call sites, then validate representative subclasses rather than only the class that motivated the change.

## Data ownership and persistence boundaries

| State | Expected authority/storage | Required checks |
| --- | --- | --- |
| Machine inventory, progress, energy, steam | Server tile/machine state | UI sync, NBT compatibility, chunk reload |
| Wireless steam/global networks | Server world data plus lifecycle-managed caches | Save/load, world unload, switching saves in one JVM |
| ME bridge and wireless links | Server world/global manager with client visualization messages | Node validity, dimension/chunk lifecycle, disconnect/unload cleanup |
| Pixel building jobs and undo | Server world data/queues | Bounds, permissions, queue limits, save/restart, client preview separation |
| Teleport destinations | Server/player data and synchronized client view | Destination safety, dimension existence, permission and ownership |
| Wildcard/toolbelt/terminal settings | Item or container NBT, server authoritative | Held/container identity, slot validation, malformed packet handling |
| Quest progress/import | Server events and BetterQuesting data | Optional-mod guard, idempotence, existing-world behavior |

Static state that retains a `World`, `WorldSavedData`, grid, tile, player, or dimension identity must have an explicit invalidation path. A static `loaded` flag is unsafe across save changes unless reset and rebound.

## Network topology

There are multiple networking layers:

- The main `SimpleNetworkWrapper` is created under `ScienceNotCool.MODID` and populated by `Common/packet/NetWorkHandler.java`.
- AE2Thing maintains its own wrapper and registration path under `ae2thing/network/` and `ae2thing/loader/ChannelLoader.java`.
- BoxPlusPlus contains a separate imported integration path.

For every client-to-server message, review:

1. Decode bounds before allocation.
2. Total payload and collection limits.
3. Correct server-thread scheduling.
4. Player distance or explicit remote capability.
5. Dimension, chunk, tile, container, held-item, and coordinate validity.
6. Ownership, team, or permission checks.
7. Idempotence or rate limiting for expensive/world-mutating requests.

Packet discriminators are protocol compatibility. Register them explicitly and deterministically.

## Mixins and dependency coupling

The project has extensive internal-API coupling. A dependency update can compile yet fail at runtime because a target class, descriptor, field, ordinal, local capture, or call sequence moved.

Mixin change checklist:

1. Confirm the currently resolved target class/member, not an older source tree or README claim.
2. Prefer an accessor or public event/API when it meets the requirement.
3. Put the class in the correct early/normal/late group.
4. Update the matching JSON configuration.
5. For late Mixins, update `Loader/LateMixinsLoader.java` and guard the exact target mod ID.
6. Launch the relevant runtime and inspect Mixin application/injection logs.
7. Exercise both the changed behavior and a nearby unaffected path.

## Localization and resources

Translations are extracted from comments beside Java usage:

```java
// #tr gui.example.key
// # English text
// # zh_CN 中文文本
```

The Gradle task `preprocessLangInJavaFiles` generates language files under `assets/sciencenotcool/lang`. Edit the source comments, run resource processing, and inspect the generated diff. Existing duplicate-key override messages are baseline behavior. The current validator uses `String.format` with no arguments, so valid placeholder-bearing strings such as `%s` and `%d` can produce false format warnings.

Resource work should verify:

- namespace, path, spelling, and case;
- reference from the relevant item/block/renderer/GUI;
- texture grid, alpha, UV orientation, animation metadata, and in-world appearance;
- StructureLib `.mbs` orientation and controller offsets;
- quest JSON compatibility and optional-mod availability.

## Known risk register

These are audit findings, not proof of an active exploit or data-loss incident. Re-evaluate against current code before fixing.

### High: unbounded pixel-placement request

`Common/packet/building/MessagePlacePixels.java` decodes a client-supplied count and allocates four arrays from it. The audited server handler did not establish a strict payload bound or fully validate distance, generator identity/ownership, and coordinates before placement. Treat this as a multiplayer trust-boundary issue.

Expected remediation properties: hard decode limits before allocation, bounded aggregate work, correct player/item/tile identity, distance or permission checks, valid coordinates/dimension, and server-side queue limits.

### High: wireless steam saved-data binding may survive world changes

`GlobalSteamWorldSavedData` uses static loaded/bound state. At the audit point, its `sLoaded` flag was not visibly reset on world unload, unlike cleanup paths in the ME bridge and building subsystems. This may retain state when the same JVM switches saves.

Expected remediation properties: explicit unload/server-stop reset, rebinding to the current world's storage, and tests that open world A, unload, then open world B without restarting the JVM.

### High: AE2Thing packet IDs depend on JAR iteration order

`ae2thing/loader/ChannelLoader.java` scans classes, identifies handlers, and assigns sequential IDs in discovered order while swallowing exceptions. JAR/classpath enumeration order is not a stable protocol definition, and silent failure makes a mismatch difficult to diagnose.

Expected remediation properties: an explicit ordered registry with fixed IDs and sides, duplicate detection, startup logging, and hard failure on invalid registration.

### Structural: no automated tests

The project has no test sources. Compile, style, and resource tasks are useful gates but do not cover gameplay, networking, persistence, sided classloading, structures, NEI integration, or Mixin application.

### Structural: broad internal-API coupling

The Mixin surface spans AE2, GregTech, Forestry, Thaumcraft, EnderIO, CropsNH, TecTech, and other mods. Dependency bumps require targeted runtime validation rather than a compile-only confidence claim.

## Maintenance hotspots

Large or high-churn files deserve narrower edits and extra review:

- `Loader/MachineLoader.java` — roughly 4.6k lines.
- BoxPlusPlus `GTMachineBox` — roughly 2.4k lines.
- Quick-terminal GUI — roughly 2.1k lines.
- `WeightedResearchSolver` — roughly 1.7k lines.
- `TextLocalization` — roughly 1.7k lines.
- `AssemblerMatrix` — roughly 1.5k lines.

Recent architectural churn at the audit point included the quick-terminal refactor, the ME wireless transceiver/link manager, an EnderIO soul-vessel Mixin, Steam Godforge, and the shared steam multiblock base.

## Validation matrix

Use `.agents/skills/gt-not-cool-maintainer/references/validation.md` for commands and change-type coverage. At minimum:

- Java: compile plus repository checks.
- GUI/network: actual client/server interaction, not just single-player rendering.
- Persistence: old and new saves, reload, and same-JVM world switching.
- Mixins: application logs plus affected behavior.
- Machines/recipes: registration, structure, progression, and NEI/recipe visibility.
- Assets: resource processing plus visual/in-world inspection.

The deep onboarding baseline passed `compileJava`, `spotlessCheck`, `checkstyleMain`, `check`, and `git diff --check`. `check` reported `test NO-SOURCE`. No runtime log was available, so runtime behavior was not certified.

## Known documentation drift at audit time

- README and `AE2THING_PORT_MAINTENANCE.md` referenced AE2 977 and AE2FluidCraft 1.5.88, while `dependencies.gradle` used AE2 1000 and AE2FluidCraft 1.5.95.
- The AE2 port note stated 220 files, while the audited `ae2thing` subtree contained 154 files.
- The GTNH settings convention reported that 2.0.29 was available while the repository intentionally remained on 2.0.20. Do not update it as incidental cleanup.

## When to update this map

Update this file in the same change when modifying:

- lifecycle phase ownership;
- subsystem entry points or major directory structure;
- persistence owners, save-data names, or cleanup strategy;
- network channels or packet registries;
- foundational dependency pins;
- official upstream ownership or exact-version source routes;
- machine/recipe registration architecture;
- multiblock base families, inheritance-choice policy, or construction workflow;
- localization generation;
- validation commands or test coverage;
- known risks after they are confirmed, mitigated, or superseded.
