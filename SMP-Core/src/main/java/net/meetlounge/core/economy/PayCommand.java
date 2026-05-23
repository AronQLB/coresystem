package net.meetlounge.core.economy;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PayCommand extends AbstractCommand {

    public PayCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.PAY_USE)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 2) {
            context.raw(Core.prefix + "&7Nutze: /pay <spieler> <coins>");
            return;
        }

        Player sender = context.player();
        Player target = Bukkit.getPlayerExact(context.arg(0));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        if (target.getUniqueId().equals(sender.getUniqueId())) {
            context.raw(Core.prefix + "&cDu kannst dir nicht selbst Coins senden.");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(context.arg(1));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cBitte gib eine gültige Zahl ein.");
            return;
        }

        if (amount <= 0) {
            context.raw(Core.prefix + "&cDer Betrag muss größer als 0 sein.");
            return;
        }

        if (!plugin.economy().has(sender.getUniqueId(), amount)) {
            context.raw(Core.prefix + "&cDu hast nicht genug Coins.");
            return;
        }

        plugin.economy().remove(sender.getUniqueId(), amount);
        plugin.economy().add(target.getUniqueId(), amount);

        sender.sendMessage(plugin.messages().raw(Core.prefix + "&7Du hast &f" + amount + " &7Coins an &a" + target.getName() + " &7gesendet."));
        target.sendMessage(plugin.messages().raw(Core.prefix + "&7Du hast &f" + amount + " &7Coins von &f" + sender.getName() + " &7erhalten."));
    }
}
