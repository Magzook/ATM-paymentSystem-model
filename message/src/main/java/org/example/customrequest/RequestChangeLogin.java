package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestChangeLogin extends Request {
    public String login;
    public String newLogin;
    public RequestChangeLogin(String currentLogin, String newLogin) {
        this.login = currentLogin;
        this.newLogin = newLogin;
    }
}
