package org.example.atm.exceptions.by_atm;

public class AtmBadLoginException extends Exception{
    public AtmBadLoginException(String text) {
        super(text);
    }
}
