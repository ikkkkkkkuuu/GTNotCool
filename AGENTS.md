# GT-Not-Cool Repository Guidance

## Scope and source of truth

- This repository is a Minecraft 1.7.10 / Forge 10.13.4.1614 GT New Horizons addon.
- The mod ID is `sciencenotcool`; the root Java package is `com.xyp.gtnc`.
- Modern syntax is provided by Jabel, but output must remain Java 8 compatible. Do not use newer JDK library APIs unless the build explicitly provides a compatible substitute.
- Treat `gradle.properties`, `dependencies.gradle`, the lifecycle code, and the current source tree as authoritative. README files and maintenance notes can lag behind dependency upgrades.
- Read `docs/codex/PROJECT_MAP.md` before broad, cross-cutting, persistence, networking, Mixin, or lifecycle work. Read `docs/codex/UPSTREAMS.md` before integration, dependency, API, build-convention, or upstream-derived work. Update them when those facts change.
- Preserve unrelated user changes. Do not edit generated/build/runtime state such as `build/`, `run/`, `.gradle/`, `.gradle-codex/`, `.idea/`, or temporary files.

## Implementation workflow

1. Locate the nearest existing implementation and follow its local conventions before introducing a new abstraction.
2. Classify the change by lifecycle phase, logical side, data owner, persistence boundary, and optional-mod dependency.
3. Make the smallest coherent change that preserves machine IDs, NBT keys, packet discriminators, save-data names, recipe-map identity, and resource keys unless a migration is part of the task.
4. Validate in proportion to risk using the matrix in `.agents/skills/gt-not-cool-maintainer/references/validation.md`.
5. Report what was verified and what still requires an actual client, server, multiplayer session, or save migration test.

## Upgrade authorization gate

- Do not change dependency versions, the GTNH target baseline, build conventions, integration APIs, Mixins, or migration code merely because an upstream release or Gradle update is available.
- Upgrade mutations require an explicit user instruction to upgrade and an unambiguous target GTNH release, dependency version, or supplied manifest.
- Requests to check for updates, compare versions, assess impact, or explain an upgrade are read-only. If an upgrade is requested without a target, perform read-only discovery and ask for the target before editing.
- Once explicitly authorized, read and follow `docs/codex/UPGRADE_PLAYBOOK.md`. Keep the change limited to the requested target and required compatibility work.
- An upgrade request does not authorize committing, pushing, publishing, releasing, deleting saves, or accepting a breaking save/protocol migration without separate approval.

## Multiblock base-class gate

- Before any multiblock Java, structure, registration, recipe, GUI, localization, or asset mutation, ask the user whether to inherit an official/upstream base or a GT-Not-Cool custom base, then obtain confirmation of the exact base class.
- Never infer or silently choose the base from a similar machine. Even when the request names a base, explicitly reconfirm it before editing. For an existing machine, state its current base and ask whether to retain it.
- Read-only inspection of local and exact-version upstream source is allowed before confirmation so the question can include concrete candidates and tradeoffs.
- After confirmation, follow `docs/codex/MULTIBLOCK_PLAYBOOK.md`. Changing the selected base or moving behavior into a shared base requires renewed user confirmation.

## Lifecycle and architecture

- `ScienceNotCool` delegates FML lifecycle events to `CommonProxy` or `ClientProxy`.
- Register materials, items, blocks, entities, event handlers, channels, and integrations in the same lifecycle phase as adjacent code.
- Machine registration is centralized in `Loader/MachineLoader.java`; recipe registration and dynamic recipe generation are centered in `Loader/RecipeLoader.java` and `Loader/GTNCRecipeMaps.java`.
- Common/server-safe code belongs outside client-only packages. Never reference Minecraft client classes from common or dedicated-server paths.
- Client-only renderers, keybindings, overlays, and GUI integrations belong behind `ClientProxy`, a client-side event handler, or an equivalent side guard.
- Prefer GTNH, GregTech, AE2, StructureLib, and ModularUI2 public APIs. Use Mixins only when the required hook is not exposed.

