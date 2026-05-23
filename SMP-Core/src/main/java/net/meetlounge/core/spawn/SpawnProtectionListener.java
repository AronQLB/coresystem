package net.meetlounge.core.spawn;

import net.meetlounge.core.Core;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public final class SpawnProtectionListener implements Listener {

    private final Core plugin;

    public SpawnProtectionListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (plugin.npcs().isSpawningNpc()) {
            return;
        }

        if (!plugin.configs().config().get().getBoolean("spawn-protection.enabled", true)) {
            return;
        }

        Location spawn = plugin.spawns().getSpawn();

        if (spawn == null || spawn.getWorld() == null) {
            return;
        }

        Location location = event.getLocation();

        if (location.getWorld() == null || !location.getWorld().equals(spawn.getWorld())) {
            return;
        }

        int radius = plugin.configs().config().get().getInt("spawn-protection.radius", 100);

        if (location.distanceSquared(spawn) > radius * radius) {
            return;
        }

        Entity entity = event.getEntity();

        if (shouldBlock(entity)) {
            event.setCancelled(true);
        }
    }

    private boolean shouldBlock(Entity entity) {
        if (entity instanceof Monster) {
            return plugin.configs().config().get().getBoolean("spawn-protection.block-monsters", true);
        }

        if (entity instanceof Animals) {
            return plugin.configs().config().get().getBoolean("spawn-protection.block-animals", true);
        }

        if (entity instanceof Slime) {
            return plugin.configs().config().get().getBoolean("spawn-protection.block-slimes", true);
        }

        if (entity instanceof Ambient) {
            return plugin.configs().config().get().getBoolean("spawn-protection.block-ambient", true);
        }

        return entity.getType() == EntityType.PHANTOM
                || entity.getType() == EntityType.GHAST
                || entity.getType() == EntityType.MAGMA_CUBE
                || entity.getType() == EntityType.BLAZE
                || entity.getType() == EntityType.WITHER
                || entity.getType() == EntityType.ENDER_DRAGON;
    }
}
