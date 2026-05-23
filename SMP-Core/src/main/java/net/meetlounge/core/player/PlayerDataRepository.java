package net.meetlounge.core.player;

import net.meetlounge.core.Core;
import net.meetlounge.core.player.PlayerData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class PlayerDataRepository {

    private final Core plugin;

    public PlayerDataRepository(Core plugin) {
        this.plugin = plugin;
    }

    public Optional<PlayerData> findByUuid(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM player_data WHERE uuid = ?
             """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                PlayerData data = new PlayerData(
                        UUID.fromString(result.getString("uuid")),
                        result.getString("name"),
                        result.getLong("first_join"),
                        result.getLong("last_join"),
                        result.getLong("playtime"),
                        result.getDouble("coins"),
                        result.getString("rank_name"),
                        result.getInt("level"),
                        result.getLong("xp")
                );

                return Optional.of(data);
            }

        } catch (SQLException exception) {
            plugin.getLogger().severe("PlayerData konnte nicht geladen werden: " + exception.getMessage());
            return Optional.empty();
        }
    }

    public void save(PlayerData data) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
            INSERT INTO player_data
            (uuid, name, first_join, last_join, playtime, coins, rank_name, level, xp)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            last_join = VALUES(last_join),
            playtime = VALUES(playtime),
            coins = VALUES(coins),
            rank_name = VALUES(rank_name),
            level = VALUES(level),
            xp = VALUES(xp)
             """)) {

            statement.setString(1, data.uuid().toString());
            statement.setString(2, data.name());
            statement.setLong(3, data.firstJoin());
            statement.setLong(4, data.lastJoin());
            statement.setLong(5, data.playtime());
            statement.setDouble(6, data.coins());
            statement.setString(7, data.rank());
            statement.setInt(8, data.level());
            statement.setLong(9, data.xp());

            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.getLogger().severe("PlayerData konnte nicht gespeichert werden: " + exception.getMessage());
        }
    }

    public boolean exists(UUID uuid) {
        return findByUuid(uuid).isPresent();
    }

    public void delete(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 DELETE FROM player_data WHERE uuid = ?
             """)) {

            statement.setString(1, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.getLogger().severe("PlayerData konnte nicht gelöscht werden: " + exception.getMessage());
        }
    }
}