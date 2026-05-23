package net.meetlounge.core.command;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.sub.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CoreCommand extends AbstractCommand {

    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final List<String> helpLines = List.of(
            "/core reload",
            "/core save",
            "/core debug",
            "/core maintenance <on|off|toggle|status>",
            "/core player <name>",
            "/core rank <player> <rank>",
            "/core rank perm add <rank> <permission>",
            "/core rank perm remove <rank> <permission>",
            "/core rank perm list <rank>",
            "/core rank perm create <rank>",
            "/core rank perm edit <rank> <weight>",
            "/core rank perm setprefix <rank> <prefix>"
    );

    public CoreCommand(Core plugin) {
        super(plugin);

        register(new ReloadSubCommand(plugin));
        register(new DebugSubCommand(plugin));
        register(new MaintenanceSubCommand(plugin));
        register(new SaveSubCommand(plugin));
        register(new PlayerSubCommand(plugin));
        register(new RankSubCommand(plugin));
    }

    private void register(SubCommand subCommand) {
        subCommands.put(subCommand.name().toLowerCase(), subCommand);
    }

    @Override
    protected void execute(CommandContext context) {
        if (context.length() == 0) {
            sendHelp(context);
            return;
        }

        SubCommand subCommand = subCommands.get(context.arg(0).toLowerCase());

        if (subCommand == null) {
            context.reply("unknown-command");
            return;
        }

        if (!plugin.permissions().has(context.sender(), subCommand.permission())) {
            context.reply("no-permission");
            return;
        }

        subCommand.execute(context);
    }

    private void sendHelp(CommandContext context) {
        context.raw("&8&m--------------------------------");
        context.raw("&aSMP-Core &7v" + plugin.getDescription().getVersion());

        for (String line : helpLines) {
            context.raw("&8» &7" + line);
        }

        context.raw("&8&m--------------------------------");
    }

    public List<String> tabComplete(CommandContext context) {
        if (context.length() == 1) {
            List<String> result = new ArrayList<>();

            for (SubCommand subCommand : subCommands.values()) {
                if (subCommand.name().toLowerCase().startsWith(context.arg(0).toLowerCase())) {
                    result.add(subCommand.name());
                }
            }

            return result;
        }

        SubCommand subCommand = subCommands.get(context.arg(0).toLowerCase());

        if (subCommand == null) {
            return List.of();
        }

        return subCommand.tabComplete(context);
    }
}
