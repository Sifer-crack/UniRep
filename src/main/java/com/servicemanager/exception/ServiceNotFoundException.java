package com.servicemanager.exception;

public class ServiceNotFoundException extends ServiceException {
    public ServiceNotFoundException(String serviceName) {
        super("Service not found: " + serviceName);
        this.serviceName = serviceName;
    }

    @Override
    public String getRecoveryHint() {
        return "Check services.json for valid service names";
    }
}
