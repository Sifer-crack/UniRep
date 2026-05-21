package com.servicemanager.gui.exception;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExceptionTest {

    @Test
    public void configLoadExceptionShouldHaveHint() {
        ConfigLoadException e = new ConfigLoadException("bad config");
        assertEquals("bad config", e.getMessage());
        assertTrue(e.getRecoveryHint().toLowerCase().contains("services.json"));
    }

    @Test
    public void serviceNotFoundExceptionShouldHaveHint() {
        ServiceNotFoundException e = new ServiceNotFoundException("missing");
        assertTrue(e.getMessage().contains("missing"));
        assertTrue(e.getRecoveryHint().toLowerCase().contains("services.json"));
    }

    @Test
    public void serviceAlreadyRunningExceptionShouldHaveHint() {
        ServiceAlreadyRunningException e = new ServiceAlreadyRunningException("running-svc");
        assertTrue(e.getMessage().contains("running-svc"));
        assertTrue(e.getRecoveryHint().toLowerCase().contains("restart"));
    }

    @Test
    public void serviceNotRunningExceptionShouldHaveHint() {
        ServiceNotRunningException e = new ServiceNotRunningException("stopped-svc");
        assertTrue(e.getMessage().contains("stopped-svc"));
        assertEquals("Use 'start' command first", e.getRecoveryHint());
    }

    @Test
    public void invalidCommandExceptionShouldHaveHint() {
        InvalidCommandException e = new InvalidCommandException("badcmd");
        assertTrue(e.getMessage().contains("badcmd"));
        assertEquals("badcmd", e.getCommand());
        assertTrue(e.getRecoveryHint().toLowerCase().contains("help"));
    }

    @Test
    public void duplicateServiceExceptionShouldHaveHint() {
        DuplicateServiceException e = new DuplicateServiceException("dup-name");
        assertTrue(e.getMessage().contains("dup-name"));
        assertTrue(e.getRecoveryHint().toLowerCase().contains("different"));
    }

    @Test
    public void serviceExceptionIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(ServiceException.class.getModifiers()));
    }
}
