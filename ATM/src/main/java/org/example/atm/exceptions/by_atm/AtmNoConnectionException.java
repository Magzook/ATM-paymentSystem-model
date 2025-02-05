package org.example.atm.exceptions.by_atm;

public class AtmNoConnectionException extends Exception {
    public AtmNoConnectionException() {
        super("Не удалось отправить запрос: соединение с сервером прервалось");
    }
}
