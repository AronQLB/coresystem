package net.meetlounge.core.database;

public enum DatabaseType {

    MYSQL,
    MARIADB;

    public static DatabaseType fromString(String value) {
        if (value == null || value.isBlank()) {
            return MYSQL;
        }

        try {
            return DatabaseType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return MYSQL;
        }
    }
}