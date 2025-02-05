package edu.nstu.magzook.tcp.message.response;

public class ResponseHello extends Response {
    public String sessionID;
    public ResponseHello(String sessionID) {
        this.sessionID = sessionID;
    }
}
