package net.meetlounge.core.database;

import net.meetlounge.core.Core;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteProvider {

    private final Core plugin;
    private Connection connection;

    public SQLiteProvider(Core plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File file = new File(plugin.getDataFolder(), "database.db");
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (SQLException exception) {
            plugin.getLogger().severe("SQLite-Verbindung fehlgeschlagen: " + exception.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("SQLite konnte nicht geschlossen werden: " + exception.getMessage());
        }
    }
}