# GTNH Upstream Source Map

This file routes GT-Not-Cool work to the correct GT New Horizons upstream source. It is an index, not a vendored source mirror. Last verified: 2026-08-14.

## Contents

- Source precedence and version-resolution workflow
- Foundation repositories
- Core API and integration routing
- Audited version-to-tag map
- Task-to-source decision guides
- Progression, reuse, and update rules

## Source precedence

Use upstream evidence in this order:

1. `dependencies.gradle` for versions requested and forced by this project.
2. Gradle `dependencyInsight` for the version actually selected after transitive conflict resolution.
3. The owning repository at the exact matching release tag.
4. The resolved development JAR/source JAR when a tag is missing or generated artifacts differ from source layout.
5. The owning repository's default branch only to understand newer direction, never as proof that an API exists in this project.
6. The modpack repository, official quest data, and NewHorizonsCoreMod for pack composition and progression context.

Never infer availability from another Minecraft version, a non-GTNH fork, an old maintenance note, or the latest upstream branch.

## Version-resolution workflow

Before changing an integration, Mixin target, dependency, or build convention:

1. Find the dependency declaration and any forced version:

   ```powershell
   rg -n "ArtifactName|force" dependencies.gradle
   ```

2. Resolve transitive versions when the artifact is not directly pinned or can be overridden:

   ```powershell
   .\gradlew.bat dependencyInsight --dependency ArtifactName --configuration runtimeClasspath --no-daemon
   ```

3. Open the owning repository at the exact tag from the tables below. If that tag is absent, inspect the resolved artifact under the Gradle module cache and record the evidence used.
4. Search the exact class, member descriptor, recipe map, event, packet, or GUI API in that version.
5. For a Mixin, also confirm runtime names, injection point, ordinal/local capture, side, and target-mod guard.
6. For a dependency update, compare old and new tags and revalidate all direct calls and Mixins targeting that dependency.

Do not add cloned upstream repositories, source dumps, decompiled classes, or Gradle caches to this repository. Temporary shallow clones may be created outside the workspace when needed.

## Foundation repositories

