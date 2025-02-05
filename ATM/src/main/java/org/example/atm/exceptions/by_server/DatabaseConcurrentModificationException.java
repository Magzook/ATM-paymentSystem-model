package org.example.atm.exceptions.by_server;

public class DatabaseConcurrentModificationException extends Exception {
    public DatabaseConcurrentModificationException() {
        super("Во время приготовления запроса соответствующие данные в базе данных были изменены");
    }
}
