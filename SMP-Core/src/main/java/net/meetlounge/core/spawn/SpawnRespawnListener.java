package net.meetlounge.core.spawn;

import net.meetlounge.core.Core;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class SpawnRespawnListener implements Listener {

    private final Core plugin;

    public SpawnRespawnListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        Location spawn = plugin.spawns().spawn();

        if (spawn == null) {
            return;
        }

        event.setRespawnLocation(spawn);
    }
}