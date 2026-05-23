package net.meetlounge.core.home;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class DelHomeCommand extends AbstractCommand {

    public DelHomeCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        String name = context.length() >= 1 ? context.arg(0) : "home";

        if (!plugin.homes().exists(context.player(), name)) {
            context.raw(Core.prefix + "&cDieses Home existiert nicht.");
            return;
        }

        plugin.homes().delete(context.player(), name);
        context.raw(Core.prefix + "&7Home &f" + name + " &7wurde gelöscht.");
    }
}