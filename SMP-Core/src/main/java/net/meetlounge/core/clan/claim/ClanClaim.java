package net.meetlounge.core.clan.claim;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class ClanClaim {

    private final int clanId;
    private final String clanName;
    private final String world;
    private final int centerX;
    private final int centerZ;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;
    private final long createdAt;

    public ClanClaim(int clanId, String clanName, String world, int centerX, int centerZ,
                     int minX, int minZ, int maxX, int maxZ, long createdAt) {
        this.clanId = clanId;
        this.clanName = clanName;
        this.world = world;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.createdAt = createdAt;
    }

    public int clanId() {
        return clanId;
    }

    public String clanName() {
        return clanName;
    }

    public String world() {
        return world;
    }

    public boolean contains(Location location) {
        if (location.getWorld() == null) return false;
        if (!location.getWorld().getName().equalsIgnoreCase(world)) return false;

        int x = location.getBlockX();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public Location teleportLocation() {
        World bukkitWorld = Bukkit.getWorld(world);

        if (bukkitWorld == null) {
            return null;
        }

        int y = bukkitWorld.getHighestBlockYAt(centerX, centerZ) + 1;
        return new Location(bukkitWorld, centerX + 0.5, y, centerZ + 0.5);
    }

    public int centerX() {
        return centerX;
    }

    public int centerZ() {
        return centerZ;
    }

    public int minX() { return minX; }
    public int minZ() { return minZ; }
    public int maxX() { return maxX; }
    public int maxZ() { return maxZ; }
    public long createdAt() { return createdAt; }
}