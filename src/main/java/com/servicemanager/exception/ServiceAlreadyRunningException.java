package com.servicemanager.exception;

public class ServiceAlreadyRunningException extends ServiceException {
    public ServiceAlreadyRunningException(String serviceName) {
        super("Service already running: " + serviceName);
        this.serviceName = serviceName;
    }

    @Override
    public String getRecoveryHint() {
        return "Use 'restart' command to stop and start again";
    }
}
