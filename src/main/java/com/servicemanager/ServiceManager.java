package com.servicemanager;

import com.servicemanager.exception.*;
import com.servicemanager.processor.ServiceProcessor;
import com.servicemanager.processor.JavaProcessor;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
    
    private List<Service> services;
    private ConfigLoader configLoader;
    private ServiceProcessor processor;

    public ServiceManager() throws ConfigLoadException {
        this.configLoader = new ConfigLoader();
        this.processor = new JavaProcessor();
        this.services = configLoader.loadServices();
        log.info("Loaded {} services", services.size());
    }

    public ServiceManager(String configPath) throws ConfigLoadException {
        this.configLoader = new ConfigLoader(configPath);
        this.processor = new JavaProcessor();
        this.services = configLoader.loadServices();
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

    public String startService(String name) throws ServiceException {
        Service service = findService(name);
        if (processor.isAlive(service.getProcess())) {
            throw new ServiceAlreadyRunningException(name);
        }
        try {
            Process process = processor.start(service);
            service.setProcess(process);
            service.setRunning(true);
            service.setStartTime(java.time.LocalDateTime.now());
            service.addLog("Started at " + service.getStartTime());
            return "Starting " + name + "...\nStarted successfully";
        } catch (Exception e) {
            log.error("Failed to start service: {}", name, e);
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
            log.info("Stopped service: {}", name);
            return "Stopping " + name + "...\nStopped successfully";
        } catch (Exception e) {
            log.error("Failed to stop service: {}", name, e);
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
        if (service.getWorkingDir() != null) {
            sb.append("Working Dir: ").append(service.getWorkingDir()).append("\n");
        }
        if (service.getStartTime() != null) {
            sb.append("Started: ").append(service.getStartTime()).append("\n");
        }
        return sb.toString();
    }

    public String getServiceLogs(String name, int lines) throws ServiceNotFoundException {
        Service service = findService(name);
        
        List<String> logs = new ArrayList<>();
        String sanitizedName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
        java.io.File logFile = new java.io.File("logs", sanitizedName + ".log");
        
        if (logFile.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(logFile))) {
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
            return "No logs available";
        }
        return String.join("\n", logs);
    }

    private Service findService(String name) throws ServiceNotFoundException {
        for (Service service : services) {
            if (service.getName().equals(name)) {
                return service;
            }
        }
        throw new ServiceNotFoundException(name);
    }

    public List<Service> getServices() {
        return services;
    }
}
