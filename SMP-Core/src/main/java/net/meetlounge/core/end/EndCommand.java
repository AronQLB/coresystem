package net.meetlounge.core.end;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class EndCommand extends AbstractCommand {

    public EndCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {

        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        World world = Bukkit.getWorld("world_the_end");

        if (world == null) {
            context.raw(Core.prefix + "&cDie End-Welt existiert nicht.");
            return;
        }

        Location location = world.getSpawnLocation();

        int y = world.getHighestBlockYAt(location) + 1;

        location.setY(y);

        context.player().teleport(location);

        context.raw(Core.prefix + "&7Du wurdest ins End teleportiert.");
    }
}