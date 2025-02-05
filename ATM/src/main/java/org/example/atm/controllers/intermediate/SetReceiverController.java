package org.example.atm.controllers.intermediate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.atm.controllers.Controller;
import org.example.atm.exceptions.by_atm.AtmNoConnectionException;
import org.example.atm.exceptions.by_server.DatabaseNotAvailableException;
import org.example.atm.exceptions.by_server.NoSuchReceiverException;
import org.example.atm.view.AtmView;

public class SetReceiverController extends Controller {

    @FXML
    private Label labelNoSuchReceiver;
    @FXML
    private TextField fieldLogin;

    @FXML
    private void initialize() {
        labelNoSuchReceiver.setVisible(false);
    }

    @FXML
    private void clickContinue() {
        String receiver = fieldLogin.getText();
        try {
            atm.setReceiver(receiver);
            atmView.showNextPane();
        } catch (NoSuchReceiverException e) {
            labelNoSuchReceiver.setVisible(true);
        } catch (AtmNoConnectionException e) {
            atmView.showPane(AtmView.PaneType.NO_CONNECTION);
        } catch (DatabaseNotAvailableException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_NOT_AVAILABLE);
        }
    }

    @FXML
    private void clickBack() {
        atmView.showPane(AtmView.PaneType.WELCOME);
    }

    @Override
    public void fillWithInitialData() {}
}
