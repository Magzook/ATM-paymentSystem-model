package org.example.atm.exceptions.by_server;

public class AuthFailedException extends Exception {
    public AuthFailedException() {
        super("Authentication failed: incorrect login or password");
    }
}
