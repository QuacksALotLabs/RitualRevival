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
package dev.quacksalot.ritualrevival;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.quacksalot.ritualrevival.command.RitualRevivalCommand;
import dev.quacksalot.ritualrevival.config.ConfigManager;
import dev.quacksalot.ritualrevival.limbo.LimboManager;
import dev.quacksalot.ritualrevival.listener.AltarListener;
import dev.quacksalot.ritualrevival.listener.DeathListener;
import dev.quacksalot.ritualrevival.listener.RespawnListener;
import dev.quacksalot.ritualrevival.listener.RitualCancelListener;
import dev.quacksalot.ritualrevival.ritual.RitualManager;
import dev.quacksalot.ritualrevival.util.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public final class RitualRevival extends JavaPlugin {

    private static RitualRevival instance;

    private ConfigManager configManager;
    private LimboManager limboManager;
    private RitualManager ritualManager;
    private Messages messages;

    public static RitualRevival get() {
        return instance;
    }

    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIPaperConfig(this));
    }

    @Override
    public void onEnable() {
        instance = this;
        CommandAPI.onEnable();

        // Config zuerst laden, weil alle anderen Manager sie brauchen.
        this.configManager = new ConfigManager(this);
        this.configManager.load();

        this.messages = new Messages(this);

        // Limbo-Manager laden (liest limbo-data.yml ein).
        this.limboManager = new LimboManager(this);
        this.limboManager.load();

        // Ritual-Manager (kein persistenter State).
        this.ritualManager = new RitualManager(this);

        // Listener registrieren.
        var pm = getServer().getPluginManager();
        pm.registerEvents(new DeathListener(this), this);
        pm.registerEvents(new RespawnListener(this), this);
        pm.registerEvents(new AltarListener(this), this);
        pm.registerEvents(new RitualCancelListener(this), this);

        // Befehle registrieren – erst nach CommandAPI.onEnable().
        new RitualRevivalCommand(this).register();

        getLogger().info("RitualRevival enabled.");
    }

    @Override
    public void onDisable() {
        if (ritualManager != null) {
            ritualManager.cancelAll();
        }
        if (limboManager != null) {
            limboManager.save();
        }
        CommandAPI.onDisable();
    }

    // ── Getter ───────────────────────────────────────────────────────────

    public ConfigManager config()   { return configManager; }
    public LimboManager limbo()     { return limboManager; }
    public RitualManager rituals()  { return ritualManager; }
    public Messages messages()      { return messages; }
}