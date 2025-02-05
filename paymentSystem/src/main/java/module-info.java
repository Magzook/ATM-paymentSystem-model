module org.example.paymentsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires tcp.session;
    requires custommessage;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;


    opens org.example.paymentsystem to javafx.fxml, com.fasterxml.jackson.databind;
    exports org.example.paymentsystem;
    exports org.example.paymentsystem.server;
    opens org.example.paymentsystem.server to com.fasterxml.jackson.databind, javafx.fxml;
    exports org.example.paymentsystem.database;
    opens org.example.paymentsystem.database to com.fasterxml.jackson.databind, javafx.fxml;
}