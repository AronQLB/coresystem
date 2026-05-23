package net.meetlounge.core.level;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import net.meetlounge.core.player.PlayerData;

public final class LevelCommand extends AbstractCommand {

    public LevelCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        PlayerData data = plugin.players().data(context.player().getUniqueId());

        if (data == null) {
            context.raw("&cDeine Daten sind noch nicht geladen.");
            return;
        }

        long required = plugin.levels().requiredXp(data.level());

        context.raw(Core.prefix + "&8&m----------------------------");
        context.raw(Core.prefix + "&aLevel");
        context.raw(Core.prefix + "&7Level: &a" + data.level());
        context.raw(Core.prefix + "&7XP: &f" + data.xp() + "&8/&f" + required);
        context.raw(Core.prefix + "&8&m----------------------------");
    }
}