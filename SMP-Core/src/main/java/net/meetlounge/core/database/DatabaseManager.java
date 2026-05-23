package net.meetlounge.core.database;

import net.meetlounge.core.Core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private final Core plugin;
    private DatabaseProvider provider;

    public DatabaseManager(Core plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        DatabaseType type = DatabaseType.fromString(
                plugin.configs().config().get().getString("database.type")
        );

        if (type == DatabaseType.MYSQL || type == DatabaseType.MARIADB) {
            provider = new MySQLProvider(plugin);
        } else {
            throw new IllegalStateException("Aktuell ist nur MYSQL/MARIADB eingerichtet.");
        }

        provider.connect();
    }

    public Connection getConnection() throws SQLException {
        return provider.getConnection();
    }

    public void createTables() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // =========================================
            // Player Data
            // =========================================

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_data (
                    uuid VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(16) NOT NULL,
                    level INT NOT NULL DEFAULT 1,
                    xp BIGINT NOT NULL DEFAULT 0,
                    first_join BIGINT NOT NULL,
                    last_join BIGINT NOT NULL,
                    playtime BIGINT NOT NULL DEFAULT 0,
                    coins DOUBLE NOT NULL DEFAULT 0,
                    rank_name VARCHAR(32) NOT NULL DEFAULT 'player'
                )
            """);

            // =========================================
            // Clans
            // =========================================

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(32) NOT NULL UNIQUE,
                    tag VARCHAR(4) NOT NULL UNIQUE,
                    owner_uuid VARCHAR(36) NOT NULL,
                    owner_name VARCHAR(16) NOT NULL,
                    kills INT NOT NULL DEFAULT 0,
                    deaths INT NOT NULL DEFAULT 0,
                    bank DOUBLE NOT NULL DEFAULT 0,
                    created_at BIGINT NOT NULL
                )
            """);

            // =========================================
            // Clan Members
            // =========================================

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clan_members (
                    player_uuid VARCHAR(36) PRIMARY KEY,
                    player_name VARCHAR(16) NOT NULL,
                    clan_id INT NOT NULL,
                    role VARCHAR(16) NOT NULL DEFAULT 'member',
                    joined_at BIGINT NOT NULL,
                    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
                )
            """);



            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS bans (
            uuid VARCHAR(36) PRIMARY KEY,
            name VARCHAR(16) NOT NULL,
            reason_id INT NOT NULL,
            reason VARCHAR(64) NOT NULL,
            staff VARCHAR(16) NOT NULL,
            created_at BIGINT NOT NULL,
            expires_at BIGINT NOT NULL,
            active BOOLEAN NOT NULL DEFAULT TRUE
             )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS reports (
            id INT AUTO_INCREMENT PRIMARY KEY,
            reporter_uuid VARCHAR(36) NOT NULL,
            reporter_name VARCHAR(16) NOT NULL,
            target_uuid VARCHAR(36) NOT NULL,
            target_name VARCHAR(16) NOT NULL,
            reason VARCHAR(64) NOT NULL,
            created_at BIGINT NOT NULL,
            open BOOLEAN NOT NULL DEFAULT TRUE
             )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS homes (
            uuid VARCHAR(36) NOT NULL,
            name VARCHAR(32) NOT NULL,
            world VARCHAR(64) NOT NULL,
            x DOUBLE NOT NULL,
            y DOUBLE NOT NULL,
            z DOUBLE NOT NULL,
            yaw FLOAT NOT NULL,
            pitch FLOAT NOT NULL,
            PRIMARY KEY (uuid, name)
            )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS warps (
            name VARCHAR(32) PRIMARY KEY,
            world VARCHAR(64) NOT NULL,
            x DOUBLE NOT NULL,
            y DOUBLE NOT NULL,
            z DOUBLE NOT NULL,
            yaw FLOAT NOT NULL,
            pitch FLOAT NOT NULL
            )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS spawn_location (
            id INT PRIMARY KEY,
            world VARCHAR(64) NOT NULL,
            x DOUBLE NOT NULL,
            y DOUBLE NOT NULL,
            z DOUBLE NOT NULL,
            yaw FLOAT NOT NULL,
            pitch FLOAT NOT NULL
            )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS mutes (
            uuid VARCHAR(36) PRIMARY KEY,
            name VARCHAR(16) NOT NULL,
            reason VARCHAR(64) NOT NULL,
            staff VARCHAR(16) NOT NULL,
            created_at BIGINT NOT NULL,
            expires_at BIGINT NOT NULL,
            active BOOLEAN NOT NULL
            )
            """);
            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS regions (
            name VARCHAR(32) PRIMARY KEY,
            world VARCHAR(64) NOT NULL,

            type VARCHAR(16) NOT NULL DEFAULT 'CUBOID',

            min_x INT NOT NULL,
            min_y INT NOT NULL,
            min_z INT NOT NULL,

            max_x INT NOT NULL,
            max_y INT NOT NULL,
            max_z INT NOT NULL,

            center_x INT NOT NULL DEFAULT 0,
            center_z INT NOT NULL DEFAULT 0,
            radius INT NOT NULL DEFAULT 0,

            pvp BOOLEAN NOT NULL DEFAULT TRUE,
            build_flag BOOLEAN NOT NULL DEFAULT TRUE,
            break_flag BOOLEAN NOT NULL DEFAULT TRUE,
            mob_spawn BOOLEAN NOT NULL DEFAULT TRUE,
            crop_trample BOOLEAN NOT NULL DEFAULT TRUE,
            fall_damage BOOLEAN NOT NULL DEFAULT TRUE
            )
            """);


            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS clan_claims (
            clan_id INT PRIMARY KEY,
            clan_name VARCHAR(32) NOT NULL,
            world VARCHAR(64) NOT NULL,
            center_x INT NOT NULL,
            center_z INT NOT NULL,
            min_x INT NOT NULL,
            min_z INT NOT NULL,
            max_x INT NOT NULL,
            max_z INT NOT NULL,
            created_at BIGINT NOT NULL
            )
            """);


            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS npcs (
            id VARCHAR(32) PRIMARY KEY,
            type VARCHAR(32) NOT NULL DEFAULT 'ALLAY',
            color VARCHAR(32) NOT NULL DEFAULT 'WHITE',
            world VARCHAR(64) NOT NULL,
            x DOUBLE NOT NULL,
            y DOUBLE NOT NULL,
            z DOUBLE NOT NULL,
            yaw FLOAT NOT NULL,
            pitch FLOAT NOT NULL,
            `command` TEXT NOT NULL
            )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS auction_house (
            id INT AUTO_INCREMENT PRIMARY KEY,
            seller_uuid VARCHAR(36) NOT NULL,
            seller_name VARCHAR(16) NOT NULL,
            item_data LONGTEXT NOT NULL,
            price DOUBLE NOT NULL,
            created_at BIGINT NOT NULL,
            expires_at BIGINT NOT NULL,
            reminder_sent BOOLEAN NOT NULL DEFAULT FALSE
            )
            """);

            statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS player_data (
            uuid VARCHAR(36) PRIMARY KEY,
            name VARCHAR(16) NOT NULL,
            first_join BIGINT NOT NULL,
            last_join BIGINT NOT NULL,
            playtime BIGINT NOT NULL DEFAULT 0,
            coins DOUBLE NOT NULL DEFAULT 0,
            rank_name VARCHAR(32) NOT NULL DEFAULT 'player',
            level INT NOT NULL DEFAULT 1,
            xp BIGINT NOT NULL DEFAULT 0
            )
            """);

            // =========================================
            // Fix existing databases automatically
            // =========================================

            try {
                statement.executeUpdate("""
                    ALTER TABLE clans
                    MODIFY tag VARCHAR(4) NOT NULL UNIQUE
                """);
            } catch (SQLException ignored) {
                // Bereits korrekt
            }

            try {
                statement.executeUpdate("""
                    ALTER TABLE npcs
                    DROP COLUMN skin
                """);
            } catch (SQLException ignored) {
                // Alte Skin-Spalte existiert nicht mehr oder wurde bereits entfernt
            }

            try {
                statement.executeUpdate("""
                    ALTER TABLE npcs
                    MODIFY type VARCHAR(32) NOT NULL DEFAULT 'ALLAY'
                """);
            } catch (SQLException ignored) {
                // Bereits korrekt
            }

            try {
                statement.executeUpdate("""
                    ALTER TABLE npcs
                    MODIFY color VARCHAR(32) NOT NULL DEFAULT 'WHITE'
                """);
            } catch (SQLException ignored) {
                // Bereits korrekt
            }

            plugin.debug().info("Alle Datenbanktabellen wurden geladen.");

        } catch (SQLException exception) {
            plugin.getLogger().severe("Tabellen konnten nicht erstellt werden: " + exception.getMessage());
        }
    }

    public void disconnect() {
        if (provider != null) {
            provider.disconnect();
        }
    }

    public boolean isConnected() {
        return provider != null && provider.isConnected();
    }
}
