package net.meetlounge.core.spawn;

import net.meetlounge.core.Core;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SpawnService {

    private final Core plugin;
    private final SpawnRepository repository;
    private Location spawn;

    public SpawnService(Core plugin, SpawnRepository repository) {
        this.plugin = plugin;
        this.repository = repository;
    }

    public void load() {
        this.spawn = repository.load();
    }

    public void setSpawn(Location location) {
        this.spawn = location;
        repository.save(location);
    }

    public boolean teleport(Player player) {
        if (spawn == null) {
            return false;
        }

        player.teleport(spawn);
        return true;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location spawn() {
        return spawn;
    }
}