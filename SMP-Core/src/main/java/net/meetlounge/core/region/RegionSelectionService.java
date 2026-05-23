package net.meetlounge.core.region;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RegionSelectionService {

    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();

    public void setPos1(UUID uuid, Location location) {
        pos1.put(uuid, location);
    }

    public void setPos2(UUID uuid, Location location) {
        pos2.put(uuid, location);
    }

    public Location pos1(UUID uuid) {
        return pos1.get(uuid);
    }

    public Location pos2(UUID uuid) {
        return pos2.get(uuid);
    }

    public boolean hasSelection(UUID uuid) {
        return pos1.containsKey(uuid) && pos2.containsKey(uuid);
    }

    public void clear(UUID uuid) {
        pos1.remove(uuid);
        pos2.remove(uuid);
    }
}