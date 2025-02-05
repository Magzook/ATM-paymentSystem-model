package org.example.customresponse.specific;

import edu.nstu.magzook.tcp.message.response.Response;

public class ResponseAuth extends Response {
    public boolean success;
    public AccountDetails accountDetails;
    public ResponseAuth(boolean success, AccountDetails accountDetails) {
        this.success = success;
        this.accountDetails = accountDetails;
    }
}
