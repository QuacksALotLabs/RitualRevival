# Project: RitualRevival

## Overview
RitualRevival is a Minecraft plugin built exclusively against the **Paper API** (no Spigot/Bukkit-only support). It is the revival system: dead players are sent to a configurable limbo waiting area, and other players bring them back through a multiblock ritual (altar + soul orb). It does not store items itself — it queries SoulGraves (if installed) for grave data via the `graveId` and requests the items back on successful revival. Without SoulGraves it still runs as a standalone limbo/ritual plugin, but items drop normally (vanilla behavior).

## Core Requirements
- Intercept death flow: send dead players to a configurable limbo area (respawn location, gamemode, flight, invulnerability).
- Persist limbo state: `{UUID → (graveId, enteredAt)}` — survives restarts.
- Multiblock ritual detection: validate altar structure via `BlockPlaceEvent` / `PlayerInteractEvent`, checking relative block offsets against a configurable pattern.
- Orb validation: if SoulGraves is present, read `graveId` from the held/placed soul orb via `SoulGraveAPI.getGraveIdFromOrb(ItemStack)`.
- Ritual flow: optional countdown / channeling (cancels on movement or damage — configurable), boss bar + particles + sounds, then revival.
- On success: call `SoulGraveAPI.releaseGrave(graveId, deadPlayer)` (if SoulGraves present), teleport player back, set gamemode, restore health, clear limbo state.
- Fully configurable via `config.yml`: altar structure, limbo spawn, countdown duration, revival health, fallback behavior.

## Soft Dependency on SoulGraves
- `plugin.yml`: `softdepend: [SoulGraves, Nexo, ItemsAdder, Oraxen]`
- On `onEnable()`: check `Bukkit.getPluginManager().getPlugin("SoulGraves")` and set an internal `graveIntegrationEnabled` flag.
- All grave-related code paths (orb detection, `releaseGrave` calls) only run when the flag is `true`.
- Without SoulGraves: player is still sent to limbo and revived via ritual, but **items drop normally on death** — no gravestone, no soul orb, no item return.

## Communication with SoulGraves
- Access `SoulGraveAPI` via `ServicesManager` (`getRegistration(SoulGraveAPI.class).getProvider()`)
- Listen to `SoulGraveCreatedEvent` to move the player into limbo
- Never store items itself — always delegate to `releaseGrave(graveId, player)`

## Dependencies (server-installed)
| Plugin | Required? | Purpose |
|---|---|---|
| SoulGraves | Optional (`softdepend`) | Grave data, soul orb detection, item return. Without it: items drop normally, no grave/orb binding |
| Nexo / ItemsAdder / Oraxen | Optional (one of them) | Custom ritual blocks & orb item detection |

## Build Dependencies
- Paper API 1.21.x (`compileOnly`)
- Adventure MiniMessage (`compileOnly`)
- RevivalAPI (`common` module: `SoulGraveAPI` + `SoulGraveCreatedEvent`) (`compileOnly`, via ServicesManager)
- ItemUtils (`implementation`, shaded)
- BoostedYAML (`implementation`, shaded)
- CommandAPI for Paper (`implementation`, shaded)
- Jackson / JacksonPaper (`implementation`, shaded)
- MockBukkit + JUnit 5 (`testImplementation`)

## Permissions (`plugin.yml`)
- `ritualrevival.command.use` (default: true)
- `ritualrevival.admin` (default: op)
- `ritualrevival.ritual.perform` (default: true)

## CI/CD
GitHub Actions workflow (`.github/workflows/release.yml`):
- Trigger: `push` on tags matching `v[0-9]+.[0-9]+.0` (i.e. only `vX.X.0` releases, patch versions like `v1.0.1` are ignored)
- JDK 21 (Temurin), `chmod +x ./gradlew`, `./gradlew build` (or `shadowJar`)
- Create GitHub Release via `softprops/action-gh-release@v2`
- `generate_release_notes: true` — GitHub automatically generates a changelog summary from commits and PRs since the last release
- Upload `build/libs/*.jar` as release asset
- `permissions: contents: write` required for release creation

## License
GNU General Public License v3.0 (GPL-3.0). `LICENSE` file in repo root, copyright header in every `.java` file.

## Repo
https://github.com/QuacksALotLabs/RitualRevival