package org.example.atm.controllers.result;

import javafx.fxml.FXML;
import org.example.atm.controllers.Controller;
import org.example.atm.view.AtmView;

public class DatabaseConcurrentModificationController extends Controller {

    @FXML
    private void clickReturnToMain() {
        atmView.showPane(AtmView.PaneType.WELCOME);
    }

    @Override
    public void fillWithInitialData() {}
}
