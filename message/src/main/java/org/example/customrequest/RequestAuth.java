package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestAuth extends Request {
    public String login;
    public byte[] password_hash;
    public RequestAuth(String login, byte[] password_hash) {
        this.login = login;
        this.password_hash = password_hash;
    }
}
