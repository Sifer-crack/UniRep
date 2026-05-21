package com.servicemanager.gui.dao;

import com.servicemanager.gui.db.DatabaseManager;
import com.servicemanager.gui.model.Execution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class SQLiteExecutionDAOTest {

    private File tempDb;
    private SQLiteExecutionDAO dao;

    @Before
    public void setUp() throws Exception {
        DatabaseManager.reset();
        tempDb = File.createTempFile("unirep_test_exec_", ".db");
        tempDb.deleteOnExit();
        DatabaseManager.getInstance("jdbc:sqlite:" + tempDb.getAbsolutePath());
        dao = new SQLiteExecutionDAO();
    }

    @After
    public void tearDown() {
        DatabaseManager.reset();
        if (tempDb != null) {
            tempDb.delete();
        }
    }

    @Test
    public void insertShouldSetId() throws Exception {
        Execution exec = new Execution("hello", LocalDateTime.of(2025, 6, 1, 10, 0));
        dao.insert(exec);
        assertTrue(exec.getId() > 0);
    }

    @Test
    public void findByIdShouldReturnExecution() throws Exception {
        Execution exec = new Execution("svc1", LocalDateTime.of(2025, 6, 1, 10, 0));
        dao.insert(exec);

        Execution found = dao.findById(exec.getId());
        assertNotNull(found);
        assertEquals("svc1", found.getServiceName());
        assertEquals("RUNNING", found.getStatus());
    }

    @Test
    public void findByServiceNameShouldReturnExecutions() throws Exception {
        dao.insert(new Execution("svcA", LocalDateTime.of(2025, 6, 1, 10, 0)));
        dao.insert(new Execution("svcA", LocalDateTime.of(2025, 6, 1, 11, 0)));
        dao.insert(new Execution("svcB", LocalDateTime.of(2025, 6, 1, 12, 0)));

        List<Execution> results = dao.findByServiceName("svcA");
        assertEquals(2, results.size());
    }

    @Test
    public void updateFinishShouldSetCompletionFields() throws Exception {
        Execution exec = new Execution("svc1", LocalDateTime.of(2025, 6, 1, 10, 0));
        dao.insert(exec);

        LocalDateTime finish = LocalDateTime.of(2025, 6, 1, 10, 5);
        dao.updateFinish(exec.getId(), finish, 0, "FINISHED");

        Execution updated = dao.findById(exec.getId());
        assertEquals("FINISHED", updated.getStatus());
        assertEquals(Integer.valueOf(0), updated.getExitCode());
        assertNotNull(updated.getFinishTime());
    }
}
