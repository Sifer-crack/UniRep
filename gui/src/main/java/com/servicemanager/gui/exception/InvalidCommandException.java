package com.servicemanager.gui.exception;

/** Thrown when the user enters an unrecognised CLI command.
 *  Provides a hint listing available commands so the user
 *  can correct their input. */
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
