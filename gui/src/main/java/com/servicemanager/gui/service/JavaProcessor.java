package com.servicemanager.gui.service;

import com.servicemanager.gui.dao.ExecutionDAO;
import com.servicemanager.gui.dao.OutputDAO;
import com.servicemanager.gui.model.Execution;
import com.servicemanager.gui.model.Output;
import com.servicemanager.gui.model.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/** Strategy implementation that launches OS processes via ProcessBuilder.
 *  Reads stdout on a background daemon thread so the GUI stays responsive.
 *  Each line of output is persisted to the database and added to the
 *  service's in-memory log. When the process exits, it notifies the
 *  ServiceManager so observers can update the UI. */
public class JavaProcessor implements ServiceProcessor {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ExecutionDAO executionDAO;
    private final OutputDAO outputDAO;
    private final ServiceManager serviceManager;

    static String osName = System.getProperty("os.name");

    public JavaProcessor(ExecutionDAO executionDAO, OutputDAO outputDAO, ServiceManager serviceManager) {
        this.executionDAO = executionDAO;
        this.outputDAO = outputDAO;
        this.serviceManager = serviceManager;
    }

    @Override
    public Process start(Service service) throws Exception {
        String command = resolveCommand(service);
        ProcessBuilder pb;
        if (isWindows()) {
            pb = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            pb = new ProcessBuilder("/bin/sh", "-c", command);
        }

        String workDir = service.getWorkingDir();
        if (workDir != null && !workDir.isEmpty()) {
            pb.directory(new java.io.File(workDir));
        }

        pb.redirectErrorStream(true);
        Process process = pb.start();

        Execution execution = new Execution(service.getName(), LocalDateTime.now());
        executionDAO.insert(execution);

        service.setProcess(process);
        service.setRunning(true);
        service.setStartTime(execution.getStartTime());

        final int executionId = execution.getId();

        Thread ioThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String timestamp = LocalDateTime.now().format(FORMATTER);
                    String logEntry = timestamp + " - " + line;
                    service.addLog(logEntry);
                    outputDAO.insert(new Output(executionId, timestamp, line, "STDOUT"));
                }
            } catch (Exception e) {
                String timestamp = LocalDateTime.now().format(FORMATTER);
                service.addLog(timestamp + " - [IO Error: " + e.getMessage() + "]");
            }

            try {
                int exitCode = process.waitFor();
                LocalDateTime finishTime = LocalDateTime.now();
                String finishStatus = exitCode == 0 ? "FINISHED" : "FAILED";

                String timestamp = finishTime.format(FORMATTER);
                service.addLog(timestamp + " - " + finishStatus + " (exit: " + exitCode + ")");

                executionDAO.updateFinish(executionId, finishTime, exitCode, finishStatus);
                service.setRunning(false);
                service.setFinishedTime(finishTime);
                serviceManager.notifyServiceFinished(service.getName(), exitCode);
            } catch (Exception e) {
                String timestamp = LocalDateTime.now().format(FORMATTER);
                service.addLog(timestamp + " - [WaitFor Error: " + e.getMessage() + "]");
            }
        });
        ioThread.setDaemon(true);
        ioThread.start();

        return process;
    }

    @Override
    public void stop(Process process) throws Exception {
        if (process != null && process.isAlive()) {
            process.destroy();
            try {
                process.waitFor(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        }
    }

    @Override
    public boolean isAlive(Process process) {
        return process != null && process.isAlive();
    }

    static String resolveCommand(Service service) {
        String windowsCommand = service.getWindowsCommand();
        if (isWindows() && windowsCommand != null && !windowsCommand.isEmpty()) {
            return windowsCommand;
        }
        return service.getCommand();
    }

    static boolean isWindows() {
        return osName.toLowerCase().contains("win");
    }
}
