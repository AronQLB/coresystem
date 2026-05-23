package net.meetlounge.core.command;

import net.meetlounge.core.Core;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public final class CommandManager {

    private static final String COMMANDS_PATH = "commands.";

    private final Core plugin;

    public CommandManager(Core plugin) {
        this.plugin = plugin;
    }

    public void register(String name, AbstractCommand command) {
        if (!isEnabled(name)) {
            plugin.getLogger().info("Command deaktiviert: " + name);
            return;
        }

        PluginCommand pluginCommand = plugin.getCommand(name);

        if (pluginCommand == null) {
            plugin.getLogger().severe("Command nicht in plugin.yml gefunden: " + name);
            return;
        }

        pluginCommand.setExecutor(command);
    }

    public void register(String name, AbstractCommand command, TabCompleter tabCompleter) {
        if (!isEnabled(name)) {
            plugin.getLogger().info("Command deaktiviert: " + name);
            return;
        }

        PluginCommand pluginCommand = plugin.getCommand(name);

        if (pluginCommand == null) {
            plugin.getLogger().severe("Command nicht in plugin.yml gefunden: " + name);
            return;
        }

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(tabCompleter);
    }

    private boolean isEnabled(String name) {
        return plugin.configs().config().get().getBoolean(COMMANDS_PATH + name + ".enabled", true);
    }
}
