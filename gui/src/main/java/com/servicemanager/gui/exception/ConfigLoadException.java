package com.servicemanager.gui.exception;

public class ConfigLoadException extends ServiceException {

    public ConfigLoadException(String message) {
        super(message);
    }

    @Override
    public String getRecoveryHint() {
        return "Check services.json file exists and is valid JSON";
    }
}
