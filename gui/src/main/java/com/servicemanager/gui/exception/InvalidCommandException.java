package com.servicemanager.gui.exception;

public class InvalidCommandException extends ServiceException {

    private final String command;

    public InvalidCommandException(String command) {
        super(command, "Unknown command: " + command);
        this.command = command;
    }

    @Override
    public String getRecoveryHint() {
        return "Use 'list' to see available commands or 'help' for usage.";
    }

    public String getCommand() {
        return command;
    }
}
