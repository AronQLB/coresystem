package net.meetlounge.core.region;

import org.bukkit.Location;

import java.util.EnumMap;
import java.util.Map;

public final class Region {

    private final String name;
    private final String world;
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final Map<RegionFlag, Boolean> flags;

    private final RegionType type;

    private final int centerX;
    private final int centerZ;
    private final int radius;

    public Region(
            String name,
            String world,
            RegionType type,

            int minX,
            int minY,
            int minZ,

            int maxX,
            int maxY,
            int maxZ,

            int centerX,
            int centerZ,
            int radius,

            Map<RegionFlag, Boolean> flags
    ) {
        this.name = name;
        this.world = world;
        this.type = type;

        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;

        this.flags = flags;
    }

    public String name() {
        return name;
    }

    public String world() {
        return world;
    }

    public boolean contains(Location location) {

        if (location.getWorld() == null) {
            return false;
        }

        if (!location.getWorld().getName().equalsIgnoreCase(world)) {
            return false;
        }

        if (type == RegionType.CIRCLE) {

            int dx = location.getBlockX() - centerX;
            int dz = location.getBlockZ() - centerZ;

            return (dx * dx + dz * dz) <= (radius * radius);
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public boolean flag(RegionFlag flag) {
        return flags.getOrDefault(flag, true);
    }

    public Map<RegionFlag, Boolean> flags() {
        return flags;
    }

    public int minX() { return minX; }
    public int minY() { return minY; }
    public int minZ() { return minZ; }
    public int maxX() { return maxX; }
    public int maxY() { return maxY; }
    public int maxZ() { return maxZ; }

    public RegionType type() {
        return type;
    }

    public int centerX() {
        return centerX;
    }

    public int centerZ() {
        return centerZ;
    }

    public int radius() {
        return radius;
    }
}