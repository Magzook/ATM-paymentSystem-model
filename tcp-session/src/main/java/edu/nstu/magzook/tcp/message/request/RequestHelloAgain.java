package edu.nstu.magzook.tcp.message.request;

public class RequestHelloAgain extends Request {
    public String sessionID;
    public RequestHelloAgain(String sessionID) {
        this.sessionID = sessionID;
    }
}
