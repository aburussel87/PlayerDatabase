module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.example.Controller to javafx.fxml;
    opens com.example to javafx.fxml;
    exports com.example.Controller;
    exports com.example;
}
