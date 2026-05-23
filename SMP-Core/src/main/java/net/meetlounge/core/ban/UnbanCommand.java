package net.meetlounge.core.ban;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class UnbanCommand extends AbstractCommand {

    public UnbanCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!plugin.permissions().has(context.sender(), PermissionNode.BAN_MANAGE)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&7Nutze: /&cunban &8[&aSpieler&8]");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(context.arg(0));
        plugin.bans().unban(target.getUniqueId());

        context.raw(Core.prefix + "&7Spieler &f" + context.arg(0) + " &7wurde entbannt.");
    }
}