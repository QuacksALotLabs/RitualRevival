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
package dev.quacksalot.ritualrevival.util;

import dev.quacksalot.ritualrevival.RitualRevival;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Messages {

    private final RitualRevival plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public Messages(RitualRevival plugin) { this.plugin = plugin; }

    public Component get(String key) {
        String raw = plugin.config().yaml().getString("messages." + key, "<red>" + key);
        return mm.deserialize(raw);
    }
}