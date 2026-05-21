package com.servicemanager.gui.dao;

import com.servicemanager.gui.db.DatabaseManager;
import com.servicemanager.gui.model.Service;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class SQLiteServiceDAOTest {

    private File tempDb;
    private SQLiteServiceDAO dao;

    @Before
    public void setUp() throws Exception {
        DatabaseManager.reset();
        tempDb = File.createTempFile("unirep_test_services_", ".db");
        tempDb.deleteOnExit();
        DatabaseManager.getInstance("jdbc:sqlite:" + tempDb.getAbsolutePath());
        dao = new SQLiteServiceDAO();
    }

    @After
    public void tearDown() {
        DatabaseManager.reset();
        if (tempDb != null) {
            tempDb.delete();
        }
    }

    @Test
    public void insertAndFindByName() throws Exception {
        Service service = new Service("test-svc", "echo test", "/tmp");
        dao.insert(service);

        Service found = dao.findByName("test-svc");
        assertNotNull(found);
        assertEquals("test-svc", found.getName());
        assertEquals("echo test", found.getCommand());
        assertEquals("/tmp", found.getWorkingDir());
    }

    @Test
    public void insertAndFindAll() throws Exception {
        dao.insert(new Service("svc1", "cmd1", ""));
        dao.insert(new Service("svc2", "cmd2", "/var"));

        List<Service> all = dao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void findByNameReturnsNullForMissing() throws Exception {
        assertNull(dao.findByName("nonexistent"));
    }

    @Test
    public void updateShouldModifyService() throws Exception {
        dao.insert(new Service("upd-svc", "old-cmd", ""));
        Service service = new Service("upd-svc", "new-cmd", "/new/dir");
        dao.update(service);

        Service found = dao.findByName("upd-svc");
        assertEquals("new-cmd", found.getCommand());
        assertEquals("/new/dir", found.getWorkingDir());
    }

    @Test
    public void deleteShouldRemoveService() throws Exception {
        dao.insert(new Service("del-svc", "cmd", ""));
        dao.delete("del-svc");
        assertNull(dao.findByName("del-svc"));
    }
}
