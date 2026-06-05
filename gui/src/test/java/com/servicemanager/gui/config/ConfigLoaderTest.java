package com.servicemanager.gui.config;

import com.servicemanager.gui.exception.ConfigLoadException;
import com.servicemanager.gui.model.Service;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.Assert.*;

public class ConfigLoaderTest {

    @Test
    public void loadServicesFromValidFileShouldReturnServices() throws Exception {
        File tempConfig = File.createTempFile("services_", ".json");
        tempConfig.deleteOnExit();
        try (FileWriter w = new FileWriter(tempConfig)) {
            w.write("{\"services\":[{\"name\":\"a\",\"command\":\"echo a\",\"workingDir\":\"\"}]}");
        }

        ConfigLoader loader = new ConfigLoader(tempConfig.getAbsolutePath());
        List<Service> services = loader.loadServices();
        assertEquals(1, services.size());
        assertEquals("a", services.get(0).getName());
    }

    @Test(expected = ConfigLoadException.class)
    public void loadServicesFromMalformedFileShouldThrow() throws Exception {
        File tempConfig = File.createTempFile("services_bad_", ".json");
        tempConfig.deleteOnExit();
        try (FileWriter w = new FileWriter(tempConfig)) {
            w.write("this is not json");
        }

        ConfigLoader loader = new ConfigLoader(tempConfig.getAbsolutePath());
        loader.loadServices();
    }

    @Test
    public void loadServicesFromNonexistentFileShouldReturnEmptyList() throws Exception {
        ConfigLoader loader = new ConfigLoader("/nonexistent/path/services.json");
        List<Service> services = loader.loadServices();
        assertTrue(services.isEmpty());
    }

    @Test
    public void loadServicesWithWindowsCommandShouldParseIt() throws Exception {
        File tempConfig = File.createTempFile("services_win_", ".json");
        tempConfig.deleteOnExit();
        try (FileWriter w = new FileWriter(tempConfig)) {
            w.write("{\"services\":[{\"name\":\"win-svc\",\"command\":\"unix-cmd\",\"windowsCommand\":\"win-cmd\",\"workingDir\":\"\"}]}");
        }

        ConfigLoader loader = new ConfigLoader(tempConfig.getAbsolutePath());
        List<Service> services = loader.loadServices();
        assertEquals(1, services.size());
        assertEquals("win-cmd", services.get(0).getWindowsCommand());
        assertEquals("unix-cmd", services.get(0).getCommand());
    }

    @Test
    public void loadServicesWithoutWindowsCommandShouldLeaveItNull() throws Exception {
        File tempConfig = File.createTempFile("services_no_win_", ".json");
        tempConfig.deleteOnExit();
        try (FileWriter w = new FileWriter(tempConfig)) {
            w.write("{\"services\":[{\"name\":\"svc\",\"command\":\"cmd\",\"workingDir\":\"\"}]}");
        }

        ConfigLoader loader = new ConfigLoader(tempConfig.getAbsolutePath());
        List<Service> services = loader.loadServices();
        assertNull(services.get(0).getWindowsCommand());
    }
}
