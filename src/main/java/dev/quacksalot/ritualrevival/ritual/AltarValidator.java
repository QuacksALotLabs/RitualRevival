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

import dev.quacksalot.ritualrevival.config.AltarPattern;
import org.bukkit.Location;
import org.bukkit.block.Block;

public final class AltarValidator {

    private AltarValidator() {}

    /**
     * Validates the altar assuming {@code base} is the offset (0,0,0) block.
     * @return the matched trigger block, or null if invalid.
     */
    public static Block validate(Location base, AltarPattern pattern) {
        for (var b : pattern.blocks()) {
            var off = b.offset();
            Block world = base.getWorld().getBlockAt(
                    base.getBlockX() + off.getBlockX(),
                    base.getBlockY() + off.getBlockY(),
                    base.getBlockZ() + off.getBlockZ());
            if (world.getType() != b.material()) return null;
        }
        var trig = pattern.triggerOffset();
        return base.getWorld().getBlockAt(
                base.getBlockX() + trig.getBlockX(),
                base.getBlockY() + trig.getBlockY(),
                base.getBlockZ() + trig.getBlockZ());
    }

    /** Derive the base from a clicked trigger block. */
    public static Location baseFromTrigger(Block triggerBlock, AltarPattern pattern) {
        var t = pattern.triggerOffset();
        return new Location(triggerBlock.getWorld(),
                triggerBlock.getX() - t.getBlockX(),
                triggerBlock.getY() - t.getBlockY(),
                triggerBlock.getZ() - t.getBlockZ());
    }
}