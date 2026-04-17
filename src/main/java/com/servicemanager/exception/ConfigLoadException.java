package com.servicemanager.exception;

public class ConfigLoadException extends ServiceException {
    public ConfigLoadException(String message) {
        super(message);
        this.serviceName = null;
    }

    @Override
    public String getRecoveryHint() {
        return "Check services.json file exists and is valid JSON";
    }
}
