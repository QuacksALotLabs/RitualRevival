<img src="icon.svg" width="150" alt="Icon" align="left">

### RitualRivival

![Paper Plugin](https://img.shields.io/badge/api-Paper-c77dff?labelColor=9b4dca&style=flat-square&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZD0iTTIgMTJMMjIgMkwxMiAyMkwxMCAxM0wyIDEyWiIgZmlsbD0iI2ZmZmZmZiIvPjxwYXRoIGQ9Ik0xMCAxM0wyMiAyIiBzdHJva2U9IiMwMDAwMDAiIHN0cm9rZS13aWR0aD0iMC44IiBvcGFjaXR5PSIwLjMiLz48L3N2Zz4=)
![SoulGraves Version](https://img.shields.io/github/v/tag/QuacksALotLabs/SoulGraves?label=Version&style=flat-square&color=00c8d4&labelColor=008a9c)

is the revival system of your project: it sends dead players to a configurable limbo waiting area and lets other players bring them back through a multiblock ritual (altar + soul orb). It does not store items itself; instead, it retrieves a player's grave data via the graveId and returns their items upon successful revival. Limbo spawn, ritual structure, and countdown are fully configurable, and the plugin can run standalone or as part of the larger death-to-revival chain.

<br clear="left">

## Plugin Dependencies

| Plugin | Required? | Purpose |
|---|---|---|
| **SoulGraves** | Optional | Grave data, soul orb detection, item return. Without it: items drop normally, no grave/orb binding |
| **Nexo** / **ItemsAdder** / **Oraxen** | Optional (one of them) | Custom ritual blocks & orb item detection |