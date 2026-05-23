package net.meetlounge.core.chat;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class ChatMuteCommand extends AbstractCommand {

    public ChatMuteCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!plugin.permissions().has(context.sender(), PermissionNode.CHAT_MODERATION)) {
            context.reply("no-permission");
            return;
        }

        boolean newState = !plugin.mutes().chatMuted();

        plugin.mutes().setChatMuted(newState);

        context.raw(Core.prefix + (newState
                ? "&cDer Chat wurde deaktiviert."
                : "&aDer Chat wurde aktiviert."));
    }
}
