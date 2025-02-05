package org.example.atm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.atm.exceptions.by_atm.*;
import org.example.atm.model.ATM;
import org.example.atm.view.AtmView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        JsonNode jsonNode;
        try {
            String jsonSettings = Files.readString(Path.of(System.getProperty("user.dir") + "/ATM/src/main/resources/settings.json"));
            jsonNode = new ObjectMapper().readTree(jsonSettings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ATM atm = new ATM(
                jsonNode.get("serverIP").asText(),
                jsonNode.get("serverPort").asInt(),
                jsonNode.get("sessionInfoFilePath").asText()
        );

        try {
            atm.connectToServer();
        } catch (AtmBadIpException | AtmBadPortException e) {
            System.err.println(e.getMessage());
            return;
        }

        AtmView atmView = new AtmView(atm);
        stage.setTitle("ATM");
        stage.setScene(new Scene(atmView.getRoot()));
        stage.setResizable(false);
        stage.show();
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/pictures/kiwi.png")));
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}