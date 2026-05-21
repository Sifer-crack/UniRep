package com.servicemanager.gui.controller;

import com.servicemanager.gui.exception.ConfigLoadException;
import com.servicemanager.gui.exception.DuplicateServiceException;
import com.servicemanager.gui.model.Service;
import com.servicemanager.gui.service.ServiceManager;
import com.servicemanager.gui.service.ServiceObserver;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/** JavaFX controller for the main service management view.
 *  Handles all user interactions: start, stop, restart, logs,
 *  status, refresh, and create custom service.
 *  Implements ServiceObserver so the table and output area
 *  update automatically when service state changes. */
public class MainController implements ServiceObserver {

    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, String> nameColumn;
    @FXML private TableColumn<Service, String> statusColumn;
    @FXML private TableColumn<Service, String> commandColumn;
    @FXML private TextField nameField;
    @FXML private TextField commandField;
    @FXML private TextField workingDirField;
    @FXML private TextArea outputArea;

    private ServiceManager serviceManager;
    private final ObservableList<Service> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        commandColumn.setCellValueFactory(new PropertyValueFactory<>("command"));

        try {
            serviceManager = new ServiceManager();
            serviceManager.addObserver(this);
            refreshTable();
            appendOutput("Service Manager ready. Loaded " + serviceList.size() + " services.");
        } catch (ConfigLoadException e) {
            appendOutput("ERROR: " + e.getMessage());
        }

        serviceTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                nameField.setText(sel.getName());
                commandField.setText(sel.getCommand());
                workingDirField.setText(sel.getWorkingDir());
            }
        });
    }

    @Override
    public void onServiceEvent(String eventType, String serviceName, String message) {
        Platform.runLater(() -> {
            appendOutput("[" + eventType + "] " + serviceName + " - " + message);
            refreshTable();
        });
    }

    @FXML
    private void handleStart() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendOutput("Select a service first.");
            return;
        }
        String result = callManager(() -> serviceManager.startService(selected.getName()));
        appendOutput(result);
        refreshTable();
    }

    @FXML
    private void handleStop() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendOutput("Select a service first.");
            return;
        }
        String result = callManager(() -> serviceManager.stopService(selected.getName()));
        appendOutput(result);
        refreshTable();
    }

    @FXML
    private void handleRestart() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendOutput("Select a service first.");
            return;
        }
        String result = callManager(() -> serviceManager.restartService(selected.getName()));
        appendOutput(result);
        refreshTable();
    }

    @FXML
    private void handleStatus() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendOutput("Select a service first.");
            return;
        }
        String result = callManager(() -> serviceManager.getServiceStatus(selected.getName()));
        appendOutput(result);
    }

    @FXML
    private void handleLogs() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendOutput("Select a service first.");
            return;
        }
        String result = callManager(() -> serviceManager.getServiceLogs(selected.getName(), 50));
        appendOutput(result);
        refreshTable();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
        appendOutput("Service list refreshed.");
    }

    @FXML
    private void handleCreate() {
        String name = nameField.getText().trim();
        String command = commandField.getText().trim();
        String workingDir = workingDirField.getText().trim();

        if (name.isEmpty() || command.isEmpty()) {
            appendOutput("Name and Command are required.");
            return;
        }

        try {
            serviceManager.createCustomService(name, command, workingDir);
            appendOutput("Service '" + name + "' created.");
            nameField.clear();
            commandField.clear();
            workingDirField.clear();
            refreshTable();
        } catch (DuplicateServiceException e) {
            appendOutput("ERROR: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            appendOutput("ERROR: " + e.getMessage());
        } catch (Exception e) {
            appendOutput("ERROR: " + e.getMessage());
        }
    }

    private void refreshTable() {
        if (serviceManager == null) return;
        serviceList.setAll(serviceManager.getServices());
        serviceTable.setItems(serviceList);
    }

    private void appendOutput(String text) {
        if (text == null || text.isEmpty()) return;
        outputArea.appendText(text + "\n");
    }

    private String callManager(ManagerCall call) {
        try {
            return call.execute();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @FunctionalInterface
    private interface ManagerCall {
        String execute() throws Exception;
    }
}
