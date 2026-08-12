# Wireless Dual Interface Terminal refactor

## Copy and adapt

- `GuiQuickEncodingTerminal`: use the GTNH-Qol-Improvements three-panel layout (ME storage / interface terminal / pattern encoder).
- `ContainerQuickEncodingTerminal`: retain AE2's native pattern terminal and interface-terminal delegates instead of the old AE2Things monitor/container stack.
- `DualTerminalGuiObject`, `RecipeTransferPayload`, and `InterfacePatternTarget`: provide the wireless host, synchronized recipe transfer, and direct pattern placement target.
- Four referenced Modernity dark GUI textures under `assets/sciencenotcool/textures/gui/quick_terminal/`; the
  upstream 3x3, inverted and unused pattern-panel variants are intentionally omitted. The resource pack's global AE2
  `states.png` override is intentionally not bundled with the mod.
- Preserve the upstream GPL-3.0 license and attribution in `THIRD_PARTY_NOTICES.md` and `META-INF/licenses/`.

## GT-Not-Cool integration adjustments

- Open the new host through a dedicated item GUI factory while retaining the old item inventory host for existing sub-GUIs during migration.
- Re-encode GT-Not-Cool's Baubles slot offset to AE2's slot offset before constructing the wireless GUI object.
- Add an 18x18 button below the central interface-terminal controls to switch to the wireless crafting terminal.
- Keep the saved last-view behavior handled by `CPacketSwitchGuis`.
- Keep GT-Not-Cool's NEI recipe-name suggestion rules, including the custom input-assembly interface suffix and configured circuit/meta suffix behavior.
- Keep automatic crafting-vs-processing detection; processing has one fixed 16-input + 16-output layout, rendered as
  two complete 4x4 grids with no 3x3 mode, inverted view, scrollbar, or hidden page.
- Preserve an untouched NBT backup when a pre-refactor terminal still contains data in processing slots 17-32.
- Keep the processing-pattern "combine duplicate inputs" and "prioritize fluids" controls; Shift-clicking the
  priority control sorts the current 4x4 input grid without changing the saved preference.
- Migrate the old `combine` and misspelled `priorization` item NBT settings without deleting the legacy tags.
- Register recipe transfer for the new GUI without removing the old handler until feature parity is verified.
- Close the embedded interface-terminal delegate when the combined container closes.

## Delete only after validation

- The old `GuiWirelessDualInterfaceTerminal` / `GuiBaseInterfaceWireless` implementation and its panel widgets.
- The old `ContainerWirelessDualInterfaceTerminal` and monitor/container support classes used only by that screen.
- Packets, adapters, mixins, and textures whose reference graph becomes empty after the new main GUI and NEI path are active.
- Keep shared wireless power/range handling, crafting-terminal switching, item NBT migration, sub-GUIs, and custom machine/interface naming code.

## Validation gates

1. `git diff --check`
2. `gradlew.bat compileJava`
3. `gradlew.bat processResources`
4. `gradlew.bat check`
5. In-game checks: inventory and Baubles opening, both switch directions, interface list updates, NEI transfer/name fill, combine/fluid-priority behavior, encode/direct upload, close/reopen, power/range cards, and all retained sub-GUIs.
