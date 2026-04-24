package com.servicemanager.processor;

import com.servicemanager.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JavaProcessor implements ServiceProcessor {
    private static final Logger log = LoggerFactory.getLogger(JavaProcessor.class);
    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final List<Process> runningProcesses = new ArrayList<>();

    @Override
    public Process start(Service service) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd.exe", "/c", service.getCommand());
        } else {
            pb = new ProcessBuilder("/bin/sh", "-c", service.getCommand());
        }

        String workDir = service.getWorkingDir();
        if (workDir != null && !workDir.isEmpty()) {
            pb.directory(new java.io.File(workDir));
        }

        pb.redirectErrorStream(true);

        System.out.println("Starting " + service.getName() + "...");
        System.out.println("Started successfully");
        System.out.println();

        Process process = pb.start();

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String startedEntry = timestamp + " - Started";
        writeToLogFile(service.getName(), startedEntry);
        service.addLog(startedEntry);

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                output.append(line).append("\n");
            }
        }
        process.waitFor();

        timestamp = LocalDateTime.now().format(FORMATTER);
        String finishedEntry = timestamp + " - Finished";
        writeToLogFile(service.getName(), finishedEntry);
        service.addLog(finishedEntry);
        service.setFinishedTime(LocalDateTime.now());

        runningProcesses.add(process);
        return process;
    }

    private void writeToLogFile(String serviceName, String entry) throws Exception {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        String sanitizedName = serviceName.replaceAll("[^a-zA-Z0-9.-]", "_");
        File logFile = new File(logDir, sanitizedName + ".log");

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(entry);
        }
    }

    private String captureOutput(Process process) throws Exception {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        process.waitFor();
        return output.toString().trim();
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
}
