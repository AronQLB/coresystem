package net.meetlounge.core.auction;

import net.meetlounge.core.Core;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AuctionRepository {

    private final Core plugin;

    public AuctionRepository(Core plugin) {
        this.plugin = plugin;
    }

    public boolean create(UUID sellerUuid, String sellerName, ItemStack item, double price, long createdAt, long expiresAt) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO auction_house
                     (seller_uuid, seller_name, item_data, price, created_at, expires_at, reminder_sent)
                     VALUES (?, ?, ?, ?, ?, ?, FALSE)
                     """)) {

            statement.setString(1, sellerUuid.toString());
            statement.setString(2, sellerName);
            statement.setString(3, ItemSerializer.serialize(item));
            statement.setDouble(4, price);
            statement.setLong(5, createdAt);
            statement.setLong(6, expiresAt);
            statement.executeUpdate();
            return true;
        } catch (Exception exception) {
            plugin.debug().error("Auction konnte nicht gespeichert werden", exception);
            return false;
        }
    }

    public List<AuctionListing> findPage(int page, int pageSize) {
        List<AuctionListing> listings = new ArrayList<>();
        int offset = Math.max(0, page) * pageSize;

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT * FROM auction_house
                     WHERE expires_at > ?
                     ORDER BY created_at DESC
                     LIMIT ? OFFSET ?
                     """)) {

            statement.setLong(1, System.currentTimeMillis());
            statement.setInt(2, pageSize);
            statement.setInt(3, offset);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    toListing(result).ifPresent(listings::add);
                }
            }
        } catch (SQLException exception) {
            plugin.debug().error("Auctions konnten nicht geladen werden", exception);
        }

        return listings;
    }

    public Optional<AuctionListing> findById(int id) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM auction_house WHERE id = ?")) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return toListing(result);
                }
            }
        } catch (SQLException exception) {
            plugin.debug().error("Auction konnte nicht geladen werden", exception);
        }

        return Optional.empty();
    }

    public boolean delete(int id) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM auction_house WHERE id = ?")) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            plugin.debug().error("Auction konnte nicht gelöscht werden", exception);
            return false;
        }
    }

    public List<AuctionListing> findReminders(long now) {
        List<AuctionListing> listings = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT * FROM auction_house
                     WHERE reminder_sent = FALSE AND expires_at > ? AND expires_at <= ?
                     """)) {

            statement.setLong(1, now);
            statement.setLong(2, now + AuctionService.REMINDER_BEFORE_MILLIS);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    toListing(result).ifPresent(listings::add);
                }
            }
        } catch (SQLException exception) {
            plugin.debug().error("Auction-Reminder konnten nicht geladen werden", exception);
        }

        return listings;
    }

    public void markReminderSent(int id) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE auction_house SET reminder_sent = TRUE WHERE id = ?")) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.debug().error("Auction-Reminder konnte nicht gespeichert werden", exception);
        }
    }

    public void deleteExpired(long now) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM auction_house WHERE expires_at <= ?")) {

            statement.setLong(1, now);
            statement.executeUpdate();
        } catch (SQLException exception) {
            plugin.debug().error("Abgelaufene Auctions konnten nicht gelöscht werden", exception);
        }
    }

    private Optional<AuctionListing> toListing(ResultSet result) {
        try {
            return Optional.of(new AuctionListing(
                    result.getInt("id"),
                    UUID.fromString(result.getString("seller_uuid")),
                    result.getString("seller_name"),
                    ItemSerializer.deserialize(result.getString("item_data")),
                    result.getDouble("price"),
                    result.getLong("created_at"),
                    result.getLong("expires_at"),
                    result.getBoolean("reminder_sent")
            ));
        } catch (Exception exception) {
            plugin.debug().error("Auction-Item konnte nicht gelesen werden", exception);
            return Optional.empty();
        }
    }
}
