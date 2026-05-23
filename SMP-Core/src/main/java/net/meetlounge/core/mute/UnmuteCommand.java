package net.meetlounge.core.mute;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class UnmuteCommand extends AbstractCommand {

    public UnmuteCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!plugin.permissions().has(context.sender(), PermissionNode.CHAT_MODERATION)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw("&cNutze: /unmute <spieler>");
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(0));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        plugin.mutes().unmute(target);

        context.raw("&aSpieler wurde entmutet.");
        target.sendMessage(plugin.messages().raw(Core.prefix + "&aDu wurdest entmutet."));
    }
}