## Server authority and networking

- The server owns mutable gameplay state. Clients may request an action or render synchronized state, but must not be the authority for inventories, energy, steam, links, structures, teleport destinations, quest progress, or world edits.
- In ModularUI2 handlers, mutate gameplay state only on the server; button handlers normally guard with `if (!mouseData.isClient())`.
- Do not call `PanelSyncManager.getPlayer()` while a panel is being constructed. Capture or synchronize the necessary context through the supported UI pattern.
- Treat every client-to-server packet as untrusted. Bound counts and allocation sizes before allocating, validate dimensions and coordinates, validate distance or remote-access capability, verify the referenced item/tile/container, and check ownership/permission where relevant.
- Keep packet registration deterministic. Do not derive discriminators from filesystem, classpath, reflection, or JAR iteration order.
- Schedule packet-side world mutations onto the correct game thread when the networking API requires it.

## Persistence and world lifecycle

- Use `WorldSavedData` for world-owned global state and NBT for item, tile, and player-owned state.
- Static caches and singleton managers that reference a world or its saved data must be rebound per world and reset on world unload/server stop. Test switching saves in the same JVM.
- Preserve serialized names and tag types. When changing a schema, read old data, write the new form, and document the migration.
- Teleport, chunk, dimension, and structure-placement code must validate that the world exists, chunks can be used safely, and the destination or placement is valid before mutating state.

## Performance

- Do not perform full world, registry, AE topology, inventory, structure, or recipe scans every tick.
- Cache expensive results and invalidate them on the narrowest reliable event. Bound queues and per-tick work for building or network propagation.
- Avoid allocations and logging in hot tick paths unless measured and justified.

## Integrations and Mixins

- Use `docs/codex/UPSTREAMS.md` to locate the owning official repository. Check the requested and Gradle-resolved dependency version, then inspect the exact matching tag or resolved artifact before changing an integration.
- Never use an upstream default branch as proof that an API exists in this project's pinned dependency set.
- Guard optional integrations with mod-loaded checks and keep optional classes out of eagerly loaded common paths.
- A new or renamed Mixin must be reflected in the correct Mixin JSON and, for late Mixins, in `Loader/LateMixinsLoader.java` with an exact target-mod guard.
- Compile success is not enough for Mixin work. Verify injection/application logs and exercise the affected behavior in the appropriate runtime.

## Localization and assets

- Use `ScienceNotCool.MODID` or `ScienceNotCool.RESOURCE_ROOT_ID`; do not duplicate the resource namespace as a string in new code.
- Source translations live beside their Java usage in this form:

  ```java
  // #tr gui.example.key
  // # English text
  // # zh_CN 中文文本
  ```

- Do not hand-edit generated files under `src/main/resources/assets/sciencenotcool/lang/`. Regenerate them through Gradle and inspect the resulting diff.
- Put resources under `src/main/resources/assets/sciencenotcool` and match the key/path/case used by code.
- Keep Minecraft textures on native integer pixel grids, normally 16x16 or 32x32, and verify orientation, alpha, animation metadata, and in-world rendering.

## Validation baseline

- Fast source check: `git diff --check`.
- Java change: `.\gradlew.bat compileJava --no-daemon` followed by `.\gradlew.bat check --no-daemon`.
- Resource/localization change: `.\gradlew.bat processResources --no-daemon` and inspect generated resources.
- Release-sensitive change: `.\gradlew.bat build --no-daemon` plus relevant client/server runtime coverage.
- The repository currently has no automated test source set; a successful `check` may report `test NO-SOURCE`. Do not describe that as gameplay validation.
- Translation preprocessing currently reports duplicate-key overrides and can report false format warnings for valid `%s`/`%d` placeholders. Investigate new warnings, but distinguish them from known baseline noise.
