package org.example.atm.exceptions.by_server;

public class DatabaseNotAvailableException extends Exception {
    public DatabaseNotAvailableException() {
        super("Сервер: нет соединения с базой данных");
    }
}
