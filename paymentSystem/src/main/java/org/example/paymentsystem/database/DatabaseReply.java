package org.example.paymentsystem.database;

public class DatabaseReply<T> {
    public Status status;
    public T data;
    public DatabaseReply(Status status) {
        this.status = status;
    }
    public DatabaseReply(Status status, T data) {
        this.status = status;
        this.data = data;
    }
    public enum Status {
        SUCCESS,
        FAILURE,
        NO_CONNECTION,
        CONCURRENT_MODIFICATION
    }
}
