package net.meetlounge.core.mute;

import net.meetlounge.core.Core;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public final class MuteRepository {

    private final Core plugin;

    public MuteRepository(Core plugin) {
        this.plugin = plugin;
    }

    public void save(MuteData data) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO mutes
                 (uuid, name, reason, staff, created_at, expires_at, active)
                 VALUES (?, ?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE
                 name = VALUES(name),
                 reason = VALUES(reason),
                 staff = VALUES(staff),
                 created_at = VALUES(created_at),
                 expires_at = VALUES(expires_at),
                 active = VALUES(active)
             """)) {

            statement.setString(1, data.uuid().toString());
            statement.setString(2, data.name());
            statement.setString(3, data.reason());
            statement.setString(4, data.staff());
            statement.setLong(5, data.createdAt());
            statement.setLong(6, data.expiresAt());
            statement.setBoolean(7, data.active());

            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Mute konnte nicht gespeichert werden", exception);
        }
    }

    public Optional<MuteData> find(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM mutes WHERE uuid = ? AND active = TRUE
             """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {

                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(new MuteData(
                        UUID.fromString(result.getString("uuid")),
                        result.getString("name"),
                        result.getString("reason"),
                        result.getString("staff"),
                        result.getLong("created_at"),
                        result.getLong("expires_at"),
                        result.getBoolean("active")
                ));
            }

        } catch (SQLException exception) {
            plugin.debug().error("Mute konnte nicht geladen werden", exception);
            return Optional.empty();
        }
    }

    public void unmute(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 UPDATE mutes SET active = FALSE WHERE uuid = ?
             """)) {

            statement.setString(1, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Mute konnte nicht entfernt werden", exception);
        }
    }
}