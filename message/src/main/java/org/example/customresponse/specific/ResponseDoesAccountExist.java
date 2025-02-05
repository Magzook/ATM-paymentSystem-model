package org.example.customresponse.specific;

import edu.nstu.magzook.tcp.message.response.Response;

public class ResponseDoesAccountExist extends Response {
    public boolean exists;
    public ResponseDoesAccountExist(boolean exists) {
        this.exists = exists;
    }
}
