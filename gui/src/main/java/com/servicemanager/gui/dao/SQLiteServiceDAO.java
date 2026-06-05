package com.servicemanager.gui.dao;

import com.servicemanager.gui.db.DatabaseManager;
import com.servicemanager.gui.model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** SQLite JDBC implementation of ServiceDAO.
 *  Uses PreparedStatements for all CRUD operations and
 *  maps ResultSet rows to Service model objects.
 *  All database operations synchronize on the shared DatabaseManager
 *  instance to prevent concurrent-close races when multiple service
 *  IO threads write to the database at the same time. */
public class SQLiteServiceDAO implements ServiceDAO {

    private final DatabaseManager dbManager;

    public SQLiteServiceDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public SQLiteServiceDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void insert(Service service) throws SQLException {
        String sql = "INSERT INTO services (name, command, windows_command, working_dir) VALUES (?, ?, ?, ?)";
        synchronized (dbManager) {
            Connection conn = dbManager.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, service.getName());
                stmt.setString(2, service.getCommand());
                stmt.setString(3, service.getWindowsCommand());
                stmt.setString(4, service.getWorkingDir());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public Service findByName(String name) throws SQLException {
        String sql = "SELECT * FROM services WHERE name = ?";
        synchronized (dbManager) {
            Connection conn = dbManager.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapService(rs);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Service> findAll() throws SQLException {
        List<Service> result = new ArrayList<>();
        String sql = "SELECT * FROM services";
        synchronized (dbManager) {
            Connection conn = dbManager.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    result.add(mapService(rs));
                }
            }
        }
        return result;
    }

    @Override
    public void update(Service service) throws SQLException {
        String sql = "UPDATE services SET command = ?, windows_command = ?, working_dir = ? WHERE name = ?";
        synchronized (dbManager) {
            Connection conn = dbManager.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, service.getCommand());
                stmt.setString(2, service.getWindowsCommand());
                stmt.setString(3, service.getWorkingDir());
                stmt.setString(4, service.getName());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void delete(String name) throws SQLException {
        String sql = "DELETE FROM services WHERE name = ?";
        synchronized (dbManager) {
            Connection conn = dbManager.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.executeUpdate();
            }
        }
    }

    private Service mapService(ResultSet rs) throws SQLException {
        Service service = new Service(
            rs.getString("name"),
            rs.getString("command"),
            rs.getString("windows_command"),
            rs.getString("working_dir")
        );
        service.setId(rs.getInt("id"));
        return service;
    }
}
