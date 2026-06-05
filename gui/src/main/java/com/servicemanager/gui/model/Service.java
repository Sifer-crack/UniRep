package com.servicemanager.gui.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Core model representing a service that the manager can run.
 *  Holds the service definition (name, command, working directory),
 *  runtime state (running, process handle, timestamps), and an
 *  in-memory log buffer captured during execution. */
public class Service {

    private int id;
    private String name;
    private String command;
    private String windowsCommand;
    private String workingDir;
    private boolean running;
    private Process process;
    private LocalDateTime startTime;
    private LocalDateTime finishedTime;
    private List<String> logs;

    public Service(String name, String command, String workingDir) {
        this(name, command, null, workingDir);
    }

    public Service(String name, String command, String windowsCommand, String workingDir) {
        this.name = name;
        this.command = command;
        this.windowsCommand = windowsCommand;
        this.workingDir = workingDir;
        this.running = false;
        this.logs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getWindowsCommand() {
        return windowsCommand;
    }

    public void setWindowsCommand(String windowsCommand) {
        this.windowsCommand = windowsCommand;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public boolean isRunning() {
        return running;
    }

    public Process getProcess() {
        return process;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public String getStatus() {
        return running ? "RUNNING" : "STOPPED";
    }

    public List<String> getLogs(int lines) {
        if (lines >= logs.size()) {
            return new ArrayList<>(logs);
        }
        return new ArrayList<>(logs.subList(logs.size() - lines, logs.size()));
    }
}
