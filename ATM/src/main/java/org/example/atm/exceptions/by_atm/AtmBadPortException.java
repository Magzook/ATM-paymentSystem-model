package org.example.atm.exceptions.by_atm;

public class AtmBadPortException extends IllegalArgumentException {
    public AtmBadPortException(int serverPort) {
        super("Порт " + serverPort + " должен быть в пределах 0..65535");
    }
}
