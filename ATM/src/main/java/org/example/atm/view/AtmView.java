package org.example.atm.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import org.example.atm.controllers.Controller;
import org.example.atm.model.ATM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AtmView {

    private final ATM atm;
    private final Pane root;

    private ArrayList<PaneType> paneQueue;

    public AtmView(ATM atm) {
        this.atm = atm;
        root = new Pane();
        root.setStyle("-fx-background-color: white");
        paneQueue = new ArrayList<>();
        if (atm.getConnectionState())
            showPane(PaneType.WELCOME);
        else
            showPane(PaneType.NO_CONNECTION);
    }

    public Pane getRoot() {
        return root;
    }

    public void showPane(PaneType paneType) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/" + paneType.fxmlFileName));
            Pane pane = fxmlLoader.load();
            Controller controller = fxmlLoader.getController();
            controller.setAtm(atm);
            controller.setAtmView(this);
            controller.fillWithInitialData();
            root.getChildren().clear();
            root.getChildren().add(pane);
            if (paneType == PaneType.NO_CONNECTION) {
                waitUntilConnectionIsReestablished();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPaneQueue(PaneType ...panes) {
        paneQueue.clear();
        paneQueue.addAll(Arrays.asList(panes));
    }

    public void showNextPane() {
        showPane(paneQueue.removeFirst());
    }

    private void waitUntilConnectionIsReestablished() {
        new Thread(() -> {
            synchronized (atm.reconnectionMonitor) {
                try {
                    if (!atm.getConnectionState())
                        atm.reconnectionMonitor.wait();
                } catch (InterruptedException ignored) {

                }
            }
            Platform.runLater(() -> showPane(PaneType.WELCOME));
        }).start();
    }

    public enum PaneType {
        WELCOME("welcome.fxml"),
        AUTH("auth.fxml"),
        NEW_ACCOUNT("newAccount.fxml"),
        NEW_ACCOUNT_SUCCESS("newAccountSuccess.fxml"),
        CONFIGURE_ACCOUNT("configureAccount.fxml"),
        ACCOUNT_DELETED("accountDeleted.fxml"),
        SET_RECEIVER("setReceiver.fxml"),
        DEPOSIT("deposit.fxml"),
        WITHDRAW("withdraw.fxml"),
        TRANSFER("transfer.fxml"),
        SUCCESS("success.fxml"),
        NO_CONNECTION("noConnection.fxml"),
        DATABASE_NOT_AVAILABLE("databaseNotAvailable.fxml"),
        DATABASE_CONCURRENT_MODIFICATION("databaseConcurrentModification.fxml");

        private final String fxmlFileName;
        PaneType(String fxmlFileName) {
            this.fxmlFileName = fxmlFileName;
        }
    }
}
