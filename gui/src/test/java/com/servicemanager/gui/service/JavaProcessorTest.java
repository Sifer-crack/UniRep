package com.servicemanager.gui.service;

import com.servicemanager.gui.model.Service;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JavaProcessorTest {

    private String realOsName;

    @Before
    public void setUp() {
        realOsName = JavaProcessor.osName;
    }

    @After
    public void tearDown() {
        JavaProcessor.osName = realOsName;
    }

    @Test
    public void onWindowsShouldUseWindowsCommandWhenSet() {
        JavaProcessor.osName = "Windows 10";
        Service service = new Service("test", "unix-cmd", "win-cmd", "");
        assertEquals("win-cmd", JavaProcessor.resolveCommand(service));
    }

    @Test
    public void onWindowsShouldFallBackToCommandWhenNoWindowsCommand() {
        JavaProcessor.osName = "Windows 10";
        Service service = new Service("test", "unix-cmd", null, "");
        assertEquals("unix-cmd", JavaProcessor.resolveCommand(service));
    }

    @Test
    public void onWindowsShouldFallBackToCommandWhenWindowsCommandIsEmptyString() {
        JavaProcessor.osName = "Windows 10";
        Service service = new Service("test", "unix-cmd", "", "");
        assertEquals("unix-cmd", JavaProcessor.resolveCommand(service));
    }

    @Test
    public void onUnixShouldUseCommandRegardlessOfWindowsCommand() {
        JavaProcessor.osName = "Linux";
        Service service = new Service("test", "unix-cmd", "win-cmd", "");
        assertEquals("unix-cmd", JavaProcessor.resolveCommand(service));
    }

    @Test
    public void onMacShouldUseCommandRegardlessOfWindowsCommand() {
        JavaProcessor.osName = "Mac OS X";
        Service service = new Service("test", "unix-cmd", "win-cmd", "");
        assertEquals("unix-cmd", JavaProcessor.resolveCommand(service));
    }

    @Test
    public void isWindowsShouldReturnTrueForWindows() {
        JavaProcessor.osName = "Windows 10";
        assertTrue(JavaProcessor.isWindows());
    }

    @Test
    public void isWindowsShouldReturnFalseForLinux() {
        JavaProcessor.osName = "Linux";
        assertFalse(JavaProcessor.isWindows());
    }

    @Test
    public void isWindowsShouldReturnFalseForMac() {
        JavaProcessor.osName = "Mac OS X";
        assertFalse(JavaProcessor.isWindows());
    }
}
