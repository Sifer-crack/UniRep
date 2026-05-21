package com.servicemanager.gui.exception;

/** Thrown when a user tries to operate on a service name
 *  that does not exist in the loaded service list.
 *  This happens when the name is mistyped or the service
 *  was removed from the config file. */
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
