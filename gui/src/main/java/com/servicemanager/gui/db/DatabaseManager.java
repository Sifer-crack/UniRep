package com.servicemanager.gui.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Singleton managing the SQLite database connection.
 *  Lazily initialises the connection on first access and
 *  auto-creates the services, executions, and outputs tables
 *  if they do not yet exist. Supports custom connection URLs
 *  for testing with temporary in-memory databases.
 *  Connection lifecycle is managed solely by this class —
 *  DAOs must NOT close the connection themselves. Thread-safe
 *  database access is guaranteed by synchronized blocks in the
 *  DAO layer that serialize all operations on this instance. */
public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);

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
                "windows_command TEXT DEFAULT '', " +
                "working_dir TEXT DEFAULT '')"
            );
            migrateServicesTable(stmt);

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

    private void migrateServicesTable(Statement stmt) {
        try {
            stmt.executeUpdate("ALTER TABLE services ADD COLUMN windows_command TEXT DEFAULT ''");
            log.info("Migrated services table: added windows_command column");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                log.warn("Migration attempt for services table: {}", e.getMessage());
            }
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Failed to close database connection", e);
        }
    }

    public static void reset() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }
}
