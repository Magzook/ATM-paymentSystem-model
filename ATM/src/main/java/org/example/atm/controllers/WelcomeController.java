package org.example.atm.controllers;

import javafx.fxml.FXML;
import org.example.atm.view.AtmView;

public class WelcomeController extends Controller {

    @FXML
    private void clickDeposit() {
        atmView.setPaneQueue(
                AtmView.PaneType.SET_RECEIVER,
                AtmView.PaneType.DEPOSIT);
        atmView.showNextPane();
    }

    @FXML
    private void clickWithdraw() {
        atmView.setPaneQueue(
                AtmView.PaneType.AUTH,
                AtmView.PaneType.WITHDRAW);
        atmView.showNextPane();
    }

    @FXML
    private void clickTransfer() {
        atmView.setPaneQueue(
                AtmView.PaneType.SET_RECEIVER,
                AtmView.PaneType.AUTH,
                AtmView.PaneType.TRANSFER);
        atmView.showNextPane();
    }

    @FXML
    private void clickCreateAccount() {
        atmView.showPane(AtmView.PaneType.NEW_ACCOUNT);
    }

    @FXML
    private void clickConfigureAccount() {
        atmView.setPaneQueue(
                AtmView.PaneType.AUTH,
                AtmView.PaneType.CONFIGURE_ACCOUNT);
        atmView.showNextPane();
    }

    @Override
    public void fillWithInitialData() {}
}
