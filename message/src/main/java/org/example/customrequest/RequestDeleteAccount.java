package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestDeleteAccount extends Request {
    public String login;
    public RequestDeleteAccount(String login) {
        this.login = login;
    }
}
