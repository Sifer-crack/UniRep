package com.servicemanager.gui.dao;

import com.servicemanager.gui.model.Output;
import java.sql.SQLException;
import java.util.List;

public interface OutputDAO {

    void insert(Output output) throws SQLException;

    List<Output> findByExecutionId(int executionId) throws SQLException;
}
