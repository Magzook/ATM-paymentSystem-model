package org.example.atm.exceptions.by_server;

public class LoginIsAlreadyTakenException extends Exception {
    public LoginIsAlreadyTakenException() {
        super("Этот логин уже занят!");
    }
}
