package com.servicemanager.gui.service;

import com.servicemanager.gui.model.Service;

public interface ServiceObserver {
    void onServiceEvent(String eventType, String serviceName, String message);
}
