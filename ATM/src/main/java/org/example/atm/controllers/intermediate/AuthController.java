package org.example.atm.controllers.intermediate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.atm.controllers.Controller;
import org.example.atm.exceptions.by_atm.AtmNoConnectionException;
import org.example.atm.exceptions.by_server.AuthFailedException;
import org.example.atm.exceptions.by_server.DatabaseNotAvailableException;
import org.example.atm.view.AtmView;

public class AuthController extends Controller {

    @FXML
    private TextField fieldLogin;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Label labelAuthFailed;

    @FXML
    private void initialize() {
        labelAuthFailed.setVisible(false);
    }

    @FXML
    private void clickSignIn() {
        try {
            atm.auth(fieldLogin.getText(), fieldPassword.getText());
            atmView.showNextPane();
        } catch (AtmNoConnectionException e) {
            atmView.showPane(AtmView.PaneType.NO_CONNECTION);
        } catch (AuthFailedException e) {
            labelAuthFailed.setVisible(true);
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
