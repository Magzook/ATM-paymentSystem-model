package org.example.atm.exceptions.by_atm;

public class AtmReceiverNotSpecifiedException extends Exception {
    public AtmReceiverNotSpecifiedException() {
        super("Получатель не указан");
    }
}
