package com.servicemanager.gui.exception;

/** Abstract base for all service-related exceptions.
 *  Subclasses must provide a recovery hint via getRecoveryHint()
 *  so the UI can show the user what action to take next. */
public abstract class ServiceException extends Exception {

    protected String serviceName;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public abstract String getRecoveryHint();
}
