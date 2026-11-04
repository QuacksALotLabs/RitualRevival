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
import dev.quacksalot.ritualrevival.ritual.RitualSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class RitualCancelListener implements Listener {

    private final RitualRevival plugin;
    public RitualCancelListener(RitualRevival plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.config().cancelOnMove()) return;
        RitualSession s = plugin.rituals().activeFor(event.getPlayer().getUniqueId());
        if (s == null) return;

        var from = s.startLocation();
        var to = event.getTo();
        if (from.getWorld() != to.getWorld()) { plugin.rituals().cancel(s, "ritual-cancel-move"); return; }

        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        double dz = from.getZ() - to.getZ();
        if (Math.sqrt(dx * dx + dy * dy + dz * dz) > plugin.config().moveThreshold()) {
            plugin.rituals().cancel(s, "ritual-cancel-move");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!plugin.config().cancelOnDamage()) return;
        if (!(event.getEntity() instanceof Player p)) return;

        RitualSession s = plugin.rituals().activeFor(p.getUniqueId());
        if (s != null) plugin.rituals().cancel(s, "ritual-cancel-damage");

        // Protect limbo players
        if (plugin.limbo().has(p.getUniqueId()) && plugin.config().limboInvulnerable()) {
            event.setCancelled(true);
        }
    }
}