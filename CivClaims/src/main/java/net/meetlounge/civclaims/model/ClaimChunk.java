package net.meetlounge.civclaims.model;

import org.bukkit.Chunk;
import org.bukkit.Location;

public record ClaimChunk(String worldName, int x, int z) {

    public static ClaimChunk from(Chunk chunk) {
        return new ClaimChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ClaimChunk from(Location location) {
        return from(location.getChunk());
    }

    public String key() {
        return worldName + ":" + x + ":" + z;
    }
}
