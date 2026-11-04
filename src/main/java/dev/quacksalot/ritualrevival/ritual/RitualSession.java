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

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;
import java.util.UUID;

public final class RitualSession {

    private final UUID performerId;
    private final Set<UUID> targets;
    private final Location altarLocation;
    private final Location startLocation;
    private final long startTimeMs;

    private BukkitTask task;
    private BossBar bossBar;   // ← jetzt Adventure-BossBar
    private int ticksElapsed;

    public RitualSession(UUID performerId, Set<UUID> targets,
                         Location altarLocation, Location startLocation) {
        this.performerId = performerId;
        this.targets = Set.copyOf(targets);
        this.altarLocation = altarLocation;
        this.startLocation = startLocation;
        this.startTimeMs = System.currentTimeMillis();
    }

    public UUID performerId() { return performerId; }
    public Set<UUID> targets() { return targets; }
    public Location altarLocation() { return altarLocation; }
    public Location startLocation() { return startLocation; }
    public long startTimeMs() { return startTimeMs; }

    public BukkitTask task() { return task; }
    public void task(BukkitTask t) { this.task = t; }

    public BossBar bossBar() { return bossBar; }
    public void bossBar(BossBar b) { this.bossBar = b; }

    public int ticksElapsed() { return ticksElapsed; }
    public void addTick() { ticksElapsed++; }
}