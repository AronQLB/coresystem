package net.meetlounge.core.teleport;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class TpAcceptCommand extends AbstractCommand {

    public TpAcceptCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        UUID requesterUuid = plugin.tpa().getRequester(context.player());

        if (requesterUuid == null) {
            context.raw(Core.prefix + "&cDu hast keine TPA-Anfrage.");
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUuid);

        if (requester == null) {
            context.raw(Core.prefix + "&cDer Spieler ist nicht mehr online.");
            plugin.tpa().clear(context.player());
            return;
        }

        requester.teleport(context.player());
        plugin.tpa().clear(context.player());

        requester.sendMessage(plugin.messages().raw(Core.prefix + "&aTPA angenommen."));
        context.raw(Core.prefix + "&aTPA angenommen.");
    }
}
