package com.servicemanager.gui.dao;

import com.servicemanager.gui.model.Output;
import java.sql.SQLException;
import java.util.List;

/** Data access interface for the outputs table.
 *  Supports inserting captured output lines and retrieving
 *  all lines for a given execution in chronological order. */
public interface OutputDAO {

    void insert(Output output) throws SQLException;

    List<Output> findByExecutionId(int executionId) throws SQLException;
}
