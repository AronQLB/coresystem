package net.meetlounge.core.staff;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class VanishCommand extends AbstractCommand {

    public VanishCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.VANISH)) {
            context.reply("no-permission");
            return;
        }

        boolean enabled = plugin.vanish().toggle(context.player());
        context.raw(Core.prefix + (enabled ? "&aVanish wurde aktiviert." : "&cVanish wurde deaktiviert."));
    }
}
