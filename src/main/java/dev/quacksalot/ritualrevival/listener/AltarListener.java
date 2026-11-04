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
import dev.quacksalot.ritualrevival.ritual.AltarValidator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AltarListener implements Listener {

    private final RitualRevival plugin;
    public AltarListener(RitualRevival plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("ritualrevival.ritual.perform")) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null || event.getItem() == null) return;

        Material trigger = Material.valueOf(
                plugin.config().yaml().getString("ritual.trigger-item", "ECHO_SHARD"));
        if (event.getItem().getType() != trigger) return;

        Location base = AltarValidator.baseFromTrigger(clicked, plugin.config().altar());
        Block validated = AltarValidator.validate(base, plugin.config().altar());
        if (validated == null || !validated.equals(clicked)) {
            player.sendMessage(plugin.messages().get("invalid-altar"));
            event.setCancelled(true);
            return;
        }

        String mode = plugin.config().yaml().getString("ritual.revive.mode", "OLDEST");
        Set<UUID> targets = new HashSet<>();
        if ("ALL".equalsIgnoreCase(mode)) {
            targets.addAll(plugin.limbo().snapshot().keySet());
        } else {
            UUID oldest = plugin.limbo().oldest();
            if (oldest != null) targets.add(oldest);
        }

        if (targets.isEmpty()) {
            player.sendMessage(plugin.messages().get("no-one-in-limbo"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        plugin.rituals().start(player, targets, base);
    }
}