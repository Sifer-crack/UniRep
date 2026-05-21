package com.servicemanager.gui.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class OutputTest {

    @Test
    public void constructorShouldSetFields() {
        Output output = new Output(1, "2025-06-01 10:00:00", "Hello World", "STDOUT");
        assertEquals(1, output.getExecutionId());
        assertEquals("Hello World", output.getLine());
        assertEquals("STDOUT", output.getStream());
    }

    @Test
    public void fullConstructorShouldSetAllFields() {
        Output output = new Output(10, 1, "2025-06-01 10:00:00", "Error!", "STDERR");
        assertEquals(10, output.getId());
        assertEquals(1, output.getExecutionId());
        assertEquals("Error!", output.getLine());
        assertEquals("STDERR", output.getStream());
    }
}
