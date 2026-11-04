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
Same GitHub Actions pattern as SoulGraves:
- Trigger: `push` on tags matching `v*`
- JDK 21 (Temurin), `./gradlew build` (or `shadowJar`)
- GitHub Release via `softprops/action-gh-release@v2`, upload `build/libs/*.jar`

## License
GNU General Public License v3.0 (GPL-3.0). `LICENSE` file in repo root, copyright header in every `.java` file.

## Repo
https://github.com/QuacksALotLabs/RitualRevival