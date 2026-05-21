package com.servicemanager.gui.service;

import com.servicemanager.gui.config.ConfigLoader;
import com.servicemanager.gui.dao.*;
import com.servicemanager.gui.exception.*;
import com.servicemanager.gui.model.Execution;
import com.servicemanager.gui.model.Output;
import com.servicemanager.gui.model.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManager {

    private final List<Service> services;
    private final ServiceDAO serviceDAO;
    private final ExecutionDAO executionDAO;
    private final OutputDAO outputDAO;
    private final ServiceProcessor processor;
    private final ServicesLoader servicesLoader;
    private final List<ServiceObserver> observers;

    public ServiceManager() throws ConfigLoadException {
        this(new SQLiteDAOFactory());
    }

    public ServiceManager(DAOFactory daoFactory) throws ConfigLoadException {
        this.serviceDAO = daoFactory.createServiceDAO();
        this.executionDAO = daoFactory.createExecutionDAO();
        this.outputDAO = daoFactory.createOutputDAO();
        this.processor = new JavaProcessor(executionDAO, outputDAO, this);
        this.servicesLoader = new ServicesLoader(new ConfigLoader(), serviceDAO);
        this.services = servicesLoader.loadAllServices();
        this.observers = new CopyOnWriteArrayList<>();
    }

    public ServiceManager(ServiceDAO serviceDAO, ExecutionDAO executionDAO,
                          OutputDAO outputDAO, ServiceProcessor processor,
                          ServicesLoader servicesLoader) throws ConfigLoadException {
        this.serviceDAO = serviceDAO;
        this.executionDAO = executionDAO;
        this.outputDAO = outputDAO;
        this.processor = processor;
        this.servicesLoader = servicesLoader;
        this.services = servicesLoader.loadAllServices();
        this.observers = new CopyOnWriteArrayList<>();
    }

    public void addObserver(ServiceObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ServiceObserver observer) {
        observers.remove(observer);
    }

    void notifyServiceFinished(String serviceName, int exitCode) {
        String status = exitCode == 0 ? "finished" : "failed";
        try {
            Service service = findService(serviceName);
            notifyObservers(service, status, "Exit code: " + exitCode);
        } catch (Exception e) {

        }
    }

    private void notifyObservers(Service service, String eventType, String message) {
        for (ServiceObserver observer : observers) {
            observer.onServiceEvent(eventType, service.getName(), message);
        }
    }

    public String listServices() {
        StringBuilder sb = new StringBuilder();
        for (Service service : services) {
            boolean alive = processor.isAlive(service.getProcess());
            service.setRunning(alive);
            sb.append(String.format("%-20s %s%n", service.getName(), service.getStatus()));
        }
        return sb.toString();
    }

    public List<Service> getServices() {
        for (Service service : services) {
            boolean alive = processor.isAlive(service.getProcess());
            service.setRunning(alive);
        }
        return services;
    }

    public String startService(String name) throws ServiceException {
        Service service = findService(name);
        if (processor.isAlive(service.getProcess())) {
            throw new ServiceAlreadyRunningException(name);
        }
        try {
            processor.start(service);
            notifyObservers(service, "started", "Service started");
            return "Admin:" + name + " service started";
        } catch (Exception e) {
            throw new RuntimeException("Failed to start: " + e.getMessage());
        }
    }

    public String stopService(String name) throws ServiceException {
        Service service = findService(name);
        if (!processor.isAlive(service.getProcess())) {
            throw new ServiceNotRunningException(name);
        }
        try {
            processor.stop(service.getProcess());
            service.setRunning(false);
            service.setProcess(null);
            service.setFinishedTime(java.time.LocalDateTime.now());
            notifyObservers(service, "stopped", "Service stopped");
            return "Stopping " + name + "...\nStopped successfully";
        } catch (Exception e) {
            throw new RuntimeException("Failed to stop: " + e.getMessage());
        }
    }

    public String restartService(String name) throws ServiceException {
        if (processor.isAlive(findService(name).getProcess())) {
            stopService(name);
        }
        return startService(name);
    }

    public String getServiceStatus(String name) throws ServiceNotFoundException {
        Service service = findService(name);
        boolean alive = processor.isAlive(service.getProcess());
        service.setRunning(alive);

        StringBuilder sb = new StringBuilder();
        sb.append("Service: ").append(service.getName()).append("\n");
        sb.append("Status: ").append(service.getStatus()).append("\n");
        sb.append("Command: ").append(service.getCommand()).append("\n");
        if (service.getWorkingDir() != null && !service.getWorkingDir().isEmpty()) {
            sb.append("Working Dir: ").append(service.getWorkingDir()).append("\n");
        }
        if (service.getStartTime() != null) {
            sb.append("Started: ").append(service.getStartTime()).append("\n");
        }
        if (service.getFinishedTime() != null) {
            sb.append("Finished: ").append(service.getFinishedTime()).append("\n");
        }
        return sb.toString();
    }

    public String getServiceLogs(String name, int lines) throws ServiceNotFoundException {
        Service service = findService(name);

        List<String> logs = new ArrayList<>();
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
        File logFile = new File("logs", sanitizedName + ".log");

        if (logFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                List<String> allLogs = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    allLogs.add(line);
                }
                int start = Math.max(0, allLogs.size() - lines);
                logs = allLogs.subList(start, allLogs.size());
            } catch (Exception e) {
                logs = service.getLogs(lines);
            }
        } else {
            logs = service.getLogs(lines);
        }

        if (logs.isEmpty()) {
            return "===== " + name + " Logs =====\nNo logs available";
        }
        return "===== " + name + " Logs =====\n" + String.join("\n", logs);
    }

    public void createCustomService(String name, String command, String workingDir)
            throws ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be empty");
        }
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }

        for (Service s : services) {
            if (s.getName().equals(name)) {
                throw new DuplicateServiceException(name);
            }
        }

        Service service = new Service(name.trim(), command.trim(), workingDir != null ? workingDir.trim() : "");
        try {
            serviceDAO.insert(service);
        } catch (java.sql.SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new DuplicateServiceException(name);
            }
            throw new RuntimeException("Failed to save service: " + e.getMessage());
        }
        services.add(service);
        notifyObservers(service, "created", "Custom service created");
    }

    public List<Execution> getExecutionHistory(String serviceName) {
        try {
            return executionDAO.findByServiceName(serviceName);
        } catch (java.sql.SQLException e) {
            return new ArrayList<>();
        }
    }

    public List<Output> getOutputs(int executionId) {
        try {
            return outputDAO.findByExecutionId(executionId);
        } catch (java.sql.SQLException e) {
            return new ArrayList<>();
        }
    }

    private Service findService(String name) throws ServiceNotFoundException {
        for (Service service : services) {
            if (service.getName().equals(name)) {
                return service;
            }
        }
        throw new ServiceNotFoundException(name);
    }
}
