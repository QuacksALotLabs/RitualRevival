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
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public final class ConfigManager {

    private final JavaPlugin plugin;
    private YamlDocument yaml;

    private Location limboSpawn;
    private GameMode limboGameMode;
    private boolean limboAllowFlight;
    private boolean limboInvulnerable;

    private AltarPattern altarPattern;

    private boolean countdownEnabled;
    private int countdownSeconds;
    private boolean cancelOnMove;
    private boolean cancelOnDamage;
    private double moveThreshold;

    private double reviveHealth;
    private GameMode reviveGameMode;
    private boolean reviveTeleportToAltar;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            this.yaml = YamlDocument.create(
                    new File(plugin.getDataFolder(), "config.yml"),
                    plugin.getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load config.yml", e);
        }

        World world = Bukkit.getWorld(yaml.getString("limbo.spawn.world", "world"));
        if (world == null) world = Bukkit.getWorlds().get(0);

        this.limboSpawn = new Location(world,
                yaml.getDouble("limbo.spawn.x", 0.5),
                yaml.getDouble("limbo.spawn.y", 100.0),
                yaml.getDouble("limbo.spawn.z", 0.5),
                yaml.getDouble("limbo.spawn.yaw", 0.0).floatValue(),
                yaml.getDouble("limbo.spawn.pitch", 0.0).floatValue());
        this.limboGameMode = GameMode.valueOf(yaml.getString("limbo.gamemode", "ADVENTURE").toUpperCase());
        this.limboAllowFlight = yaml.getBoolean("limbo.allow-flight", true);
        this.limboInvulnerable = yaml.getBoolean("limbo.invulnerable", true);

        this.altarPattern = AltarPattern.fromYaml(yaml);

        this.countdownEnabled = yaml.getBoolean("ritual.countdown.enabled", true);
        this.countdownSeconds  = yaml.getInt("ritual.countdown.duration-seconds", 10);
        this.cancelOnMove      = yaml.getBoolean("ritual.countdown.cancel-on-move", true);
        this.cancelOnDamage    = yaml.getBoolean("ritual.countdown.cancel-on-damage", true);
        this.moveThreshold     = yaml.getDouble("ritual.countdown.move-threshold", 0.3);

        this.reviveHealth = yaml.getDouble("ritual.revive.health", 20.0);
        this.reviveGameMode = GameMode.valueOf(yaml.getString("ritual.revive.gamemode", "SURVIVAL").toUpperCase());
        this.reviveTeleportToAltar = yaml.getBoolean("ritual.revive.teleport-to-altar", true);
    }

    public YamlDocument yaml() { return yaml; }
    public Location limboSpawn() { return limboSpawn; }
    public GameMode limboGameMode() { return limboGameMode; }
    public boolean limboAllowFlight() { return limboAllowFlight; }
    public boolean limboInvulnerable() { return limboInvulnerable; }
    public AltarPattern altar() { return altarPattern; }
    public boolean countdownEnabled() { return countdownEnabled; }
    public int countdownSeconds() { return countdownSeconds; }
    public boolean cancelOnMove() { return cancelOnMove; }
    public boolean cancelOnDamage() { return cancelOnDamage; }
    public double moveThreshold() { return moveThreshold; }
    public double reviveHealth() { return reviveHealth; }
    public GameMode reviveGameMode() { return reviveGameMode; }
    public boolean reviveTeleportToAltar() { return reviveTeleportToAltar; }
}