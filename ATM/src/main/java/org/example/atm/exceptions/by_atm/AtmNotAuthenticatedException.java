package org.example.atm.exceptions.by_atm;

public class AtmNotAuthenticatedException extends Exception {
    public AtmNotAuthenticatedException() {
        super("Это действие нельзя совершать, потому что вы не вошли в аккаунт!");
    }
}
