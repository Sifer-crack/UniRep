package com.servicemanager.processor;

import com.servicemanager.Service;
import com.servicemanager.exception.ServiceAlreadyRunningException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JavaProcessor implements ServiceProcessor {
    private final List<Process> runningProcesses = new ArrayList<>();

    @Override
    public Process start(Service service) throws Exception {
        List<String> command = parseCommand(service.getCommand());
        ProcessBuilder pb = new ProcessBuilder(command);
        if (service.getWorkingDir() != null) {
            pb.directory(new java.io.File(service.getWorkingDir()));
        }
        pb.redirectErrorStream(true);
        Process process = pb.start();
        runningProcesses.add(process);
        return process;
    }

    @Override
    public void stop(Process process) throws Exception {
        if (process != null && process.isAlive()) {
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                process.destroyForcibly();
            }
        }
        runningProcesses.remove(process);
    }

    @Override
    public boolean isAlive(Process process) {
        return process != null && process.isAlive();
    }

    private List<String> parseCommand(String command) {
        List<String> args = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(command);
        while (tokenizer.hasMoreTokens()) {
            args.add(tokenizer.nextToken());
        }
        return args;
    }
}
