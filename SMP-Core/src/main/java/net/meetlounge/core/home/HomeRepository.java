package net.meetlounge.core.home;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.LocationSerializer;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;

public final class HomeRepository {

    private final Core plugin;

    public HomeRepository(Core plugin) {
        this.plugin = plugin;
    }

    public void save(UUID uuid, String name, Location location) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO homes
                 (uuid, name, world, x, y, z, yaw, pitch)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE
                 world = VALUES(world),
                 x = VALUES(x),
                 y = VALUES(y),
                 z = VALUES(z),
                 yaw = VALUES(yaw),
                 pitch = VALUES(pitch)
             """)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, name.toLowerCase());
            statement.setString(3, location.getWorld().getName());
            statement.setDouble(4, location.getX());
            statement.setDouble(5, location.getY());
            statement.setDouble(6, location.getZ());
            statement.setFloat(7, location.getYaw());
            statement.setFloat(8, location.getPitch());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Home konnte nicht gespeichert werden", exception);
        }
    }

    public Location find(UUID uuid, String name) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM homes WHERE uuid = ? AND name = ?
             """)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, name.toLowerCase());

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
            plugin.debug().error("Home konnte nicht geladen werden", exception);
            return null;
        }
    }

    public List<String> names(UUID uuid) {
        List<String> names = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT name FROM homes WHERE uuid = ?
             """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    names.add(result.getString("name"));
                }
            }

        } catch (SQLException exception) {
            plugin.debug().error("Homes konnten nicht geladen werden", exception);
        }

        return names;
    }

    public void delete(UUID uuid, String name) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 DELETE FROM homes WHERE uuid = ? AND name = ?
             """)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, name.toLowerCase());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Home konnte nicht gelöscht werden", exception);
        }
    }
}