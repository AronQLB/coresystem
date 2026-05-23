package net.meetlounge.core.warp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public final class WarpService {

    private final WarpRepository repository;

    public WarpService(WarpRepository repository) {
        this.repository = repository;
    }

    public void set(String name, Location location) {
        repository.save(name, location);
    }

    public boolean teleport(Player player, String name) {
        Location location = repository.find(name);

        if (location == null) {
            return false;
        }

        player.teleport(location);
        return true;
    }

    public boolean exists(String name) {
        return repository.find(name) != null;
    }

    public List<String> list() {
        return repository.names();
    }

    public void delete(String name) {
        repository.delete(name);
    }
}