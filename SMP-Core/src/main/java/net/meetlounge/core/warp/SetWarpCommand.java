package net.meetlounge.core.warp;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class SetWarpCommand extends AbstractCommand {

    public SetWarpCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.permissions().has(context.sender(), PermissionNode.CORE_ADMIN)) {
            context.reply("no-permission");
            return;
        }

        if (context.length() < 1) {
            context.raw(Core.prefix + "&7Nutze: /setwarp <rtp|nether|end>");
            return;
        }

        WarpType type = WarpType.fromId(context.arg(0));

        if (type == null) {
            context.raw(Core.prefix + "&cDiesen Warp gibt es nicht. Nutze: &f/setwarp <rtp|nether|end>");
            return;
        }

        plugin.warps().set(type.id(), context.player().getLocation());
        context.raw(Core.prefix + "&7Warp &a" + type.displayName() + " &7wurde gesetzt.");
    }
}