---
name: gt-not-cool-maintainer
description: Maintain, extend, debug, review, and validate the GT-Not-Cool GTNH 1.7.10 addon. Use for its machines, recipes, ModularUI2 GUIs, AE2 integrations, wireless networks, Mixins, quests, textures, localization, persistence, packets, dependency updates, or build configuration. Apply version upgrades only after an explicit user request with a target. For every multiblock task, require user confirmation of official/upstream versus GT-Not-Cool custom inheritance and the exact base before mutation; do not use for unrelated repositories.
---

# GT-Not-Cool Maintainer

## Start with repository context

1. Read the repository-root `AGENTS.md` completely.
2. Read `../../../docs/codex/PROJECT_MAP.md` for cross-cutting, unfamiliar, networking, persistence, lifecycle, integration, or Mixin tasks.
3. Read `../../../docs/codex/UPSTREAMS.md` for dependency, external API, Mixin target, build-convention, or progression-facing tasks.
4. Read the nearest implementation, its callers, and at least one sibling with matching behavior.
5. Re-read `dependencies.gradle` and resolve transitive conflicts before relying on external class names, descriptors, fields, or APIs.

Treat executable code and current build files as authoritative when documentation has drifted.

## Gate version upgrades

- Do not mutate dependency pins, build conventions, integration code, Mixins, or migration logic because a newer version exists.
- Treat update checks, comparisons, warnings, and impact questions as read-only.
- Require an explicit instruction to upgrade plus an unambiguous target release, version, or manifest. If the upgrade intent is explicit but the target is missing, perform read-only discovery and ask for the target before editing.
- After authorization, read `../../../docs/codex/UPGRADE_PLAYBOOK.md` and limit work to the requested target and required compatibility changes.
- Require separate authorization for breaking save/protocol decisions, commits, pushes, tags, publishing, or releases.

## Route the task

- For every multiblock task, read `../../../docs/codex/MULTIBLOCK_PLAYBOOK.md`. Inspect local and exact-version upstream source read-only, then ask the user to choose official/upstream versus GT-Not-Cool custom inheritance and confirm the exact base before any mutation. Reconfirm a named base; for existing machines ask whether to retain the current base.

- Route lifecycle and registration work through `ScienceNotCool`, `CommonProxy`, `ClientProxy`, and `Loader/`.
- Route machine and recipe work through `Common/machines/`, `MachineLoader`, `RecipeLoader`, and `GTNCRecipeMaps`.
- Route AE cross-dimensional/wireless work through `Common/mebridge/`; route the embedded terminal port through `ae2thing/`.
- Route structure placement through `Common/building/`; route teleport, wildcard, and toolbelt state through their matching `Common/` packages.
- Route imported BoxPlusPlus behavior through `com/silvermoon/boxplusplus/` without silently replacing its local conventions.
- Route client-only research, renderers, keybindings, and overlays behind client-only entry points.
- Route integration hooks through the exact tagged owning upstream, then prefer public APIs and use the appropriate early/normal/late Mixin set only when necessary.

## Establish authority and compatibility

Before editing, state internally:

- Which FML lifecycle phase owns registration or initialization?
- Is the code common, client-only, or server-only?
- Does the server own the mutable state and does the client only request/render it?
- Is the state transient, item/tile/player NBT, or world-level `WorldSavedData`?
- Which IDs, NBT keys, packet discriminators, save names, recipe maps, or resource keys are compatibility boundaries?
- Is the target mod required or optional, and can its classes load safely when absent?

Never trust client-supplied counts, arrays, coordinates, dimensions, tiles, containers, or item identity. Bound before allocating, validate permissions and reach, and mutate on the server thread.

## Implement narrowly

- Preserve Java 8 runtime compatibility even when using Jabel syntax.
- Follow the nearest established GTNH/GregTech/AE2/ModularUI2 pattern.
- Avoid full scans in tick paths; cache and invalidate deliberately.
- Reset world-bound static caches on unload or server stop.
- Keep packet registration explicit and deterministic.
- Guard optional integrations and keep client classes out of common paths.
- Use source translation markers beside Java usage. Do not hand-edit generated `.lang` files.
- Use `ScienceNotCool.MODID` or `RESOURCE_ROOT_ID` for new resource references.
- Preserve unrelated worktree changes and avoid incidental dependency/build upgrades.

## Validate and report

Read `references/validation.md`, select the rows matching the change, and run the smallest sufficient set plus any required runtime checks. Start with `git diff --check`; for Java, normally run `compileJava` and `check`.

Do not equate `test NO-SOURCE` with behavior coverage. State which compile/style/resource gates passed and which client, dedicated-server, multiplayer, persistence, Mixin, NEI, structure, or visual checks remain unverified.

## References

- Repository architecture and risk register: `../../../docs/codex/PROJECT_MAP.md`
- Official GTNH upstream and exact-version routing: `../../../docs/codex/UPSTREAMS.md`
- Explicitly authorized GTNH/dependency migrations: `../../../docs/codex/UPGRADE_PLAYBOOK.md`
- Multiblock design, source inspection, construction, and validation: `../../../docs/codex/MULTIBLOCK_PLAYBOOK.md`
- Change-type validation commands: `references/validation.md`
