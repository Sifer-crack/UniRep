package com.servicemanager.gui.service;

import com.servicemanager.gui.config.ConfigLoader;
import com.servicemanager.gui.dao.ServiceDAO;
import com.servicemanager.gui.exception.ConfigLoadException;
import com.servicemanager.gui.model.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServicesLoader {

    private final ConfigLoader configLoader;
    private final ServiceDAO serviceDAO;

    public ServicesLoader(ConfigLoader configLoader, ServiceDAO serviceDAO) {
        this.configLoader = configLoader;
        this.serviceDAO = serviceDAO;
    }

    public List<Service> loadAllServices() throws ConfigLoadException {
        List<Service> allServices = new ArrayList<>();

        List<Service> predefined = configLoader.loadServices();
        allServices.addAll(predefined);

        try {
            List<Service> customServices = serviceDAO.findAll();
            for (Service custom : customServices) {
                boolean duplicate = false;
                for (Service existing : allServices) {
                    if (existing.getName().equals(custom.getName())) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    allServices.add(custom);
                }
            }
        } catch (SQLException e) {
            throw new ConfigLoadException("Failed to load custom services from database");
        }

        return allServices;
    }
}
