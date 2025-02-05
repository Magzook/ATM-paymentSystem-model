module org.example.atm {
    requires javafx.controls;
    requires javafx.fxml;
    requires tcp.session;
    requires custommessage;
    requires java.rmi;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    exports org.example.atm.model;
    opens org.example.atm.model to javafx.fxml, com.fasterxml.jackson.databind;

    exports org.example.atm.controllers;
    opens org.example.atm.controllers to javafx.fxml;
    exports org.example.atm.view;
    opens org.example.atm.view to javafx.fxml;

    exports org.example.atm.exceptions.by_atm;
    exports org.example.atm.exceptions.by_server;
    exports org.example.atm;
    opens org.example.atm to javafx.fxml;
    exports org.example.atm.controllers.result;
    opens org.example.atm.controllers.result to javafx.fxml;
    exports org.example.atm.controllers.intermediate;
    opens org.example.atm.controllers.intermediate to javafx.fxml;
}