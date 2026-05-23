package net.meetlounge.core.clan.claim;

import net.meetlounge.core.Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClanClaimRepository {

    private final Core plugin;

    public ClanClaimRepository(Core plugin) {
        this.plugin = plugin;
    }

    public void save(ClanClaim claim) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO clan_claims
                 (clan_id, clan_name, world, center_x, center_z, min_x, min_z, max_x, max_z, created_at)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                 ON DUPLICATE KEY UPDATE
                 clan_name = VALUES(clan_name),
                 world = VALUES(world),
                 center_x = VALUES(center_x),
                 center_z = VALUES(center_z),
                 min_x = VALUES(min_x),
                 min_z = VALUES(min_z),
                 max_x = VALUES(max_x),
                 max_z = VALUES(max_z),
                 created_at = VALUES(created_at)
             """)) {

            statement.setInt(1, claim.clanId());
            statement.setString(2, claim.clanName());
            statement.setString(3, claim.world());
            statement.setInt(4, claim.centerX());
            statement.setInt(5, claim.centerZ());
            statement.setInt(6, claim.minX());
            statement.setInt(7, claim.minZ());
            statement.setInt(8, claim.maxX());
            statement.setInt(9, claim.maxZ());
            statement.setLong(10, claim.createdAt());

            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("ClanClaim konnte nicht gespeichert werden", exception);
        }
    }

    public Optional<ClanClaim> findByClanId(int clanId) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM clan_claims WHERE clan_id = ?
             """)) {

            statement.setInt(1, clanId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }

                return Optional.of(read(result));
            }

        } catch (SQLException exception) {
            plugin.debug().error("ClanClaim konnte nicht geladen werden", exception);
            return Optional.empty();
        }
    }

    public List<ClanClaim> findAll() {
        List<ClanClaim> claims = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM clan_claims");
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                claims.add(read(result));
            }

        } catch (SQLException exception) {
            plugin.debug().error("ClanClaims konnten nicht geladen werden", exception);
        }

        return claims;
    }

    public void delete(int clanId) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 DELETE FROM clan_claims WHERE clan_id = ?
             """)) {

            statement.setInt(1, clanId);
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("ClanClaim konnte nicht gelöscht werden", exception);
        }
    }

    private ClanClaim read(ResultSet result) throws SQLException {
        return new ClanClaim(
                result.getInt("clan_id"),
                result.getString("clan_name"),
                result.getString("world"),
                result.getInt("center_x"),
                result.getInt("center_z"),
                result.getInt("min_x"),
                result.getInt("min_z"),
                result.getInt("max_x"),
                result.getInt("max_z"),
                result.getLong("created_at")
        );
    }
}