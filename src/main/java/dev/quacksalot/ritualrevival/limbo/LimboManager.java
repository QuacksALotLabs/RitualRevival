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
package dev.quacksalot.ritualrevival.limbo;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LimboManager {

    private final JavaPlugin plugin;
    private final Map<UUID, LimboState> states = new ConcurrentHashMap<>();
    private File file;

    public LimboManager(JavaPlugin plugin) { this.plugin = plugin; }

    public void load() {
        file = new File(plugin.getDataFolder(), "limbo-data.yml");
        if (!file.exists()) return;
        try {
            YamlDocument doc = YamlDocument.create(file);
            for (String key : doc.getRoutesAsStrings(false)) {
                UUID id = UUID.fromString(key);
                long enteredAt = doc.getLong(key + ".enteredAt", System.currentTimeMillis());
                boolean pending = doc.getBoolean(key + ".pendingRevival", false);
                states.put(id, new LimboState(enteredAt, pending));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void save() {
        try {
            if (!file.exists()) { file.getParentFile().mkdirs(); file.createNewFile(); }
            YamlDocument doc = YamlDocument.create(file);
            for (var e : states.entrySet()) {
                String k = e.getKey().toString();
                doc.set(k + ".enteredAt", e.getValue().enteredAt());
                doc.set(k + ".pendingRevival", e.getValue().pendingRevival());
            }
            doc.save();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void put(UUID uuid) {
        states.put(uuid, new LimboState(System.currentTimeMillis(), false));
        save();
    }

    public boolean has(UUID uuid) { return states.containsKey(uuid); }
    public LimboState get(UUID uuid) { return states.get(uuid); }
    public LimboState remove(UUID uuid) { LimboState s = states.remove(uuid); save(); return s; }
    public boolean isPendingRevival(UUID uuid) {
        LimboState s = states.get(uuid);
        return s != null && s.pendingRevival();
    }
    public void markPendingRevival(UUID uuid) {
        LimboState s = states.get(uuid);
        if (s != null) { states.put(uuid, s.withPendingRevival(true)); save(); }
    }

    /** Ältester Limbo-Spieler (FIFO). */
    public UUID oldest() {
        return states.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().enteredAt()))
                .map(Map.Entry::getKey).orElse(null);
    }

    public Map<UUID, LimboState> snapshot() { return Map.copyOf(states); }
}