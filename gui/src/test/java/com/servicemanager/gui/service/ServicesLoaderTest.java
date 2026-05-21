package com.servicemanager.gui.service;

import com.servicemanager.gui.config.ConfigLoader;
import com.servicemanager.gui.dao.ServiceDAO;
import com.servicemanager.gui.model.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServicesLoaderTest {

    @Mock private ConfigLoader configLoader;
    @Mock private ServiceDAO serviceDAO;

    private ServicesLoader loader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loader = new ServicesLoader(configLoader, serviceDAO);
    }

    @Test
    public void loadAllServicesShouldMergeJsonAndDb() throws Exception {
        List<Service> jsonServices = new ArrayList<>();
        jsonServices.add(new Service("json-svc", "echo json", ""));
        when(configLoader.loadServices()).thenReturn(jsonServices);

        List<Service> dbServices = new ArrayList<>();
        dbServices.add(new Service("custom-svc", "echo custom", "/tmp"));
        when(serviceDAO.findAll()).thenReturn(dbServices);

        List<Service> all = loader.loadAllServices();
        assertEquals(2, all.size());
    }

    @Test
    public void loadAllServicesShouldSkipDbDuplicates() throws Exception {
        List<Service> jsonServices = new ArrayList<>();
        jsonServices.add(new Service("shared-name", "echo json", ""));
        when(configLoader.loadServices()).thenReturn(jsonServices);

        List<Service> dbServices = new ArrayList<>();
        dbServices.add(new Service("shared-name", "echo custom", ""));
        when(serviceDAO.findAll()).thenReturn(dbServices);

        List<Service> all = loader.loadAllServices();
        assertEquals(1, all.size());
    }

    @Test
    public void loadAllServicesWithEmptyJsonShouldReturnDbOnly() throws Exception {
        when(configLoader.loadServices()).thenReturn(new ArrayList<>());

        List<Service> dbServices = new ArrayList<>();
        dbServices.add(new Service("db-only", "echo db", ""));
        when(serviceDAO.findAll()).thenReturn(dbServices);

        List<Service> all = loader.loadAllServices();
        assertEquals(1, all.size());
    }
}
