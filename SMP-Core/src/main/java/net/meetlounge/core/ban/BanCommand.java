package net.meetlounge.core.ban;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;

public final class BanCommand extends AbstractCommand {

    public BanCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!plugin.permissions().has(context.sender(), PermissionNode.BAN_MANAGE)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 2) {
            sendHelp(context);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(context.arg(0));

        int id;

        try {
            id = Integer.parseInt(context.arg(1));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&7Die &cBan-ID &7muss eine Zahl sein.");
            sendReasons(context);
            return;
        }

        BanReason reason = BanReason.fromId(id);

        if (reason == null) {
            context.raw(Core.prefix + "&7Diese &cBan-ID &7gibt es nicht.");
            sendReasons(context);
            return;
        }

        plugin.bans().ban(context.sender(), target, reason);

        context.raw(Core.prefix + "&aSpieler &f" + context.arg(0) + " &awurde gebannt.");
        context.raw(Core.prefix + "&7Grund: &f" + reason.displayName());
        context.raw(Core.prefix + "&7Dauer: &f" + (reason.permanent() ? "Permanent" : reason.days() + " Tage"));
    }

    private void sendHelp(CommandContext context) {
        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw("&2");
        context.raw(Core.prefix + "&cBanSystem");
        context.raw(Core.prefix + "&7/ban <spieler> <id>");
        sendReasons(context);
        context.raw("&1");
        context.raw(Core.prefix + "&8&m----------------------------");
    }

    private void sendReasons(CommandContext context) {
        Arrays.stream(BanReason.values()).forEach(reason ->
                context.raw(Core.prefix + "&7" + reason.id() + " &8- &f" + reason.displayName()
                        + " &8- &c" + (reason.permanent() ? "Permanent" : reason.days() + " Tage"))
        );
    }
}
