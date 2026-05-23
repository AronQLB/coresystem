package net.meetlounge.core.teleport;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TpaCommand extends AbstractCommand {

    public TpaCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&7Nutze: /tpa <spieler>");
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(0));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        plugin.tpa().request(context.player(), target);

        context.raw(Core.prefix + "&aTPA-Anfrage gesendet.");
        target.sendMessage(plugin.messages().raw(Core.prefix + "&a" + context.player().getName() + " &7möchte sich zu dir teleportieren."));
        target.sendMessage(plugin.messages().raw(Core.prefix + "&7Nutze &a/tpaccept &7oder &c/tpdeny"));
    }
}
