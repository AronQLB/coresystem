package net.meetlounge.core.clan;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ClanTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 1) {
            return List.of("create", "info", "delete", "bank", "deposit", "withdraw",
                    "create",
                    "info",
                    "delete",
                    "bank",
                    "deposit",
                    "withdraw",
                    "invite",
                    "accept",
                    "deny",
                    "leave",
                    "kick",
                    "members",
                    "promote",
                    "demote",
                    "claim");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return List.of("Name");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return List.of("TAG");
        }

        if (args[0].equalsIgnoreCase("claim")) {
            return List.of("create", "delete", "show");
        }

        return List.of();
    }
}