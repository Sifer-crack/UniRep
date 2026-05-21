package com.servicemanager.gui.exception;

/** Thrown when trying to create a custom service with a name
 *  that already exists in the system — either from the JSON config
 *  file or from a previously created custom service. */
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
