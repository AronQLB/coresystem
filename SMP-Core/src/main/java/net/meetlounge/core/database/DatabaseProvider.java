package net.meetlounge.core.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseProvider {

    void connect();

    Connection getConnection() throws SQLException;

    void disconnect();

    boolean isConnected();
}