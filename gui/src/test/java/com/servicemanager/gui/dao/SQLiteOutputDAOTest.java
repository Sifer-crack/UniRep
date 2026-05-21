package com.servicemanager.gui.dao;

import com.servicemanager.gui.db.DatabaseManager;
import com.servicemanager.gui.model.Execution;
import com.servicemanager.gui.model.Output;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class SQLiteOutputDAOTest {

    private File tempDb;
    private SQLiteOutputDAO outputDAO;
    private SQLiteExecutionDAO executionDAO;
    private int executionId;

    @Before
    public void setUp() throws Exception {
        DatabaseManager.reset();
        tempDb = File.createTempFile("unirep_test_out_", ".db");
        tempDb.deleteOnExit();
        DatabaseManager.getInstance("jdbc:sqlite:" + tempDb.getAbsolutePath());
        outputDAO = new SQLiteOutputDAO();
        executionDAO = new SQLiteExecutionDAO();

        Execution exec = new Execution("svc1", LocalDateTime.now());
        executionDAO.insert(exec);
        executionId = exec.getId();
    }

    @After
    public void tearDown() {
        DatabaseManager.reset();
        if (tempDb != null) {
            tempDb.delete();
        }
    }

    @Test
    public void insertAndFindByExecutionId() throws Exception {
        outputDAO.insert(new Output(executionId, "2025-06-01 10:00:00", "line1", "STDOUT"));
        outputDAO.insert(new Output(executionId, "2025-06-01 10:00:01", "line2", "STDOUT"));

        List<Output> outputs = outputDAO.findByExecutionId(executionId);
        assertEquals(2, outputs.size());
        assertEquals("line1", outputs.get(0).getLine());
    }

    @Test
    public void findByExecutionIdReturnsEmptyForNonexistent() throws Exception {
        List<Output> outputs = outputDAO.findByExecutionId(999);
        assertTrue(outputs.isEmpty());
    }
}