| Upstream | Authority for | How GT-Not-Cool should use it |
| --- | --- | --- |
| [GTNewHorizons organization](https://github.com/GTNewHorizons) | Canonical organization namespace | Confirm that an upstream is organization-owned before treating it as GTNH official |
| [ExampleMod1.7.10](https://github.com/GTNewHorizons/ExampleMod1.7.10) | GTNHGradle layout, dependency configuration, Mixins, access transformers, publishing conventions | Use for build-system patterns; preserve this repository's pinned convention version unless an upgrade is requested |
| [GT-New-Horizons-Modpack](https://github.com/GTNewHorizons/GT-New-Horizons-Modpack) | Pack composition, configuration, scripts, and release context | Use with the installed pack version; do not treat its latest branch as the user's runtime baseline |
| [NewHorizonsCoreMod](https://github.com/GTNewHorizons/NewHorizonsCoreMod) | Pack-specific items, recipes, materials, coins, and integration content | Check for progression-facing private content and optional runtime availability; it is compile-only in this project and is not a reliable local trimmed-runtime validation target |
| [GTNH-Translations](https://github.com/GTNewHorizons/GTNH-Translations) | Official translation coordination | Consult for upstream terminology; keep this project's Java-comment generation workflow authoritative for its own keys |

## Core API and implementation routing

| Change area | Owning upstream | Inspect for |
| --- | --- | --- |
| GregTech machines, hatches, recipe processing, overclocking, energy, materials | [GT5-Unofficial](https://github.com/GTNewHorizons/GT5-Unofficial) | MTE base contracts, recipe maps/backends, processing logic, saved tags, UI hooks, internal fields targeted by Mixins |
| Structure definitions and survival construction | [StructureLib](https://github.com/GTNewHorizons/StructureLib) | Structure elements, channels, hints, offsets, alignment, build/check contracts |
| ModularUI2 panels, widgets, sync values, interaction handlers | [ModularUI2](https://github.com/GTNewHorizons/ModularUI2) | Server/client sync semantics, panel/container lifecycle, widget behavior, version-specific signatures |
| Shared utilities, networking helpers, configuration, rendering helpers | [GTNHLib](https://github.com/GTNewHorizons/GTNHLib) | Public utilities and runtime compatibility before writing a local duplicate |
| AE2 grids, channels, crafting CPUs, interfaces, terminals | [Applied-Energistics-2-Unofficial](https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial) | Grid lifecycle, node/channel contracts, crafting internals, terminal/container APIs, Mixin targets |
| AE2 fluids and dual interfaces | [AE2FluidCraft-Rework](https://github.com/GTNewHorizons/AE2FluidCraft-Rework) | Fluid packet/storage APIs, duality extensions, fluid crafting integration, version coupling with AE2 |
| NEI handlers, overlays, recipe display | [NotEnoughItems](https://github.com/GTNewHorizons/NotEnoughItems) | Handler lifecycle, overlay/container APIs, recipe visibility and client-only boundaries |

## Integration repositories used by this project

| Integration | Official repository | Main GT-Not-Cool concern |
| --- | --- | --- |
| CropsNH | [CropsNH](https://github.com/GTNewHorizons/CropsNH) | Crop tile/seed Mixins and optional gameplay behavior |
| Ender IO | [EnderIO](https://github.com/GTNewHorizons/EnderIO) | Conduit, inventory, soul vessel, powered-machine, and time-acceleration Mixins |
| Botania | [Botania](https://github.com/GTNewHorizons/Botania) | Required integration objects and recipes |
| Forestry | [ForestryMC](https://github.com/GTNewHorizons/ForestryMC) | Bee genome, mutation, hive, and species Mixins |
| BetterQuesting | [BetterQuesting](https://github.com/GTNewHorizons/BetterQuesting) | Optional quest import, progress events, and data compatibility |
| TecTech | [TecTech](https://github.com/GTNewHorizons/TecTech) | Endgame machine/energy conventions and indirect internal coupling; first confirm whether it is present in the resolved graph |

Other artifacts in `dependencies.gradle` follow the same owner-and-exact-version rule. Add them to this map when they become a recurring development surface or receive a direct Mixin/API integration.

## Audited version-to-tag map

The following versions were requested or resolved by this repository during the 2026-08-14 audit, and matching official tags were verified. Re-run the workflow above after dependency changes.

| Artifact | Audited version | Exact tagged source |
| --- | --- | --- |
| GT5-Unofficial | `5.09.54.20` | [tag](https://github.com/GTNewHorizons/GT5-Unofficial/tree/5.09.54.20) |
| Applied-Energistics-2-Unofficial | `rv3-beta-1000-GTNH` | [tag](https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/tree/rv3-beta-1000-GTNH) |
| AE2FluidCraft-Rework | `1.5.95-gtnh` | [tag](https://github.com/GTNewHorizons/AE2FluidCraft-Rework/tree/1.5.95-gtnh) |
| GTNHLib | `0.11.24` | [tag](https://github.com/GTNewHorizons/GTNHLib/tree/0.11.24) |
| NotEnoughItems | `2.8.111-GTNH` | [tag](https://github.com/GTNewHorizons/NotEnoughItems/tree/2.8.111-GTNH) |
| ModularUI2 | `2.3.79-1.7.10` resolved transitively | [tag](https://github.com/GTNewHorizons/ModularUI2/tree/2.3.79-1.7.10) |
| StructureLib | `1.4.42` resolved transitively | [tag](https://github.com/GTNewHorizons/StructureLib/tree/1.4.42) |
| CropsNH | `2.0.91` | [tag](https://github.com/GTNewHorizons/CropsNH/tree/2.0.91) |
| EnderIO | `2.10.32` | [tag](https://github.com/GTNewHorizons/EnderIO/tree/2.10.32) |
| Botania | `1.13.25-GTNH` | [tag](https://github.com/GTNewHorizons/Botania/tree/1.13.25-GTNH) |
| BetterQuesting | `3.8.70-GTNH` | [tag](https://github.com/GTNewHorizons/BetterQuesting/tree/3.8.70-GTNH) |
| ForestryMC | `4.11.31` | [tag](https://github.com/GTNewHorizons/ForestryMC/tree/4.11.31) |
| NewHorizonsCoreMod | `2.9.5` | [tag](https://github.com/GTNewHorizons/NewHorizonsCoreMod/tree/2.9.5) |

`dependencies.gradle` remains authoritative if any row becomes stale. Transitive versions can change without a direct edit when a parent dependency changes.

## Task-to-source decision guide

### Adding or changing a GregTech machine

1. For a multiblock, read `MULTIBLOCK_PLAYBOOK.md`, inspect candidates read-only, and obtain user confirmation of official/upstream versus GT-Not-Cool custom inheritance plus the exact base class.
2. Inspect the nearest GT-Not-Cool sibling and selected shared base class.
3. Inspect GT5-Unofficial at the exact pinned tag for the inherited contract and at least one upstream concrete example.
4. Inspect StructureLib for structure behavior and ModularUI2 for GUI behavior when applicable.
5. Use the modpack and official quest data to establish the intended tier and progression impact.

### Changing AE2 or AE2Thing behavior

1. Determine whether the owner is base AE2, AE2FluidCraft, or the embedded GT-Not-Cool AE2Thing port.
2. Read both exact AE2 and AE2FluidCraft tags when a dual interface, fluid pattern, or crafting path crosses their boundary.
3. Treat crafting CPU, grid cache, node/channel, duality, and container internals as version-sensitive Mixin surfaces.
4. Verify client and dedicated-server behavior with the same dependency set.

### Changing a ModularUI2 GUI

1. Resolve the actual ModularUI2 version because it is currently transitive.
2. Inspect its exact tag for panel construction, container binding, sync values, and interaction semantics.
3. Keep gameplay mutations server-authoritative and validate close/reopen, reconnect, and stale-container behavior.

### Updating a dependency

1. Confirm the user explicitly authorized adopting an unambiguous target. An available update or impact-analysis request is read-only.
2. Read `UPGRADE_PLAYBOOK.md` and record requested and resolved old versions.
3. Confirm the intended new version belongs to the GTNH fork and matches the target pack.
4. Compare exact tags and search this repository for the changed API surface.
5. Audit Mixins, access transformers, reflection, serialized formats, packets, and optional-mod guards.
6. Run compile/check, then the full relevant runtime matrix.
7. Update this map and `PROJECT_MAP.md` if foundational versions or coupling change.

## Progression and private-content evidence

Use sources in this order for balance decisions:

1. The user's explicit design intent.
2. Official GTNH quest data through the installed `gtnh-skill`.
3. The target release of the modpack repository and NewHorizonsCoreMod.
4. The owning mod's exact source tag.
5. The wiki for explanation and tutorials, with version drift treated cautiously.

Clearly label intentional deviations. Do not silently bypass a GT tier, quest gate, required material, or infrastructure step merely because an API makes it possible.

## Code and asset reuse

- Prefer calling a public API or adapting a small pattern over copying an implementation.
- Check the exact upstream license before copying substantial code, data, textures, sounds, or other assets.
- Preserve notices and attribution required by the source license.
- Do not copy a newer implementation into an older tagged API without reviewing every dependency it assumes.
- Record the upstream repository and tag in code comments or maintenance documentation when a non-obvious compatibility workaround is derived from it.

## Update triggers

Update this file when:

- a direct or forced dependency version changes;
- a transitive foundational dependency such as ModularUI2 or StructureLib changes;
- a new recurring official integration is added;
- a Mixin changes target repository, class, descriptor, or lifecycle group;
- the target GTNH pack release changes;
- an upstream repository is renamed, archived, transferred, or replaced;
- a documented source route proves insufficient or misleading during real work.

The existence of a newer upstream release does not by itself update this baseline. Refresh version rows only when the project actually adopts a new authorized target or when correcting an audit error.
