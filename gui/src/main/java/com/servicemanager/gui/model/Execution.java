package com.servicemanager.gui.model;

import java.time.LocalDateTime;

public class Execution {

    private int id;
    private String serviceName;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String status;
    private Integer exitCode;

    public Execution(String serviceName, LocalDateTime startTime) {
        this.serviceName = serviceName;
        this.startTime = startTime;
        this.status = "RUNNING";
    }

    public Execution(int id, String serviceName, LocalDateTime startTime,
                     LocalDateTime finishTime, String status, Integer exitCode) {
        this.id = id;
        this.serviceName = serviceName;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.status = status;
        this.exitCode = exitCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }
}
