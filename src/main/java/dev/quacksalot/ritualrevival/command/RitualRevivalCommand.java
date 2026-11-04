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
package dev.quacksalot.ritualrevival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.quacksalot.ritualrevival.RitualRevival;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RitualRevivalCommand {

    private final RitualRevival plugin;
    public RitualRevivalCommand(RitualRevival plugin) { this.plugin = plugin; }

    public void register() {
        new CommandAPICommand("ritualrevival")
                .withAliases("rr")
                .withPermission("ritualrevival.command.use")
                .withSubcommand(new CommandAPICommand("reload")
                        .withPermission("ritualrevival.admin")
                        .executes(this::reload))
                .withSubcommand(new CommandAPICommand("status")
                        .withPermission("ritualrevival.admin")
                        .executes(this::status))
                .withSubcommand(new CommandAPICommand("list")
                        .withPermission("ritualrevival.admin")
                        .executes(this::list))
                .withSubcommand(new CommandAPICommand("revive")
                        .withPermission("ritualrevival.admin")
                        .withArguments(new EntitySelectorArgument.OnePlayer("player")
                                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                                        plugin.limbo().snapshot().keySet().stream()
                                                .map(id -> Bukkit.getOfflinePlayer(id).getName())
                                                .filter(n -> n != null)
                                                .toArray(String[]::new))))
                        .executes(this::revive))
                .register();
    }

    private void reload(CommandSender sender, CommandArguments args) {
        plugin.config().load();
        sender.sendMessage(plugin.messages().get("reloaded"));
    }

    private void status(CommandSender sender, CommandArguments args) {
        sender.sendMessage("Players in limbo: " + plugin.limbo().snapshot().size());
    }

    private void list(CommandSender sender, CommandArguments args) {
        var snap = plugin.limbo().snapshot();
        if (snap.isEmpty()) { sender.sendMessage("Nobody in limbo."); return; }
        snap.forEach((id, state) ->
                sender.sendMessage(" - " + Bukkit.getOfflinePlayer(id).getName()
                        + " since " + new java.util.Date(state.enteredAt())));
    }

    private void revive(CommandSender sender, CommandArguments args) {
        Player target = (Player) args.get("player");
        if (target == null || !plugin.limbo().has(target.getUniqueId())) {
            sender.sendMessage(plugin.messages().get("not-in-limbo"));
            return;
        }
        plugin.limbo().remove(target.getUniqueId());
        target.setGameMode(plugin.config().reviveGameMode());
        target.setHealth(Math.min(plugin.config().reviveHealth(), target.getMaxHealth()));
        target.setFireTicks(0);
        target.setInvulnerable(false);
        target.setAllowFlight(false);
        target.setFlying(false);
        target.sendMessage(plugin.messages().get("revived-by-command")
                .replaceText(b -> b.matchLiteral("<player>").replacement(sender.getName())));
    }
}