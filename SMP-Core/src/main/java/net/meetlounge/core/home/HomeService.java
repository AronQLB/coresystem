package net.meetlounge.core.home;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public final class HomeService {

    private final HomeRepository repository;

    public HomeService(HomeRepository repository) {
        this.repository = repository;
    }

    public void set(Player player, String name) {
        repository.save(player.getUniqueId(), name, player.getLocation());
    }

    public boolean teleport(Player player, String name) {
        Location location = repository.find(player.getUniqueId(), name);

        if (location == null) {
            return false;
        }

        player.teleport(location);
        return true;
    }

    public boolean exists(Player player, String name) {
        return repository.find(player.getUniqueId(), name) != null;
    }

    public List<String> list(Player player) {
        return repository.names(player.getUniqueId());
    }

    public void delete(Player player, String name) {
        repository.delete(player.getUniqueId(), name);
    }
}