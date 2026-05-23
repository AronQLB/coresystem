package net.meetlounge.core.rtp;

import net.meetlounge.core.Core;
import net.meetlounge.core.command.AbstractCommand;
import net.meetlounge.core.command.CommandContext;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

public final class RtpCommand extends AbstractCommand {

    private final Random random = new Random();

    public RtpCommand(Core plugin) {
        super(plugin);
    }

    @Override
    protected void execute(CommandContext context) {
        if (!context.isPlayer()) {
            context.reply("player-only");
            return;
        }

        World world = context.player().getWorld();
        Location center = plugin.spawns().getSpawn();

        if (center == null || center.getWorld() == null || !center.getWorld().equals(world)) {
            center = world.getSpawnLocation();
        }

        int radius = plugin.configs().config().get().getInt("rtp.radius", 20000);
        int protectedRadius = plugin.configs().config().get().getInt("rtp.protected-spawn-radius", 10000);
        int minDistance = plugin.configs().config().get().getInt("rtp.min-distance-from-spawn", protectedRadius + 1000);

        radius = Math.max(radius, protectedRadius + 1000);
        minDistance = Math.max(protectedRadius + 1, Math.min(minDistance, radius));

        Location location = null;

        for (int attempt = 0; attempt < 25; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = minDistance + (random.nextDouble() * (radius - minDistance));

            int x = center.getBlockX() + (int) Math.round(Math.cos(angle) * distance);
            int z = center.getBlockZ() + (int) Math.round(Math.sin(angle) * distance);
            int y = world.getHighestBlockYAt(x, z) + 1;
            Material ground = world.getBlockAt(x, y - 1, z).getType();

            if (ground.isAir() || ground == Material.WATER || ground == Material.LAVA) {
                continue;
            }

            location = new Location(world, x + 0.5, y, z + 0.5);
            break;
        }

        if (location == null) {
            context.raw(Core.prefix + "&cEs wurde kein sicherer RTP-Punkt gefunden.");
            return;
        }

        context.player().teleport(location);

        context.raw(Core.prefix + "&aDu wurdest zufällig teleportiert.");
    }
}
