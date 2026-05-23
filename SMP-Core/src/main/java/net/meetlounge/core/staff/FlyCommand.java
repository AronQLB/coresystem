package net.meetlounge.core.staff;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class FlyCommand extends AbstractCommand {

    public FlyCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.FLY)) {
            context.reply("no-permission");
            return;
        }

        Player target = context.player();

        if (context.length() >= 1) {
            if (!plugin.permissions().has(context.sender(), PermissionNode.FLY_OTHER)) {
                context.reply("no-permission");
                return;
            }

            target = Bukkit.getPlayerExact(context.arg(0));

            if (target == null) {
                context.reply("not-online");
                return;
            }
        }

        boolean enabled = !target.getAllowFlight();
        target.setAllowFlight(enabled);

        if (!enabled) {
            target.setFlying(false);
        }

        if (target.equals(context.player())) {
            context.raw(Core.prefix + (enabled ? "&aFly wurde aktiviert." : "&cFly wurde deaktiviert."));
            return;
        }

        context.raw(Core.prefix + "&7Fly für &f" + target.getName() + " &7wurde " + (enabled ? "&aaktiviert." : "&cdeaktiviert."));
        target.sendMessage(plugin.messages().raw(Core.prefix + (enabled ? "&aFly wurde aktiviert." : "&cFly wurde deaktiviert.")));
    }
}
