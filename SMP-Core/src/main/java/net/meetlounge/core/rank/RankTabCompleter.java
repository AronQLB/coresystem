package net.meetlounge.core.rank;

import net.meetlounge.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class RankTabCompleter implements TabCompleter {

    private final Core plugin;

    public RankTabCompleter(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            List<String> subCommands = List.of("info", "perm", "permission");
            if (args[0].isBlank() || subCommands.stream().anyMatch(value -> value.startsWith(args[0].toLowerCase()))) {
                return subCommands;
            }

            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .toList();
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .toList();
            }

            if (args[0].equalsIgnoreCase("perm") || args[0].equalsIgnoreCase("permission")) {
                return List.of("add", "remove", "list", "create", "edit", "setprefix");
            }

            return plugin.ranks().rankIds();
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("perm") || args[0].equalsIgnoreCase("permission"))) {
            if (args[1].equalsIgnoreCase("create")) {
                return List.of("<rang>");
            }

            return plugin.ranks().rankIds();
        }

        if (args.length == 4 && (args[0].equalsIgnoreCase("perm") || args[0].equalsIgnoreCase("permission"))) {
            if (args[1].equalsIgnoreCase("add")) {
                return plugin.permissions().customNodes();
            }

            if (args[1].equalsIgnoreCase("remove")) {
                return plugin.permissions().rankPermissions(args[2]);
            }
        }

        return List.of();
    }
}
