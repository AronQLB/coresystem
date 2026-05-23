package net.meetlounge.core.spawn;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.LocationSerializer;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class SpawnRepository {

    private final Core plugin;

    public SpawnRepository(Core plugin) {
        this.plugin = plugin;
    }

    public void save(Location location) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO spawn_location
                 (id, world, x, y, z, yaw, pitch)
                 VALUES (1, ?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE
                 world = VALUES(world),
                 x = VALUES(x),
                 y = VALUES(y),
                 z = VALUES(z),
                 yaw = VALUES(yaw),
                 pitch = VALUES(pitch)
             """)) {

            statement.setString(1, location.getWorld().getName());
            statement.setDouble(2, location.getX());
            statement.setDouble(3, location.getY());
            statement.setDouble(4, location.getZ());
            statement.setFloat(5, location.getYaw());
            statement.setFloat(6, location.getPitch());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Spawn konnte nicht gespeichert werden", exception);
        }
    }

    public Location load() {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM spawn_location WHERE id = 1
             """);
             ResultSet result = statement.executeQuery()) {

            if (!result.next()) {
                return null;
            }

            return LocationSerializer.location(
                    result.getString("world"),
                    result.getDouble("x"),
                    result.getDouble("y"),
                    result.getDouble("z"),
                    result.getFloat("yaw"),
                    result.getFloat("pitch")
            );

        } catch (SQLException exception) {
            plugin.debug().error("Spawn konnte nicht geladen werden", exception);
            return null;
        }
    }
}