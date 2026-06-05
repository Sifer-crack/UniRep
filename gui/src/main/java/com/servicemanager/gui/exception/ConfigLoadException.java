package com.servicemanager.gui.exception;

/** Thrown when services.json cannot be loaded or parsed.
 *  This can happen when the file is missing from the classpath,
 *  the path is incorrect, or the JSON content is malformed. */
public class ConfigLoadException extends ServiceException {

    public ConfigLoadException(String message) {
        super(message);
    }

    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getRecoveryHint() {
        return "Check services.json file exists and is valid JSON";
    }
}
