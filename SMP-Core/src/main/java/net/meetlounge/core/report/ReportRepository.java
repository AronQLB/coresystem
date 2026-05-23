package net.meetlounge.core.report;

import net.meetlounge.core.Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ReportRepository {

    private final Core plugin;

    public ReportRepository(Core plugin) {
        this.plugin = plugin;
    }

    public int create(UUID reporterUuid, String reporterName, UUID targetUuid, String targetName, ReportReason reason) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO reports
                 (reporter_uuid, reporter_name, target_uuid, target_name, reason, created_at, open)
                 VALUES (?, ?, ?, ?, ?, ?, TRUE)
             """, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, reporterUuid.toString());
            statement.setString(2, reporterName);
            statement.setString(3, targetUuid.toString());
            statement.setString(4, targetName);
            statement.setString(5, reason.displayName());
            statement.setLong(6, System.currentTimeMillis());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException exception) {
            plugin.debug().error("Report konnte nicht erstellt werden", exception);
        }

        return -1;
    }

    public List<ReportData> findOpenReports() {
        List<ReportData> reports = new ArrayList<>();

        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT * FROM reports
                 WHERE open = TRUE
                 ORDER BY created_at DESC
                 LIMIT 45
             """);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                reports.add(read(result));
            }

        } catch (SQLException exception) {
            plugin.debug().error("Reports konnten nicht geladen werden", exception);
        }

        return reports;
    }

    public void close(int id) {
        try (Connection connection = plugin.database().getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 UPDATE reports SET open = FALSE WHERE id = ?
             """)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException exception) {
            plugin.debug().error("Report konnte nicht geschlossen werden", exception);
        }
    }

    private ReportData read(ResultSet result) throws SQLException {
        return new ReportData(
                result.getInt("id"),
                UUID.fromString(result.getString("reporter_uuid")),
                result.getString("reporter_name"),
                UUID.fromString(result.getString("target_uuid")),
                result.getString("target_name"),
                result.getString("reason"),
                result.getLong("created_at"),
                result.getBoolean("open")
        );
    }
}