module com.example.javashooter {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.example.javashooter to javafx.fxml;
    exports com.example.javashooter;
    exports com.example.javashooter.connection;
    exports com.example.javashooter.myobjects;
    opens com.example.javashooter.connection to javafx.fxml, com.google.gson;
    exports com.example.javashooter.connection.responses;
    opens com.example.javashooter.connection.responses to com.google.gson, javafx.fxml;
    opens com.example.javashooter.myobjects to com.google.gson;

}