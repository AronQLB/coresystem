package net.meetlounge.core.database;

import net.meetlounge.core.Core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class MySQLProvider implements DatabaseProvider {

    private final Core plugin;
    private Connection connection;

    public MySQLProvider(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() {
        String host = plugin.configs().config().get().getString("database.mysql.host");
        int port = plugin.configs().config().get().getInt("database.mysql.port");
        String database = plugin.configs().config().get().getString("database.mysql.database");
        String username = plugin.configs().config().get().getString("database.mysql.username");
        String password = plugin.configs().config().get().getString("database.mysql.password");

        boolean useSSL = plugin.configs().config().get().getBoolean("database.mysql.useSSL", false);
        boolean autoReconnect = plugin.configs().config().get().getBoolean("database.mysql.autoReconnect", true);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=" + useSSL
                + "&autoReconnect=" + autoReconnect
                + "&characterEncoding=utf8"
                + "&serverTimezone=UTC";

        try {
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("MySQL Verbindung hergestellt.");
        } catch (SQLException exception) {
            plugin.getLogger().severe("MySQL Verbindung fehlgeschlagen: " + exception.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }

        return connection;
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("MySQL Verbindung geschlossen.");
            }
        } catch (SQLException exception) {
            plugin.getLogger().severe("MySQL konnte nicht geschlossen werden: " + exception.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }
}