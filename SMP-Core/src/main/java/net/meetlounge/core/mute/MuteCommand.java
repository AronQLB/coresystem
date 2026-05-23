package net.meetlounge.core.mute;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class MuteCommand extends AbstractCommand {

    public MuteCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!plugin.permissions().has(context.sender(), PermissionNode.CHAT_MODERATION)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 3) {
            context.raw("&cNutze: /mute <spieler> <sekunden|-1> <grund>");
            return;
        }

        Player target = Bukkit.getPlayerExact(context.arg(0));

        if (target == null) {
            context.reply("not-online");
            return;
        }

        long seconds;

        try {
            seconds = Long.parseLong(context.arg(1));
        } catch (NumberFormatException exception) {
            context.raw("&cUngültige Zeit.");
            return;
        }

        String reason = context.joined(2);

        long duration = seconds == -1 ? -1 : seconds * 1000L;

        plugin.mutes().mute(context.player(), target, reason, duration);

        context.raw("&aSpieler wurde gemutet.");

        target.sendMessage(plugin.mutes().muteMessage(
                plugin.mutes().getMute(target).orElseThrow()
        ));
    }
}