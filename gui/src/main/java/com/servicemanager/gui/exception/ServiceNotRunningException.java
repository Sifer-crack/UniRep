package com.servicemanager.gui.exception;

public class ServiceNotRunningException extends ServiceException {

    public ServiceNotRunningException(String serviceName) {
        super("Service not running: " + serviceName);
        this.serviceName = serviceName;
    }

    @Override
    public String getRecoveryHint() {
        return "Use 'start' command first";
    }
}
