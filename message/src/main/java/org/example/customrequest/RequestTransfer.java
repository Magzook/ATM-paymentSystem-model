package org.example.customrequest;

import edu.nstu.magzook.tcp.message.request.Request;

public class RequestTransfer extends Request {
    public String sourceLogin;
    public String destinationLogin;
    public double sum;
    public RequestTransfer(String sourceLogin, String destinationLogin, double sum) {
        this.sourceLogin = sourceLogin;
        this.destinationLogin = destinationLogin;
        this.sum = sum;
    }
}
