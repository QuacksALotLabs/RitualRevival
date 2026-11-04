# Project: RitualRevival

## Overview
RitualRevival is a Minecraft plugin built exclusively against the **Paper API** (no Spigot/Bukkit-only support). It is the revival system: dead players are sent to a configurable limbo waiting area, and other players bring them back through a multiblock ritual (altar + orb). It knows nothing about graves or items at its core. SoulGraves is an optional soft dependency.

## Core Requirements
- Send dead players to a configurable limbo area (respawn location, gamemode, flight, invulnerability).
- Persist limbo state: `{UUID → enteredAt}` — survives restarts.
- Multiblock ritual detection: validate altar structure via `BlockPlaceEvent` / `PlayerInteractEvent`, checking relative block offsets against a configurable pattern.
- Ritual flow: optional countdown / channeling (cancels on movement or damage — configurable), boss bar + particles + sounds, then revival.
- On success: teleport player back, set gamemode, restore health, clear limbo state.
- The item used to trigger the ritual is fully configurable (vanilla material or custom item via ItemUtils).
- Fully configurable via `config.yml`: altar structure, limbo spawn, countdown duration, revival health, ritual trigger item.

## Dependencies (server-installed)
| Plugin | Required? | Purpose |
|---|---|---|
| SoulGraves | Optional | Soul orb is found in the grave inventory |
| Nexo / ItemsAdder / Oraxen | Optional (one of them) | Custom ritual blocks & trigger item detection |

## Build Dependencies
- Paper API 1.21.x (`compileOnly`)
- Adventure MiniMessage (`compileOnly`)
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