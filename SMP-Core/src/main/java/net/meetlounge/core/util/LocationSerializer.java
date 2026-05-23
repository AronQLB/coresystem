package net.meetlounge.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer {

    private LocationSerializer() {}

    public static String world(Location location) {
        return location.getWorld().getName();
    }

    public static Location location(String world, double x, double y, double z, float yaw, float pitch) {
        World bukkitWorld = Bukkit.getWorld(world);

        if (bukkitWorld == null) {
            return null;
        }

        return new Location(bukkitWorld, x, y, z, yaw, pitch);
    }
}