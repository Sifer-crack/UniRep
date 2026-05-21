package com.servicemanager.gui.exception;

/** Thrown when trying to start a service that is already running.
 *  The previous instance has not finished yet, so the user
 *  should stop it first or use the restart command instead. */
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
