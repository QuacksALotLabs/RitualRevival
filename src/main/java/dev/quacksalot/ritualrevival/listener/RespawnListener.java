/*
 * RitualRevival - Revive dead players through a multiblock ritual.
 * Copyright (C) 2025 QuacksALotLabs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.quacksalot.ritualrevival.listener;

import dev.quacksalot.ritualrevival.RitualRevival;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class RespawnListener implements Listener {

    private final RitualRevival plugin;
    public RespawnListener(RitualRevival plugin) { this.plugin = plugin; }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (!plugin.limbo().has(p.getUniqueId())) return;

        event.setRespawnLocation(plugin.config().limboSpawn());

        Bukkit.getScheduler().runTask(plugin, () -> {
            p.setGameMode(plugin.config().limboGameMode());
            p.setAllowFlight(plugin.config().limboAllowFlight());
            p.setFlying(plugin.config().limboAllowFlight());
            if (plugin.config().limboInvulnerable()) p.setInvulnerable(true);
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        var state = plugin.limbo().get(p.getUniqueId());
        if (state == null) return;

        if (state.pendingRevival()) {
            // A ritual completed while the player was offline.
            p.setGameMode(plugin.config().reviveGameMode());
            p.setHealth(Math.min(plugin.config().reviveHealth(), p.getMaxHealth()));
            p.setFireTicks(0);
            plugin.limbo().remove(p.getUniqueId());
            return;
        }

        // Still in limbo: enforce the environment.
        Bukkit.getScheduler().runTask(plugin, () -> {
            p.teleport(plugin.config().limboSpawn());
            p.setGameMode(GameMode.valueOf(plugin.config().limboGameMode().name()));
            p.setAllowFlight(plugin.config().limboAllowFlight());
            p.setFlying(plugin.config().limboAllowFlight());
            if (plugin.config().limboInvulnerable()) p.setInvulnerable(true);
        });
    }
}