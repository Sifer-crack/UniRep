package com.servicemanager.gui.dao;

import com.servicemanager.gui.model.Execution;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Data access interface for the executions table.
 *  Supports creating new execution records, querying by ID or
 *  service name, and updating finish details when the
 *  underlying process completes. */
public interface ExecutionDAO {

    void insert(Execution execution) throws SQLException;

    Execution findById(int id) throws SQLException;

    List<Execution> findByServiceName(String serviceName) throws SQLException;

    List<Execution> findAll() throws SQLException;

    void updateFinish(int id, LocalDateTime finishTime, int exitCode, String status) throws SQLException;
}
