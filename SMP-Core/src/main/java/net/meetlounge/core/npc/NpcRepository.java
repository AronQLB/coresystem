package net.meetlounge.core.npc;

import net.meetlounge.core.Core;
import net.meetlounge.core.util.LocationSerializer;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class NpcRepository {

    private final Core plugin;

    public NpcRepository(Core plugin) {
        this.plugin = plugin;
    }

    public boolean save(NpcData npc) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             INSERT INTO npcs
             (id, type, color, world, x, y, z, yaw, pitch, `command`)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
             ON DUPLICATE KEY UPDATE
             type = VALUES(type),
             color = VALUES(color),
             world = VALUES(world),
             x = VALUES(x),
             y = VALUES(y),
             z = VALUES(z),
             yaw = VALUES(yaw),
             pitch = VALUES(pitch),
             `command` = VALUES(`command`)
         """)) {

            var location = npc.location();

            statement.setString(1, npc.id().toLowerCase());
            statement.setString(2, npc.type().name());
            statement.setString(3, npc.color().name());
            statement.setString(4, location.getWorld().getName());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.setFloat(8, location.getYaw());
            statement.setFloat(9, location.getPitch());
            statement.setString(10, npc.command());

            statement.executeUpdate();
            return true;

        } catch (SQLException exception) {
            plugin.debug().error("NPC konnte nicht gespeichert werden", exception);
            return false;
        }
    }

    public List<NpcData> findAll() {
        List<NpcData> npcs = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM npcs");
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Location location = LocationSerializer.location(
                        result.getString("world"),
                        result.getDouble("x"),
                        result.getDouble("y"),
                        result.getDouble("z"),
                        result.getFloat("yaw"),
                        result.getFloat("pitch")
                );

                if (location == null) {
                    continue;
                }

                NpcType type;
                NpcColor color;

                try {
                    type = NpcType.valueOf(result.getString("type").toUpperCase());
                } catch (IllegalArgumentException exception) {
                    plugin.debug().warn("NPC mit ungültigem Typ übersprungen: " + result.getString("id"));
                    continue;
                }

                try {
                    color = NpcColor.valueOf(result.getString("color").toUpperCase());
                } catch (IllegalArgumentException exception) {
                    plugin.debug().warn("NPC mit ungültiger Farbe übersprungen: " + result.getString("id"));
                    continue;
                }

                npcs.add(new NpcData(
                        result.getString("id"),
                        type,
                        color,
                        location,
                        result.getString("command")
                ));
            }

        } catch (SQLException exception) {
            plugin.debug().error("NPCs konnten nicht geladen werden", exception);
        }

        return npcs;
    }

    public void delete(String id) {

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM npcs WHERE id = ?"
             )) {

            statement.setString(1, id.toLowerCase());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("NPC konnte nicht gelöscht werden", exception);
        }
    }

}
