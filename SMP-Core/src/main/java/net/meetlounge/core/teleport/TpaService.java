package net.meetlounge.core.teleport;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TpaService {

    private final Map<UUID, UUID> requests = new HashMap<>();

    public void request(Player sender, Player target) {
        requests.put(target.getUniqueId(), sender.getUniqueId());
    }

    public UUID getRequester(Player target) {
        return requests.get(target.getUniqueId());
    }

    public void clear(Player target) {
        requests.remove(target.getUniqueId());
    }
}