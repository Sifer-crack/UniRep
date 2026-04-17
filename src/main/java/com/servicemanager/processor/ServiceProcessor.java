package com.servicemanager.processor;

import com.servicemanager.Service;
import java.lang.Process;

public interface ServiceProcessor {
    Process start(Service service) throws Exception;
    void stop(Process process) throws Exception;
    boolean isAlive(Process process);
}
