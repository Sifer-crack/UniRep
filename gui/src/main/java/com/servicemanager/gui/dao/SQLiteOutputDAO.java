package com.servicemanager.gui.dao;

import com.servicemanager.gui.db.DatabaseManager;
import com.servicemanager.gui.model.Output;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** SQLite JDBC implementation of OutputDAO.
 *  Persists each output line with its execution foreign key,
 *  timestamp, content, and stream type. Ordered by ID ascending
 *  to preserve the order lines were captured. */
public class SQLiteOutputDAO implements OutputDAO {

    private final DatabaseManager dbManager;

    public SQLiteOutputDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public SQLiteOutputDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public void insert(Output output) throws SQLException {
        String sql = "INSERT INTO outputs (execution_id, timestamp, line, stream) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, output.getExecutionId());
            stmt.setString(2, output.getTimestamp());
            stmt.setString(3, output.getLine());
            stmt.setString(4, output.getStream());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Output> findByExecutionId(int executionId) throws SQLException {
        List<Output> outputs = new ArrayList<>();
        String sql = "SELECT * FROM outputs WHERE execution_id = ? ORDER BY id ASC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, executionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    outputs.add(new Output(
                        rs.getInt("id"),
                        rs.getInt("execution_id"),
                        rs.getString("timestamp"),
                        rs.getString("line"),
                        rs.getString("stream")
                    ));
                }
            }
        }
        return outputs;
    }
}
