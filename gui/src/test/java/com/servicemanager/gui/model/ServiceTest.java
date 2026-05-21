package com.servicemanager.gui.model;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ServiceTest {

    @Test
    public void constructorShouldSetFieldsCorrectly() {
        Service service = new Service("test-svc", "echo hello", "/tmp");
        assertEquals("test-svc", service.getName());
        assertEquals("echo hello", service.getCommand());
        assertEquals("/tmp", service.getWorkingDir());
        assertFalse(service.isRunning());
        assertTrue(service.getLogs().isEmpty());
    }

    @Test
    public void statusShouldReflectRunningState() {
        Service service = new Service("test", "echo", "");
        assertEquals("STOPPED", service.getStatus());
        service.setRunning(true);
        assertEquals("RUNNING", service.getStatus());
        service.setRunning(false);
        assertEquals("STOPPED", service.getStatus());
    }

    @Test
    public void addLogShouldAppendToLogList() {
        Service service = new Service("test", "echo", "");
        service.addLog("line1");
        service.addLog("line2");
        assertEquals(2, service.getLogs().size());
        assertEquals("line1", service.getLogs().get(0));
        assertEquals("line2", service.getLogs().get(1));
    }

    @Test
    public void getLogsWithLimitShouldReturnLastNLines() {
        Service service = new Service("test", "echo", "");
        for (int i = 1; i <= 10; i++) {
            service.addLog("line" + i);
        }
        List<String> subset = service.getLogs(3);
        assertEquals(3, subset.size());
        assertEquals("line8", subset.get(0));
        assertEquals("line10", subset.get(2));
    }

    @Test
    public void getLogsWithLimitExceedingTotalShouldReturnAll() {
        Service service = new Service("test", "echo", "");
        service.addLog("only");
        List<String> result = service.getLogs(100);
        assertEquals(1, result.size());
    }

    @Test
    public void getLogsReturnsSameReference() {
        Service service = new Service("test", "echo", "");
        service.addLog("original");
        List<String> result = service.getLogs();
        result.add("mutated");
        assertEquals(2, service.getLogs().size());
    }

    @Test
    public void settersShouldUpdateFields() {
        Service service = new Service("s", "c", "w");
        service.setId(42);
        assertEquals(42, service.getId());
        service.setRunning(true);
        assertTrue(service.isRunning());
        service.setStartTime(java.time.LocalDateTime.of(2025, 1, 1, 0, 0));
        assertNotNull(service.getStartTime());
    }
}
