package org.example.atm.exceptions.by_server;

public class NoSuchReceiverException extends Exception {
    public NoSuchReceiverException(String receiver) {
        super("No such receiver: " + receiver);
    }
}
