package net.meetlounge.core.chat;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class SlowChatCommand extends AbstractCommand {

    public SlowChatCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!plugin.permissions().has(context.sender(), PermissionNode.CHAT_MODERATION)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&cNutze: /slowchat <sekunden|off>");
            return;
        }

        if (context.arg(0).equalsIgnoreCase("off")
                || context.arg(0).equalsIgnoreCase("aus")
                || context.arg(0).equalsIgnoreCase("disable")) {
            plugin.mutes().setSlowChatSeconds(0);
            context.raw(Core.prefix + "&aSlowChat wurde deaktiviert.");
            return;
        }

        int seconds;

        try {
            seconds = Integer.parseInt(context.arg(0));
        } catch (NumberFormatException exception) {
            context.raw(Core.prefix + "&cUngültige Zahl.");
            return;
        }

        if (seconds < 0) {
            context.raw(Core.prefix + "&cDie Sekunden müssen mindestens 0 sein.");
            return;
        }

        plugin.mutes().setSlowChatSeconds(seconds);

        if (seconds == 0) {
            context.raw(Core.prefix + "&aSlowChat wurde deaktiviert.");
        } else {
            context.raw(Core.prefix + "&aSlowChat wurde auf &f" + seconds + " Sekunden &agesetzt.");
        }
    }
}
