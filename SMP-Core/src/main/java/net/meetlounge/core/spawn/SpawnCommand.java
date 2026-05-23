package net.meetlounge.core.spawn;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;

public final class SpawnCommand extends AbstractCommand {

    public SpawnCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        if (!plugin.spawns().teleport(context.player())) {
            context.raw("&cSpawn wurde noch nicht gesetzt.");
            return;
        }

        context.raw(Core.prefix + "&7Du wurdest zum Spawn teleportiert.");
    }
}