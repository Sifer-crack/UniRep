module com.servicemanager.gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.servicemanager.gui to javafx.fxml;
    exports com.servicemanager.gui;
}