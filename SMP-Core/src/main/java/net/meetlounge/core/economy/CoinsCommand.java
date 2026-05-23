package net.meetlounge.core.economy;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CoinsCommand extends AbstractCommand {

    public CoinsCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!plugin.permissions().has(context.sender(), PermissionNode.CORE_ADMIN)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 3) {
            context.raw(Core.prefix + "&7Nutze: /coins <add|remove|set> <spieler> <betrag>");
            return;
        }

        String action = context.arg(0).toLowerCase();

        Player target = Bukkit.getPlayerExact(context.arg(1));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(context.arg(2));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cUngültiger Betrag.");
            return;
        }

        switch (action) {

            case "add" -> {
                plugin.economy().add(target.getUniqueId(), amount);

                context.raw(Core.prefix + "&a" + amount + " &7Coins hinzugefügt.");
                target.sendMessage(plugin.messages().raw(Core.prefix + "&7Du hast &f" + amount + " &7Coins erhalten."));
            }

            case "remove" -> {
                plugin.economy().remove(target.getUniqueId(), amount);

                context.raw(Core.prefix + "&c" + amount + " &7Coins entfernt.");
                target.sendMessage(plugin.messages().raw(Core.prefix + "&7Dir wurden &f" + amount + " &7Coins entfernt."));
            }

            case "set" -> {
                plugin.economy().set(target.getUniqueId(), amount);

                context.raw(Core.prefix + "&aCoins gesetzt.");
                target.sendMessage(plugin.messages().raw(Core.prefix + "&aDein Kontostand wurde gesetzt."));
            }

            default -> context.raw(Core.prefix + "&7Nutze: /coins <add|remove|set> <spieler> <betrag>");
        }
    }
}
