package net.meetlounge.core.home;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

import java.util.List;

public final class HomesCommand extends AbstractCommand {

    public HomesCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        List<String> homes = plugin.homes().list(context.player());

        if (homes.isEmpty()) {
            context.raw(Core.prefix + "&cDu hast keine Homes gesetzt.");
            return;
        }

        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aDeine Homes:");
        context.raw(Core.prefix + "&f" + String.join("&7, &f", homes));
        context.raw(Core.prefix + "&8&m----------------------------");
    }
}