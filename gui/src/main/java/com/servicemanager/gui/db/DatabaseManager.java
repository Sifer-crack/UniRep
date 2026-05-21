package com.servicemanager.gui.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;
    private final String url;

    private DatabaseManager() {
        this.url = "jdbc:sqlite:unirep.db";
    }

    private DatabaseManager(String url) {
        this.url = url;
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public static synchronized DatabaseManager getInstance(String url) {
        if (instance == null) {
            instance = new DatabaseManager(url);
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
            createTables();
        }
        return connection;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS services (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE NOT NULL, " +
                "command TEXT NOT NULL, " +
                "working_dir TEXT DEFAULT '')"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS executions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "service_name TEXT NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "finish_time TEXT, " +
                "status TEXT DEFAULT 'RUNNING', " +
                "exit_code INTEGER)"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS outputs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "execution_id INTEGER NOT NULL, " +
                "timestamp TEXT NOT NULL, " +
                "line TEXT NOT NULL, " +
                "stream TEXT DEFAULT 'STDOUT', " +
                "FOREIGN KEY (execution_id) REFERENCES executions(id) ON DELETE CASCADE)"
            );
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}
