package net.meetlounge.core.home;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class SetHomeCommand extends AbstractCommand {

    public SetHomeCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        String name = context.length() >= 1 ? context.arg(0) : "home";

        if (name.length() > 32) {
            context.raw(Core.prefix + "&cDer Home-Name darf maximal 32 Zeichen lang sein.");
            return;
        }

        plugin.homes().set(context.player(), name);
        context.raw(Core.prefix + "&aHome &f" + name + " &7wurde gesetzt.");
    }
}