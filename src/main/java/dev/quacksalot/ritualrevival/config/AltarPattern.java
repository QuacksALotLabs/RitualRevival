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
package dev.quacksalot.ritualrevival.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Parsed altar pattern. Offsets are relative to the altar "base".
 * Each block is identified by a Bukkit {@link Material} for now; swap for
 * an ItemUtils/Nexo/ItemsAdder/Oraxen resolver when custom items are needed.
 */
public final class AltarPattern {

    public record Block(Vector offset, Material material) {}

    private final Vector triggerOffset;
    private final List<Block> blocks;

    public AltarPattern(Vector triggerOffset, List<Block> blocks) {
        this.triggerOffset = triggerOffset;
        this.blocks = List.copyOf(blocks);
    }

    public static AltarPattern fromYaml(YamlDocument yaml) {
        Vector trigger = parseVector(yaml.getString("ritual.altar.trigger-offset", "0,1,0"));
        List<Block> blocks = new ArrayList<>();
        for (var map : yaml.getMapList("ritual.altar.blocks")) {
            Vector off = parseVector(String.valueOf(map.get("offset")));
            String mat = String.valueOf(map.get("material")).toUpperCase();
            blocks.add(new Block(off, Material.valueOf(mat)));
        }
        return new AltarPattern(trigger, blocks);
    }

    private static Vector parseVector(String raw) {
        String[] parts = raw.split(",");
        return new Vector(
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim()));
    }

    public Vector triggerOffset() { return triggerOffset; }
    public List<Block> blocks() { return blocks; }
}