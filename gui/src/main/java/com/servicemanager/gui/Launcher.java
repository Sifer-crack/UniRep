package com.servicemanager.gui;

import javafx.application.Application;

/** Launcher class with a standard main() method.
 *  Workaround for Java module-path restrictions — JavaFX requires
 *  Application.launch() to be called from a non-module context,
 *  so this class provides the entry point that Maven invokes. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
