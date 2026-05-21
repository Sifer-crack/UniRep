package com.servicemanager.gui.exception;

/** Thrown when trying to stop or query a service that is not running.
 *  The service may have already finished, failed, or was never started. */
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
