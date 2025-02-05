package org.example.paymentsystem;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.paymentsystem.server.QiwiWallet_2_0_Server;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        JsonNode jsonNode;
        try {
            String jsonSettings = Files.readString(Path.of(System.getProperty("user.dir") + "/paymentSystem/src/main/resources/settings.json"));
            jsonNode = new ObjectMapper().readTree(jsonSettings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        QiwiWallet_2_0_Server qiwiServer = new QiwiWallet_2_0_Server(
                jsonNode.get("port").asInt(),
                jsonNode.get("sessionsInfoFolder").asText(),
                jsonNode.get("jdbcUrl").asText(),
                jsonNode.get("jdbcName").asText(),
                jsonNode.get("jdbcPassword").asText()
        );

        qiwiServer.launch();
    }

    public static void main(String[] args) {
        launch();
    }
}