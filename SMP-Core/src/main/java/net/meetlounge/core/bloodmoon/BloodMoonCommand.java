package net.meetlounge.core.bloodmoon;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class BloodMoonCommand extends AbstractCommand {

    public BloodMoonCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.hasPermission("meetlounge.bloodmoon.admin")) {
            context.raw(Core.prefix + "&cDazu hast du keine Rechte.");
            return;
        }

        if (!context.isPlayer()) {
            context.raw(Core.prefix + "&cNutze den Befehl ingame.");
            return;
        }

        String action = context.arg(0).toLowerCase();

        if (action.equals("start")) {
            plugin.bloodMoon().forceStart(context.player().getWorld());
            context.raw(Core.prefix + "&4BloodMoon &8| &cBloodMoon wurde gestartet.");
            return;
        }

        if (action.equals("stop")) {
            plugin.bloodMoon().forceStop(context.player().getWorld());
            context.raw(Core.prefix + "&4BloodMoon &8| &7BloodMoon wurde beendet.");
            return;
        }

        boolean active = plugin.bloodMoon().isActive(context.player().getWorld());
        context.raw(Core.prefix + "&4BloodMoon &8| &7Status: " + (active ? "&caktiv" : "&ainaktiv"));
        context.raw(Core.prefix + "&7Nutze &c/bloodmoon start&7 oder &c/bloodmoon stop&7.");
    }
}
