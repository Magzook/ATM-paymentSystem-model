package org.example.atm.controllers.intermediate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.atm.controllers.Controller;
import org.example.atm.exceptions.by_atm.AtmBadLoginException;
import org.example.atm.exceptions.by_atm.AtmBadPasswordException;
import org.example.atm.exceptions.by_atm.AtmNoConnectionException;
import org.example.atm.exceptions.by_server.DatabaseNotAvailableException;
import org.example.atm.exceptions.by_server.LoginIsAlreadyTakenException;
import org.example.atm.view.AtmView;

public class NewAccountController extends Controller {

    @FXML
    private TextField fieldLogin;
    @FXML
    private PasswordField fieldPassword, fieldPasswordRepeat;
    @FXML
    private Label labelLoginError, labelPasswordError, labelPasswordsDoNotMatch;

    @FXML
    private void initialize() {
        hideAllErrorLabels();
    }

    @FXML
    private void clickCreateAccount() {
        hideAllErrorLabels();
        String login = fieldLogin.getText();
        String password = fieldPassword.getText();
        String passwordRepeat = fieldPasswordRepeat.getText();
        if (!password.equals(passwordRepeat)) {
            labelPasswordsDoNotMatch.setVisible(true);
            return;
        }
        try {
            atm.createNewAccount(login, password);
            atmView.showPane(AtmView.PaneType.NEW_ACCOUNT_SUCCESS);
        } catch (AtmBadLoginException | LoginIsAlreadyTakenException e) {
            labelLoginError.setText(e.getMessage());
            labelLoginError.setVisible(true);
        } catch (AtmBadPasswordException e) {
            labelPasswordError.setText(e.getMessage());
            labelPasswordError.setVisible(true);
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

    private void hideAllErrorLabels() {
        labelLoginError.setVisible(false);
        labelPasswordError.setVisible(false);
        labelPasswordsDoNotMatch.setVisible(false);
    }

    @Override
    public void fillWithInitialData() {
        atmView.getRoot().requestFocus();
        fieldLogin.setPromptText("от " + atm.getPolicy().loginLengthBottom() + " до " + atm.getPolicy().loginLengthTop() + " символов");
        fieldPassword.setPromptText("от " + atm.getPolicy().passwordLengthBottom() + " символов");
    }
}
