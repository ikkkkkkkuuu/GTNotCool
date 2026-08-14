# GTNH Upgrade Playbook

Use this playbook only after the user explicitly authorizes a GTNH pack or dependency upgrade. It governs migration work for GT-Not-Cool; it is not an automatic update policy.

## Contents

- Authorization gate
- Target and baseline lock
- Version-diff inventory
- Migration order
- Compatibility audit
- Validation gates
- Stop, rollback, and completion rules

## Authorization gate

### Mutation is allowed only after an explicit upgrade request

An upgrade mutation requires clear language directing the project to adopt a target, for example:

- “把项目升级到 GTNH 2.x.y。”
- “将 GT5-Unofficial 升级到 5.xx.xx.xx，并处理兼容问题。”
- “同步到这份目标整合包清单。”
- “Upgrade this addon to the dependency versions used by release X.”

The request must identify a target GTNH release, dependency version, manifest, or other unambiguous baseline. If the user clearly requests an upgrade but omits the target, perform read-only discovery and ask for the target before editing.

The following are not authorization to mutate:

- “GTNH 有新版本吗？”
- “看看上游更新了什么。”
- “这个项目是否落后了？”
- “升级会有什么影响？”
- “当 GTNH 更新时怎么办？”
- A Gradle, IDE, GitHub, or build warning that a newer version exists.

For those requests, inspect and report only. Do not edit dependency pins, build scripts, source, Mixins, resources, save migrations, or documentation baselines. Do not run `updateBuildScript` merely because Gradle advertises it.

### Scope does not expand automatically

- A request to upgrade one dependency authorizes that dependency and only the coupled changes required to make it compatible.
- A request to upgrade the full GTNH target authorizes alignment to that target release, not arbitrary latest versions.
- An upgrade request does not authorize publishing, pushing, releasing, deleting saves, rewriting history, or changing gameplay balance unless separately requested.
- Keep optional integrations optional unless the target pack makes them mandatory and the user accepts that change.

## Target and baseline lock

Before editing, record:

| Field | Required evidence |
| --- | --- |
| Current project revision | Branch, HEAD, and worktree status |
| Current declared versions | `dependencies.gradle`, forced versions, settings convention |
| Current resolved versions | Gradle `dependencyInsight` for direct and foundational transitive artifacts |
| Target baseline | Exact GTNH release, dependency tag, or supplied manifest |
| Official ownership | Repository listed in `UPSTREAMS.md` or otherwise verified under the owning project |
| Runtime baseline | Target client/server mod list and configuration, when available |
| Save baseline | A disposable copy of an old save plus a new-world test plan |

Use read-only commands first:

```powershell
git status --short --branch
rg -n "implementation|compileOnlyApi|devOnlyNonPublishable|runtimeOnlyNonPublishable|force" dependencies.gradle
.\gradlew.bat dependencies --configuration runtimeClasspath --no-daemon
```

Never discard unrelated worktree changes. Do not use a production-only save as the migration test fixture.

## Build the version-diff inventory

Create a reviewable table before changing source:

| Component | Old requested | Old resolved | Target | Official tags/evidence | Coupled project area | Status |
| --- | --- | --- | --- | --- | --- | --- |
| Example | `x` | `x` | `y` | old tag → new tag | Mixins/API/recipes | pending |

Include:

- directly declared dependencies;
- forced versions;
- foundational transitive dependencies, especially GTNHLib, StructureLib, ModularUI2, UniMixins, CodeChicken components, and recipe/GUI libraries;
- required runtime-only or development-only integrations;
- the GTNH settings/build convention when the target baseline requires it;
- target-mod IDs and versions used by late Mixins;
- pack-specific content from NewHorizonsCoreMod and BetterQuesting when progression is affected.

Compare exact old and new tags. The latest default branch is useful for discovery but is not a migration baseline.

## Generate the impact map

For each changed component, search this repository for:

- imported packages and direct API calls;
- superclass and interface contracts;
- Mixins, accessors, invokers, shadows, overwrites, injection descriptors, ordinals, and local capture;
- access transformers and reflection strings;
- packet registration and serialized payload assumptions;
- NBT keys, `WorldSavedData` names, machine IDs, recipe-map identity, item-list bindings, and resource keys;
- optional-mod guards and eagerly loaded integration classes;
- recipes, quests, NEI handlers, structures, textures, and localization tied to renamed content.

Classify every hit as:

- compile-time API compatibility;
- runtime classloading or Mixin compatibility;
- network protocol compatibility;
- save/world compatibility;
- registration identity compatibility;
- progression or balance compatibility;
- client-only visual compatibility.

## Migration order

Align the complete target dependency set so Gradle can resolve it coherently, then repair and validate in layers:

