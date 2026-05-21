package com.servicemanager.gui.service;

import com.servicemanager.gui.model.Service;

public interface ServiceProcessor {

    Process start(Service service) throws Exception;

    void stop(Process process) throws Exception;

    boolean isAlive(Process process);
}
