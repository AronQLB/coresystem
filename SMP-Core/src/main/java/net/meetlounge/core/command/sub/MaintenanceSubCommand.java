package net.meetlounge.core.command.sub;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

import java.util.List;

public final class MaintenanceSubCommand implements SubCommand {

    private final Core plugin;

    public MaintenanceSubCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "maintenance";
    }

    @Override
    public String permission() {
        return PermissionNode.MAINTENANCE_MANAGE.node();
    }

    @Override
    public String usage() {
        return Core.prefix + "/core maintenance <on|off|toggle|status>";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.length() < 2) {
            context.raw("&cNutze: " + usage());
            return;
        }

        switch (context.arg(1).toLowerCase()) {
            case "on" -> {
                plugin.maintenance().enable();
                context.reply("maintenance-enabled");
            }
            case "off" -> {
                plugin.maintenance().disable();
                context.reply("maintenance-disabled");
            }
            case "toggle" -> {
                plugin.maintenance().toggle();
                context.raw("&7Maintenance: &f" + plugin.maintenance().isEnabled());
            }
            case "status" -> context.raw(Core.prefix + "&7Maintenance: &f" + plugin.maintenance().isEnabled());
            default -> context.raw(Core.prefix + "&cNutze: " + usage());
        }
    }

    @Override
    public List<String> tabComplete(CommandContext context) {
        if (context.length() == 2) {
            return List.of("on", "off", "toggle", "status");
        }

        return List.of();
    }
}
