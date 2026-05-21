package com.servicemanager.gui.service;

import com.servicemanager.gui.dao.ExecutionDAO;
import com.servicemanager.gui.dao.OutputDAO;
import com.servicemanager.gui.dao.ServiceDAO;
import com.servicemanager.gui.exception.*;
import com.servicemanager.gui.model.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServiceManagerTest {

    @Mock private ServiceDAO serviceDAO;
    @Mock private ExecutionDAO executionDAO;
    @Mock private OutputDAO outputDAO;
    @Mock private ServiceProcessor processor;
    @Mock private ServicesLoader servicesLoader;

    private ServiceManager serviceManager;
    private Service testService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        testService = new Service("test-svc", "echo hello", "/tmp");
        List<Service> serviceList = new ArrayList<>();
        serviceList.add(testService);

        when(servicesLoader.loadAllServices()).thenReturn(serviceList);

        serviceManager = new ServiceManager(serviceDAO, executionDAO, outputDAO, processor, servicesLoader);
    }

    @Test
    public void listServicesShouldReturnAllServices() {
        when(processor.isAlive(any())).thenReturn(false);
        String result = serviceManager.listServices();
        assertTrue(result.contains("test-svc"));
        assertTrue(result.contains("STOPPED"));
    }

    @Test
    public void startServiceShouldStartAndReturnMessage() throws Exception {
        when(processor.isAlive(any())).thenReturn(false);
        when(processor.start(any())).thenReturn(Runtime.getRuntime().exec("echo test"));

        String result = serviceManager.startService("test-svc");
        assertTrue(result.contains("Admin:"));
        assertTrue(result.contains("started"));
    }

    @Test(expected = ServiceAlreadyRunningException.class)
    public void startServiceWhenAlreadyRunningShouldThrow() throws Exception {
        Process mockProcess = mock(Process.class);
        testService.setProcess(mockProcess);
        when(processor.isAlive(mockProcess)).thenReturn(true);

        serviceManager.startService("test-svc");
    }

    @Test
    public void stopServiceShouldStopAndReturnMessage() throws Exception {
        Process mockProcess = mock(Process.class);
        testService.setProcess(mockProcess);
        when(processor.isAlive(mockProcess)).thenReturn(true);
        doNothing().when(processor).stop(mockProcess);

        String result = serviceManager.stopService("test-svc");
        assertTrue(result.contains("Stopped"));
        assertFalse(testService.isRunning());
    }

    @Test(expected = ServiceNotRunningException.class)
    public void stopServiceWhenNotRunningShouldThrow() throws Exception {
        Process mockProcess = mock(Process.class);
        testService.setProcess(mockProcess);
        when(processor.isAlive(mockProcess)).thenReturn(false);

        serviceManager.stopService("test-svc");
    }

    @Test(expected = ServiceNotFoundException.class)
    public void startNonexistentServiceShouldThrow() throws Exception {
        serviceManager.startService("does-not-exist");
    }

    @Test
    public void restartServiceShouldCallStopThenStart() throws Exception {
        Process mockProcess = mock(Process.class);
        testService.setProcess(mockProcess);
        when(processor.isAlive(mockProcess)).thenReturn(true);
        doNothing().when(processor).stop(mockProcess);

        serviceManager.restartService("test-svc");
        verify(processor, times(1)).stop(mockProcess);
    }

    @Test
    public void createCustomServiceShouldInsertAndAddToList() throws Exception {
        serviceManager.createCustomService("new-svc", "echo new", "/home");
        verify(serviceDAO, times(1)).insert(any(Service.class));

        String list = serviceManager.listServices();
        assertTrue(list.contains("new-svc"));
    }

    @Test(expected = DuplicateServiceException.class)
    public void createDuplicateServiceShouldThrow() throws Exception {
        serviceManager.createCustomService("test-svc", "echo conflict", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createServiceWithEmptyNameShouldThrow() throws Exception {
        serviceManager.createCustomService("", "echo", "");
    }

    @Test
    public void getServiceStatusShouldReturnFormattedString() throws Exception {
        when(processor.isAlive(any())).thenReturn(false);
        String status = serviceManager.getServiceStatus("test-svc");
        assertTrue(status.contains("test-svc"));
        assertTrue(status.contains("STOPPED"));
        assertTrue(status.contains("echo hello"));
    }
}
