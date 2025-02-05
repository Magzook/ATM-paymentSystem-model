package org.example.atm.controllers.intermediate;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.example.atm.controllers.Controller;
import org.example.atm.exceptions.by_atm.AtmIllegalSumException;
import org.example.atm.exceptions.by_atm.AtmNoConnectionException;
import org.example.atm.exceptions.by_atm.AtmReceiverNotSpecifiedException;
import org.example.atm.exceptions.by_server.DatabaseConcurrentModificationException;
import org.example.atm.exceptions.by_server.DatabaseNotAvailableException;
import org.example.atm.view.AtmView;

public class DepositController extends Controller {

    @FXML
    private Label labelReceiver, labelSumError;
    @FXML
    private TextField fieldSum;

    @FXML
    private void initialize() {
        labelSumError.setText("");

        fieldSum.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) return change;

            // Целая часть (хотя бы 1 цифра), точка (необязательно), дробная часть (не более 2 цифр).
            return newText.matches("^(\\d+)(\\.\\d{0,2})?$")
                    ? change
                    : null;
        }));
    }

    @FXML
    private void clickDeposit() {
        String sumStr = fieldSum.getText();
        double sum;
        try {
            sum = Double.parseDouble(sumStr);
            atm.deposit(sum);
            atmView.showPane(AtmView.PaneType.SUCCESS);
        } catch (NumberFormatException e) {
            labelSumError.setText("Сумма должна быть числом!");
        } catch (AtmIllegalSumException e) {
            labelSumError.setText(e.getMessage());
        } catch (AtmNoConnectionException e) {
            atmView.showPane(AtmView.PaneType.NO_CONNECTION);
        } catch (DatabaseConcurrentModificationException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_CONCURRENT_MODIFICATION);
        } catch (DatabaseNotAvailableException e) {
            atmView.showPane(AtmView.PaneType.DATABASE_NOT_AVAILABLE);
        } catch (AtmReceiverNotSpecifiedException e) {
            // Не может быть выброшено при использовании графического интерфейса
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void clickBack() {
        atmView.showPane(AtmView.PaneType.WELCOME);
    }

    @Override
    public void fillWithInitialData() {
        labelReceiver.setText(atm.getReceiver());
    }
}
