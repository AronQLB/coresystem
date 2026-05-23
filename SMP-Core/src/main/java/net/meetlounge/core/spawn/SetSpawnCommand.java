package net.meetlounge.core.spawn;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.permission.PermissionNode;

public final class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand(Core plugin) {
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

        plugin.spawns().setSpawn(context.player().getLocation());
        context.raw(Core.prefix + "&aSpawn wurde gesetzt.");
    }
}