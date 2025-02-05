package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestWithdraw extends Request {
    public String login;
    public double sum;
    public RequestWithdraw(String login, double sum) {
        this.login = login;
        this.sum = sum;
    }
}
