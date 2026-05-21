package com.servicemanager.gui.model;

import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

public class ExecutionTest {

    @Test
    public void constructorShouldSetFields() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 10, 0);
        Execution exec = new Execution("hello", now);
        assertEquals("hello", exec.getServiceName());
        assertEquals(now, exec.getStartTime());
        assertEquals("RUNNING", exec.getStatus());
        assertNull(exec.getFinishTime());
    }

    @Test
    public void fullConstructorShouldSetAllFields() {
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime finish = LocalDateTime.of(2025, 6, 1, 10, 5);
        Execution exec = new Execution(1, "hello", start, finish, "FINISHED", 0);
        assertEquals(1, exec.getId());
        assertEquals("FINISHED", exec.getStatus());
        assertEquals(Integer.valueOf(0), exec.getExitCode());
        assertEquals(finish, exec.getFinishTime());
    }
}
