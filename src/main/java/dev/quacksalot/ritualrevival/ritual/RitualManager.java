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
package dev.quacksalot.ritualrevival.ritual;

import dev.quacksalot.ritualrevival.RitualRevival;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RitualManager {

    private final RitualRevival plugin;
    private final Map<UUID, RitualSession> byPerformer = new ConcurrentHashMap<>();

    public RitualManager(RitualRevival plugin) { this.plugin = plugin; }

    public boolean isRunning(Location altar) {
        return byPerformer.values().stream().anyMatch(s -> s.altarLocation().equals(altar));
    }

    public RitualSession activeFor(UUID performer) { return byPerformer.get(performer); }

    public void start(Player performer, Set<UUID> targets, Location altarLocation) {
        if (isRunning(altarLocation)) {
            performer.sendMessage(plugin.messages().get("already-running"));
            return;
        }

        var session = new RitualSession(performer.getUniqueId(), targets,
                altarLocation, performer.getLocation());
        byPerformer.put(performer.getUniqueId(), session);
        performer.sendMessage(plugin.messages().get("ritual-start"));

        if (!plugin.config().countdownEnabled()) {
            complete(session);
            return;
        }

        int totalTicks = plugin.config().countdownSeconds() * 20;

        BossBar bar = BossBar.bossBar(
                MiniMessage.miniMessage().deserialize("<gold>Channeling..."),
                1.0f,
                BossBar.Color.PURPLE,
                BossBar.Overlay.PROGRESS);
        performer.showBossBar(bar);
        session.bossBar(bar);

        session.task(new BukkitRunnable() {
            @Override public void run() {
                if (!byPerformer.containsKey(performer.getUniqueId())) { cancel(); return; }

                session.addTick();
                double progress = 1.0 - (session.ticksElapsed() / (double) totalTicks);
                bar.progress((float) Math.max(0.0, progress));   // ← geändert

                Location c = altarLocation.clone().add(0.5, 1.0, 0.5);
                for (int i = 0; i < 2; i++) {
                    double angle = (session.ticksElapsed() * 0.3) + (i * Math.PI);
                    c.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,
                            c.clone().add(Math.cos(angle) * 0.8, 0, Math.sin(angle) * 0.8),
                            1, 0, 0, 0, 0);
                }
                if (session.ticksElapsed() % 10 == 0)
                    c.getWorld().playSound(c, Sound.BLOCK_SOUL_SAND_BREAK, 0.6f, 0.8f);

                if (session.ticksElapsed() >= totalTicks) {
                    cancel();
                    complete(session);
                }
            }
        }.runTaskTimer(plugin, 1L, 1L));
    }

    public void cancel(RitualSession session, String reasonMessageKey) {
        byPerformer.remove(session.performerId());
        if (session.task() != null) session.task().cancel();

        Player performer = Bukkit.getPlayer(session.performerId());
        if (performer != null) {
            if (session.bossBar() != null) performer.hideBossBar(session.bossBar());  // ← geändert
            if (reasonMessageKey != null)
                performer.sendMessage(plugin.messages().get(reasonMessageKey));
        }
    }

    public void cancelAll() { byPerformer.values().forEach(s -> cancel(s, null)); }

    private void complete(RitualSession session) {
        Location dest = session.altarLocation().clone().add(0.5, 1.0, 0.5);

        for (UUID targetId : session.targets()) {
            Player target = Bukkit.getPlayer(targetId);
            if (target == null) {
                plugin.limbo().markPendingRevival(targetId);
                continue;
            }
            if (!plugin.limbo().has(targetId)) continue;

            Location where = plugin.config().reviveTeleportToAltar()
                    ? dest : target.getLocation();
            target.teleport(where);
            target.setGameMode(plugin.config().reviveGameMode());
            target.setHealth(Math.min(plugin.config().reviveHealth(), target.getMaxHealth()));
            target.setFireTicks(0);
            target.setFoodLevel(20);
            target.setInvulnerable(false);
            target.setAllowFlight(false);
            target.setFlying(false);
            plugin.limbo().remove(targetId);

            where.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, where, 30, 0.6, 0.6, 0.6, 0.1);
            where.getWorld().playSound(where, Sound.ITEM_TOTEM_USE, 1f, 1f);

            String msg = plugin.config().yaml()
                    .getString("messages.ritual-success", "<green><player> has been revived!")
                    .replace("<player>", target.getName());
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(msg));
        }

        cancel(session, null);
    }
}