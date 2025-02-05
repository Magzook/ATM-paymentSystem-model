package org.example.atm.exceptions.by_atm;

import java.net.UnknownHostException;

public class AtmBadIpException extends UnknownHostException {
    public AtmBadIpException(String serverIP) {
        super("Неизвестный IP-адрес: " + serverIP);
    }
}
