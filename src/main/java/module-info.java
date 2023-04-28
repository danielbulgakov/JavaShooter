module com.example.javashooter {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.graphics;
    requires javafx.base;
    requires com.google.gson;
    requires java.persistence;
    requires org.hibernate.orm.core;

    requires java.naming;
    requires java.sql;

    opens com.example.javashooter to javafx.fxml;
    exports com.example.javashooter;
    exports com.example.javashooter.connection;
    exports com.example.javashooter.myobjects;
    exports com.example.javashooter.connection.database;
    opens com.example.javashooter.connection to javafx.fxml, com.google.gson;
    exports com.example.javashooter.connection.responses;
    opens com.example.javashooter.connection.responses to com.google.gson, javafx.fxml;
    opens com.example.javashooter.myobjects to com.google.gson;


}