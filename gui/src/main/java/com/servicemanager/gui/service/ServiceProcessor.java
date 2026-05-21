package com.servicemanager.gui.service;

import com.servicemanager.gui.model.Service;

/** Strategy interface for process lifecycle management.
 *  Abstracts OS-specific process creation so different backends
 *  (local process, Docker, remote SSH, etc.) can be plugged
 *  in without changing the ServiceManager. */
public interface ServiceProcessor {

    Process start(Service service) throws Exception;

    void stop(Process process) throws Exception;

    boolean isAlive(Process process);
}
