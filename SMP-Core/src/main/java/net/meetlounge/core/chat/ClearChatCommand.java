package net.meetlounge.core.chat;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClearChatCommand extends AbstractCommand {

    public ClearChatCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!plugin.permissions().has(context.sender(), PermissionNode.CHAT_MODERATION)) {
            context.reply("no-permission");
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {

            for (int i = 0; i < 200; i++) {
                player.sendMessage(" ");
            }
        }

        Bukkit.broadcastMessage(plugin.messages().raw(Core.prefix + "&aDer Chat wurde geleert."));
    }
}
