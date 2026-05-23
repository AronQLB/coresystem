package net.meetlounge.core.warp;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.LocationSerializer;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class WarpRepository {

    private final Core plugin;

    public WarpRepository(Core plugin) {
        this.plugin = plugin;
    }

    public void save(String name, Location location) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO warps
                 (name, world, x, y, z, yaw, pitch)
                 VALUES (?, ?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE
                 world = VALUES(world),
                 x = VALUES(x),
                 y = VALUES(y),
                 z = VALUES(z),
                 yaw = VALUES(yaw),
                 pitch = VALUES(pitch)
             """)) {

            statement.setString(1, name.toLowerCase());
            statement.setString(2, location.getWorld().getName());
            statement.setDouble(3, location.getX());
            statement.setDouble(4, location.getY());
            statement.setDouble(5, location.getZ());
            statement.setFloat(6, location.getYaw());
            statement.setFloat(7, location.getPitch());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Warp konnte nicht gespeichert werden", exception);
        }
    }

    public Location find(String name) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM warps WHERE name = ?")) {

            statement.setString(1, name.toLowerCase());

            try (ResultSet result = statement.executeQuery()) {
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
            }

        } catch (SQLException exception) {
            plugin.debug().error("Warp konnte nicht geladen werden", exception);
            return null;
        }
    }

    public List<String> names() {
        List<String> names = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM warps ORDER BY name ASC");
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                names.add(result.getString("name"));
            }

        } catch (SQLException exception) {
            plugin.debug().error("Warps konnten nicht geladen werden", exception);
        }

        return names;
    }

    public void delete(String name) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM warps WHERE name = ?")) {

            statement.setString(1, name.toLowerCase());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Warp konnte nicht gelöscht werden", exception);
        }
    }
}