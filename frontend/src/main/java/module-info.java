module ma.ensah {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    // FXML needs reflective access
    opens ma.ensah.ui to javafx.fxml;
    // Jackson needs reflective access to model classes
    opens ma.ensah.model to com.fasterxml.jackson.databind;

    exports ma.ensah;
}
