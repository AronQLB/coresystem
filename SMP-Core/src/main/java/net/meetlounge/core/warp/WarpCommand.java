package net.meetlounge.core.warp;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class WarpCommand extends AbstractCommand {

    public WarpCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (context.length() < 1) {
            context.player().openInventory(new WarpsGui(plugin).create());
            return;
        }

        WarpType type = WarpType.fromId(context.arg(0));

        if (type == null) {
            context.raw(Core.prefix + "&cDiesen Warp gibt es nicht. Nutze: &f/warp <rtp|nether|end>");
            return;
        }

        if (!plugin.warps().teleport(context.player(), type.id())) {
            context.raw(Core.prefix + "&cDieser Warp wurde noch nicht gesetzt.");
            return;
        }

        context.raw(Core.prefix + "&7Du wurdest zu &a" + type.displayName() + " &7teleportiert.");
    }
}