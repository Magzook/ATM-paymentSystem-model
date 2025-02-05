package org.example.atm.exceptions.by_atm;

public class AtmBadPasswordException extends Exception {
    public AtmBadPasswordException(String text) {
        super(text);
    }
}
