package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestDoesAccountExist extends Request {
    public String login;
    public RequestDoesAccountExist(String login) {
        this.login = login;
    }
}
