package com.servicemanager.gui.service;

import com.servicemanager.gui.model.Service;

/** Observer interface for service lifecycle events.
 *  Implementations receive notifications when a service is
 *  started, stopped, finished (process exited), or created.
 *  The GUI controller uses this to auto-refresh without polling. */
public interface ServiceObserver {
    void onServiceEvent(String eventType, String serviceName, String message);
}
