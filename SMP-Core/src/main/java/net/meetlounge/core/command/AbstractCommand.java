package net.meetlounge.core.command;

import net.meetlounge.core.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements CommandExecutor {

    protected final Core plugin;

    protected AbstractCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(new CommandContext(plugin, sender, label, args));
        return true;
    }

    protected abstract void execute(CommandContext context);
}