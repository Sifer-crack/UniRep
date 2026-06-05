module com.servicemanager.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.desktop;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.slf4j;

    opens com.servicemanager.gui to javafx.fxml;
    opens com.servicemanager.gui.controller to javafx.fxml;
    exports com.servicemanager.gui;
    exports com.servicemanager.gui.model;
    exports com.servicemanager.gui.service;
    exports com.servicemanager.gui.dao;
    exports com.servicemanager.gui.config;
    exports com.servicemanager.gui.exception;
    exports com.servicemanager.gui.controller;
}
