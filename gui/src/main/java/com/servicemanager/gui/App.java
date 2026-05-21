package com.servicemanager.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** JavaFX entry point for the Service Manager GUI.
 *  Loads the main FXML layout, creates the scene, and shows the stage.
 *  The FXML file is loaded from the classpath next to this class. */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Service Manager");
        stage.setScene(scene);
        stage.show();
    }
}
