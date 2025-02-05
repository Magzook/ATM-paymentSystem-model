package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestDeposit extends Request {
    public String receiver;
    public double sum;
    public RequestDeposit(String receiver, double sum) {
        this.receiver = receiver;
        this.sum = sum;
    }
}
