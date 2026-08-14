# GT-Not-Cool Validation Matrix

Run commands from the repository root on Windows PowerShell. Select every row touched by the change; the rows are cumulative, not alternatives.

## Universal checks

```powershell
git status --short
git diff --check
```

Inspect the final diff for accidental generated files, unrelated user changes, unstable IDs, and secrets. Do not clean or revert user-owned changes.

## Documentation or repository guidance only

Required:

```powershell
git diff --check
```

For the repository skill, also run:

```powershell
$skillCreator = Join-Path $env:USERPROFILE ".codex\skills\.system\skill-creator"
python "$skillCreator\scripts\quick_validate.py" ".agents\skills\gt-not-cool-maintainer"
```

Use a real Python installation with `PyYAML`, not the Microsoft Store command alias. If the available Python lacks `PyYAML`, install it into a temporary target and expose that target through `PYTHONPATH`; do not add validator dependencies to this repository.

## Java source

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat check --no-daemon
```

`check` currently includes style/static gates but no behavior tests because `src/test` does not exist. Review new warnings separately from known baseline output.

## Localization or resources

```powershell
.\gradlew.bat processResources --no-daemon
.\gradlew.bat check --no-daemon
git status --short
git diff -- src/main/resources/assets/sciencenotcool/lang
```

Edit `// #tr` source comments, not generated `.lang` files. Known baseline noise includes duplicate-key override messages and false format warnings for placeholder-bearing strings such as `%s` or `%d`; investigate warnings introduced by the change.

For textures, models, GUIs, StructureLib blueprints, or animation metadata, visually inspect the result in the actual client. Confirm namespace, path, case, alpha, UV orientation, controller offset, facing, and pixel grid.

## ModularUI2 GUI or container state

Run Java checks, then verify in an actual client/server session:

- open, interact, close, and reopen the GUI;
- confirm mutations execute server-side only;
- confirm the server rejects invalid clicks, slots, amounts, or stale containers;
- confirm values synchronize after reconnect or chunk reload;
- check scaling, localization, disabled states, tooltips, and NEI overlap;
- repeat on a dedicated server if common/client classloading or packet flow changed.

## Networking

Run Java checks and verify both endpoints use the same build. Review every changed client-to-server packet for:

- a hard payload/count bound before allocation;
- aggregate work and queue limits;
- correct game-thread scheduling;
- player reach or explicit remote-access authorization;
- dimension, chunk, coordinate, tile, container, slot, and held-item validity;
- ownership/team/permission checks;
- deterministic fixed discriminators and correct logical side;
- useful failure logging without log spam.

Exercise malformed and repeated requests where practical. A single-player success is insufficient for a multiplayer trust-boundary change.

## WorldSavedData, NBT, caches, or migration

Run Java checks, then test:

1. Create and save new data.
2. Reload the same world.
3. Load an older save/item/tile representation if compatibility is relevant.
4. Unload world A and open world B in the same JVM.
5. Restart the server and reconnect clients.
6. Confirm dirty marking, cleanup, missing/corrupt-data handling, and schema defaults.

Do not rename saved-data identifiers, NBT keys, or tag types without an explicit migration.

## Mixins or dependency updates

Read `docs/codex/UPSTREAMS.md`. Confirm both the requested dependency version and the version selected by `dependencyInsight`, then inspect the exact matching upstream tag or resolved artifact.

Do not mutate versions unless the user explicitly authorized a target. For an authorized GTNH or dependency migration, follow every applicable gate in `docs/codex/UPGRADE_PLAYBOOK.md`; the checks below are only the compile/static subset.

Run:

```powershell
.\gradlew.bat compileJava --no-daemon
.\gradlew.bat check --no-daemon
```

Then launch the relevant environment and inspect the log for Mixin discovery, target resolution, injection success, refmap problems, and classloading errors. Exercise the target behavior and an adjacent unaffected behavior.

For late Mixins, verify the class appears in the correct JSON and in `Loader/LateMixinsLoader.java` behind the exact target-mod guard. For dependency updates, re-check every Mixin and direct internal-API call aimed at that dependency.

## Machines, structures, or recipes

For a multiblock, confirm the task record names the user-approved base family and exact class, the local sibling inspected, and the exact-version upstream source inspected. Follow `docs/codex/MULTIBLOCK_PLAYBOOK.md` in addition to this summary.

Run Java checks, then verify:

- machine IDs and `GTNCItemList` bindings remain stable and unique;
- registration occurs in the correct lifecycle phase;
- recipe maps, catalysts, frontends, and NEI views are present;
- dynamic server recipes have the required client-side view where appropriate;
- structure orientation, controller offset, hatches, tiers, casing counts, and construction hints work;
- power/steam accounting, overclocking, parallelism, input consumption, outputs, shutdown, and recipe locking match a sibling implementation;
- save/reload and chunk unload do not duplicate work or lose state;
- progression and optional-mod availability remain coherent.

Test at least one success path and one rejected/invalid structure or recipe path.

## Release-sensitive changes

```powershell
.\gradlew.bat build --no-daemon
```

Also perform every relevant runtime row above. Inspect the produced artifact and start a clean client and, when server/common code changed, a dedicated server with the intended modpack dependency set.

## Reporting

Report commands and outcomes precisely. Separate:

- compile/style/resource validation that passed;
- runtime scenarios exercised;
- known baseline warnings;
- remaining runtime, multiplayer, save-migration, integration, visual, or performance uncertainty.
