package net.meetlounge.core.clan;

import net.meetlounge.core.Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ClanRepository {

    private final Core plugin;

    public ClanRepository(Core plugin) {
        this.plugin = plugin;
    }

    public Optional<Clan> findByPlayer(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT c.id, c.name, c.tag, c.owner_uuid, c.owner_name, c.kills, c.deaths, c.bank, c.created_at
                 FROM clans c
                 INNER JOIN clan_members m ON c.id = m.clan_id
                 WHERE m.player_uuid = ?
             """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(readClan(result));
            }

        } catch (SQLException | RuntimeException exception) {
            plugin.debug().error("Clan konnte nicht geladen werden", exception);
            return Optional.empty();
        }
    }

    public boolean existsName(String name) {
        return exists("name", name);
    }

    public boolean existsTag(String tag) {
        return exists("tag", tag.toUpperCase());
    }

    private boolean exists(String column, String value) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id FROM clans WHERE " + column + " = ?"
             )) {

            statement.setString(1, value);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Existenzprüfung fehlgeschlagen", exception);
            return true;
        }
    }

    public boolean isInClan(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT clan_id FROM clan_members WHERE player_uuid = ?
             """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Mitgliedprüfung fehlgeschlagen", exception);
            return true;
        }
    }

    public void createClan(String name, String tag, UUID ownerUuid, String ownerName) {
        try (Connection connection = plugin.database().getConnection()) {
            connection.setAutoCommit(false);

            try {
                int clanId;

                try (PreparedStatement clanStatement = connection.prepareStatement("""
                    INSERT INTO clans
                    (name, tag, owner_uuid, owner_name, kills, deaths, bank, created_at)
                    VALUES (?, ?, ?, ?, 0, 0, 0, ?)
                """, Statement.RETURN_GENERATED_KEYS)) {

                    clanStatement.setString(1, name);
                    clanStatement.setString(2, tag.toUpperCase());
                    clanStatement.setString(3, ownerUuid.toString());
                    clanStatement.setString(4, ownerName);
                    clanStatement.setLong(5, System.currentTimeMillis());
                    clanStatement.executeUpdate();

                    try (ResultSet keys = clanStatement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Keine Clan-ID generiert.");
                        }

                        clanId = keys.getInt(1);
                    }
                }

                try (PreparedStatement memberStatement = connection.prepareStatement("""
                    INSERT INTO clan_members
                    (player_uuid, player_name, clan_id, role, joined_at)
                    VALUES (?, ?, ?, 'owner', ?)
                """)) {

                    memberStatement.setString(1, ownerUuid.toString());
                    memberStatement.setString(2, ownerName);
                    memberStatement.setInt(3, clanId);
                    memberStatement.setLong(4, System.currentTimeMillis());
                    memberStatement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException exception) {
            plugin.debug().error("Clan konnte nicht erstellt werden", exception);
        }
    }

    public void deleteClan(int clanId) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 DELETE FROM clans WHERE id = ?
             """)) {

            statement.setInt(1, clanId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clan konnte nicht gelöscht werden", exception);
        }
    }

    private Clan readClan(ResultSet result) throws SQLException {
        return new Clan(
                result.getInt(1),
                result.getString(2),
                result.getString(3),
                UUID.fromString(result.getString(4)),
                result.getString(5),
                result.getInt(6),
                result.getInt(7),
                result.getDouble(8),
                result.getLong(9)
        );
    }

    public void updateBank(int clanId, double bank) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             UPDATE clans SET bank = ? WHERE id = ?
         """)) {

            statement.setDouble(1, bank);
            statement.setInt(2, clanId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clankasse konnte nicht aktualisiert werden", exception);
        }
    }

    public void addMember(UUID uuid, String name, int clanId, ClanRole role) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             INSERT INTO clan_members
             (player_uuid, player_name, clan_id, role, joined_at)
             VALUES (?, ?, ?, ?, ?)
             ON DUPLICATE KEY UPDATE
             player_name = VALUES(player_name),
             clan_id = VALUES(clan_id),
             role = VALUES(role)
         """)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, name);
            statement.setInt(3, clanId);
            statement.setString(4, role.name().toLowerCase());
            statement.setLong(5, System.currentTimeMillis());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Mitglied konnte nicht hinzugefügt werden", exception);
        }
    }

    public void removeMember(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             DELETE FROM clan_members WHERE player_uuid = ?
         """)) {

            statement.setString(1, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Mitglied konnte nicht entfernt werden", exception);
        }
    }

    public Optional<ClanMember> findMember(UUID uuid) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             SELECT * FROM clan_members WHERE player_uuid = ?
         """)) {

            statement.setString(1, uuid.toString());

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(new ClanMember(
                        UUID.fromString(result.getString("player_uuid")),
                        result.getString("player_name"),
                        result.getInt("clan_id"),
                        ClanRole.fromString(result.getString("role")),
                        result.getLong("joined_at")
                ));
            }

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Mitglied konnte nicht geladen werden", exception);
            return Optional.empty();
        }
    }

    public List<ClanMember> findMembers(int clanId) {
        List<ClanMember> members = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             SELECT * FROM clan_members WHERE clan_id = ?
             ORDER BY role ASC
         """)) {

            statement.setInt(1, clanId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    members.add(new ClanMember(
                            UUID.fromString(result.getString("player_uuid")),
                            result.getString("player_name"),
                            result.getInt("clan_id"),
                            ClanRole.fromString(result.getString("role")),
                            result.getLong("joined_at")
                    ));
                }
            }

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Mitglieder konnten nicht geladen werden", exception);
        }

        return members;
    }

    public Optional<Clan> findById(int clanId) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             SELECT id, name, tag, owner_uuid, owner_name, kills, deaths, bank, created_at FROM clans WHERE id = ?
         """)) {

            statement.setInt(1, clanId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(readClan(result));
            }

        } catch (SQLException | RuntimeException exception) {
            plugin.debug().error("Clan konnte nicht per ID geladen werden", exception);
            return Optional.empty();
        }
    }

    public void updateMemberRole(UUID uuid, ClanRole role) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             UPDATE clan_members SET role = ? WHERE player_uuid = ?
         """)) {

            statement.setString(1, role.name().toLowerCase());
            statement.setString(2, uuid.toString());
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Rolle konnte nicht geändert werden", exception);
        }
    }

    public void addKill(int clanId) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             UPDATE clans SET kills = kills + 1 WHERE id = ?
         """)) {

            statement.setInt(1, clanId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Kill konnte nicht gespeichert werden", exception);
        }
    }

    public void addDeath(int clanId) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             UPDATE clans SET deaths = deaths + 1 WHERE id = ?
         """)) {

            statement.setInt(1, clanId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Clan-Death konnte nicht gespeichert werden", exception);
        }
    }
}
