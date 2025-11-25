module me.ensah {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    // FXML needs reflective access
    opens me.ensah.ui to javafx.fxml;
    opens me.ensah.ui.components to javafx.fxml;
    opens me.ensah.ui.controllers to javafx.fxml;
    // Jackson and JavaFX need reflective access to model classes
    opens me.ensah.model to com.fasterxml.jackson.databind, javafx.base;

    exports me.ensah;
    exports me.ensah.ui.components;
}

// this file is for :