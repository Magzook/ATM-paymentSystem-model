module custommessage {
    requires tcp.session;
    exports org.example.customrequest;
    exports org.example.customresponse.common;
    exports org.example.customresponse.specific;
}