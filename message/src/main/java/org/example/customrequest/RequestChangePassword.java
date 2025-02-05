package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestChangePassword extends Request {
    public String login;
    public byte[] newPasswordHash;
    public RequestChangePassword(String login, byte[] passwordHash) {
        this.login = login;
        this.newPasswordHash = passwordHash;
    }
}
