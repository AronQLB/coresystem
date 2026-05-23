package net.meetlounge.core.ban;

import net.meetlounge.core.Core;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public final class BanRepository {

    private final Core plugin;

    public BanRepository(Core plugin) {
        this.plugin = plugin;
    }

    public Optional<BanData> findActive(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM bans WHERE uuid = ? AND active = TRUE
             """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(read(result));
            }

        } catch (SQLException exception) {
            plugin.debug().error("Ban konnte nicht geladen werden", exception);
            return Optional.empty();
        }
    }

    public void save(BanData data) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO bans
                 (uuid, name, reason_id, reason, staff, created_at, expires_at, active)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE
                 name = VALUES(name),
                 reason_id = VALUES(reason_id),
                 reason = VALUES(reason),
                 staff = VALUES(staff),
                 created_at = VALUES(created_at),
                 expires_at = VALUES(expires_at),
                 active = VALUES(active)
             """)) {

            statement.setString(1, data.uuid().toString());
            statement.setString(2, data.name());
            statement.setInt(3, data.reasonId());
            statement.setString(4, data.reason());
            statement.setString(5, data.staff());
            statement.setLong(6, data.createdAt());
            statement.setLong(7, data.expiresAt());
            statement.setBoolean(8, data.active());

            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Ban konnte nicht gespeichert werden", exception);
        }
    }

    public void deactivate(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 UPDATE bans SET active = FALSE WHERE uuid = ?
             """)) {

            statement.setString(1, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Ban konnte nicht deaktiviert werden", exception);
        }
    }

    private BanData read(ResultSet result) throws SQLException {
        return new BanData(
                UUID.fromString(result.getString("uuid")),
                result.getString("name"),
                result.getInt("reason_id"),
                result.getString("reason"),
                result.getString("staff"),
                result.getLong("created_at"),
                result.getLong("expires_at"),
                result.getBoolean("active")
        );
    }
}