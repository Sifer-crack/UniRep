package com.servicemanager.gui.dao;

import com.servicemanager.gui.db.DatabaseManager;
import com.servicemanager.gui.model.Execution;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** SQLite JDBC implementation of ExecutionDAO.
 *  Stores timestamps as formatted date-time strings (yyyy-MM-dd HH:mm:ss)
 *  and retrieves the last inserted row ID after each insert to
 *  keep Execution objects in sync with the database. */
public class SQLiteExecutionDAO implements ExecutionDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DatabaseManager dbManager;

    public SQLiteExecutionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public SQLiteExecutionDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void insert(Execution execution) throws SQLException {
        String sql = "INSERT INTO executions (service_name, start_time, status) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, execution.getServiceName());
            stmt.setString(2, execution.getStartTime().format(FORMATTER));
            stmt.setString(3, execution.getStatus());
            stmt.executeUpdate();

            try (Statement idStmt = conn.createStatement();
                 ResultSet rs = idStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    execution.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Execution findById(int id) throws SQLException {
        String sql = "SELECT * FROM executions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapExecution(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Execution> findByServiceName(String serviceName) throws SQLException {
        List<Execution> executions = new ArrayList<>();
        String sql = "SELECT * FROM executions WHERE service_name = ? ORDER BY start_time DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serviceName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    executions.add(mapExecution(rs));
                }
            }
        }
        return executions;
    }

    @Override
    public List<Execution> findAll() throws SQLException {
        List<Execution> executions = new ArrayList<>();
        String sql = "SELECT * FROM executions ORDER BY start_time DESC";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                executions.add(mapExecution(rs));
            }
        }
        return executions;
    }

    @Override
    public void updateFinish(int id, LocalDateTime finishTime, int exitCode, String status) throws SQLException {
        String sql = "UPDATE executions SET finish_time = ?, exit_code = ?, status = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, finishTime.format(FORMATTER));
            stmt.setInt(2, exitCode);
            stmt.setString(3, status);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

    private Execution mapExecution(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String serviceName = rs.getString("service_name");
        LocalDateTime startTime = LocalDateTime.parse(rs.getString("start_time"), FORMATTER);
        String finishTimeStr = rs.getString("finish_time");
        LocalDateTime finishTime = finishTimeStr != null
            ? LocalDateTime.parse(finishTimeStr, FORMATTER) : null;
        String status = rs.getString("status");
        int exitCode = rs.getInt("exit_code");
        Integer exitCodeObj = rs.wasNull() ? null : exitCode;

        Execution execution = new Execution(id, serviceName, startTime, finishTime, status, exitCodeObj);
        return execution;
    }
}
