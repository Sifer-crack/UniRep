package com.servicemanager.gui.model;

public class Output {

    private int id;
    private int executionId;
    private String timestamp;
    private String line;
    private String stream;

    public Output(int executionId, String timestamp, String line, String stream) {
        this.executionId = executionId;
        this.timestamp = timestamp;
        this.line = line;
        this.stream = stream;
    }

    public Output(int id, int executionId, String timestamp, String line, String stream) {
        this.id = id;
        this.executionId = executionId;
        this.timestamp = timestamp;
        this.line = line;
        this.stream = stream;
    }

    public int getId() {
        return id;
    }

    public int getExecutionId() {
        return executionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLine() {
        return line;
    }

    public String getStream() {
        return stream;
    }
}
