package org.example.atm.controllers.intermediate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.atm.controllers.Controller;
import org.example.atm.exceptions.by_atm.*;
import org.example.atm.exceptions.by_server.DatabaseConcurrentModificationException;
import org.example.atm.exceptions.by_server.DatabaseNotAvailableException;
import org.example.atm.exceptions.by_server.LoginIsAlreadyTakenException;
import org.example.atm.view.AtmView;

import java.text.DecimalFormat;

public class ConfigureAccountController extends Controller {
    @FXML
    private Label labelLogin, labelBalance;
    @FXML
    private Label labelLoginError, labelPasswordError, labelPasswordsDoNotMatch;
    @FXML
    private Label labelLoginSuccess, labelPasswordSuccess, labelCannotDeleteAccount;
    @FXML
    private TextField fieldLogin;
    @FXML
    private PasswordField fieldPassword, fieldPasswordRepeat;

    @FXML
    private void initialize() {
        eraseOrHideLabels();
    }

    private void eraseOrHideLabels() {
        labelLoginError.setText("");
        labelPasswordError.setText("");
        labelPasswordsDoNotMatch.setVisible(false);
        labelLoginSuccess.setVisible(false);
        labelPasswordSuccess.setVisible(false);
        labelCannotDeleteAccount.setVisible(false);
    }

    @FXML
    private void clickQuit() {
        atm.logout();
        atmView.showPane(AtmView.PaneType.WELCOME);
    }

    @FXML
    private void clickChangeLogin() {
        eraseOrHideLabels();
        String newLogin = fieldLogin.getText();
        if (newLogin.equals(labelLogin.getText())) {
            labelLoginError.setText("Вы уже используете этот логин!");
            return;
        }
        try {
            atm.changeLogin(newLogin);
            labelLoginSuccess.setVisible(true);
            labelLogin.setText(newLogin);
        } catch (AtmBadLoginException e) {
            labelLoginError.setText(e.getMessage());
        } catch (LoginIsAlreadyTakenException e) {
            labelLoginError.setText("Этот логин уже занят!");
        } catch (AtmNoConnectionException e) {
            atmView.showPane(AtmView.PaneType.NO_CONNECTION);
        } catch (DatabaseConcurrentModificationException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_CONCURRENT_MODIFICATION);
        } catch (DatabaseNotAvailableException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_NOT_AVAILABLE);
        } catch (AtmNotAuthenticatedException e) {
            // Не может быть выброшено при использовании графического интерфейса.
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void clickChangePassword() {
        eraseOrHideLabels();
        String newPassword = fieldPassword.getText();
        String newPasswordRepeat = fieldPasswordRepeat.getText();
        if (!newPassword.equals(newPasswordRepeat)) {
            labelPasswordsDoNotMatch.setVisible(true);
            return;
        }
        try {
            atm.changePassword(newPassword);
            labelPasswordSuccess.setVisible(true);
        } catch (AtmBadPasswordException e) {
            labelPasswordError.setText(e.getMessage());
        } catch (AtmNoConnectionException e) {
            atmView.showPane(AtmView.PaneType.NO_CONNECTION);
        } catch (DatabaseConcurrentModificationException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_CONCURRENT_MODIFICATION);
        } catch (DatabaseNotAvailableException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_NOT_AVAILABLE);
        } catch (AtmNotAuthenticatedException e) {
            // Не может быть выброшено при использовании графического интерфейса.
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void clickDeleteAccount() {
        eraseOrHideLabels();
        try {
            atm.deleteAccount();
            atmView.showPane(AtmView.PaneType.ACCOUNT_DELETED);
        } catch (AtmDeleteAccountWithMoneyException e) {
            labelCannotDeleteAccount.setVisible(true);
        } catch (AtmNoConnectionException e) {
            atmView.showPane(AtmView.PaneType.NO_CONNECTION);
        } catch (DatabaseConcurrentModificationException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_CONCURRENT_MODIFICATION);
        } catch (DatabaseNotAvailableException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_NOT_AVAILABLE);
        } catch (AtmNotAuthenticatedException e) {
            // Не может быть выброшено при использовании графического интерфейса.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fillWithInitialData() {
        fieldLogin.setPromptText("от " + atm.getPolicy().loginLengthBottom() + " до " + atm.getPolicy().loginLengthTop() + " символов");
        fieldPassword.setPromptText("от " + atm.getPolicy().passwordLengthBottom() + " символов");
        try {
            String login = atm.getCurrentLogin();
            String balance = to2DecimalPlaces(atm.getCurrentBalance());
            labelLogin.setText(login);
            labelBalance.setText(balance);
        } catch (AtmNotAuthenticatedException e) {
            // Не может быть выброшено при использовании графического интерфейса.
            throw new RuntimeException(e);
        }
    }

    private String to2DecimalPlaces(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}
