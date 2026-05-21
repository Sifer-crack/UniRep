package com.servicemanager.gui.exception;

public class DuplicateServiceException extends ServiceException {

    public DuplicateServiceException(String serviceName) {
        super("Service already exists: " + serviceName);
        this.serviceName = serviceName;
    }

    @Override
    public String getRecoveryHint() {
        return "Choose a different name for the service";
    }
}
