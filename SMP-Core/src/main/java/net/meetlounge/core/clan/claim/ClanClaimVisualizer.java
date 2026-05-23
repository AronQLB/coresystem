package net.meetlounge.core.clan.claim;

import net.meetlounge.core.Core;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class ClanClaimVisualizer {

    private final Core plugin;

    public ClanClaimVisualizer(Core plugin) {
        this.plugin = plugin;
    }

    public void show(Player player, ClanClaim claim) {

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (ticks >= 100) {
                    cancel();
                    return;
                }

                draw(player, claim);

                ticks += 10;
            }

        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void draw(Player player, ClanClaim claim) {

        World world = player.getWorld();

        if (!world.getName().equalsIgnoreCase(claim.world())) {
            return;
        }

        int minX = claim.minX();
        int minZ = claim.minZ();

        int maxX = claim.maxX();
        int maxZ = claim.maxZ();

        int y = world.getHighestBlockYAt(player.getLocation()) + 1;

        for (int x = minX; x <= maxX; x++) {

            spawn(player, world, x, y, minZ);
            spawn(player, world, x, y, maxZ);
        }

        for (int z = minZ; z <= maxZ; z++) {

            spawn(player, world, minX, y, z);
            spawn(player, world, maxX, y, z);
        }
    }

    private void spawn(Player player, World world, int x, int y, int z) {

        player.spawnParticle(
                Particle.END_ROD,
                new Location(world, x + 0.5, y + 0.2, z + 0.5),
                1,
                0,
                0,
                0,
                0
        );
    }
}