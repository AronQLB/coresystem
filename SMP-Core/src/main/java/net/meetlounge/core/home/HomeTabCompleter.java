package net.meetlounge.core.home;

import net.meetlounge.core.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class HomeTabCompleter implements TabCompleter {

    private final Core plugin;

    public HomeTabCompleter(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!(sender instanceof Player player)) {
            return List.of();
        }

        if (args.length == 1) {
            return plugin.homes().list(player);
        }

        return List.of();
    }
}