package net.meetlounge.core.tab;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.command.CoreCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class CoreTabCompleter implements TabCompleter {

    private final Core plugin;
    private final CoreCommand coreCommand;

    public CoreTabCompleter(Core plugin, CoreCommand coreCommand) {
        this.plugin = plugin;
        this.coreCommand = coreCommand;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        return coreCommand.tabComplete(new CommandContext(plugin, sender, label, args));
    }
}