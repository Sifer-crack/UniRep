package com.servicemanager.gui.exception;

public abstract class ServiceException extends Exception {

    protected String serviceName;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public abstract String getRecoveryHint();
}