1. Build convention, Forge/Java assumptions, mappings, Mixins infrastructure, and access transformers when the target requires them.
2. Foundation libraries: GTNHLib, StructureLib, ModularUI2, UniMixins, and shared rendering/network helpers.
3. GregTech and its machine, material, recipe, structure, and UI contracts.
4. AE2 and AE2FluidCraft as a coupled pair; then repair the embedded AE2Thing and ME bridge surfaces.
5. Direct integration mods such as EnderIO, Forestry, CropsNH, Botania, Thaumcraft-related addons, and BetterQuesting.
6. NewHorizonsCoreMod content, private recipes, quest integration, NEI display, and progression alignment.
7. Documentation baselines and the exact-tag map only after the resolved graph is stable.

Do not mix unrelated cleanup, redesign, or balance changes into the migration. Keep compatibility shims narrow and document why they exist.

## Compatibility audit

### Build and dependency graph

- Confirm every requested artifact resolves from the intended repository.
- Review conflict resolution; a successful download does not prove the selected transitive version is correct.
- Confirm Java 8 runtime compatibility and Jabel/build-convention expectations.
- Do not accept a build-script migration generated for another template version without reviewing the diff.

### Public APIs and lifecycle

- Re-check registration phases, event buses, logical sides, and client-only class references.
- Prefer newly available public APIs over retaining a brittle Mixin, but do not refactor beyond the authorized migration scope.
- Check method descriptors and generic/return-type changes, not only class names.

### Mixins

For every dependency with a targeted Mixin:

1. Verify the target class exists in the new exact version.
2. Verify field/method descriptors, injection points, ordinals, slices, and local capture.
3. Verify the Mixin JSON group and late-loader mod guard.
4. Compile with the new refmap.
5. Inspect runtime application logs; compilation alone is insufficient.
6. Exercise both the modified behavior and a nearby unaffected path.

### Save and protocol compatibility

- Preserve machine IDs, metadata, item-list bindings, NBT keys/types, `WorldSavedData` names, packet discriminators, recipe-map IDs, and resource keys.
- If a target version forces a breaking schema change, stop and obtain explicit approval for the migration behavior.
- Read old data and write the new schema; do not make old saves silently unloadable.
- Keep client and server protocol versions aligned and reject malformed or obsolete requests safely.

### Gameplay and progression

- Compare removed/renamed items, fluids, materials, recipes, quest gates, and machine tiers.
- Use official quest data and the target pack manifest as evidence.
- Report intentional private-content deviations; do not silently bypass new progression gates.

## Validation gates

Do not skip directly from compilation to completion.

### Gate 1: dependency resolution

```powershell
.\gradlew.bat dependencies --configuration runtimeClasspath --no-daemon
```

Confirm there are no unresolved artifacts or unintended conflict winners. Run focused `dependencyInsight` for every foundational or suspicious component.

### Gate 2: compilation and repository checks

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat check --no-daemon
git diff --check
```

Treat `test NO-SOURCE` as absence of automated behavior coverage.

### Gate 3: release build

```powershell
.\gradlew.bat build --no-daemon
```

Inspect the produced artifact, bundled metadata, Mixin JSON, refmap, access transformers, and accidental dependency/resource inclusion.

### Gate 4: client runtime

- Start with the target dependency set.
- Inspect loading and Mixin logs.
- Verify main menus/registries, NEI, affected GUIs, textures, structures, recipes, machines, and client-only integrations.

### Gate 5: dedicated server and multiplayer

- Start a dedicated server with the target pack set.
- Join with a matching client.
- Exercise packets, containers, ME/wireless networks, dimensions, chunk unload/reload, permissions, and disconnect/reconnect behavior.
- Confirm common code does not load client-only classes.

### Gate 6: persistence

- Create and reload a new target-version world.
- Load a disposable copy of an old-version world.
- Save, restart, and reload.
- Switch from world A to world B in the same JVM when static caches or world data are involved.
- Verify machines, item NBT, global networks, quests, building queues, and teleport destinations.

### Gate 7: progression and integration

- Confirm recipe visibility and execution, tier requirements, quest prerequisites, optional-mod behavior, and pack-specific items.
- Test one valid and one rejected path for changed machines, structures, packets, and GUIs.

## Stop conditions

Stop mutation and request direction when:

- the target version or manifest is ambiguous;
- the required official artifact/tag cannot be established;
- the target requires a Minecraft, Forge, or Java runtime change beyond the request;
- save compatibility would break without a user-owned migration decision;
- an optional integration would have to become required;
- the target runtime pack is unavailable for required validation;
- unrelated worktree changes overlap the migration surface;
- an upstream license blocks the intended code or asset reuse.

Report the exact blocker and any safe read-only findings. Do not silently choose a different target version.

## Completion and handoff

An upgrade is complete only when:

- requested and resolved versions match the authorized target;
- all known compile errors and runtime Mixin/classloading failures are resolved;
- relevant client, dedicated-server, multiplayer, and save tests are reported;
- compatibility breaks and unverified scenarios are explicit;
- `UPSTREAMS.md` exact-version rows are refreshed;
- `PROJECT_MAP.md` is updated when architecture, lifecycle, coupling, risks, or validation changed;
- stale maintenance docs are corrected or clearly marked;
- temporary upstream clones and generated audit artifacts remain outside version control.

Do not commit, push, tag, publish, or release unless the user separately requests it.
